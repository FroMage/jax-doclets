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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.lunatech.doclets.jax.jaxb.model.JAXBClass;
import com.lunatech.doclets.jax.jaxrs.model.Resource;
import com.lunatech.doclets.jax.jaxrs.model.ResourceMethod;
import com.lunatech.doclets.jax.writers.DocletWriter;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationDesc.ElementValuePair;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.formats.html.ConfigurationImpl;
import com.sun.tools.doclets.formats.html.HtmlDocletWriter;
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
      // skip empty fragments
      if (fragment == null || fragment.length() == 0)
        continue;
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

  public static String slashify(String url) {
    if (url == null)
      return "";
    if (!url.endsWith("/"))
      return url + "/";
    return url;
  }

  public static String unEndSlashify(String url) {
    if (url == null)
      return "";
    if (url.endsWith("/"))
      return url.substring(0, url.length() - 1);
    return url;
  }

  public static String unStartSlashify(String url) {
    if (url == null)
      return "";
    if (!url.startsWith("/"))
      return url;
    if (url.length() == 1) {
      return "";
    }
    return url.substring(1);
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

  public static void copyResources(ConfigurationImpl configuration) {
    InputStream defaultCSS = Utils.class.getResourceAsStream("/doclet.css");
    if (defaultCSS == null)
      throw new RuntimeException("Failed to find doclet CSS (incorrect jax-doclets packaging?)");
    if (!isEmptyOrNull(configuration.stylesheetfile)) {
      try {
        InputStream stream = new FileInputStream(configuration.stylesheetfile);
        copyResource(stream, new File(configuration.destDirName, "doclet.css"));
        // also put the original stylesheet in case it's needed
        copyResource(defaultCSS, new File(configuration.destDirName, "default-doclet.css"));
      } catch (Exception x) {
        throw new RuntimeException("Failed to read user stylesheet " + configuration.stylesheetfile, x);
      }
    } else
      copyResource(defaultCSS, new File(configuration.destDirName, "doclet.css"));
  }

  private static void copyResource(InputStream stream, File output) {
    try {
      OutputStream os = new FileOutputStream(output);
      byte[] buffer = new byte[1024];
      int read;
      while ((read = stream.read(buffer)) >= 0) {
        os.write(buffer, 0, read);
      }
      os.flush();
      os.close();
      stream.close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Returns either Produces.class or ProduceMime.class (old version)
   * 
   * @return
   */
  public static Class<?> getProducesClass() {
    try {
      return Class.forName("javax.ws.rs.Produces");
    } catch (ClassNotFoundException e) {
      try {
        return Class.forName("javax.ws.rs.ProduceMime");
      } catch (ClassNotFoundException e1) {
        throw new RuntimeException(e1);
      }
    }
  }

  /**
   * Returns either Consumes.class or ConsumeMime.class (old version)
   * 
   * @return
   */
  public static Class<?> getConsumesClass() {
    try {
      return Class.forName("javax.ws.rs.Consumes");
    } catch (ClassNotFoundException e) {
      try {
        return Class.forName("javax.ws.rs.ConsumeMime");
      } catch (ClassNotFoundException e1) {
        throw new RuntimeException(e1);
      }
    }
  }

  public static String getExternalLink(Configuration configuration, Type type, HtmlDocletWriter writer) {
    return getExternalLink(configuration, type.asClassDoc().containingPackage().name(), type.typeName(), writer);
  }

  public static String getExternalLink(Configuration configuration, String className, HtmlDocletWriter writer) {
    int lastSep = className.lastIndexOf('.');
    if (lastSep == -1)
      return getExternalLink(configuration, "", className, writer);
    String link;
    // since classes can be internal, look up backwards with an ever shrinking
    // package name
    do {
      link = getExternalLink(configuration, className.substring(0, lastSep), className.substring(lastSep + 1), writer);
      if (link != null)
        return link;
      lastSep = className.lastIndexOf('.', lastSep - 1);
    } while (lastSep > -1);
    return null;
  }

  private static String getExternalLink(Configuration configuration, String packageName, String className, HtmlDocletWriter writer) {
    return configuration.extern.getExternalLink(packageName, writer.relativePath, className + ".html");
  }

  public static String getLinkTypeName(String url) {
    int lastSep = url.lastIndexOf('/');
    if (lastSep != -1 && url.endsWith(".html"))
      return url.substring(lastSep + 1, url.length() - 5);
    throw new IllegalArgumentException("Invalid type link: " + url);
  }

  public static Tag getTag(Doc doc, String tagName) {
    Tag[] tags = doc.tags("@" + tagName);
    if (tags != null && tags.length > 0) {
      return tags[0];
    }
    return null;
  }

  public static Tag[] getTags(Doc doc, String tagName) {
    Tag[] tags = doc.tags("@" + tagName);
    if (tags != null && tags.length > 0) {
      return tags;
    }
    return null;
  }

  public static String getOption(String options[][], String optionName) {
    for (String option[] : options) {
      String name = option[0];
      if (!optionName.equals(name)) {
        continue;
      }
      String value = option.length > 1 ? option[1] : null;
      return value;
    }
    return null;
  }

  /**
   * @return true if optionName exists in one of the options.
   */
  public static boolean hasOption(String options[][], String optionName) {
    for (String option[] : options) {
      String name = option[0];
      if (!optionName.equals(name)) {
        continue;
      }
      return true;
    }
    return false;
  }

  public static boolean isCollection(Type type) {
    String dimension = type.dimension();
    if (dimension != null && dimension.length() > 0) {
      return true;
    }
    ParameterizedType parameterizedType = type.asParameterizedType();
    Type collectionType = Utils.findSuperType(type, "java.util.Collection");
    // FIXME: this is dodgy at best
    return collectionType != null;
  }

  public static Type getCollectionType(Type type, JAXDoclet doclet) {
    Type collectionType = Utils.findSuperType(type, "java.util.Collection");
    // FIXME: this is dodgy at best
    if (collectionType != null) {
      ParameterizedType parameterizedType = type.asParameterizedType();
      Type[] types = parameterizedType == null ? null : parameterizedType.typeArguments();
      if (types != null && types.length == 1)
        return types[0];
      return doclet.forName("java.lang.Object");
    }
    return type;
  }

  public static Type resolveType(String typeName, ClassDoc klass, JAXDoclet doclet) {
    log("resolving " + typeName + " in " + klass.qualifiedTypeName());
    // first look in inner classes
    for (ClassDoc innerClass : klass.innerClasses(false)) {
      if (innerClass.simpleTypeName().equals(typeName))
        return innerClass;
    }
    // then the class itself
    if (klass.typeName().equals(typeName))
      return klass;
    // then go through the named imports
    for (ClassDoc importedClass : klass.importedClasses()) {
      if (importedClass.typeName().equals(typeName))
        return importedClass;
    }
    // then the package imports
    for (PackageDoc importedPackage : klass.importedPackages()) {
      for (ClassDoc importedClass : importedPackage.allClasses(false)) {
        if (importedClass.typeName().equals(typeName))
          return importedClass;
      }
    }
    // now try FQDN
    Type type = doclet.forName(typeName);
    if (type != null)
      return type;
    log("resolving failed for " + typeName + " in " + klass.qualifiedTypeName());
    return null;
  }

  public static JaxType parseType(String typeName, ClassDoc containingClass, JAXDoclet doclet) throws InvalidJaxTypeException {
    typeName = typeName.trim();
    char[] chars = typeName.toCharArray();
    Stack<JaxType> types = new Stack<JaxType>();
    JaxType currentType = new JaxType();
    types.push(currentType);
    StringBuffer currentTypeName = new StringBuffer();
    for (int i = 0; i < chars.length; i++) {
      char c = chars[i];
      log("Looking at char " + c);
      if (c == '<') {
        log("Start params for " + currentTypeName);
        // we're done for the type name
        setupType(currentType, currentTypeName, containingClass, doclet);
        // add a parameter to the current type
        JaxType parameterType = new JaxType();
        currentType.parameters.add(parameterType);
        currentType = parameterType;
        // prepare for the parameter type
        types.push(currentType);
      } else if (c == '>') {
        // we're done for the parameter type
        if (currentTypeName.length() > 0)
          setupType(currentType, currentTypeName, containingClass, doclet);
        // reset and pop
        types.pop();
        currentType = types.peek();
        log("End params for " + currentType.typeName);
        // we should have at least the top type
        if (types.size() < 1)
          throw new InvalidJaxTypeException();
      } else if (c == ',') {
        // we're done for the parameter type, unless it was already done by
        // closing its parameter list
        if (currentTypeName.length() > 0) {
          setupType(currentType, currentTypeName, containingClass, doclet);
          // reset, pop
          types.pop();
          currentType = types.peek();
        }
        log("Next params for " + currentType.typeName);
        // we should have at least the top type
        if (types.size() < 1)
          throw new InvalidJaxTypeException();
        // add a parameter to the current type
        JaxType parameterType = new JaxType();
        currentType.parameters.add(parameterType);
        currentType = parameterType;
        // prepare for the parameter type
        types.push(currentType);
      } else if (c == '[' || c == ']') {
        log("Dimension for " + currentType.typeName);
        // done for the class name unless it was already done by
        // closing its parameter list
        if (currentTypeName.length() > 0) {
          setupType(currentType, currentTypeName, containingClass, doclet);
        }
        // FIXME: check dimension correctness
        currentType.dimension += c;
      } else {
        log("Name char: " + currentTypeName);
        // if the currentType already has a name, barf
        if (currentType.typeName != null)
          throw new InvalidJaxTypeException();
        currentTypeName.append(c);
      }
    }
    // perhaps we didn't have any parameters or dimension
    if (currentTypeName.length() > 0) {
      log("End of type without param or dimension for " + currentTypeName);
      setupType(currentType, currentTypeName, containingClass, doclet);
    }
    // we should have the original type to return
    if (types.size() != 1)
      throw new InvalidJaxTypeException();
    return currentType;
  }

  public static class InvalidJaxTypeException extends Exception {}

  private static void setupType(JaxType currentType, StringBuffer currentTypeName, ClassDoc containingClass, JAXDoclet doclet)
      throws InvalidJaxTypeException {
    if (currentTypeName.length() == 0) {
      throw new InvalidJaxTypeException();
    }
    currentType.typeName = currentTypeName.toString();
    currentType.type = resolveType(currentType.typeName, containingClass, doclet);
    currentTypeName.setLength(0);
  }

  public static class JaxType {

    String typeName;

    Type type;

    List<JaxType> parameters = new LinkedList<JaxType>();

    String dimension = "";

    public String getDimension() {
      return dimension;
    }

    public String getTypeName() {
      return typeName;
    }

    public Type getType() {
      return type;
    }

    public List<JaxType> getParameters() {
      return parameters;
    }

    public boolean hasParameters() {
      return !parameters.isEmpty();
    }
  }

  private static String addContextPath(DocletWriter writer, String url) {
    String jaxrscontext = getOption(writer.getConfiguration().root.options(), "-jaxrscontext");
    if (jaxrscontext == null)
      return url;
    else
      return appendURLFragments(jaxrscontext, url);
  }

  public static String getDisplayURL(DocletWriter writer, Resource resource, ResourceMethod method) {
    return addContextPath(writer, method.getURL(resource));
  }

  public static String getAbsolutePath(DocletWriter writer, Resource resource) {
    return addContextPath(writer, resource.getAbsolutePath());
  }

  public static void log(String mesg) {
    // System.err.println(mesg);
  }
}
