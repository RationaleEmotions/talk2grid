package com.rationaleemotions.pojos;

/**
 * A Simple POJO that represents the slot information.
 * A slot can be visualised as one combination of browser flavor, version, platform and instance.
 * Lets say we have a node configuration json file that looks like <a href='https://github
 * .com/SeleniumHQ/selenium/blob/master/java/server/src/org/openqa/grid/common/defaults/DefaultNodeWebDriver
 * .json'>this</a>. Then the number of slots would be equal to <code>11</code>.
 * <ul>
 * <li>5 slots for firefox (because the json defines maxInstances as 5 for firefox)</li>
 * <li>5 slots for chrome</li>
 * <li>1 slot for internet explorer</li>
 * </ul>
 */
public class SlotCount {

    private int free;
    private int total;

    /**
     * @return - The number of free slots
     */
    public int getFree() {
        return free;
    }

    /**
     * @return - The total number of slots.
     */
    public int getTotal() {
        return total;
    }

    @Override
    public String toString() {
        return String.format("SlotCount{free=%d,total=%d}", getFree(), getTotal());
    }
}
