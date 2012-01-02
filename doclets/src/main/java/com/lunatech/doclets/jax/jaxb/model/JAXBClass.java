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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

import com.lunatech.doclets.jax.JAXConfiguration;
import com.lunatech.doclets.jax.Utils;
import com.lunatech.doclets.jax.jaxb.JAXBConfiguration;
import com.lunatech.doclets.jax.jaxb.JAXBDoclet;
import com.lunatech.doclets.jax.jaxb.writers.JAXBClassWriter;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.tools.doclets.formats.html.ConfigurationImpl;

public class JAXBClass implements Comparable<JAXBClass> {

  private Set<Value> values = new TreeSet<Value>();

  private Set<Attribute> attributes = new TreeSet<Attribute>();

  private Set<Element> elements = new TreeSet<Element>();

  private ClassDoc klass;

  private String name;

  private String namespace;

  private Registry registry;

  private JAXBDoclet doclet;

  private Map<String, JAXBMember> members = new HashMap<String, JAXBMember>();

  public JAXBClass(ClassDoc klass, Registry registry, JAXBDoclet doclet) {
    // System.err.println("Root: " + klass.name());
    this.klass = klass;
    this.registry = registry;
    this.doclet = doclet;
    AnnotationDesc rootAnnotation = Utils.findAnnotation(klass, XmlRootElement.class);
    if (rootAnnotation != null) {
      name = (String) Utils.getAnnotationValue(rootAnnotation, "name");
      namespace = (String) Utils.getAnnotationValue(rootAnnotation, "namespace");
    }
    if (name == null)
      name = klass.simpleTypeName();
    setupMembers(klass);
  }

  private void setupMembers(ClassDoc klass) {
    ClassDoc accessorAnnotationType = Utils.findAnnotatedClass(klass, XmlAccessorType.class);
    AnnotationDesc accessorAnnotation = null;
    if (accessorAnnotationType != null)
      accessorAnnotation = Utils.findAnnotation(accessorAnnotationType, XmlAccessorType.class);
    XmlAccessType accessType = XmlAccessType.PUBLIC_MEMBER;
    // System.err.println("Accessor: " + accessorAnnotation);
    if (accessorAnnotation != null) {
      FieldDoc value = (FieldDoc) Utils.getAnnotationValue(accessorAnnotation);
      accessType = XmlAccessType.valueOf(value.name());
    }
    for (FieldDoc field : klass.fields(false)) {
      addProperty(field, accessType);
    }
    for (MethodDoc method : klass.methods(false)) {
      addProperty(method, accessType);
    }
    ClassDoc superClass = klass.superclass();
    if (superClass != null && !superClass.qualifiedTypeName().startsWith("java."))
      setupMembers(superClass);
  }

  private void addProperty(ProgramElementDoc property, XmlAccessType accessType) {
    if (property.isMethod() && !isProperty((MethodDoc) property))
      return;
    if (property.isStatic())
      return;

    AnnotationDesc xmlElementAnnotation = Utils.findAnnotation(property, XmlElement.class);
    AnnotationDesc xmlAttributeAnnotation = Utils.findAnnotation(property, XmlAttribute.class);
    AnnotationDesc xmlValueAnnotation = Utils.findAnnotation(property, XmlValue.class);
    AnnotationDesc xmlTransientAnnotation = Utils.findAnnotation(property, XmlTransient.class);
    boolean hasXmlAnnotation = xmlElementAnnotation != null || xmlAttributeAnnotation != null || xmlValueAnnotation != null;
    boolean isTransient = xmlTransientAnnotation != null;
    if (property.isField()) {
      isTransient |= ((FieldDoc) property).isTransient();
    }
    boolean include = false;
    switch (accessType) {
    case NONE:
      // all annotated non-transient properties
      include = hasXmlAnnotation && !isTransient;
      break;
    case FIELD:
      // all non-transient fields included
      include = !isTransient;
      // methods only if annotated
      if (property.isMethod())
        include &= hasXmlAnnotation;
      break;
    case PROPERTY:
      // all non-transient properties included
      include = !isTransient;
      // fields only if annotated
      if (property.isField())
        include &= hasXmlAnnotation;
      break;
    case PUBLIC_MEMBER:
      // all public or annotated members
      include = (property.isPublic() || hasXmlAnnotation) && (this.doclet.conf.enableJaxBMethodOutput || !property.isMethod());
      break;
    }
    if (include)
      addElement(property, xmlElementAnnotation, xmlAttributeAnnotation, xmlValueAnnotation);

  }

