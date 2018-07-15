package com.rationaleemotions;

import com.rationaleemotions.pojos.Host;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.AbstractWaitStrategy;
import org.testcontainers.containers.wait.strategy.Wait;

final class Grid {
  private GenericContainer hub, node;

  private Grid(GenericContainer hub, GenericContainer node) {
    this.hub = hub;
    this.node = node;
  }

  Host getHub() {
      return new Host(hub.getContainerIpAddress(), hub.getFirstMappedPort().toString());
  }
  void stopGrid() {
      if (hub != null) {
          hub.stop();
      }
      if (node != null) {
          node.stop();
      }
  }

  static Grid newGrid() {
    Network network = Network.newNetwork();
    GenericContainer hub =
        new GenericContainer("selenium/hub:latest")
            .withNetwork(network)
            .withExposedPorts(4444)
            .withNetworkAliases("selenium-hub")
            .waitingFor(Wait.forHttp("/wd/hub/status").forStatusCode(200));
    hub.start();
    AbstractWaitStrategy isRegistered = new NodeRegistrationDetectorStrategy(hub);
    GenericContainer node =
        new GenericContainer("selenium/node-chrome:latest")
            .withNetwork(network)
            .withEnv("HUB_HOST", "selenium-hub")
            .waitingFor(isRegistered);
    node.start();
    return new Grid(hub, node);
  }
}
