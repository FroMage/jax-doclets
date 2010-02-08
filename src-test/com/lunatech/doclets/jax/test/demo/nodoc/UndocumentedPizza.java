package com.lunatech.doclets.jax.test.demo.nodoc;

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

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UndocumentedPizza {

  @XmlAttribute
  private PizzaStyle style;

  @XmlTransient
  private Date expirationDate;

  @XmlElementWrapper(name = "ingredients")
  @XmlElement(name = "ingredient")
  private List<UndocumentedIngredient> ingredients;

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

  public List<UndocumentedIngredient> getIngredients() {
    return ingredients;
  }

  public void setIngredients(List<UndocumentedIngredient> ingredients) {
    this.ingredients = ingredients;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
