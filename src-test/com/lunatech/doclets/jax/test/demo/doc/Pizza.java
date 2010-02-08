package com.lunatech.doclets.jax.test.demo.doc;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.lunatech.doclets.jax.test.demo.PizzaStyle;

/**
 * The pizza type, cool as hell
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Pizza {

  /**
   * The pizza style, choose with care.
   */
  @XmlAttribute
  private PizzaStyle style;

  /**
   * Bah, who needs that?
   */
  @XmlTransient
  private Date expirationDate;

  /**
   * The list of ingredients.
   */
  @XmlElementWrapper(name = "ingredients")
  @XmlElement(name = "ingredient")
  private List<Ingredient> ingredients;

  /**
   * The pizza name.
   */
  @XmlID
  private String name;

  public PizzaStyle getStyle() {
    return style;
  }

  public void setStyle(PizzaStyle style) {
    this.style = style;
  }

  public Date getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(Date expirationDate) {
    this.expirationDate = expirationDate;
  }

  public List<Ingredient> getIngredients() {
    return ingredients;
  }

  public void setIngredients(List<Ingredient> ingredients) {
    this.ingredients = ingredients;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
