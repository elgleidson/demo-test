package com.github.elgleidson.demo.test.domain;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

@Entity
public class Person {
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="sq_person")
	@SequenceGenerator(name="sq_person", sequenceName="sq_person", allocationSize=1)
	private Long id;
	
	@Column
	@NotBlank(message = "Name cannot be empty or null")
	@Length(max = 100, message = "Name cannot be longer than 100 characteres")
	private String name;
	
	@Column
	@NotNull(message = "Birthday cannot be null")
	private LocalDate birthday;
	
	@Column
	@Pattern(regexp = "^[0-9]{10}$", message = "Phone must have 10 numbers")
	private String phone;
	
	@Column
	@Pattern(regexp = "^[A-Z]{2}[0-9]{6}[A-Z]$", message = "NIN must have 2 letters + 6 numbers + 1 letter")
	private String nin;
	
	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Person name(String name) {
		setName(name);
		return this;
	}

	public LocalDate getBirthday() {
		return birthday;
	}

	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
	}
	
	public Person birthday(LocalDate birthday) {
		setBirthday(birthday);
		return this;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public Person phone(String phone) {
		setPhone(phone);
		return this;
	}

	public String getNin() {
		return nin;
	}

	public void setNin(String nin) {
		this.nin = nin;
	}
	
	public Person nin(String nin) {
		setNin(nin);
		return this;
	}
	
}
