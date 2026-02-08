// HandlerBase.java: Simple base class for AElfred processors.
// NO WARRANTY! See README, and copyright below.
// $Id: LoadDocumentHandler.java,v 1.3 2006/07/11 13:13:13 mdfischer Exp $

// package com.microstar.xml;

import com.microstar.xml.XmlHandler;
import com.microstar.xml.XmlParser;
import com.microstar.xml.XmlException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.*; // Stack, Vector


/**
  * Convenience base class for AElfred handlers.
  * <p>This base class implements the XmlHandler interface with
  * (mostly empty) default handlers.  You are not required to use this,
  * but if you need to handle only a few events, you might find
  * it convenient to extend this class rather than implementing
  * the entire interface.  This example overrides only the
  * <code>charData</code> method, using the defaults for the others:
  * <pre>
  * import com.microstar.xml.HandlerBase;
  *
  * public class MyHandler extends HandlerBase {
  *   public void charData (char ch[], int start, int length)
  *   {
  *     System.out.println("Data: " + new String (ch, start, length));
  *   }
  * }
  * </pre>
  * <p>This class is optional, but if you use it, you must also
  * include the <code>XmlException</code> class.
  * <p>Do not extend this if you are using SAX; extend
  * <code>org.xml.sax.HandlerBase</code> instead.
  * @author Copyright (c) 1998 by Microstar Software Ltd.
  * @author written by David Megginson &lt;dmeggins@microstar.com&gt;
  * @version 1.1
  * @see XmlHandler
  * @see XmlException
  * @see org.xml.sax.HandlerBase
  */
public class LoadDocumentHandler implements XmlHandler {

	Stack elements = new Stack();
	
	boolean attributes=false;
	String rootElement = null;
	String currentElement=null;
	
  /**
    * Handle the start of the document.
    * <p>The default implementation does nothing.
    * @see com.microstar.xml.XmlHandler#startDocument
    * @exception java.lang.Exception Derived methods may throw exceptions.
    */
  public void startDocument () 
    throws java.lang.Exception
  {
  	callBack.startDocument();
  }

  /**
    * Handle the end of the document.
    * <p>The default implementation does nothing.
    * @see com.microstar.xml.XmlHandler#endDocument
    * @exception java.lang.Exception Derived methods may throw exceptions.
    */
  public void endDocument ()
    throws java.lang.Exception
  {
  	callBack.endDocument();
  }

  /**
    * Resolve an external entity.
    * <p>The default implementation simply returns the supplied
    * system identifier.
    * @see com.microstar.xml.XmlHandler#resolveEntity
    * @exception java.lang.Exception Derived methods may throw exceptions.
    */
  public Object resolveEntity (String publicId, String systemId) 
    throws java.lang.Exception
  {
    return null;
  }


  /**
    * Handle the start of an external entity.
    * <p>The default implementation does nothing.
    * @see com.microstar.xml.XmlHandler#startExternalEntity
    * @exception java.lang.Exception Derived methods may throw exceptions.
    */
  public void startExternalEntity (String systemId)
    throws java.lang.Exception
  {
  }

  /**
    * Handle the end of an external entity.
    * <p>The default implementation does nothing.
    * @see com.microstar.xml.XmlHandler#endExternalEntity
    * @exception java.lang.Exception Derived methods may throw exceptions.
    */
  public void endExternalEntity (String systemId)
    throws java.lang.Exception
  {
  }

  /**
    * Handle a document type declaration.
    * @see com.microstar.xml.XmlHandler#doctypeDecl
    * @exception java.lang.Exception Derived methods may throw exceptions.
    * Dump the DTD.
    * <p>Once this event is received, we know that the DTD is
    * completely parsed, and can use AElfred's query routines
    * to reconstruct a normalised version of it.
    * @see #dumpNotations
    * @see #dumpEntities
    * @see #dumpElements
    */
  public void doctypeDecl (String name, String pubid, String sysid)
  {
  	callBack.doctypeDecl(name,pubid,sysid);
  	//getDTDDefs();
  }

 // public void getDTDDefs ()
 // {
   // dumpNotations();
  //  dumpEntities();
  //  dumpElements();
//  }
  /**
    * Produce normalised declarations for all notations.
    * @see #makeExternalIdentifiers
    */
/*  public void dumpNotations ()
  {
    Enumeration notationNames = parser.declaredNotations();
    String nname;
    String extId;

				// Mark the beginning of a new section.
    displayText("<-- Notation Declarations -->\n");

				// Loop through all declared notations.
    while (notationNames.hasMoreElements()) {
      nname = (String)notationNames.nextElement();
      extId =
	makeExternalIdentifiers(parser.getNotationPublicId(nname),
				parser.getNotationSystemId(nname).toString());
      displayText("<!NOTATION " + nname + + ' ' + extId + ">\n");
    }
  }
*/

