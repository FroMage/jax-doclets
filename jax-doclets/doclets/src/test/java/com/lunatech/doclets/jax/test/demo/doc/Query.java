package com.lunatech.doclets.jax.test.demo.doc;

import javax.ws.rs.CookieParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.QueryParam;

public class Query {

  /** The user session ID */
  @CookieParam("session")
  String session;

  /** The style of pizza to use */
  @HeaderParam("X-Pizza-Style")
  String style;

  /** The maximum number of pizzas to return */
  @MatrixParam("size")
  int size;

  /** A query string to be matched against the pizza names */
  @QueryParam("q")
  String query;
}
