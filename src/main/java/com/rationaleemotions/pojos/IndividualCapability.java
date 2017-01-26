package com.rationaleemotions.pojos;

/**
 * A Simple POJO that represents a supported capability by a node.
 */
public class IndividualCapability {

    private String browserName;
    private String seleniumProtocol;
    private int maxInstances;
    private String platform;

    /**
     * @return - The browser flavor
     */
    public String getBrowserName() {
        return browserName;
    }

    /**
     * @return - The selenium protocol (either <code>selenium</code> protocol (or) the <code>webdriver</code> protocol)
     */
    public String getSeleniumProtocol() {
        return seleniumProtocol;
    }

    /**
     * @return - The maximum instances for the browser flavor.
     */
    public int getMaxInstances() {
        return maxInstances;
    }

    /**
     * @return - The Platform on which the node is configured to support the browser flavor.
     */
    public String getPlatform() {
        return platform;
    }

    @Override
    public String toString() {
        return String.format("Capability{browserName='%s', seleniumProtocol='%s', maxInstances=%d,, platform='%s'}"
            , getBrowserName(), getSeleniumProtocol(), getMaxInstances(), getPlatform());
    }
}
