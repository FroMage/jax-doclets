package com.lunatech.doclets.jax.test.jpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Bill {
	@Id
	@GeneratedValue
	public String id;
	
	@OneToOne
	public Order order;
}
