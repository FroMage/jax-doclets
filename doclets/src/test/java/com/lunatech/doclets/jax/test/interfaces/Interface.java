package com.lunatech.doclets.jax.test.interfaces;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/interface")
public interface Interface {

  /** This is where the doc should be */
  @GET
  @Path("a")
  public String getA();

  /** This is where the doc should be */
  @GET
  @Path("b")
  public String getB();
}
