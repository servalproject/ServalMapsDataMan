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

/**
 * a utility class used to represent an element in a GPS Trace
 */
public class GpsTraceElement {

	/*
	 * class level private variables
	 */
	double latitude;
	double longitude;
	long   timestamp;

	/**
	 * construct a new GpsTraceElement object
	 * 
	 * @param latitude the latitude coordinate in decimal notation
	 * @param longitude the longitude coordinate in decimal notiation
	 * @param timestamp the timestamp of when this coordinate was recorded 
	 */
	public GpsTraceElement(double latitude, double longitude, long timestamp) {

		//TODO work out better way to validate these parameters
		this.latitude = latitude;
		this.longitude = longitude;
		this.timestamp = timestamp;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
}
