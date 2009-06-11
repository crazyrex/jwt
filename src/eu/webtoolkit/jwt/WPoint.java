package eu.webtoolkit.jwt;

import java.util.*;
import java.util.regex.*;
import java.io.*;
import java.util.concurrent.locks.ReentrantLock;
import javax.servlet.http.*;
import eu.webtoolkit.jwt.*;
import eu.webtoolkit.jwt.chart.*;
import eu.webtoolkit.jwt.utils.*;
import eu.webtoolkit.jwt.servlet.*;

/**
 * Utility class that defines a 2D point with integer coordinates
 * 
 * @see WPolygonArea
 */
public class WPoint {
	/**
	 * Default constructor.
	 * 
	 * Constructs a point (<i>x=0</i>, <i>y=0</i>).
	 */
	public WPoint() {
		this.x_ = 0;
		this.y_ = 0;
	}

	/**
	 * Construct a point.
	 * 
	 * Constructs a point (<i>x</i>, <i>y</i>).
	 */
	public WPoint(int x, int y) {
		this.x_ = x;
		this.y_ = y;
	}

	/**
	 * Changes the X coordinate.
	 */
	public void setX(int x) {
		this.x_ = x;
	}

	/**
	 * Changes the Y coordinate.
	 */
	public void setY(int y) {
		this.y_ = y;
	}

	/**
	 * Returns the X coordinate.
	 */
	public int getX() {
		return this.x_;
	}

	/**
	 * Returns the Y coordinate.
	 */
	public int getY() {
		return this.y_;
	}

	/**
	 * Comparison operator.
	 */
	public boolean equals(WPoint other) {
		return this.x_ == other.x_ && this.y_ == other.y_;
	}

	public WPoint add(WPoint other) {
		this.x_ += other.x_;
		this.y_ += other.y_;
		return this;
	}

	private int x_;
	private int y_;
}
