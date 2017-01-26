package com.rationaleemotions.pojos;

import java.util.List;

/**
 *
 */
public class NodeConfiguration extends BaseConfiguration {
    private String hubHost;
    private int hubPort;
    private String id;
    private List<IndividualCapability> capabilities = null;
    private String hub;
    private int nodeStatusCheckTimeout;
    private String proxy;
    private int registerCycle;

    /**
     * @return - The Hub IP (or) Name.
     */
    public String getHubHost() {
        return hubHost;
    }

    /**
     * @return - The Port on which the Hub is listening to.
     */
    public int getHubPort() {
        return hubPort;
    }

    /**
     * @return - A String of the form <code>http://ip:port</code> which helps the Grid uniquely identify a particular
     * node.
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return - A List of {@link IndividualCapability} that represents all the browser combos that this node currently
     * supports.
     */
    public List<IndividualCapability> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<IndividualCapability> capabilities) {
        this.capabilities = capabilities;
    }

    /**
     * @return - The registration URL of the hub that the node used to hook itself into the Hub.
     */
    public String getHub() {
        return hub;
    }

    /**
     * @return - How frequently will the node be polled for any connection/socket timeouts [ time in milli-seconds ]
     */
    public int getNodeStatusCheckTimeout() {
        return nodeStatusCheckTimeout;
    }

    /**
     * @return - The fully qualified class name that represents itself as a proxy instance to the hub.
     */
    public String getProxy() {
        return proxy;
    }

    /**
     * @return -  how often the node will try to register itself again.
     */
    public int getRegisterCycle() {
        return registerCycle;
    }

    @Override
    public String toString() {
        return "NodeData{" +
            "hubHost='" + hubHost + '\'' +
            ", hubPort=" + hubPort +
            ", id='" + id + '\'' +
            ", capabilities=" + capabilities +
            ", hub='" + hub + '\'' +
            ", nodeStatusCheckTimeout=" + nodeStatusCheckTimeout +
            ", proxy='" + proxy + '\'' +
            ", registerCycle=" + registerCycle +
            ", host='" + getHub() + '\'' +
            ", maxSession=" + getMaxSession() +
            ", browserTimeout=" + getBrowserTimeout() +
            ", port=" + getPort() +
            ", timeout=" + getTimeout() +
            '}';
    }
}
