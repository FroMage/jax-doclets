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
import java.util.Map;

import com.lunatech.doclets.jax.JAXConfiguration;
import com.lunatech.doclets.jax.Utils;
import com.lunatech.doclets.jax.jaxrs.model.Resource;
import com.lunatech.doclets.jax.jaxrs.model.ResourceMethod;
import com.sun.javadoc.Doc;
import com.sun.tools.doclets.formats.html.HtmlDocletWriter;

public class SummaryWriter extends com.lunatech.doclets.jax.writers.DocletWriter {

  private Resource resource;

  public SummaryWriter(JAXConfiguration configuration, Resource resource) {
    super(configuration, getWriter(configuration));
    this.resource = resource;
  }

  private static HtmlDocletWriter getWriter(JAXConfiguration configuration) {
    try {
      return new HtmlDocletWriter(configuration.parentConfiguration, "", "overview-summary.html", "");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void write() {
    printPrelude("Overview of resources", "Overview");
    printResources();
    tag("hr");
    printPostlude("Overview");
    writer.flush();
    writer.close();
  }

  private void printResources() {
    tag("hr");
    open("table class='info'");
    around("caption class='TableCaption'", "Resources");
    open("tbody");
    open("tr class='subheader'");
    around("th class='TableHeader'", "Resource");
    around("th class='TableHeader'", "Description");
    around("th class='TableHeader'", "Methods");
    close("tr");
    printResource(resource);
    close("tbody");
    close("table");
  }

  private void printResource(Resource resource) {
    if (resource.hasRealMethods())
      printResourceLine(resource);
    // now recurse
    Map<String, Resource> subResources = resource.getResources();
    for (String name : subResources.keySet()) {
      Resource subResource = subResources.get(name);
      printResource(subResource);
    }
  }

  private void printResourceLine(Resource resource) {
    open("tr");
    open("td");
    String path = Utils.urlToPath(resource);
    if (path.length() == 0)
      path = ".";
    open("a href='" + path + "/index.html'");
    around("tt", Utils.getAbsolutePath(this, resource));
    close("a");
    close("td");
    open("td");
    Doc javaDoc = resource.getJavaDoc();
    if (javaDoc != null && javaDoc.firstSentenceTags() != null)
      writer.printSummaryComment(javaDoc);
    close("td");
    open("td");
    boolean first = true;
    for (ResourceMethod method : resource.getMethods()) {
      for (String httpMethod : method.getMethods()) {
        if (!first)
          print(", ");
        open("a href='" + path + "/index.html#" + httpMethod + "'");
        around("tt", httpMethod);
        close("a");
        first = false;
      }
    }
    close("td");
    close("tr");
  }

  protected void printHeader() {
    printHeader("Overview of resources");
  }

  protected void printOtherMenuItems(String selected) {
    printMenuItem("Index", writer.relativePath + "overview-index.html", selected);
    printMenuItem("Root resource", writer.relativePath + "index.html", selected);
  }
}
