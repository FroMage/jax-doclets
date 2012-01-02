package com.lunatech.doclets.jax.test.demo.doc;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.annotations.Form;

@Consumes({ "application/ingredient" })
@Produces({ "application/pizza" })
@Path("/pizzas")
public class RESTPizza {

  private PizzaDAO pizzaDAO;

  /**
   * The pizzas resource
   * 
   * @return the list of all the pizzas matching the query
   */
  @GET
  public List<Pizza> getPizzas(@Form Query query) {
    // retrieve pizzas
    return pizzaDAO.getPizzas(query.style, query.size, query.query);
  }

  /**
   * Creates a new pizza by assembling the given ingredients
   * 
   * @param ingredients
   *          the ingredients to use in the new pizza
   * @ResponseHeader Location the URI of the newly created pizza
   * @returnWrapped void This operation returns no entity
   */
  @POST
  public Response makePizza(List<Ingredient> ingredients, @Context UriInfo uriInfo) {
    // store pizza
    Pizza pizza = pizzaDAO.makePizza(ingredients);
    return Response.created(uriInfo.getBaseUriBuilder().path(getClass(), "getPizza").build(pizza.getName())).build();
  }

  /**
   * The pizza resource
   * 
   * @param name
   *          the name of the pizza to find
   * @return the pizza with the given name
   * @HTTP 404 Sent when the pizza doesn't exist.
   */
  @GET
  @Path("{name}")
  public Pizza getPizza(@PathParam("name") String name) {
    // fetch pizza
    Pizza pizza = pizzaDAO.getPizza(name);
    return pizza;
  }
}
