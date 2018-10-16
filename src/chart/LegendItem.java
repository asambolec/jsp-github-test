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
 * ---------------
 * LegendItem.java
 * ---------------
 * (C) Copyright 2000-2014, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Andrzej Porebski;
 *                   David Li;
 *                   Wolfgang Irler;
 *                   Luke Quinane;
 *
 * Changes (from 2-Oct-2002)
 * -------------------------
 * 02-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 17-Jan-2003 : Dropped outlineStroke attribute (DG);
 * 08-Oct-2003 : Applied patch for displaying series line style, contributed by
 *               Luke Quinane (DG);
 * 21-Jan-2004 : Added the shapeFilled flag (DG);
 * 04-Jun-2004 : Added equals() method, implemented Serializable (DG);
 * 25-Nov-2004 : Changes required by new LegendTitle implementation (DG);
 * 11-Jan-2005 : Removed deprecated code in preparation for the 1.0.0
 *               release (DG);
 * 20-Apr-2005 : Added tooltip and URL text (DG);
 * 28-Nov-2005 : Separated constructors for AttributedString labels (DG);
 * 10-Dec-2005 : Fixed serialization bug (1377239) (DG);
 * ------------- JFREECHART 1.0.x ---------------------------------------------
 * 20-Jul-2006 : Added dataset and series index fields (DG);
 * 13-Dec-2006 : Added fillPaintTransformer attribute (DG);
 * 18-May-2007 : Added dataset and seriesKey fields (DG);
 * 03-Aug-2007 : Fixed null pointer exception (DG);
 * 23-Apr-2008 : Added new constructor and implemented Cloneable (DG);
 * 17-Jun-2008 : Added optional labelFont and labelPaint attributes (DG);
 * 15-Oct-2008 : Added new constructor (DG);
 * 28-Apr-2009 : Added various setter methods (DG);
 * 15-Jun-2012 : Removed JCommon dependency (DG);
 *
 */

package chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.AttributedString;
import java.text.CharacterIterator;

import chart.util.ObjectUtils;
import chart.util.ParamChecks;
import chart.util.PublicCloneable;
import chart.util.SerialUtils;
import chart.util.ShapeUtils;
import data.general.Dataset;

/**
 * A temporary storage object for recording the properties of a legend item,
 * without any consideration for layout issues.
 */
