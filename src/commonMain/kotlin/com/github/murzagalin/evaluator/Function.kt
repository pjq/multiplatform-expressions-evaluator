package com.github.murzagalin.evaluator

import kotlin.math.*

abstract class Function(val name: String, val argsCount: IntRange) {

    constructor(name: String, argsCount: Int): this(name, argsCount..argsCount)

    constructor(name: String, minArgsCount: Int, maxArgsCount: Int): this(name, minArgsCount..maxArgsCount)

    abstract operator fun invoke(vararg args: Any): Any
}

private fun <T> Array<T>.getAsNumber(index: Int, lazyMessage: () -> Any): Number {
    val e = get(index)
    require(e is Number, lazyMessage)

    return e
}

private fun <T> Array<T>.getAsDouble(index: Int, lazyMessage: () -> Any): Double {
    val e = get(index)
    require(e is Number, lazyMessage)

    return e.toDouble()
}

private fun <T> Array<T>.getAsBoolean(index: Int, lazyMessage: () -> Any): Boolean {
    val e = get(index)
    require(e is Boolean, lazyMessage)

    return e
}

abstract class OneNumberArgumentFunction(name: String, argsCount: IntRange) : Function(name, argsCount) {

    constructor(name: String, argsCount: Int): this(name, argsCount..argsCount)

    constructor(name: String, minArgsCount: Int, maxArgsCount: Int): this(name, minArgsCount..maxArgsCount)

    override fun invoke(vararg args: Any): Any {
        require(args.size == 1) { "$name function requires 1 argument" }
        val operand = args.getAsNumber(0) {
            "$name is called with argument type ${Number::class.simpleName}, but supports only numbers"
        }

        return invokeInternal(operand)
    }

    abstract fun invokeInternal(arg: Number): Double
}

object DefaultFunctions {

    val ABS = object: OneNumberArgumentFunction("abs", 1) {
        override fun invokeInternal(arg: Number) = abs(arg.toDouble())
    }

    val ACOS = object: OneNumberArgumentFunction("acos", 1) {
        override fun invokeInternal(arg: Number) = acos(arg.toDouble())
    }

    val ASIN = object: OneNumberArgumentFunction("asin", 1) {
        override fun invokeInternal(arg: Number) = asin(arg.toDouble())
    }

    val ATAN = object: OneNumberArgumentFunction("atan", 1) {
        override fun invokeInternal(arg: Number) = atan(arg.toDouble())
    }

    val COS = object: OneNumberArgumentFunction("cos", 1) {
        override fun invokeInternal(arg: Number) = cos(arg.toDouble())
    }

    val COSH = object: OneNumberArgumentFunction("cosh", 1) {
        override fun invokeInternal(arg: Number) = cosh(arg.toDouble())
    }

    val SINH = object: OneNumberArgumentFunction("sinh", 1) {
        override fun invokeInternal(arg: Number) = sinh(arg.toDouble())
    }

    val SIN = object: OneNumberArgumentFunction("sin", 1) {
        override fun invokeInternal(arg: Number) = sin(arg.toDouble())
    }

    val TAN = object: OneNumberArgumentFunction("tan", 1) {
        override fun invokeInternal(arg: Number) = tan(arg.toDouble())
    }

    val TANH = object: OneNumberArgumentFunction("tanh", 1) {
        override fun invokeInternal(arg: Number) = tanh(arg.toDouble())
    }

    val CEIL = object: OneNumberArgumentFunction("ceil", 1) {
        override fun invokeInternal(arg: Number) = ceil(arg.toDouble())
    }

    val FLOOR = object: OneNumberArgumentFunction("floor", 1) {
        override fun invokeInternal(arg: Number) = floor(arg.toDouble())
    }

    val ROUND = object: OneNumberArgumentFunction("round", 1) {
        override fun invokeInternal(arg: Number) = round(arg.toDouble())
    }

    val LN = object: OneNumberArgumentFunction("ln", 1) {
        override fun invokeInternal(arg: Number) = ln(arg.toDouble())
    }

    val LOG = object: Function("log", 2) {
        override fun invoke(vararg args: Any): Any {
            val operand = args.getAsNumber(0) { "$name argument must be a number" }
            val base = args.getAsNumber(1) { "$name base must be a number" }

            return log(operand.toDouble(), base.toDouble())
        }
    }

