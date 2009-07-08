package com.lunatech.doclets.jax.jaxb.model;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ProgramElementDoc;

public class Value extends JAXBMember {

	public Value(JAXBClass klass, ProgramElementDoc property, String name,
			AnnotationDesc xmlValueAnnotation) {
		super(klass, property, name, xmlValueAnnotation);
	}

}
