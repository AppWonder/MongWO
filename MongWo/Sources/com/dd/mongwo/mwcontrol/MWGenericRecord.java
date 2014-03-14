package com.dd.mongwo.mwcontrol;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.BSONObject;
import org.bson.types.ObjectId;

import com.dd.mongwo.mwaccess.MWEntity;
import com.dd.mongwo.mwaccess.MWModel;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;

public class MWGenericRecord implements DBObject{
	private DBObject _values;
	private DBCollection collection;
	private String collectionName;
	private MWEditingContext editingContext;
	private Object _primaryKey;
	private static String ID_KEY = "_id";
	
	protected MWGenericRecord(DBObject object, DBCollection collection){
		_values = object;
		this.collection = collection;
		_primaryKey = object.get(ID_KEY);
	}
	
	public MWGenericRecord(String entityName){
		_values = new BasicDBObject();
		_values.put(MWEntity.ENTITY_KEY, entityName);
		_primaryKey = new ObjectId();
	}
	
	public Object storedValueForKey(String key){
		Object value = values().get(key);
		if(value instanceof Date){
			return new NSTimestamp((Date)value);
		}
		if(value instanceof DBRef){
			return editingContext.recordForRef((DBRef)value);
		}
		
		if(value instanceof List && !((List)value).isEmpty() && ((List)value).get(0) instanceof DBRef){
			NSMutableArray<MWGenericRecord> records = new NSMutableArray<MWGenericRecord>();
			for(Object object : ((List)value)){
				MWGenericRecord record = editingContext.objectForRef((DBRef)object);
				records.addObject(record);
			}
			return records;	
		}
		if(value instanceof BasicDBObject){
			return editingContext.objectForDBObject((DBObject)value);
		}
		return value;
	}
	
	public NSArray<? extends MWGenericRecord> storedValuesForKey(String key) {
		Object value = storedValueForKey(key);
		if(value==null){
			return NSArray.emptyArray();
		}
		if(value instanceof NSArray){
			return (NSArray<MWGenericRecord>)value;
		}
		return new NSArray<MWGenericRecord>((MWGenericRecord)value);
	}
	
	public NSArray<? extends MWGenericRecord> objectsForRefKey(String keyPath){
		return editingContext.objectsForRefKey(this,keyPath);

	}
	
	public void takeObjectsForRefKey(NSArray<? extends MWGenericRecord> values, String keyPath){
		editingContext.takeObjectsForRefKey(this, keyPath, values);
	}
	
	public void takeStoredValueForKey(Object value, String key){
		if(value instanceof MWGenericRecord){
			values().put(key, new DBRef(editingContext.database(), ((MWGenericRecord)value).collectionName(),  ((MWGenericRecord)value).primaryKey()));
		}
		else{
			if(value instanceof List){
				List valueList = (List)value;
				if(!valueList.isEmpty()&&valueList.get(0) instanceof MWGenericRecord){
					BasicDBList refList = new BasicDBList();
					for(Object object : valueList){
						MWGenericRecord currentMWRecord = (MWGenericRecord)object;
						refList.add(new DBRef(editingContext.database(), currentMWRecord.collectionName() , currentMWRecord.primaryKey()));
					}
					value = refList;
				}
			}
		 values().put(key,value);
		}
		 editingContext.objectWasUpdated(this);
	}
	
	public void addObjectToPropertyWithKey(MWGenericRecord record, String key){
		Object storedRef = values().get(key);
		BasicDBList list = null;
		if(storedRef==null||storedRef instanceof DBRef||storedRef instanceof BasicDBObject){
			list = new BasicDBList();
			if(storedRef!=null){
				if(storedRef instanceof DBRef){
					list.add(storedRef);		
				}
				
			}
		}
		if(storedRef instanceof BasicDBList){
			list = (BasicDBList)storedRef;
		}
		if(list==null){
			throw new IllegalArgumentException("Cannot do anything with object of type: "+storedRef.getClass().getName());
		}
		if(!list.isEmpty()){
			for(Object ref : list){
				if(((DBRef)ref).getId().equals(record.primaryKey())){
					System.out.println("Object already in reference array.");
					return;
				}
			}
		}
		list.add(new DBRef(editingContext.database(), record.collectionName(), record.primaryKey()));
		values().put(key, list);
		editingContext.objectWasUpdated(this);
	}
	
	public void removeObjectFromPropertyWithKey(MWGenericRecord record, String key){
		Object storedRef = values().get(key);
		BasicDBList list = null;
		if(storedRef==null||storedRef instanceof DBRef){
			list = new BasicDBList();
			if(storedRef!=null){
				list.add(storedRef);
			}
		}
		if(storedRef instanceof BasicDBList){
			list = (BasicDBList)storedRef;
		}
		if(list==null){
			throw new IllegalArgumentException("Cannot do anything with object of type: "+storedRef.getClass().getName());
		}

		while(list.contains(record.values())){
			list.remove(record.values());
		}

		if(list.isEmpty()){
			values().removeField(key);
		}
		else{
			values().put(key, list);
		}
		editingContext.objectWasUpdated(this);
	}
	
	private DBObject values(){
		return _values;
	}
	
	public Object primaryKey(){
		return _primaryKey;
	}

	public boolean containsField(String arg0) {
		return _values.containsField(arg0);
	}

	public boolean containsKey(String arg0) {
		return _values.containsKey(arg0);
	}

	public Object get(String arg0) {
		return _values.get(arg0);
	}

	public Set<String> keySet() {
		return _values.keySet();
	}

	public Object put(String arg0, Object arg1) {
		return _values.put(arg0, arg1);
	}

	public void putAll(BSONObject arg0) {
		_values.putAll(arg0);
	}

	public void putAll(Map arg0) {
		_values.putAll(arg0);
		
	}

	public Object removeField(String arg0) {
		return _values.removeField(arg0);
	}

	public Map toMap() {
		return _values.toMap();
	}

	public boolean isPartialObject() {
		return _values.isPartialObject();
	}

	public void markAsPartialObject() {
		_values.markAsPartialObject();
	}
	
	public DBCollection collection(){
		return collection;
	}
	
	protected void init(DBObject object, DBCollection collection, MWEditingContext editingContext){
		if(object!=null){
			_values = object;
			_primaryKey = object.get("_id");
		}
		this.collection = collection;
		this.editingContext = editingContext;
	}
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof MWGenericRecord){
			if(primaryKey()!=null){
				return primaryKey().equals(((MWGenericRecord)object).primaryKey());
			}
		}
		return super.equals(object);
	}
	
	protected String collectionName(){
		return MWModel.defaultModel().entityForObjectClass(getClass()).collection();
	}
	
	public DBRef ref(){
		return new DBRef(editingContext.database(), MWModel.defaultModel().entityForDBObject(values()).collection(), primaryKey());
	}
	
	public void validateForDelete() {
		//TODO
	}
	
	public void validateForInsert() {
		//TODO		
	}
	
	public void validateForSave() {
		//TODO
	}
	
	
	public void validateForUpdate() {
		//TODO
	}
	
	
	public void  validateTakeValueForKeyPath(Object value, String keyPath) {
		//TODO
	}
	
	public void validateValueForKey(Object value, String key) {
		if(ID_KEY.equals(key)){
			throw new IllegalArgumentException(ID_KEY+" is a primaryKey and cannot be changed");
		}
		if(MWEntity.ENTITY_KEY.equals(key)){
			throw new IllegalArgumentException(MWEntity.ENTITY_KEY+" cannot be changed");
		}
		//TODO
	}
	
	public void willChange() {
		//TODO
	}
	
	public void willRead() {
		//TODO
	}
	
}

