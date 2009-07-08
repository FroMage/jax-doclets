package com.lunatech.doclets.jax.jaxb.model;

import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;

import com.lunatech.doclets.jax.Utils;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Type;

public class JAXBMember implements Comparable<JAXBMember> {

  protected AnnotationDesc xmlAnnotation;

  protected ProgramElementDoc property;

  protected String name;

  protected JAXBClass klass;

  private boolean isIDREF;

  private boolean isID;

  public JAXBMember(JAXBClass klass, ProgramElementDoc property, String name, AnnotationDesc xmlAnnotation) {
    this.xmlAnnotation = xmlAnnotation;
    this.property = property;
    this.name = name;
    this.klass = klass;
    this.isIDREF = Utils.findAnnotation(property, XmlIDREF.class) != null;
    this.isID = Utils.findAnnotation(property, XmlID.class) != null;
  }

  public String getName() {
    return name;
  }

  public Doc getJavaDoc() {
    return property;
  }

  public Type getJavaType() {
    Type type;
    if (property.isMethod()) {
      type = ((MethodDoc) property).returnType();
    } else
      type = ((FieldDoc) property).type();
    Type collectionType = Utils.findSuperType(type, "java.util.Collection");
    // FIXME: this is dodgy at best
    if (collectionType != null) {
      Type[] types = type.asParameterizedType().typeArguments();
      if (types.length == 1)
        return types[0];
      return klass.getDoclet().forName("java.lang.Object");
    }
    return type;
  }

  public String getJavaTypeName() {
    return getJavaType().qualifiedTypeName();
  }

  public boolean isJAXBType() {
    Type type = getJavaType();
    return !type.isPrimitive() && klass.getRegistry().isJAXBClass(type.qualifiedTypeName());
  }

  public String getXSDType() {
    String typeName = getJavaTypeName();
    if (typeName.equals("java.lang.String"))
      return "xsd:string";
    if (typeName.equals("java.lang.Character") || typeName.equals("char"))
      return "xsd:string";
    if (typeName.equals("java.util.Date"))
      return "xsd:datetime";
    if (typeName.equals("java.lang.Integer") || typeName.equals("int"))
      return "xsd:int";
    if (typeName.equals("java.lang.Long") || typeName.equals("long"))
      return "xsd:long";
    if (typeName.equals("java.lang.Short") || typeName.equals("short"))
      return "xsd:short";
    if (typeName.equals("java.lang.Byte") || typeName.equals("byte"))
      return "xsd:byte";
    if (typeName.equals("java.lang.Float") || typeName.equals("float"))
      return "xsd:float";
    if (typeName.equals("java.lang.Double") || typeName.equals("double"))
      return "xsd:double";
    if (typeName.equals("java.lang.Boolean") || typeName.equals("boolean"))
      return "xsd:boolean";
    if (typeName.equals("java.lang.Object"))
      return "xsd:any";

    ClassDoc type = getJavaType().asClassDoc();
    if (type.isEnum()) {
      FieldDoc[] constants = type.enumConstants();
      StringBuffer ret = new StringBuffer();
      boolean first = true;
      for (FieldDoc constant : constants) {
        if (!first)
          ret.append("|");
        else
          first = false;
        ret.append(constant.name());
      }
      return ret.toString();
    }

    System.err.println("WARNING: unknown XSD type " + typeName);

    return typeName;
  }

  public boolean isIDREF() {
    return isIDREF;
  }

  public boolean isID() {
    return isID;
  }

  public int compareTo(JAXBMember other) {
    return name.compareToIgnoreCase(other.name);
  }

}
