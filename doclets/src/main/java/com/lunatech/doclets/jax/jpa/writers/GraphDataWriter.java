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
package com.lunatech.doclets.jax.jpa.writers;

import java.io.IOException;

import com.lunatech.doclets.jax.JAXConfiguration;
import com.lunatech.doclets.jax.Utils;
import com.lunatech.doclets.jax.jpa.model.JPAClass;
import com.lunatech.doclets.jax.jpa.model.Registry;
import com.lunatech.doclets.jax.jpa.model.Relation;
import com.sun.tools.doclets.formats.html.HtmlDocletWriter;

public class GraphDataWriter extends com.lunatech.doclets.jax.writers.DocletWriter {

  private Registry registry;

  public GraphDataWriter(JAXConfiguration configuration, Registry registry) {
    super(configuration, getWriter(configuration));
    this.registry = registry;
  }

  private static HtmlDocletWriter getWriter(JAXConfiguration configuration) {
    try {
      return new HtmlDocletWriter(configuration.parentConfiguration, "", "graph-data.js", "");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void write() {
    print("var json = [\n");
    boolean doComma = false;
    for (JPAClass klass : registry.getJPAClasses()) {
      if(doComma)
        print(" ,\n");
      doComma = true;
      String url = writer.relativePath + Utils.classToPath(klass) + "/" + klass.getShortClassName() + ".html";
    	print(" {\n");
    	print("  name: '"+klass.getName()+"',\n");
      print("  id: '"+klass.getName()+"',\n");
      print("  data: {\n");
      print("   $url: '"+url+"'\n");
      print("  },\n");
      print("  adjacencies: [\n");
      boolean doComma2 = false;
      for(Relation relation : klass.getRelations()){
        if(doComma2)
          print("   ,\n");
        doComma2 = true;
        String name = relation.getJavaTypeName();
        JPAClass typeClass = registry.getJPAClass(name);
        print("   {\n");
        print("    nodeTo: '"+typeClass.getName()+"',\n");
        print("    data: {\n");
        print("     $labeltext: '"+relation.getRelationFrom()+".."+relation.getRelationTo()+"',\n");
        print("     $type: 'fooType'\n");
        print("    }\n");
        print("   }\n");
      }
      print("  ]\n");
      print(" }\n");
    }
    print("];\n");
    writer.flush();
    writer.close();
  }
}
