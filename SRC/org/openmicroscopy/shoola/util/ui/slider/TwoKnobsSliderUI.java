/*
 * org.openmicroscopy.shoola.util.ui.slider.TwoKnobsSliderUI
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006 University of Dundee. All rights reserved.
 *
 *
 * 	This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */

package org.openmicroscopy.shoola.util.ui.slider;




//Java imports
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicSliderUI;


//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.util.ui.IconManager;
import org.openmicroscopy.shoola.util.ui.UIUtilities;
import org.openmicroscopy.shoola.util.ui.colourpicker.ColourSlider;

/** 
* The UI delegate for the {@link TwoKnobsSlider}.
* A delegate can't be shared among different instances of
* {@link TwoKnobsSlider} and has a life-time dependency with its owning slider.
*
* @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
* 				<a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
* @author	Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
* 				<a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
* @version 3.0
* <small>
* (<b>Internal version:</b> $Revision: $ $Date: $)
* </small>
* @since OME2.2
*/
class TwoKnobsSliderUI
{
	/** Static variable holding the colour of the track border. */
	private final static Color	TRACK_BORDER_COLOUR = new Color(128, 128, 128);

	/** Extra space added to the track. */
	static final int            	EXTRA = 3;

	/** Space added to paint the label. */
	static final int            	BUFFER = 1;

	/** The default color of the track. */
	private static final Color  	TRACK_COLOR = Color.LIGHT_GRAY;

	/** Default size of a thumbnail i.e. 16x16. */
	private static final Dimension	DEFAULT_THUMB_SIZE = new Dimension(16, 16);

	/** The component that owns this delegate. */
	private TwoKnobsSlider          component;

	/** Reference to the model. */
	private TwoKnobsSliderModel     model;

	/** The rectangle hosting the track and the two knobs. */
	private Rectangle               trackRect;

	/** The rectangle hosting the ticks. */
	private Rectangle               tickRect;

	/** The rectangle hosting the label. */
	private Rectangle               labelRect;

	/** The color the track. */
	private Color                   shadowColor;

	/** The color of the font. */
	private Color                   fontColor;

	/** The image used to draw the thumb.  */
	private Image 					thumbImage;
	
	/** The image used to draw the arrow thumb.  */
	private Image 					upArrowImage;

	/** The image used to draw the disabled arrow thumb.  */
	private Image 					disabledUpArrowImage;
	
	/** The image used to draw the thumb when the slider is disabled.  */
	private Image 					disabledThumbImage;

	/** The colour of the border for the track. */
	private Color 					trackBorderColour;
	
	/** Start of the gradient. */
	private Color					RGBStart;
	 
	/** The end of the gradient. */
	private Color					RGBEnd;
	
	/** Initializes the components. */
	private void initialize()
	{
		trackRect = new Rectangle();
		tickRect = new Rectangle();
		labelRect = new Rectangle();
		shadowColor = UIManager.getColor("Slider.shadow");
		fontColor = UIUtilities.LINE_COLOR;
		trackBorderColour = TRACK_BORDER_COLOUR;
	}

