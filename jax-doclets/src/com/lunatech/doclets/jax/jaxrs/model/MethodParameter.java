package com.lunatech.doclets.jax.jaxrs.model;

import com.lunatech.doclets.jax.Utils;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.Type;

public class MethodParameter {

	private Parameter parameter;
	private AnnotationDesc paramAnnotation;
	private MethodParameterType type;
	private MethodDoc method;
	private int parameterIndex;

	public MethodParameter(Parameter parameter, int parameterIndex,
			AnnotationDesc paramAnnotation, MethodParameterType type,
			MethodDoc method) {
		this.parameter = parameter;
		this.paramAnnotation = paramAnnotation;
		this.type = type;
		this.method = method;
		this.parameterIndex = parameterIndex;
	}

	public String getName() {
		return (String) Utils.getAnnotationValue(paramAnnotation);
	}

	public String getDoc() {
		Parameter overriddenParameter = method.parameters()[parameterIndex];
		for (ParamTag paramTag : method.paramTags()) {
			if (overriddenParameter.name().equals(paramTag.parameterName()))
				return paramTag.parameterComment();
		}
		return "";
	}

	public String getTypeName() {
		return parameter.type().qualifiedTypeName()
				+ parameter.type().dimension();
	}

	public Type getType() {
		return parameter.type();
	}
}
