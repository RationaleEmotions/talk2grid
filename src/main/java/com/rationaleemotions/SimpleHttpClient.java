package com.rationaleemotions;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * An internal class that is used to make HTTP calls.
 */
class SimpleHttpClient {
    private interface Marker {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Marker.class.getEnclosingClass());

    private SimpleHttpClient() {
        //utility class. Defeat instantiation.
    }

    static JsonObject post(URL endPoint) {
        return new JsonParser().parse(simplePost(endPoint)).getAsJsonObject();
    }

    static String simplePost(URL endpoint) {
        return doHttpCall(endpoint, HttpPost.METHOD_NAME);
    }

    static JsonObject get(URL endPoint) {
        return new JsonParser().parse(doHttpCall(endPoint, HttpGet.METHOD_NAME)).getAsJsonObject();
    }

    private static String doHttpCall(URL endpoint, String method) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Invoking " + method + " on the end-point : " + endpoint);
        }
        try (CloseableHttpResponse response = getClient().execute(getMethod(method, endpoint))) {
            try (Writer writer = new StringWriter()) {
                InputStream isr = response.getEntity().getContent();
                IOUtils.copy(isr, writer);
                return writer.toString();
            }
        } catch (IOException | URISyntaxException e) {
            String msg = String.format("[%s] call on [%s] failed. Root cause : [%s]", method, endpoint, e.getMessage());
            throw new GridApiException(msg, e);
        }
    }

    private static HttpRequestBase getMethod(String type, URL endpoint) throws URISyntaxException {
        HttpRequestBase method = null;
        if (type.equals(HttpPost.METHOD_NAME)) {
            method = new HttpPost(endpoint.toURI());

        } else if (type.equals(HttpGet.METHOD_NAME)) {
            method = new HttpGet(endpoint.toURI());

        }
        return method;
    }

    private static CloseableHttpClient getClient() {
        return HttpClientBuilder.create().build();
    }

}
