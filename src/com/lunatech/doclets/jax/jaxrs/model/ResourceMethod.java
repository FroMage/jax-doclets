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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import com.lunatech.doclets.jax.Utils;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.Tag;

public class ResourceMethod implements Comparable<ResourceMethod> {

  public static final Class<?>[] MethodAnnotations = new Class<?>[] { GET.class, POST.class, PUT.class, HEAD.class, DELETE.class };

  private MethodDoc declaringMethod;

  private String path;

  private ResourceClass resource;

  private ClassDoc declaringClass;

  private MethodDoc method;

  final Map<String, MethodParameter> pathParameters = new HashMap<String, MethodParameter>();

  final Map<String, MethodParameter> matrixParameters = new HashMap<String, MethodParameter>();

  final Map<String, MethodParameter> queryParameters = new HashMap<String, MethodParameter>();

  private List<AnnotationDesc> methods = new LinkedList<AnnotationDesc>();

  private AnnotationDesc producesAnnotation;

  private AnnotationDesc consumesAnnotation;

  private MethodParameter inputParameter;

  private MethodOutput output;

  public ResourceMethod(MethodDoc method, MethodDoc declaringMethod, ResourceClass resource) {
    this.resource = resource;
    this.method = method;
    this.declaringClass = resource.getDeclaringClass();
    this.declaringMethod = declaringMethod;
    this.output = new MethodOutput(declaringMethod);
    setupPath();
    setupParameters();
    setupMethods();
    setupMIMEs();
  }

  public MethodOutput getOutput() {
    return output;
  }

  private void setupMIMEs() {
    producesAnnotation = Utils.findMethodAnnotation(declaringClass, method, Utils.getProducesClass());
    consumesAnnotation = Utils.findMethodAnnotation(declaringClass, method, Utils.getConsumesClass());
    if (producesAnnotation == null) {
      producesAnnotation = resource.getProducesAnnotation();
    }
    if (consumesAnnotation == null) {
      consumesAnnotation = resource.getConsumesAnnotation();
    }
  }

  private void setupMethods() {
    for (Class<?> methodAnnotation : MethodAnnotations) {
      final AnnotationDesc annotation = Utils.findMethodAnnotation(declaringClass, method, methodAnnotation);
      if (annotation != null)
        methods.add(annotation);
    }
  }

  private void setupParameters() {
    int i = -1;
    for (final Parameter parameter : method.parameters()) {
      i++;
      final AnnotationDesc pathParamAnnotation = Utils.findParameterAnnotation(declaringMethod, parameter, i, PathParam.class);
      if (pathParamAnnotation != null) {
        String name = (String) Utils.getAnnotationValue(pathParamAnnotation);
        pathParameters.put(name, new MethodParameter(parameter, i, pathParamAnnotation, MethodParameterType.Path, declaringMethod));
        continue;
      }
      final AnnotationDesc matrixParamAnnotation = Utils.findParameterAnnotation(declaringMethod, parameter, i, MatrixParam.class);
      if (matrixParamAnnotation != null) {
        String name = (String) Utils.getAnnotationValue(matrixParamAnnotation);
        matrixParameters.put(name, new MethodParameter(parameter, i, matrixParamAnnotation, MethodParameterType.Matrix, declaringMethod));
        continue;
      }
      final AnnotationDesc queryParamAnnotation = Utils.findParameterAnnotation(declaringMethod, parameter, i, QueryParam.class);
      if (queryParamAnnotation != null) {
        String name = (String) Utils.getAnnotationValue(queryParamAnnotation);
        queryParameters.put(name, new MethodParameter(parameter, i, queryParamAnnotation, MethodParameterType.Query, declaringMethod));
        continue;
      }
      final AnnotationDesc contextAnnotation = Utils.findParameterAnnotation(declaringMethod, parameter, i, Context.class);
      if (contextAnnotation == null) {
        this.inputParameter = new MethodParameter(parameter, i, null, MethodParameterType.Input, declaringMethod);
      }
    }
  }

  private void setupPath() {
    final AnnotationDesc pathAnnotation = Utils.findMethodAnnotation(declaringClass, method, Path.class);
    final String rootPath = (String) Utils.getAnnotationValue(resource.getRootPathAnnotation());

    if (pathAnnotation != null) {
      String path = (String) Utils.getAnnotationValue(pathAnnotation);
      this.path = Utils.appendURLFragments(rootPath, path);
    } else
      this.path = rootPath;
  }

  public int compareTo(ResourceMethod other) {
    return path.compareTo(other.path);
  }

  public String toString() {
    StringBuffer strbuf = new StringBuffer(path);
    strbuf.append(" ");
    for (AnnotationDesc method : methods) {
      strbuf.append(method.annotationType().name());
      strbuf.append(" ");
    }
    return strbuf.toString();
  }

  public List<String> getMethods() {
    List<String> httpMethods = new ArrayList<String>(methods.size());
    for (AnnotationDesc method : methods) {
      httpMethods.add(method.annotationType().name());
    }
    return httpMethods;
  }

  public List<String> getProduces() {
    if (producesAnnotation == null)
      return Collections.emptyList();
    List<String> producedMIMEs = new ArrayList<String>();
    for (String mime : Utils.getAnnotationValues(producesAnnotation)) {
      producedMIMEs.add(mime);
    }
    return producedMIMEs;
  }

  public List<String> getConsumes() {
    if (consumesAnnotation == null)
      return Collections.emptyList();
    List<String> consumedMIMEs = new ArrayList<String>();
    for (String mime : Utils.getAnnotationValues(consumesAnnotation)) {
      consumedMIMEs.add(mime);
    }
    return consumedMIMEs;
  }

  public MethodDoc getJavaDoc() {
    return declaringMethod;
  }

  public String getDoc() {
    return declaringMethod.commentText();
  }

  public Tag[] getDocFirstSentence() {
    return declaringMethod.firstSentenceTags();
  }

  public Map<String, MethodParameter> getQueryParameters() {
    return queryParameters;
  }

  public Map<String, MethodParameter> getPathParameters() {
    return pathParameters;
  }

  public Map<String, MethodParameter> getMatrixParameters() {
    return matrixParameters;
  }

  public String getPath() {
    return path;
  }

  public MethodParameter getInputParameter() {
    return inputParameter;
  }

  public boolean isGET() {
    for (AnnotationDesc method : methods) {
      if (method.annotationType().name().equals("GET"))
        return true;
    }
    return false;
  }

  public String getURL(Resource resource) {
    StringBuffer strbuf = new StringBuffer(resource.getAbsolutePath());
    Map<String, MethodParameter> queryParameters = getQueryParameters();
    if (!queryParameters.isEmpty()) {
      strbuf.append("?");
      boolean first = true;
      for (String name : queryParameters.keySet()) {
        if (!first)
          strbuf.append("&");
        strbuf.append(name);
        first = false;
      }
    }
    return strbuf.toString();
  }

}
