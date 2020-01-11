package com.nacho.producer;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SuppressWarnings("rawtypes")
@Testcontainers
public class AppTestWithDockerCompose {

    @Container
    public static DockerComposeContainer compose = new DockerComposeContainer<>(new File("src/test/resources/docker-compose-test.yml"))
            .withExposedService("producer", 8080);

    @Test
    public void testPingGenericContainer() throws Exception {

        final String address = "http://" + compose.getServiceHost("producer", 8080) + ":" + compose.getServicePort("producer", 8080) + "/ping";
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
