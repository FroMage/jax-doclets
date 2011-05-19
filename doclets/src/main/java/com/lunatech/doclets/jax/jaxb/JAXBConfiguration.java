package com.lunatech.doclets.jax.jaxb;

import com.lunatech.doclets.jax.JAXConfiguration;
import com.lunatech.doclets.jax.Utils;
import com.sun.tools.doclets.formats.html.ConfigurationImpl;

import java.util.regex.Pattern;

public class JAXBConfiguration extends JAXConfiguration {

  public boolean enableJSONTypeName;
  public Pattern onlyOutputJAXBClassPackagesMatching;
  public boolean enableJaxBMethodOutput = true;

  public JAXBConfiguration(ConfigurationImpl conf) {
    super(conf);
  }

  public void setOptions() {
    super.setOptions();
    String[][] options = parentConfiguration.root.options();
    String pattern = Utils.getOption(options, "-matchingjaxbnamesonly");
    if (pattern != null) {
      onlyOutputJAXBClassPackagesMatching = Pattern.compile(pattern);
    }
    enableJaxBMethodOutput  = !Utils.hasOption(options, "-disablejaxbmethodoutput");
    enableJSONTypeName  = !Utils.hasOption(options, "-disablejsontypename");
  }
}
