package com.lunatech.doclets.jax;

import com.sun.tools.doclets.formats.html.ConfigurationImpl;

public class JAXConfiguration {

  public ConfigurationImpl parentConfiguration;

  public JAXConfiguration(ConfigurationImpl conf) {
    this.parentConfiguration = conf;
  }

  public void setOptions() {
    parentConfiguration.setOptions();
  }
}
