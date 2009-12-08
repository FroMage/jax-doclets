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

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.Type;

public class RealMethodParameter extends MethodParameter {

  private Parameter parameter;

  private MethodDoc method;

  private int parameterIndex;

  public RealMethodParameter(Parameter parameter, int parameterIndex, AnnotationDesc paramAnnotation, MethodParameterType type,
                             MethodDoc method) {
    super(paramAnnotation, type);
    this.parameter = parameter;
    this.method = method;
    this.parameterIndex = parameterIndex;
  }

  public String getDoc() {
    Parameter overriddenParameter = method.parameters()[parameterIndex];
    for (ParamTag paramTag : method.paramTags()) {
      if (overriddenParameter.name().equals(paramTag.parameterName()))
        return paramTag.parameterComment();
    }
    return "";
  }

  @Override
  protected Doc getParameterDoc() {
    return method;
  }

  @Override
  protected Type getParameterType() {
    return parameter.type();
  }

}
