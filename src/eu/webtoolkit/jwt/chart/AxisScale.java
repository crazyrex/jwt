/*
 * Copyright (C) 2009 Emweb bvba, Leuven, Belgium.
 *
 * See the LICENSE file for terms of use.
 */
package eu.webtoolkit.jwt.chart;

import java.util.*;
import java.util.regex.*;
import java.io.*;
import java.lang.ref.*;
import java.util.concurrent.locks.ReentrantLock;
import javax.servlet.http.*;
import javax.servlet.*;
import eu.webtoolkit.jwt.*;
import eu.webtoolkit.jwt.chart.*;
import eu.webtoolkit.jwt.utils.*;
import eu.webtoolkit.jwt.servlet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enumeration that indicates a scale for an axis.
 * <p>
 * The scale determines how values are mapped onto an axis.
 * <p>
 * 
 * @see WAxis#setScale(AxisScale scale)
 */
public enum AxisScale {
	/**
	 * <p>
	 * A category scale is set as the scale for the X axis in a
	 * {@link ChartType#CategoryChart CategoryChart}, and is only applicable
	 * there. It lists all values, evenly spaced, and consecutively in the order
	 * of the model. The categories are converted to numbers using their ordinal
	 * (first category = 0, second = 1, ...).
	 */
	CategoryScale(0),
	/**
	 * <p>
	 * A linear scale is the default scale for all axes, except for the X scale
	 * in a CategoryScale. It maps values in a linear fashion on the axis.
	 */
	LinearScale(1),
	/**
	 * <p>
	 * A logarithmic scale is useful for plotting values with of a large range,
	 * but only works for positive values.
	 */
	LogScale(2),
	/**
	 * <p>
	 * A date scale is a special linear scale, which is useful for the X axis in
	 * a ScatterPlot, when the X series contain dates (of type
	 * {@link eu.webtoolkit.jwt.WDate}). The dates are converted to numbers, as
	 * Julian Days.
	 */
	DateScale(3),
	/**
	 * <p>
	 * A datetime scale is a special linear scale, which is useful for the X
	 * axis in a ScatterPlot, when the X series contain timedates (of type
	 * {@link eu.webtoolkit.jwt.WDate}). The dates are converted to numbers, as
	 * the number of seconds since the Unix Epoch (midnight Coordinated
	 * Universal Time (UTC) of January 1, 1970).
	 */
	DateTimeScale(4);

	private int value;

	AxisScale(int value) {
		this.value = value;
	}

	/**
	 * Returns the numerical representation of this enum.
	 */
	public int getValue() {
		return value;
	}
}
