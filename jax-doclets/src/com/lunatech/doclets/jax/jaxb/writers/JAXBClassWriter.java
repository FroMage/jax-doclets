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
package com.lunatech.doclets.jax.jaxb.writers;

import java.io.IOException;
import java.util.Collection;

import com.lunatech.doclets.jax.Utils;
import com.lunatech.doclets.jax.jaxb.model.Attribute;
import com.lunatech.doclets.jax.jaxb.model.Element;
import com.lunatech.doclets.jax.jaxb.model.JAXBClass;
import com.lunatech.doclets.jax.jaxb.model.JAXBMember;
import com.lunatech.doclets.jax.jaxb.model.MemberType;
import com.lunatech.doclets.jax.jaxb.model.Value;
import com.sun.javadoc.Doc;
import com.sun.tools.doclets.formats.html.ConfigurationImpl;
import com.sun.tools.doclets.formats.html.HtmlDocletWriter;
import com.sun.tools.doclets.internal.toolkit.Configuration;

public class JAXBClassWriter extends DocletWriter {

  public JAXBClassWriter(ConfigurationImpl configuration, JAXBClass jaxbClass) {
    super(configuration, getWriter(configuration, jaxbClass), jaxbClass);
  }

  private static HtmlDocletWriter getWriter(Configuration configuration, JAXBClass jaxbClass) {
    try {
      return new HtmlDocletWriter((ConfigurationImpl) configuration, Utils.classToPath(jaxbClass), jaxbClass.getShortClassName() + ".html",
          Utils.classToRoot(jaxbClass));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void write() {
    printHeader();
    printMenu("");
    printSummary();
    printElements();
    printAttributes();
    printValues();
    tag("hr");
    printMenu("");
    printFooter();
    writer.flush();
    writer.close();
  }

  private void printElements() {
    printMembers(jaxbClass.getElements(), "Elements", MemberType.Element);
  }

  private void printAttributes() {
    printMembers(jaxbClass.getAttributes(), "Attributes", MemberType.Attribute);
  }

  private void printValues() {
    printMembers(jaxbClass.getValues(), "Value", MemberType.Value);
  }

  private void printMembers(Collection<? extends JAXBMember> members, String title, MemberType type) {
    if (members.isEmpty())
      return;
    tag("hr");
    open("table class='info' id='" + title + "'");
    boolean isValue = type == MemberType.Value;
    around("caption class='TableCaption'", title);
    open("tbody");
    open("tr");
    if (!isValue) {
      around("th class='TableHeader'", "Name");
    }
    around("th class='TableHeader'", "Type");
    around("th class='TableHeader'", "Description");
    close("tr");
    for (JAXBMember member : members) {
      open("tr");
      if (!isValue) {
        around("td", member.getName());
      }
      open("td");
      printMemberType(member, true);
      close("td");
      open("td");
      Doc javaDoc = member.getJavaDoc();
      if (javaDoc != null && javaDoc.firstSentenceTags() != null)
        writer.printSummaryComment(javaDoc);
      close("td");
      close("tr");

    }
    close("tbody");
    close("table");
  }

  private void printMemberType(JAXBMember member, boolean markCollections) {
    if (markCollections && member.isCollection())
      print("xsd:list[");
    if (member.isIDREF())
      print("xsd:IDREF[");
    if (member.isID())
      print("xsd:ID[");
    if (member.isJAXBType()) {
      String name = member.getJavaTypeName();
      JAXBClass typeClass = jaxbClass.getRegistry().getJAXBClass(name);
      around("a href='" + Utils.urlToClass(jaxbClass, typeClass) + "'", typeClass.getName());
    } else
      print(member.getXSDType());
    if (member.isID())
      print("]");
    if (member.isIDREF())
      print("]");
    if (markCollections && member.isCollection())
      print("]");
  }

  private void printSummary() {
    open("h2");
    around("h2", "Name: " + jaxbClass.getName());
    Doc javaDoc = jaxbClass.getJavaDoc();
    if (javaDoc != null && javaDoc.tags() != null) {
      writer.printInlineComment(javaDoc);
    }
    around("b", "Example:");
    open("pre");
    print("&lt;" + jaxbClass.getName());
    Collection<Attribute> attributes = jaxbClass.getAttributes();
    for (Attribute attribute : attributes) {
      print("\n " + attribute.getName() + "=\"");
      printMemberType(attribute, false);
      print("\"");
    }
    print(">\n");
    Collection<Element> elements = jaxbClass.getElements();
    for (Element element : elements) {
      print("  &lt;" + element.getName() + ">");
      if (element.isWrapped()) {
        print("\n   &lt;" + element.getWrappedName() + ">");
        printMemberType(element, true);
        print("&lt;/" + element.getWrappedName() + ">");
        // if (element.isCollection())
        // print("…");
        print("\n  ");
      } else
        printMemberType(element, true);
      print("&lt;/" + element.getName() + ">");
      // if (!element.isWrapped() && element.isCollection())
      // print("…");
      print("\n");
    }
    for (Value value : jaxbClass.getValues()) {
      print(" ");
      printMemberType(value, true);
      print("\n");
    }
    print("&lt;/" + jaxbClass.getName() + ">\n");
    close("pre");
    open("dl");
    JAXBMember idMember = jaxbClass.getID();
    if (idMember != null) {
      open("dt");
      around("b", "ID");
      close("dt");
      around("dd", idMember.getName());
    }
    close("dl");
  }

  protected void printHeader() {
    printHeader("XML element " + jaxbClass.getName());
  }

  protected void printThirdMenu() {
    open("tr");
    open("td class='NavBarCell3' colspan='2'");
    print("detail: ");
    printLink(!jaxbClass.getElements().isEmpty(), "#Elements", "element");
    print(" | ");
    printLink(!jaxbClass.getAttributes().isEmpty(), "#Attributes", "attribute");
    print(" | ");
    printLink(!jaxbClass.getValues().isEmpty(), "#Value", "value");
    close("td", "tr");
  }
}
