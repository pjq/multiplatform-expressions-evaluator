package com.github.murzagalin.evaluator

import com.github.murzagalin.evaluator.ast.BooleanAstEvaluator
import com.github.murzagalin.evaluator.ast.DoubleAstEvaluator
import com.github.murzagalin.evaluator.ast.StringAstEvaluator
import com.github.murzagalin.evaluator.ast.Expression
import com.github.murzagalin.evaluator.ast.Parser

class Evaluator(
    functions: List<Function> = DefaultFunctions.ALL,
    constants: List<Constant> = DefaultConstants.ALL,
    doubleDelimiter: Char = '.',
    argumentsDelimiter: Char = ','
) {

    private val booleanEvaluator = BooleanAstEvaluator()
    private val doubleEvaluator = DoubleAstEvaluator()
    private val stringAstEvaluator = StringAstEvaluator()

    private val parser = Parser()
    private val tokenizer = Tokenizer(
        functions = functions,
        constants = constants,
        doubleDelimiter = doubleDelimiter,
        argumentsDelimiter = argumentsDelimiter
    )

    fun evaluateDouble(
        expression: String,
        values: Map<String, Any> = emptyMap()
    ): Double {
        val tokenized = tokenizer.tokenize(expression)
        val parsed = parser.parse(tokenized)

        return doubleEvaluator.evaluate(parsed, values)
    }

    fun evaluateBoolean(
        expression: String,
        values: Map<String, Any> = emptyMap()
    ): Boolean {
        val tokenized = tokenizer.tokenize(expression)
        val parsed = parser.parse(tokenized)

        return booleanEvaluator.evaluate(parsed, values)
    }

    fun evaluateDouble(
        expression: Expression,
        values: Map<String, Any> = emptyMap()
    ) = doubleEvaluator.evaluate(expression, values)

    fun evaluateBoolean(
        expression: Expression,
        values: Map<String, Any> = emptyMap()
    ) = booleanEvaluator.evaluate(expression, values)

     // Add evaluateString method
    fun evaluateString(
        expression: String,
        values: Map<String, Any> = emptyMap()
    ): String {
        val tokenized = tokenizer.tokenize(expression)
        val parsed = parser.parse(tokenized)

        return stringAstEvaluator.evaluate(parsed, values)
    }

    // Optional: Add overload for Expression
    fun evaluateString(
        expression: Expression,
        values: Map<String, Any> = emptyMap()
    ) = stringAstEvaluator.evaluate(expression, values)

    fun preprocessExpression(expression: String): Expression {
        val tokenized = tokenizer.tokenize(expression)

        return parser.parse(tokenized)
    }
}
