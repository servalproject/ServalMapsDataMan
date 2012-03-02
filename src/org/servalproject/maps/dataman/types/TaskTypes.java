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

import java.util.HashMap;
import java.util.Set;

/**
 * a utility class to manage the various task types
 */
public class TaskTypes {

	/**
	 * return the list of tasks that can be undertaken
	 * 
	 * @return a hashmap where the key is the task type, and the value is the task description
	 */
	public static HashMap<String, String> getTaskTypes() {

		HashMap<String, String> taskTypes = new HashMap<String, String>();
		
		taskTypes.put("binloctokml", "Convert a binary location file to a KML file");
		taskTypes.put("binloctokml2", "Convert a binary location file to a KML file including time span elements");
		
		return taskTypes;
	}
	
	/**
	 * return the list tasks as a plain text list
	 * 
	 * @return the plain text list of tasks
	 */
	public static String getTaskList() {
		
		HashMap<String, String> taskTypes = getTaskTypes();
		
		StringBuilder list = new StringBuilder("\n");
		
		Set<String> keys = taskTypes.keySet();
		
		for(String key : keys) {
			
			list.append("  - " + key + ": " + taskTypes.get(key) + "\n");
		}
		
		return list.toString();
	}
}
