package com.lunatech.doclets.jax.jaxb.testcase;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

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

  @XmlElement
  private byte byteAlone;

  public byte getByteAlone() {
    return byteAlone;
  }

  public void setByteAlone(byte byteAlone) {
    this.byteAlone = byteAlone;
  }

  @XmlList
  @XmlElement
  private String[] stringArrayAsXsdList;

  public String[] getStringArrayAsXsdList() {
    return stringArrayAsXsdList;
  }

  public void setStringArrayAsXsdList(String[] stringArrayAsXsdList) {
    this.stringArrayAsXsdList = stringArrayAsXsdList;
  }

  @XmlElement
  private Date[] dateArray;

  public Date[] getDateArray() {
    return dateArray;
  }

  public void setDateArray(Date[] dateArray) {
    this.dateArray = dateArray;
  }

  @XmlElement
  @XmlList
  private List<Long> longListAsXsdList;

  public List<Long> getLongListAsXsdList() {
    return longListAsXsdList;
  }

  public void setLongListAsXsdList(List<Long> longListAsXsdList) {
    this.longListAsXsdList = longListAsXsdList;
  }

  @XmlElement
  private List<Integer> integerList;

  public List<Integer> getIntegerList() {
    return integerList;
  }

  public void setIntegerList(List<Integer> integerList) {
    this.integerList = integerList;
  }

  @XmlElement
  private Collection<String> stringCollection;
  
  public Collection<String> getStringCollection() {
    return stringCollection;
  }
  
  public void setStringCollection(Collection<String> stringCollection) {
    this.stringCollection = stringCollection;
  }

  public String nonTransientField;

  public transient String transientField;

  @XmlTransient
  public String xmlTransientField;
}
