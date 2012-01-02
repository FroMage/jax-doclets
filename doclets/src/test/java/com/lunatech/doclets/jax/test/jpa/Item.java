package com.lunatech.doclets.jax.test.jpa;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Table(name = "item")
@Entity
public class Item {

  @Id
  public Long id;

  @ManyToMany(mappedBy = "itemSet")
  public Set<Order> orderSet;
}
