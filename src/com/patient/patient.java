package com.patient;

import com.connectDB.MongoClass;

import org.joda.time.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

public class patient {
	public static void main(String[] args) {
		DB db = MongoClass.connectdb();
		
		//find Patient (DONE)
		boolean foundPatient = findPatient(db,"71509-833338-4");
		System.out.println("foundPatient: " + foundPatient);
		
		//find Patient by ID (DONE)
		ObjectId patientid = new ObjectId("5e979ff17771221bd0b2c768");
		int foundPatientById = findPatientById(db,patientid);
		System.out.println("foundPatientById: " + foundPatientById);
		
		//removePatientById(db,patientid); (DONE)
		
		//Patient Details
		PatientDetailOutput patientDetails = getPatientDetails(db,patientid);
		if(patientDetails!=null) {
			System.out.println("patientName: " + patientDetails.name);
			System.out.println("patientAge: " + patientDetails.age);
		}
	}
	
	public static boolean findPatient(DB db, String nationalID) {
		DBCollection collection = db.getCollection("patient");
		DBCursor cursor = collection.find();
				
		while(cursor.hasNext()) {
			String dbNationalID =cursor.next().get("nationalID").toString();
				
			if (dbNationalID.contentEquals(nationalID)) {
				System.out.println("patient found");
				return true;
			}
		}
		System.out.println("patient doesn't exist");
		return false;
	}

	public static  PatientDetailOutput getPatientDetails(DB db,ObjectId patientid) {
		// TODO Auto-generated method stub
		DBCollection collection = db.getCollection("patient");
		DBCursor cursor = collection.find();
				
		while(cursor.hasNext()) {
			String a = cursor.next().get("_id").toString();
			String b =patientid.toString();
			
			if(a.contentEquals(b)){
				PatientDetailOutput out = new PatientDetailOutput(); 
				out.name = cursor.curr().get("name").toString();
				out.nationalID = cursor.curr().get("nationalID").toString();
				out.gender = cursor.curr().get("gender").toString();
				out.age = (int) cursor.curr().get("age");
				out.address = cursor.curr().get("address").toString();
				out.username = cursor.curr().get("username").toString();
				out.password = cursor.curr().get("password").toString();
				out.phone = cursor.curr().get("phone").toString();
				return out;
			}		
		}
		return null;
	}

	private static int findPatientById(DB db, ObjectId patientid) {
		DBCollection collection = db.getCollection("patient");
		
		DBObject patientDoc = collection.findOne(patientid);
		if (patientDoc!=null) {
			return 1;
		}
		return 0;
	}
	
	public static void removePatientById(DB db,ObjectId patientid) {
		if(findPatientById(db,patientid)==1) {
			DBCollection collection = db.getCollection("patient");
			
			BasicDBObject document = new BasicDBObject();
			document.put("_id", patientid);
			collection.remove(document);
			System.out.println("Deleted document successfully");
		}		
	}
}
