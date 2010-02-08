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

import com.lunatech.doclets.jax.Utils;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;

public abstract class MethodParameter {

  protected AnnotationDesc paramAnnotation;

  protected MethodParameterType type;

  public MethodParameter(AnnotationDesc paramAnnotation, MethodParameterType type) {
    this.paramAnnotation = paramAnnotation;
    this.type = type;
  }

  public String getName() {
    return (String) Utils.getAnnotationValue(paramAnnotation);
  }

  public abstract String getDoc();

  public boolean isWrapped() {
    Type parameterType = getType();
    return !parameterType.isPrimitive() && parameterType.qualifiedTypeName().equals("java.lang.String")
           && Utils.getTag(getParameterDoc(), "inputWrapped") != null;
  }

  public String getWrappedType() {
    if (!isWrapped())
      return null;
    Tag tag = Utils.getTag(getParameterDoc(), "inputWrapped");
    return tag.text();
  }

  protected abstract Type getParameterType();

  public abstract Doc getParameterDoc();

  public abstract Tag[] getFirstSentenceTags();

  public String getTypeString() {
    return getParameterType().qualifiedTypeName() + getParameterType().dimension();
  }

  public Type getType() {
    return getParameterType();
  }
}
