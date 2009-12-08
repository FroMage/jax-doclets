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
package com.lunatech.doclets.jax.jaxrs.writers;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lunatech.doclets.jax.Utils;
import com.lunatech.doclets.jax.Utils.InvalidJaxTypeException;
import com.lunatech.doclets.jax.Utils.JaxType;
import com.lunatech.doclets.jax.jaxrs.JAXRSDoclet;
import com.lunatech.doclets.jax.jaxrs.model.MethodOutput;
import com.lunatech.doclets.jax.jaxrs.model.MethodParameter;
import com.lunatech.doclets.jax.jaxrs.model.ResourceMethod;
import com.lunatech.doclets.jax.jaxrs.tags.HTTPTaglet;
import com.lunatech.doclets.jax.jaxrs.tags.RequestHeaderTaglet;
import com.lunatech.doclets.jax.jaxrs.tags.ResponseHeaderTaglet;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.formats.html.TagletOutputImpl;

public class MethodWriter extends DocletWriter {

  private ResourceMethod method;

  public MethodWriter(ResourceMethod method, ResourceWriter resourceWriter, JAXRSDoclet doclet) {
    super(resourceWriter.getConfiguration(), resourceWriter.getWriter(), resourceWriter.getResource(), doclet);
    this.method = method;
  }

  public void print() {
    for (String httpMethod : method.getMethods()) {
      printMethod(httpMethod);
    }
  }

  private void printMethod(String httpMethod) {
    printHTTPExample(httpMethod);
    if (!Utils.isEmptyOrNull(method.getDoc())) {
      open("p");
      print(method.getDoc());
      close("p");
    }
    open("dl");
    printInput();
    printOutput();
    printParameters(method.getQueryParameters(), "Query");
    // done on resource
    // printParameters(method.getPathParameters(), "Path");
    printParameters(method.getMatrixParameters(), "Matrix");
    printParameters(method.getFormParameters(), "Form");
    printParameters(method.getCookieParameters(), "Cookie");
    printParameters(method.getHeaderParameters(), "Header");
    printMIMEs(method.getProduces(), "Produces");
    printMIMEs(method.getConsumes(), "Consumes");
    printHTTPCodes();
    printHTTPRequestHeaders();
    printHTTPResponseHeaders();
    close("dl");
  }

  private void printTaglets(String tagletName) {
    MethodDoc javaDoc = method.getJavaDoc();
    TagletOutputImpl output = new TagletOutputImpl("");
    Set<String> tagletsSet = new HashSet<String>();
    tagletsSet.add(tagletName);
    Utils.genTagOuput(configuration.tagletManager, javaDoc, configuration.tagletManager.getCustomTags(javaDoc), writer
        .getTagletWriterInstance(false), output, tagletsSet);
    writer.print(output.toString());
  }

  private void printHTTPRequestHeaders() {
    printTaglets(RequestHeaderTaglet.NAME);
  }

  private void printHTTPResponseHeaders() {
    printTaglets(ResponseHeaderTaglet.NAME);
  }

  private void printHTTPCodes() {
    printTaglets(HTTPTaglet.NAME);
  }

  private void printOutput() {
    open("dt");
    around("b", "Output:");
    close("dt");

    MethodOutput output = method.getOutput();

    if (output.isOutputWrapped()) {
      for (int i = 0; i < output.getOutputWrappedCount(); i++) {
        open("dd");
        String typeName = output.getWrappedOutputType(i);
        JaxType returnType = null;
        try {
          returnType = Utils.parseType(typeName, method.getJavaDoc().containingClass(), doclet);
        } catch (InvalidJaxTypeException e) {
          doclet.warn("Invalid @returnWrapped type: " + typeName);
          e.printStackTrace();
        }
        if (returnType != null)
          printOutputType(returnType);
        else
          around("tt", escape(typeName));
        if (output.getOutputDoc(i) != null) {
          print(" - ");
          print(output.getOutputDoc(i));
        }
        close("dd");
      }
    } else {
      open("dd");
      Type returnType = output.getOutputType();
      printOutputType(returnType);
      if (output.getOutputDoc() != null) {
        print(" - ");
        print(output.getOutputDoc());
      }
      close("dd");
    }
  }

  private void printOutputType(Type type) {
    open("tt");
    printOutputGenericType(type);
    close("tt");
  }

  private void printOutputGenericType(Type type) {
    String link = null;
    if (!type.isPrimitive()) {
      link = Utils.getExternalLink(configuration, type, writer);
    }

    if (link == null) {
      print(type.qualifiedTypeName());
    } else {
      around("a href='" + link + "'", type.typeName());
    }
    ParameterizedType pType = type.asParameterizedType();
    if (pType != null) {
      boolean first = true;
      print("&lt;");
      for (Type genericType : pType.typeArguments()) {
        if (first) {
          first = false;
        } else {
          print(",");
        }
        printOutputGenericType(genericType);
      }
      print("&gt;");
    }
    print(type.dimension());
  }