  /**
    * Produce normalised declarations for all general entities.
    * @see #makeLiteral
    * @see #makeExternalIdentifiers
    */
 /* public void dumpEntities ()
  {
    Enumeration entityNames = parser.declaredEntities();
    String ename;
    String value;

				// Mark the beginning of a new section.
    displayText("<-- Entity Declarations -->\n");

				// Loop through all the declared
				// entities.
    while (entityNames.hasMoreElements()) {

      ename = (String)entityNames.nextElement();

				// Skip parameter entities.
      if (ename.startsWith("%")) {
	// continue; // don't skip after all
      }

				// Construct a value based on the
				// class of entity.
      value = null;
      switch (parser.getEntityType(ename)) {
				// Internal text entity
      case XmlParser.ENTITY_INTERNAL:
	value = makeLiteral(parser.getEntityValue(ename));
	break;
				// External binary entity
      case XmlParser.ENTITY_NDATA:
	value =
	  makeExternalIdentifiers(parser.getEntityPublicId(ename),
				  parser.getEntitySystemId(ename).toString())
	  + "NDATA " + parser.getEntityNotationName(ename);
	break;
				// External text entity
      case XmlParser.ENTITY_TEXT:
	value =
	  makeExternalIdentifiers(parser.getEntityPublicId(ename),
				  parser.getEntitySystemId(ename).toString());
	break;
      }

				// Print a normalised declaration.
      displayText("<!ENTITY " + ename + ' ' + value + ">\n");
    }
  }
*/
	boolean parsedDTD = false;

  /**
    * Produce normalised declarations for all elements.
    * @see #dumpAttributes
    */
 /*
  public void dumpElements ()
  {
    Enumeration elementNames = parser.declaredElements();
    String elname;

				// Mark the beginning of a new section.
    displayText("<-- Element Type Declarations -->\n");

				// Loop through all of the declared
				// elements.
	if (elementNames.hasMoreElements()) {
		parsedDTD = true;
	}
	
    while (elementNames.hasMoreElements()) {
      String contentSpec = "ANY";

      elname = (String)elementNames.nextElement();

				// Construct a content spec based
				// on the element's content type.
      switch (parser.getElementContentType(elname)) {
      case XmlParser.CONTENT_EMPTY:
	contentSpec = "EMPTY";
	break;
      case XmlParser.CONTENT_ANY:
	contentSpec = "ANY";
	break;
      case XmlParser.CONTENT_ELEMENTS:
      case XmlParser.CONTENT_MIXED:
	contentSpec = parser.getElementContentModel(elname);
	break;
      }
	// parse the content model 
	//ElementModel em = parseContentModel(contentSpec);
				// Print a normalised element type
				// declaration.
      displayText("<!ELEMENT " + elname + ' ' + contentSpec + ">");

				// Print the ATTLIST declaration,
				// if any.
	//ElementDef ed = ElementManager.findElement(elname);
   // ed.setAttributeModel(getAttributes(elname));
	//ed.setContentModel(em);
    //  displayText("Model: "+ed.getContentModel().toString());
				// Blank line.
      displayText("");
    }
  }
*/
/*
	public ElementModel parseContentModel(String content) {
		ElementModel ret = new ElementModel();
		ret.parseContent(content,0);
		return ret;
	}
*/
	
	boolean disp=false;
	
	public void displayText(String s) {
		if (disp) System.out.println(s);
	}
	
  /**
    * Dump attributes for an element.
    * @see #makeAttributeType
    * @see #makeAttributeValue
    */
 /* AttributeModel getAttributes (String elname)
  {
    Enumeration attributeNames = parser.declaredAttributes(elname);
    String aname;
    String type;
    String value;

				// Skip if there are no declared
				// attributes for this element
				// type.
    if (attributeNames == null) {
      return null;
    }

				// Print the start of the ATTLIST
				// declaration.
    displayText("<!ATTLIST " + elname);

				// Loop through all of the declared
				// attributes.
    AttributeModel am = new AttributeModel();
    
    while (attributeNames.hasMoreElements()) {

      aname = (String)attributeNames.nextElement();
      type = makeAttributeType(elname, aname);
      value = makeAttributeValue(elname, aname);
		
		if (type.equals("ENUMERATION") || type.equals("NOTATION")) {
			am.addItem(aname,type,value,parser.getAttributeEnumeration(elname, aname));
			displayText("  " + aname + ' ' + type + ' ' + value + ' ' + parser.getAttributeEnumeration(elname, aname));
		} else {
			am.addItem(aname,type,value);
			displayText("  " + aname + ' ' + type + ' ' + value);
		}
				// Print the declaration for a
				// single attribute.
     
    }

				// Finish the ATTLIST declaration.
    displayText(">");
    
    return am;
  }
*/

