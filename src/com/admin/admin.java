package com.admin;

import com.connectDB.MongoClass;

import com.patient.*;
import com.doctor.*;

import org.joda.time.*;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class admin {

	public static void main(String[] args) {
		//Connect to DB
		DB db = MongoClass.connectdb();
		
		//Add Patient (DONE)
		addPatient(db,"faisal","71509-833338-4","Male",19,"Lahore Pakistan","faisal64","password123","03348399389");
		addPatient(db,"Kamran","71509-88999988-3","Male",20,"Karachi Pakistan","kamran12","password345","03348344444");
		addPatient(db,"Atif","71509-888111118-2","Male",21,"Faisalabad Pakistan","Atif11","password456","03348395555");
		
		//Add Doctor (DONE)
		addDoctor(db, "Dr. Arshad", "Male", 45 , "71509-8888888-0", "Lahore Pakistan", "034444444444", "Arshad123", "passcode123", "Cardiology", 1);
		addDoctor(db, "Dr. Amir", "Male", 50 , "71509-8885555-0", "Lahore Pakistan", "03445555555", "Amir123", "passcode345", "Radiology", 1);
		addDoctor(db, "Dr. Umair", "Male", 46 , "71509-99999999-0", "Lahore Pakistan", "034666666666", "Umair123", "passcode567", "Phisiology", 1);
		
		//Add Appointment (DONE)
		ObjectId patientid = new ObjectId("5e979ff17771221bd0b2c768");
		ObjectId docID = new ObjectId("5e979ff27771221bd0b2c76b");
		addAppointment(db,"2020-04-04","11:20:02:444",docID,patientid);
		addAppointment(db,"2020-06-12","12:50:12:234",docID,patientid);
		
		//Add Admin (DONE)
		addAdmin(db,"naseer", "male", 22, "naseer@gmail.com", "72209-9923456-1", "lahore", "0392884757", "naseer1122", "abc1234");
		addAdmin(db,"ahmad", "male", 22, "ahmad@gmail.com", "72224-933333-4", "lahore", "0349234567", "ahmad1122", "ertc3434");
		
		//find Admin (DONE)
		boolean foundAdmin = findAdmin(db,"72209-9923456-1");
		System.out.println("foundAdmin: " + foundAdmin);
		
		//Login Check (DONE)
		boolean login = loginCheck(db,"naseer1122","abc1234");
		System.out.println("login: " + login);	
	}
	
	private static int addPatient(DB db,String name, String nationalID, String gender,int age, String address,String username, String password, String phone) {
		
		if (patient.findPatient(db,nationalID) == false) {
			DBCollection collection = db.getCollection("patient");
			
			//populate Collection
			BasicDBObject document = new BasicDBObject();
			document.put("name",name);
			document.put("nationalID",nationalID);
			document.put("gender", gender);
			document.put("age",age);
			document.put("address",address);
			document.put("username",username);
			document.put("password",password);
			document.put("phone",phone);
						
			collection.insert(document);
			System.out.println("patient successfully added to the collection");
			return 1;
		}else {
			System.out.println("patient already exists");
			return 0;
		}
		
	}
	
	private static int addDoctor(DB db, String name, String gender, int age, String nationalID, String address, String phone, String username, String password, String department, int availability) {
		if(doctor.findDoctor(db,nationalID)==false) {
			DBCollection collection = db.getCollection("doctor");
			
			//make document
			BasicDBObject document = new BasicDBObject();		
			
			//populate Collection
			document = new BasicDBObject();
			document.put("name",name);
			document.put("gender",gender);
			document.put("age",age);
			document.put("nationalID",nationalID);
			document.put("address",address);
			document.put("phone",phone);
			document.put("username",username);
			document.put("password",password);
			document.put("department",department);
			document.put("availability",availability);
			
			collection.insert(document);
			System.out.println("doctor successfully added to the collection");
			return 1;
		}else {
			System.out.println("doctor already exists");
			return 0;
		}
		
	}
	
	private static int addAppointment(DB db,String date, String time, ObjectId docID,ObjectId patientid) {
		//get collection
		DBCollection patientCollection = db.getCollection("patient");
		DBCollection doctorCollection = db.getCollection("doctor");
		
		//find the patient by id
		DBObject patientDoc = patientCollection.findOne(patientid);
		if(patientDoc==null) {
			System.out.println("patient doesn't exist");
			return 0;
		}
		DBObject doctorDoc = doctorCollection.findOne(docID);
		if(doctorDoc==null) {
			System.out.println("patient doesn't exist");
			return 0;
		}
		DBObject cursor = (DBObject) patientDoc.get("Appointments");
		DBObject dateCursor = null;
		ObjectId timeCursor = null;
		if(cursor!=null) {
			dateCursor = (DBObject) cursor.get(date);
			if(dateCursor!=null) {
				timeCursor = (ObjectId) dateCursor.get(time);
			}
		}
		
		if(cursor != null && dateCursor!=null && timeCursor!=null) {
			System.out.println("The Appointment already exists");
			return 0;
		}else if(cursor==null){
			//In case the Appointment field does not exist in the document
			BasicDBObject document1 = new BasicDBObject();
			BasicDBObject document2 = new BasicDBObject();
			document1.put(time,docID);
			document2.put(time,patientid);
			
			BasicDBObject patientAppointment = new BasicDBObject();
			BasicDBObject doctorAppointment = new BasicDBObject();
			patientAppointment.put(date,document1);
			doctorAppointment.put(date,document2);
			
			patientDoc.put("Appointments",patientAppointment);
			doctorDoc.put("Appointments",doctorAppointment);
			
			//Update the Document
			BasicDBObject patientQuery = new BasicDBObject();
			BasicDBObject doctorQuery = new BasicDBObject();
			patientQuery.putAll(patientCollection.findOne(patientid));
			doctorQuery.putAll(doctorCollection.findOne(docID));

			BasicDBObject patientDocument = new BasicDBObject();
			BasicDBObject doctorDocument = new BasicDBObject();
			patientDocument.putAll(patientDoc);
			doctorDocument.putAll(doctorDoc);

			BasicDBObject updatePatient = new BasicDBObject();
			updatePatient.put("$set", patientDocument);
			
			BasicDBObject updateDoctor = new BasicDBObject();
			updateDoctor.put("$set", doctorDocument);

			patientCollection.update(patientQuery, updatePatient);
			doctorCollection.update(doctorQuery, updateDoctor);
			System.out.println("Appointment Field created: Appointment successfully added to patient and doctor Collection");
			return 1;
		}else {
			//The Appointment field already exists
			BasicDBObject document1 = new BasicDBObject();
			BasicDBObject document2 = new BasicDBObject();
			document1.put(time,docID);
			document2.put(time,patientid);
			
			BasicDBObject patientAppointment = new BasicDBObject();
			BasicDBObject doctorAppointment = new BasicDBObject();
			patientAppointment.put(date,document1);
			doctorAppointment.put(date,document2);
			
			BasicDBObject patientApps = (BasicDBObject) patientDoc.get("Appointments");
			BasicDBObject doctorApps = (BasicDBObject) doctorDoc.get("Appointments");
			patientApps.put(date,document1);
			doctorApps.put(date,document2);
			
			patientDoc.put("Appointments", patientApps);
			patientDoc.put("Appointments", doctorApps);
			
			//Update the Document
			BasicDBObject patientQuery = new BasicDBObject();
			BasicDBObject doctorQuery = new BasicDBObject();
			patientQuery.putAll(patientCollection.findOne(patientid));
			doctorQuery.putAll(doctorCollection.findOne(docID));

			BasicDBObject patientDocument = new BasicDBObject();
			BasicDBObject doctorDocument = new BasicDBObject();
			patientDocument.putAll(patientDoc);
			doctorDocument.putAll(doctorDoc);

			BasicDBObject updatePatient = new BasicDBObject();
			updatePatient.put("$set", patientDocument);
			
			BasicDBObject updateDoctor = new BasicDBObject();
			updateDoctor.put("$set", doctorDocument);

			patientCollection.update(patientQuery, updatePatient);
			doctorCollection.update(doctorQuery, updateDoctor);
			System.out.println("Appointment successfully added to Doctor and Patient Collection");
			return 1;
		}
		
	}
	
	private static int addAdmin(DB db,String name, String gender, int age, String email, String nationalID, String address, String phone, String username, String password ) {
		
		if(findAdmin(db,nationalID)==false) {
			DBCollection collection = db.getCollection("admin");
			
			//make document
			BasicDBObject document = new BasicDBObject();		
			
			//populate Collection
			document = new BasicDBObject();
			document.put("name",name);
			document.put("gender",gender);
			document.put("age",age);
			document.put("email",email);
			document.put("nationalID",nationalID);
			document.put("address",address);
			document.put("phone",phone);
			document.put("username",username);
			document.put("password",password);
			
			collection.insert(document);
			System.out.println("admin successfully added to the collection");
			return 1;
		}else{
			System.out.println("admin already exists");
			return 0;
		}
	}
	
	public static boolean findAdmin(DB db, String nationalID) {
		DBCollection collection = db.getCollection("admin");
		DBCursor cursor = collection.find();
				
		while(cursor.hasNext()) {
			String dbNationalID =cursor.next().get("nationalID").toString();
				
			if (dbNationalID.contentEquals(nationalID)) {
				return true;
			}
		}
		return false;
	}

	public static boolean loginCheck(DB db, String username, String password) {
		DBCollection collection = db.getCollection("admin");
		DBCursor cursor = collection.find();
		
		while(cursor.hasNext()) {
			String dbUsername =cursor.next().get("username").toString();
			String dbPassword =cursor.curr().get("password").toString();
		
			if (dbUsername.contentEquals(username) && dbPassword.contentEquals(password)) {
				return true;
			}
		}
		return false;
	}
}
