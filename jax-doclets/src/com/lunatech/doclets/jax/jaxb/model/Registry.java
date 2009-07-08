package com.lunatech.doclets.jax.jaxb.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Registry {

	private Map<String, JAXBClass> jaxbClasses = new HashMap<String, JAXBClass>();

	public void addJAXBClass(JAXBClass klass) {
		jaxbClasses.put(klass.getQualifiedClassName(), klass);
	}

	public boolean isJAXBClass(String className) {
		return jaxbClasses.containsKey(className);
	}

	public JAXBClass getJAXBClass(String name) {
		return jaxbClasses.get(name);
	}

	public Collection<JAXBClass> getJAXBClasses() {
		return jaxbClasses.values();
	}

}
