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
package com.lunatech.doclets.jax.jaxrs;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Path;

import com.lunatech.doclets.jax.JAXDoclet;
import com.lunatech.doclets.jax.Utils;
import com.lunatech.doclets.jax.jaxrs.model.Resource;
import com.lunatech.doclets.jax.jaxrs.model.ResourceClass;
import com.lunatech.doclets.jax.jaxrs.model.ResourceMethod;
import com.lunatech.doclets.jax.jaxrs.tags.HTTPTaglet;
import com.lunatech.doclets.jax.jaxrs.tags.InputWrappedTaglet;
import com.lunatech.doclets.jax.jaxrs.tags.RequestHeaderTaglet;
import com.lunatech.doclets.jax.jaxrs.tags.ResponseHeaderTaglet;
import com.lunatech.doclets.jax.jaxrs.tags.ReturnWrappedTaglet;
import com.lunatech.doclets.jax.jaxrs.writers.SummaryWriter;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.formats.html.HtmlDoclet;
import com.sun.tools.doclets.internal.toolkit.AbstractDoclet;
import com.sun.tools.doclets.internal.toolkit.taglets.LegacyTaglet;

public class JAXRSDoclet implements JAXDoclet {

  public static final HtmlDoclet htmlDoclet = new HtmlDoclet();

  private static final Class<?>[] jaxrsAnnotations = new Class<?>[] { Path.class };

  public static int optionLength(final String option) {
    if ("-jaxrscontext".equals(option)) {
      return 2;
    }
    return HtmlDoclet.optionLength(option);
  }

  public static boolean validOptions(final String[][] options, final DocErrorReporter reporter) {
    return HtmlDoclet.validOptions(options, reporter);
  }

  public static LanguageVersion languageVersion() {
    return AbstractDoclet.languageVersion();
  }

  private static List<ResourceMethod> jaxrsMethods = new LinkedList<ResourceMethod>();

  public static boolean start(final RootDoc rootDoc) {
    new JAXRSDoclet(rootDoc).start();
    return true;
  }

  public JAXRSDoclet(RootDoc rootDoc) {
    htmlDoclet.configuration.root = rootDoc;
    htmlDoclet.configuration.setOptions();
    htmlDoclet.configuration.tagletManager.addCustomTag(new LegacyTaglet(new ResponseHeaderTaglet()));
    htmlDoclet.configuration.tagletManager.addCustomTag(new LegacyTaglet(new RequestHeaderTaglet()));
    htmlDoclet.configuration.tagletManager.addCustomTag(new LegacyTaglet(new HTTPTaglet()));
    htmlDoclet.configuration.tagletManager.addCustomTag(new LegacyTaglet(new ReturnWrappedTaglet()));
    htmlDoclet.configuration.tagletManager.addCustomTag(new LegacyTaglet(new InputWrappedTaglet()));
  }

  public void start() {
    final ClassDoc[] classes = htmlDoclet.configuration.root.classes();
    for (final ClassDoc klass : classes) {
      if (Utils.findAnnotatedClass(klass, jaxrsAnnotations) != null) {
        handleJAXRSClass(klass);
      }
    }
    Collections.sort(jaxrsMethods);
    Resource rootResource = Resource.getRootResource(jaxrsMethods);
    rootResource.write(this, htmlDoclet.configuration);
    new SummaryWriter(htmlDoclet.configuration, rootResource).write();
    Utils.copyResources(htmlDoclet.configuration);
  }

  private static void handleJAXRSClass(final ClassDoc klass) {
    jaxrsMethods.addAll(new ResourceClass(klass, null).getMethods());
  }

  public RootDoc getRootDoc() {
    return htmlDoclet.configuration.root;
  }

  public ClassDoc forName(String className) {
    return getRootDoc().classNamed(className);
  }

  public void warn(String string) {
    System.err.println("Warning: " + string);
  }

}
