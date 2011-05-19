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
package com.lunatech.doclets.jax.jaxb.writers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.lunatech.doclets.jax.JAXConfiguration;
import com.lunatech.doclets.jax.Utils;
import com.lunatech.doclets.jax.jaxb.JAXBConfiguration;
import com.lunatech.doclets.jax.jaxb.model.JAXBClass;
import com.lunatech.doclets.jax.jaxb.model.Registry;
import com.sun.javadoc.Doc;
import com.sun.tools.doclets.formats.html.HtmlDocletWriter;

public class SummaryWriter extends com.lunatech.doclets.jax.writers.DocletWriter {

  private Registry registry;

  public SummaryWriter(JAXConfiguration configuration, Registry registry) {
    super(configuration, getWriter(configuration));
    this.registry = registry;
  }

  private static HtmlDocletWriter getWriter(JAXConfiguration configuration) {
    try {
      return new HtmlDocletWriter(configuration.parentConfiguration, "", "overview-summary.html", "");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void write() {
    printHeader();
    printMenu("Overview");
    List<JAXBClass> classes = new ArrayList<JAXBClass>(registry.getJAXBClasses());
    Collections.sort(classes);
    printClasses(classes);
    tag("hr");
    printMenu("Overview");
    printFooter();
    writer.flush();
    writer.close();
  }

  private void printClasses(Collection<JAXBClass> classes) {
    tag("hr");
    open("table class='info'");
    around("caption class='TableCaption'", "Elements");
    open("tbody");
    open("tr");
    around("th class='TableHeader'", "Name");
    around("th class='TableHeader'", "Description");
    close("tr");
    for (JAXBClass klass : classes) {
      open("tr");
      open("td");
      around("a href='" + writer.relativePath + Utils.classToPath(klass) + "/" + klass.getShortClassName() + ".html'", klass.getName());
      close("td");
      open("td");
      Doc javaDoc = klass.getJavaDoc();
      if (javaDoc != null && javaDoc.firstSentenceTags() != null)
        writer.printSummaryComment(javaDoc);
      close("td");
      close("tr");

    }
    close("tbody");
    close("table");
  }

  protected void printHeader() {
    printHeader("Overview of XML elements");
  }
}
