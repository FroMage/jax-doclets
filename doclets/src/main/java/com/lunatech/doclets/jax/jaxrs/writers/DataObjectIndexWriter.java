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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.lunatech.doclets.jax.JAXConfiguration;
import com.lunatech.doclets.jax.Utils;
import com.lunatech.doclets.jax.jaxb.model.JAXBClass;
import com.lunatech.doclets.jax.jaxrs.JAXRSDoclet;
import com.lunatech.doclets.jax.jaxrs.model.JAXRSApplication;
import com.lunatech.doclets.jax.jaxrs.model.PojoTypes;
import com.lunatech.doclets.jax.jaxrs.model.Resource;
import com.lunatech.doclets.jax.jaxrs.model.ResourceMethod;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;
import com.sun.tools.doclets.formats.html.HtmlDocletWriter;
import com.sun.tools.doclets.internal.toolkit.util.DirectoryManager;

public class DataObjectIndexWriter extends DocletWriter {

  private PojoTypes pojoTypes;

  public DataObjectIndexWriter(JAXConfiguration configuration, JAXRSApplication application, JAXRSDoclet doclet, PojoTypes types) {
    super(configuration, getWriter(configuration, application), application, application.getRootResource(), doclet);
    this.pojoTypes = types;
  }

  private static HtmlDocletWriter getWriter(JAXConfiguration configuration, JAXRSApplication application) {
    try {
      return new JAXRSHtmlDocletWriter(application, configuration, "", "objects-index.html", "");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void write() {
    printPrelude("Data object index", "Data objects");

    Comparator<Type> typeSimpleNameComparator = new Comparator<Type>() {
			@Override
      public int compare(Type t0, Type t1) {
				return t0.simpleTypeName().compareTo(t1.simpleTypeName());
      }
    };
    List<Type> resolvedClasses = new ArrayList<Type>(pojoTypes.getResolvedTypes());
		Collections.sort(resolvedClasses, typeSimpleNameComparator);

    printClasses(resolvedClasses);


		// TODO: Separate section headers for unresolvable types, plus config param
    // List<Type> unresolvedClasses = new
    // ArrayList<Type>(pojoTypes.getUnresolvedTypes());
    // Collections.sort(unresolvedClasses, typeSimpleNameComparator);
    // printClasses(unresolvedClasses);

    tag("hr");
    printPostlude("Data objects");
    writer.flush();
    writer.close();
  }

  private void printClasses(Collection<Type> classes) {
    tag("hr");
    open("table class='info'");
    around("caption class='TableCaption'", "Elements");
    open("tbody");
    open("tr");
    around("th class='TableHeader'", "Data Type");
    around("th class='TableHeader DescriptionHeader'", "Description");
    close("tr");
    for (Type klass : classes) {
    	ClassDoc cDoc = klass.asClassDoc();

      open("tr");
      open("td");
      if (cDoc != null) {
      	around("a href='" + writer.relativePath + getLink(cDoc) + "'", cDoc.simpleTypeName());
      } else {
      	print(klass.simpleTypeName());
      }
      close("td");
      open("td");
      if (cDoc != null) { // && cDoc.firstSentenceTags() != null
      	writer.printSummaryComment(cDoc);
      }
      close("td");
      close("tr");

    }
    close("tbody");
    close("table");
  }

  static String getLink(final ClassDoc type) {
    return Utils.classToPath(type) + "/" + type.typeName() + ".html";
  }

}
