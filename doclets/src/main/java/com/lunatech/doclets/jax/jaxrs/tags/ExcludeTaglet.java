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

import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.taglets.Taglet;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletOutput;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletWriter;

public class ExcludeTaglet implements Taglet {

  public static final String NAME = "exclude";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean inField() {
    return true;
  }

  @Override
  public boolean inConstructor() {
    return true;
  }

  @Override
  public boolean inMethod() {
    return true;
  }

  @Override
  public boolean inOverview() {
    return true;
  }

  @Override
  public boolean inPackage() {
    return true;
  }

  @Override
  public boolean inType() {
    return true;
  }

  @Override
  public boolean isInlineTag() {
    return false;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static void register(Map tagletMap) {
    ExcludeTaglet tag = new ExcludeTaglet();
    Taglet t = (Taglet) tagletMap.get(tag.getName());
    if (t != null) {
      tagletMap.remove(tag.getName());
    }
    tagletMap.put(tag.getName(), tag);
  }

  /**
   * Exclude tag does not support output.
   */
  @Override
  public TagletOutput getTagletOutput(Tag tag, TagletWriter writer) throws IllegalArgumentException {
    throw new IllegalArgumentException();
  }

  /**
   * Exclude tag does not support output.
   */
  @Override
  public TagletOutput getTagletOutput(Doc doc, TagletWriter writer) throws IllegalArgumentException {
    throw new IllegalArgumentException();
  }

}
