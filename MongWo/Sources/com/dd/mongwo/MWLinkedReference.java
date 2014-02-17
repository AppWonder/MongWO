package com.dd.mongwo;

public class MWLinkedReference {
	private String leftKey;
	private String rightKey;
	private MWEntity leftEntity;
	private MWEntity rightEntity;
	
	
	public MWLinkedReference(String leftKey, String rightKey, MWEntity leftEntity,
			MWEntity rightEntity) {
		super();
		this.leftKey = leftKey;
		this.rightKey = rightKey;
		this.leftEntity = leftEntity;
		this.rightEntity = rightEntity;
	}
	
	
	public String leftKey() {
		return leftKey;
	}
	
	public void setLeftKey(String leftKey) {
		this.leftKey = leftKey;
	}
	
	public String rightKey() {
		return rightKey;
	}
	
	public void setRightKey(String rightKey) {
		this.rightKey = rightKey;
	}
	
	public MWEntity leftEntity() {
		return leftEntity;
	}
	
	public void setLeftEntity(MWEntity leftEntity) {
		this.leftEntity = leftEntity;
	}
	
	public MWEntity rightEntity() {
		return rightEntity;
	}
	
	public void setRightEntity(MWEntity rightEntity) {
		this.rightEntity = rightEntity;
	}
}
