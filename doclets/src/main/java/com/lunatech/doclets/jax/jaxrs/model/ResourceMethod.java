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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
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
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;
import com.sun.javadoc.ParameterizedType;
import java.util.Arrays;

public class ResourceMethod implements Comparable<ResourceMethod> {

  public static final Class<?>[] MethodAnnotations = new Class<?>[] { GET.class, POST.class, PUT.class, HEAD.class, DELETE.class };

  private MethodDoc declaringMethod;

  private String path;

  private Map<String, String> regexFragments = new HashMap<String, String>();

  private ResourceClass resource;

  private ClassDoc declaringClass;

  private MethodDoc method;

  final List<MethodParameter> pathParameters = new ArrayList<MethodParameter>();

  final List<MethodParameter> matrixParameters = new ArrayList<MethodParameter>();

  final List<MethodParameter> queryParameters = new ArrayList<MethodParameter>();

  final List<MethodParameter> headerParameters = new ArrayList<MethodParameter>();

  final List<MethodParameter> cookieParameters = new ArrayList<MethodParameter>();

  final List<MethodParameter> formParameters = new ArrayList<MethodParameter>();

  private List<AnnotationDesc> methods = new LinkedList<AnnotationDesc>();

  private AnnotationDesc producesAnnotation;

  private AnnotationDesc consumesAnnotation;

  private MethodParameter inputParameter;

  private MethodOutput output;

  private ResourceClass resourceLocator;

  private Class<?> formClass;

  public ResourceMethod(MethodDoc method, MethodDoc declaringMethod, ResourceClass resource) {
    this.resource = resource;
    this.method = method;
    this.declaringClass = resource.getDeclaringClass();
    this.declaringMethod = declaringMethod;
    this.output = new MethodOutput(declaringMethod);
    try {
      formClass = Class.forName("org.jboss.resteasy.annotations.Form");
    } catch (ClassNotFoundException e) {
      // we won't support @Form
    }
    setupPath();
    setupParameters();
    setupMethods();
    setupMIMEs();
    // is this a resource locator?
    if (methods.isEmpty() && !declaringMethod.returnType().isPrimitive()) {
      // Handle Class style resource locator factory methods
      Type t = declaringMethod.returnType();
    	if("java.lang.Class".equals(t.qualifiedTypeName())) {
        ParameterizedType p = t.asParameterizedType();
       	 	if (p != null) {
       	 		t = p.typeArguments()[0];
       	 	}
    	}
       resourceLocator = new ResourceClass(t.asClassDoc(), this);
    }
  }

  public ResourceClass getResourceClass() {
    return resource;
  }

  public ClassDoc getDeclaringClass() {
    return declaringClass;
  }

