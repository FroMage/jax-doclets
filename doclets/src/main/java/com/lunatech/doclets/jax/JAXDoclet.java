package com.lunatech.doclets.jax;

import com.lunatech.doclets.jax.jaxb.JAXBConfiguration;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.formats.html.ConfigurationImpl;
import com.sun.tools.doclets.formats.html.HtmlDoclet;

public abstract class JAXDoclet<T extends JAXConfiguration> {

  private final HtmlDoclet htmlDoclet = new HtmlDoclet();
  public final T conf;

  public JAXDoclet(RootDoc rootDoc) {
    htmlDoclet.configuration.root = rootDoc;
    conf = makeConfiguration(htmlDoclet.configuration);
    conf.setOptions();
  }

  protected abstract T makeConfiguration(ConfigurationImpl configuration);

  public RootDoc getRootDoc() {
    return conf.parentConfiguration.root;
  }

  public ClassDoc forName(String className) {
    return getRootDoc().classNamed(className);
  }
}