@SuppressWarnings("rawtypes")
public class LegendItem implements Cloneable, Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -797214582948827144L;

    /**
     * The dataset.
     *
     * @since 1.0.6
     */
    private Dataset dataset;

    /**
     * The series key.
     *
     * @since 1.0.6
     */
    private Comparable series_Key;

    /** The dataset index. */
    private int dataset_Index;

    /** The series index. */
    private int series;

    /** The label. */
    private String label;

    /**
     * The label font ({@code null} is permitted).
     *
     * @since 1.0.11
     */
    private Font label_Font;

    /**
     * The label paint ({@code null} is permitted).
     *
     * @since 1.0.11
     */
    private transient Paint label_Paint;

    /** The attributed label (if null, fall back to the regular label). */
    private transient AttributedString attributed_Label;

    /**
     * The description (not currently used - could be displayed as a tool tip).
     */
    private String description;

    /** The tool tip text. */
    private String tool_Tip_Text;

    /** The url text. */
    private String url_Text;

    /** A flag that controls whether or not the shape is visible. */
    private boolean shape_Visible;

    /** The shape. */
    private transient Shape shape;

    /** A flag that controls whether or not the shape is filled. */
    private boolean shape_Filled;

    /** The paint. */
    private transient Paint fill_Paint;

    /** A flag that controls whether or not the shape outline is visible. */
    private boolean shape_Outline_Visible;

    /** The outline paint. */
    private transient Paint outline_Paint;

    /** The outline stroke. */
    private transient Stroke outline_Stroke;

    /** A flag that controls whether or not the line is visible. */
    private boolean line_Visible;

    /** The line. */
    private transient Shape line;

    /** The stroke. */
    private transient Stroke line_Stroke;

    /** The line paint. */
    private transient Paint line_Paint;

    /**
     * The shape must be non-null for a LegendItem - if no shape is required,
     * use this.
     */
    private static final Shape UNUSED_SHAPE = new Line2D.Float();

    /**
     * The stroke must be non-null for a LegendItem - if no stroke is required,
     * use this.
     */
    private static final Stroke UNUSED_STROKE = new BasicStroke(0.0f);

    /**
     * Creates a legend item with the specified label.  The remaining
     * attributes take default values.
     *
     * @param label  the label ({@code null} not permitted).
     *
     * @since 1.0.10
     */
    public LegendItem(String label) {
        this(label, Color.BLACK);
    }

    /**
     * Creates a legend item with the specified label and fill paint.  The
     * remaining attributes take default values.
     *
     * @param label  the label ({@code null} not permitted).
     * @param paint  the paint ({@code null} not permitted).
     *
     * @since 1.0.12
     */
    public LegendItem(String label, Paint paint) {
        this(label, null, null, null, new Rectangle2D.Double(-4.0, -4.0, 8.0,
                8.0), paint);
    }

    /**
     * Creates a legend item with a filled shape.  The shape is not outlined,
     * and no line is visible.
     *
     * @param label  the label ({@code null} not permitted).
     * @param description  the description ({@code null} permitted).
     * @param toolTipText  the tool tip text ({@code null} permitted).
     * @param urlText  the URL text ({@code null} permitted).
     * @param shape  the shape ({@code null} not permitted).
     * @param fillPaint  the paint used to fill the shape ({@code null}
     *                   not permitted).
     */
    public LegendItem(String label, String description,
                      String toolTipText, String urlText,
                      Shape shape, Paint fillPaint) {

        this(label, description, toolTipText, urlText,
                /* shape visible = */ true, shape,
                /* shape filled = */ true, fillPaint,
                /* shape outlined */ false, Color.BLACK, UNUSED_STROKE,
                /* line visible */ false, UNUSED_SHAPE, UNUSED_STROKE,
                Color.BLACK);

    }

    /**
     * Creates a legend item with a filled and outlined shape.
     *
     * @param label  the label ({@code null} not permitted).
     * @param description  the description ({@code null} permitted).
     * @param toolTipText  the tool tip text ({@code null} permitted).
     * @param urlText  the URL text ({@code null} permitted).
     * @param shape  the shape ({@code null} not permitted).
     * @param fillPaint  the paint used to fill the shape ({@code null}
     *                   not permitted).
     * @param outlineStroke  the outline stroke ({@code null} not
     *                       permitted).
     * @param outlinePaint  the outline paint ({@code null} not
     *                      permitted).
     */
    public LegendItem(String label, String description, String toolTipText, 
            String urlText, Shape shape, Paint fillPaint,
            Stroke outlineStroke, Paint outlinePaint) {

        this(label, description, toolTipText, urlText,
                /* shape visible = */ true, shape,
                /* shape filled = */ true, fillPaint,
                /* shape outlined = */ true, outlinePaint, outlineStroke,
                /* line visible */ false, UNUSED_SHAPE, UNUSED_STROKE,
                Color.BLACK);

    }

    /**
     * Creates a legend item using a line.
     *
     * @param label  the label ({@code null} not permitted).
     * @param description  the description ({@code null} permitted).
     * @param toolTipText  the tool tip text ({@code null} permitted).
     * @param urlText  the URL text ({@code null} permitted).
     * @param line  the line ({@code null} not permitted).
     * @param lineStroke  the line stroke ({@code null} not permitted).
     * @param linePaint  the line paint ({@code null} not permitted).
     */
    public LegendItem(String label, String description, String toolTipText, 
            String urlText, Shape line, Stroke lineStroke, Paint linePaint) {

        this(label, description, toolTipText, urlText,
                /* shape visible = */ false, UNUSED_SHAPE,
                /* shape filled = */ false, Color.BLACK,
                /* shape outlined = */ false, Color.BLACK, UNUSED_STROKE,
                /* line visible = */ true, line, lineStroke, linePaint);
    }

    /**
     * Creates a new legend item.
     *
     * @param label  the label ({@code null} not permitted).
     * @param description  the description (not currently used,
     *        <code>null</code> permitted).
     * @param toolTipText  the tool tip text (<code>null</code> permitted).
     * @param urlText  the URL text (<code>null</code> permitted).
     * @param shapeVisible  a flag that controls whether or not the shape is
     *                      displayed.
     * @param shape  the shape (<code>null</code> permitted).
     * @param shapeFilled  a flag that controls whether or not the shape is
     *                     filled.
     * @param fillPaint  the fill paint (<code>null</code> not permitted).
     * @param shapeOutlineVisible  a flag that controls whether or not the
     *                             shape is outlined.
     * @param outlinePaint  the outline paint (<code>null</code> not permitted).
     * @param outlineStroke  the outline stroke (<code>null</code> not
     *                       permitted).
     * @param lineVisible  a flag that controls whether or not the line is
     *                     visible.
     * @param line  the line.
     * @param lineStroke  the stroke (<code>null</code> not permitted).
     * @param linePaint  the line paint (<code>null</code> not permitted).
     */
    public LegendItem(String label, String description,
                      String toolTipText, String urlText,
                      boolean shapeVisible, Shape shape,
                      boolean shapeFilled, Paint fillPaint,
                      boolean shapeOutlineVisible, Paint outlinePaint,
                      Stroke outlineStroke,
                      boolean lineVisible, Shape line,
                      Stroke lineStroke, Paint linePaint) {

        ParamChecks.nullNotPermitted(label, "label");
        ParamChecks.nullNotPermitted(fillPaint, "fillPaint");
        ParamChecks.nullNotPermitted(lineStroke, "lineStroke");
        ParamChecks.nullNotPermitted(line, "line");
        ParamChecks.nullNotPermitted(linePaint, "linePaint");
        ParamChecks.nullNotPermitted(outlinePaint, "outlinePaint");
        ParamChecks.nullNotPermitted(outlineStroke, "outlineStroke");

        this.label = label;
        this.label_Paint = null;
        this.attributed_Label = null;
        this.description = description;
        this.shape_Visible = shapeVisible;
        this.shape = shape;
        this.shape_Filled = shapeFilled;
        this.fill_Paint = fillPaint;
        this.shape_Outline_Visible = shapeOutlineVisible;
        this.outline_Paint = outlinePaint;
        this.outline_Stroke = outlineStroke;
        this.line_Visible = lineVisible;
        this.line = line;
        this.line_Stroke = lineStroke;
        this.line_Paint = linePaint;
        this.tool_Tip_Text = toolTipText;
        this.url_Text = urlText;
    }

    /**
     * Creates a legend item with a filled shape.  The shape is not outlined,
     * and no line is visible.
     *
     * @param label  the label (<code>null</code> not permitted).
     * @param description  the description (<code>null</code> permitted).
     * @param toolTipText  the tool tip text (<code>null</code> permitted).
     * @param urlText  the URL text (<code>null</code> permitted).
     * @param shape  the shape (<code>null</code> not permitted).
     * @param fillPaint  the paint used to fill the shape (<code>null</code>
     *                   not permitted).
     */
    public LegendItem(AttributedString label, String description, 
            String toolTipText, String urlText, Shape shape, Paint fillPaint) {

        this(label, description, toolTipText, urlText,
                /* shape visible = */ true, shape,
                /* shape filled = */ true, fillPaint,
                /* shape outlined = */ false, Color.BLACK, UNUSED_STROKE,
                /* line visible = */ false, UNUSED_SHAPE, UNUSED_STROKE,
                Color.BLACK);

    }

    /**
     * Creates a legend item with a filled and outlined shape.
     *
     * @param label  the label (<code>null</code> not permitted).
     * @param description  the description (<code>null</code> permitted).
     * @param toolTipText  the tool tip text (<code>null</code> permitted).
     * @param urlText  the URL text (<code>null</code> permitted).
     * @param shape  the shape (<code>null</code> not permitted).
     * @param fillPaint  the paint used to fill the shape (<code>null</code>
     *                   not permitted).
     * @param outlineStroke  the outline stroke (<code>null</code> not
     *                       permitted).
     * @param outlinePaint  the outline paint (<code>null</code> not
     *                      permitted).
     */
    public LegendItem(AttributedString label, String description,
            String toolTipText, String urlText, Shape shape, Paint fillPaint,
            Stroke outlineStroke, Paint outlinePaint) {

        this(label, description, toolTipText, urlText,
                /* shape visible = */ true, shape,
                /* shape filled = */ true, fillPaint,
                /* shape outlined = */ true, outlinePaint, outlineStroke,
                /* line visible = */ false, UNUSED_SHAPE, UNUSED_STROKE,
                Color.BLACK);
    }

    /**
     * Creates a legend item using a line.
     *
     * @param label  the label (<code>null</code> not permitted).
     * @param description  the description (<code>null</code> permitted).
     * @param toolTipText  the tool tip text (<code>null</code> permitted).
     * @param urlText  the URL text (<code>null</code> permitted).
     * @param line  the line (<code>null</code> not permitted).
     * @param lineStroke  the line stroke (<code>null</code> not permitted).
     * @param linePaint  the line paint (<code>null</code> not permitted).
     */
    public LegendItem(AttributedString label, String description,
            String toolTipText, String urlText, Shape line, Stroke lineStroke, 
            Paint linePaint) {

        this(label, description, toolTipText, urlText,
                /* shape visible = */ false, UNUSED_SHAPE,
                /* shape filled = */ false, Color.BLACK,
                /* shape outlined = */ false, Color.BLACK, UNUSED_STROKE,
                /* line visible = */ true, line, lineStroke, linePaint);
    }

    /**
     * Creates a new legend item.
     *
     * @param label  the label (<code>null</code> not permitted).
     * @param description  the description (not currently used,
     *        <code>null</code> permitted).
     * @param toolTipText  the tool tip text (<code>null</code> permitted).
     * @param urlText  the URL text (<code>null</code> permitted).
     * @param shapeVisible  a flag that controls whether or not the shape is
     *                      displayed.
     * @param shape  the shape (<code>null</code> permitted).
     * @param shapeFilled  a flag that controls whether or not the shape is
     *                     filled.
     * @param fillPaint  the fill paint (<code>null</code> not permitted).
     * @param shapeOutlineVisible  a flag that controls whether or not the
     *                             shape is outlined.
     * @param outlinePaint  the outline paint (<code>null</code> not permitted).
     * @param outlineStroke  the outline stroke (<code>null</code> not
     *                       permitted).
     * @param lineVisible  a flag that controls whether or not the line is
     *                     visible.
     * @param line  the line (<code>null</code> not permitted).
     * @param lineStroke  the stroke (<code>null</code> not permitted).
     * @param linePaint  the line paint (<code>null</code> not permitted).
     */
    public LegendItem(AttributedString label, String description, 
            String toolTipText, String urlText, boolean shapeVisible, 
            Shape shape, boolean shapeFilled, Paint fillPaint,
            boolean shapeOutlineVisible, Paint outlinePaint, 
            Stroke outlineStroke, boolean lineVisible, Shape line, 
            Stroke lineStroke, Paint linePaint) {

        ParamChecks.nullNotPermitted(label, "label");
        ParamChecks.nullNotPermitted(fillPaint, "fillPaint");
        ParamChecks.nullNotPermitted(lineStroke, "lineStroke");
        ParamChecks.nullNotPermitted(line, "line");
        ParamChecks.nullNotPermitted(linePaint, "linePaint");
        ParamChecks.nullNotPermitted(outlinePaint, "outlinePaint");
        ParamChecks.nullNotPermitted(outlineStroke, "outlineStroke");

        this.label = characterIteratorToString(label.getIterator());
        this.attributed_Label = label;
        this.description = description;
        this.shape_Visible = shapeVisible;
        this.shape = shape;
        this.shape_Filled = shapeFilled;
        this.fill_Paint = fillPaint;
        this.shape_Outline_Visible = shapeOutlineVisible;
        this.outline_Paint = outlinePaint;
        this.outline_Stroke = outlineStroke;
        this.line_Visible = lineVisible;
        this.line = line;
        this.line_Stroke = lineStroke;
        this.line_Paint = linePaint;
        this.tool_Tip_Text = toolTipText;
        this.url_Text = urlText;
    }

    /**
     * Returns a string containing the characters from the given iterator.
     *
     * @param iterator  the iterator (<code>null</code> not permitted).
     *
     * @return A string.
     */
    private String characterIteratorToString(CharacterIterator iterator) {
        int endIndex = iterator.getEndIndex();
        int beginIndex = iterator.getBeginIndex();
        int count = endIndex - beginIndex;
        if (count <= 0) {
            return "";
        }
        char[] chars = new char[count];
        int i = 0;
        char c = iterator.first();
        while (c != CharacterIterator.DONE) {
            chars[i] = c;
            i++;
            c = iterator.next();
        }
        return new String(chars);
    }

    /**
     * Returns the dataset.
     *
     * @return The dataset.
     *
     * @since 1.0.6
     *
     * @see #setDatasetIndex(int)
     */
    public Dataset getDataset() {
        return this.dataset;
    }

    /**
     * Sets the dataset.
     *
     * @param dataset  the dataset.
     *
     * @since 1.0.6
     */
    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    /**
     * Returns the dataset index for this legend item.
     *
     * @return The dataset index.
     *
     * @since 1.0.2
     *
     * @see #setDatasetIndex(int)
     * @see #getDataset()
     */
    public int getDatasetIndex() {
        return this.dataset_Index;
    }

    /**
     * Sets the dataset index for this legend item.
     *
     * @param index  the index.
     *
     * @since 1.0.2
     *
     * @see #getDatasetIndex()
     */
    public void setDatasetIndex(int index) {
        this.dataset_Index = index;
    }

    /**
     * Returns the series key.
     *
     * @return The series key.
     *
     * @since 1.0.6
     *
     * @see #setSeriesKey(Comparable)
     */
    public Comparable getSeriesKey() {
        return this.series_Key;
    }

    /**
     * Sets the series key.
     *
     * @param key  the series key.
     *
     * @since 1.0.6
     */
    public void setSeriesKey(Comparable key) {
        this.series_Key = key;
    }

    /**
     * Returns the series index for this legend item.
     *
     * @return The series index.
     *
     * @since 1.0.2
     */
    public int getSeriesIndex() {
        return this.series;
    }

    /**
     * Sets the series index for this legend item.
     *
     * @param index  the index.
     *
     * @since 1.0.2
     */
    public void setSeriesIndex(int index) {
        this.series = index;
    }

    /**
     * Returns the label.
     *
     * @return The label (never <code>null</code>).
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Returns the label font.
     *
     * @return The label font (possibly <code>null</code>).
     *
     * @since 1.0.11
     */
    public Font getLabelFont() {
        return this.label_Font;
    }

    /**
     * Sets the label font.
     *
     * @param font  the font (<code>null</code> permitted).
     *
     * @since 1.0.11
     */
    public void setLabelFont(Font font) {
        this.label_Font = font;
    }

    /**
     * Returns the paint used to draw the label.
     *
     * @return The paint (possibly <code>null</code>).
     *
     * @since 1.0.11
     */
    public Paint getLabelPaint() {
        return this.label_Paint;
    }

    /**
     * Sets the paint used to draw the label.
     *
     * @param paint  the paint (<code>null</code> permitted).
     *
     * @since 1.0.11
     */
    public void setLabelPaint(Paint paint) {
        this.label_Paint = paint;
    }

    /**
     * Returns the attributed label.
     *
     * @return The attributed label (possibly <code>null</code>).
     */
    public AttributedString getAttributedLabel() {
        return this.attributed_Label;
    }

    /**
     * Returns the description for the legend item.
     *
     * @return The description (possibly <code>null</code>).
     *
     * @see #setDescription(java.lang.String)
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description for this legend item.
     *
     * @param text  the description (<code>null</code> permitted).
     *
     * @see #getDescription()
     * @since 1.0.14
     */
    public void setDescription(String text) {
        this.description = text;
    }

    /**
     * Returns the tool tip text.
     *
     * @return The tool tip text (possibly <code>null</code>).
     *
     * @see #setToolTipText(java.lang.String)
     */
    public String getToolTipText() {
        return this.tool_Tip_Text;
    }

    /**
     * Sets the tool tip text for this legend item.
     *
     * @param text  the text (<code>null</code> permitted).
     *
     * @see #getToolTipText()
     * @since 1.0.14
     */
    public void setToolTipText(String text) {
        this.tool_Tip_Text = text;
    }

    /**
     * Returns the URL text.
     *
     * @return The URL text (possibly <code>null</code>).
     *
     * @see #setURLText(java.lang.String)
     */
    public String getURLText() {
        return this.url_Text;
    }

    /**
     * Sets the URL text.
     *
     * @param text  the text (<code>null</code> permitted).
     *
     * @see #getURLText()
     *
     * @since 1.0.14
     */
    public void setURLText(String text) {
        this.url_Text = text;
    }

    /**
     * Returns a flag that indicates whether or not the shape is visible.
     *
     * @return A boolean.
     *
     * @see #setShapeVisible(boolean)
     */
    public boolean isShapeVisible() {
        return this.shape_Visible;
    }

    /**
     * Sets the flag that controls whether or not the shape is visible.
     *
     * @param visible  the new flag value.
     *
     * @see #isShapeVisible()
     * @see #isLineVisible()
     *
     * @since 1.0.14
     */
    public void setShapeVisible(boolean visible) {
        this.shape_Visible = visible;
    }

    /**
     * Returns the shape used to label the series represented by this legend
     * item.
     *
     * @return The shape (never <code>null</code>).
     *
     * @see #setShape(java.awt.Shape)
     */
    public Shape getShape() {
        return this.shape;
    }

    /**
     * Sets the shape for the legend item.
     *
     * @param shape  the shape (<code>null</code> not permitted).
     *
     * @see #getShape()
     * @since 1.0.14
     */
    public void setShape(Shape shape) {
        ParamChecks.nullNotPermitted(shape, "shape");
        this.shape = shape;
    }

    /**
     * Returns a flag that controls whether or not the shape is filled.
     *
     * @return A boolean.
     */
    public boolean isShapeFilled() {
        return this.shape_Filled;
    }

    /**
     * Returns the fill paint.
     *
     * @return The fill paint (never <code>null</code>).
     */
    public Paint getFillPaint() {
        return this.fill_Paint;
    }

    /**
     * Sets the fill paint.
     *
     * @param paint  the paint (<code>null</code> not permitted).
     *
     * @since 1.0.11
     */
    public void setFillPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.fill_Paint = paint;
    }

    /**
     * Returns the flag that controls whether or not the shape outline
     * is visible.
     *
     * @return A boolean.
     */
    public boolean isShapeOutlineVisible() {
        return this.shape_Outline_Visible;
    }

    /**
     * Returns the line stroke for the series.
     *
     * @return The stroke (never <code>null</code>).
     */
    public Stroke getLineStroke() {
        return this.line_Stroke;
    }

    /**
     * Sets the line stroke.
     * 
     * @param stroke  the stroke (<code>null</code> not permitted).
     * 
     * @since 1.0.18
     */
    public void setLineStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.line_Stroke = stroke;
    }

    /**
     * Returns the paint used for lines.
     *
     * @return The paint (never <code>null</code>).
     */
    public Paint getLinePaint() {
        return this.line_Paint;
    }

    /**
     * Sets the line paint.
     *
     * @param paint  the paint (<code>null</code> not permitted).
     *
     * @since 1.0.11
     */
    public void setLinePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.line_Paint = paint;
    }

    /**
     * Returns the outline paint.
     *
     * @return The outline paint (never <code>null</code>).
     */
    public Paint getOutlinePaint() {
        return this.outline_Paint;
    }

    /**
     * Sets the outline paint.
     *
     * @param paint  the paint (<code>null</code> not permitted).
     *
     * @since 1.0.11
     */
    public void setOutlinePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.outline_Paint = paint;
    }

    /**
     * Returns the outline stroke.
     *
     * @return The outline stroke (never <code>null</code>).
     *
     * @see #setOutlineStroke(java.awt.Stroke)
     */
    public Stroke getOutlineStroke() {
        return this.outline_Stroke;
    }

    /**
     * Sets the outline stroke.
     *
     * @param stroke  the stroke (<code>null</code> not permitted).
     *
     * @see #getOutlineStroke()
     *
     * @since 1.0.14
     */
    public void setOutlineStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.outline_Stroke = stroke;
    }

    /**
     * Returns a flag that indicates whether or not the line is visible.
     *
     * @return A boolean.
     *
     * @see #setLineVisible(boolean)
     */
    public boolean isLineVisible() {
        return this.line_Visible;
    }

    /**
     * Sets the flag that controls whether or not the line shape is visible for
     * this legend item.
     *
     * @param visible  the new flag value.
     *
     * @see #isLineVisible()
     * @since 1.0.14
     */
    public void setLineVisible(boolean visible) {
        this.line_Visible = visible;
    }

    /**
     * Returns the line.
     *
     * @return The line (never <code>null</code>).
     *
     * @see #setLine(java.awt.Shape)
     * @see #isLineVisible()
     */
    public Shape getLine() {
        return this.line;
    }

    /**
     * Sets the line.
     *
     * @param line  the line (<code>null</code> not permitted).
     *
     * @see #getLine()
     * @since 1.0.14
     */
    public void setLine(Shape line) {
        ParamChecks.nullNotPermitted(line, "line");
        this.line = line;
    }

    /**
     * Tests this item for equality with an arbitrary object.
     *
     * @param obj  the object (<code>null</code> permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LegendItem)) {
            return false;
        }
        LegendItem that = (LegendItem) obj;
        if (this.dataset_Index != that.dataset_Index) {
            return false;
        }
        if (this.series != that.series) {
            return false;
        }
        if (!this.label.equals(that.label)) {
            return false;
        }
        if (!ObjectUtils.equal(this.description, that.description)) {
            return false;
        }
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
        if (!this.outline_Stroke.equals(that.outline_Stroke)) {
            return false;
        }
        if (!this.line_Visible == that.line_Visible) {
            return false;
        }
        if (!ShapeUtils.equal(this.line, that.line)) {
            return false;
        }
        if (!this.line_Stroke.equals(that.line_Stroke)) {
            return false;
        }
        if (!ObjectUtils.equal(this.label_Font, that.label_Font)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.series_Key != null ? this.series_Key.hashCode() : 0);
        hash = 47 * hash + this.dataset_Index;
        return hash;
    }

    /**
     * Returns an independent copy of this object (except that the clone will
     * still reference the same dataset as the original
     * <code>LegendItem</code>).
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if the legend item cannot be cloned.
     *
     * @since 1.0.10
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        LegendItem clone = (LegendItem) super.clone();
        if (this.series_Key instanceof PublicCloneable) {
            PublicCloneable pc = (PublicCloneable) this.series_Key;
            clone.series_Key = (Comparable) pc.clone();
        }
        // FIXME: Clone the attributed string if it is not null
        clone.shape = ShapeUtils.clone(this.shape);
        clone.line = ShapeUtils.clone(this.line);
        return clone;
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the output stream (<code>null</code> not permitted).
     *
     * @throws IOException  if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtils.writeAttributedString(this.attributed_Label, stream);
        SerialUtils.writeShape(this.shape, stream);
        SerialUtils.writePaint(this.fill_Paint, stream);
        SerialUtils.writeStroke(this.outline_Stroke, stream);
        SerialUtils.writePaint(this.outline_Paint, stream);
        SerialUtils.writeShape(this.line, stream);
        SerialUtils.writeStroke(this.line_Stroke, stream);
        SerialUtils.writePaint(this.line_Paint, stream);
        SerialUtils.writePaint(this.label_Paint, stream);
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the input stream (<code>null</code> not permitted).
     *
     * @throws IOException  if there is an I/O error.
     * @throws ClassNotFoundException  if there is a classpath problem.
     */
    private void readObject(ObjectInputStream stream)
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.attributed_Label = SerialUtils.readAttributedString(stream);
        this.shape = SerialUtils.readShape(stream);
        this.fill_Paint = SerialUtils.readPaint(stream);
        this.outline_Stroke = SerialUtils.readStroke(stream);
        this.outline_Paint = SerialUtils.readPaint(stream);
        this.line = SerialUtils.readShape(stream);
        this.line_Stroke = SerialUtils.readStroke(stream);
        this.line_Paint = SerialUtils.readPaint(stream);
        this.label_Paint = SerialUtils.readPaint(stream);
    }

}
