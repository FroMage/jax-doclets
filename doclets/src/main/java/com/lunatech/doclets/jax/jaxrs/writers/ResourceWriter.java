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

import com.lunatech.doclets.jax.JAXConfiguration;
import com.lunatech.doclets.jax.Utils;
import com.lunatech.doclets.jax.jaxrs.JAXRSDoclet;
import com.lunatech.doclets.jax.jaxrs.model.JAXRSApplication;
import com.lunatech.doclets.jax.jaxrs.model.MethodParameter;
import com.lunatech.doclets.jax.jaxrs.model.PojoTypes;
import com.lunatech.doclets.jax.jaxrs.model.Resource;
import com.lunatech.doclets.jax.jaxrs.model.ResourceMethod;
import com.sun.javadoc.Doc;
import com.sun.tools.doclets.formats.html.HtmlDocletWriter;

public class ResourceWriter extends DocletWriter {

  public ResourceWriter(JAXConfiguration configuration, JAXRSApplication application, Resource resource, JAXRSDoclet doclet) {
    super(configuration, getWriter(configuration, application, resource), application, resource, doclet);
  }

  private static HtmlDocletWriter getWriter(JAXConfiguration configuration, JAXRSApplication application, Resource resource) {
    String pathName = Utils.urlToSystemPath(resource);
    try {
      return new JAXRSHtmlDocletWriter(application, configuration, pathName, "index.html", Utils.urlToRoot(resource));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void write(PojoTypes types) {
    boolean isRoot = resource.getParent() == null;
    String selected = isRoot ? "Root resource" : "";
    printPrelude(isRoot, selected);
    open("dl");
    printResourceInfo();
    printSubresources();
    if (resource.hasRealMethods()) {
      printMethodOverview(resource.getMethods());
    }
    close("dl");
    if (resource.hasRealMethods()) {
      printMethodDetails(resource.getMethods(), types);
    }
    tag("hr");
    printPostlude(selected);
    writer.flush();
  }

  private void printMethodOverview(List<ResourceMethod> methods) {
    tag("hr");
    open("dt");
    print("Resource Methods");
    close("dt");
    open("dd");
    open("table class='info' id='methods-summary'");
    around("caption class='TableCaption'", "Method Summary");
    open("tbody");
    open("tr");
    around("th class='TableHeader'", "Name");
    around("th class='TableHeader'", "Description");
    close("tr");
    for (ResourceMethod method : methods) {
      // skip resource locator methods
      if (method.isResourceLocator())
        continue;
      for (String httpMethod : method.getMethods()) {
        open("tr");
        open("td");
        open("tt");
        around("a href='#" + httpMethod + "'", httpMethod + " " + Utils.getDisplayURL(this, resource, method));
        close("tt");
        close("td");
        open("td");
        Doc javaDoc = method.getJavaDoc();
        if (javaDoc != null && javaDoc.firstSentenceTags() != null)
          writer.printSummaryComment(javaDoc);
        close("td");
        close("tr");
      }
    }
    close("tbody");
    close("table");
    close("dd");
  }

  private void printMethodDetails(List<ResourceMethod> methods, PojoTypes types) {
    tag("hr");
    open("table class='info' id='methods-details'");
    around("caption class='TableCaption'", "Method Detail");
    open("tbody");
    close("tr");
    for (ResourceMethod method : methods) {
      // skip resource locator methods
      if (method.isResourceLocator())
        continue;
      open("tr");
      open("td");
      new MethodWriter(method, this, doclet, application).print(types);
      close("td");
      close("tr");
    }
    close("tbody");
    close("table");
  }

  private void printSubresources() {
    Map<String, Resource> resources = resource.getResources();
    if (resources.isEmpty())
      return;
    tag("hr");
    open("dt");
    print("Sub-Resources");
    close("dt");
    open("dd");
    open("table class='info' id='resources'");
    around("caption class='TableCaption'", "Resources");
    open("tbody");
    open("tr");
    around("th class='TableHeader'", "Name");
    around("th class='TableHeader'", "Description");
    close("tr");
    for (String subResourceKey : resources.keySet()) {
      Resource realSubResource = resources.get(subResourceKey);
      Resource subResource = deepFilter(realSubResource);
      open("tr");
      open("td");
      String path = subResource.getPathFrom(resource);
      if (subResource != realSubResource) {
        String realPath = realSubResource.getName();
        around("a href='" + realPath + "/index.html'", realPath);
        tag("br");
        print(" ↳ ");
        open("span class='deep-resource'");
        around("a href='" + path + "/index.html'", path);
        close("span");

      } else
        around("a href='" + path + "/index.html'", path);
      close("td");
      open("td");
      Doc javaDoc = subResource.getJavaDoc();
      if (javaDoc != null && javaDoc.firstSentenceTags() != null)
        writer.printSummaryComment(javaDoc);
      close("td");
      close("tr");
    }
    close("tbody");
    close("table");
    close("dd");
  }

  private Resource deepFilter(Resource resource) {
    if (resource.hasRealMethods())
      return resource;
    if (resource.getResources().size() > 1)
      return resource;
    // there cannot be any resource with no method, and no subresources
    // return the first subresource
    return deepFilter(resource.getResources().values().iterator().next());
  }

  private void printResourceInfo() {
    open("h2");
    print("Path: ");
    String jaxrscontext = getJAXRSConfiguration().jaxrscontext;
    String name = resource.getName();
    if (Utils.isEmptyOrNull(name))
      name = Utils.unStartSlashify(Utils.unEndSlashify(jaxrscontext));
    StringBuffer buf = new StringBuffer(name);
    Resource _resource = this.resource;
    String rel = "";
    while ((_resource = _resource.getParent()) != null) {
      rel = "../" + rel;
      String resourceName = _resource.getName();
      if (Utils.isEmptyOrNull(resourceName)) {
        if (!Utils.isEmptyOrNull(jaxrscontext)) {
          resourceName = Utils.unStartSlashify(Utils.unEndSlashify(jaxrscontext));
        } else {
          // start with slash
          resourceName = "/";
        }
      }
      String href = "<a href='" + rel + "index.html'>" + resourceName;
      if (resourceName.equals("/"))
        href += "</a> ";
      else
        href += "</a> / ";
      buf.insert(0, href);
    }

    if (!Utils.isEmptyOrNull(jaxrscontext))
      print("/ ");
    if (buf.length() == 0)
      print("/");
    else
      print(buf.toString());
    close("h2");
    open("div class='doc-comment'");
    Doc javaDoc = this.resource.getJavaDoc();
    if (javaDoc != null && javaDoc.tags() != null) {
      writer.printInlineComment(javaDoc);
    }
    close("div");
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
          open("dt");
          around("b", "Path parameters:");
          close("dt");
          needsPathHeading = false;
        }
        open("dd");
        around("b", param.getName());
        String regex = rm.getPathParamRegex(param.getName());
        if (regex != null) {
          around("tt", " (" + regex + ")");
        }
        print(" - " + param.getDoc());
        close("dd");
      }
      if (!needsPathHeading) {
      }
    } while (false);

  }

  private void printPrelude(boolean isRoot, String selected) {
    if (isRoot)
      printPrelude("Root Resource", selected);
    else
      printPrelude("Resource " + resource.getName(), selected);
  }

  protected void printThirdMenu() {
    open("tr");
    open("td class='NavBarCell3'");
    print("summary: ");
    printLink(!resource.getResources().isEmpty(), "#resources", "resource");
    print(" | ");
    printLink(resource.hasRealMethods(), "#methods-summary", "method");
    close("td");
    open("td class='NavBarCell3'");
    print("detail: ");
    printLink(resource.hasRealMethods(), "#methods-details", "method");
    close("td", "tr");
  }
}
