package com.dd.mongwo;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.bson.types.Code;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import com.mongodb.MongoClient;
import com.mongodb.QueryBuilder;
import com.mongodb.QueryOperators;
import com.sun.org.apache.xml.internal.security.Init;
import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOKeyComparisonQualifier;
import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EONotQualifier;
import com.webobjects.eocontrol.EOOrQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSMutableSet;
import com.webobjects.foundation.NSSelector;

import er.extensions.foundation.ERXArrayUtilities;

public class MWEditingContext {

	private DB database;
	private MongoClient mongoClient;
	private NSMutableSet<MWGenericRecord> insertedObjects;
	private NSMutableSet<MWGenericRecord> updatedObjects;
	private NSMutableSet<MWGenericRecord> deletedObjects;
	private MWModel model;
	
	public MWEditingContext(MongoClient client, String databaseName, MWModel model){
		mongoClient = client;
		this.database = client.getDB(databaseName);
		insertedObjects = new NSMutableSet<MWGenericRecord>();
		updatedObjects = new NSMutableSet<MWGenericRecord>();
		deletedObjects = new NSMutableSet<MWGenericRecord>();
		this.model = model;
		
	}
	
	public MWEditingContext(MongoClient client, String databaseName){
		mongoClient = client;
		this.database = client.getDB(databaseName);
		insertedObjects = new NSMutableSet<MWGenericRecord>();
		updatedObjects = new NSMutableSet<MWGenericRecord>();
		deletedObjects = new NSMutableSet<MWGenericRecord>();
		this.model = MWModel.defaultModel();
	}
	
	public NSArray objectsWithFetchSpecification(MWFetchSpecification spec){
		return spec.objectsForEditingContext(this);
	}
	
	public NSArray objectsWithFetchSpecification(EOFetchSpecification spec){
		//spec.qualifier().
		
		List<DBObject> result;
		DBCollection collection = database().getCollection(model.entityWithName(spec.entityName()).collection());
		DBObject qualifierObject = null;
		BasicDBList sortOrderingObject = null;
		if(spec.qualifier()!=null){
			qualifierObject = qualifierObjectForQualifier(spec.qualifier());
		}
		if(spec.sortOrderings()!=null&&!spec.sortOrderings().isEmpty()){
			sortOrderingObject = new BasicDBList();
			for(EOSortOrdering sort : spec.sortOrderings()){
				sortOrderingObject.add(sortOrderingObjectForSortOrdering(sort));
			}
			
		}
		
		
		if(qualifierObject==null){
			if(sortOrderingObject==null){
				result = collection.find().toArray();
			}
			else{
				result = collection.find().sort(sortOrderingObject).toArray();
			}
			
		}
		else{
			if(sortOrderingObject==null){
				result = collection.find(qualifierObject).toArray();
			}
			else{
				System.out.println(qualifierObject);
				result = collection.find(qualifierObject).sort(sortOrderingObject).toArray();
			}
		
		}
		MWGenericRecord[] objects = new MWGenericRecord[result.size()];
		int i = 0;
		try{
		for(DBObject dict : result){
			//Class.forName("Movie").getConstructor(DBObject.class, DBCollection.class).newInstance(dict, ec.database().getCollection(MWEntity.entityWithName(entityName).collection()));
			
			MWGenericRecord record = (MWGenericRecord)model.entityWithName(spec.entityName()).objectClass().newInstance();
			record.init(dict, database().getCollection(model.entityWithName(spec.entityName()).collection()), this);
			objects[i] = record;
			i++;
		}
		
		}
		
		catch(Exception e){
			e.printStackTrace();
		}
		return new NSArray(objects);
	}
	
	
	public void insertObject(MWGenericRecord object){
		insertedObjects.addObject(object);
		object.init(null, database().getCollection(model.entityForObjectClass(object.getClass()).collection()), this);
	}
	
	public void deleteObject(MWGenericRecord object){
		deletedObjects.addObject(object);
	}
	
	public void saveChanges(){
		for(MWGenericRecord record : insertedObjects){
			database().getCollection(model.entityForObjectClass(record.getClass()).collection()).insert(record);
		}
		insertedObjects = new NSMutableSet<MWGenericRecord>();
		for(MWGenericRecord record : updatedObjects){
			database().getCollection(model.entityForObjectClass(record.getClass()).collection()).save(record);
		}
		updatedObjects = new NSMutableSet<MWGenericRecord>();
		for(MWGenericRecord record : deletedObjects){
			database().getCollection(model.entityForObjectClass(record.getClass()).collection()).remove(record);
		}
		deletedObjects = new NSMutableSet<MWGenericRecord>();
	}

	
	public DB database(){
		return database;
	}
	
