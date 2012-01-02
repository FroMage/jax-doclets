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
package com.lunatech.doclets.jax.jaxrs.tags;

import java.util.Map;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

public class HTTPTaglet implements Taglet {

  public static final String NAME = "HTTP";

  private static final String HEADER = "HTTP return codes:";

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

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static void register(Map tagletMap) {
    HTTPTaglet tag = new HTTPTaglet();
    Taglet t = (Taglet) tagletMap.get(tag.getName());
    if (t != null) {
      tagletMap.remove(tag.getName());
    }
    tagletMap.put(tag.getName(), tag);
  }

  public String toString(Tag tag) {
    return "<DT><B>" + HEADER + "</B></DT><DD><B>" + getHTTPCode(tag) + "</B> - " + getHTTPDoc(tag) + "</DD>\n";
  }

  public String toString(Tag[] tags) {
    if (tags.length == 0) {
      return null;
    }
    String result = "\n<DT><B>" + HEADER + "</B></DT>";
    for (int i = 0; i < tags.length; i++) {
      result += "<DD><B>" + getHTTPCode(tags[i]) + "</B> - " + getHTTPDoc(tags[i]) + "</DD>\n";
    }
    return result + "\n";
  }

  public String getHTTPCode(Tag tag) {
    String text = tag.text().trim();
    int ws = text.indexOf(' ');
    if (ws > -1)
      return text.substring(0, ws);
    return text;
  }

  public String getHTTPDoc(Tag tag) {
    String text = tag.text().trim();
    int ws = text.indexOf(' ');
    if (ws > -1)
      return text.substring(ws + 1);
    return null;
  }

}
