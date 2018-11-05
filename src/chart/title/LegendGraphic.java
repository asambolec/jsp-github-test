/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2014, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.]
 *
 * ------------------
 * LegendGraphic.java
 * ------------------
 * (C) Copyright 2004-2014, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 26-Oct-2004 : Version 1 (DG);
 * 21-Jan-2005 : Modified return type of RectangleAnchor.coordinates()
 *               method (DG);
 * 20-Apr-2005 : Added new draw() method (DG);
 * 13-May-2005 : Fixed to respect margin, border and padding settings (DG);
 * 01-Sep-2005 : Implemented PublicCloneable (DG);
 * ------------- JFREECHART 1.0.x ---------------------------------------------
 * 13-Dec-2006 : Added fillPaintTransformer attribute, so legend graphics can
 *               display gradient paint correctly, updated equals() and
 *               corrected clone() (DG);
 * 01-Aug-2007 : Updated API docs (DG);
 * 16-Jun-2012 : Removed JCommon dependencies (DG);
 *
 */

package chart.title;

import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import chart.block.AbstractBlock;
import chart.block.Block;
import chart.block.LengthConstraintType;
import chart.block.RectangleConstraint;
import chart.ui.Size2D;
import chart.util.ObjectUtils;
import chart.util.ParamChecks;
import chart.util.PublicCloneable;
import chart.util.SerialUtils;
import chart.util.ShapeUtils;

/**
 * The graphical item within a legend item.
 */
public class LegendGraphic extends AbstractBlock implements Block, PublicCloneable {

	/** For serialization. */
	static final long serialVersionUID = -1338791523854985009L;

	/**
	 * A flag that controls whether or not the shape is visible - see also
	 * lineVisible.
	 */
	private boolean shape_Visible;

	/**
	 * The shape to display. To allow for accurate positioning, the center of the
	 * shape should be at (0, 0).
	 */
	private transient Shape shape;

	/** A flag that controls whether or not the shape is filled. */
	private boolean shape_Filled;

	/** The fill paint for the shape. */
	private transient Paint fill_Paint;

	/** A flag that controls whether or not the shape outline is visible. */
	private boolean shape_Outline_Visible;

	/** The outline paint for the shape. */
	private transient Paint outline_Paint;

	/** The outline stroke for the shape. */
	private transient Stroke outline_Stroke;

	/**
	 * A flag that controls whether or not the line is visible - see also
	 * shapeVisible.
	 */
	private boolean line_Visible;

	/** The line. */
	private transient Shape line;

	/** The line stroke. */
	private transient Stroke line_Stroke;

	/** The line paint. */
	private transient Paint line_Paint;

	/**
	 * Creates a new legend graphic.
	 *
	 * @param shape     the shape (<code>null</code> not permitted).
	 * @param fillPaint the fill paint (<code>null</code> not permitted).
	 */
	public LegendGraphic(Shape shape, Paint fill_Paint) {
		ParamChecks.nullNotPermitted(shape, "shape");
		ParamChecks.nullNotPermitted(fill_Paint, "fill_Paint");
		this.shape_Visible = true;
		this.shape = shape;
		this.shape_Filled = true;
		this.fill_Paint = fill_Paint;
		setPadding(2.0, 2.0, 2.0, 2.0);
	}

	/**
	 * Returns a flag that controls whether or not the shape is visible.
	 *
	 * @return A boolean.
	 *
	 * @see #setShapeVisible(boolean)
	 */
	public boolean isShapeVisible() {
		return this.shape_Visible;
	}

	/**
	 * Sets a flag that controls whether or not the shape is visible.
	 *
	 * @param visible the flag.
	 *
	 * @see #isShapeVisible()
	 */
	public void setShapeVisible(boolean visible) {
		this.shape_Visible = visible;
	}

	/**
	 * Returns the shape.
	 *
	 * @return The shape.
	 *
	 * @see #setShape(Shape)
	 */
	public Shape getShape() {
		return this.shape;
	}

