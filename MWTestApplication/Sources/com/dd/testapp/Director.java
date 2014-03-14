package com.dd.testapp;
import com.dd.mongwo.mwcontrol.MWGenericRecord;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import er.extensions.eof.ERXKey;


public class Director extends MWGenericRecord {
	public static final String ENTITY_NAME = "Director";
	public static final ERXKey<String> NAME = new ERXKey<String>("name");
	
	public Director(DBObject object, DBCollection collection) {
		super(object, collection);
		// TODO Auto-generated constructor stub
	}

	public Director() {
		super(ENTITY_NAME);
	}
	
	public String name(){
		return (String)storedValueForKey("name");
	}
	
	public void setName(String value){
		takeStoredValueForKey(value,"name");
	}

}
