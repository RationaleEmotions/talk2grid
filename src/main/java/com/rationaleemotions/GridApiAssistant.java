package com.rationaleemotions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.rationaleemotions.pojos.Host;
import com.rationaleemotions.pojos.HubConfiguration;
import com.rationaleemotions.pojos.IndividualCapability;
import com.rationaleemotions.pojos.NodeConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.io.Zip;

import static com.rationaleemotions.IndividualCapabilityDeserializer.getAdaptor;

/** The main helper class, that exposes some of the Hub and Node APIs in a Selenium Grid. */
public class GridApiAssistant {
  private static final String API_PROXY = "http://%s:%d/grid/api/proxy/?id=%s";
  private static final String API_HUB = "http://%s:%d/grid/api/hub/";
  private static final String API_TESTSESSION = "http://%s:%d/grid/api/testsession?session=%s";
  private static final String API_UPLOAD = "http://%s:%d/wd/hub/session/%s/file";
  private Host grid;
  private Gson gson;

  /** @param grid - A {@link Host} object that represents the Selenium Grid's hub machine. */
  public GridApiAssistant(Host grid) {
    this.grid = grid;
    gson =
        new GsonBuilder()
            .registerTypeAdapter(getAdaptor(), new IndividualCapabilityDeserializer())
            .create();
  }

  /**
   * A utility method that helps figure out to which node did the hub route your test to.
   *
   * @param sessionId - The session Id that can be retrieved via <code>driver.getSessionId()</code>
   *     [ here <code>driver</code> is usually a <code>RemoteWebDriver</code> instance.
   * @return - A {@link Host} object that represents the node ip and port to which the Selenium Hub
   *     routed the test case.
   */
  public Host getNodeDetailsForSession(String sessionId) {
    try {
      URL url =
          new URL(String.format(API_TESTSESSION, grid.getIpAddress(), grid.getPort(), sessionId));
      JsonObject object = SimpleHttpClient.get(url);
      return new Host(object.get("proxyId").getAsString());
    } catch (MalformedURLException e) {
      throw new GridApiException(e);
    }
  }

  /**
   * A utility method that helps get the configuration details of a particular node to which the hub
   * routed a test to.
   *
   * @param node - A {@link Host} object that represents a particular node to which a test was
   *     routed to. This information can be obtained by invoking {{@link
   *     #getNodeDetailsForSession(String)}}.
   * @return - A {@link NodeConfiguration} object that represents the configuration information of a
   *     particular node.
   */
  public NodeConfiguration getNodeConfigForSession(Host node) {
    try {
      String id = String.format("http://%s:%s", node.getIpAddress(), node.getPort());
      URL url = new URL(String.format(API_PROXY, grid.getIpAddress(), grid.getPort(), id));
      JsonObject object = SimpleHttpClient.get(url);
      boolean success = object.get("success").getAsBoolean();

      if (!success) {
        throw new IllegalArgumentException(object.get("msg").getAsString());
      }
      JsonObject request = object.get("request").getAsJsonObject();
      JsonObject config = request.get("configuration").getAsJsonObject();

      NodeConfiguration data = gson.fromJson(config, NodeConfiguration.class);
      if (request.has("capabilities")) {
        // Looks like we are dealing with a Selenium 2 Grid [ version <= 2.53.1 ]
        List<IndividualCapability> caps =
            gson.fromJson(
                request.get("capabilities"),
                new TypeToken<List<IndividualCapability>>() {}.getType());
        data.setCapabilities(caps);
      }
      data.setId(object.get("id").getAsString());
      return data;
    } catch (MalformedURLException e) {
      throw new GridApiException(e);
    }
  }

  /**
   * A utility method that retrieves the Hub's configuration.
   *
   * @return - A {@link HubConfiguration} object that represents the Selenium Hub's configuration.
   */
  public HubConfiguration getHubConfiguration() {
    try {
      URL url = new URL(String.format(API_HUB, grid.getIpAddress(), grid.getPort()));
      JsonObject object = SimpleHttpClient.get(url);
      return gson.fromJson(object, HubConfiguration.class);
    } catch (MalformedURLException e) {
      throw new GridApiException(e);
    }
  }

  /**
   * This utility method helps upload a file to a remote machine on which the current selenium test
   * is executing.
   *
   * @param sessionId - The session Id that can be retrieved via <code>driver.getSessionId()</code>
   *     [ here <code>driver</code> is usually a <code>RemoteWebDriver</code> instance.
   * @param file - The {@link File} to be uploaded.
   * @return - The path to the uploaded file on the remote file system of the node (where the test
   *     is currently executing).
   */
  public String uploadFileToNode(String sessionId, File file) {
    try {
      URL url = new URL(String.format(API_UPLOAD, grid.getIpAddress(), grid.getPort(), sessionId));
      Map<String, String> parameters = new HashMap<>();
      parameters.put("file", asString(file));
      return SimpleHttpClient.post(url, parameters).get("value").getAsString();
    } catch (MalformedURLException e) {
      throw new GridApiException(e);
    }
  }

  private String asString(File localFile) {
    if (!localFile.isFile()) {
      throw new GridApiException("You may only upload files: " + localFile);
    }

    try {
      return Zip.zip(localFile);
    } catch (IOException e) {
      throw new GridApiException("Cannot upload " + localFile, e);
    }
  }
}
