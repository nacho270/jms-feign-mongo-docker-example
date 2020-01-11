package com.nacho.producer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.mongodb.MongoClient;

@SuppressWarnings("rawtypes")
@Testcontainers
public class AppTestWithSeparateDockerImages {

    private static Network network = Network.newNetwork();

    @Container
    public static GenericContainer mongo = new GenericContainer<>("mongo:latest") //
            .withExposedPorts(27017) //
            .withNetwork(network) //
            .withNetworkAliases("mongodb");

    @Container
    public static GenericContainer activeMq = new GenericContainer<>("webcenter/activemq:latest") //
            .withExposedPorts(61616, 8161, 61613) //
            .withNetwork(network) //
            .withNetworkAliases("activemq");

    @Container
    public static GenericContainer producer = new GenericContainer<>("nacho/producer:latest") //
            .withNetworkAliases("producer") //
            .withExposedPorts(8080) //
            .dependsOn(mongo, activeMq) //
            .withNetwork(network);

    @Container
    public static GenericContainer consumer = new GenericContainer<>("nacho/consumer:latest") //
            .withNetworkAliases("consumer") //
            .withExposedPorts(8180) //
            .dependsOn(mongo, activeMq) //
            .withNetwork(network);

    @Test
    public void testPing() throws Exception {

        final String address = "http://localhost:" + producer.getMappedPort(8080) + "/ping";
        final String response = simpleGetRequest(address);

        assertEquals(response, "pong");
    }

    @Test
    public void testProduce() throws Exception {
        final String address = "http://localhost:" + producer.getMappedPort(8080) + "/produce";
        final String body = "{\"nombre\":\"nacho\"}";
        simplePost(address, body);
        Thread.sleep(2000L);
        assertMessageInMongo();
        assertMessageInApi();
    }

    private void assertMessageInApi() throws Exception {
        final String address = "http://localhost:" + consumer.getMappedPort(8180) + "/list";
        final String response = simpleGetRequest(address);
        assertEquals("[\"{\\\"nombre\\\": \\\"nacho\\\"}\"]", response);
    }

    private void assertMessageInMongo() throws InterruptedException {
        final List<String> docs = new ArrayList<>();
        try (MongoClient mongoClient = new MongoClient("localhost", mongo.getMappedPort(27017))) {
            mongoClient.getDatabase("producerDB").getCollection("objects").find().forEach((Consumer<Document>) d -> {
                docs.add(d.toJson());
            });
            assertEquals(1, docs.size());
            assertTrue(docs.get(0).contains("\"nombre\": \"nacho\""));
        }
    }

    private void simplePost(final String address, final String body) throws Exception {
        final URL url = new URL(address);
        final URLConnection con = url.openConnection();
        final HttpURLConnection http = (HttpURLConnection) con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        final byte[] out = body.getBytes(StandardCharsets.UTF_8);
        final int length = out.length;

        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        http.connect();
        try (OutputStream os = http.getOutputStream()) {
            os.write(out);
        }
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
