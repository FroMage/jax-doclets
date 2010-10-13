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
import java.util.LinkedList;
import java.util.List;

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

  private AnnotationDesc rootPathAnnotation;

  private AnnotationDesc rootProducesAnnotation;

  private AnnotationDesc rootConsumesAnnotation;

  private ResourceMethod parentMethod;

  public ResourceClass(ClassDoc resourceClass, ResourceMethod methodLocator) {
    this.parentMethod = methodLocator;
    // find the annotated class or interface
    declaringClass = Utils.findAnnotatedClass(resourceClass, Path.class);
    // sub-resources may not have a path, but they're still resources
    if (declaringClass == null)
      declaringClass = resourceClass;
    rootPathAnnotation = Utils.findAnnotation(declaringClass, Path.class);
    rootProducesAnnotation = Utils.findAnnotation(declaringClass, Utils.getProducesClass());
    rootConsumesAnnotation = Utils.findAnnotation(declaringClass, Utils.getConsumesClass());
    for (final MethodDoc method : resourceClass.methods(false)) {
      MethodDoc declaringMethod = Utils.findAnnotatedMethod(declaringClass, method, Path.class, GET.class, PUT.class, DELETE.class,
                                                            HEAD.class, POST.class);
      if (declaringMethod != null) {
        methods.add(new ResourceMethod(method, declaringMethod, this));
      }
    }

  }

  public ClassDoc getDeclaringClass() {
    return declaringClass;
  }

  public String getPath() {
    String myPath;
    if (rootPathAnnotation != null) {
      myPath = (String) Utils.getAnnotationValue(rootPathAnnotation);
      if (!myPath.startsWith("/"))
        myPath = "/" + myPath;
    } else
      myPath = null;
    if (parentMethod != null)
      return Utils.appendURLFragments(parentMethod.getPath(), myPath);
    return myPath;
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
}
