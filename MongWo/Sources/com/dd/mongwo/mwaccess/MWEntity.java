package com.dd.mongwo.mwaccess;

import com.dd.mongwo.mwcontrol.MWGenericRecord;
import com.webobjects.foundation.NSMutableDictionary;

public class MWEntity {
	public static final String ENTITY_KEY = "_entity";
	private String entityName;
	private String collection;
	private MWModel model;
	private Class<? extends MWGenericRecord> objectClass;
	private NSMutableDictionary<String, MWLinkedReference> _references;
	public MWEntity(String entityName, String collection, Class<? extends MWGenericRecord> aClass, MWModel model) {
		super();
		this.setEntityName(entityName);
		this.setCollection(collection);
		this.model = model;
		objectClass = aClass;
	}
	
	public String entityName() {
		return entityName;
	}
	
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	
	public String collection() {
		return collection;
	}
	public void setCollection(String collection) {
		this.collection = collection;
	}

	public String objectClassNameForEntity() {
		return objectClass.getName();
	}
	
	public MWModel model(){
		return model;
	}
	
	public Class<? extends MWGenericRecord> objectClass(){
		return objectClass;
	}
	
	public MWGenericRecord newObject(){
		try{
			return objectClass.newInstance();
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	

}
