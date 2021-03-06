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
 * A widget for selecting an X axis range to display on an associated
 * {@link eu.webtoolkit.jwt.chart.WCartesianChart}.
 * <p>
 * 
 * <p>
 * <i><b>Note: </b>This widget currently only works with the HtmlCanvas
 * rendering method. </i>
 * </p>
 */
public class WAxisSliderWidget extends WPaintedWidget {
	private static Logger logger = LoggerFactory
			.getLogger(WAxisSliderWidget.class);

	/**
	 * Creates an axis slider widget.
	 * <p>
	 * Creates an axis slider widget that is not associated with a chart. Before
	 * it is used, a chart should be assigned with setChart(), and a series
	 * column chosen with setSeriesColumn().
	 */
	public WAxisSliderWidget(WContainerWidget parent) {
		super(parent);
		this.series_ = null;
		this.seriesPen_ = new WPen();
		this.selectedSeriesPen_ = this.seriesPen_;
		this.handleBrush_ = new WBrush(new WColor(0, 0, 200));
		this.background_ = new WBrush(new WColor(230, 230, 230));
		this.selectedAreaBrush_ = new WBrush(new WColor(255, 255, 255));
		this.autoPadding_ = false;
		this.labelsEnabled_ = true;
		this.yAxisZoomEnabled_ = true;
		this.transform_ = null;
		this.init();
	}

	/**
	 * Creates an axis slider widget.
	 * <p>
	 * Calls {@link #WAxisSliderWidget(WContainerWidget parent)
	 * this((WContainerWidget)null)}
	 */
	public WAxisSliderWidget() {
		this((WContainerWidget) null);
	}

	/**
	 * Creates an axis slider widget.
	 * <p>
	 * Creates an axis slider widget associated with the given data series of
	 * the given chart.
	 */
	public WAxisSliderWidget(WDataSeries series, WContainerWidget parent) {
		super(parent);
		this.series_ = series;
		this.seriesPen_ = new WPen();
		this.selectedSeriesPen_ = this.seriesPen_;
		this.handleBrush_ = new WBrush(new WColor(0, 0, 200));
		this.background_ = new WBrush(new WColor(230, 230, 230));
		this.selectedAreaBrush_ = new WBrush(new WColor(255, 255, 255));
		this.autoPadding_ = false;
		this.labelsEnabled_ = true;
		this.yAxisZoomEnabled_ = true;
		this.transform_ = null;
		this.init();
	}

	/**
	 * Creates an axis slider widget.
	 * <p>
	 * Calls
	 * {@link #WAxisSliderWidget(WDataSeries series, WContainerWidget parent)
	 * this(series, (WContainerWidget)null)}
	 */
	public WAxisSliderWidget(WDataSeries series) {
		this(series, (WContainerWidget) null);
	}

	/**
	 * Destructor.
	 */
	public void remove() {
		if (this.getChart() != null) {
			this.getChart().removeAxisSliderWidget(this);
		}
		if (this.selectedSeriesPen_ != this.seriesPen_) {
			;
		}
		super.remove();
	}

	public void setSeries(WDataSeries series) {
		if (this.series_ != series) {
			if (this.series_ != null) {
				this.getChart().removeAxisSliderWidget(this);
			}
			this.series_ = series;
			if (this.series_ != null) {
				this.getChart().addAxisSliderWidget(this);
			}
			this.update();
		}
	}

	/**
	 * Set the pen to draw the data series with.
	 */
	public void setSeriesPen(final WPen pen) {
		if (!pen.equals(this.seriesPen_)) {
			this.seriesPen_ = pen;
			this.update();
		}
	}

	/**
	 * Returns the pen to draw the data series with.
	 */
	public WPen getSeriesPen() {
		return this.seriesPen_;
	}

	/**
	 * Set the pen to draw the selected part of the data series with.
	 * <p>
	 * If not set, this defaults to {@link WAxisSliderWidget#getSeriesPen()
	 * getSeriesPen()}.
	 */
	public void setSelectedSeriesPen(final WPen pen) {
		if (this.selectedSeriesPen_ != this.seriesPen_) {
			;
		}
		this.selectedSeriesPen_ = pen.clone();
		this.update();
	}

	/**
	 * Returns the pen to draw the selected part of the data series with.
	 */
	public WPen getSelectedSeriesPen() {
		return this.selectedSeriesPen_;
	}

	/**
	 * Set the brush to draw the handles left and right of the selected area
	 * with.
	 */
	public void setHandleBrush(final WBrush brush) {
		if (!brush.equals(this.handleBrush_)) {
			this.handleBrush_ = brush;
			this.update();
		}
	}

	/**
	 * Returns the brush to draw the handles left and right of the selected area
	 * with.
	 */
	public WBrush getHandleBrush() {
		return this.handleBrush_;
	}

