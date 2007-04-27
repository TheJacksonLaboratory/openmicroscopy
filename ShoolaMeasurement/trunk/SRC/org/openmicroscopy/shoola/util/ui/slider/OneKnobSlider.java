/*
 * org.openmicroscopy.shoola.util.ui.slider.OneKnobSlider
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
import javax.swing.JSlider;

//Third-party libraries

//Application-internal dependencies

/** 
 * OneKnobSlider is an extension of the {@link JSlider}, 
 * it has a more <code>Aqua look and feel</code>, 
 * plus the addition of arrow buttons at the ends of the track which can 
 * increment the slider by one.
 * <p>
 * When the track is selected, the thumb will move to the point clicked, which
 * is different to the original.
 * </p>
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author	Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * 	<a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since OME2.2
 */
public class OneKnobSlider
	extends JSlider
{

	/** Show the arrows on the track if true. */
	private boolean		showArrows;
	
	/** Slider UI for new laf. */
	private OneKnobSliderUI	sliderUI;	
	
	/** This is set to <code>true</code> if the slider has tooltipString. */
	private boolean 	hasLabel;
	
	/** Tooltip string which is shown when slider is dragged, changed value. */
	private String 		endLabel;
	
	/** This value is set to true if the tip label will be displayed. */
	private	boolean		showTipLabel;
	
	/** This value is set to true if the end label will be displayed. */
	private boolean 	showEndLabel;
	
	/**
	 * Creates a new instance.
	 * 
	 * @param orientation  Orientation of slider.
	 * @param min          Minimum value for slider. 
	 * @param max          Maximum value for slider. 
	 * @param value        Value of slider. 
	 */
	public OneKnobSlider(int orientation, int min, int max, int value)
	{
		super();
		sliderUI = new OneKnobSliderUI(this);
		this.setUI(sliderUI);
		this.setOrientation(orientation);
		this.setMinimum(min);
		this.setMaximum(max);
		this.setValue(value);
		this.hasLabel = false;
		this.setSnapToTicks(false);
	}
	
	/** Creates a default slider.  */
	public OneKnobSlider()
	{
		this(OneKnobSlider.HORIZONTAL, 0, 1, 0);
	}
	
	/**
	 * Returns <code>true</code> if the  arrows on the track, 
     * <code>false</code> otherwise.
	 * 
	 * @return See above.
	 */
	public boolean isShowArrows() { return showArrows; }
	
	/**
	 * Shows the arrows on the track if the passed value is <code>true</code>,
     * hides otherwise.
	 * 
	 * @param isShow See above.
	 */
	public void setShowArrows(boolean isShow)
	{
		showArrows = isShow;
		sliderUI.setShowArrows(showArrows);
	}
	
	/**
	 * Sets the string for the tooltip which is displayed when slider changes
	 * value, as well as the label shown at the end of the text. 
	 * 
	 * @param label prefix data for the string to display.
	 */
	public void setEndLabel(String label)
	{
		endLabel = label;
		hasLabel = true;
		sliderUI.setEndLabel(label);
	}
	
	/**
	 * Returns the text used in the end Label.
	 *  
	 * @return see above.
	 */
	public String getEndLabel() { return endLabel; }
	
	/**
	 * Returns <code>true</code> if the component has an <code>endLabel</code>,
     * <code>false</code> otherwise.
	 * 
	 * @return See above. 
	 */
	public boolean hasEndLabel() { return hasLabel; }
	
    /**
     * Shows the end label if set to <code>true</code>, hides it 
     * <code>otherwise</code>.
     *  
     * @param show  Pass <code>true</code> to show the label, 
     *              <code>false</code> otherwise.
     */
	public void setShowEndLabel(boolean show)
	{
		showEndLabel = show;
		sliderUI.setShowEndLabel(show);
	}
	
    /**
     * Shows the tip label if set to <code>true</code>, hides it 
     * <code>otherwise</code>.
     *  
     * @param show Pass <code>true</code> to show the tip label, 
     *              <code>false</code> otherwise.
     */
	public void setShowTipLabel(boolean show)
	{
		showTipLabel = show;
		sliderUI.setShowTipLabel(show);
	}
	
	/**
	 * Returns <code>true</code> if the tip label will be displayed,
     * <code>false</code> otherwise.
	 * 
	 * @return See above.
	 */
	public boolean showTipLabel() { return showTipLabel; }
	
	/**
	 * Returns <code>true</code> if the end label will be displayed,
     * <code>false</code> otherwise.
	 * 
	 * @return See above.
	 */
	public boolean showEndLabel() { return showEndLabel; }
    
}
