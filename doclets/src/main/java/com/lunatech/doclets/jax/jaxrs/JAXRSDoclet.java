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

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import javax.ws.rs.Path;

import com.lunatech.doclets.jax.JAXDoclet;
import com.lunatech.doclets.jax.Utils;
import com.lunatech.doclets.jax.jaxrs.model.Resource;
import com.lunatech.doclets.jax.jaxrs.model.ResourceClass;
import com.lunatech.doclets.jax.jaxrs.model.ResourceMethod;
import com.lunatech.doclets.jax.jaxrs.tags.HTTPTaglet;
import com.lunatech.doclets.jax.jaxrs.tags.IncludeTaglet;
import com.lunatech.doclets.jax.jaxrs.tags.InputWrappedTaglet;
import com.lunatech.doclets.jax.jaxrs.tags.RequestHeaderTaglet;
import com.lunatech.doclets.jax.jaxrs.tags.ResponseHeaderTaglet;
import com.lunatech.doclets.jax.jaxrs.tags.ReturnWrappedTaglet;
import com.lunatech.doclets.jax.jaxrs.writers.IndexWriter;
import com.lunatech.doclets.jax.jaxrs.writers.SummaryWriter;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SourcePosition;
import com.sun.tools.doclets.formats.html.ConfigurationImpl;
import com.sun.tools.doclets.formats.html.HtmlDoclet;
import com.sun.tools.doclets.internal.toolkit.AbstractDoclet;
import com.sun.tools.doclets.internal.toolkit.taglets.LegacyTaglet;

public class JAXRSDoclet extends JAXDoclet<JAXRSConfiguration> {

  public final HtmlDoclet htmlDoclet = new HtmlDoclet();

  private static final Class<?>[] jaxrsAnnotations = new Class<?>[] { Path.class };

  public static int optionLength(final String option) {
    if ("-jaxrscontext".equals(option) || "-pathexcludefilter".equals(option)) {
      return 2;
    }
    if ("-disablehttpexample".equals(option)
        || "-disablejavascriptexample".equals(option)) {
      return 1;
    }
    return HtmlDoclet.optionLength(option);
  }

  public static boolean validOptions(final String[][] options, final DocErrorReporter reporter) {
    return HtmlDoclet.validOptions(options, reporter);
  }

  public static LanguageVersion languageVersion() {
    return AbstractDoclet.languageVersion();
  }

  private List<ResourceMethod> jaxrsMethods = new LinkedList<ResourceMethod>();

  public static boolean start(final RootDoc rootDoc) {
    new JAXRSDoclet(rootDoc).start();
    return true;
  }

  public JAXRSDoclet(RootDoc rootDoc) {
    super(rootDoc);
    htmlDoclet.configuration.tagletManager.addCustomTag(new LegacyTaglet(new ResponseHeaderTaglet()));
    htmlDoclet.configuration.tagletManager.addCustomTag(new LegacyTaglet(new RequestHeaderTaglet()));
    htmlDoclet.configuration.tagletManager.addCustomTag(new LegacyTaglet(new HTTPTaglet()));
    htmlDoclet.configuration.tagletManager.addCustomTag(new LegacyTaglet(new ReturnWrappedTaglet()));
    htmlDoclet.configuration.tagletManager.addCustomTag(new LegacyTaglet(new InputWrappedTaglet()));
    htmlDoclet.configuration.tagletManager.addCustomTag(new LegacyTaglet(new IncludeTaglet()));
  }

  @Override
  protected JAXRSConfiguration makeConfiguration(ConfigurationImpl configuration) {
    return new JAXRSConfiguration(configuration);
  }

        public void filterMethods() {
                if (conf.pathExcludeFilters.isEmpty())
                        return;

                System.out.println("Resource methods exclude filters are defined");
                
                // collection for paths removing, since jaxrsMethods is immutable
                List<ResourceMethod> toRemove = new LinkedList<ResourceMethod>();
                for (String regexpFilter : conf.pathExcludeFilters) {
                        System.out.println("Filtering upon filter " + regexpFilter);
                        Pattern p = Pattern.compile(regexpFilter);
                        Iterator<ResourceMethod> irm = jaxrsMethods.iterator();
                        while (irm.hasNext()) {
                                ResourceMethod rm = irm.next();

                                if (p.matcher(rm.getPath()).matches()) {
                                        System.out.println("Resource method removed: " + rm);
                                        toRemove.add(rm);
                                }
                        }
                }
                int beforeRemoving = jaxrsMethods.size();
                jaxrsMethods.removeAll(toRemove);
                System.out.println("Resource methods removed. Was " + beforeRemoving + ", now " + jaxrsMethods.size());
        }

  public void start() {
    final ClassDoc[] classes = conf.parentConfiguration.root.classes();
    for (final ClassDoc klass : classes) {
      if (Utils.findAnnotatedClass(klass, jaxrsAnnotations) != null) {
        handleJAXRSClass(klass);
      }
    }
    Collections.sort(jaxrsMethods);
    filterMethods();

    Resource rootResource = Resource.getRootResource(jaxrsMethods);
    rootResource.write(this, conf);
    new IndexWriter(conf, rootResource).write();
    new SummaryWriter(conf, rootResource).write();
    Utils.copyResources(conf);
  }

  private void handleJAXRSClass(final ClassDoc klass) {
    jaxrsMethods.addAll(new ResourceClass(klass, null).getMethods());
  }

  public void warn(String warning) {
    conf.parentConfiguration.root.printWarning(warning);
  }

  public void printError(SourcePosition position, String error) {
    conf.parentConfiguration.root.printError(position, error);
  }
}
