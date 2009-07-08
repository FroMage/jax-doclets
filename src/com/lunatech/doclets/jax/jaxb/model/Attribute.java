package com.lunatech.doclets.jax.jaxb.model;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ProgramElementDoc;

public class Attribute extends JAXBMember {

	public Attribute(JAXBClass klass, ProgramElementDoc property, String name,
			AnnotationDesc xmlAttributeAnnotation) {
		super(klass, property, name, xmlAttributeAnnotation);
	}

}
