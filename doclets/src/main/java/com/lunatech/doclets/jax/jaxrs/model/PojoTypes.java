package com.lunatech.doclets.jax.jaxrs.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import com.sun.javadoc.Type;
import com.sun.javadoc.ClassDoc;

public class PojoTypes {
  
  public static Comparator<Type> TYPE_COMPARATOR = new Comparator<Type>() {
		@Override
    public int compare(Type t0, Type t1) {
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

  private final Set<ClassDoc> resolvedTypes = new TreeSet<ClassDoc>(TYPE_COMPARATOR);
  
  private final Set<Type> unresolvedTypes = new TreeSet<Type>(TYPE_COMPARATOR);
  
  public Set<ClassDoc> getResolvedTypes() {
  	return Collections.unmodifiableSet(resolvedTypes);
  }
  
  public Set<Type> getUnresolvedTypes() {
  	return Collections.unmodifiableSet(unresolvedTypes);
  }
  
  public void addResolvedType(ClassDoc cDoc) {
  	this.resolvedTypes.add(cDoc);
  }
  
  public void addUnresolvedType(Type type) {
  	this.unresolvedTypes.add(type);
  }
  
}
