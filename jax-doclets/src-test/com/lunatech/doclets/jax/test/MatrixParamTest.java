package com.lunatech.doclets.jax.test;

import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.xml.bind.annotation.XmlRootElement;

@Path("switches")
public class MatrixParamTest {

  @XmlRootElement
  public static class HostServerData {}

  /**
   * e.g. /switches;search=switch1
   * 
   * @param search
   *          the search result
   * @return an array of results
   */
  @GET
  public HostServerData[] search(@MatrixParam(value = "search") String search) {
    return null;
  }

}
