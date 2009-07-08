package com.lunatech.doclets.jax.jaxrs.tags;

import java.util.Map;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

public class HTTPTaglet implements Taglet {

	public static final String NAME = "HTTP";
	private static final String HEADER = "HTTP return code:";

	public String getName() {
		return NAME;
	}

	public boolean inField() {
		return false;
	}

	public boolean inConstructor() {
		return false;
	}

	public boolean inMethod() {
		return true;
	}

	public boolean inOverview() {
		return false;
	}

	public boolean inPackage() {
		return false;
	}

	public boolean inType() {
		return false;
	}

	public boolean isInlineTag() {
		return false;
	}

	public static void register(Map tagletMap) {
		HTTPTaglet tag = new HTTPTaglet();
		Taglet t = (Taglet) tagletMap.get(tag.getName());
		if (t != null) {
			tagletMap.remove(tag.getName());
		}
		tagletMap.put(tag.getName(), tag);
	}

	public String toString(Tag tag) {
		return "<DT><B>" + HEADER + "</B><DD>" + tag.text() + "</DD>\n";
	}

	public String toString(Tag[] tags) {
		if (tags.length == 0) {
			return null;
		}
		String result = "\n<DT><B>" + HEADER + "</B><DD>";
		for (int i = 0; i < tags.length; i++) {
			if (i > 0) {
				result += ", ";
			}
			result += tags[i].text();
		}
		return result + "</DD>\n";
	}

}
