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
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.Type;

public class MethodParameter {

  private Parameter parameter;

  private AnnotationDesc paramAnnotation;

  private MethodParameterType type;

  private MethodDoc method;

  private int parameterIndex;

  public MethodParameter(Parameter parameter, int parameterIndex, AnnotationDesc paramAnnotation, MethodParameterType type, MethodDoc method) {
    this.parameter = parameter;
    this.paramAnnotation = paramAnnotation;
    this.type = type;
    this.method = method;
    this.parameterIndex = parameterIndex;
  }

  public String getName() {
    return (String) Utils.getAnnotationValue(paramAnnotation);
  }

  public String getDoc() {
    Parameter overriddenParameter = method.parameters()[parameterIndex];
    for (ParamTag paramTag : method.paramTags()) {
      if (overriddenParameter.name().equals(paramTag.parameterName()))
        return paramTag.parameterComment();
    }
    return "";
  }

  public String getTypeName() {
    return parameter.type().qualifiedTypeName() + parameter.type().dimension();
  }

  public Type getType() {
    return parameter.type();
  }
}
