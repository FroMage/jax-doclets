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
import com.sun.javadoc.Doc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.SeeTag;
import com.sun.tools.doclets.formats.html.HtmlDocletWriter;

public class JAXRSHtmlDocletWriter extends HtmlDocletWriter {

  private JAXRSApplication application;
  private JAXConfiguration config;

  public JAXRSHtmlDocletWriter(JAXRSApplication application, JAXConfiguration config, String s, String s1, String s2) throws IOException {
    super(config.parentConfiguration, s, s1, s2);
    this.application = application;
    this.config = config;
  }

  /**
   * Override links to JAX-RS methods to output method + path text and an appropriate resource page link. POJO DTO pages are generated using
   * the standard page location conventions, so the default works for them.
   */
  @Override
  public String seeTagToString(SeeTag tag) {
    Resource res = null;
    String linkText = null;
    String hash = null;

    if (tag.referencedClassName() != null) {
      final ClassDoc cDoc = tag.referencedClass();

      if (cDoc == null) {
        // JavaDoc was unable to locate the DTO/resource class
        return invalidLink(tag, String.format("can't find referenced class %s", tag.referencedClassName()));
      }

      // Check for a resource class first
      res = application.findResourceClass(cDoc);
      if (res != null) {
        linkText = Utils.getAbsolutePath(this.config, res);
      }

      // If we found a resource class, then we can look further for a resource method
      if ((res != null) && (tag.referencedMemberName() != null)) {
        MemberDoc member = tag.referencedMember();
        if (member == null) {
          // No point looking if JavaDoc can't find the member
          return invalidLink(tag, String.format("can't find resource method %s in %s", tag.referencedMemberName(), cDoc.qualifiedName()));
        }

        if (member instanceof MethodDoc) {
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
    // Fall back to looking for DTO types/members or linked objects using standard mechanism.
    return super.seeTagToString(tag);
  }

  private String getDisplayText(Resource resource, ResourceMethod rMethod) {
    final StringBuilder sb = new StringBuilder();
    if (!rMethod.isResourceLocator()) {
      List<String> methods = rMethod.getMethods();
      for (int i = 0; i < methods.size(); i++) {
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
    config.parentConfiguration.root.printWarning(tag.position(), "Tag " + tag.name() + ": " + msg);
    return String.format("<code class='invalid-link'>%s</code>", tag.text());
  }

  public JAXRSApplication getApplication() {
    return application;
  }

}