    val MIN = object: Function("min", 2..Int.MAX_VALUE) {
        override fun invoke(vararg args: Any): Any {
            require(args.size > 1) { "$name should be called with at least 2 arguments" }
            require(args.all { it is Number }) { "$name function requires all arguments to be numbers" }

            return args.minByOrNull { (it as Number).toDouble() }!!
        }
    }

    val AVG = object: Function("avg", 2..Int.MAX_VALUE) {
        override fun invoke(vararg args: Any): Any {
            require(args.size > 1) { "$name should be called with at least 2 arguments" }
            require(args.all { it is Number }) { "$name function requires all arguments to be numbers" }

            return args.map { (it as Number).toDouble() }.average()
        }
    }

    val SUM = object: Function("sum", 2..Int.MAX_VALUE) {
        override fun invoke(vararg args: Any): Any {
            require(args.size > 1) { "$name should be called with at least 2 arguments" }
            require(args.all { it is Number }) { "$name function requires all arguments to be numbers" }

            return args.sumOf { (it as Number).toDouble() }
        }
    }

    val MAX = object: Function("max", 2..Int.MAX_VALUE) {
        override fun invoke(vararg args: Any): Any {
            require(args.size > 1) { "$name should be called with at least 2 arguments" }
            require(args.all { it is Number }) { "$name function requires all arguments to be numbers" }

            return args.maxByOrNull { (it as Number).toDouble() }!!
        }
    }
    
    val FormatCurrency = object: Function("format.currency", 3) {
        override fun invoke(vararg args: Any): Any {
            require(args.size == 3) { "$name should be called with 3 arguments" }
            val amount = args.getAsNumber(0) { "$name amount must be a number" }
            val currency = args[1] as? kotlin.String ?: throw IllegalArgumentException("$name currency must be a string")
            val options = args[2] as? Map<kotlin.String, Any> ?: throw IllegalArgumentException("$name options must be a map")

            // Implement currency formatting logic here
            // For simplicity, let's just return a formatted string
            return "$currency ${amount.toDouble()}"
        }
    }

    val STRING_LENGTH = object : Function("length", 1) {
        override fun invoke(vararg args: Any): Any {
            require(args.size == 1) { "$name function requires 1 argument" }
            val str = args[0] as? kotlin.String ?: throw IllegalArgumentException("$name function requires a string argument")
            return str.length.toDouble()
        }
    }

    val STRING_CONCAT = object : Function("concat", 2..Int.MAX_VALUE) {
        override fun invoke(vararg args: Any): Any {
            require(args.isNotEmpty()) { "$name function requires at least one argument" }
            return args.joinToString("") { it.toString() }
        }
    }

    val STRING_CONTAINS = object : Function("contains", 2) {
        override fun invoke(vararg args: Any): Any {
            require(args.size == 2) { "$name function requires 2 arguments" }
            val str = args[0] as? kotlin.String ?: throw IllegalArgumentException("$name first argument must be a string")
            val substring = args[1] as? kotlin.String ?: throw IllegalArgumentException("$name second argument must be a string")
            return str.contains(substring)
        }
    }

    val STRING_SUBSTRING = object : Function("substring", 2..3) {
        override fun invoke(vararg args: Any): Any {
            require(args.size in 2..3) { "$name function requires 2 or 3 arguments" }
            val str = args[0] as? kotlin.String ?: throw IllegalArgumentException("$name first argument must be a string")
            val start = (args[1] as? Number)?.toInt() ?: throw IllegalArgumentException("$name second argument must be a number")
            
            return if (args.size == 3) {
                val end = (args[2] as? Number)?.toInt() ?: throw IllegalArgumentException("$name third argument must be a number")
                str.substring(start, end)
            } else {
                str.substring(start)
            }
        }
    }

    val ALL = listOf(ABS, ACOS, ASIN, ATAN, COS, COSH, SINH, SIN, TAN, TANH, CEIL, FLOOR, ROUND, LN, LOG, MIN, MAX, AVG, SUM, FormatCurrency, STRING_LENGTH, STRING_CONCAT, STRING_CONTAINS, STRING_SUBSTRING)
}