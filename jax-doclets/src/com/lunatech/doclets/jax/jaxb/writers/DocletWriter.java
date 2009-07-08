package com.lunatech.doclets.jax.jaxb.writers;

import com.lunatech.doclets.jax.jaxb.model.JAXBClass;
import com.sun.tools.doclets.formats.html.HtmlDocletWriter;
import com.sun.tools.doclets.internal.toolkit.Configuration;

public class DocletWriter extends com.lunatech.doclets.jax.writers.DocletWriter {
	protected JAXBClass jaxbClass;

	public DocletWriter(Configuration configuration, HtmlDocletWriter writer,
			JAXBClass jaxbClass) {
		super(configuration, writer);
		this.jaxbClass = jaxbClass;
	}

	public JAXBClass getJAXBClass() {
		return jaxbClass;
	}

}
