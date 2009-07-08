package com.lunatech.doclets.jax.jaxrs.writers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.lunatech.doclets.jax.Utils;
import com.lunatech.doclets.jax.jaxrs.model.Resource;
import com.lunatech.doclets.jax.jaxrs.model.ResourceMethod;
import com.sun.javadoc.Doc;
import com.sun.tools.doclets.formats.html.ConfigurationImpl;
import com.sun.tools.doclets.formats.html.HtmlDocletWriter;
import com.sun.tools.doclets.internal.toolkit.Configuration;

public class ResourceWriter extends DocletWriter {

	public ResourceWriter(Configuration configuration, Resource resource) {
		super(configuration, getWriter(configuration, resource), resource);
	}

	private static HtmlDocletWriter getWriter(Configuration configuration,
			Resource resource) {
		String pathName = Utils.urlToPath(resource);
		System.err.println(pathName);
		try {
			return new HtmlDocletWriter((ConfigurationImpl) configuration,
					pathName, "index.html", Utils.urlToRoot(resource));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void write() {
		boolean isRoot = resource.getParent() == null;
		String selected = isRoot ? "Root resource" : "";
		printHeader();
		printMenu(selected);
		printResourceInfo();
		printSubresources();
		printMethods();
		tag("hr");
		printMenu(selected);
		printFooter();
		writer.flush();
	}

	private void printMethods() {
		List<ResourceMethod> methods = resource.getMethods();
		if (methods.isEmpty())
			return;
		tag("hr");
		open("table");
		open("tr");
		open("th");
		print("Methods");
		close("th");
		close("tr");
		for (ResourceMethod method : methods) {
			open("tr");
			open("td");
			new MethodWriter(method, this).print();
			close("td");
			close("tr");
		}
		close("table");
	}

	private void printSubresources() {
		Map<String, Resource> resources = resource.getResources();
		if (resources.isEmpty())
			return;
		tag("hr");
		open("table");
		open("tr");
		open("th colspan='2'");
		print("Resources");
		close("th");
		close("tr");
		open("tr class='subheader'");
		open("td");
		print("Name");
		close("td");
		open("td");
		print("Description");
		close("td");
		close("tr");
		for (String subResourceKey : resources.keySet()) {
			Resource subResource = resources.get(subResourceKey);
			open("tr");
			open("td");
			open("a href='" + subResource.getName() + "/index.html'");
			print(subResource.getName());
			close("a");
			close("td");
			open("td");
			Doc javaDoc = subResource.getJavaDoc();
			if (javaDoc != null && javaDoc.firstSentenceTags() != null)
				writer.printSummaryComment(javaDoc);
			close("td");
			close("tr");
		}
		close("table");
	}

	private void printResourceInfo() {
		open("h2");
		print("Path: ");
		String name = resource.getName();
		if (name.length() == 0)
			name = "/";
		StringBuffer buf = new StringBuffer(name);
		Resource resource = this.resource;
		String rel = "";
		while ((resource = resource.getParent()) != null) {
			rel = "../" + rel;
			String href = "<a href='" + rel + "index.html'>"
					+ resource.getName();
			if (resource.getName().length() == 0)
				href += "/</a> ";
			else
				href += "</a> / ";
			buf.insert(0, href);
		}
		print(buf.toString());
		close("h2");
		Doc javaDoc = this.resource.getJavaDoc();
		if (javaDoc != null && javaDoc.tags() != null) {
			writer.printInlineComment(javaDoc);
		}
	}

	private void printHeader() {
		printHeader("Resource " + resource.getName());
	}

}
