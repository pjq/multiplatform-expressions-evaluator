import kotlin.math.exp

class Converter {

    private val operators = ArrayDeque<Token>()

    fun convert(expression: List<Token>): List<Token> {
        val output = mutableListOf<Token>()

        checkSyntax(expression)

        for (token in expression) {
            when (token) {
                is Token.Operand -> output.add(token)
                is Token.Bracket.Left -> operators.add(token)
                is Token.Bracket.Right -> {
                    while (operators.lastOrNull() != Token.Bracket.Left) {
                        require(operators.isNotEmpty()) { "mismatched parenthesis" }
                        output.add(operators.removeLast())
                    }
                    require(operators.lastOrNull() == Token.Bracket.Left) { "mismatched parenthesis" }
                    operators.removeLast()
                    if (operators.isNotEmpty() && operators.last() is Token.Function) {
                        output.add(operators.removeLast())
                    }
                }
                is Token.Operator -> {
                    while (
                        operators.isNotEmpty() && operators.last() != Token.Bracket.Left &&
                        (token.priority < operators.lastOperator.priority ||
                                token.priority == operators.lastOperator.priority && token.isLeftAssociative)
                    ) output.add(operators.removeLast())
                    operators.addLast(token)
                }
                is Token.Function -> operators.add(token)
                is Token.Function.Delimeter -> { /* just ignore it */ }
            }
        }

        while (operators.isNotEmpty()) {
            output.add(operators.removeLast())
        }

        return output
    }

    private fun checkSyntax(expression: List<Token>) {
        var bracketsCounter = 0
        for (token in expression) {
            when (token) {
                is Token.Bracket.Left -> bracketsCounter++
                is Token.Bracket.Right -> bracketsCounter--
                else -> { /*do nothing*/ }
            }
            require(bracketsCounter >= 0) { "mismatched parenthesis" }
        }
        require(bracketsCounter == 0) { "mismatched parenthesis" }
    }

    private val Token.Operator.isLeftAssociative: Boolean
        get() = this.associativity == Token.Associativity.LEFT


    private val ArrayDeque<Token>.lastOperator: Token.Operator
        get() {
            val lastOp = last()
            require(lastOp is Token.Operator) { "last operator is not supported" }
            return lastOp
        }
}