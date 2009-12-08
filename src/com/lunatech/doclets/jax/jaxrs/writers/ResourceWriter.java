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
import java.util.List;
import java.util.Map;

import com.lunatech.doclets.jax.Utils;
import com.lunatech.doclets.jax.jaxrs.JAXRSDoclet;
import com.lunatech.doclets.jax.jaxrs.model.MethodParameter;
import com.lunatech.doclets.jax.jaxrs.model.Resource;
import com.lunatech.doclets.jax.jaxrs.model.ResourceMethod;
import com.sun.javadoc.Doc;
import com.sun.tools.doclets.formats.html.ConfigurationImpl;
import com.sun.tools.doclets.formats.html.HtmlDocletWriter;
import com.sun.tools.doclets.internal.toolkit.Configuration;

public class ResourceWriter extends DocletWriter {

  public ResourceWriter(Configuration configuration, Resource resource, JAXRSDoclet doclet) {
    super(configuration, getWriter(configuration, resource), resource, doclet);
  }

  private static HtmlDocletWriter getWriter(Configuration configuration, Resource resource) {
    String pathName = Utils.urlToPath(resource);
    System.err.println(pathName);
    try {
      return new HtmlDocletWriter((ConfigurationImpl) configuration, pathName, "index.html", Utils.urlToRoot(resource));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void write() {
    boolean isRoot = resource.getParent() == null;
    String selected = isRoot ? "Root resource" : "";
    printHeader();
    printMenu(selected);
    printResourceInfo();
    printSubresources();
    printMethods();
    tag("hr");
    printMenu(selected);
    printFooter();
    writer.flush();
  }

  private void printMethods() {
    List<ResourceMethod> methods = resource.getMethods();
    boolean hasMethods = false;
    for (ResourceMethod method : methods) {
      if (!method.isResourceLocator()) {
        hasMethods = true;
        break;
      }
    }
    if (!hasMethods)
      return;
    tag("hr");
    open("table");
    open("tr");
    open("th");
    print("Methods");
    close("th");
    close("tr");
    for (ResourceMethod method : methods) {
      // skip resource locator methods
      if (method.isResourceLocator())
        continue;
      open("tr");
      open("td");
      new MethodWriter(method, this, doclet).print();
      close("td");
      close("tr");
    }
    close("table");
  }

  private void printSubresources() {
    Map<String, Resource> resources = resource.getResources();
    if (resources.isEmpty())
      return;
    tag("hr");
    open("table");
    open("tr");
    open("th colspan='2'");
    print("Resources");
    close("th");
    close("tr");
    open("tr class='subheader'");
    open("td");
    print("Name");
    close("td");
    open("td");
    print("Description");
    close("td");
    close("tr");
    for (String subResourceKey : resources.keySet()) {
      Resource subResource = resources.get(subResourceKey);
      open("tr");
      open("td");
      open("a href='" + subResource.getName() + "/index.html'");
      print(subResource.getName());
      close("a");
      close("td");
      open("td");
      Doc javaDoc = subResource.getJavaDoc();
      if (javaDoc == null) {
        // Got a method like this?
        // @PUT
        // @Path("cancelled")
        // public PutCancelledResponse setCancelled() {
        // Then there won't be a javadoc on the class for it. Rather we pull the
        // javadoc off the method itself.
        do {
          List<ResourceMethod> methods = subResource.getMethods();
          if (methods == null || methods.size() != 1)
            break;
          ResourceMethod rm = methods.get(0);
          javaDoc = rm.getJavaDoc();
        } while (false);
      }
      if (javaDoc != null && javaDoc.firstSentenceTags() != null)
        writer.printSummaryComment(javaDoc);
      close("td");
      close("tr");
    }
    close("table");
  }

  private void printResourceInfo() {
    open("h2");
    print("Path: ");
    String jaxrscontext = Utils.getOption(this.configuration.root.options(), "-jaxrscontext");
    String name = resource.getName();
    if (Utils.isEmptyOrNull(name))
      name = jaxrscontext == null ? "/" : Utils.slashify(jaxrscontext);
    StringBuffer buf = new StringBuffer(name);
    Resource _resource = this.resource;
    String rel = "";
    while ((_resource = _resource.getParent()) != null) {
      rel = "../" + rel;
      String resourceName = _resource.getName();
      if (Utils.isEmptyOrNull(resourceName)) {
        if (!Utils.isEmptyOrNull(jaxrscontext)) {
          resourceName = Utils.slashify(jaxrscontext);
        } else {
          // start with slash
          resourceName = "/";
        }
      }
      String href = "<a href='" + rel + "index.html'>" + resourceName;
      if (_resource.getName().length() == 0)
        href += "</a> ";
      else
        href += "</a> / ";
      buf.insert(0, href);
    }

    print(buf.toString());
    close("h2");
    Doc javaDoc = this.resource.getJavaDoc();
    if (javaDoc != null && javaDoc.tags() != null) {
      writer.printInlineComment(javaDoc);
    }
    do {
      boolean needsPathHeading = true;
      List<ResourceMethod> lrm = this.resource.getMethods();
      if (lrm.size() == 0) {
        // not expected (resource with no methods)
        break;
      }
      // All methods on same resource, so path should be same
      ResourceMethod rm = lrm.get(0);
      Map<String, MethodParameter> parameters = rm.getPathParameters();
      for (MethodParameter param : parameters.values()) {
        if (needsPathHeading) {
          open("dl");
          open("dt");
          around("b", "Path parameters:");
          close("dt");
          needsPathHeading = false;
        }
        open("dd");
        around("b", param.getName());
        print(" - " + param.getDoc());
        close("dd");
      }
      if (!needsPathHeading) {
        close("dl");
      }
    } while (false);

  }

  private void printHeader() {
    printHeader("Resource " + resource.getName());
  }

}
