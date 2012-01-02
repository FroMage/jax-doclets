/*
    Copyright 2009 Lunatech Research
    
    This file is part of jax-doclets.

    jax-doclets is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    jax-doclets is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with jax-doclets.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.lunatech.doclets.jax.jaxb.model;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationDesc.ElementValuePair;
import com.sun.javadoc.ProgramElementDoc;

public class Element extends JAXBMember {

  private String wrapperName;

  public Element(JAXBClass klass, ProgramElementDoc property, String name, AnnotationDesc xmlElementAnnotation) {
    super(klass, property, name, xmlElementAnnotation);
  }

  public Element(JAXBClass klass, ProgramElementDoc property, String name, String wrapperName, AnnotationDesc xmlElementAnnotation) {
    super(klass, property, name, xmlElementAnnotation);
    this.wrapperName = wrapperName;
  }

  public boolean isWrapped() {
    return wrapperName != null;
  }

  public String getWrapperName() {
    return wrapperName;
  }
  
  private ElementValuePair getElementAttribute(String attributeName){
	  if(xmlAnnotation != null && xmlAnnotation.elementValues() != null){
		  for (ElementValuePair elementValuePair : xmlAnnotation.elementValues()) {
			  if(attributeName.equals(elementValuePair.element().name())){
				  return elementValuePair;
			  }
		  }
	  }
	  return null;
  }
  
  public boolean isRequired(){
	  ElementValuePair elementValuePair = getElementAttribute("required");
	  if(elementValuePair != null){
		  return "true".equalsIgnoreCase(elementValuePair.value().toString());
	  }
	  return false;
  }
  
  public boolean isNillable(){
	  ElementValuePair elementValuePair = getElementAttribute("nillable");
	  if(elementValuePair != null){
		  return "true".equalsIgnoreCase(elementValuePair.value().toString());
	  }
	  return false;
  }
  
  public String getDefaultValue(){
	  ElementValuePair elementValuePair = getElementAttribute("defaultValue");
	  if(elementValuePair != null){
		  return elementValuePair.value().toString();
	  }
	  return "";
  }
}
