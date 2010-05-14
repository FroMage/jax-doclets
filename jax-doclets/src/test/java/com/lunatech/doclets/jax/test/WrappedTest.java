package com.lunatech.doclets.jax.test;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Path("/wrapped")
public class WrappedTest {

  @XmlRootElement
  public static class Foo<T, V> {

    @XmlElement
    String id;
  }

  @GET
  public List<Foo<Integer, Object>> get() {
    return null;
  }

  /**
   * 
   * @returnWrapped List<Foo<Integer,Object>> the list
   */
  @Path("wrapped")
  @GET
  public Response getWrapped() {
    return null;
  }

  @Path("void")
  @GET
  public void getVoid() {}

  @Path("primitive")
  @GET
  public int getInt() {
    return 0;
  }

  @Path("integer")
  @GET
  public Integer getInteger() {
    return null;
  }

}
