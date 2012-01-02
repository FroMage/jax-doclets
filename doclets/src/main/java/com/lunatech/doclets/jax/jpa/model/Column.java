package com.lunatech.doclets.jax.jpa.model;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ProgramElementDoc;

public class Column extends JPAMember {

  public Column(JPAClass jpaClass, ProgramElementDoc property, String name, AnnotationDesc columnAnnotation) {
    super(jpaClass, property, name, columnAnnotation);
  }

}
