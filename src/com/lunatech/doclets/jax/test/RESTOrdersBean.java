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
package com.lunatech.doclets.jax.test;

import java.util.Date;
import java.util.List;

import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

import com.lunatech.doclets.jax.test.interfaces.RESTOrders;

public class RESTOrdersBean implements RESTOrders {

  //
  // JAXB

  /**
   * The order type, cool as hell
   */
  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  public static class Order {

    /**
     * The name of the order
     */
    String name;

    /**
     * The order key
     */
    String key;

    /**
     * This has an overridden name
     */
    @XmlElement(name = "overridden-name")
    int overriddenName;

    @XmlTransient
    String notMe1;

    transient String notMe2;

    /**
     * Our cool attribute
     */
    @XmlAttribute
    Date attribute;

    /**
     * Overridden name attribute
     */
    @XmlAttribute(name = "attribute-2")
    long attribute2;

    /**
     * Our value
     */
    @XmlValue
    Comment comment;

    @XmlElementWrapper(name = "string-list")
    @XmlElement(name = "string")
    List<String> stringList;

    @XmlElementWrapper(name = "object-list")
    @XmlElement(name = "object")
    List objectList;

    OrderStatusType status;

    @XmlElementWrapper(name = "tag-list")
    @XmlElement(name = "tag")
    List<Tag> tagList;

    @XmlIDREF
    Attachment attachment;
  }

  static class Tag {

    @XmlValue
    String name;
  }

  enum OrderStatusType {
    Open, Closed;
  }

  @XmlRootElement
  public static class Comment {}

  @XmlRootElement
  public static class Attachment {

    @XmlElement
    @XmlID
    String id;
  }

  @XmlRootElement
  public static class OrderLine {}

  @XmlRootElement
  public static class ResourceLink {}

  @XmlRootElement
  public static class OrderStatus {}

  //
  // REST services

  // orders

  public Response getOrders(final String creatorKey, final String filter) {
    return null;
  }

  public Response postOrders(final Order newOrder, final UriInfo uriInfo) {
    return null;
  }

  public Response putOrders() {
    return null;
  }

  public Response headOrders() {
    return null;
  }

  public Response deleteOrders() {
    return null;
  }

  // order

  public Order getOrder(final String orderKey) {
    return null;
  }

  public Response putOrder(final String orderKey, final Order newOrder, final UriInfo uriInfo) {
    return null;
  }

  public Response postOrder() {
    return null;
  }

  public Response deleteOrder() {
    return null;
  }

  public Response headOrder() {
    return null;
  }

  // order comments

  public Response getOrderComments(final String orderKey) {
    return null;
  }

  public Response postOrderComments(final String orderKey, final Comment comment) {
    return null;
  }

  public Response putOrderComments() {
    return null;
  }

  public Response headOrderComments() {
    return null;
  }

  public Response deleteOrderComments() {
    return null;
  }

  // order lines
  public Response getOrderLines(final String orderKey) {
    return null;
  }

  public Response postOrderLines(final String orderKey, final OrderLine orderLine) {
    return null;
  }

  public Response putOrderLines() {
    return null;
  }

  public Response deleteOrderLines() {
    return null;
  }

  public Response headOrderLines() {
    return null;
  }

  // order statuses
  public Response getOrderStatuses(final String orderKey) {
    return null;
  }

  public Response postOrderStatuses(final String orderKey, final OrderStatus orderStatus) {
    return null;
  }

  public Response putOrderStatuses() {
    return null;
  }

  public Response deleteOrderStatuses() {
    return null;
  }

  public Response headOrderStatuses() {
    return null;
  }

  // order consignments
  public Response getOrderConsignments(final String orderKey) {
    return null;
  }

  public Response postOrderConsignments(final String orderKey, final ResourceLink newConsignment) {
    return null;
  }

  public Response putOrderConsignments() {
    return null;
  }

  public Response deleteOrderConsignments() {
    return null;
  }

  public Response headOrderConsignments() {
    return null;
  }

  //
  // Attachments

  public Response getOrderAttachment(final String orderKey, final String filename, final Request request) {
    return null;
  }

  public Response getOrderAttachmentData(final String orderKey, final String filename) {
    return null;
  }

  public Response getOrderAttachmentThumbnail(final String orderKey, final String filename) {
    return null;
  }

  //
  // Various

  public void putSomething(String content) {}

}
