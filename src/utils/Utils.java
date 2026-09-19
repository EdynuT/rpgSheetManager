package utils;

import model.Item;

public class Utils {
    /**
     * Calculates the total weight of the given items.
     *
     * @param items the items to sum the weight of
     * @return the total weight of all the items
     */
    public static double weightCalculator(Item... items) {
        double totalWeight = 0;
        for (Item item : items) {
            totalWeight += item.getWeight();
        }
        return totalWeight;
    }

    /**
     * Calculates the maximum weight a player can carry based on their strength.
     *
     * @param playerStrength the strength of the player
     * @return the maximum weight the player can carry
     */
    public static double maxCarryableWeight(double playerStrength) {
        return (5 * Math.log(playerStrength + 6.74) / Math.log(2)) + 1; //  (5 * ln(playerStrength + 6.74) / ln(2)) + 1
    }
}
