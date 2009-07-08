/*
    Copyright 2009 Lunatech Research
    
    This file is part of jax-doclets.

    jax-doclets is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    jax-doclets is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with jax-doclets.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.lunatech.doclets.jax.test.interfaces;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.lunatech.doclets.jax.test.RESTOrdersBean.Comment;
import com.lunatech.doclets.jax.test.RESTOrdersBean.Order;
import com.lunatech.doclets.jax.test.RESTOrdersBean.OrderLine;
import com.lunatech.doclets.jax.test.RESTOrdersBean.OrderStatus;
import com.lunatech.doclets.jax.test.RESTOrdersBean.ResourceLink;

/**
 * RESTful base resource class to obtain a list of {@Link Order} keys,
 * reading or updating/creating an {@Link Order}, its {@Link
 * Comment}s, {@Link OrderLine}s, {@Link Status}es and {@Link
 *  Consignments}s.
 * 
 * @author stephane
 */
@Path("/")
@Produces( { "application/xml", "text/plain" })
public interface RESTOrders {

  //
  // Orders

  /**
   * List of all orders
   * 
   * @param creatorKey
   *          filter results so that their creators must match this key
   * @param filter
   *          name of a filter to apply
   * @return a list of orders
   */
  @Path("/orders")
  @GET
  public Response getOrders(@QueryParam("creator") String creatorKey, @QueryParam("filter") final String filter);

  /**
   * Adds a new order
   * 
   * @param newOrder
   *          the new order to add
   * @HTTP 201 When created
   * @HTTP 401 If authentication failed
   */
  @Path("/orders")
  @POST
  public Response postOrders(final Order newOrder, @Context UriInfo uriInfo);

  @Path("/orders")
  @PUT
  public Response putOrders();

  @Path("/orders")
  @DELETE
  public Response deleteOrders();

  @Path("/orders")
  @HEAD
  public Response headOrders();

  //
  // Order

  /**
   * The order resource
   * 
   * @param orderKey
   *          the order key
   * @return the order found
   */
  @Path("/order/{key}")
  @GET
  public Response getOrder(@PathParam("key") final String orderKey);

  /**
   * Creates or updates a new order
   * 
   * @param orderKey
   *          the order key
   * @param order
   *          the order to create or update
   * @HTTP 201 if created
   * @HTTP 200 if updated
   */
  @Path("/order/{key}")
  @PUT
  public Response putOrder(@PathParam("key") final String orderKey, Order order, @Context UriInfo uriInfo);

  @Path("/order/{key}")
  @POST
  public Response postOrder();

  @Path("/order/{key}")
  @DELETE
  public Response deleteOrder();

  @Path("/order/{key}")
  @HEAD
  public Response headOrder();

  //
  // Order comments

  @Path("/order/{key}/comments")
  @GET
  public Response getOrderComments(@PathParam("key") final String orderKey);

  @Path("/order/{key}/comments")
  @POST
  public Response postOrderComments(@PathParam("key") final String orderKey, Comment comment);

  @Path("/order/{key}/comments")
  @PUT
  public Response putOrderComments();

  @Path("/order/{key}/comments")
  @HEAD
  public Response headOrderComments();

  @Path("/order/{key}/comments")
  @DELETE
  public Response deleteOrderComments();

  //
  // Order lines

  @Path("/order/{key}/lines")
  @GET
  public Response getOrderLines(@PathParam("key") final String orderKey);

  @Path("/order/{key}/lines")
  @POST
  public Response postOrderLines(@PathParam("key") final String orderKey, OrderLine orderLine);

  @Path("/order/{key}/lines")
  @PUT
  public Response putOrderLines();

  @Path("/order/{key}/lines")
  @DELETE
  public Response deleteOrderLines();

  @Path("/order/{key}/lines")
  @HEAD
  public Response headOrderLines();

  //
  // Order statuses

  @Path("/order/{key}/statuses")
  @GET
  public Response getOrderStatuses(@PathParam("key") final String orderKey);

  @Path("/order/{key}/statuses")
  @POST
  public Response postOrderStatuses(@PathParam("key") final String orderKey, OrderStatus orderStatus);

  @Path("/order/{key}/statuses")
  @PUT
  public Response putOrderStatuses();

  @Path("/order/{key}/statuses")
  @DELETE
  public Response deleteOrderStatuses();

  @Path("/order/{key}/statuses")
  @HEAD
  public Response headOrderStatuses();

  //
  // Order consignments

  @Path("/order/{key}/consignments")
  @GET
  public Response getOrderConsignments(@PathParam("key") final String orderKey);

  @Path("/order/{key}/consignments")
  @POST
  public Response postOrderConsignments(@PathParam("key") final String orderKey, ResourceLink consignment);

  @Path("/order/{key}/consignments")
  @PUT
  public Response putOrderConsignments();

  @Path("/order/{key}/consignments")
  @DELETE
  public Response deleteOrderConsignments();

  @Path("/order/{key}/consignments")
  @HEAD
  public Response headOrderConsignments();

  //
  // Attachments

  /**
   * An order attachment
   * 
   * @param orderKey
   *          the order key
   * @param name
   *          the attachment name
   */
  @Path("/order/{key}/attachment/{name}")
  @GET
  public Response getOrderAttachment(@PathParam("key") final String orderKey, @PathParam("name") String name, @Context Request request);

  /**
   * An order attachment content
   * 
   * @param orderKey
   *          the order key
   * @param name
   *          the attachment name
   */
  @Path("/order/{key}/attachment/{name}/data")
  @GET
  @Produces("*/*")
  public Response getOrderAttachmentData(@PathParam("key") final String orderKey, @PathParam("name") String name);

  /**
   * An order attachment thumbnail
   * 
   * @param orderKey
   *          the order key
   * @param name
   *          the attachment name
   */
  @Path("/order/{key}/attachment/{name}/thumbnail")
  @GET
  @Produces("*/*")
  public Response getOrderAttachmentThumbnail(@PathParam("key") final String orderKey, @PathParam("name") String name);

}
