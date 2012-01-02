package com.lunatech.doclets.jax.test.jpa;

import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

/**
 * This is the order type, bla bla
 * 
 * @author stephane
 */
@Entity
public class Order {

  /**
   * This is my ID and I love it
   */
  @Id
  @GeneratedValue
  public Long id;

  @Column(name = "column_with_name")
  public String columnWithName;

  @Column
  public String columnWithAnnotation;

  public String columnWithoutAnnotation;

  public transient String transientColumn;

  @Transient
  public String transientAnnotatedColumn;

  @JoinColumn(name = "bill_id")
  @OneToOne
  public Bill bill;

  @OneToMany(mappedBy = "order")
  public List<Orderline> orderlineList;

  @OneToMany(mappedBy = "order")
  public Set<Orderline> orderlineSet;

  @JoinTable(name = "order2item", joinColumns = @JoinColumn(name = "item_id"), inverseJoinColumns = @JoinColumn(name = "order_id"))
  @ManyToMany
  public Set<Item> itemSet;
}
