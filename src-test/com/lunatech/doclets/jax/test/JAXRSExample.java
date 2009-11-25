package com.lunatech.doclets.jax.test;

import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * An example JAX-RS resource
 */
@Path("/example")
@Produces( { "application/xml", "application/*+xml" })
public class JAXRSExample {

  /**
   * An example resource
   */
  @XmlRootElement
  public static class JAXBExample {

    /**
     * The resource ID
     */
    @XmlID
    @XmlElement
    String id;

    /**
     * The example contents
     */
    @XmlValue
    String contents;

    /**
     * An optional attribute
     */
    @XmlAttribute
    String type;
  }

  /**
   * Gets an example resource
   * 
   * @param id
   *          the example id
   * @param type
   *          the type of resource we prefer
   * @param startIndex
   *          the start index
   * @return an example resource suitable for the given parameters
   * @HTTP 404 if there is no such example resource
   * @RequestHeader X-Example-Auth the authentication header
   * @ResponseHeader Location a pointer to the example details
   */
  @Path("{id}")
  @GET
  public JAXBExample getExample(@PathParam("id") String id, @MatrixParam("type") String type, @QueryParam("start") int startIndex) {
    return new JAXBExample();
  }
}
