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

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

import java.util.Map;

public class IncludeTaglet implements Taglet {

  public static final String NAME = "include";

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
    IncludeTaglet tag = new IncludeTaglet();
    Taglet t = (Taglet) tagletMap.get(tag.getName());
    if (t != null) {
      tagletMap.remove(tag.getName());
    }
    tagletMap.put(tag.getName(), tag);
  }

  public String toString(Tag tag) {
    return "";
  }

  public String toString(Tag[] tags) {
    return "";
  }

}