	/**
	 * Set the background brush.
	 */
	public void setBackground(final WBrush brush) {
		if (!brush.equals(this.background_)) {
			this.background_ = brush;
			this.update();
		}
	}

	/**
	 * Returns the background brush.
	 */
	public WBrush getBackground() {
		return this.background_;
	}

	/**
	 * Set the brush for the selected area.
	 */
	public void setSelectedAreaBrush(final WBrush brush) {
		if (!brush.equals(this.selectedAreaBrush_)) {
			this.selectedAreaBrush_ = brush;
			this.update();
		}
	}

	/**
	 * Returns the brush for the selected area.
	 */
	public WBrush getSelectedAreaBrush() {
		return this.selectedAreaBrush_;
	}

	/**
	 * Sets an internal margin for the selection area.
	 * <p>
	 * This configures the area (in pixels) around the selection area that is
	 * available for the axes and labels, and the handles.
	 * <p>
	 * Alternatively, you can configure the chart layout to be computed
	 * automatically using
	 * {@link WAxisSliderWidget#setAutoLayoutEnabled(boolean enabled)
	 * setAutoLayoutEnabled()}.
	 * <p>
	 * 
	 * @see WAxisSliderWidget#setAutoLayoutEnabled(boolean enabled)
	 */
	public void setSelectionAreaPadding(int padding, EnumSet<Side> sides) {
		if (!EnumUtils.mask(sides, Side.Top).isEmpty()) {
			this.padding_[0] = padding;
		}
		if (!EnumUtils.mask(sides, Side.Right).isEmpty()) {
			this.padding_[1] = padding;
		}
		if (!EnumUtils.mask(sides, Side.Bottom).isEmpty()) {
			this.padding_[2] = padding;
		}
		if (!EnumUtils.mask(sides, Side.Left).isEmpty()) {
			this.padding_[3] = padding;
		}
	}

	/**
	 * Sets an internal margin for the selection area.
	 * <p>
	 * Calls {@link #setSelectionAreaPadding(int padding, EnumSet sides)
	 * setSelectionAreaPadding(padding, EnumSet.of(side, sides))}
	 */
	public final void setSelectionAreaPadding(int padding, Side side,
			Side... sides) {
		setSelectionAreaPadding(padding, EnumSet.of(side, sides));
	}

	/**
	 * Sets an internal margin for the selection area.
	 * <p>
	 * Calls {@link #setSelectionAreaPadding(int padding, EnumSet sides)
	 * setSelectionAreaPadding(padding, Side.All)}
	 */
	public final void setSelectionAreaPadding(int padding) {
		setSelectionAreaPadding(padding, Side.All);
	}

	/**
	 * Returns the internal margin for the selection area.
	 * <p>
	 * This is either the padding set through
	 * {@link WAxisSliderWidget#setSelectionAreaPadding(int padding, EnumSet sides)
	 * setSelectionAreaPadding()} or computed using
	 * {@link WAxisSliderWidget#setAutoLayoutEnabled(boolean enabled)
	 * setAutoLayoutEnabled()}.
	 * <p>
	 */
	public int getSelectionAreaPadding(Side side) {
		switch (side) {
		case Top:
			return this.padding_[0];
		case Right:
			return this.padding_[1];
		case Bottom:
			return this.padding_[2];
		case Left:
			return this.padding_[3];
		default:
			logger.error(new StringWriter().append(
					"selectionAreaPadding(): improper side.").toString());
			return 0;
		}
	}

	/**
	 * Configures the axis slider layout to be automatic.
	 * <p>
	 * This configures the selection area so that the space around it is suited
	 * for the text that is rendered.
	 */
	public void setAutoLayoutEnabled(boolean enabled) {
		this.autoPadding_ = enabled;
	}

	/**
	 * Configures the axis slider layout to be automatic.
	 * <p>
	 * Calls {@link #setAutoLayoutEnabled(boolean enabled)
	 * setAutoLayoutEnabled(true)}
	 */
	public final void setAutoLayoutEnabled() {
		setAutoLayoutEnabled(true);
	}

	/**
	 * Returns whether chart layout is computed automatically.
	 * <p>
	 * 
	 * @see WAxisSliderWidget#setAutoLayoutEnabled(boolean enabled)
	 */
	public boolean isAutoLayoutEnabled() {
		return this.autoPadding_;
	}

	/**
	 * Set whether to draw the X axis tick labels on the slider widget.
	 * <p>
	 * Labels are enabled by default.
	 */
	public void setLabelsEnabled(boolean enabled) {
		if (enabled != this.labelsEnabled_) {
			this.labelsEnabled_ = enabled;
			this.update();
		}
	}

	/**
	 * Set whether to draw the X axis tick labels on the slider widget.
	 * <p>
	 * Calls {@link #setLabelsEnabled(boolean enabled) setLabelsEnabled(true)}
	 */
	public final void setLabelsEnabled() {
		setLabelsEnabled(true);
	}