  /**
    * Generate the attribute type as a normalised string.
    */
/*  String makeAttributeType (String elname, String aname)
  {
				// Construct a string equivalent
				// of the attribute type.
      switch (parser.getAttributeType(elname, aname)) {
      case XmlParser.ATTRIBUTE_CDATA:
	return "CDATA";
      case XmlParser.ATTRIBUTE_ID:
	return "ID";
      case XmlParser.ATTRIBUTE_IDREF:
	return "IDREF";
      case XmlParser.ATTRIBUTE_IDREFS:
	return "IDREFS";
      case XmlParser.ATTRIBUTE_ENTITY:
	return "ENTITY";
      case XmlParser.ATTRIBUTE_ENTITIES:
	return "ENTITIES";
      case XmlParser.ATTRIBUTE_NMTOKEN:
	return "NMTOKEN";
      case XmlParser.ATTRIBUTE_NMTOKENS:
	return "NMTOKENS";
      case XmlParser.ATTRIBUTE_ENUMERATED:
				// An enumeration.
	return "ENUMERATION";
      case XmlParser.ATTRIBUTE_NOTATION:
				// An enumeration of notations.
	return "NOTATION";
      }
      return null;
  }
*/

  /**
    * Generate a full attribute default value.
    * @see #makeLiteral
    */
  /*
  String makeAttributeValue (String elname, String aname)
  {
				// Generate a default value based
				// on the type.
    switch (parser.getAttributeDefaultValueType(elname, aname)) {
    case XmlParser.ATTRIBUTE_DEFAULT_IMPLIED:
      return "#IMPLIED";
    case XmlParser.ATTRIBUTE_DEFAULT_SPECIFIED:
      return makeLiteral(parser.getAttributeDefaultValue(elname, aname));
    case XmlParser.ATTRIBUTE_DEFAULT_REQUIRED:
      return "#REQUIRED";
    case XmlParser.ATTRIBUTE_DEFAULT_FIXED:
      return "#FIXED " +
	makeLiteral(parser.getAttributeDefaultValue(elname,aname));
    }
    return null;
  }

*/
  /**
    * Construct a string equivalent of external identifiers.
    * @see #makeLiteral
    */
    
  /*
  String makeExternalIdentifiers (String pubid, String sysid)
  {
    String extId = "";

    if (pubid != null) {
      extId = "PUBLIC " + makeLiteral(pubid);
      if (sysid != null) {
	extId = extId + ' ' + makeLiteral(sysid);
      }
    } else {
      extId = "SYSTEM " + makeLiteral(sysid);
    }

    return extId;
  }

*/
  /**
    * Quote a literal, and escape any '"' or non-ASCII characters within it.
    */
 /*
  String makeLiteral (String data)
  {
    char ch[] = data.toCharArray();
    StringBuffer buf = new StringBuffer();

    buf.append('"');
    for (int i = 0; i < ch.length; i++) {
      if (ch[i] == '"') {
	buf.append("&#22;");
      } else if ((int)ch[i] > 0x7f) {
	buf.append("&#" + (int)ch[i] + ";");
      } else {
	buf.append(ch[i]);
      }
    }
    buf.append('"');

    return buf.toString();
  }
*/
  /**
    * Handle an attribute assignment.
    * <p>The default implementation does nothing.
    * @see com.microstar.xml.XmlHandler#attribute
    * @exception java.lang.Exception Derived methods may throw exceptions.
    */
 // ---- Read Document Contents
    
  private static String attributeLabel = "attributes";
 // Vector attributeNames = new Vector(1);
//  Vector attributeValues = new Vector(1);
  Hashtable attributeTable = new Hashtable();
  
  public void attribute (String aname, String value, boolean isSpecified)
    throws java.lang.Exception
  {
  	//	attributeNames.addElement(aname);
  	//	attributeValues.addElement(value);
  		attributeTable.put(aname,value);
  		attributes = true;
  }
 
 //	ElementUnit currentEntry=null;

