package com.lunatech.doclets.jax.test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Used for subresources
 */
public class LamaSubResource {

  /**
   * Gets a lama
   * 
   * @param bar
   *          the bar
   * @return the lama
   */
  @Path("/lama/{bar}")
  @GET
  public String get(@PathParam("bar") String bar) {
    return null;
  }
}
