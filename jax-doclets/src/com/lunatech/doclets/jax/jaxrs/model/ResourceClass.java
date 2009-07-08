package com.lunatech.doclets.jax.jaxrs.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.lunatech.doclets.jax.Utils;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;

public class ResourceClass {

	private List<ResourceMethod> methods = new LinkedList<ResourceMethod>();
	private ClassDoc declaringClass;
	private AnnotationDesc rootPathAnnotation;
	private AnnotationDesc rootProducesAnnotation;
	private AnnotationDesc rootConsumesAnnotation;

	public ResourceClass(ClassDoc resourceClass) {
		// find the annotated class or interface
		declaringClass = Utils.findAnnotatedClass(resourceClass, Path.class);
		rootPathAnnotation = Utils.findAnnotation(declaringClass, Path.class);
		rootProducesAnnotation = Utils.findAnnotation(declaringClass,
				Produces.class);
		rootConsumesAnnotation = Utils.findAnnotation(declaringClass,
				Consumes.class);
		for (final MethodDoc method : resourceClass.methods(false)) {
			MethodDoc declaringMethod = Utils.findAnnotatedMethod(
					declaringClass, method, Path.class);
			if (declaringMethod != null) {
				methods.add(new ResourceMethod(method, declaringMethod, this));
			}
		}

	}

	public ClassDoc getDeclaringClass() {
		return declaringClass;
	}

	public AnnotationDesc getRootPathAnnotation() {
		return rootPathAnnotation;
	}

	public AnnotationDesc getProducesAnnotation() {
		return rootProducesAnnotation;
	}

	public AnnotationDesc getConsumesAnnotation() {
		return rootConsumesAnnotation;
	}

	public Collection<ResourceMethod> getMethods() {
		return methods;
	}
}
