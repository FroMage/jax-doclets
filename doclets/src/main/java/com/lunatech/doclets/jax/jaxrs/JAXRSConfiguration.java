package com.lunatech.doclets.jax.jaxrs;

import java.util.regex.Pattern;

import com.lunatech.doclets.jax.JAXConfiguration;
import com.lunatech.doclets.jax.Utils;
import com.sun.tools.doclets.formats.html.ConfigurationImpl;

public class JAXRSConfiguration extends JAXConfiguration {

  public String jaxrscontext;

  public boolean enableHTTPExample;

  public boolean enableJavaScriptExample;

  public boolean enablePojoJsonDataObjects ;

  public Pattern onlyOutputPojosMatching;

  public Pattern onlyOutputResourcesMatching;

  public JAXRSConfiguration(ConfigurationImpl conf) {
    super(conf);
  }

  public void setOptions() {
    super.setOptions();
    String[][] options = parentConfiguration.root.options();
    jaxrscontext = Utils.getOption(options, "-jaxrscontext");
    enableHTTPExample = !Utils.hasOption(options, "-disablehttpexample");
    enableJavaScriptExample = !Utils.hasOption(options, "-disablejavascriptexample");
    enablePojoJsonDataObjects = Utils.hasOption(options, "-enablepojojson");

    String jsonPattern = Utils.getOption(options, "-matchingpojonamesonly");
    if ((jsonPattern != null) && !jsonPattern.trim().isEmpty()) {
      onlyOutputPojosMatching = Pattern.compile(jsonPattern);
    }

    String resourcePattern = Utils.getOption(options, "-matchingresourcesonly");
    if ((resourcePattern != null) && !resourcePattern.trim().isEmpty()) {
      onlyOutputResourcesMatching = Pattern.compile(resourcePattern);
    }

  }

}
