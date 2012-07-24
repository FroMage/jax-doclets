package com.lunatech.doclets.jax.test.pojo;

import java.util.Date;
import java.util.List;

/**
 * A plainer style of Pizza.
 * 
 */
public class PojoPizza {

    private PizzaStyle style;

    private Date expirationDate;

    private List<Ingredient> ingredients;

    private String name;

    /**
     * The pizza style, choose with care.
     */
    public PizzaStyle getStyle() {
      return style;
    }

    public void setStyle(PizzaStyle style) {
      this.style = style;
    }

    /**
     * Bah, who needs that?
     */
    public Date getExpirationDate() {
      return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
      this.expirationDate = expirationDate;
    }

    /**
     * The list of ingredients.
     */
    public List<Ingredient> getIngredients() {
      return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
      this.ingredients = ingredients;
    }

    /**
     * The pizza name.
     */
    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

}
