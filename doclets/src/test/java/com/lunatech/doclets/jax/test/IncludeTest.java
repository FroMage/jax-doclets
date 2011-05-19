package com.lunatech.doclets.jax.test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("includes")
public class IncludeTest {
  /**
   *  This is an include test
   *  @include foo.html
   */
  @GET
  public String get(){return null;}
}
