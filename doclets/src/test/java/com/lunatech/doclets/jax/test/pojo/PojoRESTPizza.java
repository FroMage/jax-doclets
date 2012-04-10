package com.lunatech.doclets.jax.test.pojo;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jboss.resteasy.annotations.Form;

import com.lunatech.doclets.jax.test.demo.doc.Query;

@Consumes({ "application/ingredient" })
@Produces({ "application/PojoPizza" })
@Path("/plainPojoPizzas")
public class PojoRESTPizza {

  /**
   * The PojoPizzas resource
   *
   * @return the list of all the PojoPizzas matching the query
   */
  @GET
  public List<PojoPizza> getPojoPizzas(@Form Query query) {
    // retrieve PojoPizzas
    return Collections.emptyList();
  }

  /**
   * The PojoPizza resource
   *
   * @param name
   *          the name of the PojoPizza to find
   * @return the PojoPizza with the given name
   * @HTTP 404 Sent when the PojoPizza doesn't exist.
   */
  @GET
  @Path("{name}")
  public PojoPizza getPojoPizza(@PathParam("name") String name) {
    // fetch PojoPizza
    return new PojoPizza();
  }
}