  /**
    * Handle the start of an element.
    * <p>The default implementation does nothing.
    * @see com.microstar.xml.XmlHandler#startElement
    * @exception java.lang.Exception Derived methods may throw exceptions.
    */
  public void startElement (String elname)
    throws java.lang.Exception
	{
		ListVector atts = null;
  		String e = elname;
  		
  		if (rootElement == null) {
  			rootElement = e;
  		} else { // ignore text that preceeds rootElement!!!
			if (!contentData.equals("")) {
				callBack.text(contentData.trim(), elements);
				contentData = "";
			}
  		}
  		
  		if (attributes) callBack.startElement(elname,attributeTable,elements);
  		else callBack.startElement(elname,null,elements);
 
  		elements.push(currentElement);
  		currentElement = e;

  		//attributeNames.setSize(0);
  		//attributeValues.setSize(0);
  		attributeTable.clear();
  		attributes = false;
	}
	
	//Vector currentProducts = null;
	//Vector currentProduct = null;

  /**
    * Handle the end of an element.
    * <p>The default implementation does nothing.
    * @see com.microstar.xml.XmlHandler#endElement
    * @exception java.lang.Exception Derived methods may throw exceptions.
    */
  public void endElement (String elname)
    throws java.lang.Exception
  {
  		contentData = contentData.trim();

		if (!contentData.equals("")) {
			callBack.text(contentData,elements);
		}
		
		currentElement = (String) elements.pop();
		callBack.endElement(elname);

  		contentData = "";
    } 

	String contentData = "";
  /**
    * Handle character data.
    * <p>The default implementation does nothing.
    * @see com.microstar.xml.XmlHandler#charData
    * @exception java.lang.Exception Derived methods may throw exceptions.
    */
  public void charData (char ch[], int start, int length)
    throws java.lang.Exception
  {
  		if (Character.isWhitespace(ch[start])) return;
  		String tdata = new String(ch,start,length);
  		contentData += tdata;
  }

  /**
    * Handle ignorable whitespace.
    * <p>The default implementation does nothing.
    * @see com.microstar.xml.XmlHandler#ignorableWhitespace
    * @exception java.lang.Exception Derived methods may throw exceptions.
    */
  public void ignorableWhitespace (char ch[], int start, int length)
    throws java.lang.Exception
  {
  }

  /**
    * Handle a processing instruction.
    * <p>The default implementation does nothing.
    * @see com.microstar.xml.XmlHandler#processingInstruction
    * @exception java.lang.Exception Derived methods may throw exceptions.
    */
  public void processingInstruction (String target, String data)
    throws java.lang.Exception
  {
  }

  /**
    * Throw an exception for a fatal error.
    * <p>The default implementation throws <code>XmlException</code>.
    * @see com.microstar.xml.XmlHandler#error
    * @exception com.microstar.xml.XmlException A specific parsing error.
    * @exception java.lang.Exception Derived methods may throw exceptions.
    */
  public void error (String message, String systemId, int line, int column)
    throws XmlException, java.lang.Exception
  {
    throw new XmlException(message, systemId, line, column);
  }
	
  public String getRootElement() {
  	return rootElement;
  }
  
  XmlParser parser = null;
  XMLParserCallBack callBack=null;
    /**
    * Start a parse in application mode.
    * <p>Output will go to STDOUT.
    * @see #displayText
    * @see com.microstar.xml.XmlParser#run
    */
  void doParse (XFile xr, XMLParserCallBack cb)
    throws java.lang.Exception
  {
  //	doParse(xr.aFile.toString());
  	callBack = cb;
    String docURL = makeAbsoluteURL(xr.aFile.toString());

				// create the parser
   parser = new XmlParser();
    parser.setHandler(this);
   // parser.parse(null, null, (Reader) xr.diStream);
    parser.parse(docURL, null, xr.diStream);
  }

  void doParse (String url, XMLParserCallBack cb)
    throws java.lang.Exception
  {
    String docURL = makeAbsoluteURL(url);

				// create the parser
    parser = new XmlParser();
    parser.setHandler(this);
    parser.parse(makeAbsoluteURL(url), null, (String)null);
  }
	
 void readDTD (String url)
    throws java.lang.Exception
  {
    String x = makeAbsoluteURL(url);
    StringReader wr = new StringReader("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"+
    										"<!DOCTYPE xxx SYSTEM \""+x+"\">");
 				// create the parser
    parser = new XmlParser();
    parser.setHandler(this);
    try {
 		parser.parse(null ,null, (Reader) wr); 
 	} catch (EOFException e) {
 		
 	} 
  } 
  
  static String makeAbsoluteURL (String url)
    throws MalformedURLException
  {
    URL baseURL;

    String currentDirectory = System.getProperty("user.dir");

    String fileSep = System.getProperty("file.separator");
    String file = currentDirectory.replace(fileSep.charAt(0), '/') + '/';
    if (file.charAt(0) != '/') {
      file = "/" + file;
    }
    baseURL = new URL("file", null, file);
   return new URL(baseURL,url).toString();
  }


}


