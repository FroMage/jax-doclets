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

  public ResourceClass(ClassDoc resourceClass) {
    // find the annotated class or interface
    declaringClass = Utils.findAnnotatedClass(resourceClass, Path.class);
    rootPathAnnotation = Utils.findAnnotation(declaringClass, Path.class);
    rootProducesAnnotation = Utils.findAnnotation(declaringClass, Utils.getProducesClass());
    rootConsumesAnnotation = Utils.findAnnotation(declaringClass, Utils.getConsumesClass());
    for (final MethodDoc method : resourceClass.methods(false)) {
      MethodDoc declaringMethod = Utils.findAnnotatedMethod(declaringClass, method, Path.class);
      if (declaringMethod != null) {
        methods.add(new ResourceMethod(method, declaringMethod, this));
      }
    }

  }

  public ClassDoc getDeclaringClass() {
    return declaringClass;
  }

  public AnnotationDesc getRootPathAnnotation() {
    return rootPathAnnotation;
  }

  public AnnotationDesc getProducesAnnotation() {
    return rootProducesAnnotation;
  }

  public AnnotationDesc getConsumesAnnotation() {
    return rootConsumesAnnotation;
  }

  public Collection<ResourceMethod> getMethods() {
    return methods;
  }
}
