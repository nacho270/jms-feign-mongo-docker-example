package com.nacho.producer;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SuppressWarnings("rawtypes")
@Testcontainers
public class AppTestWithSeparateDockerImages {

    @Container
    public static GenericContainer mongo = new GenericContainer("mongo:latest").withExposedPorts(27017);

    @Container
    public static GenericContainer activeMq = new GenericContainer("webcenter/activemq:latest").withExposedPorts(61616, 8161, 61613);

    @Container
    public static GenericContainer app = new GenericContainer<>("nacho/producer:latest").withExposedPorts(8080).dependsOn(mongo, activeMq);

    @Test
    public void testPingGenericContainer() throws Exception {

        final String address = "http://localhost:" + app.getMappedPort(8080) + "/ping";
        final String response = simpleGetRequest(address);

        assertEquals(response, "pong");
    }

    private String simpleGetRequest(final String address) throws Exception {
        final URL url = new URL(address);
        final HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        final StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        return content.toString();
    }
}
