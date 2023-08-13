package com.hiperium.city.tasks.events.utils.agent;

import com.hiperium.city.tasks.events.ApplicationHandler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.InputStream;

/**
 * The class <code>TracingAgentUtil</code> is a helper class used only by the GraalVM Tracing Agent. This class is
 * excluded from the final JAR file.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TracingAgentUtil {

    public static void main(String[] args) {
        ApplicationHandler applicationHandler = new ApplicationHandler();
        InputStream inputStream = TracingAgentUtil.class.getClassLoader().getResourceAsStream("events/event.json");
        applicationHandler.handleRequest(inputStream, null, null);
    }

}
