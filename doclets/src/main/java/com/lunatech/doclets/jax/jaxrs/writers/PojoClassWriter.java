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

import com.lunatech.doclets.jax.JAXConfiguration;
import com.lunatech.doclets.jax.Utils;
import com.lunatech.doclets.jax.jaxrs.JAXRSDoclet;
import com.lunatech.doclets.jax.jaxrs.model.JAXRSApplication;
import com.lunatech.doclets.jax.jaxrs.model.PojoTypes;
import com.lunatech.doclets.jax.jaxrs.model.Resource;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.formats.html.HtmlDocletWriter;

public class PojoClassWriter extends DocletWriter {

  public static final String ZERO_OR_N = "zero or N[";

  private final ClassDoc cDoc;

	private PojoTypes pojoTypes;

  public PojoClassWriter(JAXConfiguration configuration, JAXRSApplication application, ClassDoc cDoc, PojoTypes types, Resource resource,
      JAXRSDoclet doclet) {
    super(configuration, getWriter(configuration, application, cDoc), application, resource, doclet);
    this.cDoc = cDoc;
    this.pojoTypes = types;
  }

  private static HtmlDocletWriter getWriter(JAXConfiguration configuration, JAXRSApplication application, ClassDoc cDoc) {
    try {
      return new JAXRSHtmlDocletWriter(application, configuration, Utils.classToPath(cDoc), cDoc.simpleTypeName()
          + ".html", Utils.classToRoot(cDoc));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void write() {
  	printPrelude("Data object: " + cDoc.simpleTypeName(), "");
    printSummary();
    printElements();
    tag("hr");
    printPostlude("");
    writer.flush();
    writer.close();
  }

  private void printElements() {
    printMembers(cDoc.fields(false), "Fields");
  }

  private void printSummary() {
    open("h2 class='classname'");
    around("span class='name'", "Data object: " + cDoc.simpleTypeName());
    around("span class='namespace'", "(" + cDoc.containingPackage().name() + ")");
    close("h2");
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

  private void printMembers(FieldDoc[] fieldDocs, String title) {
    if (fieldDocs.length == 0)
      return;
    tag("hr");
    open("table class='info' id='" + title + "'");
    around("caption class='TableCaption'", title);
    open("tbody");
    open("tr");

    around("th class='TableHeader'", "Name");
    around("th class='TableHeader'", "Type");
    around("th class='TableHeader DescriptionHeader'", "Description");
    close("tr");
    for (FieldDoc member : fieldDocs) {
      open("tr");
	    open("td id='m_" + member.name() + "'");
	    print(member.name());
	    close("td");
      open("td");
      printMemberType(member);
      close("td");
      open("td");
      Doc javaDoc = member;
      if (javaDoc.firstSentenceTags() != null)
        writer.printInlineComment(javaDoc);
      close("td");
      close("tr");

    }
    close("tbody");
    close("table");
  }

  private void printMemberType(FieldDoc field) {
    open("tt");
    final Type type = field.type();
    printMemberTypeGeneric(type);
    close("tt");
  }

  private void printMemberTypeGeneric(Type type) {
    // System.err.println("Type : " + type.qualifiedTypeName());
  	if (type.isPrimitive() || type.qualifiedTypeName().startsWith("java.lang")) {
  		print(type.simpleTypeName());
  	} else {
  		ClassDoc fDoc = type.asClassDoc();
  		if (fDoc == null || fDoc.qualifiedName().equals(cDoc.qualifiedName())
  				|| !this.pojoTypes.getResolvedTypes().contains(fDoc)) {
  			around("span title='" + type.qualifiedTypeName() + "'", type.typeName());
  		} else {
  			around("a title='" + fDoc.qualifiedTypeName() + "' href='"
  					   + Utils.urlToClass(cDoc, fDoc) + "'", fDoc.typeName());
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


}
