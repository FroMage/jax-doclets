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
package com.lunatech.doclets.jax;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import com.lunatech.doclets.jax.jaxb.model.JAXBClass;
import com.lunatech.doclets.jax.jaxrs.model.Resource;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;
import com.sun.javadoc.AnnotationDesc.ElementValuePair;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.taglets.DeprecatedTaglet;
import com.sun.tools.doclets.internal.toolkit.taglets.ParamTaglet;
import com.sun.tools.doclets.internal.toolkit.taglets.Taglet;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletManager;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletOutput;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletWriter;
import com.sun.tools.doclets.internal.toolkit.util.DirectoryManager;

public class Utils {

  public static boolean isEmptyOrNull(String str) {
    return str == null || str.length() == 0;
  }

  public static String getParamDoc(final MethodDoc declaringMethod, final String name) {
    for (final ParamTag paramTag : declaringMethod.paramTags()) {
      if (paramTag.parameterName().equals(name)) {
        return paramTag.parameterComment();
      }
    }
    return "";
  }

  public static Object getAnnotationValue(final AnnotationDesc annotation) {
    return getAnnotationValue(annotation, "value");
  }

  public static Object getAnnotationValue(AnnotationDesc annotation, String name) {
    for (final ElementValuePair elementValuePair : annotation.elementValues()) {
      if (elementValuePair.element().name().equals(name)) {
        return elementValuePair.value().value();
      }
    }
    return null;
  }

  public static String[] getAnnotationValues(final AnnotationDesc annotation) {
    final Object value = getAnnotationValue(annotation);
    if (value instanceof String) {
      return new String[] { (String) value };
    }
    if (value instanceof AnnotationValue[]) {
      final AnnotationValue[] values = (AnnotationValue[]) value;
      final String[] ret = new String[values.length];
      for (int i = 0; i < ret.length; i++) {
        ret[i] = (String) values[i].value();
      }
      return ret;
    }
    throw new IllegalArgumentException("value is not string or string[]: " + value);
  }

  public static boolean hasAnnotation(final ProgramElementDoc programElementDoc, final Class<?>... soughtAnnotations) {
    return findAnnotation(programElementDoc, soughtAnnotations) != null;
  }

  public static MethodDoc findAnnotatedMethod(final ClassDoc declaringClass, final MethodDoc method, final Class<?>... soughtAnnotations) {
    final AnnotationDesc onMethod = findAnnotation(method, soughtAnnotations);
    if (onMethod != null) {
      return method;
    }
    // try on the declaring class
    for (final MethodDoc declaringMethod : declaringClass.methods(false)) {
      if (method.overrides(declaringMethod)) {
        if (hasAnnotation(declaringMethod, soughtAnnotations)) {
          return declaringMethod;
        }
        return null;
      }
    }
    return null;
  }

  public static AnnotationDesc findMethodAnnotation(final ClassDoc declaringClass, final MethodDoc method,
                                                    final Class<?>... soughtAnnotations) {
    final AnnotationDesc onMethod = findAnnotation(method, soughtAnnotations);
    if (onMethod != null) {
      return onMethod;
    }
    // try on the declaring class
    for (final MethodDoc declaringMethod : declaringClass.methods(false)) {
      if (method.overrides(declaringMethod)) {
        return findAnnotation(declaringMethod, soughtAnnotations);
      }
    }
    return null;
  }

  public static AnnotationDesc findParameterAnnotation(final MethodDoc declaringMethod, final Parameter parameter, int parameterIndex,
                                                       final Class<?>... soughtAnnotations) {
    final AnnotationDesc onParameter = findAnnotation(parameter, soughtAnnotations);
    if (onParameter != null) {
      return onParameter;
    }
    // try on the declaring method
    Parameter overriddenParameter = declaringMethod.parameters()[parameterIndex];
    return findAnnotation(overriddenParameter, soughtAnnotations);
  }

  public static AnnotationDesc findAnnotation(final ProgramElementDoc programElementDoc, final Class<?>... soughtAnnotations) {
    return findAnnotation(programElementDoc.annotations(), soughtAnnotations);
  }

