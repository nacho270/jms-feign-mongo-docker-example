package com.nacho.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

@Service
public class MongoService {

    @Autowired
    private MongoClient mongoClient;

    public void saveObject(final String object) {
        final MongoCollection<Document> collection = mongoClient.getDatabase("producerDB").getCollection("objects");
        collection.insertOne(Document.parse(object));
    }

    public List<String> list() {
        final List<String> docs = new ArrayList<>();
        mongoClient.getDatabase("producerDB").getCollection("objects").find().forEach((Consumer<Document>) d -> {
            d.remove("_id");
            docs.add(d.toJson());
        });
        return docs;
    }
}