	/**
	 * Sets the shape.
	 *
	 * @param shape the shape.
	 *
	 * @see #getShape()
	 */
	public void setShape(Shape shape) {
		this.shape = shape;
	}

	/**
	 * Returns a flag that controls whether or not the shapes are filled.
	 *
	 * @return A boolean.
	 *
	 * @see #setShapeFilled(boolean)
	 */
	public boolean isShapeFilled() {
		return this.shape_Filled;
	}

	/**
	 * Sets a flag that controls whether or not the shape is filled.
	 *
	 * @param filled the flag.
	 *
	 * @see #isShapeFilled()
	 */
	public void setShapeFilled(boolean filled) {
		this.shape_Filled = filled;
	}

	/**
	 * Returns the paint used to fill the shape.
	 *
	 * @return The fill paint.
	 *
	 * @see #setFillPaint(Paint)
	 */
	public Paint getFillPaint() {
		return this.fill_Paint;
	}

	/**
	 * Sets the paint used to fill the shape.
	 *
	 * @param paint the paint.
	 *
	 * @see #getFillPaint()
	 */
	public void setFillPaint(Paint paint) {
		this.fill_Paint = paint;
	}

	/**
	 * Returns a flag that controls whether the shape outline is visible.
	 *
	 * @return A boolean.
	 *
	 * @see #setShapeOutlineVisible(boolean)
	 */
	public boolean isShapeOutlineVisible() {
		return this.shape_Outline_Visible;
	}

	/**
	 * Sets a flag that controls whether or not the shape outline is visible.
	 *
	 * @param visible the flag.
	 *
	 * @see #isShapeOutlineVisible()
	 */
	public void setShapeOutlineVisible(boolean visible) {
		this.shape_Outline_Visible = visible;
	}

	/**
	 * Returns the outline paint.
	 *
	 * @return The paint.
	 *
	 * @see #setOutlinePaint(Paint)
	 */
	public Paint getOutlinePaint() {
		return this.outline_Paint;
	}

	/**
	 * Sets the outline paint.
	 *
	 * @param paint the paint.
	 *
	 * @see #getOutlinePaint()
	 */
	public void setOutlinePaint(Paint paint) {
		this.outline_Paint = paint;
	}

	/**
	 * Returns the outline stroke.
	 *
	 * @return The stroke.
	 *
	 * @see #setOutlineStroke(Stroke)
	 */
	public Stroke getOutlineStroke() {
		return this.outline_Stroke;
	}

	/**
	 * Sets the outline stroke.
	 *
	 * @param stroke the stroke.
	 *
	 * @see #getOutlineStroke()
	 */
	public void setOutlineStroke(Stroke stroke) {
		this.outline_Stroke = stroke;
	}

	/**
	 * Returns the flag that controls whether or not the line is visible.
	 *
	 * @return A boolean.
	 *
	 * @see #setLineVisible(boolean)
	 */
	public boolean isLineVisible() {
		return this.line_Visible;
	}

	/**
	 * Sets the flag that controls whether or not the line is visible.
	 *
	 * @param visible the flag.
	 *
	 * @see #isLineVisible()
	 */
	public void setLineVisible(boolean visible) {
		this.line_Visible = visible;
	}

	/**
	 * Returns the line centered about (0, 0).
	 *
	 * @return The line.
	 *
	 * @see #setLine(Shape)
	 */
	public Shape getLine() {
		return this.line;
	}

	/**
	 * Sets the line. A Shape is used here, because then you can use Line2D,
	 * GeneralPath or any other Shape to represent the line.
	 *
	 * @param line the line.
	 *
	 * @see #getLine()
	 */
	public void setLine(Shape line) {
		this.line = line;
	}

	/**
	 * Returns the line paint.
	 *
	 * @return The paint.
	 *
	 * @see #setLinePaint(Paint)
	 */
	public Paint getLinePaint() {
		return this.line_Paint;
	}

	/**
	 * Sets the line paint.
	 *
	 * @param paint the paint.
	 *
	 * @see #getLinePaint()
	 */
	public void setLinePaint(Paint paint) {
		this.line_Paint = paint;
	}

