package utils;

public class DiceRoller {
    /**
    * <p>Example inputs:
    * - 1d20
    * - 1d20 +(or)- 5d10
    * - 1d20 *(or)/ 5d10 + 10
    * - 2 / (1d20 + 5d10) * 10 
    *<p>
    * The output should be the result of the dice roll calculation based on the input expression.
    * Exemple: 2d20 would mean rolling two 20-sided dice and summing the results.
    * The function should correctly handle the order of operations and parentheses in the expression.
    * The result must be an integer representing the final calculated value of the dice roll expression.
    */
    public int diceParser(String textWithDice) {
        return new DiceExpressionParser(textWithDice).parse();
    }

    public static int rollDice(int numberOfDice, int sides) {
        int total = 0;
        for (int i = 0; i < numberOfDice; i++) {
            total += (int)(Math.random() * sides) + 1;
        }
        return total;
    }

    // Reads and parses a dice expression, supporting numbers, dice rolls, and arithmetic operations.
    // "pos" is the current reading position within the "input" text.
    private static class DiceExpressionParser {
        private final String input;
        private int pos;

        DiceExpressionParser(String input) {
            this.input = input;
            this.pos = 0;
        }

        // Entry point: calculates the result and ensures there is no leftover garbage at the end of the string.
        int parse() {
            int result = parseExpression();
            skipWhitespace();
            if (pos < input.length()) {
                throw new IllegalArgumentException("Unexpected character at position " + pos + " in expression: " + input);
            }
            return result;
        }

        // Resolves addition and subtraction (lower priority). Ex: "A + B - C".
        private int parseExpression() {
            int value = parseTerm();
            while (true) {
                skipWhitespace();
                if (peek('+')) {
                    pos++;
                    value += parseTerm();
                } else if (peek('-')) {
                    pos++;
                    value -= parseTerm();
                } else {
                    break;
                }
            }
            return value;
        }

        // Resolves multiplication and division (higher priority than + and -). Ex: "A * B / C".
        private int parseTerm() {
            int value = parseFactor();
            while (true) {
                skipWhitespace();
                if (peek('*')) {
                    pos++;
                    value *= parseFactor();
                } else if (peek('/')) {
                    pos++;
                    value /= parseFactor();
                } else {
                    break;
                }
            }
            return value;
        }

        // Resolves a single "piece" of the expression: sign (-A), parentheses (A), or number/dice.
        private int parseFactor() {
            skipWhitespace();
            if (peek('-')) {
                pos++;
                return -parseFactor();
            }
            if (peek('+')) {
                pos++;
                return parseFactor();
            }
            if (peek('(')) {
                pos++;
                int value = parseExpression();
                skipWhitespace();
                expect(')');
                return value;
            }
            return parseNumberOrDice();
        }

        // Reads a simple number (ex: "10") or a dice roll (ex: "2d20"),
        // actually rolling the dice when it encounters the "d".
        private int parseNumberOrDice() {
            skipWhitespace();
            int start = pos;
            while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
                pos++;
            }
            if (pos == start) {
                throw new IllegalArgumentException("Expected number at position " + pos + " in expression: " + input);
            }
            int firstNumber = Integer.parseInt(input.substring(start, pos));

            if (pos < input.length() && (input.charAt(pos) == 'd' || input.charAt(pos) == 'D')) {
                pos++;
                int sidesStart = pos;
                while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
                    pos++;
                }
                if (pos == sidesStart) {
                    throw new IllegalArgumentException("Expected number of sides after 'd' in expression: " + input);
                }
                int sides = Integer.parseInt(input.substring(sidesStart, pos));
                return rollDice(firstNumber, sides);
            }
            return firstNumber;
        }

        // Advances "pos" while there are spaces, so they can be ignored at any point during reading.
        private void skipWhitespace() {
            while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) {
                pos++;
            }
        }

        // Looks at the current character without advancing the position, just to check if it is the expected one.
        private boolean peek(char c) {
            return pos < input.length() && input.charAt(pos) == c;
        }

        // Requires that the current character be "c" (ex: closing parentheses); if not, it is a syntax error.
        private void expect(char c) {
            if (!peek(c)) {
                throw new IllegalArgumentException("Expected '" + c + "' at position " + pos + " in expression: " + input);
            }
            pos++;
        }
    }
}
