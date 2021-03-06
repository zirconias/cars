package com.stefanski.cars.search;

import java.util.List;
import java.util.Map;

import com.mongodb.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.stefanski.cars.store.Car;
import com.stefanski.cars.store.events.DeletedCarEvent;
import com.stefanski.cars.store.events.NewCarEvent;
import com.stefanski.cars.store.events.UpdatedCarEvent;

import static java.util.stream.Collectors.toList;

/**
 * @author Dariusz Stefanski
 */
@Component
@Slf4j
public class AttributeStore {

    private static final String DB_NAME = "cars";
    private static final String COLLECTION_NAME = "attributes";
    private static final String ID_FIELD = "_id";

    private DBCollection collection;

    @Autowired
    AttributeStore(Mongo mongo) {
        DB db = mongo.getDB(DB_NAME);
        this.collection = db.getCollection(COLLECTION_NAME);
    }

    @EventListener
    void insertAttributes(NewCarEvent event) {
        log.debug("Handling event {}", event);
        BasicDBObject doc = createDocument(event.getCar());
        collection.insert(doc);
    }

    @EventListener
    void updateAttributes(UpdatedCarEvent event) {
        log.debug("Handling event {}", event);
        BasicDBObject doc = createDocument(event.getCar());
        collection.save(doc);
    }

    @EventListener
    void deleteAttributes(DeletedCarEvent event) {
        log.debug("Handling event {}", event);
        BasicDBObject doc = new BasicDBObject(ID_FIELD, event.getCarId());
        collection.remove(doc);
    }

    List<Long> findCars(Map<String, Object> query) {
        BasicDBObject queryDoc = new BasicDBObject(query);
        BasicDBObject fieldsDoc = new BasicDBObject(ID_FIELD, 1);
        List<DBObject> dbObjects = collection.find(queryDoc, fieldsDoc).toArray();
        List<Long> carIds =  extractIds(dbObjects);
        log.debug("Found {} cars for query {}", carIds);
        return carIds;
    }

    private BasicDBObject createDocument(Car car) {
        BasicDBObject doc = new BasicDBObject(car.getAttributesMap());
        doc.putAll(car.getBasicFieldsMap());
        doc.put(ID_FIELD, car.getId());
        return doc;
    }

    private List<Long> extractIds(List<DBObject> dbObjects) {
        return dbObjects.stream()
                .map(dbObject -> (Long) dbObject.get(ID_FIELD))
                .collect(toList());
    }
}