	/** Loads the thumb for the two knob slider.  */
	private void createThumbImage()
	{
		// Create the thumb image 
		IconManager icons = IconManager.getInstance();
		ImageIcon icon = icons.getImageIcon(IconManager.THUMB);
		thumbImage = icon.getImage();
		icon = icons.getImageIcon(IconManager.THUMB_DISABLED);
		disabledThumbImage = icon.getImage();
		icon = icons.getImageIcon(IconManager.UP_ARROW_10);
		upArrowImage = icon.getImage();
		icon = icons.getImageIcon(IconManager.UP_ARROW_DISABLED_10);
		disabledUpArrowImage = icon.getImage();

		/*
		 * I will come back to list later to fix a nicer graphic. 
		 * 
		 * Graphics2D bg = thumbImage.createGraphics();
     	bg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
     			RenderingHints.VALUE_ANTIALIAS_ON);

     	bg.setColor(new Color(255,255,255,0));
		bg.fillRect(0, 0, thumbWidth, thumbHeight);

     	// set the background of the image to white
     	bg.setColor(Color.WHITE);
		bg.fillRoundRect(0, 0, thumbWidth, thumbHeight, thumbHeight,
				thumbHeight);

		// Create the gradient paint for the first layer of the button
		Color gradientStart =  KNOB_COLOR;
		Color gradientEnd = KNOB_COLOR.darker();

		Paint vPaint = new GradientPaint(0, 0, gradientStart, 0, 
				thumbHeight, gradientEnd, false);

		bg.setPaint(vPaint);
		//	Paint the first layer of the button

		bg.fillRoundRect(0, 0, thumbWidth, thumbHeight, thumbHeight, 
				thumbHeight);

		// Calulate the size of the second layer of the button
		int highlightInset = 1;
		int thumbHighlightHeight = thumbHeight-(highlightInset*2);
		int thumbHighlightWidth = thumbWidth-(highlightInset * 2);
		int highlightArcSize = thumbHighlightHeight;

		// Create the paint for the second layer of the button
		gradientStart = Color.WHITE;
		gradientEnd = KNOB_HIGHLIGHT_COLOR.brighter();
		vPaint = new GradientPaint(0,1, gradientStart, 0,
				(thumbHighlightHeight/2), 
				KNOB_COLOR.brighter(), false);

		// Paint the second layer of the button
		bg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,.8f));
		bg.setPaint(vPaint);
		bg.setClip(new RoundRectangle2D.Float(highlightInset, 0,
				thumbHighlightWidth, thumbHighlightHeight/2,
				thumbHighlightHeight/3, thumbHighlightHeight/3));
		bg.fillRoundRect(highlightInset, 1, thumbHighlightWidth,
				thumbHighlightHeight, highlightArcSize, highlightArcSize);
		 */
	}


	/**
	 * Paints the ticks.
	 * 
	 * @param g The graphics context.
	 */
	private void paintTicks(Graphics2D g)
	{
		g.setColor(shadowColor);
		g.translate(0, tickRect.y);
		int value = model.getMinimum();
		int xPos = 0;
		int minor = model.getMinorTickSpacing();
		int major = model.getMajorTickSpacing();
		int max = model.getMaximum();
		int min = model.getMinimum();
		if (model.getOrientation() == TwoKnobsSlider.HORIZONTAL) {
			if (minor > 0) {
				while (value <= max) {
					xPos = xPositionForValue(value);
					paintMinorTickForHorizSlider(g, tickRect, xPos);
					value += minor;
				}
			}
			if (major > 0) {
				value = min;
				while (value <= max) {
					xPos = xPositionForValue(value );
					paintMajorTickForHorizSlider(g, tickRect, xPos);
					value += major;
				}
			}
			g.translate(0, -tickRect.y);
		} else {
			g.translate(tickRect.x, 0);
			value = min;
			int yPos = 0;
			if (minor > 0) {
				while (value <= max) {
					yPos = yPositionForValue(value);
					paintMinorTickForVertSlider(g, tickRect, yPos);
					value += minor;
				}
			}
			if (major > 0) {
				value = min;
				while (value <= max) {
					yPos = yPositionForValue(value);
					paintMajorTickForVertSlider( g, tickRect, yPos);
					value += major;
				}
			}
			g.translate(-tickRect.x, 0);
		}    
	}

	/**
	 * Paints the labels.
	 * 
	 * @param g             The graphics context.
	 * @param fontMetrics   Information on how to render the font.
	 */
	private void paintLabels(Graphics2D g, FontMetrics fontMetrics)
	{
		g.setColor(fontColor);
		Map labels = model.getLabels();
		Iterator i = labels.keySet().iterator();
		Integer key;
		int value;
		while (i.hasNext()) {
			key = (Integer) i.next();
			value = key.intValue();
			if (model.getOrientation() == TwoKnobsSlider.HORIZONTAL) {
				g.translate(0, labelRect.y);
				paintHorizontalLabel(g, fontMetrics, value);
				g.translate(0, -labelRect.y);
			} else {
				g.translate(labelRect.x, 0);
				paintVerticalLabel(g, fontMetrics, value);
				g.translate(-labelRect.x, 0);
			}
		}
	}

	/**
	 * Paints the label for an horizontal slider.
	 * 
	 * @param g             The graphics context.
	 * @param fontMetrics   Information on how to render the font.
	 * @param value         The value to paint.
	 */
	private void paintHorizontalLabel(Graphics2D g, FontMetrics fontMetrics, 
			int value)
	{
		String s = ""+value;
		int labelLeft = xPositionForValue(value)-fontMetrics.stringWidth(s)/2;
		g.translate(labelLeft, 0);
		g.drawString(s, 0, fontMetrics.getHeight());
		g.translate(-labelLeft, 0);
	}

