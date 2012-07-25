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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import com.lunatech.doclets.jax.Utils;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;

public class ResourceClass {

  private List<ResourceMethod> methods = new LinkedList<ResourceMethod>();

  private ClassDoc declaringClass;

  private String applicationRootPath;

  private AnnotationDesc rootPathAnnotation;

  private AnnotationDesc rootProducesAnnotation;

  private AnnotationDesc rootConsumesAnnotation;

  private ResourceMethod parentMethod;

  private String path;

  private Map<String, String> regexFragments = new HashMap<String, String>();

  public ResourceClass(ClassDoc resourceClass, ResourceMethod methodLocator) {
    this(resourceClass, methodLocator, null);
  }

  public ResourceClass(ClassDoc resourceClass, ResourceMethod methodLocator, String applicationRootPath) {
    this.parentMethod = methodLocator;
    this.applicationRootPath = applicationRootPath;
    // find the annotated class or interface
    declaringClass = Utils.findAnnotatedClass(resourceClass, Path.class);
    // sub-resources may not have a path, but they're still resources
    if (declaringClass == null)
      declaringClass = resourceClass;
    rootPathAnnotation = Utils.findAnnotation(declaringClass, Path.class);
    rootProducesAnnotation = Utils.findAnnotation(declaringClass, Utils.getProducesClass());
    rootConsumesAnnotation = Utils.findAnnotation(declaringClass, Utils.getConsumesClass());
    // this needs to be done before we create the methods
    setupPath();
    Map<String, List<MethodDoc>> handledMethods = new HashMap<String, List<MethodDoc>>();
    do {
      METHODS: for (final MethodDoc method : resourceClass.methods(false)) {
        // only consider methods we haven't already overridden
        List<MethodDoc> overridingMethods = handledMethods.get(method.name());
        if (overridingMethods != null) {
          for (MethodDoc overridingMethod : overridingMethods) {
            if (overridingMethod.overrides(method))
              // skip it we've already done it
              continue METHODS;
          }
        }
        MethodDoc declaringMethod = Utils.findAnnotatedMethod(declaringClass, method, Path.class, GET.class, PUT.class, DELETE.class,
                                                              HEAD.class, POST.class);
        if (declaringMethod != null) {
          methods.add(new ResourceMethod(method, declaringMethod, this));
          // ok we've handled it
          if (overridingMethods == null) {
            overridingMethods = new LinkedList<MethodDoc>();
            handledMethods.put(method.name(), overridingMethods);
          }
          overridingMethods.add(method);
        }
      }
      resourceClass = resourceClass.superclass();
    } while (resourceClass != null);
  }

  private void setupPath() {
    if (rootPathAnnotation != null) {
      path = (String) Utils.getAnnotationValue(rootPathAnnotation);
      path = Utils.removeFragmentRegexes(path, regexFragments);
      if (!path.startsWith("/"))
        path = "/" + path;
    } else
      path = null;
    if (parentMethod != null)
      path = Utils.appendURLFragments(parentMethod.getPath(), path);
    if (parentMethod == null && applicationRootPath != null)
      path = Utils.appendURLFragments("/", applicationRootPath, path);
  }

  public ClassDoc getDeclaringClass() {
    return declaringClass;
  }

  public String getPath() {
    return path;
  }

  public AnnotationDesc getProducesAnnotation() {
    if (rootProducesAnnotation != null)
      return rootProducesAnnotation;
    if (parentMethod != null)
      return parentMethod.getProducesAnnotation();
    return null;
  }

  public AnnotationDesc getConsumesAnnotation() {
    if (rootConsumesAnnotation != null)
      return rootConsumesAnnotation;
    if (parentMethod != null)
      return parentMethod.getConsumesAnnotation();
    return null;
  }

  public Collection<ResourceMethod> getMethods() {
    List<ResourceMethod> allMethods = new LinkedList<ResourceMethod>(methods);
    for (ResourceMethod method : methods) {
      if (method.isResourceLocator())
        allMethods.addAll(method.getResourceLocator().getMethods());
    }
    return allMethods;
  }

  public ResourceMethod getParentMethod() {
    return parentMethod;
  }

  public boolean isSubResource() {
    return parentMethod != null;
  }

  public String getPathParamRegex(String name) {
    if (regexFragments.containsKey(name))
      return regexFragments.get(name);
    if (isSubResource())
      return parentMethod.getPathParamRegex(name);
    return null;
  }
}
