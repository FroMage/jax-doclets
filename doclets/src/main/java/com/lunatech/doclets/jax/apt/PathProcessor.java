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
package com.lunatech.doclets.jax.apt;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;
import javax.tools.Diagnostic.Kind;
import javax.ws.rs.Path;

// we act on @Path
@SupportedAnnotationTypes("javax.ws.rs.Path")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class PathProcessor extends AbstractProcessor {

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    // some utilities
    Elements elementUtils = processingEnv.getElementUtils();
    Messager messager = processingEnv.getMessager();

    // get elements annotated with @Path
    Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Path.class);
    for (Element element : elements) {
      Path annotation = element.getAnnotation(Path.class);

      // make sure we only work on classes or interfaces @Path annotated
      if (annotation != null && (element.getKind() == ElementKind.CLASS || element.getKind() == ElementKind.INTERFACE)) {
        messager.printMessage(Kind.NOTE, "Generating documentation class for : " + element);
        // extract some names
        Name name = element.getSimpleName();
        PackageElement elementPackage = elementUtils.getPackageOf(element);
        // the javadoc
        String doc = elementUtils.getDocComment(element);
        // build a new class name
        String className = elementPackage.getQualifiedName() + "." + name + "_Doc";
        messager.printMessage(Kind.NOTE, "Class: " + className);
        try {
          // now write the new class which will be compiled in the
          // same run
          JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(className, element);
          Writer writer = sourceFile.openWriter();
          writer.append("package " + elementPackage.getQualifiedName() + ";\n\n");
          writer.append("import javax.annotation.Generated;\n\n");
          writer.append("@Generated(\"" + getClass().getCanonicalName() + "\")\n");
          writer.append("public class " + name + "_Doc {\n");
          writer.append(" public String getDoc(){\n");
          writer.append("return \"" + doc.replaceAll("\n", "\\\\n") + "\";\n");
          writer.append("}\n}\n");
          writer.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return false;
  }
}
