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


}