  public MethodDoc getMethodDoc() {
    return method;
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
        pathParameters.add(new RealMethodParameter(parameter, i, pathParamAnnotation, MethodParameterType.Path, declaringMethod));
        continue;
      }
      final AnnotationDesc matrixParamAnnotation = Utils.findParameterAnnotation(declaringMethod, parameter, i, MatrixParam.class);
      if (matrixParamAnnotation != null) {
        String name = (String) Utils.getAnnotationValue(matrixParamAnnotation);
        matrixParameters.add(new RealMethodParameter(parameter, i, matrixParamAnnotation, MethodParameterType.Matrix, declaringMethod));
        continue;
      }
      final AnnotationDesc queryParamAnnotation = Utils.findParameterAnnotation(declaringMethod, parameter, i, QueryParam.class);
      if (queryParamAnnotation != null) {
        String name = (String) Utils.getAnnotationValue(queryParamAnnotation);
        queryParameters.add(new RealMethodParameter(parameter, i, queryParamAnnotation, MethodParameterType.Query, declaringMethod));
        continue;
      }
      final AnnotationDesc cookieParamAnnotation = Utils.findParameterAnnotation(declaringMethod, parameter, i, CookieParam.class);
      if (cookieParamAnnotation != null) {
        String name = (String) Utils.getAnnotationValue(cookieParamAnnotation);
        cookieParameters.add(new RealMethodParameter(parameter, i, cookieParamAnnotation, MethodParameterType.Cookie, declaringMethod));
        continue;
      }
      final AnnotationDesc formParamAnnotation = Utils.findParameterAnnotation(declaringMethod, parameter, i, FormParam.class);
      if (formParamAnnotation != null) {
        String name = (String) Utils.getAnnotationValue(formParamAnnotation);
        formParameters.add(new RealMethodParameter(parameter, i, formParamAnnotation, MethodParameterType.Form, declaringMethod));
        continue;
      }
      final AnnotationDesc headerParamAnnotation = Utils.findParameterAnnotation(declaringMethod, parameter, i, HeaderParam.class);
      if (headerParamAnnotation != null) {
        String name = (String) Utils.getAnnotationValue(headerParamAnnotation);
        headerParameters.add(new RealMethodParameter(parameter, i, headerParamAnnotation, MethodParameterType.Header, declaringMethod));
        continue;
      }
      if (formClass != null) {
        final AnnotationDesc formAnnotation = Utils.findParameterAnnotation(declaringMethod, parameter, i, formClass);
        if (formAnnotation != null) {
          walkFormParameter(parameter.type().asClassDoc());
          continue;
        }
      }
      final AnnotationDesc contextAnnotation = Utils.findParameterAnnotation(declaringMethod, parameter, i, Context.class);
      if (contextAnnotation == null) {
        this.inputParameter = new RealMethodParameter(parameter, i, null, MethodParameterType.Input, declaringMethod);
      }
    }
  }

  private void walkFormParameter(ClassDoc formDoc) {
    // walk all fields
    for (FieldDoc field : formDoc.fields(false)) {
      final AnnotationDesc pathParamAnnotation = Utils.findAnnotation(field, PathParam.class);
      if (pathParamAnnotation != null) {
        String name = (String) Utils.getAnnotationValue(pathParamAnnotation);
        pathParameters.add(new FormFieldParameter(field, pathParamAnnotation, MethodParameterType.Path));
        continue;
      }
      final AnnotationDesc matrixParamAnnotation = Utils.findAnnotation(field, MatrixParam.class);
      if (matrixParamAnnotation != null) {
        matrixParameters.add(new FormFieldParameter(field, matrixParamAnnotation, MethodParameterType.Matrix));
        continue;
      }
      final AnnotationDesc queryParamAnnotation = Utils.findAnnotation(field, QueryParam.class);
      if (queryParamAnnotation != null) {
        String name = (String) Utils.getAnnotationValue(queryParamAnnotation);
        queryParameters.add(new FormFieldParameter(field, queryParamAnnotation, MethodParameterType.Query));
        continue;
      }
      final AnnotationDesc headerParamAnnotation = Utils.findAnnotation(field, HeaderParam.class);
      if (headerParamAnnotation != null) {
        String name = (String) Utils.getAnnotationValue(headerParamAnnotation);
        headerParameters.add(new FormFieldParameter(field, headerParamAnnotation, MethodParameterType.Header));
        continue;
      }
      final AnnotationDesc cookieParamAnnotation = Utils.findAnnotation(field, CookieParam.class);
      if (cookieParamAnnotation != null) {
        String name = (String) Utils.getAnnotationValue(cookieParamAnnotation);
        cookieParameters.add(new FormFieldParameter(field, cookieParamAnnotation, MethodParameterType.Cookie));
        continue;
      }
      final AnnotationDesc formParamAnnotation = Utils.findAnnotation(field, FormParam.class);
      if (formParamAnnotation != null) {
        formParameters.add(new FormFieldParameter(field, formParamAnnotation, MethodParameterType.Form));
        continue;
      }
      //Recurse into the embedded @Form field
      if(formClass != null) {
        final AnnotationDesc formAnnotation = Utils.findAnnotation(field, formClass);
        if(formAnnotation != null) {
            walkFormParameter(field.type().asClassDoc());
            continue;
        }
      }
      
      final AnnotationDesc contextAnnotation = Utils.findAnnotation(field, Context.class);
      if (contextAnnotation == null) {
        this.inputParameter = new FormFieldParameter(field, null, MethodParameterType.Input);
        continue;
      }
    }
    // and methods
    for (MethodDoc method : formDoc.methods(false)) {
      if (!method.returnType().qualifiedTypeName().equals("void") || method.parameters().length != 1 || !method.name().startsWith("set"))
        continue;
      Parameter parameter = method.parameters()[0];
      final AnnotationDesc pathParamAnnotation = Utils.findParameterAnnotation(method, parameter, 0, PathParam.class);
      if (pathParamAnnotation != null) {
        String name = (String) Utils.getAnnotationValue(pathParamAnnotation);
        pathParameters.add(new FormMethodParameter(method, pathParamAnnotation, MethodParameterType.Path));
        continue;
      }
      final AnnotationDesc matrixParamAnnotation = Utils.findParameterAnnotation(method, parameter, 0, MatrixParam.class);
      if (matrixParamAnnotation != null) {
        String name = (String) Utils.getAnnotationValue(matrixParamAnnotation);
        matrixParameters.add(new FormMethodParameter(method, matrixParamAnnotation, MethodParameterType.Matrix));
        continue;
      }
      final AnnotationDesc queryParamAnnotation = Utils.findParameterAnnotation(method, parameter, 0, QueryParam.class);
      if (queryParamAnnotation != null) {
        String name = (String) Utils.getAnnotationValue(queryParamAnnotation);
        queryParameters.add(new FormMethodParameter(method, queryParamAnnotation, MethodParameterType.Query));
        continue;
      }
      final AnnotationDesc headerParamAnnotation = Utils.findParameterAnnotation(method, parameter, 0, HeaderParam.class);
      if (headerParamAnnotation != null) {
        String name = (String) Utils.getAnnotationValue(headerParamAnnotation);
        headerParameters.add(new FormMethodParameter(method, headerParamAnnotation, MethodParameterType.Header));
        continue;
      }
      final AnnotationDesc cookieParamAnnotation = Utils.findParameterAnnotation(method, parameter, 0, CookieParam.class);
      if (cookieParamAnnotation != null) {
        String name = (String) Utils.getAnnotationValue(cookieParamAnnotation);
        cookieParameters.add(new FormMethodParameter(method, cookieParamAnnotation, MethodParameterType.Cookie));
        continue;
      }
      final AnnotationDesc formParamAnnotation = Utils.findParameterAnnotation(method, parameter, 0, FormParam.class);
      if (formParamAnnotation != null) {
        String name = (String) Utils.getAnnotationValue(formParamAnnotation);
        formParameters.add(new FormMethodParameter(method, formParamAnnotation, MethodParameterType.Form));
        continue;
      }
      // I'm not sure if @Form can be used on setter methods on an @Form field, but just in case...
      if(formClass != null) {
        //recurse into @Form parameters
        final AnnotationDesc formAnnotation = Utils.findParameterAnnotation(method, parameter, 0, formClass);
        if(formAnnotation != null) {
            walkFormParameter(parameter.type().asClassDoc());
            continue;
        }
      }
      final AnnotationDesc contextAnnotation = Utils.findParameterAnnotation(method, parameter, 0, Context.class);
      if (contextAnnotation == null) {
        this.inputParameter = new FormMethodParameter(method, null, MethodParameterType.Input);
      }
    }
  }

  private void setupPath() {
    final AnnotationDesc pathAnnotation = Utils.findMethodAnnotation(declaringClass, method, Path.class);
    final String rootPath = resource.getPath();
    if (pathAnnotation != null) {
      String path = (String) Utils.getAnnotationValue(pathAnnotation);
      path = Utils.removeFragmentRegexes(path, regexFragments);
      this.path = Utils.appendURLFragments(rootPath, path);
    } else
      this.path = rootPath;
  }

  public int compareTo(ResourceMethod other) {
    return path.compareTo(other.path);
  }

  public String toString() {
    StringBuilder strbuf = new StringBuilder(path);
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
    AnnotationDesc produces = getProducesAnnotation();
    if (produces == null) {
      return Collections.emptyList();
    } else {
      return Arrays.asList(Utils.getAnnotationValues(produces));
    }
  }

  public List<String> getConsumes() {
    AnnotationDesc consumes = getConsumesAnnotation();
    if (consumes == null) {
      return Collections.emptyList();
    } else {
      return Arrays.asList(Utils.getAnnotationValues(consumes));
    }
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

  public List<MethodParameter> getQueryParameters() {
    if (resource.isSubResource()) {
      List<MethodParameter> allQueryParameters = new ArrayList<MethodParameter>(resource.getParentMethod()
          .getQueryParameters());
      allQueryParameters.addAll(queryParameters);
      return allQueryParameters;
    }
    return queryParameters;
  }

  public List<MethodParameter> getPathParameters() {
    if (resource.isSubResource()) {
      List<MethodParameter> allPathParameters = new ArrayList<MethodParameter>(resource.getParentMethod().getPathParameters());
      allPathParameters.addAll(pathParameters);
      return allPathParameters;
    }
    return pathParameters;
  }

  public List<MethodParameter> getMatrixParameters() {
    if (resource.isSubResource()) {
      List<MethodParameter> allMatrixParameters = new ArrayList<MethodParameter>(resource.getParentMethod()
          .getMatrixParameters());
      allMatrixParameters.addAll(matrixParameters);
      return allMatrixParameters;
    }
    return matrixParameters;
  }

  public List<MethodParameter> getCookieParameters() {
    if (resource.isSubResource()) {
      List<MethodParameter> allCookieParameters = new ArrayList<MethodParameter>(resource.getParentMethod()
          .getCookieParameters());
      allCookieParameters.addAll(cookieParameters);
      return allCookieParameters;
    }
    return cookieParameters;
  }

  public List<MethodParameter> getHeaderParameters() {
    if (resource.isSubResource()) {
      List<MethodParameter> allHeaderParameters = new ArrayList<MethodParameter>(resource.getParentMethod()
          .getHeaderParameters());
      allHeaderParameters.addAll(headerParameters);
      return allHeaderParameters;
    }
    return headerParameters;
  }

  public List<MethodParameter> getFormParameters() {
    if (resource.isSubResource()) {
      List<MethodParameter> allFormParameters = new ArrayList<MethodParameter>(resource.getParentMethod().getFormParameters());
      allFormParameters.addAll(formParameters);
      return allFormParameters;
    }
    return formParameters;
  }

  public String getPath() {
    return path;
  }

  public MethodParameter getInputParameter() {
    return inputParameter;
  }

  public boolean isGET() {
    return hasHTTPMethod(GET.class);
  }

  public boolean hasHTTPMethod(Class<? extends Annotation> method) {
    return Utils.findAnnotation(methods.toArray(new AnnotationDesc[0]), method) != null;
  }

  public String getURL(Resource resource) {
    StringBuilder strbuf = new StringBuilder(resource.getAbsolutePath());

    for (MethodParameter parameter : getMatrixParameters()) {
      strbuf.append(";");
      strbuf.append(parameter.getName());
      strbuf.append("=…");
    }

    boolean first = true;
    for (MethodParameter parameter : getQueryParameters()) {
      if(first) {
        strbuf.append("?");
      } else {
        strbuf.append("&amp;");
      }
      strbuf.append(parameter.getName());
      strbuf.append("=…");
      first = false;
    }
    return strbuf.toString();
  }

  public AnnotationDesc getProducesAnnotation() {
    if (producesAnnotation != null)
      return producesAnnotation;
    if (resource.isSubResource())
      return resource.getProducesAnnotation();
    return null;
  }

  public AnnotationDesc getConsumesAnnotation() {
    if (consumesAnnotation != null)
      return consumesAnnotation;
    if (resource.isSubResource())
      return resource.getConsumesAnnotation();
    return null;
  }

  public ResourceClass getResourceLocator() {
    return resourceLocator;
  }

  public boolean isResourceLocator() {
    return resourceLocator != null;
  }

  public String getAPIFunctionName() {
    return declaringClass.name() + "." + declaringMethod.name();
  }

  public String getPathParamRegex(String name) {
    if (regexFragments.containsKey(name))
      return regexFragments.get(name);
    return resource.getPathParamRegex(name);
  }
}
