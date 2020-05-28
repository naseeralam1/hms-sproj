package com.connectDB;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoClass {

	public static DB connectdb() {
		//Connect to Database
		MongoClient mongoClient=new MongoClient();

		DB db=mongoClient.getDB("hms");
		System.out.println("Your connection to DB is ready for Use::"+db);
		
		return db;
	}

}
