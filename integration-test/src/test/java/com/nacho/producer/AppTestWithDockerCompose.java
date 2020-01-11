package com.nacho.producer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
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
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.mongodb.MongoClient;

@SuppressWarnings("rawtypes")
@Testcontainers
public class AppTestWithDockerCompose {

    @Container
    public static DockerComposeContainer compose = new DockerComposeContainer<>(new File("src/test/resources/docker-compose-test.yml"))
            .withExposedService("producer", 8080) //
            .withExposedService("mongodb", 27017) //
            .withExposedService("consumer", 8180);

    @Test
    public void testPing() throws Exception {
        final String address = "http://" + compose.getServiceHost("producer", 8080) + ":" + compose.getServicePort("producer", 8080) + "/ping";
        final String response = simpleGetRequest(address);
        assertEquals(response, "pong");
    }

    @Test
    public void testProduce() throws Exception {
        final String address = "http://" + compose.getServiceHost("producer", 8080) + ":" + compose.getServicePort("producer", 8080) + "/produce";
        final String body = "{\"nombre\":\"nacho\"}";
        simplePost(address, body);
        Thread.sleep(2000L);
        assertMessageInMongo();
        assertMessageInApi();
    }

    private void assertMessageInApi() throws Exception {
        final String address = "http://" + compose.getServiceHost("consumer", 8180) + ":" + compose.getServicePort("consumer", 8180) + "/list";
        final String response = simpleGetRequest(address);
        assertEquals("[\"{\\\"nombre\\\": \\\"nacho\\\"}\"]", response);
    }

    private void assertMessageInMongo() throws InterruptedException {
        final List<String> docs = new ArrayList<>();
        try (MongoClient mongoClient = new MongoClient(compose.getServiceHost("mongodb", 27017), compose.getServicePort("mongodb", 27017))) {
            mongoClient.getDatabase("producerDB").getCollection("objects").find().forEach((Consumer<Document>) d -> {
                docs.add(d.toJson());
            });
            assertEquals(1, docs.size());
            assertTrue(docs.get(0).contains("\"nombre\": \"nacho\""));
        }
    }

    // private void messageReceived() {
    // try {
    // final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
    // "tcp://" + compose.getServiceHost("activemq", 61616) + ":" + compose.getServicePort("activemq", 61616));
    // final Connection connection = connectionFactory.createConnection();
    // connection.start();
    // final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    // final Destination destination = session.createQueue("OBJECTS_QUEUE");
    // final MessageConsumer consumer = session.createConsumer(destination);
    // final Message message = consumer.receive(5000);
    // final TextMessage textMessage = (TextMessage) message;
    // final String text = textMessage.getText();
    // assertEquals("{\"nombre\":\"nacho\"}", text);
    // consumer.close();
    // session.close();
    // connection.close();
    // } catch (final Exception e) {
    // System.out.println("Caught: " + e);
    // e.printStackTrace();
    // }
    // }

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

}
