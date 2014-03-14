package com.dd.testapp;

import com.dd.mongwo.mwaccess.MWEntity;
import com.dd.mongwo.mwaccess.MWModel;
import com.mongodb.MongoClient;

import er.extensions.appserver.ERXApplication;

public class Application extends ERXApplication {
	public static void main(String[] argv) {
		ERXApplication.main(argv, Application.class);
	}

	public Application() {
		ERXApplication.log.info("Welcome to " + name() + " !");
		/* ** put your initialization code in here ** */
		setAllowsConcurrentRequestHandling(true);	
		model = MWModel.defaultModel();
		model.addEntity(new MWEntity(Movie.ENTITY_NAME, "movie", Movie.class,model));
		model.addEntity(new MWEntity(Director.ENTITY_NAME, "director", Director.class,model));
		model.addEntity(new MWEntity(Country.ENTITY_NAME, "country", Country.class,model));
	}
	
	private MongoClient _mgClient;
	private MWModel model;
	
	public MongoClient mongoClient(){
		if(_mgClient==null){
			try{
			_mgClient = new MongoClient();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		return _mgClient;
	}
	

}
