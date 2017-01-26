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
    <version>1.0.0</version>
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
