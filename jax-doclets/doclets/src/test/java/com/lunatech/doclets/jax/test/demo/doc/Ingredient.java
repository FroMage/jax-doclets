package com.lunatech.doclets.jax.test.demo.doc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * Ingredients are essential to any tasty pizza
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Ingredient {

  /**
   * Not too much, never too few.
   */
  @XmlAttribute
  private int quantity;

  /**
   * Name of the ingredient.
   */
  @XmlID
  @XmlValue
  private String name;

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
