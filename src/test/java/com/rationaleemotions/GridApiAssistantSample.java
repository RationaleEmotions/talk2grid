package com.rationaleemotions;

import com.rationaleemotions.pojos.Host;
import com.rationaleemotions.pojos.HubConfiguration;
import com.rationaleemotions.pojos.NodeConfiguration;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.net.URL;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Unit test for simple App.
 */
public class GridApiAssistantSample {

    @Test
    public void showHubConfiguration() {
        Host hub = new Host("localhost", "4444");
        GridApiAssistant assistant = new GridApiAssistant(hub);
        HubConfiguration hubConfig = assistant.getHubConfiguration();
        assertEquals("org.openqa.grid.internal.utils.DefaultCapabilityMatcher", hubConfig.getCapabilityMatcher());
    }

    @Test
    public void demonstrateToWhichNodeWasMyTestRoutedTo() throws Exception {
        RemoteWebDriver rwd = null;
        Host hub = new Host("localhost", "4444");
        try {
            String url = String.format("http://%s:%d/wd/hub", hub.getIpAddress(), hub.getPort());
            rwd = new RemoteWebDriver(new URL(url), DesiredCapabilities.chrome());
            //First lets get hold of the session id for our test.
            String sessionId = rwd.getSessionId().toString();
            GridApiAssistant assistant = new GridApiAssistant(hub);
            //Now lets query the Hub to figure out to which node did the hub route our test to.
            Host node = assistant.getNodeDetailsForSession(sessionId);
            assertNotNull(node);
            Reporter.log("Test routed to " + node.toString(), true);
            //Lets check what does the node configuration look like.
            NodeConfiguration nodeConfig = assistant.getNodeConfigForSession(node);
            //Here's how we get hold of the capabilities that are supported by this node.
            assertNotNull(nodeConfig.getCapabilities());
        } finally {
            if (rwd != null) {
                rwd.quit();
            }
        }
    }
}