	/**
	 * Returns whether the X axis tick labels are drawn.
	 * <p>
	 * 
	 * @see WAxisSliderWidget#setLabelsEnabled(boolean enabled)
	 */
	public boolean isLabelsEnabled() {
		return this.labelsEnabled_;
	}

	/**
	 * Set whether the Y axis of the associated chart should be updated to fit
	 * the series.
	 * <p>
	 * Y axis zoom is enabled by default.
	 */
	public void setYAxisZoomEnabled(boolean enabled) {
		if (enabled != this.yAxisZoomEnabled_) {
			this.yAxisZoomEnabled_ = enabled;
			this.update();
		}
	}

	/**
	 * Set whether the Y axis of the associated chart should be updated to fit
	 * the series.
	 * <p>
	 * Calls {@link #setYAxisZoomEnabled(boolean enabled)
	 * setYAxisZoomEnabled(true)}
	 */
	public final void setYAxisZoomEnabled() {
		setYAxisZoomEnabled(true);
	}

	/**
	 * Returns whether the Y axis of the associated chart should be updated to
	 * fit the series.
	 * <p>
	 * 
	 * @see WAxisSliderWidget#setYAxisZoomEnabled(boolean enabled)
	 */
	public boolean isYAxisZoomEnabled() {
		return this.yAxisZoomEnabled_;
	}

	public WDataSeries getSeries() {
		return this.series_;
	}

	protected void render(EnumSet<RenderFlag> flags) {
		super.render(flags);
		WApplication app = WApplication.getInstance();
		app.loadJavaScript("js/WAxisSliderWidget.js", wtjs1());
	}

