package com.lunatech.doclets.jax.jpa.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

import com.lunatech.doclets.jax.JAXConfiguration;
import com.lunatech.doclets.jax.Utils;
import com.lunatech.doclets.jax.jpa.JPADoclet;
import com.lunatech.doclets.jax.jpa.writers.JPAClassWriter;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ProgramElementDoc;

public class JPAClass implements Comparable<JPAClass> {

  private Set<Column> columns = new TreeSet<Column>();

  private Set<Relation> relations = new TreeSet<Relation>();

  private ClassDoc klass;

  private String name;

  private Registry registry;

  private JPADoclet doclet;

  private Map<String, JPAMember> members = new HashMap<String, JPAMember>();

  public JPAClass(ClassDoc klass, Registry registry, JPADoclet doclet) {
    // System.err.println("Root: " + klass.name());
    this.klass = klass;
    this.registry = registry;
    this.doclet = doclet;
    AnnotationDesc tableAnnotation = Utils.findAnnotation(klass, Table.class);
    if (tableAnnotation != null) {
      name = (String) Utils.getAnnotationValue(tableAnnotation, "name");
    }
    if (name == null)
      name = klass.simpleTypeName();
    setupMembers(klass, isIDOnField(klass));
  }

  private boolean isIDOnField(ClassDoc klass) {
    for (FieldDoc field : klass.fields(false)) {
      if (Utils.findAnnotation(field, Id.class) != null)
        return true;
    }
    for (MethodDoc method : klass.methods(false)) {
      if (Utils.findAnnotation(method, Id.class) != null)
        return false;
    }
    ClassDoc superClass = klass.superclass();
    if (superClass != null && !superClass.qualifiedTypeName().startsWith("java."))
      return isIDOnField(superClass);
    // default to true?
    return true;
  }

  private void setupMembers(ClassDoc klass, boolean isIDOnField) {
    for (FieldDoc field : klass.fields(false)) {
      addProperty(field, isIDOnField);
    }
    for (MethodDoc method : klass.methods(false)) {
      addProperty(method, !isIDOnField);
    }
    ClassDoc superClass = klass.superclass();
    if (superClass != null && !superClass.qualifiedTypeName().startsWith("java."))
      setupMembers(superClass, isIDOnField);
  }

  private void addProperty(ProgramElementDoc property, boolean includeByDefault) {
    if (property.isMethod() && !isProperty((MethodDoc) property))
      return;
    if (property.isStatic())
      return;

    AnnotationDesc columnAnnotation = Utils.findAnnotation(property, javax.persistence.Column.class);
    if (columnAnnotation == null)
      columnAnnotation = Utils.findAnnotation(property, javax.persistence.JoinColumn.class);
    AnnotationDesc transientAnnotation = Utils.findAnnotation(property, Transient.class);
    boolean hasJPAAnnotation = columnAnnotation != null;
    boolean isTransient = transientAnnotation != null;
    if (property.isField()) {
      isTransient |= ((FieldDoc) property).isTransient();
    }
    // all annotated non-transient properties
    boolean include = !isTransient && (includeByDefault || hasJPAAnnotation);
    if (include)
      addElement(property, columnAnnotation);

  }

  private boolean isProperty(MethodDoc method) {
    String name = method.name();
    if (method.parameters().length != 0)
      return false;
    if (name.startsWith("get"))
      return name.length() > 3;
    if (name.startsWith("is") && method.returnType().toString().equals("boolean"))
      return name.length() > 2;
    return false;
  }

  private void addElement(ProgramElementDoc property, AnnotationDesc columnAnnotation) {
    String name = property.name();
    if (property.isMethod()) {
      if (name.startsWith("get"))
        name = name.substring(3);
      else
        name = name.substring(2);
      name = name.substring(0, 1).toLowerCase() + name.substring(1);
    }
    if (!members.containsKey(name))
      addElement(property, name, columnAnnotation);
  }

  private void addElement(ProgramElementDoc property, String name, AnnotationDesc columnAnnotation) {
    AnnotationDesc relation = Utils.findAnnotation(property, OneToMany.class, ManyToOne.class, OneToOne.class, ManyToMany.class);
    if (relation == null)
      addColumn(property, name, columnAnnotation);
    else {
      addRelation(property, name, columnAnnotation, relation);
    }
  }

  private void addRelation(ProgramElementDoc property, String propertyName, AnnotationDesc columnAnnotation, AnnotationDesc relation) {
    String name = propertyName;
    if (columnAnnotation != null) {
      String overriddenName = (String) Utils.getAnnotationValue(columnAnnotation, "name");
      if (overriddenName != null)
        name = overriddenName;
    }
    Relation element = new Relation(this, property, name, columnAnnotation, relation);
    members.put(propertyName, element);
    relations.add(element);
  }

  private void addColumn(ProgramElementDoc property, String propertyName, AnnotationDesc columnAnnotation) {
    String name = propertyName;
    if (columnAnnotation != null) {
      String overriddenName = (String) Utils.getAnnotationValue(columnAnnotation, "name");
      if (overriddenName != null)
        name = overriddenName;
    }
    Column element = new Column(this, property, name, columnAnnotation);
    members.put(propertyName, element);
    columns.add(element);
  }

  public String getQualifiedClassName() {
    return klass.qualifiedName();
  }

  public JPADoclet getDoclet() {
    return doclet;
  }

  public String getName() {
    return name;
  }

  public void write(JAXConfiguration configuration) {
    new JPAClassWriter(configuration, this).write();
  }

  public Doc getJavaDoc() {
    return klass;
  }

  public Collection<Column> getColumns() {
    return columns;
  }

  public Collection<Relation> getRelations() {
    return relations;
  }

  public Registry getRegistry() {
    return registry;
  }

  public String getPackageName() {
    return klass.containingPackage().name();
  }

  public String getShortClassName() {
    return klass.name();
  }

  public int compareTo(JPAClass other) {
    return name.compareToIgnoreCase(other.name);
  }

  public List<JPAMember> getMembers() {
    List<JPAMember> list = new ArrayList<JPAMember>(columns.size());
    list.addAll(columns);
    list.addAll(relations);
    return list;
  }

  public JPAMember getID() {
    return null;
  }

  public Relation getRelation(String name) {
    JPAMember member = members.get(name);
    if (member instanceof Relation)
      return (Relation) member;
    return null;
  }
}
