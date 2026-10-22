package com.edynu.rpgSheetManager.utils;

import com.bernardomg.tabletop.dice.history.RollHistory;
import com.bernardomg.tabletop.dice.parser.DefaultDiceParser;
import com.bernardomg.tabletop.dice.parser.DiceParser;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    // ---------------------------------------------------------------- Damage inside free text

    /**
     * Finds a dice expression written in the middle of a text.
     * <p>
     * How to read it, piece by piece:
     * <ul>
     * <li>{@code (?<!\w)} - nothing glued before it, so "abc2d6" is not taken;</li>
     * <li>{@code \d*d\d+(?!\w)} - one die term: "2d6", "1d20" or just "d8" (amount is optional);</li>
     * <li>{@code (?:\s*[+-]\s*(?:\d*d\d+|\d+)(?!\w))*} - zero or more "+ something" or "- something",
     * where something is another die term or a plain number. Spaces around the sign are allowed.</li>
     * </ul>
     * Examples found: "2d6", "1d8 + 2", "2d6+1d4-1", "d10".
     * Multiplication, division and parentheses are left out on purpose: in a description they
     * almost never mean damage and would only make the search grab wrong pieces of the text.
     */
    private static final Pattern DICE_IN_TEXT = Pattern.compile(
            "(?<!\\w)\\d*d\\d+(?!\\w)(?:\\s*[+-]\\s*(?:\\d*d\\d+|\\d+)(?!\\w))*",
            Pattern.CASE_INSENSITIVE);

    /** Words that mark an expression as damage. "dano" also covers "danos". */
    private static final String[] DAMAGE_WORDS = {"dano", "damage", "dmg"};

    /** How many characters before and after an expression are checked for a damage word. */
    private static final int DAMAGE_WORD_DISTANCE = 20;

    /**
     * Finds the damage dice of an ability in its description.
     * <p>
     * A description can have more than one roll, for example
     * "Attack with 1d20 + 5, deals 2d6 fire damage". To roll only the damage:
     * <ol>
     * <li>the first expression that has a damage word close to it (before or after) wins,
     * here "2d6", because "damage" is right after it;</li>
     * <li>when no expression is close to a damage word, the first expression of the
     * text is used, so descriptions like "Fireball: 8d6, fire" still work;</li>
     * <li>when the text has no dice at all, {@code null} is returned.</li>
     * </ol>
     *
     * @param description the free text of the ability
     * @return the damage expression, ready for {@link #diceParser(String)}, or {@code null}
     */
    public static String findDamageExpression(String description) {
        if (description == null) {
            return null;
        }
        String firstFound = null;
        Matcher matcher = DICE_IN_TEXT.matcher(description);
        while (matcher.find()) {
            String expression = withAmount(matcher.group());
            if (firstFound == null) {
                firstFound = expression;
            }
            if (hasDamageWordNear(description, matcher.start(), matcher.end())) {
                return expression;
            }
        }
        return firstFound;
    }

    // Looks for a damage word in the few characters around the expression (start..end).
    private static boolean hasDamageWordNear(String text, int start, int end) {
        int from = Math.max(0, start - DAMAGE_WORD_DISTANCE);
        int to = Math.min(text.length(), end + DAMAGE_WORD_DISTANCE);
        String around = text.substring(from, to).toLowerCase();
        for (String word : DAMAGE_WORDS) {
            if (around.contains(word)) {
                return true;
            }
        }
        return false;
    }

    // People write "d8" meaning "1d8", but the library needs the amount, so the 1 is added:
    // "d8 + d4" becomes "1d8 + 1d4".
    private static String withAmount(String expression) {
        return expression.replaceAll("(?i)(?<!\\d)d(\\d+)", "1d$1");
    }
}
