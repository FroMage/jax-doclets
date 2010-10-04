package com.lunatech.doclets.jax.test;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

@Path("/doc-method-order")
public class DocOrder {

  /**
   * The GET method
   */
  @GET
  @Path("get")
  public void get1() {}

  /**
   * The POST method
   */
  @POST
  @Path("get")
  public void post1() {}

  /**
   * The PUT method
   */
  @PUT
  @Path("get")
  public void put1() {}

  /**
   * The DELETE method
   */
  @DELETE
  @Path("get")
  public void delete1() {}

  /**
   * The HEAD method
   */
  @HEAD
  @Path("get")
  public void head1() {}

  // remove GET

  /**
   * The POST method
   */
  @POST
  @Path("head")
  public void post2() {}

  /**
   * The PUT method
   */
  @PUT
  @Path("head")
  public void put2() {}

  /**
   * The DELETE method
   */
  @DELETE
  @Path("head")
  public void delete2() {}

  /**
   * The HEAD method
   */
  @HEAD
  @Path("head")
  public void head2() {}

  // remove HEAD

  /**
   * The POST method
   */
  @POST
  @Path("post")
  public void post3() {}

  /**
   * The PUT method
   */
  @PUT
  @Path("post")
  public void put3() {}

  /**
   * The DELETE method
   */
  @DELETE
  @Path("post")
  public void delete3() {}

  // remove POST

  /**
   * The PUT method
   */
  @PUT
  @Path("put")
  public void put4() {}

  /**
   * The DELETE method
   */
  @DELETE
  @Path("put")
  public void delete4() {}

  // remove PUT

  /**
   * The DELETE method
   */
  @DELETE
  @Path("delete")
  public void delete5() {}

}
