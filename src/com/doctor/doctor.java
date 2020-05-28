package com.doctor;

import com.connectDB.MongoClass;

import org.bson.types.ObjectId;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.patient.PatientDetailOutput;

public class doctor {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DB db = MongoClass.connectdb();
		ObjectId doctorid = new ObjectId("5e979ff27771221bd0b2c76b");
		
		//findDoctor (DONE)
		boolean foundDoc = findDoctor(db,"71509-8888888-0");
		System.out.println("foundDoc: " + foundDoc);
				
		//find Doctor by ID (DONE)
		int foundDoctorById = findDoctorById(db,doctorid);
		System.out.println("foundDoctorById: " + foundDoctorById);
		
		//Doctor Details (DONE)
		DoctorDetailOutput doctorDetails = getDoctorDetails(db,doctorid);
		if(doctorDetails!=null) {
			System.out.println("dcotorName: " + doctorDetails.name);
			System.out.println("doctorGender: " + doctorDetails.gender);
			System.out.println("doctorAvailability: " + doctorDetails.availability);
		}
		
	}
	
	public static boolean findDoctor(DB db, String nationalID) {
		DBCollection collection = db.getCollection("doctor");
		DBCursor cursor = collection.find();
		
		while(cursor.hasNext()) {
			String dbNationalID = cursor.next().get("nationalID").toString();
		
			if (dbNationalID.contentEquals(nationalID)) {
				return true;
			} 
		}
		return false;	
	}
	
	private static int findDoctorById(DB db, ObjectId doctorid) {
		DBCollection collection = db.getCollection("doctor");
		
		DBObject doctorDoc = collection.findOne(doctorid);
		if (doctorDoc!=null) {
			return 1;
		}
		return 0;
	}
	
	public static DoctorDetailOutput getDoctorDetails(DB db, ObjectId doctorid) {
		// TODO Auto-generated method stub
		DBCollection collection = db.getCollection("doctor");
		DBCursor cursor = collection.find();
						
		while(cursor.hasNext()) {
			String a = cursor.next().get("_id").toString();
			String b = doctorid.toString();
			
			if(a.contentEquals(b)){
				DoctorDetailOutput out = new DoctorDetailOutput();
				out.name = cursor.curr().get("name").toString();
				out.gender = cursor.curr().get("gender").toString();
				out.age = (int) cursor.curr().get("age");
				out.nationalID = cursor.curr().get("nationalID").toString();
				out.address = cursor.curr().get("address").toString();
				out.phone = cursor.curr().get("phone").toString();
				out.username = cursor.curr().get("username").toString();
				out.password = cursor.curr().get("password").toString();
				out.department = cursor.curr().get("department").toString();
				out.availability = (int) cursor.curr().get("availability");
				return out;
			}		
		}
		return null;
	}
}
