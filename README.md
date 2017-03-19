![Build Status](https://travis-ci.org/RationaleEmotions/talk2grid.svg?branch=master)

# Talk 2 Grid

**Talk 2 Grid** is a very simple library that exposes some of the straight forward Http APIs that a Selenium Hub/Node
 provides.
 
 This library was built with an intention of interacting with the Hub/Node to get some useful information.
 
## Pre-requisites
 
 **Talk 2 Grid** requires :
 
 * **JDK 8**.
 * An already running Selenium Grid.
 * Tests should be making use of a Selenium Grid.
 * Test are not running against a remote execution environment service provider such as Sauce Labs (this is because 
 Sauce Labs does 
 not allow users to query its actual node's IP address. The same might be true for other execution environment 
 service providers as well.)

## How to use.

**Talk 2 Grid** is a [Maven](https://maven.apache.org/guides/getting-started/) artifact. In order to 
consume it, you merely need to add the following as a dependency in your pom file.

```xml
<dependency>
    <groupId>com.rationaleemotions</groupId>
    <artifactId>talk2grid</artifactId>
    <version>1.0.1</version>
</dependency>
```

### Viewing the Hub's configuration.

The below sample shows how to access the Hub's configuration.

```java
public void showHubConfiguration() {
    Host hub = new Host("localhost", "4444");
    GridApiAssistant assistant = new GridApiAssistant(hub);
    HubConfiguration hubConfig = assistant.getHubConfiguration();
    assertEquals("org.openqa.grid.internal.utils.DefaultCapabilityMatcher", hubConfig.getCapabilityMatcher());
}
```

### How to find out the node to which a test was routed to ?

The below sample shows how to find out the node to which a test was routed to and to retrieve the node's configuration.

```java
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
```

### How to route all traffic directly to the node for browser interactions ?

As we all know the machine on which the Grid hub runs can get pretty chatty very soon when the number of nodes starts increasing.<br>
This is because the Hub is always the single point of mediation between a test and the node on which the test actually runs.<br>
We can however, try and remediate this by enriching the `RemoteWebDriver` instance with the ability to toggle between the Hub and the Node when it comes to routing browser interaction messages. <br>

**The below code shows you how to do this:**

```java
package com.rationaleemotions.webdriver;

import com.rationaleemotions.RemoteWebDriverEnricher;
import com.rationaleemotions.pojos.Host;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

public class GridGames {
    public static void main(String[] args) throws Exception {
        RemoteWebDriver driver = null;
        Host hub = new Host("localhost", "4444");

        try {
            driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), DesiredCapabilities.firefox());
            driver = RemoteWebDriverEnricher.enrichRemoteWebDriverToInteractDirectlyWithNode(driver);
            driver.get("https://the-internet.herokuapp.com/");
            System.err.println("Page Title " + driver.getTitle());
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }
}
```

When you run this, you should see a big warning such as below :

```
Mar 19, 2017 10:35:28 AM com.rationaleemotions.RemoteWebDriverEnricher enrichRemoteWebDriverToInteractDirectlyWithNode
INFO: Traffic will now be routed directly to the node.
Mar 19, 2017 10:35:28 AM com.rationaleemotions.RemoteWebDriverEnricher enrichRemoteWebDriverToInteractDirectlyWithNode
WARNING: ********************************************************************************
Your Hub URL is [http://localhost:4444/grid/console]
1. It is configured with [1800 seconds] as timeout (via -timeout parameter.)
This means that the server automatically kills a session that hasn't had any activity in the last 1800 seconds.
2. It is configured with [5 seconds] as cleanup cycle (via -cleanUpCycle parameter.)
This means that the hub will poll for currently running sessions every [5 seconds] to check if there are any 'hung' sessions.
Both these values can cause your test session to be cleaned up and cause test failures.
So please ensure that you set the values for both these parameters on the grid to an appropriately higher value.
********************************************************************************
```

So here are some of the things that you should keep in mind before using this utility to have the browser interactions directly routed to the node.
This utility is dependent on two important parameters in the Hub.

* `-cleanUpCycle` - This parameter represents how often the Hub will poll all the proxies to find out if the sessions in each of the proxies are still active or if they can be cleaned up. So lets say you have set this value to be `5 seconds` and if your test runs for more than `5 seconds` then the Hub will treat your session as inactive (remember we are now by-passing the hub and routing all traffic to the node, so in the hub's perception the session is literally idle/hung) and clean it up. So this value should be set such that its more than the average life-time of a test.
* `-timeout` - This parameter represents the maximum allowed idle time for a session, before which it gets marked as "idle session" and gets cleaned up. Here also the value should be set such that its greater than the average life-time of a test.