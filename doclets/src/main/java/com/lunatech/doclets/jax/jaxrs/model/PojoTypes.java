package com.lunatech.doclets.jax.jaxrs.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;

import com.lunatech.doclets.jax.Utils;
import com.lunatech.doclets.jax.jaxrs.JAXRSConfiguration;
import com.sun.javadoc.Type;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;

public class PojoTypes {

  public static Comparator<Type> TYPE_COMPARATOR = new Comparator<Type>() {
		@Override
    public int compare(Type t0, Type t1) {
      if (t0 == t1) {
        return 0;
      }
      if (t0 == null) {
        return -1;
      }
      if (t1 == null) {
        return 1;
      }
      String qt0 = t0.qualifiedTypeName();
      String qt1 = t1.qualifiedTypeName();
      if (qt0 == qt1) {
        return 0;
      }
      if (qt0 == null) {
        return -1;
      }
      if (qt1 == null) {
        return 1;
      }
      return qt0.compareTo(qt1);
    }
  };

  private final JAXRSConfiguration config;

  public PojoTypes(JAXRSConfiguration config) {
    this.config = config;
  }

  private final Set<ClassDoc> resolvedTypes = new TreeSet<ClassDoc>(TYPE_COMPARATOR);

  private final Set<Type> unresolvedTypes = new TreeSet<Type>(TYPE_COMPARATOR);

  public Set<ClassDoc> getResolvedTypes() {
  	return Collections.unmodifiableSet(resolvedTypes);
  }

  public Set<Type> getUnresolvedTypes() {
  	return Collections.unmodifiableSet(unresolvedTypes);
  }

  public boolean resolveUsedType(Type type) {
    final ClassDoc cDoc = type.asClassDoc();
    if (isPojoToDocument(type)) {
      if (!this.resolvedTypes.contains(cDoc)) {
        this.resolvedTypes.add(cDoc);
        System.err.println("Resolved type " + cDoc.qualifiedTypeName());
        resolveFieldDtos(cDoc);
      }
      return true;
    } else if (cDoc != null) {
      this.unresolvedTypes.add(cDoc);
      return false;
    }
    return false;
  }

  private void resolveFieldDtos(ClassDoc cDoc) {
    for (FieldDoc fDoc : cDoc.fields(false)) {
      resolveUsedType(fDoc.type());
    }
  }

  public void resolveSubclassDtos() {
    int resolved = this.resolvedTypes.size();
    while (true) {
      // Keep checking until we don't find any new types, so we find subclasses
      // of field types
      for (final ClassDoc klass : config.parentConfiguration.root.classes()) {
        resolveSubclassDtos(klass);
      }
      if (this.resolvedTypes.size() == resolved) {
        // No more resolved types discovered
        break;
      }
      resolved = this.resolvedTypes.size();
    }
  }

  private void resolveSubclassDtos(ClassDoc potentialSubclass) {
    final ClassDoc superClass = potentialSubclass.superclass();
    if (superClass != null) {
      if (resolvedTypes.contains(superClass)) {
        resolveUsedType(potentialSubclass);
      } else {
        resolveSubclassDtos(superClass);
      }
    }
  }

  public boolean isPojoToDocument(Type type) {
    if (type.isPrimitive()) {
      return false;
    }
    if (type.asClassDoc() == null) {
      return false;
    }
    if (config.onlyOutputPojosMatching != null) {
      Matcher m = config.onlyOutputPojosMatching.matcher(type.qualifiedTypeName());
      return m.matches();
    }
    return true;
  }

  public List<ClassDoc> getSubclasses(ClassDoc cDoc) {
    List<ClassDoc> subClasses = new ArrayList<ClassDoc>();
    for (ClassDoc potentialSubclass : this.resolvedTypes) {
      if (isSubclass(cDoc, potentialSubclass)) {
        subClasses.add(potentialSubclass);
      }
    }
    return subClasses;
  }

  private boolean isSubclass(ClassDoc cDoc, ClassDoc potentialSubclass) {
    ClassDoc superClass = potentialSubclass.superclass();
    return (superClass != null) && (superClass.qualifiedTypeName().equals(cDoc.qualifiedTypeName()) || isSubclass(cDoc, superClass));
  }

}
