package com.lunatech.doclets.jax.test.demo.nodoc;

import javax.ws.rs.CookieParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.QueryParam;

public class UndocumentedQuery {

  @CookieParam("session")
  String session;

  @HeaderParam("X-Pizza-Style")
  String style;

  @MatrixParam("size")
  int size;

  @QueryParam("q")
  String query;
}
