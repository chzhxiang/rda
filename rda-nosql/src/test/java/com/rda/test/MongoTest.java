/*******************************************************************************
 * Project Key : et-win
 * author: lianrao
 * 2013-12-15 下午3:08:09
 ******************************************************************************/
package com.rda.test;

import java.net.UnknownHostException;
import java.util.Set;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;


/**
 * <P>TODO</P>
 * @author lianrao
 */
public class MongoTest {

	public static void main(String[] args) throws UnknownHostException {
		accessBySpringData();
	}

	public static void accessBySpringData() throws UnknownHostException {
		MongoOperations mongoOps = new MongoTemplate(new Mongo(), "mydb");
		mongoOps.insert(new Person("Joe", 34));
		System.out.println(mongoOps.findOne(new Query(Criteria.where("name").is("Joe")), Person.class));
		mongoOps.dropCollection("person");
	}

	public static void originAccessMethod() throws UnknownHostException {
		MongoClient mongoClient = new MongoClient("localhost", 27017);
		DB db = mongoClient.getDB("mydb");
		Set<String> colls = db.getCollectionNames();

		for (String s : colls) {
			System.out.println(s);
		}

		DBCollection coll = db.getCollection("testData");
		DBCursor find = coll.find();
		while (find.hasNext()) {
			DBObject next = find.next();
			System.out.println(next);
		}
	}
}
