package com.lunatech.doclets.jax.jaxrs.writers;

import com.lunatech.doclets.jax.jaxrs.model.Resource;
import com.sun.tools.doclets.formats.html.HtmlDocletWriter;
import com.sun.tools.doclets.internal.toolkit.Configuration;

public class DocletWriter extends com.lunatech.doclets.jax.writers.DocletWriter {
	protected Resource resource;

	public DocletWriter(Configuration configuration, HtmlDocletWriter writer,
			Resource resource) {
		super(configuration, writer);
		this.resource = resource;
	}

	public Resource getResource() {
		return resource;
	}

	protected void printOtherMenuItems(String selected) {
		printMenuItem("Root resource", writer.relativePath + "index.html",
				selected);
	}
}
