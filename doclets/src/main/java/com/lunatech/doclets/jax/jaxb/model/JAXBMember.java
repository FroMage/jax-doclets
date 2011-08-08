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

import java.util.HashMap;

import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSchemaType;

import com.lunatech.doclets.jax.Utils;
import com.lunatech.doclets.jax.jaxb.writers.JAXBClassWriter;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Type;

public class JAXBMember implements Comparable<JAXBMember> {

  protected AnnotationDesc xmlAnnotation;

  protected ProgramElementDoc property;

  protected String name;

  protected String namespace;
  
  protected JAXBClass klass;

  private boolean isIDREF;

  private boolean isID;

  public JAXBMember(JAXBClass klass, ProgramElementDoc property, String name, AnnotationDesc xmlAnnotation) {
    this.xmlAnnotation = xmlAnnotation;
    this.property = property;
    this.name = name;
    if (xmlAnnotation != null) {
      this.namespace = (String) Utils.getAnnotationValue(xmlAnnotation, "namespace");
    }
    this.klass = klass;
    this.isIDREF = Utils.findAnnotation(property, XmlIDREF.class) != null;
    this.isID = Utils.findAnnotation(property, XmlID.class) != null;
  }

  public String getName() {
    return name;
  }

  public String getNamespace() {
    return namespace;
  }

  public Doc getJavaDoc() {
    return property;
  }

  private Type getType() {
    if (property.isMethod()) {
      return ((MethodDoc) property).returnType();
    } else
      return ((FieldDoc) property).type();
  }

  public boolean isCollectionOrArray() {
    return Utils.isCollection(getType()) || isArray();
  }

  public boolean isCollection() {
    if (Utils.isCollection(getType())) {
      if (isXmlList()) {
        return false;
      }
      return true;
    }
    if (isArray()) {
      if (isByteType()) {
        return false;
      }
      if (isXmlList()) {
        return false;
      }
      return true;
    }
    return false;
  }

  private boolean isXmlList() {
    AnnotationDesc xmlListAnnot = Utils.findAnnotation(property, XmlList.class);
    return xmlListAnnot != null;
  }

  public boolean isArray() {
    return Utils.isArray(getType());
  }

  public boolean isByteType() {
    String typeName = getJavaTypeName();
    return typeName.equals("java.lang.Byte") || typeName.equals("byte");
  }

  public Type getJavaType() {
    return Utils.getCollectionType(getType(), klass.getDoclet());
  }

  public String getJavaTypeName() {
    return getJavaType().qualifiedTypeName();
  }

  public boolean isJAXBType() {
    Type type = getJavaType();
    return !type.isPrimitive() && klass.getRegistry().isJAXBClass(type.qualifiedTypeName());
  }

  public String getXSDType() {
    AnnotationDesc xmlSchemaTypeAnnot = Utils.findAnnotation(property, XmlSchemaType.class);
    if (xmlSchemaTypeAnnot != null) {
      String name = (String) Utils.getAnnotationValue(xmlSchemaTypeAnnot, "name");
      if (name != null) {
        return "xsd:" + name;
      }
    }
    if (isXmlList()) {
      return "xsd:list[" + getXSDTypeFromJavaType() + "]";
    }
    return getXSDTypeFromJavaType();
  }

  public String getXSDTypeFromJavaType() {
    String typeName = getJavaTypeName();
    if (typeName.equals("java.lang.String"))
      return "xsd:string";
    if (typeName.equals("java.lang.Character") || typeName.equals("char"))
      return "xsd:string";
    if (typeName.equals("java.util.Date"))
      return "xsd:datetime";
    if (typeName.equals("java.lang.Integer") || typeName.equals("int"))
      return "xsd:int";
    if (typeName.equals("java.lang.Long") || typeName.equals("long"))
      return "xsd:long";
    if (typeName.equals("java.lang.Short") || typeName.equals("short"))
      return "xsd:short";
    if (typeName.equals("java.lang.Byte") || typeName.equals("byte")) {
      if (isArray()) {
        //TODO how to decide between base64Binary and hexbinary ?
        return "xsd:base64Binary";
      }
      return "xsd:byte";
    }
    if (typeName.equals("java.lang.Float") || typeName.equals("float"))
      return "xsd:float";
    if (typeName.equals("java.lang.Double") || typeName.equals("double"))
      return "xsd:double";
    if (typeName.equals("java.lang.Boolean") || typeName.equals("boolean"))
      return "xsd:boolean";
    if (typeName.equals("java.lang.Object"))
      return "xsd:any";

    ClassDoc type = getJavaType().asClassDoc();
    if (type.isEnum()) {
      FieldDoc[] constants = type.enumConstants();
      StringBuffer ret = new StringBuffer();
      boolean first = true;
      for (FieldDoc constant : constants) {
        if (!first)
          ret.append(" | ");
        else
          first = false;
        ret.append(constant.name());
      }
      return ret.toString();
    }

    if (null == hmKnownUnknownXSDTypes.get(typeName)) {
      System.err.println("WARNING: unknown XSD type " + typeName);
      hmKnownUnknownXSDTypes.put(typeName, Boolean.TRUE);
    }

    return typeName;
  }

  private static HashMap<String, Object> hmKnownUnknownXSDTypes = new HashMap<String, Object>();

  public boolean isIDREF() {
    return isIDREF;
  }

  public boolean isID() {
    return isID;
  }

  public int compareTo(JAXBMember other) {
    return name.compareToIgnoreCase(other.name);
  }

  public String getJSONType() {
    String typeName = getJavaTypeName();
    if (typeName.equals("java.lang.String"))
      return "String";
    if (typeName.equals("java.lang.Character") || typeName.equals("char"))
      return "String";
    if (typeName.equals("java.util.Date"))
      return "Date";
    if (typeName.equals("java.lang.Integer") || typeName.equals("int"))
      return "Number";
    if (typeName.equals("java.lang.Long") || typeName.equals("long"))
      return "Number";
    if (typeName.equals("java.lang.Short") || typeName.equals("short"))
      return "Number";
    if (typeName.equals("java.lang.Byte") || typeName.equals("byte"))
      return "Number";
    if (typeName.equals("java.lang.Float") || typeName.equals("float"))
      return "Number";
    if (typeName.equals("java.lang.Double") || typeName.equals("double"))
      return "Number";
    if (typeName.equals("java.lang.Boolean") || typeName.equals("boolean"))
      return "Boolean";
    if (typeName.equals("java.lang.Object"))
      return "Object";

    ClassDoc type = getJavaType().asClassDoc();
    if (type.isEnum()) {
      FieldDoc[] constants = type.enumConstants();
      StringBuffer ret = new StringBuffer();
      boolean first = true;
      for (FieldDoc constant : constants) {
        if (!first)
          ret.append(" | ");
        else
          first = false;
        ret.append("'").append(constant.name()).append("'");
      }
      return ret.toString();
    }

    if (null == hmKnownUnknownXSDTypes.get(typeName)) {
      System.err.println("WARNING: unknown XSD type " + typeName);
      hmKnownUnknownXSDTypes.put(typeName, Boolean.TRUE);
    }

    return typeName;
  }

}
