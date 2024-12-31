package com.github.murzagalin.evaluator.ast

internal class StringAstEvaluator {
    fun evaluate(expression: Expression, values: Map<String, Any> = emptyMap()): String {
        val baseEvaluator = AstEvaluator(values)
        val evaluated = baseEvaluator.evaluate(expression)

        require(evaluated is String) { "Expression must evaluate to a String, but got ${evaluated::class.simpleName}" }

        return evaluated
    }
}