  public static AnnotationDesc findAnnotation(final Parameter parameter, final Class<?>... soughtAnnotations) {
    return findAnnotation(parameter.annotations(), soughtAnnotations);
  }

  public static AnnotationDesc findAnnotation(final AnnotationDesc[] annotations, final Class<?>... soughtAnnotations) {
    for (final AnnotationDesc annotation : annotations) {
      final AnnotationTypeDoc annotationType = annotation.annotationType();
      for (final Class<?> soughtAnnotation : soughtAnnotations) {
        if (annotationType.qualifiedTypeName().equals(soughtAnnotation.getName())) {
          return annotation;
        }
      }
    }
    return null;
  }

  public static ClassDoc findAnnotatedInterface(final ClassDoc klass, final Class<?>... soughtAnnotations) {
    // find it in the interfaces
    final Type[] interfaceTypes = klass.interfaceTypes();
    for (final Type interfaceType : interfaceTypes) {
      final ClassDoc interfaceClassDoc = interfaceType.asClassDoc();
      if (interfaceClassDoc != null) {
        if (hasAnnotation(interfaceClassDoc, soughtAnnotations)) {
          return interfaceClassDoc;
        }
        final ClassDoc foundClassDoc = findAnnotatedInterface(interfaceClassDoc, soughtAnnotations);
        if (foundClassDoc != null) {
          return foundClassDoc;
        }
      }
    }
    return null;
  }

  public static ClassDoc findAnnotatedClass(final ClassDoc klass, final Class<?>... soughtAnnotations) {
    if (!klass.isClass())
      return null;
    if (hasAnnotation(klass, soughtAnnotations)) {
      return klass;
    }
    // find it in the interfaces
    final ClassDoc foundClassDoc = findAnnotatedInterface(klass, soughtAnnotations);
    if (foundClassDoc != null) {
      return foundClassDoc;
    }

    final Type superclass = klass.superclassType();
    if (superclass != null && superclass.asClassDoc() != null) {
      return findAnnotatedClass(superclass.asClassDoc(), soughtAnnotations);
    }
    return null;
  }

  public static Type findSuperType(final Type type, String typeName) {
    ClassDoc doc = type.asClassDoc();
    if (doc == null)
      return null;
    if (doc.isInterface())
      return findSuperTypeFromInterface(doc, typeName);
    if (doc.isClass() && !doc.isEnum() && !doc.isError() && !doc.isException())
      return findSuperTypeFromClass(doc, typeName);
    return null;
  }

  public static Type findSuperTypeFromInterface(final ClassDoc klass, String typeName) {
    // find it in the interfaces
    final Type[] interfaceTypes = klass.interfaceTypes();
    for (final Type interfaceType : interfaceTypes) {
      final ClassDoc interfaceClassDoc = interfaceType.asClassDoc();
      if (interfaceClassDoc != null) {
        if (interfaceClassDoc.qualifiedTypeName().equals(typeName))
          return interfaceClassDoc;
        final Type foundType = findSuperTypeFromInterface(interfaceClassDoc, typeName);
        if (foundType != null) {
          return foundType;
        }
      }
    }
    return null;
  }

  public static Type findSuperTypeFromClass(final ClassDoc klass, String typeName) {
    if (klass.qualifiedTypeName().equals(typeName))
      return klass;

    // find it in the interfaces
    final Type foundType = findSuperTypeFromInterface(klass, typeName);
    if (foundType != null) {
      return foundType;
    }

    final Type superclass = klass.superclassType();
    if (superclass != null && superclass.asClassDoc() != null) {
      return findSuperTypeFromClass(superclass.asClassDoc(), typeName);
    }
    return null;
  }

  public static String appendURLFragments(String... fragments) {
    StringBuffer strbuf = new StringBuffer();
    for (String fragment : fragments) {
      if (!strbuf.toString().endsWith("/") && !fragment.startsWith("/")) {
        strbuf.append("/");
      }
      if (strbuf.toString().endsWith("/") && fragment.startsWith("/")) {
        fragment = fragment.substring(1);
      }
      strbuf.append(fragment);
    }
    return strbuf.toString();
  }

