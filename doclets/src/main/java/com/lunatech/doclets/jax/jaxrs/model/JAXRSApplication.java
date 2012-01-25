package com.lunatech.doclets.jax.jaxrs.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

import javax.ws.rs.Path;

import com.lunatech.doclets.jax.Utils;
import com.lunatech.doclets.jax.jaxrs.JAXRSConfiguration;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;

public class JAXRSApplication {

  private static final Class<?>[] jaxrsAnnotations = new Class<?>[] { Path.class };

  private List<ResourceMethod> jaxrsMethods = new LinkedList<ResourceMethod>();

  private Resource rootResource;

  private final JAXRSConfiguration conf;

  public JAXRSApplication(JAXRSConfiguration conf) {
    this.conf = conf;
    discoverJAXRSResources();
  }

  private void discoverJAXRSResources() {
    final ClassDoc[] classes = conf.parentConfiguration.root.classes();
    for (final ClassDoc klass : classes) {
      if (Utils.findAnnotatedClass(klass, jaxrsAnnotations) != null) {
        handleJAXRSClass(klass);
      }
    }
    Collections.sort(jaxrsMethods);
    rootResource = buildRootResource();
  }

  private void handleJAXRSClass(final ClassDoc klass) {
    if (conf.onlyOutputResourcesMatching != null) {
      Matcher m = conf.onlyOutputResourcesMatching.matcher(klass.qualifiedTypeName());
      if(!m.matches()) {
        return;
      }
    }
    jaxrsMethods.addAll(new ResourceClass(klass, null).getMethods());
  }

  public Resource getRootResource() {
    return rootResource;
  }

  public Resource findResourceClass(ClassDoc cDoc) {
    return findResourceClass(cDoc, null, rootResource);
  }

  public Resource findResourceForMethod(ClassDoc cDoc, MethodDoc member) {
    return findResourceClass(cDoc, member, rootResource);
  }

  private Resource findResourceClass(ClassDoc cDoc, MethodDoc mDoc, Resource resource) {
    for (ResourceMethod rMethod : resource.getMethods()) {
      if (isImplementedBy(cDoc, rMethod.getDeclaringClass())) {
        if ((mDoc == null) || areEqual(mDoc, rMethod.getMethodDoc())) {
          return resource;
        }
      }
    }
    for (Resource subResource : resource.getResources().values()) {
      Resource match = findResourceClass(cDoc, mDoc, subResource);
      if (match != null) {
        return match;
      }
    }
    return null;
  }

  private boolean isImplementedBy(ClassDoc cDoc, ClassDoc declaringClass) {
    if (declaringClass.qualifiedTypeName().equals(cDoc.qualifiedTypeName())) {
      return true;
    }
    if ((declaringClass.superclass() != null) && isImplementedBy(cDoc, declaringClass.superclass())) {
      return true;
    }
    for (ClassDoc intDoc : declaringClass.interfaces()) {
      if (isImplementedBy(cDoc, intDoc)) {
        return true;
      }
    }
    return false;
  }

  static boolean areEqual(MethodDoc m1, MethodDoc m2) {
    if (!m1.qualifiedName().equals(m2.qualifiedName())) {
      return false;
    }
    Parameter[] p1 = m1.parameters();
    Parameter[] p2 = m1.parameters();

    if (p1.length != p2.length) {
      return false;
    }
    for (int i = 0; i < p1.length; i++) {
      Parameter pi1 = p1[i];
      Parameter pi2 = p2[i];

      if (!pi1.typeName().equals(pi2.typeName())) {
        return false;
      }
    }
    return true;
  }

  private Resource buildRootResource() {
    Resource rootResource = new Resource("", null);
    for (ResourceMethod resourceMethod : jaxrsMethods) {
      rootResource.addResourceMethod(resourceMethod);
    }
    // TODO: Avoid/Prune resource paths that have no resource methods (e.g. a
    // Java resource method with a multi-part path)
    return rootResource;
  }

}
