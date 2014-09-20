package com.miw.remoid;

import java.util.List;

public class WSRequest {
	private int op;
	private int x;
	private int y;
	
	private int dimX;
	private int dimY;

	private List<PhoneDimension> phoneDimensions;
	
	public int getDimX() {
		return dimX;
	}
	public void setDimX(int dimX) {
		this.dimX = dimX;
	}
	public int getDimY() {
		return dimY;
	}
	public void setDimY(int dimY) {
		this.dimY = dimY;
	}
	public int getOp() {
		return op;
	}
	public void setOp(int op) {
		this.op = op;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public List<PhoneDimension> getPhoneDimensions() {
		return phoneDimensions;
	}
	public void setPhoneDimensions(List<PhoneDimension> phoneDimensions) {
		this.phoneDimensions = phoneDimensions;
	}
}
