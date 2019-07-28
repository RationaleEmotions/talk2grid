package com.rationaleemotions;

import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.openqa.selenium.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;

/** An internal class that is used to make HTTP calls. */
class SimpleHttpClient {

    private static final String APPLICATION_JSON = "application/json";

    private interface Marker {}

  private static final Logger LOGGER = LoggerFactory.getLogger(Marker.class.getEnclosingClass());

  private SimpleHttpClient() {
    // utility class. Defeat instantiation.
  }

  static JsonObject get(URL endPoint) {
    String response = doHttpCall(getMethod(HttpGet.METHOD_NAME, endPoint));
    return new JsonParser().parse(response).getAsJsonObject();
  }

  static JsonObject post(URL endpoint, Map<String, String> parameters) {
    String content = new Json().toJson(parameters);
      StringEntity entity;
      try {
          entity = new StringEntity(content);
      } catch (UnsupportedEncodingException e) {
          throw new GridApiException(e);
      }
    HttpPost httpPost = (HttpPost) getMethod(HttpPost.METHOD_NAME, endpoint);
    httpPost.setHeader(ACCEPT, APPLICATION_JSON);
    httpPost.setHeader(CONTENT_TYPE, APPLICATION_JSON);
    httpPost.setEntity(entity);
    String response = doHttpCall(httpPost);
    return new JsonParser().parse(response).getAsJsonObject();
  }

  private static String doHttpCall(HttpRequestBase request) {
    String method = request.getMethod();
    URI endpoint = request.getURI();
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Invoking " + method + " on the end-point : " + endpoint);
    }
    try (CloseableHttpResponse response = getClient().execute(request)) {
      try (Writer writer = new StringWriter()) {
        InputStream isr = response.getEntity().getContent();
        IOUtils.copy(isr, writer);
        return writer.toString();
      }
    } catch (IOException e) {
      String msg =
          String.format(
              "[%s] call on [%s] failed. Root cause : [%s]", method, endpoint, e.getMessage());
      throw new GridApiException(msg, e);
    }
  }

  private static HttpRequestBase getMethod(String type, URL endpoint) {
    HttpRequestBase method = null;
    URI uri;
    try {
      uri = endpoint.toURI();
    } catch (URISyntaxException e) {
      throw new GridApiException(e);
    }
    if (type.equals(HttpPost.METHOD_NAME)) {
      method = new HttpPost(uri);

    } else if (type.equals(HttpGet.METHOD_NAME)) {
      method = new HttpGet(uri);
    }
    return method;
  }

  private static CloseableHttpClient getClient() {
    return HttpClientBuilder.create().build();
  }
}
