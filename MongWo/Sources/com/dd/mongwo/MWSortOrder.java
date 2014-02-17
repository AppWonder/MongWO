package com.dd.mongwo;

import com.mongodb.BasicDBObject;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSSelector;

public class MWSortOrder {
private String key;
private NSSelector selector;

public MWSortOrder(String key, NSSelector selector) {
	super();
	this.key = key;
	this.selector = selector;
}

protected BasicDBObject sortObject(){
	return new BasicDBObject(key,selectorMap().objectForKey(selector));
}

private NSDictionary<NSSelector,Integer> selectorMap(){
	NSMutableDictionary<NSSelector,Integer> map = new NSMutableDictionary<NSSelector,Integer>();
	
	map.setObjectForKey(1, EOSortOrdering.CompareAscending); 
    
	map.setObjectForKey(1, EOSortOrdering.CompareCaseInsensitiveAscending);
	           
	map.setObjectForKey(-1, EOSortOrdering.CompareCaseInsensitiveDescending); 
	           
	map.setObjectForKey(-1, EOSortOrdering.CompareDescending);
	return map.immutableClone();
}
	
}
