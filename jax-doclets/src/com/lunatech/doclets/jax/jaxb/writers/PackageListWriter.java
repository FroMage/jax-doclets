package com.lunatech.doclets.jax.jaxb.writers;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.lunatech.doclets.jax.jaxb.model.JAXBClass;
import com.lunatech.doclets.jax.jaxb.model.Registry;
import com.sun.tools.doclets.formats.html.ConfigurationImpl;
import com.sun.tools.doclets.formats.html.HtmlDocletWriter;
import com.sun.tools.doclets.internal.toolkit.Configuration;

public class PackageListWriter extends
		com.lunatech.doclets.jax.writers.DocletWriter {

	private Registry registry;

	public PackageListWriter(Configuration configuration, Registry registry) {
		super(configuration, getWriter(configuration));
		this.registry = registry;
	}

	private static HtmlDocletWriter getWriter(Configuration configuration) {
		try {
			return new HtmlDocletWriter((ConfigurationImpl) configuration, "",
					"package-list", "");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void write() {
		Set<String> packages = new HashSet<String>();
		for (JAXBClass klass : registry.getJAXBClasses()) {
			packages.add(klass.getPackageName());
		}
		for (String packageName : packages)
			print(packageName + "\n");
		writer.flush();
		writer.close();
	}
}
