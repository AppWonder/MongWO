package com.dd.testapp.components;

import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXComponent;

import com.dd.testapp.Application;
import com.dd.testapp.Session;

public class BaseComponent extends ERXComponent {
	public BaseComponent(WOContext context) {
		super(context);
	}
	
	@Override
	public Application application() {
		return (Application)super.application();
	}
	
	@Override
	public Session session() {
		return (Session)super.session();
	}
}
