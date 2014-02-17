package com.dd.testapp.components;

import com.dd.mongwo.MWEditingContext;
import com.dd.testapp.Application;
import com.dd.testapp.Country;
import com.dd.testapp.Movie;
import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.foundation.NSArray;

import er.extensions.components.ERXComponent;
import er.extensions.foundation.ERXTimestampUtilities;



public class CountryEditor extends ERXComponent {
    private Country currentCountry;
	private Country selectedCountry;
	private String newCountryName;
	private MWEditingContext editingContext;
	private Movie currentMovie;
	
    public CountryEditor(WOContext context) {
    	super(context);
        editingContext = new MWEditingContext(((Application)application()).mongoClient(), "test");
      }

  	/**
  	 * @return the currentDirector
  	 */
  	public Country currentCountry() {
  		return currentCountry;
  	}

  	/**
  	 * @param currentDirector the currentDirector to set
  	 */
  	public void setCurrentCountry(Country currentCountry) {
  		this.currentCountry = currentCountry;
  	}

  	/**
  	 * @return the selectedDirector
  	 */
  	public Country selectedCountry() {
  		return selectedCountry;
  	}

  	/**
  	 * @param selectedDirector the selectedDirector to set
  	 */
  	public void setSelectedCountry(Country selectedCountry) {
  		this.selectedCountry = selectedCountry;
  	}

  	public NSArray<Country> allCountry() {
  		return (NSArray<Country>)editingContext.objectsWithFetchSpecification(new EOFetchSpecification(Country.ENTITY_NAME, null, Country.NAME.ascs()));
  	}

  	public WOActionResults saveChanges() {
  		editingContext.saveChanges();
  		return null;
  	}

  	/**
  	 * @return the newDirectorName
  	 */
  	public String newCountryName() {
  		return newCountryName;
  	}

  	/**
  	 * @param newDirectorName the newDirectorName to set
  	 */
  	public void setNewCountryName(String newCountryName) {
  		this.newCountryName = newCountryName;
  	}

  	public WOActionResults createCountry() {
  		Country country = new Country();
  		editingContext.insertObject(country);
  		country.setName(newCountryName);
  		editingContext.saveChanges();
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

	@SuppressWarnings("unchecked")
	public NSArray<Movie> allMovies(){
		return (NSArray<Movie>)editingContext.objectsWithFetchSpecification(new EOFetchSpecification("movie", Movie.NAME.like("Star*").and(Movie.REVENUE.gt(100),Movie.RELEASE_DATE.after(ERXTimestampUtilities.distantPast())).or(Movie.NAME.like(".").and(Movie.RELEASE_DATE.before(ERXTimestampUtilities.today()).or(Movie.RELEASE_DATE.isNull()))), Movie.NAME.ascs().then(Movie.RELEASE_DATE.ascs())));
	}
}