	/**
	 * Paints the label for a vertical slider.
	 * 
	 * @param g             The graphics context.
	 * @param fontMetrics   Information on how to render the font.
	 * @param value         The value to paint.
	 */
	private void paintVerticalLabel(Graphics2D g, FontMetrics fontMetrics,
			int value)
	{
		int v = fontMetrics.getHeight()/2+(fontMetrics.getHeight()/2)%2;
		int labelTop = yPositionForValue(value)-v;
		g.translate(0, labelTop);
		g.drawString(""+value, 0, fontMetrics.getHeight());
		g.translate(0, -labelTop);
	}

	/**
	 * Paints the minor tick for an horizontal slider.
	 * 
	 * @param g         The graphics context.
	 * @param bounds    The bounds of the tick box.
	 * @param x         The x-position.
	 */
	private void paintMinorTickForHorizSlider(Graphics2D g, Rectangle bounds,
			int x)
	{
		g.drawLine(x, 0, x, bounds.height/2-1);
	}

	/**
	 * Paints the major tick for an horizontal slider.
	 * 
	 * @param g         The graphics context.
	 * @param bounds    The bounds of the tick box.
	 * @param x         The x-position.
	 */
	private void paintMajorTickForHorizSlider(Graphics2D g, Rectangle bounds,
											int x)
	{
		g.drawLine(x, 0, x, bounds.height-2);
	}

	/**
	 * Paints the minor tick for an horizontal slider.
	 * 
	 * @param g         The graphics context.
	 * @param bounds    The bounds of the tick box.
	 * @param y         The y-position.
	 */
	private void paintMinorTickForVertSlider(Graphics2D g, Rectangle bounds,
												int y)
	{
		g.drawLine(0, y, bounds.width/2-1, y);
	}

	/**
	 * Paints the major tick for an vertical slider.
	 * 
	 * @param g         The graphics context.
	 * @param bounds    The bounds of the tick box.
	 * @param y         The y-position.
	 */
	private void paintMajorTickForVertSlider(Graphics2D g, Rectangle bounds,
			int y)
	{
		g.drawLine(0, y, bounds.width-2, y);
	}

	/**
	 * Paints the track and the knobs for an horizontal slider.
	 * 
	 * @param g2D The graphic context.
	 */
	private void paintTrackAndKnobsForHorizSlider(Graphics2D g2D)
	{
		int l = xPositionForValue(model.getStartValue());
		int r = xPositionForValue(model.getEndValue());
		if (!this.component.getColourGradient())
		{
			Paint paint = new GradientPaint(0, trackRect.y, 
				UIUtilities.TRACK_GRADIENT_START, 0, 
				trackRect.y+trackRect.height-10, 
				UIUtilities.TRACK_GRADIENT_END, false);
			g2D.setPaint(paint);
			g2D.fillRoundRect(trackRect.x, trackRect.y+3, trackRect.width,
					trackRect.height-12, trackRect.height/3, trackRect.height/3);
		}
		else
		{
			Paint paint = new GradientPaint((int) trackRect.getX(),
					 (int) trackRect.getY()-2,  component.getRGBStart(),
					 (int) trackRect.getWidth(),
					 (int) trackRect.getHeight()+2,component.getRGBEnd(), false);
		
			
			g2D.setPaint(paint);
			g2D.fillRoundRect(trackRect.x, trackRect.y+2, trackRect.width,
						trackRect.height-9, trackRect.height/3, trackRect.height/3);
			g2D.setColor(Color.black);
			g2D.drawRoundRect(trackRect.x, trackRect.y+2, trackRect.width,
					trackRect.height-9, trackRect.height/3, trackRect.height/3);
		}
		//Draw the knobs
		int w  = component.getKnobWidth();
		int h = component.getKnobHeight();
		Image img;
		int offset = 0;
		if(!component.getColourGradient())
			if (model.isEnabled()) 
				img = thumbImage;
			else
				img = disabledThumbImage;
		else
		{
			w = 12;
			h = 12;
			if (model.isEnabled()) 
				img = upArrowImage;
			else
				img = disabledUpArrowImage;
			offset = 5;	
		}
		if (component.getKnobControl() == TwoKnobsSlider.LEFT) 
		{
				g2D.drawImage(img, r-w/2, 1+offset, w, h, null);
				g2D.drawImage(img, l-w/2, 1+offset, w, h, null);
		} 
		else 
		{
				g2D.drawImage(img, l-w/2, 1+offset, w, h, null);
				g2D.drawImage(img, r-w/2, 1+offset, w, h, null);
		}
	}
	
