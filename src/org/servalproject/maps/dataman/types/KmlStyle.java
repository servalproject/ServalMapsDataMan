/*
 * Copyright (C) 2012 The Serval Project
 *
 * This file is part of the Serval Maps Data Manipulator Software
 *
 * Serval Maps Data Manipulator Software is free software; you can 
 * redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation; either 
 * version 3 of the License, or (at your option) any later version.
 *
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.servalproject.maps.dataman.types;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.servalproject.maps.dataman.Utils;

/**
 * a utility class to represent the required information for
 * a style element in KML
 */
public class KmlStyle {
	
	/*
	 * public class level constants
	 */
	public final static String DEFAULT_COLOUR = "";
	public final static int    DEFAULT_WIDTH  = 1;
	
	/*
	 * private class level constants
	 */
	private final static String HEX_PATTERN = "^[A-Fa-f0-9]{8}$";
	
	/*
	 * private class level variables
	 */
	private String colour;
	private int width;
	
	private Pattern pattern;
	
	/**
	 * create a new KmlStyle object using the default style
	 */
	public KmlStyle() {
		colour = DEFAULT_COLOUR;
		width = DEFAULT_WIDTH;
		
		pattern = Pattern.compile(HEX_PATTERN);
	}
	
	/**
	 * create a new KmlStyle object using the style string
	 * received from the command line
	 * 
	 * @param styleString a comma separated list of key value pairs
	 * @throws ParseException if the style cannot be parsed
	 */
	public KmlStyle(String styleString) throws ParseException {
		
		// check the parameter
		if(Utils.isEmpty(styleString) == true) {
			throw new IllegalArgumentException("the styleString parameter is required");
		}
		
		pattern = Pattern.compile(HEX_PATTERN);
		
		// split the style string into its component parts
		String[] elems = styleString.split(",");
		String[] keyValue;
		int count = 0;
		
		// process each element
		for(String elem: elems) {
			
			keyValue = elem.split("=");
			
			if(keyValue[0].equals("colour") == true) {
				
				if(isValidColour(keyValue[1]) == true) {
					colour = keyValue[1];
				} else {
					throw new ParseException("unable to parse the colour parameter", count);
				}
				
			} else if(keyValue[0].equals("width") == true) {
				try {
					width = Integer.parseInt(keyValue[1]);
					
					if(width < 1) {
						throw new ParseException("the width parameter must be > 0", count);
					}
				} catch (NumberFormatException e) {
					throw new ParseException("unable to parse the width parameter", count);
				}
			} else {
				throw new ParseException("unrecognised key '" + keyValue[0] + "'", count);
			}
			
			// increment the count
			count++;
		}
	}
	
	
	
	public String getColour() {
		return colour;
	}

	public void setColour(String colour) {
		if(isValidColour(colour)) {
			this.colour = colour;
		} else {
			throw new IllegalArgumentException("the colour parameter is invalid");
		}
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		if(width < 1) {
			throw new IllegalArgumentException("the weight parameter must be > 0");
		} else {
			this.width = width;
		}
	}

	// method to validate a KML colour string 
	private boolean isValidColour(String colour) {
		
		Matcher matcher = pattern.matcher(colour);
		return matcher.matches();
	}

}
