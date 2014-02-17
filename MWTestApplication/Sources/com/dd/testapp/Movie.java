package com.dd.testapp;

import com.dd.mongwo.MWGenericRecord;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.eof.ERXKey;

public class Movie extends MWGenericRecord {
	public static final String ENTITY_NAME = "movie";
	public static ERXKey<String> NAME = new ERXKey<String>("name");
	public static ERXKey<String> STORY = new ERXKey<String>("story");
	public static ERXKey<Integer> REVENUE = new ERXKey<Integer>("revenue");
	public static ERXKey<NSTimestamp> RELEASE_DATE = new ERXKey<NSTimestamp>("releaseDate");
	public static ERXKey<NSArray<Country>> RELEASED_COUNTRIES = new ERXKey<NSArray<Country>>("releasedCountries");
	
	public Movie() {
		super("movie");
		// TODO Auto-generated constructor stub
	}
	
	public String name(){
		return (String)storedValueForKey("name");
	}
	
	public void setName(String value){
		takeStoredValueForKey(value, "name");
	}
	
	public String story(){
		return (String)storedValueForKey("story");
	}
	
	public void setStory(String value){
		takeStoredValueForKey(value,"story");
	}
	
	public Integer revenue(){
		return (Integer)storedValueForKey("revenue");
	}
	
	public void setRevenue(Integer value){
		takeStoredValueForKey(value, "revenue");
	}
	
	public NSArray<Country> releasedCountries(){
		return (NSArray<Country>)storedValuesForKey("releasedCountries");
	}
	


	public void setReleasedCountries(NSArray<Country> value){
		takeStoredValueForKey(value,"releasedCountries");
	}
	
	public NSTimestamp releaseDate(){
		return (NSTimestamp)storedValueForKey("releaseDate");
	}
	
	public void setReleaseDate(NSTimestamp value){
		takeStoredValueForKey(value, "releaseDate");
	}

	public Director director(){
		return (Director)storedValueForKey("director");
	}
	
	public void setDirector(Director value){
		takeStoredValueForKey(value, "director");
	}
	

}
