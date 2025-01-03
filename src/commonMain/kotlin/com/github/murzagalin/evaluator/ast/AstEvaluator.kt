/**
evaluator.evaluateString("length('hello')") // Returns 5.0
evaluator.evaluateString("'hello' + ' world'") // Returns "hello world"
evaluator.evaluateString("contains('hello world', 'world')") // Returns true
evaluator.evaluateString("substring('hello', 1, 4)") // Returns "ell"
*/
package com.github.murzagalin.evaluator.ast

import com.github.murzagalin.evaluator.Token
import kotlin.math.pow

internal class AstEvaluator(private val values: Map<String, Any> = emptyMap()): AstVisitor {

    fun evaluate(expression: Expression) = expression.visit(this)

    override fun visitTerminal(terminal: Expression.Terminal) = when(val operand = terminal.token) {
        is Token.Operand.Number -> operand.value
        is Token.Operand.Boolean -> operand.value
        is Token.Operand.String -> operand.value
        is Token.Operand.Variable -> requireNotNull(values[operand.value]) {
            "Could not resolve variable '${operand.value}'"
        }
    }

    override fun visitUnary(unary: Expression.Unary): Any {
        val literal = evaluate(unary.expression)

        return when (unary.token) {
            Token.Operator.UnaryPlus -> {
                require(literal is Number) { "A Number is expected after a unary plus" }
                literal
            }
            Token.Operator.UnaryMinus -> {
                require(literal is Number) { "A Number is expected after a unary plus" }
                -literal.toDouble()
            }
            Token.Operator.Not -> {
                require(literal is Boolean) { "A Number is expected after a unary plus" }
                !literal
            }
            else -> {
                error("${unary.token} was incorrectly parsed as a unary operator")
            }
        }
    }

    override fun visitBinary(binary: Expression.Binary) = when (binary.token) {
        Token.Operator.LessThan -> binaryOnComparable(binary,"<") { left, right -> left < right }
        Token.Operator.LessEqualThan -> binaryOnComparable(binary,"<=") { left, right -> left <= right }
        Token.Operator.GreaterThan -> binaryOnComparable(binary,">") { left, right -> left > right }
        Token.Operator.GreaterEqualThan -> binaryOnComparable(binary,">=") { left, right -> left >= right }
        Token.Operator.Equal -> {
            val left = evaluate(binary.leftExpression)
            val right = evaluate(binary.rightExpression)
            left == right
        }
        Token.Operator.NotEqual -> {
            val left = evaluate(binary.leftExpression)
            val right = evaluate(binary.rightExpression)
            left != right
        }
        Token.Operator.Plus -> {
            val left = evaluate(binary.leftExpression)
            val right = evaluate(binary.rightExpression)
            
            when {
                left is Number && right is Number -> left.toDouble() + right.toDouble()
                left is String || right is String -> left.toString() + right.toString()
                else -> throw IllegalArgumentException("Cannot add ${left::class.simpleName} and ${right::class.simpleName}")
            }
        }
        Token.Operator.And -> binaryOnBooleans(binary, "&&") { left, right -> left && right}
        Token.Operator.Or -> binaryOnBooleans(binary, "||") { left, right -> left || right}
        Token.Operator.Minus -> binaryOnNumbers(binary,"-") { left, right -> left - right }
        Token.Operator.Modulo -> binaryOnNumbers(binary,"%") { left, right -> left % right }
        Token.Operator.Multiplication -> binaryOnNumbers(binary,"*") { left, right -> left * right }
        Token.Operator.Division -> binaryOnNumbers(binary,"/") { left, right -> left / right }
        Token.Operator.Power -> binaryOnNumbers(binary,"^") { left, right -> left.pow(right) }
        else -> {
            error("${binary.token} was incorrectly parsed as a binary operator")
        }
    }

        private fun binaryOnComparable(binary: Expression.Binary, operator: String, operation: (Comparable<Any>, Comparable<Any>) -> Boolean): Boolean {
        val left = evaluate(binary.leftExpression)
        val right = evaluate(binary.rightExpression)

        require(left is Comparable<*> && right is Comparable<*>) {
            "$operator operator requires comparable operands, but got ${left::class.simpleName} and ${right::class.simpleName}"
        }

        @Suppress("UNCHECKED_CAST")
        return operation(left as Comparable<Any>, right as Comparable<Any>)
    }

//    override fun visitBinary(binary: Expression.Binary) = when (binary.token) {
//        Token.Operator.LessThan -> binaryOnComparable(binary, "<") { left, right -> left < right }
//        Token.Operator.LessEqualThan -> binaryOnComparable(binary, "<=") { left, right -> left <= right }
//        Token.Operator.GreaterThan -> binaryOnComparable(binary, ">") { left, right -> left > right }
//        Token.Operator.GreaterEqualThan -> binaryOnComparable(binary, ">=") { left, right -> left >= right }
//        // ... rest of the existing implementation remains the same
//        else -> super.visitBinary(binary)
//    }

    private fun binaryOnBooleans(binary: Expression.Binary, operator: String, operation: (Boolean, Boolean) -> Boolean): Boolean {
        val left = evaluate(binary.leftExpression)
        val right = evaluate(binary.rightExpression)

        require(left is Boolean && right is Boolean) {
            "$operator operator requires boolean operands, but got ${left::class.simpleName} and ${right::class.simpleName}"
        }

        return operation(left, right)
    }

    private fun binaryOnNumbers(binary: Expression.Binary, operator: String, operation: (Double, Double) -> Any): Any {
        val left = evaluate(binary.leftExpression)
        val right = evaluate(binary.rightExpression)

        require(left is Number && right is Number) {
            "$operator operator requires number operands, but got ${left::class.simpleName} and ${right::class.simpleName}"
        }

        return operation(left.toDouble(), right.toDouble())
    }

    override fun visitTernary(ternary: Expression.Ternary): Any {
        if (ternary.token is Token.Operator.TernaryIfElse) {
            val left = evaluate(ternary.firstExpression)
            require(left is Boolean) {
                "Ternary <condition> ? <expression1> : <expression2> must be called with a boolean value as a condition"
            }
            return if (left) {
                evaluate(ternary.secondExpression)
            } else {
                evaluate(ternary.thirdExpression)
            }
        } else {
            error("${ternary.token} was incorrectly parsed as a ternary operator")
        }
    }

    override fun visitFunctionCall(functionCall: Expression.FunctionCall): Any {
        val arguments = mutableListOf<Any>()

        for (arg in functionCall.arguments) {
            arguments.add(evaluate(arg))
        }

        return functionCall.token(arguments)
    }

}