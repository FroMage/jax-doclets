package com.lunatech.doclets.jax.jaxrs.writers;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.lunatech.doclets.jax.JAXConfiguration;
import com.lunatech.doclets.jax.Utils;
import com.lunatech.doclets.jax.jaxrs.model.JAXRSApplication;
import com.lunatech.doclets.jax.jaxrs.model.Resource;
import com.lunatech.doclets.jax.jaxrs.model.ResourceMethod;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.SeeTag;
import com.sun.tools.doclets.formats.html.HtmlDocletWriter;

public class JAXRSHtmlDocletWriter extends HtmlDocletWriter {

  private JAXRSApplication application;
  private JAXConfiguration config;

  public JAXRSHtmlDocletWriter(JAXRSApplication application, JAXConfiguration config, String s, String s1, String s2)
      throws IOException {
    super(config.parentConfiguration, s, s1, s2);
    this.application = application;
    this.config = config;
  }

  /**
   * Override links to JAX-RS methods to output method + path text and an
   * appropriate resource page link. POJO DTO pages are generated using the
   * standard page location conventions, so the default works for them.
   */
  @Override
  public String seeTagToString(SeeTag tag) {
    final ClassDoc cDoc = tag.referencedClass();
    MemberDoc member = tag.referencedMember();

    Resource res = null;
    String linkText = null;
    String hash = null;
    if (cDoc == null) {
      return invalidLink(tag, "Unable to locate referenced class " + cDoc.qualifiedName());
    } else if (member == null) {
      // Check for a resource class
      res = application.findResourceClass(cDoc);
      if (res != null) {
        linkText = Utils.getAbsolutePath(this.config, res);
      }
    } else if (member instanceof MethodDoc) {
      // Check for a resource method
      res = application.findResourceForMethod(cDoc, (MethodDoc) member);

      if (res != null) {
        ResourceMethod rMethod = res.findMethod((MethodDoc) member);
        if (rMethod != null) {
          linkText = getDisplayText(res, rMethod);
          hash = rMethod.getMethods().get(0);
        }
      }
    }
    if (res != null) {
      final String linkTitle;
      if ((tag.label() != null) && !tag.label().trim().isEmpty()) {
        linkTitle = linkText;
        linkText = tag.label();
      } else {
        linkTitle = "";
      }
      String path = Utils.urlToPath(res);
      if (path.length() == 0) {
        path = ".";
      }

      String link = relativePath + path + "/index.html";
      if (hash != null) {
        link += "#" + hash;
      }
      return String.format("<tt><a href='%s' title='%s'>%s</a></tt>", link, linkTitle, linkText);
    }

    return super.seeTagToString(tag);
  }

  private String getDisplayText(Resource resource, ResourceMethod rMethod) {
    final StringBuilder sb = new StringBuilder();
    if (!rMethod.isResourceLocator()) {
      List<String> methods = rMethod.getMethods();
      for(int i = 0; i < methods.size(); i++) {
        sb.append(methods.get(i));
        if (i < (methods.size() - 1)) {
          sb.append("/");
        }
      }
      sb.append(" ");
    }
    sb.append(Utils.getAbsolutePath(this.config, resource));
    return sb.toString();
  }

  private String invalidLink(SeeTag tag, String msg) {
    config.parentConfiguration.root.printError(tag.position(), msg);
    return "<span class='invalid-link'>" + tag.text() + "</span>";
  }

  public JAXRSApplication getApplication() {
    return application;
  }

}
