package com.dd.mongwo.mwcontrol;
import java.util.regex.Pattern;

import com.mongodb.BasicDBObject;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSSelector;


public class MWKeyValueQualifier extends MWQualifier {
	private String key;
	private Object value;
	private NSSelector selector;
	
	public MWKeyValueQualifier(String key, Object value, NSSelector selector) {
		super();
		this.key = key;
		this.value = value;
		this.selector = selector;
	}
	
	private Object evaluatingValuePart(){
		if(EOQualifier.QualifierOperatorEqual.equals(selector)){
			return value;
		}
		
		if(EOQualifier.QualifierOperatorCaseInsensitiveLike.equals(selector)||
				EOQualifier.QualifierOperatorLike.equals(selector)){

			//return new BasicDBObject(selectorMap().objectForKey(selector),convertedValueToRegex());
			return convertedValueToRegex();
		}
		//incase-sensitive like = db.stuff.find( { foo: /^bar$/i } );
		return new BasicDBObject(selectorMap().objectForKey(selector),value);
	}
	
	private Object convertedValueToRegex(){

		if(EOQualifier.QualifierOperatorCaseInsensitiveLike.equals(selector)){
			return Pattern.compile((String)value, Pattern.CASE_INSENSITIVE);
		}
	
		return Pattern.compile((String)value);
	}
	
	public String key(){
		return key;
	}
	
	public NSDictionary<NSSelector, String> selectorMap(){
		NSMutableDictionary<NSSelector,String> map = new NSMutableDictionary<NSSelector, String>(); 
	map.setObjectForKey("$in", EOQualifier.QualifierOperatorContains); 
	//map.setObjectForKey(object, EOQualifier.QualifierOperatorEqual); 
	map.setObjectForKey("$regex", EOQualifier.QualifierOperatorLike);  
	map.setObjectForKey("$regex", EOQualifier.QualifierOperatorCaseInsensitiveLike); 
	map.setObjectForKey("$gt", EOQualifier.QualifierOperatorGreaterThan);  
	map.setObjectForKey("$gte", EOQualifier.QualifierOperatorGreaterThanOrEqualTo);  
	map.setObjectForKey("$lt", EOQualifier.QualifierOperatorLessThan);  
	map.setObjectForKey("$lte", EOQualifier.QualifierOperatorLessThanOrEqualTo);   
	map.setObjectForKey("$ne", EOQualifier.QualifierOperatorNotEqual);  
	return map.immutableClone();
	}
	
	protected BasicDBObject qualifierObject(){
		return new BasicDBObject(key(),evaluatingValuePart());
	}
	
}
