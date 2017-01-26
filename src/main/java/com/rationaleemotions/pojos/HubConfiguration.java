package com.rationaleemotions.pojos;

import java.util.List;

/**
 * A Simple POJO that represents a Hub's configuration.
 */
public class HubConfiguration extends BaseConfiguration {

    private String capabilityMatcher;
    private int newSessionWaitTimeout;
    private boolean throwOnCapabilityNotPresent;
    private int cleanUpCycle;
    private int jettyMaxThreads;
    private List<String> servlets = null;
    private int newSessionRequestCount;
    private SlotCount slotCounts;

    /**
     * @return - The fully qualified class name of the Capability Matcher that is currently being used by the Hub.
     * A capability matcher is used to find out if a capabilities requested by a selenium testcase matches something on
     * the remote and should be forwarded by the grid.
     */
    public String getCapabilityMatcher() {
        return capabilityMatcher;
    }

    /**
     * @return - The time after which a new test waiting for a node to become available will time out. When that
     * happens, the test will throw an exception before attempting to start a browser. Defaults to no timeout ( -1 )
     */
    public int getNewSessionWaitTimeout() {
        return newSessionWaitTimeout;
    }

    /**
     * @return - If true, the hub will reject all test requests if no compatible proxy is currently registered. If
     * set to false, the request will queue until a node supporting the capability is registered with the grid.
     * Default is <code>true</code>
     */
    public boolean isThrowOnCapabilityNotPresent() {
        return throwOnCapabilityNotPresent;
    }

    /**
     * @return - time in milli-seconds which represents how often the hub will poll running proxies for timed-out (i.e
     * . hung) threads.
     */
    public int getCleanUpCycle() {
        return cleanUpCycle;
    }



    /**
     * @return - list of extra servlets that have been wired into the Grid when it was started.
     */
    public List<String> getServlets() {
        return servlets;
    }


    /**
     * @return - max number of threads for Jetty
     */
    public int getJettyMaxThreads() {
        return jettyMaxThreads;
    }

    /**
     * @return - Represents the number of tests that are waiting for a slot in the Grid so that they can beginning
     * running. (A valid value is received only when working against a Selenium Grid that runs on Selenium v3.0.1 (or)
     * higher.
     */
    public int getNewSessionRequestCount() {
        return newSessionRequestCount;
    }

    /**
     * @return - A {@link SlotCount} object that represents how many slots in the Grid (free and total slot count).
     */
    public SlotCount getSlotCount() {
        return slotCounts;
    }

    @Override
    public String toString() {
        return "HubConfiguration1{capabilityMatcher='" + getCapabilityMatcher() + '\'' +
            ", newSessionWaitTimeout=" + getNewSessionRequestCount() +
            ", throwOnCapabilityNotPresent=" + isThrowOnCapabilityNotPresent() +
            ", cleanUpCycle=" + getCleanUpCycle() +
            ", host='" + getHost() + '\'' +
            ", maxSession=" + getMaxSession() +
            ", servlets=" + getServlets() +
            ", browserTimeout=" + getBrowserTimeout() +
            ", jettyMaxThreads=" + getJettyMaxThreads() +
            ", port=" + getPort() +
            ", timeout=" + getTimeout() +
            ", newSessionRequestCount=" + getNewSessionRequestCount() +
            ", slotCount=" + getSlotCount() +
            '}';
    }
}
