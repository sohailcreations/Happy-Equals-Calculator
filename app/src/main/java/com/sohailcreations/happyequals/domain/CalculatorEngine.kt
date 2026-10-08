
package com.sohailcreations.happyequals.domain

import kotlin.math.*

// ==========================================
// HAPPY EQUALS - SCIENTIFIC CALCULATOR ENGINE
// ==========================================

object CalculatorEngine {

    fun evaluate(
        expression: String,
        isDegrees: Boolean = true
    ): Double {

        val normalizedExpression = expression
            .filterNot { it.isWhitespace() }
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")

        if (normalizedExpression.isEmpty()) {
            throw IllegalArgumentException(
                "Enter a calculation"
            )
        }

        val parser = ExpressionParser(
            input = normalizedExpression,
            isDegrees = isDegrees
        )

        val result = parser.parse()

        if (!result.isFinite()) {
            throw IllegalArgumentException(
                "Result out of range"
            )
        }

        return result
    }

    // ======================================
    // EXPRESSION PARSER
    // ======================================

    private class ExpressionParser(
        private val input: String,
        private val isDegrees: Boolean
    ) {

        private var position = 0

        // ==================================
        // PARSE COMPLETE EXPRESSION
        // ==================================

        fun parse(): Double {

            val result = parseExpression()

            if (position != input.length) {

                if (input[position] == ')') {
                    throw IllegalArgumentException(
                        "Check your brackets"
                    )
                }

                throw IllegalArgumentException(
                    "Invalid expression"
                )
            }

            if (!result.isFinite()) {
                throw IllegalArgumentException(
                    "Result out of range"
                )
            }

            return result
        }

        // ==================================
        // ADDITION AND SUBTRACTION
        // ==================================

        private fun parseExpression(): Double {

            var value = parseTerm()

            while (true) {

                value = when {

                    match('+') -> {
                        value + parseTerm()
                    }

                    match('-') -> {
                        value - parseTerm()
                    }

                    else -> {
                        return value
                    }
                }

                checkFinite(value)
            }
        }

        // ==================================
        // MULTIPLICATION AND DIVISION
        // ==================================

        private fun parseTerm(): Double {

            var value = parseUnary()

            while (true) {

                value = when {

                    match('*') -> {
                        value * parseUnary()
                    }

                    match('/') -> {

                        val divisor = parseUnary()

                        if (divisor == 0.0) {
                            throw IllegalArgumentException(
                                "Cannot divide by zero"
                            )
                        }

                        value / divisor
                    }

                    else -> {
                        return value
                    }
                }

                checkFinite(value)
            }
        }

        // ==================================
        // POSITIVE AND NEGATIVE SIGNS
        // ==================================

        private fun parseUnary(): Double {

            return when {

                match('+') -> {
                    parseUnary()
                }

                match('-') -> {
                    -parseUnary()
                }

                else -> {
                    parsePower()
                }
            }
        }

        // ==================================
        // POWERS
        // Right-associative: 2^3^2 = 512
        // ==================================

        private fun parsePower(): Double {

            val base = parsePostfix()

            if (match('^')) {

                val exponent = parseUnary()

                val result = base.pow(exponent)

                checkFinite(result)

                return result
            }

            return base
        }

        // ==================================
        // FACTORIAL AND PERCENTAGE
        // ==================================

        private fun parsePostfix(): Double {

            var value = parsePrimary()

            while (true) {

                value = when {

                    match('!') -> {
                        calculateFactorial(value)
                    }

                    match('%') -> {
                        value / 100.0
                    }

                    else -> {
                        return value
                    }
                }

                checkFinite(value)
            }
        }

        // ==================================
        // NUMBERS, BRACKETS AND CONSTANTS
        // ==================================

        private fun parsePrimary(): Double {

            // Parentheses
            if (match('(')) {

                val value = parseExpression()

                expect(')')

                return value
            }

            // Pi
            if (match('π')) {
                return Math.PI
            }

            // Euler's number
            if (match('e')) {
                return Math.E
            }

            // Square root
            if (match('√')) {

                expect('(')

                val value = parseExpression()

                expect(')')

                if (value < 0.0) {

                    throw IllegalArgumentException(
                        "Cannot calculate square root of a negative number"
                    )
                }

                return sqrt(value)
            }

            // Scientific functions
            if (
                position < input.length &&
                input[position].isLetter()
            ) {

                return parseFunction()
            }

            // Regular and decimal numbers
            return parseNumber()
        }

        // ==================================
        // SCIENTIFIC FUNCTIONS
        // ==================================

        private fun parseFunction(): Double {

            val start = position

            while (
                position < input.length &&
                input[position].isLetter()
            ) {
                position++
            }

            val functionName = input.substring(
                start,
                position
            ).lowercase()

            expect('(')

            val argument = parseExpression()

            expect(')')

            return evaluateFunction(
                functionName,
                argument
            )
        }

        private fun evaluateFunction(
            functionName: String,
            argument: Double
        ): Double {

            val angle = if (isDegrees) {

                Math.toRadians(argument)

            } else {

                argument
            }

            val result = when (functionName) {

                "sin" -> {
                    sin(angle)
                }

                "cos" -> {
                    cos(angle)
                }

                "tan" -> {

                    if (abs(cos(angle)) < 1e-12) {

                        throw IllegalArgumentException(
                            "Tangent is undefined at this angle"
                        )
                    }

                    tan(angle)
                }

                "log" -> {

                    if (argument <= 0.0) {

                        throw IllegalArgumentException(
                            "Logarithm requires a positive number"
                        )
                    }

                    log10(argument)
                }

                "ln" -> {

                    if (argument <= 0.0) {

                        throw IllegalArgumentException(
                            "Natural logarithm requires a positive number"
                        )
                    }

                    ln(argument)
                }

                else -> {

                    throw IllegalArgumentException(
                        "Unknown scientific function"
                    )
                }
            }

            checkFinite(result)

            return result
        }

        // ==================================
        // NUMBER PARSER
        // Supports decimals and scientific
        // notation such as 1.25E-9
        // ==================================

        private fun parseNumber(): Double {

            val start = position

            var hasDigit = false

            while (
                position < input.length &&
                input[position].isDigit()
            ) {

                position++
                hasDigit = true
            }

            if (match('.')) {

                while (
                    position < input.length &&
                    input[position].isDigit()
                ) {

                    position++
                    hasDigit = true
                }
            }

            if (!hasDigit) {

                throw IllegalArgumentException(
                    "Invalid expression"
                )
            }

            // Scientific notation
            if (
                position < input.length &&
                input[position] == 'E'
            ) {

                position++

                if (
                    position < input.length &&
                    (
                            input[position] == '+' ||
                                    input[position] == '-'
                            )
                ) {
                    position++
                }

                val exponentStart = position

                while (
                    position < input.length &&
                    input[position].isDigit()
                ) {
                    position++
                }

                if (exponentStart == position) {

                    throw IllegalArgumentException(
                        "Invalid exponent"
                    )
                }
            }

            val numberText = input.substring(
                start,
                position
            )

            val value = numberText.toDoubleOrNull()
                ?: throw IllegalArgumentException(
                    "Invalid number"
                )

            checkFinite(value)

            return value
        }

        // ==================================
        // FACTORIAL
        // ==================================

        private fun calculateFactorial(
            number: Double
        ): Double {

            if (!number.isFinite()) {

                throw IllegalArgumentException(
                    "Invalid factorial"
                )
            }

            if (
                number < 0.0 ||
                number != floor(number)
            ) {

                throw IllegalArgumentException(
                    "Factorial requires a non-negative whole number"
                )
            }

            // Double precision factorial limit
            if (number > 170.0) {

                throw IllegalArgumentException(
                    "Factorial is too large"
                )
            }

            val integer = number.toInt()

            if (integer == 0 || integer == 1) {
                return 1.0
            }

            var result = 1.0

            for (i in 2..integer) {
                result *= i
            }

            return result
        }

        // ==================================
        // PARSER HELPERS
        // ==================================

        private fun match(
            character: Char
        ): Boolean {

            if (
                position < input.length &&
                input[position] == character
            ) {

                position++

                return true
            }

            return false
        }

        private fun expect(
            character: Char
        ) {

            if (!match(character)) {

                throw IllegalArgumentException(
                    "Check your brackets"
                )
            }
        }

        private fun checkFinite(
            value: Double
        ) {

            if (!value.isFinite()) {

                throw IllegalArgumentException(
                    "Result out of range"
                )
            }
        }
    }
}