package com.lunatech.doclets.jax.jaxb.testcase;

import javax.xml.bind.annotation.*;

@XmlRootElement
public class ClassExample {

	@XmlElement
	private byte[] byteArray;

	public byte[] getByteArray() {
		return byteArray;
	}
	public void setByteArray(byte[] byteArray) {
		this.byteArray = byteArray;
	}

	@XmlList
	@XmlElement
	private String[] stringArray;
	public String[] getStringArray() {
    return stringArray;
  }
	public void setStringArray(String[] stringArray) {
    this.stringArray = stringArray;
  }

}