	protected void paintEvent(WPaintDevice paintDevice) {
		if (!(this.getChart() != null)) {
			logger.error(new StringWriter()
					.append("Attempted to draw a slider widget not associated with a chart.")
					.toString());
			return;
		}
		if (!this.getChart().cObjCreated_ || this.getChart().needsRerender()) {
			return;
		}
		if (this.series_.getType() != SeriesType.LineSeries
				&& this.series_.getType() != SeriesType.CurveSeries) {
			if (this.getMethod() == WPaintedWidget.Method.HtmlCanvas) {
				StringBuilder ss = new StringBuilder();
				ss.append("jQuery.removeData(").append(this.getJsRef())
						.append(",'sobj');");
				ss.append("\nif (").append(this.getObjJsRef()).append(") {")
						.append(this.getObjJsRef())
						.append(".canvas.style.cursor = 'auto';")
						.append("setTimeout(").append(this.getObjJsRef())
						.append(".repaint,0);}\n");
				this.doJavaScript(ss.toString());
			}
			logger.error(new StringWriter()
					.append("WAxisSliderWidget is not associated with a line or curve series.")
					.toString());
			return;
		}
		WPainter painter = new WPainter(paintDevice);
		boolean horizontal = this.getChart().getOrientation() == Orientation.Vertical;
		double w = horizontal ? this.getWidth().getValue() : this.getHeight()
				.getValue();
		double h = horizontal ? this.getHeight().getValue() : this.getWidth()
				.getValue();
		boolean autoPadding = this.autoPadding_;
		if (autoPadding
				&& EnumUtils.mask(paintDevice.getFeatures(),
						WPaintDevice.FeatureFlag.HasFontMetrics).equals(0)
				&& this.labelsEnabled_) {
			logger.error(new StringWriter()
					.append("setAutoLayout(): device does not have font metrics (not even server-side font metrics).")
					.toString());
			autoPadding = false;
		}
		if (autoPadding) {
			if (horizontal) {
				if (this.labelsEnabled_) {
					this.setSelectionAreaPadding(0, EnumSet.of(Side.Top));
					this.setSelectionAreaPadding(
							(int) (this
									.getChart()
									.getAxis(Axis.XAxis)
									.calcMaxTickLabelSize(paintDevice,
											Orientation.Vertical) + 10),
							EnumSet.of(Side.Bottom));
					this.setSelectionAreaPadding((int) Math.max(
							this.getChart()
									.getAxis(Axis.XAxis)
									.calcMaxTickLabelSize(paintDevice,
											Orientation.Horizontal) / 2, 10.0),
							EnumSet.of(Side.Left, Side.Right));
				} else {
					this.setSelectionAreaPadding(0, EnumSet.of(Side.Top));
					this.setSelectionAreaPadding(5,
							EnumSet.of(Side.Left, Side.Right, Side.Bottom));
				}
			} else {
				if (this.labelsEnabled_) {
					this.setSelectionAreaPadding(0, EnumSet.of(Side.Right));
					this.setSelectionAreaPadding((int) Math.max(
							this.getChart()
									.getAxis(Axis.XAxis)
									.calcMaxTickLabelSize(paintDevice,
											Orientation.Vertical) / 2, 10.0),
							EnumSet.of(Side.Top, Side.Bottom));
					this.setSelectionAreaPadding(
							(int) (this
									.getChart()
									.getAxis(Axis.XAxis)
									.calcMaxTickLabelSize(paintDevice,
											Orientation.Horizontal) + 10),
							EnumSet.of(Side.Left));
				} else {
					this.setSelectionAreaPadding(0, EnumSet.of(Side.Right));
					this.setSelectionAreaPadding(5,
							EnumSet.of(Side.Top, Side.Bottom, Side.Left));
				}
			}
		}
		double left = horizontal ? this.getSelectionAreaPadding(Side.Left)
				: this.getSelectionAreaPadding(Side.Top);
		double right = horizontal ? this.getSelectionAreaPadding(Side.Right)
				: this.getSelectionAreaPadding(Side.Bottom);
		double top = horizontal ? this.getSelectionAreaPadding(Side.Top) : this
				.getSelectionAreaPadding(Side.Right);
		double bottom = horizontal ? this.getSelectionAreaPadding(Side.Bottom)
				: this.getSelectionAreaPadding(Side.Left);
		double maxW = w - left - right;
		WRectF drawArea = new WRectF(left, 0, maxW, h);
		List<WAxis.Segment> segmentsBak = new ArrayList<WAxis.Segment>();
		for (int i = 0; i < this.getChart().getAxis(Axis.XAxis).segments_
				.size(); ++i) {
			segmentsBak.add(new WAxis.Segment(this.getChart().getAxis(
					Axis.XAxis).segments_.get(i)));
		}
		double renderIntervalBak = this.getChart().getAxis(Axis.XAxis).renderInterval_;
		double fullRenderLengthBak = this.getChart().getAxis(Axis.XAxis).fullRenderLength_;
		this.getChart()
				.getAxis(Axis.XAxis)
				.prepareRender(
						horizontal ? Orientation.Horizontal
								: Orientation.Vertical, drawArea.getWidth());
		final WRectF chartArea = this.getChart().chartArea_;
		WRectF selectionRect = null;
		{
			double u = -this.getChart().xAxis_.transformHandle.getValue()
					.getDx()
					/ (chartArea.getWidth() * this.getChart().xAxis_.transformHandle
							.getValue().getM11());
			selectionRect = new WRectF(0, top, maxW, h - (top + bottom));
			this.transform_.setValue(new WTransform(
					1 / this.getChart().xAxis_.transformHandle.getValue()
							.getM11(), 0, 0, 1, u * maxW, 0));
		}
		WRectF seriesArea = new WRectF(left, top + 5, maxW, h
				- (top + bottom + 5));
		WTransform selectionTransform = this.hv(new WTransform(1, 0, 0, 1,
				left, 0).multiply(this.transform_.getValue()));
		WRectF rect = selectionTransform.map(this.hv(selectionRect));
		painter.fillRect(
				this.hv(new WRectF(left, top, maxW, h - top - bottom)),
				this.background_);
		painter.fillRect(rect, this.selectedAreaBrush_);
		final double TICK_LENGTH = 5;
		final double ANGLE1 = 15;
		final double ANGLE2 = 80;
		double tickStart = 0.0;
		double tickEnd = 0.0;
		double labelPos = 0.0;
		AlignmentFlag labelHFlag = AlignmentFlag.AlignCenter;
		AlignmentFlag labelVFlag = AlignmentFlag.AlignMiddle;
		final WAxis axis = this.getChart().getAxis(Axis.XAxis);
		if (horizontal) {
			tickStart = 0;
			tickEnd = TICK_LENGTH;
			labelPos = TICK_LENGTH;
			labelVFlag = AlignmentFlag.AlignTop;
		} else {
			tickStart = -TICK_LENGTH;
			tickEnd = 0;
			labelPos = -TICK_LENGTH;
			labelHFlag = AlignmentFlag.AlignRight;
		}
		if (horizontal) {
			if (axis.getLabelAngle() > ANGLE1) {
				labelHFlag = AlignmentFlag.AlignRight;
				if (axis.getLabelAngle() > ANGLE2) {
					labelVFlag = AlignmentFlag.AlignMiddle;
				}
			} else {
				if (axis.getLabelAngle() < -ANGLE1) {
					labelHFlag = AlignmentFlag.AlignLeft;
					if (axis.getLabelAngle() < -ANGLE2) {
						labelVFlag = AlignmentFlag.AlignMiddle;
					}
				}
			}
		} else {
			if (axis.getLabelAngle() > ANGLE1) {
				labelVFlag = AlignmentFlag.AlignBottom;
				if (axis.getLabelAngle() > ANGLE2) {
					labelHFlag = AlignmentFlag.AlignCenter;
				}
			} else {
				if (axis.getLabelAngle() < -ANGLE1) {
					labelVFlag = AlignmentFlag.AlignTop;
					if (axis.getLabelAngle() < -ANGLE2) {
						labelHFlag = AlignmentFlag.AlignCenter;
					}
				}
			}
		}
		EnumSet<AxisProperty> axisProperties = EnumSet.of(AxisProperty.Line);
		if (this.labelsEnabled_) {
			axisProperties.add(AxisProperty.Labels);
		}
		if (horizontal) {
			axis.render(painter, axisProperties, new WPointF(
					drawArea.getLeft(), h - bottom),
					new WPointF(drawArea.getRight(), h - bottom), tickStart,
					tickEnd, labelPos, EnumSet.of(labelHFlag, labelVFlag));
			WPainterPath line = new WPainterPath();
			line.moveTo(drawArea.getLeft() + 0.5, h - (bottom - 0.5));
			line.lineTo(drawArea.getRight(), h - (bottom - 0.5));
			painter.strokePath(line, this.getChart().getAxis(Axis.XAxis)
					.getPen());
		} else {
			axis.render(painter, axisProperties,
					new WPointF(this.getSelectionAreaPadding(Side.Left) - 1,
							drawArea.getLeft()),
					new WPointF(this.getSelectionAreaPadding(Side.Left) - 1,
							drawArea.getRight()), tickStart, tickEnd, labelPos,
					EnumSet.of(labelHFlag, labelVFlag));
			WPainterPath line = new WPainterPath();
			line.moveTo(this.getSelectionAreaPadding(Side.Left) - 0.5,
					drawArea.getLeft() + 0.5);
			line.lineTo(this.getSelectionAreaPadding(Side.Left) - 0.5,
					drawArea.getRight());
			painter.strokePath(line, this.getChart().getAxis(Axis.XAxis)
					.getPen());
		}
		WPainterPath curve = new WPainterPath();
		{
			WTransform t = new WTransform(1, 0, 0, 1, seriesArea.getLeft(),
					seriesArea.getTop()).multiply(
					new WTransform(
							seriesArea.getWidth() / chartArea.getWidth(), 0, 0,
							seriesArea.getHeight() / chartArea.getHeight(), 0,
							0)).multiply(
					new WTransform(1, 0, 0, 1, -chartArea.getLeft(), -chartArea
							.getTop()));
			if (!horizontal) {
				t.assign(new WTransform(0, 1, 1, 0, this
						.getSelectionAreaPadding(Side.Left)
						- this.getSelectionAreaPadding(Side.Right) - 5, 0)
						.multiply(t).multiply(new WTransform(0, 1, 1, 0, 0, 0)));
			}
			curve.assign(t.map(this.getChart().pathForSeries(this.series_)));
		}
		{
			WRectF leftHandle = this
					.hv(new WRectF(-5, top, 5, h - top - bottom));
			WTransform t = new WTransform(1, 0, 0, 1, left, -top)
					.multiply(new WTransform().translate(this.transform_
							.getValue().map(selectionRect.getTopLeft())));
			painter.fillRect(this.hv(t).map(leftHandle), this.handleBrush_);
		}
		{
			WRectF rightHandle = this
					.hv(new WRectF(0, top, 5, h - top - bottom));
			WTransform t = new WTransform(1, 0, 0, 1, left, -top)
					.multiply(new WTransform().translate(this.transform_
							.getValue().map(selectionRect.getTopRight())));
			painter.fillRect(this.hv(t).map(rightHandle), this.handleBrush_);
		}
		if (this.selectedSeriesPen_ != this.seriesPen_
				&& !this.selectedSeriesPen_.equals(this.seriesPen_)) {
			WPainterPath clipPath = new WPainterPath();
			clipPath.addRect(this.hv(selectionRect));
			painter.setClipPath(selectionTransform.map(clipPath));
			painter.setClipping(true);
			painter.setPen(this.getSelectedSeriesPen());
			painter.drawPath(curve);
			WPainterPath leftClipPath = new WPainterPath();
			leftClipPath.addRect(this.hv(new WTransform(1, 0, 0, 1,
					-selectionRect.getWidth(), 0).map(selectionRect)));
			painter.setClipPath(this.hv(
					new WTransform(1, 0, 0, 1, left, -top)
							.multiply(new WTransform()
									.translate(this.transform_.getValue().map(
											selectionRect.getTopLeft())))).map(
					leftClipPath));
			painter.setPen(this.getSeriesPen());
			painter.drawPath(curve);
			WPainterPath rightClipPath = new WPainterPath();
			rightClipPath.addRect(this.hv(new WTransform(1, 0, 0, 1,
					selectionRect.getWidth(), 0).map(selectionRect)));
			painter.setClipPath(this.hv(
					new WTransform(1, 0, 0, 1, left - selectionRect.getRight(),
							-top).multiply(new WTransform()
							.translate(this.transform_.getValue().map(
									selectionRect.getTopRight())))).map(
					rightClipPath));
			painter.drawPath(curve);
			painter.setClipping(false);
		} else {
			WPainterPath clipPath = new WPainterPath();
			clipPath.addRect(this.hv(new WRectF(left, top, maxW, h - top
					- bottom)));
			painter.setClipPath(clipPath);
			painter.setClipping(true);
			painter.setPen(this.getSeriesPen());
			painter.drawPath(curve);
			painter.setClipping(false);
		}
		if (this.getMethod() == WPaintedWidget.Method.HtmlCanvas) {
			WApplication app = WApplication.getInstance();
			StringBuilder ss = new StringBuilder();
			ss.append("new Wt3_3_11.WAxisSliderWidget(")
					.append(app.getJavaScriptClass())
					.append(",")
					.append(this.getJsRef())
					.append(",")
					.append(this.getObjJsRef())
					.append(",")
					.append("{chart:")
					.append(this.getChart().getCObjJsRef())
					.append(",transform:")
					.append(this.transform_.getJsRef())
					.append(",rect:function(){return ")
					.append(rect.getJsRef())
					.append("},drawArea:")
					.append(drawArea.getJsRef())
					.append(",series:")
					.append(this.getChart().getSeriesIndexOf(this.series_))
					.append(",updateYAxis:")
					.append(StringUtils.asString(this.yAxisZoomEnabled_)
							.toString()).append("});");
			this.doJavaScript(ss.toString());
		}
		this.getChart().getAxis(Axis.XAxis).segments_.clear();
		for (int i = 0; i < segmentsBak.size(); ++i) {
			this.getChart().getAxis(Axis.XAxis).segments_
					.add(new WAxis.Segment(segmentsBak.get(i)));
		}
		this.getChart().getAxis(Axis.XAxis).renderInterval_ = renderIntervalBak;
		this.getChart().getAxis(Axis.XAxis).fullRenderLength_ = fullRenderLengthBak;
	}

