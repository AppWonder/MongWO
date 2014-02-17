package com.dd.testapp.components;


import com.dd.mongwo.MWEditingContext;
import com.dd.testapp.Application;
import com.dd.testapp.Director;
import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.foundation.NSArray;

import er.extensions.components.ERXComponent;

public class DirectorEditor extends ERXComponent {
    private Director currentDirector;
	private Director selectedDirector;
	private String newDirectorName;
	private MWEditingContext editingContext;

	public DirectorEditor(WOContext context) {
        super(context);
        editingContext = new MWEditingContext(((Application)application()).mongoClient(), "test");
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

	/**
	 * @return the selectedDirector
	 */
	public Director selectedDirector() {
		return selectedDirector;
	}

	/**
	 * @param selectedDirector the selectedDirector to set
	 */
	public void setSelectedDirector(Director selectedDirector) {
		this.selectedDirector = selectedDirector;
	}

	public NSArray<Director> allDirector() {
		return (NSArray<Director>)editingContext.objectsWithFetchSpecification(new EOFetchSpecification(Director.ENTITY_NAME, null, Director.NAME.ascs()));
	}

	public WOActionResults saveChanges() {
		editingContext.saveChanges();
		return null;
	}

	/**
	 * @return the newDirectorName
	 */
	public String newDirectorName() {
		return newDirectorName;
	}

	/**
	 * @param newDirectorName the newDirectorName to set
	 */
	public void setNewDirectorName(String newDirectorName) {
		this.newDirectorName = newDirectorName;
	}

	public WOActionResults createDirector() {
		Director director = new Director();
		editingContext.insertObject(director);
		director.setName(newDirectorName);
		editingContext.saveChanges();
		return null;
	}
}