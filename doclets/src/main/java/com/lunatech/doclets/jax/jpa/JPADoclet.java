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
package com.lunatech.doclets.jax.jpa;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;

import com.lunatech.doclets.jax.JAXDoclet;
import com.lunatech.doclets.jax.Utils;
import com.lunatech.doclets.jax.jpa.model.JPAClass;
import com.lunatech.doclets.jax.jpa.model.JPAMember;
import com.lunatech.doclets.jax.jpa.model.Registry;
import com.lunatech.doclets.jax.jpa.writers.GraphDataWriter;
import com.lunatech.doclets.jax.jpa.writers.GraphWriter;
import com.lunatech.doclets.jax.jpa.writers.PackageListWriter;
import com.lunatech.doclets.jax.jpa.writers.SummaryWriter;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.formats.html.ConfigurationImpl;
import com.sun.tools.doclets.formats.html.HtmlDoclet;
import com.sun.tools.doclets.internal.toolkit.AbstractDoclet;

public class JPADoclet extends JAXDoclet<JPAConfiguration> {
	
  public static final boolean isHibernatePresent;
  
  static{
    boolean test = false;
    try{
      Class.forName("org.hibernate.annotations.GenericGenerator");
      test = true;
    }catch(Throwable t){
      // no Hibernate support
    }
    isHibernatePresent = test;
  }

  private static final Class<?>[] jpaAnnotations = new Class<?>[] { Entity.class };

  public static int optionLength(final String option) {
    return HtmlDoclet.optionLength(option);
  }

  public static boolean validOptions(final String[][] options, final DocErrorReporter reporter) {
    if (!HtmlDoclet.validOptions(options, reporter)) {
      return false;
    }
    return true;
  }

  public static LanguageVersion languageVersion() {
    return AbstractDoclet.languageVersion();
  }

  private List<JPAClass> jpaClasses = new LinkedList<JPAClass>();

  private Registry registry = new Registry();

  public JPADoclet(RootDoc rootDoc) {
    super(rootDoc);
  }

  @Override
  protected JPAConfiguration makeConfiguration(ConfigurationImpl configuration) {
    return new JPAConfiguration(configuration);
  }

  public static boolean start(final RootDoc rootDoc) {
    new JPADoclet(rootDoc).start();
    return true;
  }

  private void start() {
    final ClassDoc[] classes = conf.parentConfiguration.root.classes();
    for (final ClassDoc klass : classes) {
      if (Utils.findAnnotatedClass(klass, jpaAnnotations) != null) {
        handleJPAClass(klass);
      }
    }
    for (final JPAClass klass : jpaClasses) {
      klass.write(conf);
    }
    new PackageListWriter(conf, registry).write();
    new SummaryWriter(conf, registry).write();
    new GraphWriter(conf, registry).write();
    new GraphDataWriter(conf, registry).write();
    Utils.copyResources(conf);
    Utils.copyJPAResources(conf);
  }

  private void handleJPAClass(final ClassDoc klass) {
    ClassDoc superDoc = klass.superclass();
    if (!registry.isJPAClass(klass.qualifiedTypeName()) && !klass.isPrimitive() && !klass.qualifiedTypeName().startsWith("java.")
        && !klass.isEnum()) {
      String fqName = klass.qualifiedTypeName();
      JPAClass jpaClass = new JPAClass(klass, registry, this);
      jpaClasses.add(jpaClass);
      registry.addJPAClass(jpaClass);
      // load all used types
      List<JPAMember> members = jpaClass.getMembers();
      for (JPAMember member : members) {
        Type type = member.getJavaType();
        ClassDoc doc = type.asClassDoc();
        if (doc != null && Utils.findAnnotatedClass(doc, jpaAnnotations) != null) {
          handleJPAClass(doc);
        }
      }
    }
  }

}
