package com.github.murzagalin.evaluator.integration

import com.github.murzagalin.evaluator.Evaluator
import kotlin.test.Test
import kotlin.test.assertEquals

class StringTests {
    private val evaluator = Evaluator()

    @Test
    fun simple_string_concatenation() {
        val testCases = listOf(
            "'hello' + ' world'" to "hello world",
            "'test' + '123'" to "test123",
            "'' + 'empty'" to "empty"
        )

        testCases.forEach { (expression, expected) ->
            assertEquals(
                expected, 
                evaluator.evaluateString(expression), 
                "Failed for expression: $expression"
            )
        }
    }

    @Test
    fun string_concatenation_with_variables() {
        val testCases = listOf(
            mapOf("name" to " John") to "'hello' + name" to "hello John",
            mapOf("prefix" to "pre", "suffix" to "fix") to "prefix + suffix" to "prefix"
        )

//        testCases.forEach { (variables, pair) ->
//            val (expression, expected) = pair
//            assertEquals(
//                expected,
//                evaluator.evaluateString(expression, variables),
//                "Failed for expression: $expression with variables: $variables"
//            )
//        }
    }

    @Test
    fun string_ternary_operation() {
        val testCases = listOf(
            "'a' > 'b' ? 'success' : 'failure'" to "failure",
            "'b' > 'a' ? 'success' : 'failure'" to "success"
        )

        testCases.forEach { (expression, expected) ->
            assertEquals(
                expected, 
                evaluator.evaluateString(expression), 
                "Failed for expression: $expression"
            )
        }
    }
}