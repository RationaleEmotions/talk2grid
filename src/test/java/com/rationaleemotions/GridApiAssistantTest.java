package com.rationaleemotions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import com.rationaleemotions.pojos.Host;
import com.rationaleemotions.pojos.HubConfiguration;
import com.rationaleemotions.pojos.NodeConfiguration;
import java.net.URL;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class GridApiAssistantTest {
  private Grid grid;
  private Host hubHost;

  @BeforeClass
  public void startGrid() {
    grid = Grid.newGrid();
    hubHost = grid.getHub();
  }

  @Test
  public void showHubConfiguration() {
    GridApiAssistant assistant = new GridApiAssistant(hubHost);
    HubConfiguration hubConfig = assistant.getHubConfiguration();
    assertEquals(
        "org.openqa.grid.internal.utils.DefaultCapabilityMatcher",
        hubConfig.getCapabilityMatcher());
  }

  @Test
  public void demonstrateToWhichNodeWasMyTestRoutedTo() throws Exception {
    RemoteWebDriver rwd = null;
    try {
      String url = String.format("http://%s:%d/wd/hub", hubHost.getIpAddress(), hubHost.getPort());
      rwd = new RemoteWebDriver(new URL(url), new ChromeOptions());
      // First lets get hold of the session id for our test.
      String sessionId = rwd.getSessionId().toString();
      GridApiAssistant assistant = new GridApiAssistant(hubHost);
      // Now lets query the Hub to figure out to which node did the hubHost route our test to.
      Host node = assistant.getNodeDetailsForSession(sessionId);
      assertNotNull(node);
      Reporter.log("Test routed to " + node.toString(), true);
      // Lets check what does the node configuration look like.
      NodeConfiguration nodeConfig = assistant.getNodeConfigForSession(node);
      // Here's how we get hold of the capabilities that are supported by this node.
      assertNotNull(nodeConfig.getCapabilities());
    } finally {
      if (rwd != null) {
        rwd.quit();
      }
    }
  }

  @AfterClass
  public void stopGrid() {
    grid.stopGrid();
  }
}