	private void init() {
		this.transform_ = this.createJSTransform();
		this.mouseWentDown().addListener(
				"function(o, e){var o=" + this.getSObjJsRef()
						+ ";if(o){o.mouseDown(o, e);}}");
		this.mouseWentUp().addListener(
				"function(o, e){var o=" + this.getSObjJsRef()
						+ ";if(o){o.mouseUp(o, e);}}");
		this.mouseDragged().addListener(
				"function(o, e){var o=" + this.getSObjJsRef()
						+ ";if(o){o.mouseDrag(o, e);}}");
		this.mouseMoved().addListener(
				"function(o, e){var o=" + this.getSObjJsRef()
						+ ";if(o){o.mouseMoved(o, e);}}");
		this.touchStarted().addListener(
				"function(o, e){var o=" + this.getSObjJsRef()
						+ ";if(o){o.touchStarted(o, e);}}");
		this.touchEnded().addListener(
				"function(o, e){var o=" + this.getSObjJsRef()
						+ ";if(o){o.touchEnded(o, e);}}");
		this.touchMoved().addListener(
				"function(o, e){var o=" + this.getSObjJsRef()
						+ ";if(o){o.touchMoved(o, e);}}");
		this.setSelectionAreaPadding(0, EnumSet.of(Side.Top));
		this.setSelectionAreaPadding(20, EnumSet.of(Side.Left, Side.Right));
		this.setSelectionAreaPadding(30, EnumSet.of(Side.Bottom));
		if (this.getChart() != null) {
			this.getChart().addAxisSliderWidget(this);
		}
	}

