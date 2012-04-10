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
package com.lunatech.doclets.jax.jaxrs.writers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.lunatech.doclets.jax.JAXConfiguration;
import com.lunatech.doclets.jax.Utils;
import com.lunatech.doclets.jax.jaxrs.JAXRSDoclet;
import com.lunatech.doclets.jax.jaxrs.model.JAXRSApplication;
import com.lunatech.doclets.jax.jaxrs.model.PojoTypes;
import com.lunatech.doclets.jax.jaxrs.model.Resource;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.formats.html.HtmlDocletWriter;

public class PojoClassWriter extends DocletWriter {

  public static final String ZERO_OR_N = "zero or N[";

  private final ClassDoc cDoc;

  private final PojoTypes pojoTypes;

  private final PropertyDoc[] properties;

  public PojoClassWriter(JAXConfiguration configuration, JAXRSApplication application, ClassDoc cDoc, PojoTypes types, Resource resource,
      JAXRSDoclet doclet) {
    super(configuration, getWriter(configuration, application, cDoc), application, resource, doclet);
    this.cDoc = cDoc;
    this.pojoTypes = types;
    this.properties = getProperties(cDoc);
  }

  private static HtmlDocletWriter getWriter(JAXConfiguration configuration, JAXRSApplication application, ClassDoc cDoc) {
    try {
      return new JAXRSHtmlDocletWriter(application, configuration, Utils.classToPath(cDoc), cDoc.typeName()
          + ".html", Utils.classToRoot(cDoc));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void write() {
    final String objectType;
    if (cDoc.isEnum()) {
      objectType = "Enumeration";
    } else {
      objectType = "Data object";
    }
    printPrelude(objectType + ": " + cDoc.typeName(), "");
    printSummary();
    printElements();
    tag("hr");
    printPostlude("");
    writer.flush();
    writer.close();
  }

  private void printElements() {
    if (cDoc.isEnum()) {
      printEnumConstants();
    } else {
      printProperties(properties, "Properties");
    }
  }


  private void printSummary() {
    open("h2 class='classname'");
    // TODO: Factor this
    final String objectType;
    if (cDoc.isEnum()) {
      objectType = "Enumeration";
    } else {
      objectType = "Data object";
    }
    around("span class='name'", objectType + ": " + cDoc.simpleTypeName());
    around("span class='namespace'", "(in " + getContainer() + ")");
    close("h2");

    ClassDoc superClass = cDoc.superclass();
    if (pojoTypes.getResolvedTypes().contains(superClass)) {
      open("dl class='supertype'");
      around("dt", "Supertype:");
      open("dd");
      around("a title='" + superClass.qualifiedTypeName() + "' href='" + Utils.urlToClass(cDoc, superClass) + "'",
          superClass.simpleTypeName());
      close("dd", "dl");
    }
    List<ClassDoc> subClasses = pojoTypes.getSubclasses(cDoc);
    if (!subClasses.isEmpty()) {
      open("dl class='subtypes'");
      around("dt", "Known sub-types:");
      open("dd");
      for (int i = 0; i < subClasses.size(); i++) {
        ClassDoc scDoc = subClasses.get(i);
        around("a title='" + scDoc.qualifiedTypeName() + "' href='" + Utils.urlToClass(cDoc, scDoc) + "'", scDoc.simpleTypeName());
        if (i < (subClasses.size() - 1)) {
          print(",");
        }
      }
      close("dd", "dl");
    }

    Doc javaDoc = cDoc;
    if (javaDoc.tags() != null) {
      writer.printInlineComment(javaDoc);
    }
    // open("table class='examples'", "tr");
    // open("td");
    // printJSONExample();
    // close("td");
    // close("tr", "table");
  }

  private String getContainer() {
    return (cDoc.containingClass() == null) ? cDoc.containingPackage().name() : cDoc.containingClass().qualifiedTypeName();
  }

  private static PropertyDoc[] getProperties(ClassDoc classDoc) {
    final List<PropertyDoc> properties = new ArrayList<PropertyDoc>();

    for (final MethodDoc method : classDoc.methods()) {
      if (isDocumentableProperty(method)) {
        // TODO: Look at the Jackson annotations governing property names
        // TODO: Handle read-only/write-only properties
        PropertyDoc prop = new PropertyDoc(getPropertyName(method.name()), method.returnType(), method);
        properties.add(prop);
      }
    }
    PropertyDoc[] props = properties.toArray(new PropertyDoc[properties.size()]);
    Arrays.sort(props);
    return props;
  }

  private static boolean isDocumentableProperty(final MethodDoc method) {
    // Poor mans JavaBean property recognition
    // TODO: This won't cope with any of the more esoteric Jackson strategies for mapping properties (including auto-detection strategies)
    if (method.name().startsWith("get")
        || (method.name().startsWith("is") && method.returnType().simpleTypeName().equalsIgnoreCase("boolean"))) {
        return true;
    }
    return false;
  }

  private static String getPropertyName(final String memberName) {
    String basename = null;
    if (memberName.startsWith("is")) {
      basename = memberName.substring("is".length());
    } else if (memberName.startsWith("get")) {
      basename = memberName.substring("get".length());
    }
    if (basename != null) {
      return Character.toLowerCase(basename.charAt(0)) + basename.substring(1);
    }
    return memberName;
  }

  private void printEnumConstants() {
    tag("hr");
    open("table class='info' id='EnumConstants'");
    around("caption class='TableCaption'", "Enum Constants");
    open("tbody");
    open("tr");

    around("th class='TableHeader'", "Constant");
    around("th class='TableHeader DescriptionHeader'", "Description");
    close("tr");
    for (final FieldDoc enumConst : cDoc.enumConstants()) {
      open("tr");
      open("td id='ec_" + enumConst.name() + "'");
      print(enumConst.name());
      close("td");
      open("td");
      Doc javaDoc = enumConst;
      if (!Utils.isEmptyOrNull(javaDoc.commentText())) {
        writer.printInlineComment(javaDoc);
      }
      close("td");
      close("tr");

    }
    close("tbody");
    close("table");
  }

  private void printProperties(PropertyDoc[] propertyDocs, String title) {
    tag("hr");
    open("table class='info' id='" + title + "'");
    around("caption class='TableCaption'", title);
    open("tbody");
    open("tr");

    around("th class='TableHeader'", "Name");
    around("th class='TableHeader'", "Type");
    around("th class='TableHeader DescriptionHeader'", "Description");
    close("tr");
    for (PropertyDoc member : propertyDocs) {
      open("tr");
      open("td id='m_" + member.getName() + "'");
      print(member.getName());
	    close("td");
      open("td");
      printMemberType(member);
      close("td");
      open("td");
      Doc javaDoc = member.getMemberDoc();
      if (!Utils.isEmptyOrNull(javaDoc.commentText())) {
        writer.printInlineComment(javaDoc);
      }
      close("td");
      close("tr");

    }
    close("tbody");
    close("table");
  }

  private void printMemberType(PropertyDoc prop) {
    open("tt");
    final Type type = prop.getType();
    printMemberTypeGeneric(type);
    close("tt");
  }

  private void printMemberTypeGeneric(Type type) {
  	if (type.isPrimitive() || type.qualifiedTypeName().startsWith("java.lang")) {
  		print(type.simpleTypeName());
  	} else {
      ClassDoc fDoc = type.asClassDoc();
      if (fDoc == null || fDoc.qualifiedName().equals(cDoc.qualifiedName()) || !this.pojoTypes.getResolvedTypes().contains(fDoc)) {
        around("span title='" + type.qualifiedTypeName() + "'", type.simpleTypeName());
      } else {
        around("a title='" + fDoc.qualifiedTypeName() + "' href='" + Utils.urlToClass(cDoc, fDoc) + "'", fDoc.simpleTypeName());
      }
    }
    ParameterizedType pType = type.asParameterizedType();
    if (pType != null) {
      boolean first = true;
      print("&lt;");
      for (Type genericType : pType.typeArguments()) {
        if (first) {
          first = false;
        } else {
          print(",");
        }
        printMemberTypeGeneric(genericType);
      }
      print("&gt;");
    }
    print(type.dimension());

  }

  @SuppressWarnings("restriction")
  private static class PropertyDoc implements Comparable<PropertyDoc> {

    private final String name;
    private final Type type;
    private final MemberDoc memberDoc;

    public PropertyDoc(String name, Type type, MemberDoc memberDoc) {
      this.name = name;
      this.type = type;
      this.memberDoc = memberDoc;
    }

    public String getName() {
      return name;
    }

    public Type getType() {
      return type;
    }

    public MemberDoc getMemberDoc() {
      return memberDoc;
    }

    @Override
    public int compareTo(PropertyDoc o) {
      return name.compareTo(o.name);
    }

  }

}