	/**
	 * Paints the track and the knobs for a vertical slider.
	 * 
	 * @param g2D The graphic context.
	 */
	private void paintTrackAndKnobsForVertSlider(Graphics2D g2D)
	{
		int down = yPositionForValue(model.getStartValue());
		int up = yPositionForValue(model.getEndValue());
		int w = component.getKnobWidth();
		int h = component.getKnobHeight();
		int x = trackRect.x-w/2+(trackRect.width-w)/2;
		if(this.component.getColourGradient()==false)
		{
	
		Paint paint = new GradientPaint(trackRect.x+1, trackRect.y+h/2, 
				UIUtilities.TRACK_GRADIENT_START, 
				trackRect.x+1+trackRect.width-w-2, 
				trackRect.y+h/2, UIUtilities.TRACK_GRADIENT_END, false);

		g2D.setPaint(paint);
		g2D.fillRoundRect(trackRect.x+1, trackRect.y+h/2, trackRect.width-w-2,
				trackRect.height, trackRect.width/3, trackRect.width/3);
		}
		else
		{
			Paint paint = new GradientPaint(trackRect.x+1, trackRect.y+h/2, 
					component.RGBStart, 
					trackRect.x+1+trackRect.width-w-2, 
					trackRect.y+h/2, component.RGBEnd, false);
			g2D.setPaint(paint);
			g2D.fillRoundRect(trackRect.x, trackRect.y+3, trackRect.width,
						trackRect.height-12, trackRect.height/3, trackRect.height/3);
		}
	
		//Draw the knobs
		Image img;
	
		if(!component.getColourGradient())
			if (model.isEnabled()) 
				img = thumbImage;
			else
				img = disabledThumbImage;
		else
		{
			w = 10;
			h = 10;
			if (model.isEnabled()) 
				img = upArrowImage;
			else
				img = disabledUpArrowImage;
		}
		if (component.getKnobControl() == TwoKnobsSlider.LEFT) {
			g2D.drawImage(img, x, down, w, h, null);
			g2D.drawImage(img, x, up, w, h, null);
		} else {
			g2D.drawImage(img, x, up, w, h, null);
			g2D.drawImage(img, x, down, w, h, null);
		}
	}

	/**
	 * Determines the boundary of each rectangle composing the slider
	 * according to the font metrics and the dimension of the component.
	 * 
	 * @param fontMetrics   The font metrics.
	 * @param size          The dimension of the component.
	 */
	private void computeRectangles(FontMetrics fontMetrics, Dimension size)
	{ 
		int w = component.getKnobWidth();
		int h = component.getKnobHeight();
		//int fontWidth = 
		//    fontMetrics.stringWidth(model.render(model.getMaximum()));
		int fontWidth = 
			fontMetrics.stringWidth(model.render(model.getAbsoluteMaximum()));
		int x = 0;
		if (model.getOrientation() == TwoKnobsSlider.HORIZONTAL) {
			x = fontWidth/2;
			//x += w; //06-03
			if (model.isPaintEndLabels())
				trackRect.setBounds(x, EXTRA, size.width-2*x, h);
			else
				//trackRect.setBounds(w/2, EXTRA, size.width-2*w, h);
				trackRect.setBounds(w/2, EXTRA, size.width-w, h);

			if (model.isPaintTicks())
				tickRect = new Rectangle(trackRect.x,
						trackRect.y+trackRect.height,
						trackRect.width, trackRect.height);
			labelRect = new Rectangle(tickRect.x,
					trackRect.y+trackRect.height+tickRect.height,
					trackRect.width, 
					fontMetrics.getHeight()+2*BUFFER);
		} else {
			int y = fontMetrics.getHeight()/2+h;
			if (model.isPaintEndLabels())
				trackRect.setBounds(w/2, y, w+2*EXTRA, size.height-2*y);
			else
				//trackRect.setBounds(x+w-EXTRA, h/2, w+2*EXTRA, 
				//                    size.height-h-h/2);
				trackRect.setBounds(w/2, 0, w+2*EXTRA, size.height-h);
			if (model.isPaintTicks()) 
				tickRect = new Rectangle(trackRect.x+trackRect.width,
						trackRect.y-h, trackRect.width,
						trackRect.height);
			labelRect = new Rectangle(trackRect.x+trackRect.width+
					tickRect.width, trackRect.y, fontWidth+2*BUFFER,
					trackRect.height);
		}
	}

