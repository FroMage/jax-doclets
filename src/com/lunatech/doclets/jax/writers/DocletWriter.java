package com.lunatech.doclets.jax.writers;

import com.sun.tools.doclets.formats.html.HtmlDocletWriter;
import com.sun.tools.doclets.internal.toolkit.Configuration;

public class DocletWriter {

  protected HtmlDocletWriter writer;

  protected Configuration configuration;

  public DocletWriter(Configuration configuration, HtmlDocletWriter writer) {
    this.writer = writer;
    this.configuration = configuration;
  }

  public HtmlDocletWriter getWriter() {
    return writer;
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  protected void open(String tag) {
    print("<" + tag + ">");
  }

  protected void close(String tag) {
    print("</" + tag + ">");
  }

  protected void tag(String tag) {
    print("<" + tag + "/>");
  }

  protected void around(String tag, String value) {
    open(tag);
    print(value);
    int space = tag.indexOf(' ');
    if (space > -1) {
      tag = tag.substring(0, space);
    }
    close(tag);
  }

  protected void printHeader(String title) {
    open("HTML");
    open("HEAD");
    around("TITLE", title);
    close("TITLE");
    tag("LINK REL='stylesheet' TYPE='text/css' HREF='" + writer.relativePath + "doclet.css' TITLE='Style'");
    close("HEAD");
    open("BODY");
  }

  protected void printFooter() {
    tag("hr");
    open("div class='footer'");
    print("Generated by JaxDoc v0.1 (© Stéphane Épardaud)");
    close("div");
    close("BODY");
    close("HTML");
  }

  protected void printMenu(String selected) {
    open("table class='menu'");
    open("tr");
    printMenuItem("Overview", writer.relativePath + "overview-summary.html", selected);
    printOtherMenuItems(selected);
    close("tr");
    close("table");
  }

  protected void printOtherMenuItems(String selected) {}

  protected void printMenuItem(String title, String href, String selected) {
    boolean isSelected = title.equals(selected);
    if (isSelected)
      open("th class='selected'");
    else
      open("th");
    if (href != null && !isSelected) {
      around("a href='" + href + "'", title);
    } else
      print(title);
    close("th");

  }

  protected void print(String str) {
    writer.write(str);
  }

}
