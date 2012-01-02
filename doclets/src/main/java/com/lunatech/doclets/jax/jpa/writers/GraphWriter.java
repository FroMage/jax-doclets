/*
    Copyright 2009-2011 Lunatech Research
    Copyright 2009-2011 Stéphane Épardaud
    
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
import com.lunatech.doclets.jax.jpa.model.Registry;
import com.sun.tools.doclets.formats.html.HtmlDocletWriter;

public class GraphWriter extends com.lunatech.doclets.jax.writers.DocletWriter {

  public GraphWriter(JAXConfiguration configuration, Registry registry) {
    super(configuration, getWriter(configuration));
  }

  private static HtmlDocletWriter getWriter(JAXConfiguration configuration) {
    try {
      return new HtmlDocletWriter(configuration.parentConfiguration, "", "graph.html", "");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void printAdditionalHeader() {
    print("<script type='text/javascript' src='jit.js'></script>\n");
    print("<script type='text/javascript' src='graph-data.js'></script>\n");
  }

  public void write() {
    printHeader("Graph");
    printMenu("Graph");
    print("<div id='graph'></div>");
    print("<script type='text/javascript' src='graph.js'></script>");
    tag("hr");
    printMenu("Overview");
    printFooter();
    writer.flush();
    writer.close();
  }

  @Override
  protected void printTopMenu(String selected) {
    open("table", "tbody", "tr");
    printMenuItem("Overview", writer.relativePath + "index.html", selected);
    printOtherMenuItems(selected);
    close("tr", "tbody", "table");
  }

  protected void printOtherMenuItems(String selected) {
    printMenuItem("Graph", writer.relativePath + "graph.html", selected);
  }

}
