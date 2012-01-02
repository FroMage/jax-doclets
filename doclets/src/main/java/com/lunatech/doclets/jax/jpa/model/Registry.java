/*
    Copyright 2009-2011 Lunatech Research
    Copyright 2009-2011 Stéphane Épardaud
    
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
package com.lunatech.doclets.jax.jpa.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Registry {

  private Map<String, JPAClass> jpaClasses = new HashMap<String, JPAClass>();

  public void addJPAClass(JPAClass klass) {
    jpaClasses.put(klass.getQualifiedClassName(), klass);
  }

  public boolean isJPAClass(String className) {
    return jpaClasses.containsKey(className);
  }

  public JPAClass getJPAClass(String name) {
    return jpaClasses.get(name);
  }

  public Collection<JPAClass> getJPAClasses() {
    return jpaClasses.values();
  }

}
