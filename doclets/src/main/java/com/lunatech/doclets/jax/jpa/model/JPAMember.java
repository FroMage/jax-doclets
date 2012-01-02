package com.lunatech.doclets.jax.jpa.model;

import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import com.lunatech.doclets.jax.Utils;
import com.lunatech.doclets.jax.jpa.JPADoclet;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.Doc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Type;

public class JPAMember implements Comparable<JPAMember> {

  protected AnnotationDesc columnAnnotation;

  protected ProgramElementDoc property;

  protected String name;

  protected JPAClass klass;

  private boolean isID;

  private String sequence;

  public JPAMember(JPAClass klass, ProgramElementDoc property, String name, AnnotationDesc columnAnnotation) {
    this.columnAnnotation = columnAnnotation;
    this.property = property;
    this.name = name;
    this.klass = klass;
    this.isID = Utils.findAnnotation(property, Id.class) != null;
    if (JPADoclet.isHibernatePresent)
      lookupSequence();
  }

  private void lookupSequence() {
    AnnotationDesc generatedValue = Utils.findAnnotation(property, GeneratedValue.class);
    if (generatedValue == null)
      return;
    String generator = (String) Utils.getAnnotationValue(generatedValue, "generator");
    if (generator == null)
      return;
    Class<?> genericGeneratorClass;
    try {
      genericGeneratorClass = Class.forName("org.hibernate.annotations.GenericGenerator");
    } catch (ClassNotFoundException e) {
      return;
    }
    List<AnnotationDesc> genericGenerators = Utils.findAnnotations(property.containingClass(), genericGeneratorClass);
    AnnotationDesc genericGenerator = null;
    for (AnnotationDesc gen : genericGenerators) {
      String name = (String) Utils.getAnnotationValue(gen, "name");
      if (name != null && name.equals(generator)) {
        genericGenerator = gen;
      }
    }
    if (genericGenerator == null)
      return;
    AnnotationValue[] parameters = (AnnotationValue[]) Utils.getAnnotationValue(genericGenerator, "parameters");
    if (parameters == null)
      return;
    for (AnnotationValue parameter : parameters) {
      String name = (String) Utils.getAnnotationValue((AnnotationDesc) parameter.value(), "name");
      if (name != null && name.equals("sequence")) {
        this.sequence = (String) Utils.getAnnotationValue((AnnotationDesc) parameter.value(), "value");
        return;
      }
    }
  }

  public String getSequence() {
    return sequence;
  }

  public String getName() {
    return name;
  }

  public Doc getJavaDoc() {
    return property;
  }

  private Type getType() {
    if (property.isMethod()) {
      return ((MethodDoc) property).returnType();
    } else
      return ((FieldDoc) property).type();
  }

  public boolean isCollectionOrArray() {
    return Utils.isCollection(getType()) || isArray();
  }

  public boolean isCollection() {
    if (Utils.isCollection(getType())) {
      return true;
    }
    if (isArray()) {
      return true;
    }
    return false;
  }

  public boolean isArray() {
    return Utils.isArray(getType());
  }

  public Type getJavaType() {
    return Utils.getCollectionType(getType(), klass.getDoclet());
  }

  public String getJavaTypeName() {
    return getJavaType().qualifiedTypeName();
  }

  public String getSQLType() {
    String typeName = getJavaTypeName();
    if (typeName.equals("java.lang.String")) {
      if (Utils.findAnnotation(property, Lob.class) != null)
        return "text";
      Integer length = columnAnnotation == null ? null : (Integer) Utils.getAnnotationValue(columnAnnotation, "length");
      String size = length != null ? ("[" + length + "]") : "";
      return "varchar" + size;
    }
    if (typeName.equals("java.lang.Character") || typeName.equals("char"))
      return "char";
    if (typeName.equals("java.util.Date") || typeName.equals("java.sql.Date"))
      return "date";
    if (typeName.equals("java.sql.Timestamp"))
      return "timestamp";
    if (typeName.equals("java.lang.Integer") || typeName.equals("int"))
      return "int";
    if (typeName.equals("java.lang.Long") || typeName.equals("long"))
      return "long";
    if (typeName.equals("java.lang.Short") || typeName.equals("short"))
      return "short";
    if (typeName.equals("java.lang.Byte") || typeName.equals("byte")) {
      return "byte";
    }
    if (typeName.equals("java.math.BigDecimal")) {
      if (columnAnnotation == null)
        return "decimal";
      Integer precision = (Integer) Utils.getAnnotationValue(columnAnnotation, "precision");
      Integer scale = (Integer) Utils.getAnnotationValue(columnAnnotation, "scale");
      StringBuffer name = new StringBuffer("decimal[");
      name.append(precision != null ? precision : "?").append(",");
      name.append(scale != null ? scale : "?");
      return name.append("]").toString();
    }
    if (typeName.equals("java.lang.Float") || typeName.equals("float"))
      return "float";
    if (typeName.equals("java.lang.Double") || typeName.equals("double"))
      return "double";
    if (typeName.equals("java.lang.Boolean") || typeName.equals("boolean"))
      return "boolean";

    return typeName;
  }

  public boolean isJPAType() {
    Type type = getJavaType();
    return !type.isPrimitive() && klass.getRegistry().isJPAClass(type.qualifiedTypeName());
  }

  public boolean isID() {
    return isID;
  }

  public int compareTo(JPAMember other) {
    return name.compareToIgnoreCase(other.name);
  }
}
