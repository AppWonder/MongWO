package com.dd.testapp.components;

import com.dd.mongwo.mwcontrol.MWEditingContext;
import com.dd.testapp.Application;
import com.dd.testapp.Country;
import com.dd.testapp.Director;
import com.dd.testapp.Movie;
import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.foundation.NSArray;

import er.extensions.foundation.ERXTimestampUtilities;

public class Main extends BaseComponent {
    private Director currentDirector;
	private String newName;
	private String newStory;
	private MWEditingContext editingContext;
	private Movie currentMovie;
	private Movie selectedMovie;
	private Country currentCountry;

	public Main(WOContext context) {
		super(context);
		try{
		
		editingContext = new MWEditingContext(((Application)application()).mongoClient(), "test");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * @return the newName
	 */
	public String newName() {
		return newName;
	}

	/**
	 * @param newName the newName to set
	 */
	public void setNewName(String newName) {
		this.newName = newName;
	}

	/**
	 * @return the newStory
	 */
	public String newStory() {
		return newStory;
	}

	/**
	 * @param newStory the newStory to set
	 */
	public void setNewStory(String newStory) {
		this.newStory = newStory;
	}
	
	@SuppressWarnings("unchecked")
	public NSArray<Movie> allMovies(){
		return (NSArray<Movie>)editingContext.objectsWithFetchSpecification(new EOFetchSpecification("movie", Movie.NAME.like("Star*").and(Movie.REVENUE.gt(100),Movie.RELEASE_DATE.after(ERXTimestampUtilities.distantPast())).or(Movie.NAME.like(".").and(Movie.RELEASE_DATE.before(ERXTimestampUtilities.today()).or(Movie.RELEASE_DATE.isNull()))), Movie.NAME.ascs().then(Movie.RELEASE_DATE.ascs())));
	}
	
	public NSArray<Director> allDirector() {
		return (NSArray<Director>)editingContext.objectsWithFetchSpecification(new EOFetchSpecification(Director.ENTITY_NAME, null, Director.NAME.ascs()));
	}
	
  	public NSArray<Country> allCountry() {
  		return (NSArray<Country>)editingContext.objectsWithFetchSpecification(new EOFetchSpecification(Country.ENTITY_NAME, null, Country.NAME.ascs()));
  	}

	public WOActionResults createMovie() {
		Movie movie = new Movie();
		editingContext.insertObject(movie);
		movie.setName(newName);
		movie.setStory(newStory);

		editingContext.saveChanges();
		newName = null;
		newStory = null;
		return null;
	}

	/**
	 * @return the currentMovie
	 */
	public Movie currentMovie() {
		return currentMovie;
	}

	/**
	 * @param currentMovie the currentMovie to set
	 */
	public void setCurrentMovie(Movie currentMovie) {
		this.currentMovie = currentMovie;
	}

	/**
	 * @return the selectedMovie
	 */
	public Movie selectedMovie() {
		return selectedMovie;
	}

	/**
	 * @param selectedMovie the selectedMovie to set
	 */
	public void setSelectedMovie(Movie selectedMovie) {
		this.selectedMovie = selectedMovie;
	}
	
	/**
	 * @return the currentDirector
	 */
	public Director currentDirector() {
		return currentDirector;
	}

	/**
	 * @param currentDirector the currentDirector to set
	 */
	public void setCurrentDirector(Director currentDirector) {
		this.currentDirector = currentDirector;
	}

	public WOActionResults saveChanges() {
		editingContext.saveChanges();
		return null;
	}

	public WOActionResults deleteMovie() {
		editingContext.deleteObject(selectedMovie);
		selectedMovie = null;
		editingContext.saveChanges();
		return null;
	}

	/**
	 * @return the currentCountry
	 */
	public Country currentCountry() {
		return currentCountry;
	}

	/**
	 * @param currentCountry the currentCountry to set
	 */
	public void setCurrentCountry(Country currentCountry) {
		this.currentCountry = currentCountry;
	}
}
