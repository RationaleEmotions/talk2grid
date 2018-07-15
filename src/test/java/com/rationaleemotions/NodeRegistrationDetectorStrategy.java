package com.rationaleemotions;

import com.rationaleemotions.pojos.Host;
import com.rationaleemotions.pojos.SlotCount;
import java.util.concurrent.TimeUnit;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.AbstractWaitStrategy;

public class NodeRegistrationDetectorStrategy extends AbstractWaitStrategy {
  private Host hub;

  NodeRegistrationDetectorStrategy(GenericContainer hub) {
    this.hub = new Host(hub.getContainerIpAddress(), hub.getFirstMappedPort().toString());
  }

  @Override
  protected void waitUntilReady() {
    int timeout = (int) this.startupTimeout.getSeconds();
    Unreliables.retryUntilTrue(
        timeout,
        TimeUnit.SECONDS,
        () -> {
          SlotCount count = new GridApiAssistant(hub).getHubConfiguration().getSlotCount();
          return count.getTotal() != 0;
        });
  }
}