	/**
	 * Returns the line stroke.
	 *
	 * @return The stroke.
	 *
	 * @see #setLineStroke(Stroke)
	 */
	public Stroke getLineStroke() {
		return this.line_Stroke;
	}

	/**
	 * Sets the line stroke.
	 *
	 * @param stroke the stroke.
	 *
	 * @see #getLineStroke()
	 */
	public void setLineStroke(Stroke stroke) {
		this.line_Stroke = stroke;
	}

	/**
	 * Arranges the contents of the block, within the given constraints, and returns
	 * the block size.
	 *
	 * @param g2         the graphics device.
	 * @param constraint the constraint (<code>null</code> not permitted).
	 *
	 * @return The block size (in Java2D units, never <code>null</code>).
	 */
	@Override
	public Size2D arrange(Graphics2D g2, RectangleConstraint constraint) {
		RectangleConstraint contentConstraint = toContentConstraint(constraint);
		LengthConstraintType w = contentConstraint.getWidthConstraintType();
		LengthConstraintType h = contentConstraint.getHeightConstraintType();
		Size2D contentSize = null;
		if (w == LengthConstraintType.NONE) {
			if (h == LengthConstraintType.NONE) {
				contentSize = arrangeNN(g2);
			} else if (h == LengthConstraintType.RANGE) {
				throw new RuntimeException("Not yet implemented.");
			} else if (h == LengthConstraintType.FIXED) {
				throw new RuntimeException("Not yet implemented.");
			}
		} else if (w == LengthConstraintType.RANGE) {
			if (h == LengthConstraintType.NONE) {
				throw new RuntimeException("Not yet implemented.");
			} else if (h == LengthConstraintType.RANGE) {
				throw new RuntimeException("Not yet implemented.");
			} else if (h == LengthConstraintType.FIXED) {
				throw new RuntimeException("Not yet implemented.");
			}
		} else if (w == LengthConstraintType.FIXED) {
			if (h == LengthConstraintType.NONE) {
				throw new RuntimeException("Not yet implemented.");
			} else if (h == LengthConstraintType.RANGE) {
				throw new RuntimeException("Not yet implemented.");
			} else if (h == LengthConstraintType.FIXED) {
				contentSize = new Size2D(contentConstraint.getWidth(), contentConstraint.getHeight());
			}
		}
		return new Size2D(calculateTotalWidth(contentSize.getWidth()), calculateTotalHeight(contentSize.getHeight()));
	}

	/**
	 * Performs the layout with no constraint, so the content size is determined by
	 * the bounds of the shape and/or line drawn to represent the series.
	 *
	 * @param g2 the graphics device.
	 *
	 * @return The content size.
	 */
	protected Size2D arrangeNN(Graphics2D g2) {
		Rectangle2D contentSize = new Rectangle2D.Double();
		if (this.line != null) {
			contentSize.setRect(this.line.getBounds2D());
		}
		if (this.shape != null) {
			contentSize = contentSize.createUnion(this.shape.getBounds2D());
		}
		return new Size2D(contentSize.getWidth(), contentSize.getHeight());
	}

	/**
	 * Draws the graphic item within the specified area.
	 *
	 * @param g2   the graphics device.
	 * @param area the area.
	 */
	@Override
	public void draw(Graphics2D g2, Rectangle2D area) {

		area = trimMargin(area);
		drawBorder(g2, area);
		area = trimBorder(area);
		area = trimPadding(area);

		if (this.line_Visible) {
			Point2D location = null;
			Shape aLine = ShapeUtils.createTranslatedShape(getLine(), location.getX(), location.getY());
			g2.setPaint(this.line_Paint);
			g2.setStroke(this.line_Stroke);
			g2.draw(aLine);
		}

		if (this.shape_Visible) {
			Point2D location = null;

			Shape s = ShapeUtils.createTranslatedShape(this.shape, location.getX(), location.getY());
			if (this.shape_Filled) {
				Paint p = this.fill_Paint;
				if (p instanceof GradientPaint) {
					GradientPaint gp = (GradientPaint) this.fill_Paint;
				}
				g2.setPaint(p);
				g2.fill(s);
			}
			if (this.shape_Outline_Visible) {
				g2.setPaint(this.outline_Paint);
				g2.setStroke(this.outline_Stroke);
				g2.draw(s);
			}
		}

	}

