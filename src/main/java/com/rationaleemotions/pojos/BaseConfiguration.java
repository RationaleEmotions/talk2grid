package com.rationaleemotions.pojos;

/**
 *
 */
abstract class BaseConfiguration {
    private String host;
    private int port;
    private int timeout;
    private int maxSession;
    private int browserTimeout;

    /**
     * @return - IP or hostname of the Hub that was set when the Hub was spun off (or) <code>null</code> if not set.
     * People usually resort to setting the hub's host/ip explicitly when there's an exotic network configurations
     * (e.g. network with VPN) involved.
     */
    public String getHost() {
        return host;
    }

    /**
     * @return - The port on which the hub is running.
     */
    public int getPort() {
        return port;
    }

    /**
     * @return - the timeout in seconds before the server automatically kills a session that hasn't had any activity in
     * the last X seconds. The test slot will then be released for another test to use. This is typically used to
     * take care of client crashes.
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * @return - max number of tests that can run at the same time on the node, irrespective of the browser used.
     */
    public int getMaxSession() {
        return maxSession;
    }

    /**
     * @return - number of seconds a browser session is allowed to hang (0 means indefinite) while a WebDriver
     * command is running (example: driver.get(url)).
     * If the timeout is reached while a WebDriver command is still processing, the session will quit.
     * Minimum value is 60. Default is 0
     */
    public int getBrowserTimeout() {
        return browserTimeout;
    }

}