  private boolean isProperty(MethodDoc method) {
    String name = method.name();
    if (method.parameters().length != 0)
      return false;
    if (name.startsWith("get"))
      return name.length() > 3;
    if (name.startsWith("is") && method.returnType().toString().equals("boolean"))
      return name.length() > 2;
    return false;
  }

  private void addElement(ProgramElementDoc property, AnnotationDesc xmlElementAnnotation, AnnotationDesc xmlAttributeAnnotation,
                          AnnotationDesc xmlValueAnnotation) {
    String name = property.name();
    if (property.isMethod()) {
      if (name.startsWith("get"))
        name = name.substring(3);
      else
        name = name.substring(2);
      name = name.substring(0, 1).toLowerCase() + name.substring(1);
    }
    if (!members.containsKey(name))
      addElement(property, name, xmlElementAnnotation, xmlAttributeAnnotation, xmlValueAnnotation);
  }

  private void addElement(ProgramElementDoc property, String name, AnnotationDesc xmlElementAnnotation,
                          AnnotationDesc xmlAttributeAnnotation, AnnotationDesc xmlValueAnnotation) {
    if (xmlAttributeAnnotation != null)
      addAttribute(property, name, xmlAttributeAnnotation);
    else if (xmlValueAnnotation != null)
      addValue(property, name, xmlValueAnnotation);
    else
      addElement(property, name, xmlElementAnnotation);
  }

  private void addValue(ProgramElementDoc property, String name, AnnotationDesc xmlValueAnnotation) {
    Value value = new Value(this, property, name, xmlValueAnnotation);
    members.put(name, value);
    values.add(value);
  }

  private void addAttribute(ProgramElementDoc property, String propertyName, AnnotationDesc xmlAttributeAnnotation) {
    String name = propertyName;
    String overriddenName = (String) Utils.getAnnotationValue(xmlAttributeAnnotation, "name");
    if (overriddenName != null)
      name = overriddenName;
    Attribute attribute = new Attribute(this, property, name, xmlAttributeAnnotation);
    members.put(propertyName, attribute);
    attributes.add(attribute);
  }

  private void addElement(ProgramElementDoc property, String propertyName, AnnotationDesc xmlElementAnnotation) {
    AnnotationDesc xmlElementWrapperAnnotation = Utils.findAnnotation(property, XmlElementWrapper.class);
    String name = propertyName;
    if (xmlElementAnnotation != null) {
      String overriddenName = (String) Utils.getAnnotationValue(xmlElementAnnotation, "name");
      if (overriddenName != null)
        name = overriddenName;
    }
    String wrapperName = null;
    if (xmlElementWrapperAnnotation != null) {
      wrapperName = (String) Utils.getAnnotationValue(xmlElementWrapperAnnotation, "name");
      if (wrapperName == null)
        wrapperName = name + "s";
    }

    Element element;
    if (wrapperName == null)
      element = new Element(this, property, name, xmlElementAnnotation);
    else
      element = new Element(this, property, name, wrapperName, xmlElementAnnotation);
    members.put(propertyName, element);
    elements.add(element);
  }

  public String getQualifiedClassName() {
    return klass.qualifiedName();
  }

  public JAXBDoclet getDoclet() {
    return doclet;
  }

  public String getName() {
    return name;
  }

  public String getNamespace() {
    return namespace;
  }

  public void write(JAXConfiguration configuration) {
    new JAXBClassWriter(configuration, this).write();
  }

  public Doc getJavaDoc() {
    return klass;
  }

  public Collection<Element> getElements() {
    return elements;
  }

  public Collection<Attribute> getAttributes() {
    return attributes;
  }

  public Collection<Value> getValues() {
    return values;
  }

  public Registry getRegistry() {
    return registry;
  }

  public String getPackageName() {
    return klass.containingPackage().name();
  }

  public String getShortClassName() {
    return klass.name();
  }

  public int compareTo(JAXBClass other) {
    return name.compareToIgnoreCase(other.name);
  }

  public List<JAXBMember> getMembers() {
    List<JAXBMember> list = new ArrayList<JAXBMember>(values.size() + elements.size() + attributes.size());
    list.addAll(values);
    list.addAll(attributes);
    list.addAll(elements);
    return list;
  }

  public JAXBMember getID() {
    for (JAXBMember member : getMembers()) {
      if (member.isID())
        return member;
    }
    return null;
  }
}
