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
package org.servalproject.maps.dataman.builders;

/**
 * represent an exception that occurred during a build activity
 */
public class BuildException extends Exception {
	
	private static final long serialVersionUID = -2928995420893090056L;

	
	String error = null;

	public BuildException() {
		super();
		error = "unknown";
	}
	
	public BuildException(String err) {
		super(err);
		error = err;
	}

	public BuildException(String err, Throwable throwable) {
		super(err, throwable);
		error = err;
	}

	public String getError() {
		return error;
	}

}