  public static String getFirstURLFragment(String path) {
    if (path.startsWith("/"))
      path = path.substring(1);
    if (path.length() == 0)
      return null;
    String[] fragments = path.split("/+");
    if (fragments.length == 0)
      return null;
    return fragments[0];
  }

  public static String classToPath(JAXBClass jaxbClass) {
    return DirectoryManager.getPath(jaxbClass.getPackageName());
  }

  public static String urlToPath(Resource resource) {
    String name = resource.getAbsolutePath();
    if (name.startsWith("/"))
      name = name.substring(1);
    return urlToPath(name);
  }

  public static String urlToPath(String name) {
    if (name == null || name.length() == 0) {
      return "";
    }
    StringBuffer pathstr = new StringBuffer();
    for (int i = 0; i < name.length(); i++) {
      char ch = name.charAt(i);
      if (ch == '/') {
        pathstr.append(File.separator);
      } else {
        pathstr.append(ch);
      }
    }
    return pathstr.toString();
  }

  public static String urlToClass(JAXBClass from, JAXBClass to) {
    return classToRoot(from) + classToPath(to) + "/" + to.getShortClassName() + ".html";
  }

  public static String urlToType(ClassDoc klass) {
    return DirectoryManager.getPathToClass(klass);
  }

  public static String classToRoot(JAXBClass klass) {
    return DirectoryManager.getRelativePath(klass.getPackageName());
  }

  public static String urlToRoot(Resource resource) {
    String from = resource.getAbsolutePath();
    if (from.startsWith("/"))
      from = from.substring(1);
    return urlToRoot(from);
  }

  public static String urlToRoot(String from) {
    if (from == null || from.length() == 0) {
      return "";
    }
    StringBuffer pathstr = new StringBuffer();
    for (int i = 0; i < from.length(); i++) {
      char ch = from.charAt(i);
      if (ch == '/') {
        pathstr.append("../");
      }
    }
    pathstr.append("../");
    return pathstr.toString();
  }

  public static void createDirectory(String path) {
    if (path == null || path.length() == 0) {
      return;
    }
    File dir = new File(path);
    if (dir.exists()) {
      return;
    } else {
      if (dir.mkdirs()) {
        return;
      } else {
        throw new RuntimeException("Could not create path: " + path);
      }
    }
  }

  public static void genTagOuput(TagletManager tagletManager, Doc doc, Taglet[] taglets, TagletWriter writer, TagletOutput output,
                                 Set<String> tagletsToPrint) {
    tagletManager.checkTags(doc, doc.tags(), false);
    tagletManager.checkTags(doc, doc.inlineTags(), true);
    TagletOutput currentOutput = null;
    for (int i = 0; i < taglets.length; i++) {
      if (!tagletsToPrint.contains(taglets[i].getName()))
        continue;
      if (doc instanceof ClassDoc && taglets[i] instanceof ParamTaglet) {
        // The type parameters are documented in a special section away
        // from the tag info, so skip here.
        continue;
      }
      if (taglets[i] instanceof DeprecatedTaglet) {
        // Deprecated information is documented "inline", not in tag
        // info
        // section.
        continue;
      }
      try {
        currentOutput = taglets[i].getTagletOutput(doc, writer);
      } catch (IllegalArgumentException e) {
        // The taglet does not take a member as an argument. Let's try
        // a single tag.
        Tag[] tags = doc.tags(taglets[i].getName());
        if (tags.length > 0) {
          currentOutput = taglets[i].getTagletOutput(tags[0], writer);
        }
      }
      if (currentOutput != null) {
        tagletManager.seenCustomTag(taglets[i].getName());
        output.appendOutput(currentOutput);
      }
    }
  }

  public static void copyResources(Configuration configuration) {
    File cssFile = new File(configuration.destDirName, "doclet.css");
    InputStream stream = Utils.class.getResourceAsStream("resources/doclet.css");
    OutputStream os;
    try {
      os = new FileOutputStream(cssFile);
      byte[] buffer = new byte[1024];
      int read;
      while ((read = stream.read(buffer)) >= 0) {
        os.write(buffer, 0, read);
      }
      os.flush();
      os.close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
