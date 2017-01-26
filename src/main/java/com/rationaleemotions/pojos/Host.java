package com.rationaleemotions.pojos;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This POJO represents a remote host ( a machine on which either the Selenium Hub (or) Selenium Node is running.
 */
public class Host {
    private String ipAddress;
    private String port;

    /**
     * @param ipAddress - The IP address of the remote host.
     * @param port      - The port on which either the Selenium Hub (or) Node may-be listening to.
     */
    public Host(String ipAddress, String port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    /**
     * @param url - A {@link URL} that represents the IP port of either a Selenium Hub (or) Node.
     */
    public Host(URL url) {
        this(url.getHost(), Integer.toString(url.getPort()));
    }

    /**
     * @param url - A String that represents a valid {@link URL} of either a Selenium Hub (or) Node.
     * @throws MalformedURLException - In case the URL is not well formed.
     */
    public Host(String url) throws MalformedURLException {
        this(new URL(url));
    }

    /**
     * @return - The IP address.
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * @return - The port.
     */
    public int getPort() {
        return Integer.parseInt(port);
    }

    @Override
    public String toString() {
        return "Host [" + ipAddress + "[ listening on port [" + port + "]";
    }
}
