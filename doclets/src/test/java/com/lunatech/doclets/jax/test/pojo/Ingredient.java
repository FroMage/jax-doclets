package com.lunatech.doclets.jax.test.pojo;

/**
 * General type for pizza ingredients.
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