	public void objectWasUpdated(MWGenericRecord record){
		updatedObjects.addObject(record);
	}
	
	public static DBObject qualifierObjectForQualifier(EOQualifier qual){
		if(qual instanceof EOKeyValueQualifier){
			EOKeyValueQualifier kvq = (EOKeyValueQualifier)qual;
			return new BasicDBObject(kvq.key(),evaluatingValuePart(kvq));
		}
		if(qual instanceof EOOrQualifier){
			EOOrQualifier oq = (EOOrQualifier)qual;
			BasicDBList or = new BasicDBList();
			for(EOQualifier currentQual : oq.qualifiers()){
				or.add(qualifierObjectForQualifier(currentQual));
			}
			return new BasicDBObject(QueryOperators.OR, or);
		}
		if(qual instanceof EOAndQualifier){
			EOAndQualifier aq = (EOAndQualifier)qual;
			BasicDBList and = new BasicDBList();
			for(EOQualifier currentQual : aq.qualifiers()){
				and.add(qualifierObjectForQualifier(currentQual));
			}
			return new BasicDBObject(QueryOperators.AND, and);
		}
		if(qual instanceof EOKeyComparisonQualifier){
			EOKeyComparisonQualifier kcq = (EOKeyComparisonQualifier)qual;
			String selectorString = null;
			if(EOQualifier.QualifierOperatorEqual.equals(kcq.selector())){
				selectorString = "==";
			}
			if(EOQualifier.QualifierOperatorNotEqual.equals(kcq.selector())){
				selectorString = "!=";
			}
			if(EOQualifier.QualifierOperatorGreaterThan.equals(kcq.selector())){
				selectorString = ">";
			}
			if(EOQualifier.QualifierOperatorGreaterThanOrEqualTo.equals(kcq.selector())){
				selectorString = ">=";
			}
			if(EOQualifier.QualifierOperatorLessThan.equals(kcq.selector())){
				selectorString = "<";
			}
			if(EOQualifier.QualifierOperatorLessThanOrEqualTo.equals(kcq.selector())){
				selectorString = "<=";
			}
			if(selectorString==null){
				throw new IllegalArgumentException("Selector not supported: "+kcq.selector());
			}
			

			return new BasicDBObject(QueryOperators.WHERE,new Code("function(){return ("+kcq.rightKey()+" "+selectorString+" "+kcq.leftKey()+");}"));
		}
		if(qual instanceof EONotQualifier){
			EONotQualifier nq = (EONotQualifier)qual;
			return new BasicDBObject(QueryOperators.NOT, qualifierObjectForQualifier(nq));	
		}
		throw new IllegalArgumentException("Qualifier of type "+qual.getClass().getName()+" is not supported");	
	}
	
	public static DBObject sortOrderingObjectForSortOrdering(EOSortOrdering sort){
		return new BasicDBObject(sort.key(),sortSelectorMap().objectForKey(sort.selector()));	
	}
	
	private static NSDictionary<NSSelector,Integer> sortSelectorMap(){
		NSMutableDictionary<NSSelector,Integer> map = new NSMutableDictionary<NSSelector,Integer>();
		
		map.setObjectForKey(1, EOSortOrdering.CompareAscending); 
	    
		map.setObjectForKey(1, EOSortOrdering.CompareCaseInsensitiveAscending);
		           
		map.setObjectForKey(-1, EOSortOrdering.CompareCaseInsensitiveDescending); 
		           
		map.setObjectForKey(-1, EOSortOrdering.CompareDescending);
		return map.immutableClone();
	}
	
	private static Object evaluatingValuePart(EOKeyValueQualifier qual){
		if(qual.value() instanceof com.webobjects.foundation.NSKeyValueCoding.Null){
			return new BasicDBObject(QueryOperators.EXISTS,!EOQualifier.QualifierOperatorEqual.equals(qual.selector()));
		}
		if(EOQualifier.QualifierOperatorEqual.equals(qual.selector())){
			return qual.value();
		}
		
		if(EOQualifier.QualifierOperatorCaseInsensitiveLike.equals(qual.selector())||
				EOQualifier.QualifierOperatorLike.equals(qual.selector())){

			//return new BasicDBObject(selectorMap().objectForKey(selector),convertedValueToRegex());
			return convertedValueToRegex(qual);
		}
		//incase-sensitive like = db.stuff.find( { foo: /^bar$/i } );
		return new BasicDBObject(qualifierSelectorMap().objectForKey(qual.selector()),qual.value());
	}
	
