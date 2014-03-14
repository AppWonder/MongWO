package com.dd.mongwo.mwaccess;

import com.dd.mongwo.mwcontrol.MWGenericRecord;
import com.mongodb.DBObject;
import com.webobjects.foundation.NSMutableDictionary;

public class MWModel {

	private NSMutableDictionary<String, MWEntity> _entities;
	private NSMutableDictionary<Class<? extends MWGenericRecord>, MWEntity> _entitiesByObjectClass;
	
	public MWModel(){
		super();
		_entities = new NSMutableDictionary<String, MWEntity>();
		_entitiesByObjectClass = new NSMutableDictionary<Class<? extends MWGenericRecord>, MWEntity>();
	}
	
	public void addEntity(MWEntity entity){
		_entities.setObjectForKey(entity, entity.entityName());
		_entitiesByObjectClass.setObjectForKey(entity, entity.objectClass());
	}
	
	public MWEntity entityWithName(String name){
		return _entities.objectForKey(name);
	}
	
	public MWEntity entityForObjectClass(Class<? extends MWGenericRecord> objectClass){
		return _entitiesByObjectClass.objectForKey(objectClass);
	}
	
	private static MWModel _defaultModel;
	public static MWModel defaultModel(){
		if(_defaultModel==null){
			_defaultModel = new MWModel();
		}
		return _defaultModel;
	}

	public MWEntity entityForDBObject(DBObject object) {
		return entityWithName((String)object.get(MWEntity.ENTITY_KEY));
	}
	
	
}
