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

import java.io.PrintWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.servalproject.maps.dataman.types.GpsTraceElement;
import org.servalproject.maps.dataman.types.KmlStyle;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * utility class to construct KML
 */
public class KmlBuilder {

	/* 
	 * private class level variables
	 */
	private DocumentBuilderFactory factory;
	private DocumentBuilder        builder;
	private DOMImplementation      domImpl;
	private Document               xmlDoc;
	private Element                rootElement;
	private Element                rootDocument;
	
	private boolean hasStyle = false;
	
	/*
	 * private class level constants
	 */
	private final String STYLE_URL = "gpsTraceStyle";

	/**
	 * instantiates a new KML builder
	 * @throws BuildException if the underlying XML infrastructure cannot be used
	 */
	public KmlBuilder() throws BuildException {

		// create the xml document builder factory object
		factory = DocumentBuilderFactory.newInstance();

		// set the factory to be namespace aware
		factory.setNamespaceAware(true);

		// create the xml document builder object and get the DOMImplementation object
		try {
			builder = factory.newDocumentBuilder();
		} catch (javax.xml.parsers.ParserConfigurationException e) {
			throw new BuildException("unable to instantiate class", e);
		}

		domImpl = builder.getDOMImplementation();

		// create a document with the appropriate default namespace
		xmlDoc = domImpl.createDocument("http://www.opengis.net/kml/2.2", "kml", null);

		// get the root element
		rootElement = this.xmlDoc.getDocumentElement();

		// add atom namespace to the root element
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:atom", "http://www.w3.org/2005/Atom");

		// add google earth extension namespace to the root element
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:gx", "http://www.google.com/kml/ext/2.2");

		// add schema namespace to the root element
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");

		// add reference to the kml schema
		rootElement.setAttribute("xsi:schemaLocation", "http://www.opengis.net/kml/2.2 http://schemas.opengis.net/kml/2.2.0/ogckml22.xsd");

		// add the root document
		rootDocument = xmlDoc.createElement("Document");
		rootElement.appendChild(rootDocument);

		// add author information
		Element author = xmlDoc.createElement("atom:author");

		Element authorName = xmlDoc.createElement("atom:name");
		authorName.setTextContent("Serval Maps Data Manipulator");
		author.appendChild(authorName);

		// add link information
		Element link = xmlDoc.createElement("atom:link");
		link.setAttribute("href", "http://servalproject.org");

		// add info to node tree
		rootDocument.appendChild(author);
		rootDocument.appendChild(link);
	}
	
	/**
	 * set the KML style for the line that represents the GPS trace
	 * 
	 * @param style an object representing the style parameters
	 */
	public boolean setStyle(KmlStyle style) {
		
		if(style != null && hasStyle == false) {
			// add the style information
			Element styleElem = xmlDoc.createElement("Style");
			styleElem.setAttribute("id", STYLE_URL);
			
			Element lineStyle = xmlDoc.createElement("LineStyle");
			styleElem.appendChild(lineStyle);
			
			Element elem = xmlDoc.createElement("color");
			elem.setTextContent(style.getColour());
			lineStyle.appendChild(elem);
			
			elem = xmlDoc.createElement("width");
			elem.setTextContent(Integer.toString(style.getWidth()));
			lineStyle.appendChild(elem);
			
			rootDocument.appendChild(styleElem);
			
			hasStyle = true;
		} 
		
		return false;
	}
	
	/**
	 * 
	 * @param trace a list of GpsTraceElements
	 * @throws BuildException if a error occurs while processing the list of traces
	 */
	public void addTrace(ArrayList<GpsTraceElement> trace) throws BuildException {
		
		// validate the parameters
		if(trace == null) {
			throw new IllegalArgumentException("the trace parameter is required");
		}
		
		if(trace.size() == 0) {
			throw new IllegalArgumentException("the trace must contain at least one element");
		}
		
		// add the start of the PlaceMark element
		Element elem = xmlDoc.createElement("Placemark");
		rootDocument.appendChild(elem);
		
		if(hasStyle == true) {
			Element styleUrl = xmlDoc.createElement("styleUrl");
			styleUrl.setTextContent(STYLE_URL);
			elem.appendChild(styleUrl);
		}
		
		// add the LineString element
		Element lineString = xmlDoc.createElement("LineString");
		elem.appendChild(lineString);
		
		// add the tessellate element
		elem = xmlDoc.createElement("tessellate");
		elem.setTextContent("1");
		lineString.appendChild(elem);
		
		// create the altitude mode
		// used in conjunction with the tessellate element above to ensure the 
		// line string is stuck to the ground
		elem = xmlDoc.createElement("altitudeMode");
		elem.setTextContent("clampToGround");
		lineString.appendChild(elem);
		
		// create the coordinates element
		elem = xmlDoc.createElement("coordinates");
		
		StringBuilder coordinates = new StringBuilder();
		
		// process the list of elements
		for(GpsTraceElement element : trace) {
			coordinates.append(Double.toString(element.getLongitude()) + "," + Double.toString(element.getLatitude()) + " ");
		}
		
		elem.setTextContent(coordinates.toString());
		lineString.appendChild(elem);
	}
	
	/**
	 * output the contents of the KML to a file
	 * 
	 * @throws BuildException if an error occurs during xml transformation our file writing
	 */
	public void outputToFile(PrintWriter printWriter) throws BuildException {

		try {
            // create a transformer 
            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer        transformer  = transFactory.newTransformer();
            
            // set some options on the transformer
            transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            
            // get a transformer and supporting classes
            StreamResult result = new StreamResult(printWriter);
            DOMSource    source = new DOMSource(xmlDoc);
            
            // transform the internal objects into XML and print it
            transformer.transform(source, result);
	
	    } catch (javax.xml.transform.TransformerException e) {
	            throw new BuildException("unable to create the XML and output it to the file", e);
	    }
	}

}