	private static Object convertedValueToRegex(EOKeyValueQualifier qual){

		if(EOQualifier.QualifierOperatorCaseInsensitiveLike.equals(qual.selector())){
			return Pattern.compile((String)qual.value(), Pattern.CASE_INSENSITIVE);
		}
	
		return Pattern.compile((String)qual.value());
	}
	

	
	public static NSDictionary<NSSelector, String> qualifierSelectorMap(){
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

	public DBCollection collectionWithName(String name){
		return database.getCollection(name);
	}
	public MWGenericRecord recordForRef(DBRef ref){
		
		DBObject object = ref.fetch();
		MWEntity entity = MWModel.defaultModel().entityForDBObject(object);
		MWGenericRecord record = null;
		try{
		record = (MWGenericRecord)entity.objectClass().newInstance();
		record.init(object, collectionWithName(entity.collection()), this);
		return record;
		}
		catch(Exception e){
			
			throw new RuntimeException(e);
		}
		
	}

	public MWGenericRecord objectForRef(DBRef ref) {
		DBObject object = ref.fetch();
		MWGenericRecord record = MWModel.defaultModel().entityForDBObject(object).newObject();
		record.init(object, collectionWithName(MWModel.defaultModel().entityForDBObject(object).collection()), this);
		return record;
	}

	public NSArray<MWGenericRecord> objectsForRefKey(MWGenericRecord genericRecord,
			String keyPath) {
		DBCollection collection = collectionForKeyPath(keyPath);
		String key = StringUtils.substringAfterLast(keyPath, ".");
		NSMutableArray<MWGenericRecord> records = new NSMutableArray<MWGenericRecord>();
		BasicDBObject refQueryObject = new BasicDBObject("$id", genericRecord.primaryKey());//new Code("DBRef(\\\""+genericRecord.ref().getRef()+"\\\", ObjectId(\\\""+genericRecord.ref().getId()+"\\\"))"));
		System.out.println(QueryBuilder.start(key).elemMatch(refQueryObject).get());
		List<DBObject> list = collection.find(QueryBuilder.start(key).elemMatch(refQueryObject).get()).toArray();
		System.out.println("list.count: "+list.size());
		for(DBObject object : list){
			MWGenericRecord record = MWModel.defaultModel().entityForDBObject(object).newObject();
			record.init(object, collection, this);
			records.addObject(record);
		}
		return records.immutableClone();
	}
	

	public void takeObjectsForRefKey(MWGenericRecord genericRecord,
			String keyPath, NSArray<? extends MWGenericRecord> values) {
		NSArray<MWGenericRecord> currentObjects = objectsForRefKey(genericRecord, keyPath);
		String key = StringUtils.substringAfterLast(keyPath, ".");
		
		NSArray<MWGenericRecord> toDelete = ERXArrayUtilities.arrayMinusArray(currentObjects, values);
		for(MWGenericRecord record : toDelete){
			record.removeObjectFromPropertyWithKey(genericRecord, key);
			updatedObjects.addObject(record);
		}
		NSArray<? extends MWGenericRecord> toAdd = ERXArrayUtilities.arrayMinusArray(values, currentObjects);
		for(MWGenericRecord record : toAdd){
			record.addObjectToPropertyWithKey(genericRecord, key);
			updatedObjects.addObject(record);
		}
		updatedObjects.addObject(genericRecord);
		
	}
	
	protected DBCollection collectionForKeyPath(String keyPath){
		return collectionWithName(StringUtils.substringBefore(keyPath, "."));	
	}
	
	protected List<DBObject> dbObjectsReferencingRefKey(DBRef ref, DBCollection collection, String key){
		NSMutableArray<MWGenericRecord> records = new NSMutableArray<MWGenericRecord>();
		return collection.find(QueryBuilder.start(key).in(ref).get()).toArray();
	}

	public MWGenericRecord objectForDBObject(DBObject object) {
		MWGenericRecord record = MWModel.defaultModel().entityForDBObject(object).newObject();
		record.init(object, collectionWithName(MWModel.defaultModel().entityForDBObject(object).collection()), this);
		return record;
	}

	

	
	 
	
}
