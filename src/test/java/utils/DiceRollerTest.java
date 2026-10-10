package utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DiceRollerTest {
    private final DiceRoller roller = new DiceRoller();

    private void assertBetween(String expr, int min, int max) {
        // Rolls are random, so repeat a few times and check the bounds.
        for (int i = 0; i < 200; i++) {
            int total = roller.diceParser(expr);
            assertTrue(expr + " = " + total, total >= min && total <= max);
        }
    }

    @Test
    public void singleDie() {
        assertBetween("1d20", 1, 20);
    }

    @Test
    public void additionAndSubtraction() {
        assertBetween("1d20 + 5d10", 6, 70);
        assertBetween("1d20 - 5d10", 1 - 50, 20 - 5);
    }

    @Test
    public void multiplicationWithConstant() {
        assertBetween("1d20 * 5d10 + 10", 1 * 5 + 10, 20 * 50 + 10);
    }

    @Test
    public void parenthesesAndIntegerDivision() {
        // (1d20 + 5d10) is at least 6, so 2 / it is always 0.
        assertBetween("2 / (1d20 + 5d10) * 10", 0, 0);
        assertBetween("(1d20 + 5d10) * 2", 12, 140);
    }

    @Test
    public void constantOnly() {
        assertEquals(10, roller.diceParser("10"));
    }

    @Test
    public void rollKeepsEachDie() {
        assertTrue(roller.roll("3d6").getRollResults().iterator().hasNext());
    }

    @Test
    public void rollDice() {
        for (int i = 0; i < 200; i++) {
            int total = DiceRoller.rollDice(2, 6);
            assertTrue(total >= 2 && total <= 12);
        }
    }

    @Test(expected = IllegalStateException.class)
    public void invalidExpression() {
        roller.diceParser("1d20 +");
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyExpression() {
        roller.diceParser("   ");
    }
}
