package com.miw.remoid;

public class PhoneDimension {
	public int x;
	public int y;
	public double diagonal;

	public double getDiagonal() {
		return diagonal;
	}

	public void setDiagonal(double diagonal) {
		this.diagonal = diagonal;
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

	public static PhoneDimension instantiate(int x, int y, double diagonal) {
		PhoneDimension d = new PhoneDimension();
		d.x = x;
		d.y = y;
		d.diagonal = diagonal;
		
		return d;
	}
}