	private String getSObjJsRef() {
		return "jQuery.data(" + this.getJsRef() + ",'sobj')";
	}

	private WRectF hv(final WRectF rect) {
		boolean horizontal = this.getChart().getOrientation() == Orientation.Vertical;
		if (horizontal) {
			return rect;
		} else {
			return new WRectF(this.getWidth().getValue() - rect.getY()
					- rect.getHeight(), rect.getX(), rect.getHeight(),
					rect.getWidth());
		}
	}

	private WTransform hv(final WTransform t) {
		boolean horizontal = this.getChart().getOrientation() == Orientation.Vertical;
		if (horizontal) {
			return t;
		} else {
			return new WTransform(0, 1, 1, 0, 0, 0).multiply(t).multiply(
					new WTransform(0, 1, 1, 0, 0, 0));
		}
	}

	private WCartesianChart getChart() {
		if (this.series_ != null) {
			return this.series_.getChart();
		} else {
			return null;
		}
	}

	private WDataSeries series_;
	private WPen seriesPen_;
	private WPen selectedSeriesPen_;
	private WBrush handleBrush_;
	private WBrush background_;
	private WBrush selectedAreaBrush_;
	private boolean autoPadding_;
	private boolean labelsEnabled_;
	private boolean yAxisZoomEnabled_;
	private int[] padding_ = new int[4];
	private WJavaScriptHandle<WTransform> transform_;

