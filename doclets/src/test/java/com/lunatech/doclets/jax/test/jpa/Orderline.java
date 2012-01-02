package com.lunatech.doclets.jax.test.jpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Orderline {

  @Id
  @GeneratedValue
  public String id;

  @ManyToOne
  public Order order;
}
