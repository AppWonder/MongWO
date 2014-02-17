package com.dd.mongwo;



import java.util.List;



import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.webobjects.foundation.NSArray;

public class MWFetchSpecification {
	private String entityName;
	private MWQualifier qualifier;
	private MWSortOrder sortOrder;
	

	
	public MWFetchSpecification(String entityName, MWQualifier qualifier,
			MWSortOrder sortOrder) {
		super();
		this.entityName = entityName;
		this.qualifier = qualifier;
		this.sortOrder = sortOrder;
	}

	public NSArray objectsForEditingContext(MWEditingContext ec){
		List<DBObject> result;
		DBCollection collection = ec.database().getCollection(MWModel.defaultModel().entityWithName(entityName).collection());
		if(qualifier==null){
			if(sortOrder==null){
				result = collection.find().toArray();
			}
			else{
				result = collection.find().sort(sortOrder.sortObject()).toArray();
			}
			
		}
		else{
			if(sortOrder==null){
				result = collection.find(qualifier.qualifierObject()).toArray();
			}
			else{
				System.out.println(qualifier.qualifierObject());
				result = collection.find(qualifier.qualifierObject()).sort(sortOrder.sortObject()).toArray();
			}
		
		}
		MWGenericRecord[] objects = new MWGenericRecord[result.size()];
		int i = 0;
		try{
		for(DBObject dict : result){
			//Class.forName("Movie").getConstructor(DBObject.class, DBCollection.class).newInstance(dict, ec.database().getCollection(MWEntity.entityWithName(entityName).collection()));
			
			MWGenericRecord record = (MWGenericRecord)Class.forName("com.dd.testapp.Movie").newInstance();
			record.init(dict, ec.database().getCollection(MWModel.defaultModel().entityWithName(entityName).collection()), ec);
			objects[i] = record;
			i++;
		}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return new NSArray(objects);
	}
	
	private DBObject ref(){
		return qualifier.qualifierObject();
	}
}