	static WJavaScriptPreamble wtjs1() {
		return new WJavaScriptPreamble(
				JavaScriptScope.WtClassScope,
				JavaScriptObjectType.JavaScriptConstructor,
				"WAxisSliderWidget",
				"function(y,e,m,f){function C(a){return a.pointerType===2||a.pointerType===3||a.pointerType===\"pen\"||a.pointerType===\"touch\"}function I(){M(m.repaint)}function J(){var a=f.transform[4]/f.drawArea[2];return[a,f.transform[0]+a]}function p(a){I();if(a){a=f.transform;var b=a[4]/f.drawArea[2];f.chart.setXRange(f.series,b,a[0]+b,f.updateYAxis)}}function r(){return!f.chart.config.isHorizontal}function D(a,b,c){return r()?a.y>=t(b)&&a.y<=u(b)&& a.x>v(b)-c/2&&a.x<v(b)+c/2:a.x>=v(b)&&a.x<=w(b)&&a.y>t(b)-c/2&&a.y<t(b)+c/2}function E(a,b,c){return r()?a.y>=t(b)&&a.y<=u(b)&&a.x>w(b)-c/2&&a.x<w(b)+c/2:a.x>=v(b)&&a.x<=w(b)&&a.y>u(b)-c/2&&a.y<u(b)+c/2}function z(a,b){return r()?a.y>=t(b)&&a.y<=u(b)&&a.x>v(b)&&a.x<w(b):a.x>=v(b)&&a.x<=w(b)&&a.y>t(b)&&a.y<u(b)}function F(a){var b=f.transform,c=f.drawArea,d=b[4]/c[2];b=b[0]+d;a=(d*c[2]+a)/c[2];if(!(b<=a)){c=1/(b-a);if(!(c>f.chart.config.maxZoom.x))if(!(c<f.chart.config.minZoom.x)){if(a<0)a=0;if(a> 1)a=1;n.changeRange(a,b);p(true);a=J();if(Math.abs(a[1]-b)>Math.abs(a[0]-d)){n.changeRange(d,b);p(true)}}}}function G(a){var b=f.transform,c=f.drawArea,d=b[4]/c[2];b=b[0]+d;a=(b*c[2]+a)/c[2];if(!(a<=d)){c=1/(a-d);if(!(c>f.chart.config.maxZoom.x))if(!(c<f.chart.config.minZoom.x)){if(a<0)a=0;if(a>1)a=1;n.changeRange(d,a);p(true);a=J();if(Math.abs(a[0]-d)>Math.abs(a[1]-b)){n.changeRange(d,b);p(true)}}}}function K(a){var b=f.transform,c=f.drawArea,d=b[4]/c[2];b=b[0]+d;d=d*c[2];var g=d+a;if(g<0){a=-d; g=0}g=g/c[2];b=b*c[2];a=b+a;if(a>c[2]){a=c[2]-b;g=d+a;g=g/c[2];a=c[2]}n.changeRange(g,a/c[2]);p(true)}var N=function(){return window.requestAnimationFrame||window.webkitRequestAnimationFrame||window.mozRequestAnimationFrame||function(a){window.setTimeout(a,0)}}(),H=false,M=function(a){if(!H){H=true;N(function(){a();H=false})}},o={};jQuery.data(e,\"sobj\",this);var n=this,h=y.WT;m.canvas.style.msTouchAction=\"none\";var q=false;if(window.MSPointerEvent||window.PointerEvent){e.style.touchAction=\"none\"; m.canvas.style.msTouchAction=\"none\";m.canvas.style.touchAction=\"none\"}if(window.MSPointerEvent||window.PointerEvent)(function(){function a(){if(pointers.length>0&&!q)q=true;else if(pointers.length<=0&&q)q=false}function b(k){if(C(k)){k.preventDefault();pointers.push(k);a();o.start(e,{touches:pointers.slice(0)})}}function c(k){if(q)if(C(k)){k.preventDefault();var l;for(l=0;l<pointers.length;++l)if(pointers[l].pointerId===k.pointerId){pointers.splice(l,1);break}a();o.end(e,{touches:pointers.slice(0), changedTouches:[]})}}function d(k){if(C(k)){k.preventDefault();var l;for(l=0;l<pointers.length;++l)if(pointers[l].pointerId===k.pointerId){pointers[l]=k;break}a();o.moved(e,{touches:pointers.slice(0)})}}pointers=[];var g=jQuery.data(e,\"eobj\");if(g)if(window.PointerEvent){e.removeEventListener(\"pointerdown\",g.pointerDown);e.removeEventListener(\"pointerup\",g.pointerUp);e.removeEventListener(\"pointerout\",g.pointerUp);e.removeEventListener(\"pointermove\",g.pointerMove)}else{e.removeEventListener(\"MSPointerDown\", g.pointerDown);e.removeEventListener(\"MSPointerUp\",g.pointerUp);e.removeEventListener(\"MSPointerOut\",g.pointerUp);e.removeEventListener(\"MSPointerMove\",g.pointerMove)}jQuery.data(e,\"eobj\",{pointerDown:b,pointerUp:c,pointerMove:d});if(window.PointerEvent){e.addEventListener(\"pointerdown\",b);e.addEventListener(\"pointerup\",c);e.addEventListener(\"pointerout\",c);e.addEventListener(\"pointermove\",d)}else{e.addEventListener(\"MSPointerDown\",b);e.addEventListener(\"MSPointerUp\",c);e.addEventListener(\"MSPointerOut\", c);e.addEventListener(\"MSPointerMove\",d)}})();var v=h.gfxUtils.rect_left,w=h.gfxUtils.rect_right,t=h.gfxUtils.rect_top,u=h.gfxUtils.rect_bottom,i=null,j=null;this.changeRange=function(a,b){if(a<0)a=0;if(b>1)b=1;var c=f.drawArea;f.transform[0]=b-a;f.transform[4]=a*c[2];I()};this.mouseDown=function(a,b){if(!q){i=h.widgetCoordinates(e,b);a=f.rect();if(D(i,a,10))j=1;else if(E(i,a,10))j=3;else if(z(i,a))j=2;else{j=null;return}h.cancelEvent(b)}};this.mouseUp=function(a,b){if(!q){i=null;if(j!==null){j=null; h.cancelEvent(b)}}};this.mouseDrag=function(a,b){if(!q)if(j){h.cancelEvent(b);a=h.widgetCoordinates(e,b);if(i===null)i=a;else{b=r()?a.x-i.x:a.y-i.y;switch(j){case 1:F(b);break;case 2:K(b);break;case 3:G(b);break}i=a;p(true)}}};this.mouseMoved=function(a,b){setTimeout(function(){if(!q)if(!j){var c=h.widgetCoordinates(e,b),d=f.rect();m.canvas.style.cursor=D(c,d,10)||E(c,d,10)?r()?\"col-resize\":\"row-resize\":z(c,d)?\"move\":\"auto\"}},0)};var x=false,s=false,A=null;o.start=function(a,b){x=b.touches.length=== 1;s=b.touches.length===2;if(x){i=h.widgetCoordinates(m.canvas,b.touches[0]);a=f.rect();if(D(i,a,20))j=1;else if(E(i,a,20))j=3;else if(z(i,a))j=2;else{j=null;return}h.capture(null);h.capture(m.canvas);b.preventDefault&&b.preventDefault()}else if(s){j=null;var c=[h.widgetCoordinates(m.canvas,b.touches[0]),h.widgetCoordinates(m.canvas,b.touches[1])];a=f.rect();if(z(c[0],a)&&z(c[1],a)){A=r()?Math.abs(c[0].x-c[1].x):Math.abs(c[0].y-c[1].y);h.capture(null);h.capture(m.canvas);b.preventDefault&&b.preventDefault()}}}; o.end=function(a,b){var c=Array.prototype.slice.call(b.touches);a=x;var d=s,g=c.length===0;x=c.length===1;s=c.length===2;g||function(){var k;for(k=0;k<b.changedTouches.length;++k)(function(){for(var l=b.changedTouches[k].identifier,B=0;B<c.length;++B)if(c[B].identifier===l){c.splice(B,1);return}})()}();g=c.length===0;x=c.length===1;s=c.length===2;if(g&&a){i=null;if(j===null)return;j=null;h.cancelEvent(b)}if(x&&d){s=false;A=null;h.cancelEvent(b);o.start(e,b)}if(g&&d){s=false;A=null;h.cancelEvent(b)}}; o.moved=function(a,b){if(j){b.preventDefault&&b.preventDefault();a=h.widgetCoordinates(e,b);if(i===null)i=a;else{b=r()?a.x-i.x:a.y-i.y;switch(j){case 1:F(b);break;case 2:K(b);break;case 3:G(b);break}i=a;p(true)}}else if(s){b.preventDefault&&b.preventDefault();touches=[h.widgetCoordinates(m.canvas,b.touches[0]),h.widgetCoordinates(m.canvas,b.touches[1])];f.rect();a=r()?Math.abs(touches[0].x-touches[1].x):Math.abs(touches[0].y-touches[1].y);b=a-A;F(-b/2);G(b/2);A=a;p(true)}};this.updateConfig=function(a){for(var b in a)if(a.hasOwnProperty(b))f[b]= a[b];p(false)};n.updateConfig({});if(window.TouchEvent&&!window.MSPointerEvent&&!window.PointerEvent){n.touchStarted=o.start;n.touchEnded=o.end;n.touchMoved=o.moved}else{y=function(){};n.touchStarted=y;n.touchEnded=y;n.touchMoved=y}}");
	}
}
