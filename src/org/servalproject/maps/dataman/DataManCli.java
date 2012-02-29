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
package org.servalproject.maps.dataman;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.servalproject.maps.dataman.tasks.LocationsToKml;
import org.servalproject.maps.dataman.tasks.TaskException;

/**
 * main driving class for the command line interface entry 
 * point to the application
 */
public class DataManCli {
	
	/*
	 * class level constants
	 */
	
	/**
	 * name of the app
	 */
	public static final String APP_NAME    = "Serval Maps Data Manipulator";

	/**
	 * version of the app
	 */
	public static final String APP_VERSION = "1.0";

	/**
	 * url for more information about the app
	 */
	public static final String MORE_INFO   = "http://bytechxplorer.com/development/serval-project/#servalmaps";
	
	/**
	 * url for the license info
	 */
	public static final String LICENSE_INFO = "http://www.gnu.org/licenses/gpl-3.0.txt";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// parse the command line options
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(createOptions(), args);
		}catch(org.apache.commons.cli.ParseException e) {
			// something bad happened so output help message
			printCliHelp("Error in parsing arguments:\n" + e.getMessage());
		}
		
		/*
		 * get and test the command line arguments
		 */
		
		// input path
		String inputPath = cmd.getOptionValue("input");
		
		if(Utils.isEmpty(inputPath)) {
			printCliHelp("Error: the path to the input file is required");
		}
		
		if(Utils.isFileAccessible(inputPath) == false) {
			printCliHelp("Error: the input file is not accessible");
		}
		
		File inputFile = new File(inputPath);
		
		// output path
		String outputPath = cmd.getOptionValue("output");
		
		if(Utils.isEmpty(outputPath)) {
			printCliHelp("Error: the path to the output file is required");
		}
		
		if(Utils.isFileAccessible(outputPath) == true) {
			printCliHelp("Error: the output file already exists");
		}
		
		File outputFile = new File(outputPath);
		
		// task type
		String taskType = cmd.getOptionValue("task");
		
		if(Utils.isEmpty(taskType)) {
			printCliHelp("Error: the task type is required");
		}
		
		HashMap<String, String> taskTypes = getTaskTypes();
		
		if(taskTypes.containsKey(taskType) == false) {
			printCliHelp("Error: the task type was not recognised.\nKnown task types are:" + getTaskList(taskTypes));
		}
		
		boolean verbose = cmd.hasOption("verbose");
		
		// output info if required
		if(verbose) {
			System.out.println(APP_NAME);
			System.out.println("Version: " + APP_VERSION);
			System.out.println("More info: " + MORE_INFO + "\n");
			System.out.println("License info: " + LICENSE_INFO + "\n");
			try {
				System.out.println("Input file: " + inputFile.getCanonicalPath());
				System.out.println("Output file: " + outputFile.getCanonicalPath());
			} catch (IOException e) {
				
			}
			
			System.out.println("Undertaking the task to: " + taskTypes.get(taskType));
		}
		
		// undertake the specific task
		if(taskType.equals("binloctokml") == true) {
			
			LocationsToKml task = new LocationsToKml(inputFile, outputFile, LocationsToKml.BINARY_FILE_TYPE, verbose);
			try {
				task.undertakeTask();
			} catch (TaskException e) {
				System.err.println("Error: Task execution failed\n" + e.toString());
			}
			
		}

	}
	
	/*
	 * output the command line options help
	 */
	private static void printCliHelp(String message) {
		System.out.println(message);
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -jar ServalMapsDataMan.jar", createOptions());
		System.exit(-1);
	}

	/*
	 * create the command line options used by the app
	 */
	private static Options createOptions() {
		
		Options options = new Options();
		
		// path to the input file
		OptionBuilder.withArgName("path");
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("path to the input file");
		OptionBuilder.isRequired(true);
		options.addOption(OptionBuilder.create("input"));

		// path to the output file
		OptionBuilder.withArgName("path");
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("path to the output file");
		OptionBuilder.isRequired(true);
		options.addOption(OptionBuilder.create("output"));
		
		// task to undertake
		OptionBuilder.withArgName("text");
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("manipulation task to undertake");
		OptionBuilder.isRequired(true);
		options.addOption(OptionBuilder.create("task"));
		
		// verbose output or not
		options.addOption(new Option("verbose", "use verbose output"));
		
		return options;
	}
	
	/*
	 * build the list of task types
	 */
	private static HashMap<String, String> getTaskTypes() {
		
		HashMap<String, String> taskTypes = new HashMap<String, String>();
		
		taskTypes.put("binloctokml", "Convert a binary location file to a KML file");
		
		return taskTypes;	
	}
	
	/*
	 * build a human readable list of task types
	 */
	private static String getTaskList(HashMap<String, String> taskTypes) {
		
		StringBuilder list = new StringBuilder("\n");
		
		Set<String> keys = taskTypes.keySet();
		
		for(String key : keys) {
			
			list.append("  - " + key + ": " + taskTypes.get(key) + "\n");
		}
		
		return list.toString();
	}
}