  private void printOutputType(JaxType type) {
    open("tt");
    printOutputGenericType(type);
    close("tt");
  }

  private void printOutputGenericType(JaxType type) {
    String link = null;
    if (type.getType() == null) {
      doclet.warn("Type information not found: " + type.getTypeName());
      print(type.getTypeName());
      return;
    }

    if (!type.getType().isPrimitive()) {
      link = Utils.getExternalLink(configuration, type.getType(), writer);
    }

    if (link == null) {
      print(type.getType().qualifiedTypeName());
    } else {
      around("a href='" + link + "'", type.getType().typeName());
    }
    if (type.hasParameters()) {
      boolean first = true;
      print("&lt;");
      for (JaxType genericType : type.getParameters()) {
        if (first) {
          first = false;
        } else {
          print(",");
        }
        printOutputGenericType(genericType);
      }
      print("&gt;");
    }
    print(type.getDimension());
  }

  private void printInput() {
    MethodParameter inputParameter = method.getInputParameter();
    if (inputParameter == null)
      return;
    open("dt");
    around("b", "Input:");
    close("dt");
    open("dd");
    Type type = inputParameter.getType();
    String link = null;
    String typeName = inputParameter.getTypeString();
    if (!type.isPrimitive()) {
      // check if we have it wrapped
      if (inputParameter.isWrapped()) {
        String wrappedType = inputParameter.getWrappedType();
        link = Utils.getExternalLink(configuration, wrappedType, writer);
        if (link != null)
          typeName = Utils.getLinkTypeName(link);
      } else {
        link = Utils.getExternalLink(configuration, type, writer);
        if (link != null)
          typeName = type.typeName() + type.dimension();
      }
    }
    if (link == null) {
      around("tt", typeName);
    } else {
      around("a href='" + link + "'", typeName);
    }
    String d = inputParameter.getDoc();
    if (d != null) {
      writer.print(" - ");
      writer.print(d);
    }
    close("dd");
  }

  private void printParameters(Map<String, MethodParameter> parameters, String header) {
    if (parameters.isEmpty())
      return;
    open("dt");
    around("b", header + " parameters:");
    close("dt");
    for (MethodParameter param : parameters.values()) {
      open("dd");
      around("b", param.getName());
      print(" - " + param.getDoc());
      close("dd");
    }
  }

  private void printMIMEs(List<String> mimes, String header) {
    if (!mimes.isEmpty()) {
      open("dt");
      around("b", header + ":");
      close("dt");
      for (String mime : mimes) {
        open("dd");
        print(mime);
        close("dd");
      }
    }
  }

  private void printHTTPExample(String httpMethod) {
    open("pre");
    String absPath = resource.getAbsolutePath();
    String jaxrscontext = Utils.getOption(this.configuration.root.options(), "-jaxrscontext");
    if (jaxrscontext != null) {
      absPath = jaxrscontext + absPath;
    }

    print(httpMethod + " " + absPath);
    Map<String, MethodParameter> matrixParameters = method.getMatrixParameters();
    if (!matrixParameters.isEmpty()) {
      for (String name : matrixParameters.keySet()) {
        print(";");
        print(name);
        print("=…");
      }
    }
    Map<String, MethodParameter> queryParameters = method.getQueryParameters();
    if (!queryParameters.isEmpty()) {
      print("?");
      boolean first = true;
      for (String name : queryParameters.keySet()) {
        if (!first)
          print("&");
        print(name);
        print("=…");
        first = false;
      }
    }
    print("\n");

    Map<String, MethodParameter> headerParameters = method.getHeaderParameters();
    if (!headerParameters.isEmpty()) {
      for (String name : headerParameters.keySet()) {
        print(name);
        print(": …\n");
      }
    }
    Map<String, MethodParameter> cookieParameters = method.getCookieParameters();
    if (!cookieParameters.isEmpty()) {
      for (String name : headerParameters.keySet()) {
        print("Cookie: ");
        print(name);
        print("=…\n");
      }
    }

    Map<String, MethodParameter> formParameters = method.getFormParameters();
    if (!formParameters.isEmpty()) {
      print("\n");
      boolean first = true;
      for (String name : formParameters.keySet()) {
        if (!first)
          print("&");
        print(name);
        print("=…");
        first = false;
      }
    }
    print("\n");
    close("pre");
  }
}
