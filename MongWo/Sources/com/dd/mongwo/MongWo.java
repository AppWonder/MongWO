package com.dd.mongwo;

import com.mongodb.util.JSONSerializers;

import er.extensions.ERXFrameworkPrincipal;

public class MongWo extends ERXFrameworkPrincipal {

	public MongWo() {
		super();
	}

	@Override
	public void finishInitialization() {
		// TODO Auto-generated method stub
		JSONSerializers.getStrict();

	}

}
