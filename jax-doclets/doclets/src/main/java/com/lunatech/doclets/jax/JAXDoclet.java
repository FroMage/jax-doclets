package com.lunatech.doclets.jax;

import com.sun.javadoc.ClassDoc;

public interface JAXDoclet {

  public ClassDoc forName(String className);
}
