package com.dd.testapp;

import com.dd.mongwo.MWGenericRecord;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.webobjects.foundation.NSArray;

import er.extensions.eof.ERXKey;

public class Country extends MWGenericRecord {
	public static final String ENTITY_NAME = "Country";
	public static final ERXKey<String> NAME = new ERXKey<String>("name");
	
	
	public Country(DBObject object, DBCollection collection) {
		super(object, collection);
		// TODO Auto-generated constructor stub
	}

	public Country() {
		super(ENTITY_NAME);
		// TODO Auto-generated constructor stub
	}
	
	public String name(){
		return (String)storedValueForKey("name");
	}
	
	public void setName(String value){
		takeStoredValueForKey(value,"name");
	}
	
	public NSArray<Movie> movies(){
		return (NSArray<Movie>)objectsForRefKey(Movie.ENTITY_NAME+"."+Movie.RELEASED_COUNTRIES.key());
	}
	


	public void setMovies(NSArray<Movie> value){
		takeObjectsForRefKey(value, Movie.ENTITY_NAME+"."+Movie.RELEASED_COUNTRIES.key());
	}


}
