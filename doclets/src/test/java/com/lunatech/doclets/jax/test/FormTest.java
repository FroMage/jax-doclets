package com.lunatech.doclets.jax.test;

import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.jboss.resteasy.annotations.Form;

import com.lunatech.doclets.jax.test.demo.doc.Ingredient;

@Path("/form-{id}")
public class FormTest {

  public static class MyForm {

    /**
     * the query
     */
    @QueryParam("q")
    String q;

    /**
     * the matrix param
     */
    @MatrixParam("m")
    String m;

    /**
     * the body
     */
    String body;

    /**
     * The header
     */
    @HeaderParam("h")
    String header;

    /**
     * The path param
     */
    @PathParam("id")
    String id;

    /**
     * The form
     */
    @FormParam("f")
    String form;

    /**
     * The cookie
     */
    @CookieParam("c")
    String cookie;
    
    /**
     * An embedded @Form
     */
    @Form
    EmbeddedForm aNestedForm;

    /**
     * @param m2
     *          The second foo matrix param.
     */
    void setFoo(@MatrixParam("m2") String m2) {}
  }
  
  public static class EmbeddedForm {
    /**
     * A path parameter in the embedded @Form
     */
     @PathParam("embedded-path-param")
     String aString;
     
     /**
      * A form parameter in the embedded @Form
      */
      @FormParam("embedded-form-param")
      String anotherString;
      
      /**
       * An @Form nested inside the embedded @Form
       */
      @Form
      DoubleEmbeddedForm doubleEmbeddedForm;
  }
  
  public static class DoubleEmbeddedForm{
      /**
       * A query parameter in the @Form that is nested within the embedded @Form 
       */
      @QueryParam("query-param-two-Forms-deep")
      Long aQueryParam;
  }

  @GET
  public void get(@Form MyForm form) {

  }
}
