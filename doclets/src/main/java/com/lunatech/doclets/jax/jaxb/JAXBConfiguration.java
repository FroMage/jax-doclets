package com.lunatech.doclets.jax.jaxb;

import com.lunatech.doclets.jax.JAXConfiguration;
import com.lunatech.doclets.jax.Utils;
import com.sun.tools.doclets.formats.html.ConfigurationImpl;

import java.util.regex.Pattern;

public class JAXBConfiguration extends JAXConfiguration {

  public boolean enableJSONTypeName;

  public Pattern onlyOutputJAXBClassPackagesMatching;

  public boolean enableJaxBMethodOutput = true;
  
  public boolean enableJSONExample = true;
  
  public boolean enableXMLExample = true;

  public JSONConvention jsonConvention = JSONConvention.JETTISON_MAPPED;

  
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
    enableJaxBMethodOutput = !Utils.hasOption(options, "-disablejaxbmethodoutput");
    enableJSONTypeName = !Utils.hasOption(options, "-disablejsontypename");
    enableJSONExample = !Utils.hasOption(options, "-disablejsonexample");
    enableXMLExample = !Utils.hasOption(options, "-disablexmlexample");
    
    String jsonConvention = Utils.getOption(options, "-jsonconvention");
    if(jsonConvention == null || "jettison".equals(jsonConvention))
      this.jsonConvention = JSONConvention.JETTISON_MAPPED;
    else if("badgerfish".equals(jsonConvention))
      this.jsonConvention = JSONConvention.BADGERFISH;
    else if("mapped".equals(jsonConvention))
      this.jsonConvention = JSONConvention.MAPPED;
    else{
      parentConfiguration.root.printError("Unknown JSON convention: "+jsonConvention+" (must be one of 'jettison' (default), 'badgerfish', 'mapped')");
    }
  }
}
