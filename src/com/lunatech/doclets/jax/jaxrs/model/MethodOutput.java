package com.lunatech.doclets.jax.jaxrs.model;

import com.lunatech.doclets.jax.Utils;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;

public class MethodOutput {

  private MethodDoc declaringMethod;

  private Tag[] returnWrappedTags;

  public MethodOutput(MethodDoc declaringMethod) {
    this.declaringMethod = declaringMethod;
    Type returnType = declaringMethod.returnType();
    if (!returnType.isPrimitive()
        && (returnType.qualifiedTypeName().equals("java.lang.String") || returnType.qualifiedTypeName().equals("javax.ws.rs.core.Response")))
      returnWrappedTags = Utils.getTags(declaringMethod, "returnWrapped");

  }

  public Type getOutputType() {
    return declaringMethod.returnType();
  }

  public boolean isOutputWrapped() {
    return returnWrappedTags != null;
  }

  public int getOutputWrappedCount() {
    return returnWrappedTags != null ? returnWrappedTags.length : 0;
  }

  public String getWrappedOutputType(int index) {
    if (!isOutputWrapped())
      return null;
    String text = returnWrappedTags[index].text();
    int ws = text.indexOf(' ');
    if (ws > -1)
      return text.substring(0, ws);
    return text;
  }

  public String getOutputTypeString() {
    Type returnType = getOutputType();
    return returnType.qualifiedTypeName() + returnType.dimension();
  }

  public String getOutputDoc(int index) {
    if (isOutputWrapped()) {
      String text = returnWrappedTags[index].text();
      int ws = text.indexOf(' ');
      if (ws > -1) {
        String doc = text.substring(ws);
        if (!Utils.isEmptyOrNull(doc.trim()))
          return doc;
      }
    }
    // default to @return tag
    return getOutputDoc();
  }

  public String getOutputDoc() {
    Tag returnDoc = Utils.getTag(declaringMethod, "return");
    if (returnDoc != null)
      return returnDoc.text();
    return null;
  }
}
