package com.nacho.consumer;

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
}