	/**
	 * Creates a new instance.
	 * 
	 * @param component The component that owns this uiDelegate. 
	 *                  Mustn't be <code>null</code>.
	 * @param model     Reference to the model. Mustn't be <code>null</code>.
	 */
	TwoKnobsSliderUI(TwoKnobsSlider component, TwoKnobsSliderModel model)
	{
		if (component == null) throw new NullPointerException("No component");
		if (model == null) throw new NullPointerException("No model");
		this.component = component;
		this.model = model;
		initialize();
		createThumbImage();
	}

	/**
	 * Returns the width of the image knob.
	 * 
	 * @return See above.
	 */
	int getKnobWidth()
	{
		if (thumbImage == null) return DEFAULT_THUMB_SIZE.width;
		return thumbImage.getWidth(null);
	}

	/**
	 * Returns the height of the image knob.
	 * 
	 * @return See above.
	 */
	int getKnobHeight()
	{
		if (thumbImage == null) return DEFAULT_THUMB_SIZE.height;
		return thumbImage.getHeight(null);
	}

	/**
	 * Sets the color of the font.
	 * 
	 * @param c The color to set.
	 */
	void setFontColor(Color c)
	{
		if (c == null) return;
		fontColor = c;
	}

	/**
	 * Determines the x-coordinate of the knob corresponding to the passed
	 * value.
	 * 
	 * @param value The value to map.
	 * @return See above.
	 */
	int xPositionForValue(int value)
	{
		int min = model.getPartialMinimum();
		int max = model.getPartialMaximum();
		int trackLength = trackRect.width;
		double valueRange = (double) max-(double) min;
		double pixelsPerValue = trackLength/valueRange;
		int trackLeft = trackRect.x;
		int trackRight = trackRect.x+trackRect.width-1;
		int xPosition = trackLeft;
		xPosition += Math.round(pixelsPerValue*((double) value-min));
		xPosition = Math.max(trackLeft, xPosition);
		xPosition = Math.min(trackRight, xPosition);
		return xPosition;
	}

	/**
	 * Determines the y-coordinate of the knob corresponding to the passed
	 * value.
	 * 
	 * @param value The value to map.
	 * @return See above.
	 */
	int yPositionForValue(int value)
	{
		int min = model.getPartialMinimum();
		int max = model.getPartialMaximum();
		int trackLength = trackRect.height; 
		double valueRange = (double) max-(double) min;
		double pixelsPerValue = trackLength/valueRange;
		int trackTop = trackRect.y;
		int trackBottom = trackRect.y+trackRect.height-1;
		int yPosition= trackTop;
		yPosition += Math.round(pixelsPerValue*((double) max-value));
		yPosition = Math.max(trackTop, yPosition);
		yPosition = Math.min(trackBottom, yPosition);
		return yPosition;
	}

	/**
	 * Determines the value corresponding to the passed x-coordinate.
	 * 
	 * @param xPosition The x-coordinate to map.
	 * @return See above.
	 */
	int xValueForPosition(int xPosition)
	{
		int value;
		int minValue = model.getPartialMinimum();
		int maxValue = model.getPartialMaximum();
		int trackLength = trackRect.width;
		int trackLeft = trackRect.x; 
		int trackRight = trackRect.x+trackRect.width-1;

		if (xPosition <= trackLeft)  value = minValue;
		else if (xPosition >= trackRight) value = maxValue;
		else {
			int distanceFromTrackLeft = xPosition-trackLeft;
			double valueRange = (double) maxValue-(double) minValue;
			double valuePerPixel = valueRange/trackLength;
			int valueFromTrackLeft = 
				(int) Math.round(distanceFromTrackLeft*valuePerPixel);
			value = minValue+valueFromTrackLeft;
		}
		return value;
	}

