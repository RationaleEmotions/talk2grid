package com.rationaleemotions;

import com.google.common.base.Strings;
import com.rationaleemotions.pojos.Host;
import com.rationaleemotions.pojos.HubConfiguration;
import org.openqa.selenium.remote.CommandCodec;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.ResponseCodec;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.logging.Logger;

/**
 * A helper class that enriches a {@link RemoteWebDriver} instance such that its able to directly talk to
 * the Selenium Node for all browser interactions and talks to the Selenium Hub only when the session is being terminated.
 */
public class RemoteWebDriverEnricher {
    private static final Logger LOG = Logger.getLogger(RemoteWebDriverEnricher.class.getName());

    private RemoteWebDriverEnricher() {
        //Utility class. Defeat instantiation.
    }

    /**
     * A helper method that enriches a {@link RemoteWebDriver} instance with the ability to route all browser
     * interaction requests directly to the node on which the session was created and route only the session termination
     * request to the hub.
     *
     * @param driver - A {@link RemoteWebDriver} instance.
     * @return - A {@link RemoteWebDriver} instance that is enriched with the ability to route all browser interactions
     * directly to the node.
     */
    public static RemoteWebDriver enrichRemoteWebDriverToInteractDirectlyWithNode(RemoteWebDriver driver) {
        Host hub = getHubInfo(driver);
        return enrichRemoteWebDriverToInteractDirectlyWithNode(driver, hub);
    }

    /**
     * A helper method that enriches a {@link RemoteWebDriver} instance with the ability to route all browser
     * interaction requests directly to the node on which the session was created and route only the session termination
     * request to the hub.
     *
     * @param driver - A {@link RemoteWebDriver} instance.
     * @param hub    - A {@link Host} object that represents the Hub information.
     * @return - A {@link RemoteWebDriver} instance that is enriched with the ability to route all browser interactions
     * directly to the node.
     */
    public static RemoteWebDriver enrichRemoteWebDriverToInteractDirectlyWithNode(RemoteWebDriver driver, Host hub) {
        if (hub == null) {
            return driver;
        }
        try {
            CommandExecutor grid = driver.getCommandExecutor();
            String sessionId = driver.getSessionId().toString();
            GridApiAssistant assistant = new GridApiAssistant(hub);

            Host nodeHost = assistant.getNodeDetailsForSession(sessionId);
            URL url = new URL(String.format("http://%s:%d/wd/hub", nodeHost.getIpAddress(), nodeHost.getPort()));
            CommandExecutor node = new HttpCommandExecutor(url);
            CommandCodec commandCodec = getCodec(grid, "commandCodec");
            ResponseCodec responseCodec = getCodec(grid, "responseCodec");
            setCodec(node, commandCodec, "commandCodec");
            setCodec(node, responseCodec, "responseCodec");
            appendListenerToWebDriver(driver, grid, node);
            LOG.info("Traffic will now be routed directly to the node.");
            LOG.warning(constructWarningMessage(hub));
        } catch (Exception e) {
            //Gobble exceptions
            LOG.warning("Unable to enrich the RemoteWebDriver instance. Root cause :" + e.getMessage()
                + ". Returning back the original instance that was passed, as is.");
        }
        return driver;
    }

    private static String constructWarningMessage(Host hub) {
        String separator = Strings.repeat("*", 80);
        StringBuilder msg = new StringBuilder(separator);
        GridApiAssistant assistant = new GridApiAssistant(hub);
        HubConfiguration hubConfig = assistant.getHubConfiguration();
        int timeout = hubConfig.getTimeout();
        String hubUrl = String.format("http://%s:%s/grid/console", hub.getIpAddress(), hub.getPort());
        msg.append("\nYour Hub URL is [").append(hubUrl).append("]");
        msg.append("\n1. It is configured with [").append(timeout).append(" seconds] as timeout (via -timeout parameter.)\n");
        msg.append("This means that the server automatically kills a session that hasn't had any activity in the last ");
        msg.append(timeout).append(" seconds.");
        int cleanupCycle = hubConfig.getCleanUpCycle() / 1000;
        msg.append("\n2. It is configured with [").append(cleanupCycle).append(" seconds] as cleanup cycle ");
        msg.append("(via -cleanUpCycle parameter.)");
        msg.append("\nThis means that the hub will poll for currently running sessions every [");
        msg.append(cleanupCycle).append(" seconds] to check if there are any 'hung' sessions.");
        msg.append("\nBoth these values can cause your test session to be cleaned up and cause test failures.");
        msg.append("\nSo please ensure that you set the values for both these parameters on the grid to an appropriately higher value.");
        msg.append("\n").append(separator);
        return msg.toString();
    }

    private static Host getHubInfo(RemoteWebDriver driver) {
        Host hub = null;
        CommandExecutor executor = driver.getCommandExecutor();
        if (executor instanceof HttpCommandExecutor) {
            URL url = ((HttpCommandExecutor) executor).getAddressOfRemoteServer();
            hub = new Host(url.getHost(), Integer.toString(url.getPort()));
        }
        return hub;
    }

    @SuppressWarnings("unchecked")
    private static <T> T getCodec(CommandExecutor executor, String fieldName) throws Exception {
        Class clazz = executor.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(executor);
    }

    private static <T> void setCodec(CommandExecutor executor, T codec, String fieldName) throws Exception {
        Class clazz = executor.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(executor, codec);
    }

    @SuppressWarnings("unchecked")
    private static void appendListenerToWebDriver(RemoteWebDriver rwd, CommandExecutor grid, CommandExecutor node)
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        CommandExecutor executor = new CustomCommandExecutor(grid, node);
        Class clazz = rwd.getClass();
        while (!RemoteWebDriver.class.equals(clazz)) {
            clazz = clazz.getSuperclass();
        }
        Method m = clazz.getDeclaredMethod("setCommandExecutor", CommandExecutor.class);
        m.setAccessible(true);
        m.invoke(rwd, executor);
    }

}
