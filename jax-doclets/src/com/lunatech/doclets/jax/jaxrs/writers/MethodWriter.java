/*
    Copyright 2009 Lunatech Research
    
    This file is part of jax-doclets.

    jax-doclets is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    jax-doclets is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with jax-doclets.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.lunatech.doclets.jax.jaxrs.writers;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lunatech.doclets.jax.Utils;
import com.lunatech.doclets.jax.jaxrs.model.MethodParameter;
import com.lunatech.doclets.jax.jaxrs.model.ResourceMethod;
import com.lunatech.doclets.jax.jaxrs.tags.HTTPTaglet;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.formats.html.TagletOutputImpl;

public class MethodWriter extends DocletWriter {
	private ResourceMethod method;

	public MethodWriter(ResourceMethod method, ResourceWriter resourceWriter) {
		super(resourceWriter.getConfiguration(), resourceWriter.getWriter(),
				resourceWriter.getResource());
		this.method = method;
	}

	public void print() {
		for (String httpMethod : method.getMethods()) {
			printMethod(httpMethod);
		}
	}

	private void printMethod(String httpMethod) {
		open("pre");
		print(httpMethod + " " + resource.getAbsolutePath());
		Map<String, MethodParameter> queryParameters = method
				.getQueryParameters();
		if (!queryParameters.isEmpty()) {
			print("?");
			boolean first = true;
			for (String name : queryParameters.keySet()) {
				if (!first)
					print("&");
				print(name);
				first = false;
			}
		}
		close("pre");
		if (!Utils.isEmptyOrNull(method.getDoc())) {
			open("p");
			print(method.getDoc());
			close("p");
		}
		open("dl");
		printInput();
		printParameters(queryParameters, "Query");
		printParameters(method.getPathParameters(), "Path");
		printParameters(method.getMatrixParameters(), "Matrix");
		printMIMEs(method.getProduces(), "Produces");
		printMIMEs(method.getConsumes(), "Consumes");
		printHTTPCodes();
		close("dl");
	}

	private void printHTTPCodes() {
		MethodDoc javaDoc = method.getJavaDoc();
		TagletOutputImpl output = new TagletOutputImpl("");
		Set<String> tagletsSet = new HashSet<String>();
		tagletsSet.add(HTTPTaglet.NAME);
		Utils.genTagOuput(configuration.tagletManager, javaDoc,
				configuration.tagletManager.getCustomTags(javaDoc), writer
						.getTagletWriterInstance(false), output, tagletsSet);
		writer.print(output.toString());
	}

	private void printInput() {
		MethodParameter inputParameter = method.getInputParameter();
		if (inputParameter == null)
			return;
		open("dt");
		around("b", "Input:");
		close("dt");
		open("dd");
		Type type = inputParameter.getType();
		String link = null;
		if (!type.isPrimitive()) {
			link = configuration.extern.getExternalLink(type.asClassDoc()
					.containingPackage().name(), writer.relativePath, type
					.typeName()
					+ ".html");
		}
		if (link == null) {
			String typeName = inputParameter.getTypeName();
			around("tt", typeName);
		} else {
			around("a href='" + link + "'", type.typeName());
		}
		writer.print(" - ");
		writer.print(inputParameter.getDoc());
		close("dd");
	}

	private void printParameters(Map<String, MethodParameter> parameters,
			String header) {
		if (parameters.isEmpty())
			return;
		open("dt");
		around("b", header + " parameters:");
		close("dt");
		for (MethodParameter param : parameters.values()) {
			open("dd");
			around("b", param.getName());
			print(" - " + param.getDoc());
			close("dd");
		}
	}

	private void printMIMEs(List<String> mimes, String header) {
		if (!mimes.isEmpty()) {
			open("dt");
			around("b", header + ":");
			close("dt");
			for (String mime : mimes) {
				open("dd");
				print(mime);
				close("dd");
			}
		}
	}

}
