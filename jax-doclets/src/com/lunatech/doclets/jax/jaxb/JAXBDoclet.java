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
package com.lunatech.doclets.jax.jaxb;

import java.util.*;
import java.util.regex.*;

import javax.xml.bind.annotation.XmlRootElement;

import com.lunatech.doclets.jax.Utils;
import com.lunatech.doclets.jax.jaxb.model.*;
import com.lunatech.doclets.jax.jaxb.writers.*;
import com.sun.javadoc.*;
import com.sun.tools.doclets.formats.html.HtmlDoclet;
import com.sun.tools.doclets.internal.toolkit.AbstractDoclet;

public class JAXBDoclet {

  private static final Class<?>[] jaxbAnnotations = new Class<?>[] { XmlRootElement.class };

  public static int optionLength(final String option) {
	  if("-matchingjaxbnamesonly".equals(option)) {
		  return 2;
	  }
    return HtmlDoclet.optionLength(option);
  }

  public static boolean validOptions(final String[][] options, final DocErrorReporter reporter) {
    if(!HtmlDoclet.validOptions(options, reporter)) {
    	return false;
    }
	String value = Utils.getOption(options, "-matchingjaxbnamesonly");
	try {
		Pattern p = value==null ? null : Pattern.compile(value);
	} catch(Throwable t) {
		return false;
	}
    return true;
  }

  public static LanguageVersion languageVersion() {
    return AbstractDoclet.languageVersion();
  }

  private final HtmlDoclet htmlDoclet = new HtmlDoclet();

  private List<JAXBClass> jaxbClasses = new LinkedList<JAXBClass>();

  private Registry registry = new Registry();

  public JAXBDoclet(RootDoc rootDoc) {
    htmlDoclet.configuration.root = rootDoc;
    String pattern = Utils.getOption(getRootDoc().options(), "-matchingjaxbnamesonly");
    if(pattern!=null) {
    	onlyOutputJAXBClassPackagesMatching = Pattern.compile(pattern);
    }
  }

  public static boolean start(final RootDoc rootDoc) {
    new JAXBDoclet(rootDoc).start();
    return true;
  }

  private Pattern onlyOutputJAXBClassPackagesMatching;
  private void start() {
    htmlDoclet.configuration.setOptions();
    final ClassDoc[] classes = htmlDoclet.configuration.root.classes();
    for (final ClassDoc klass : classes) {
      if (Utils.findAnnotatedClass(klass, jaxbAnnotations) != null) {
        handleJAXBClass(klass);
      }
    }
    for (final JAXBClass klass : jaxbClasses) {
      //System.err.println("JAXB class: " + klass);
      klass.write(htmlDoclet.configuration);
    }
    new PackageListWriter(htmlDoclet.configuration, registry).write();
    new SummaryWriter(htmlDoclet.configuration, registry).write();
    Utils.copyResources(htmlDoclet.configuration);
  }

  private void handleJAXBClass(final ClassDoc klass) {
	  ClassDoc superDoc = klass.superclass();
    if (!registry.isJAXBClass(klass.qualifiedTypeName()) && !klass.isPrimitive() && !klass.qualifiedTypeName().startsWith("java.")
        && !klass.isEnum()) {
    	String fqName = klass.qualifiedTypeName();
		if(onlyOutputJAXBClassPackagesMatching!=null) {
			 Matcher m = onlyOutputJAXBClassPackagesMatching.matcher(fqName);
			 if(!m.matches()) {
				 return;
			 }
		}
		/*
    	do { 
	    	 String typeName = klass.typeName();
	    	boolean hasFoundMatch = false;
	    	PackageDoc packageDocs[] = this.htmlDoclet.configuration().packages;
	    	if(packageDocs==null) {
	    		break;
	    	}
    		for(PackageDoc packageDoc : packageDocs) {
    			String packageName = packageDoc.name();
    			String className = klass.asClassDoc().qualifiedName();
    			String a = packageName+"."+klass.name();
    			if(!a.equals(className)) {
    				continue;
    			}
    			if(onlyOutputJAXBClassPackagesMatching!=null) {
    				 Matcher m = onlyOutputJAXBClassPackagesMatching.matcher(className);
    				 if(!m.matches()) {
    					 continue;
    				 }
    			}
    			hasFoundMatch = true;
    			break;
	    	}
    		if(!hasFoundMatch)
    			return;
    	} while(false);
	    	 */
      JAXBClass jaxbClass = new JAXBClass(klass, registry, this);
      jaxbClasses.add(jaxbClass);
      registry.addJAXBClass(jaxbClass);
      // load all used types
      List<JAXBMember> members = jaxbClass.getMembers();
      for (JAXBMember member : members) {
        Type type = member.getJavaType();
        ClassDoc doc = type.asClassDoc();
        if (doc != null) {
          handleJAXBClass(doc);
        }
      }
    }
  }

  public RootDoc getRootDoc() {
    return htmlDoclet.configuration.root;
  }

  public ClassDoc forName(String className) {
    return getRootDoc().classNamed(className);
  }
}
