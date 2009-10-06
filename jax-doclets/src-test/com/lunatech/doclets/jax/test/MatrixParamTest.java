package com.lunatech.doclets.jax.test;

import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;

@Path("switches")
public class MatrixParamTest {

  /**
   * e.g. /switches/search=switch1
   */
  @GET
  public String[] search(@MatrixParam("search") String search) {
    return null;
  }

}