	/**
	 * Determines the value corresponding to the passed y-coordinate.
	 * 
	 * @param yPosition The y-coordinate to map.
	 * @return See above.
	 */
	int yValueForPosition(int yPosition)
	{
		int value;
		int minValue = model.getPartialMinimum();
		int maxValue = model.getPartialMaximum();
		int trackLength = trackRect.height;
		int trackTop = trackRect.y;
		int trackBottom = trackRect.y+trackRect.height-1;

		if (yPosition <= trackTop) value = maxValue;
		else if (yPosition >= trackBottom) value = minValue;
		else {
			int distanceFromTrackTop = yPosition-trackTop;
			double valueRange = (double) maxValue-(double) minValue;
			double valuePerPixel = valueRange/trackLength;
			int valueFromTrackTop = 
				(int)Math.round(distanceFromTrackTop*valuePerPixel);
			value = maxValue-valueFromTrackTop;
		}
		return value;
	}

	/**
	 * Paints the slider.
	 * 
	 * @param g2D   The graphics context.
	 * @param size  The dimension of the component.
	 */
	void paintComponent(Graphics2D g2D, Dimension size)
	{
		FontMetrics fontMetrics = g2D.getFontMetrics();
		computeRectangles(fontMetrics, size);
		//Draw the track
		g2D.setColor(TRACK_COLOR);
		if (model.getOrientation() == TwoKnobsSlider.HORIZONTAL)
			paintTrackAndKnobsForHorizSlider(g2D);
		else paintTrackAndKnobsForVertSlider(g2D);
		if (model.isPaintTicks()) paintTicks(g2D);
		if (model.isPaintLabels() || model.isPaintEndLabels()) 
			paintLabels(g2D, fontMetrics); 
	}

	private void paintThumb(Graphics2D g, int x, int y, int w, int h)
	{
        Rectangle knobBounds = new Rectangle(x,y,w,h);
      
        g.translate(knobBounds.x, knobBounds.y-2);
        
    
	  if (component.getOrientation() == TwoKnobsSlider.HORIZONTAL ) {
            int cw = w / 2;
            g.fillRect(1, cw, w-3, h-1-cw);
            g.setColor(UIUtilities.WINDOW_BACKGROUND_COLOR);
                Polygon p = new Polygon();
               // p.addPoint(1, h-cw);
                //p.addPoint(cw-1, h-1);
                //p.addPoint(w-2, h-1-cw);
                p.addPoint(1, h);
                p.addPoint(cw-1, h-cw);
                p.addPoint(w-2, h);
                g.fillPolygon(p);       
            
           /* g.setColor(UIUtilities.WINDOW_BACKGROUND_COLOR);
            g.drawLine(0, 0, w-2, 0);
            g.drawLine(0, 1, 0, h-1-cw);
            g.drawLine(0, h-cw, cw-1, h-1); 

            g.setColor(Color.black);
            g.drawLine(w-1, 0, w-1, h-2-cw);    
            g.drawLine(w-1, h-1-cw, w-1-cw, h-1);       

            g.setColor(shadowColor);
            g.drawLine(w-2, 1, w-2, h-2-cw);    
            g.drawLine(w-2, h-1-cw, w-1-cw, h-2);       */
        }
        else {  // vertical
            int cw = h / 2;
	 	  g.fillRect(1, 1, w-1-cw, h-3);
	          Polygon p = new Polygon();
                  p.addPoint(w-cw-1, 0);
                  p.addPoint(w-1, cw);
                  p.addPoint(w-1-cw, h-2);
                  g.fillPolygon(p);

                  g.setColor(UIUtilities.WINDOW_BACKGROUND_COLOR);
	          g.drawLine(0, 0, 0, h - 2);                  // left
	          g.drawLine(1, 0, w-1-cw, 0);                 // top
	          g.drawLine(w-cw-1, 0, w-1, cw);              // top slant

                  g.setColor(Color.black);
	          g.drawLine(0, h-1, w-2-cw, h-1);             // bottom
	          g.drawLine(w-1-cw, h-1, w-1, h-1-cw);        // bottom slant

                  g.setColor(shadowColor);
                  g.drawLine(1, h-2, w-2-cw,  h-2 );         // bottom
                  g.drawLine(w-1-cw, h-2, w-2, h-cw-1 );     // bottom slant
	    }
	 
        g.translate(-knobBounds.x, -knobBounds.y);
	}
	
}