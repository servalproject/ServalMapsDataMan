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
package org.servalproject.maps.dataman.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.servalproject.maps.dataman.Utils;
import org.servalproject.maps.dataman.builders.BuildException;
import org.servalproject.maps.dataman.builders.KmlBuilder;
import org.servalproject.maps.dataman.types.GpsTraceElement;
import org.servalproject.maps.dataman.types.KmlStyle;
import org.servalproject.maps.protobuf.BinaryFileContract;
import org.servalproject.maps.protobuf.LocationMessage;
import org.servalproject.maps.protobuf.LocationMessage.Message.Builder;

/**
 * methods to process location data and output KML data
 */
public class LocationsToKml {
	
	/*
	 * public class level constants
	 */
	public static final int BINARY_FILE_TYPE = 1;
	
	/*
	 * private class level variables
	 */
	private boolean  verbose = false;
	private File     inputFile;
	private File     outputFile;
	private int      fileType;
	private KmlStyle style;
	
	/**
	 * convert the locations stored in a file into a KML file
	 * 
	 * @param inputFile the input file containing the data data
	 * @param outputFile the output file to contain the KML data
	 * @param fileType the type of input file
	 * @param verbose indicates if verbose output is required
	 */
	public LocationsToKml(File inputFile, File outputFile, int fileType, boolean verbose, KmlStyle style) {
		
		// check the parameters
		if(inputFile == null) {
			throw new IllegalArgumentException("the input file parameter is required");
		}
		
		if(outputFile == null) {
			throw new IllegalArgumentException("the output file parameter is required");
		}
	
		try {
			if(Utils.isFileAccessible(inputFile.getCanonicalPath()) == false) {
				throw new IllegalArgumentException("the input file cannot be accessed");
			}
		} catch(IOException e) {
			throw new IllegalArgumentException("the input file cannot be accessed", e);
		}
		
		try {
			if(Utils.isFileAccessible(outputFile.getCanonicalPath()) == true) {
				throw new IllegalArgumentException("the output file already exists");
			}
		}catch(IOException e) {
			throw new IllegalArgumentException("the output file already exists", e);
		}
		
		this.verbose = verbose;
		
		switch(fileType) {
		case BINARY_FILE_TYPE:
			if(inputFile.getName().endsWith(BinaryFileContract.LOCATION_EXT) == false) {
				throw new IllegalArgumentException("a binary file is required to end with '" + BinaryFileContract.LOCATION_EXT + "'");
			}
			break;
		default:
			throw new IllegalArgumentException("the provided input type is invalid");
		}
		
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.fileType = fileType;
		this.style = style;
	}
	
	/**
	 * undertake the task
	 */
	public void undertakeTask(String taskType) throws TaskException {
		
		// check on the parameters
		if(Utils.isEmpty(taskType) == true) {
			throw new IllegalArgumentException("the taskType parameter is required");
		}
		
		if(taskType.equals("binloctokml") == true) {
			switch(fileType) {
			case BINARY_FILE_TYPE:
				processBinaryFileBasicOutput(inputFile, outputFile);
				break;
			default:
				return;
			}
		} else if(taskType.equals("binloctokml2") == true) {
			switch(fileType) {
			case BINARY_FILE_TYPE:
				processBinaryFileTimeOutput(inputFile, outputFile);
				break;
			default:
				return;
			}
		} else {
			throw new TaskException("unrecognised task type parameter");
		}
	}
	
	// private method to retrieve the GPS trace from the binary file
	private ArrayList<GpsTraceElement> retrieveTraceFromBinaryFile(File inputFile) throws TaskException {
		
		// declare helper variables
		FileInputStream inputStream = null;
		ArrayList<GpsTraceElement> trace = new ArrayList<GpsTraceElement>();
		
		// open the input stream
		try {
			inputStream = new FileInputStream(inputFile);
		} catch(FileNotFoundException e) {
			throw new TaskException("unable to open the input file", e);
		}
		
		// process the messages in the file
		Builder messageBuilder = LocationMessage.Message.newBuilder();
		
		try {
			while(messageBuilder.mergeDelimitedFrom(inputStream) == true) {
				
				// add the coordinates to the list
				trace.add(new GpsTraceElement(
						messageBuilder.getLatitude(),
						messageBuilder.getLongitude(),
						messageBuilder.getTimestamp(),
						messageBuilder.getTimeZone()
						));
			}
			
			// play nice and tidy up
			inputStream.close();
		} catch (IOException e) {
			throw new TaskException("unable to read messages from the binary file", e);
		}
		
		return trace;
	}
	 
	/*
	 * process a binary file
	 */
	private void processBinaryFileBasicOutput(File inputFile, File outputFile) throws TaskException {
		
		if(verbose) {
			System.out.println("processing a binary file");
		}
		
		ArrayList<GpsTraceElement> trace = retrieveTraceFromBinaryFile(inputFile);
		
		// build the KML
		try {
			
			// start a new KML file
			KmlBuilder builder = new KmlBuilder();
			
			// add any style info
			builder.setStyle(style);
			
			// add the GPS trace
			builder.addTrace(trace);
			
			// output the KML to a file
			PrintWriter printWriter = new PrintWriter(new FileOutputStream(outputFile));
			builder.outputToFile(printWriter);
			
			// close the output file
			printWriter.close();
			
		} catch (BuildException e) {
			throw new TaskException("unable to build the KML file", e);
		} catch (FileNotFoundException e) {
			throw new TaskException("unable to build the KML file", e);
		}
	}
	
	/*
	 * process a binary file
	 */
	private void processBinaryFileTimeOutput(File inputFile, File outputFile) throws TaskException {
		
		if(verbose) {
			System.out.println("processing a binary file");
		}
		
		ArrayList<GpsTraceElement> trace = retrieveTraceFromBinaryFile(inputFile);
		
		// build the KML
		try {
			
			// start a new KML file
			KmlBuilder builder = new KmlBuilder();
			
			// add any style info
			builder.setStyle(style);
			
			// add the GPS trace
			builder.addTraceWithTime(trace);
			
			// output the KML to a file
			PrintWriter printWriter = new PrintWriter(new FileOutputStream(outputFile));
			builder.outputToFile(printWriter);
			
			// close the output file
			printWriter.close();
			
		} catch (BuildException e) {
			throw new TaskException("unable to build the KML file", e);
		} catch (FileNotFoundException e) {
			throw new TaskException("unable to build the KML file", e);
		}
	}
}
