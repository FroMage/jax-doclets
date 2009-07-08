package com.lunatech.doclets.jax.jaxb.model;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ProgramElementDoc;

public class Element extends JAXBMember {

  private String wrappedName;

  public Element(JAXBClass klass, ProgramElementDoc property, String name, AnnotationDesc xmlElementAnnotation) {
    super(klass, property, name, xmlElementAnnotation);
  }

  public Element(JAXBClass klass, ProgramElementDoc property, String wrapperName, String name, AnnotationDesc xmlElementAnnotation) {
    super(klass, property, wrapperName, xmlElementAnnotation);
    this.wrappedName = name;
  }

  public boolean isWrapped() {
    return wrappedName != null;
  }

  public String getWrappedName() {
    return wrappedName;
  }
}
