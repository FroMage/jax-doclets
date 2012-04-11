package com.lunatech.doclets.jax.test.pojo;

/**
 * General type for pizza ingredients.
 * <p>
 * Maybe you want {@link APizzaIngredientThatDoesntExist}.
 * <p>
 * Or maybe you want to look at {@link Ingredient#aMethodThatDoesntExist()}
 * <p>
 * Or maybe you needed to look at {@link PojoRESTPizza#aResourceMethodThatDoesntExist()}
 * 
 */
public class Ingredient {

  private boolean fresh;

  /**
   * Is the ingredient fresh?
   */
  public boolean isFresh() {
    return fresh;
  }

  public void setFresh(boolean fresh) {
    this.fresh = fresh;
  }

}
