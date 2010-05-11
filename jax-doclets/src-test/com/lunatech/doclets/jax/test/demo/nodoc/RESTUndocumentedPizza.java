package com.lunatech.doclets.jax.test.demo.nodoc;

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


@Consumes( { "application/ingredient" })
@Produces( { "application/pizza" })
@Path("/pizzas")
public class RESTUndocumentedPizza {

  private UndocumentedPizzaDAO pizzaDAO;

  @GET
  public List<UndocumentedPizza> getPizzas(@Form UndocumentedQuery query) {
    // retrieve pizzas
    return pizzaDAO.getPizzas(query.style, query.size, query.query);
  }

  @POST
  public Response makePizza(List<UndocumentedIngredient> ingredients, @Context UriInfo uriInfo) {
    // store pizza
    UndocumentedPizza pizza = pizzaDAO.makePizza(ingredients);
    return Response.created(uriInfo.getBaseUriBuilder().path(getClass(), "getPizza").build(pizza.getName())).build();
  }

  @GET
  @Path("{name}")
  public UndocumentedPizza getPizza(@PathParam("name") String name) {
    // fetch pizza
    UndocumentedPizza pizza = pizzaDAO.getPizza(name);
    return pizza;
  }
}
