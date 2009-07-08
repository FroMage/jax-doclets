package com.lunatech.doclets.jax.jaxrs;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Path;

import com.lunatech.doclets.jax.Utils;
import com.lunatech.doclets.jax.jaxrs.model.Resource;
import com.lunatech.doclets.jax.jaxrs.model.ResourceClass;
import com.lunatech.doclets.jax.jaxrs.model.ResourceMethod;
import com.lunatech.doclets.jax.jaxrs.tags.HTTPTaglet;
import com.lunatech.doclets.jax.jaxrs.writers.SummaryWriter;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.formats.html.HtmlDoclet;
import com.sun.tools.doclets.internal.toolkit.AbstractDoclet;
import com.sun.tools.doclets.internal.toolkit.taglets.LegacyTaglet;

public class JAXRSDoclet {
	public static final HtmlDoclet htmlDoclet = new HtmlDoclet();

	private static final Class<?>[] jaxrsAnnotations = new Class<?>[] { Path.class };

	public static int optionLength(final String option) {
		return HtmlDoclet.optionLength(option);
	}

	public static boolean validOptions(final String[][] options,
			final DocErrorReporter reporter) {
		return HtmlDoclet.validOptions(options, reporter);
	}

	public static LanguageVersion languageVersion() {
		return AbstractDoclet.languageVersion();
	}

	private static List<ResourceMethod> jaxrsMethods = new LinkedList<ResourceMethod>();

	public static boolean start(final RootDoc rootDoc) {
		htmlDoclet.configuration.root = rootDoc;
		htmlDoclet.configuration.setOptions();
		htmlDoclet.configuration.tagletManager.addCustomTag(new LegacyTaglet(
				new HTTPTaglet()));
		final ClassDoc[] classes = rootDoc.classes();
		for (final ClassDoc klass : classes) {
			if (Utils.findAnnotatedClass(klass, jaxrsAnnotations) != null) {
				handleJAXRSClass(klass);
			}
		}
		Collections.sort(jaxrsMethods);
		Resource rootResource = Resource.getRootResource(jaxrsMethods);
		rootResource.write(htmlDoclet.configuration);
		new SummaryWriter(htmlDoclet.configuration, rootResource).write();
		Utils.copyResources(htmlDoclet.configuration);
		return true;
	}

	private static void handleJAXRSClass(final ClassDoc klass) {
		jaxrsMethods.addAll(new ResourceClass(klass).getMethods());
	}

}
