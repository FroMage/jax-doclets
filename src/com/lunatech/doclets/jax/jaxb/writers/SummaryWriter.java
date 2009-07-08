package com.lunatech.doclets.jax.jaxb.writers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.lunatech.doclets.jax.Utils;
import com.lunatech.doclets.jax.jaxb.model.JAXBClass;
import com.lunatech.doclets.jax.jaxb.model.Registry;
import com.sun.javadoc.Doc;
import com.sun.tools.doclets.formats.html.ConfigurationImpl;
import com.sun.tools.doclets.formats.html.HtmlDocletWriter;
import com.sun.tools.doclets.internal.toolkit.Configuration;

public class SummaryWriter extends com.lunatech.doclets.jax.writers.DocletWriter {

  private Registry registry;

  public SummaryWriter(Configuration configuration, Registry registry) {
    super(configuration, getWriter(configuration));
    this.registry = registry;
  }

  private static HtmlDocletWriter getWriter(Configuration configuration) {
    try {
      return new HtmlDocletWriter((ConfigurationImpl) configuration, "", "overview-summary.html", "");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void write() {
    printHeader();
    printMenu("Overview");
    List<JAXBClass> classes = new ArrayList<JAXBClass>(registry.getJAXBClasses());
    Collections.sort(classes);
    printClasses(classes);
    tag("hr");
    printMenu("Overview");
    printFooter();
    writer.flush();
    writer.close();
  }

  private void printClasses(Collection<JAXBClass> classes) {
    tag("hr");
    open("table");
    open("tr");
    around("th colspan='2'", "Elements");
    close("tr");
    open("tr class='subheader'");
    around("td", "Name");
    around("td", "Description");
    close("tr");
    for (JAXBClass klass : classes) {
      open("tr");
      open("td");
      around("a href='" + writer.relativePath + Utils.classToPath(klass) + "/" + klass.getShortClassName() + ".html'", klass.getName());
      close("td");
      open("td");
      Doc javaDoc = klass.getJavaDoc();
      if (javaDoc != null && javaDoc.firstSentenceTags() != null)
        writer.printSummaryComment(javaDoc);
      close("td");
      close("tr");

    }
    close("table");
  }

  protected void printHeader() {
    printHeader("Overview of XML elements");
  }
}