	/**
	 * Draws the block within the specified area.
	 *
	 * @param g2     the graphics device.
	 * @param area   the area.
	 * @param params ignored (<code>null</code> permitted).
	 *
	 * @return Always <code>null</code>.
	 */
	@Override
	public Object draw(Graphics2D g2, Rectangle2D area, Object params) {
		draw(g2, area);
		return null;
	}

	/**
	 * Tests this <code>LegendGraphic</code> instance for equality with an arbitrary
	 * object.
	 *
	 * @param obj the object (<code>null</code> permitted).
	 *
	 * @return A boolean.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LegendGraphic)) {
			return false;
		}
		LegendGraphic that = (LegendGraphic) obj;
		if (this.shape_Visible != that.shape_Visible) {
			return false;
		}
		if (!ShapeUtils.equal(this.shape, that.shape)) {
			return false;
		}
		if (this.shape_Filled != that.shape_Filled) {
			return false;
		}
		if (this.shape_Outline_Visible != that.shape_Outline_Visible) {
			return false;
		}
		if (!ObjectUtils.equal(this.outline_Stroke, that.outline_Stroke)) {
			return false;
		}
		if (this.line_Visible != that.line_Visible) {
			return false;
		}
		if (!ShapeUtils.equal(this.line, that.line)) {
			return false;
		}
		if (!ObjectUtils.equal(this.line_Stroke, that.line_Stroke)) {
			return false;
		}
		return super.equals(obj);
	}

	/**
	 * Returns a hash code for this instance.
	 *
	 * @return A hash code.
	 */
	@Override
	public int hashCode() {
		int result = 193;
		result = 37 * result + ObjectUtils.hashCode(this.fill_Paint);
		// FIXME: use other fields too
		return result;
	}

	/**
	 * Returns a clone of this <code>LegendGraphic</code> instance.
	 *
	 * @return A clone of this <code>LegendGraphic</code> instance.
	 *
	 * @throws CloneNotSupportedException if there is a problem cloning.
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		LegendGraphic clone = (LegendGraphic) super.clone();
		clone.shape = ShapeUtils.clone(this.shape);
		clone.line = ShapeUtils.clone(this.line);
		return clone;
	}

	/**
	 * Provides serialization support.
	 *
	 * @param stream the output stream.
	 *
	 * @throws IOException if there is an I/O error.
	 */
	private void writeObject(ObjectOutputStream stream) throws IOException {
		stream.defaultWriteObject();
		SerialUtils.writeShape(this.shape, stream);
		SerialUtils.writePaint(this.fill_Paint, stream);
		SerialUtils.writePaint(this.outline_Paint, stream);
		SerialUtils.writeStroke(this.outline_Stroke, stream);
		SerialUtils.writeShape(this.line, stream);
		SerialUtils.writePaint(this.line_Paint, stream);
		SerialUtils.writeStroke(this.line_Stroke, stream);
	}

	/**
	 * Provides serialization support.
	 *
	 * @param stream the input stream.
	 *
	 * @throws IOException            if there is an I/O error.
	 * @throws ClassNotFoundException if there is a classpath problem.
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		this.shape = SerialUtils.readShape(stream);
		this.fill_Paint = SerialUtils.readPaint(stream);
		this.outline_Paint = SerialUtils.readPaint(stream);
		this.outline_Stroke = SerialUtils.readStroke(stream);
		this.line = SerialUtils.readShape(stream);
		this.line_Paint = SerialUtils.readPaint(stream);
		this.line_Stroke = SerialUtils.readStroke(stream);
	}

}
