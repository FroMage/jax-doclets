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
package com.lunatech.doclets.jax.jaxrs.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.lunatech.doclets.jax.Utils;
import com.lunatech.doclets.jax.jaxrs.JAXRSDoclet;
import com.lunatech.doclets.jax.jaxrs.writers.ResourceWriter;
import com.sun.javadoc.Doc;
import com.sun.tools.doclets.internal.toolkit.Configuration;

public class Resource {

  Map<String, Resource> subResources = new TreeMap<String, Resource>();

  List<ResourceMethod> methods = new LinkedList<ResourceMethod>();

  String fragment;

  private Resource parent;

  public Resource(String fragment, Resource parent) {
    this.fragment = fragment;
    this.parent = parent;
  }

  private ResourceMethod getDocMethod() {
    // find the first GET method with doc
    for (ResourceMethod resourceMethod : methods) {
      if (resourceMethod.isGET()) {
        String doc = resourceMethod.getDoc();
        if (!Utils.isEmptyOrNull(doc))
          return resourceMethod;
      }
    }
    // if we don't have any GET method and only one subresource, try that
    if (methods.isEmpty() && subResources.size() == 1) {
      return subResources.values().iterator().next().getDocMethod();
    }
    return null;
  }

  public String getDoc() {
    ResourceMethod docMethod = getDocMethod();
    if (docMethod == null)
      return "";
    return docMethod.getDoc();
  }

  public Doc getJavaDoc() {
    ResourceMethod docMethod = getDocMethod();
    if (docMethod == null)
      return null;
    return docMethod.getJavaDoc();
  }

  public String getAbsolutePath() {
    if (parent != null)
      return Utils.appendURLFragments(parent.getAbsolutePath(), fragment);
    else
      return "/";
  }

  public static Resource getRootResource(List<ResourceMethod> resourceMethods) {
    Resource rootResource = new Resource("", null);
    for (ResourceMethod resourceMethod : resourceMethods) {
      rootResource.addResourceMethod(resourceMethod);
    }
    return rootResource;
  }

  private void addSubResource(String firstFragment, ResourceMethod resourceMethod) {
    Resource subResource;
    if (subResources.containsKey(firstFragment)) {
      subResource = subResources.get(firstFragment);
    } else {
      subResource = new Resource(firstFragment, this);
      subResources.put(firstFragment, subResource);
    }
    subResource.addResourceMethod(resourceMethod);
  }

  private void addResourceMethod(ResourceMethod resourceMethod) {
    String firstFragment = Utils.getFirstURLFragment(resourceMethod.getPath().substring(getAbsolutePath().length()));
    if (firstFragment == null) {
      methods.add(resourceMethod);
    } else {
      addSubResource(firstFragment, resourceMethod);
    }
  }

  public void dump(int offset) {
    dump(offset, "+ /" + fragment);
    for (ResourceMethod method : methods) {
      dump(offset + 1, "+ [M]" + method.toString());
    }
    for (String subResourceKey : subResources.keySet()) {
      Resource subResource = subResources.get(subResourceKey);
      subResource.dump(offset + 1);
    }
  }

  private void dump(int offset, String fragment) {
    for (int i = 0; i < offset; i++)
      System.err.print("| ");
    System.err.println(fragment);
  }

  public String getName() {
    return fragment;
  }

  public void write(JAXRSDoclet doclet, Configuration configuration) {
    ResourceWriter writer = new ResourceWriter(configuration, this, doclet);
    writer.write();
    for (String subResourceKey : subResources.keySet()) {
      Resource subResource = subResources.get(subResourceKey);
      subResource.write(doclet, configuration);
    }
  }

  public Map<String, Resource> getResources() {
    return subResources;
  }

  public List<ResourceMethod> getMethods() {
    return methods;
  }

  public boolean hasRealMethods() {
    if (methods.isEmpty())
      return false;
    for (ResourceMethod method : methods) {
      if (!method.isResourceLocator())
        return true;
    }
    return false;
  }

  public Resource getParent() {
    return parent;
  }
}
