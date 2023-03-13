package com.github.whitenoise0000.springdatajdbcsplate;

import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = { "id" })
public class Employee {
	@Id
	private final long id;
	private final String name;
	private final int salary;
}
