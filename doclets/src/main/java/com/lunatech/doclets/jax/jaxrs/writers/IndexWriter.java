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
import com.lunatech.doclets.jax.jaxrs.model.Resource;
import com.lunatech.doclets.jax.jaxrs.model.ResourceMethod;
import com.sun.javadoc.Doc;
import com.sun.tools.doclets.formats.html.HtmlDocletWriter;

public class IndexWriter extends DocletWriter {

  public IndexWriter(JAXConfiguration configuration, JAXRSApplication application, JAXRSDoclet doclet) {
    super(configuration, getWriter(configuration, application), application, application.getRootResource(), doclet);
  }

  private static HtmlDocletWriter getWriter(JAXConfiguration configuration, JAXRSApplication application) {
    try {
      return new JAXRSHtmlDocletWriter(application, configuration, "", "overview-index.html", "");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void write() {
    printPrelude("Resource index", "Index");
    printResources();
    tag("hr");
    printPostlude("Index");
    writer.flush();
    writer.close();
  }

  private void printResources() {
    tag("hr");
    open("table class='info'");
    around("caption class='TableCaption'", "Resources");
    open("tbody");
    open("tr");
    around("th class='TableHeader'", "Method");
    around("th class='TableHeader'", "URL");
    around("th class='TableHeader'", "Description");
    close("tr");
    printResource(resource);
    close("tbody");
    close("table");
  }

  private void printResource(Resource resource) {
    for (ResourceMethod method : resource.getMethods()) {
      // skip resource locator methods
      if (method.isResourceLocator())
        continue;
      for (String httpMethod : method.getMethods()) {
        printMethod(resource, method, httpMethod);
      }
    }
    for (String name : resource.getResources().keySet()) {
      Resource subResource = resource.getResources().get(name);
      printResource(subResource);
    }
  }

  private void printMethod(Resource resource, ResourceMethod method, String httpMethod) {
    String path = Utils.urlToPath(resource);
    if (path.length() == 0)
      path = ".";

    open("tr");
    open("td");
    open("a href='" + path + "/index.html#" + httpMethod + "'");
    around("tt", httpMethod);
    close("a");
    close("td");
    open("td");
    open("a href='" + path + "/index.html'");
    around("tt", Utils.getDisplayURL(this, resource, method));
    close("a");
    close("td");
    open("td");
    Doc javaDoc = method.getJavaDoc();
    if (javaDoc != null && javaDoc.firstSentenceTags() != null)
      writer.printSummaryComment(javaDoc);
    close("td");
    close("tr");
  }

  protected void printHeader() {
    printHeader("Resource index");
  }

}
