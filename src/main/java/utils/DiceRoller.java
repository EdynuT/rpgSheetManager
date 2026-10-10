package utils;

import com.bernardomg.tabletop.dice.history.RollHistory;
import com.bernardomg.tabletop.dice.parser.DefaultDiceParser;
import com.bernardomg.tabletop.dice.parser.DiceParser;

public class DiceRoller {
    private static final DiceParser parser = new DefaultDiceParser();

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
        return roll(textWithDice).getTotalRoll();
    }

    /**
     * Same as {@link #diceParser(String)}, but also keeps every individual die rolled
     * ({@code getRollResults()}), so the interface can show what was rolled.
     */
    public RollHistory roll(String textWithDice) {
        if (textWithDice == null || textWithDice.trim().isEmpty()) {
            throw new IllegalArgumentException("Dice expression must not be empty!");
        }
        // The library grammar does not accept whitespace, so it is stripped first.
        // Division is integer division: 2 / 28 = 0.
        // Invalid expressions throw IllegalStateException.
        return parser.parse(textWithDice.replaceAll("\\s+", ""),
                new com.bernardomg.tabletop.dice.interpreter.DiceRoller());
    }

    public static int rollDice(int numberOfDice, int sides) {
        return new DiceRoller().diceParser(numberOfDice + "d" + sides);
    }
}
