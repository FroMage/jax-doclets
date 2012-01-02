package com.lunatech.doclets.jax.test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("{foo:.*}-{bar:a{2}}")
public class RegexPathTest {

  @GET
  @Path("{more:a{2}b{3,2}}{again}")
  public String get(@PathParam("foo") String foo, @PathParam("bar") String bar, @PathParam("more") String more,
                    @PathParam("again") String again) {
    return "foo";
  }
}
