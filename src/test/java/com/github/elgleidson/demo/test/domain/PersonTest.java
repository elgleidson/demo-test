package com.github.elgleidson.demo.test.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.Before;
import org.junit.Test;

public class PersonTest {
	
	private static final String NAME = "Agent Smith";
	private static final LocalDate BIRTHDAY = LocalDate.now().minusYears(30);
	private static final String PHONE = "1234567890";
	private static final String NIN = "AB123456C";

    private Validator validator;
	
	@Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
	
	private Person getPerson() {
		return new Person().name(NAME).birthday(BIRTHDAY).phone(PHONE).nin(NIN);
	}
	
	@Test
	public void testMustBeCreateFully() {
		Set<ConstraintViolation<Person>> violations = validator.validate(getPerson());
		assertThat(violations).isEmpty();
	}
	
	@Test
	public void testNameCanNotBeNull() {		
		Set<ConstraintViolation<Person>> violations = validator.validate(getPerson().name(null));
		assertThat(violations).extracting(ConstraintViolation::getMessage).allMatch(message -> message.equals("Name cannot be empty or null"));
	}
	
	@Test
	public void testNameCanNotBeEmpty() {	
		Set<ConstraintViolation<Person>> violations = validator.validate(getPerson().name(""));
		assertThat(violations).extracting(ConstraintViolation::getMessage).allMatch(message -> message.equals("Name cannot be empty or null"));
	}
	
	@Test
	public void testNameCanNotBeOnlySpaces() {
		Set<ConstraintViolation<Person>> violations = validator.validate(getPerson().name("      "));
		assertThat(violations).extracting(ConstraintViolation::getMessage).allMatch(message -> message.equals("Name cannot be empty or null"));
	}
	
	@Test
	public void testNameCanNotHaveMoreThan100Chars() {
		Set<ConstraintViolation<Person>> violations = validator.validate(getPerson().name(RandomString.make(101)));
		assertThat(violations).extracting(ConstraintViolation::getMessage).allMatch(message -> message.equals("Name cannot be longer than 100 characteres"));
	}
	
	@Test
	public void testBirthdayCanNotBeNull() {
		Set<ConstraintViolation<Person>> violations = validator.validate(getPerson().birthday(null));
		assertThat(violations).extracting(ConstraintViolation::getMessage).allMatch(message -> message.equals("Birthday cannot be null"));
	}
	
	@Test
	public void testPhoneCanBeNull() {
		Set<ConstraintViolation<Person>> violations = validator.validate(getPerson().phone(null));
		assertThat(violations).isEmpty();
	}
	
	@Test
	public void testPhoneCanNotBeEmpty() {
		Set<ConstraintViolation<Person>> violations = validator.validate(getPerson().phone(""));
		assertThat(violations).extracting(ConstraintViolation::getMessage).allMatch(message -> message.equals("Phone must have 10 numbers"));
	}
	
	@Test
	public void testPhoneCanNotHaveLessThan10Numbers() {
		Set<ConstraintViolation<Person>> violations = validator.validate(getPerson().phone("1234"));
		assertThat(violations).extracting(ConstraintViolation::getMessage).allMatch(message -> message.equals("Phone must have 10 numbers"));
	}
	
	@Test
	public void testPhoneCanNotHaveMoreThan10Numbers() {
		Set<ConstraintViolation<Person>> violations = validator.validate(getPerson().phone("1234567890123456"));
		assertThat(violations).extracting(ConstraintViolation::getMessage).allMatch(message -> message.equals("Phone must have 10 numbers"));
	}
	
	@Test
	public void testPhoneCanNotBe10Spaces() {
		Set<ConstraintViolation<Person>> violations = validator.validate(getPerson().phone("          "));
		assertThat(violations).extracting(ConstraintViolation::getMessage).allMatch(message -> message.equals("Phone must have 10 numbers"));
	}
	
	@Test
	public void testPhoneCanNotHaveLetters() {
		Set<ConstraintViolation<Person>> violations = validator.validate(getPerson().phone("ASDFGQWERT"));
		assertThat(violations).extracting(ConstraintViolation::getMessage).allMatch(message -> message.equals("Phone must have 10 numbers"));
	}
	
	@Test
	public void testPhoneCanNotHaveSpaces() {
		Set<ConstraintViolation<Person>> violations = validator.validate(getPerson().phone("20 1234 5678"));
		assertThat(violations).extracting(ConstraintViolation::getMessage).allMatch(message -> message.equals("Phone must have 10 numbers"));
	}
	
	@Test
	public void testPhoneCanNotHaveDashes() {
		Set<ConstraintViolation<Person>> violations = validator.validate(getPerson().phone("20 1234-5678"));
		assertThat(violations).extracting(ConstraintViolation::getMessage).allMatch(message -> message.equals("Phone must have 10 numbers"));
	}
	
	@Test
	public void testNinCanBeNull() {
		Set<ConstraintViolation<Person>> violations = validator.validate(getPerson().nin(null));
		assertThat(violations).isEmpty();
	}
	
	@Test
	public void testNinCanNotBeEmpty() {
		Set<ConstraintViolation<Person>> violations = validator.validate(getPerson().nin(""));
		assertThat(violations).extracting(ConstraintViolation::getMessage).allMatch(message -> message.equals("NIN must have 2 letters + 6 numbers + 1 letter"));
	}
	
	@Test
	public void testNinCanNotHaveLessThan9Chars() {
		Set<ConstraintViolation<Person>> violations = validator.validate(getPerson().nin("AB123"));
		assertThat(violations).extracting(ConstraintViolation::getMessage).allMatch(message -> message.equals("NIN must have 2 letters + 6 numbers + 1 letter"));
	}
	
	@Test
	public void testNinCanNotHaveMoreThan9Chars() {
		Set<ConstraintViolation<Person>> violations = validator.validate(getPerson().nin("AB123456CD"));
		assertThat(violations).extracting(ConstraintViolation::getMessage).allMatch(message -> message.equals("NIN must have 2 letters + 6 numbers + 1 letter"));
	}
	
	@Test
	public void testNinCanNotBe9Spaces() {
		Set<ConstraintViolation<Person>> violations = validator.validate(getPerson().nin("         "));
		assertThat(violations).extracting(ConstraintViolation::getMessage).allMatch(message -> message.equals("NIN must have 2 letters + 6 numbers + 1 letter"));
	}
	
	@Test
	public void testNinCanNotHaveSpaces() {
		Set<ConstraintViolation<Person>> violations = validator.validate(getPerson().nin("AB 12 34 56 C"));
		assertThat(violations).extracting(ConstraintViolation::getMessage).allMatch(message -> message.equals("NIN must have 2 letters + 6 numbers + 1 letter"));
	}
	
	@Test
	public void testNinCanNotHave3LeadingLetters() {
		Set<ConstraintViolation<Person>> violations = validator.validate(getPerson().nin("ABC12345C"));
		assertThat(violations).extracting(ConstraintViolation::getMessage).allMatch(message -> message.equals("NIN must have 2 letters + 6 numbers + 1 letter"));
	}
	
	@Test
	public void testNinCanNotHave2TrailingLetters() {
		Set<ConstraintViolation<Person>> violations = validator.validate(getPerson().nin("AB12345CD"));
		assertThat(violations).extracting(ConstraintViolation::getMessage).allMatch(message -> message.equals("NIN must have 2 letters + 6 numbers + 1 letter"));
	}
	
	@Test
	public void testNinCanNotBe2Letters7Numbers() {
		Set<ConstraintViolation<Person>> violations = validator.validate(getPerson().nin("AB12345567"));
		assertThat(violations).extracting(ConstraintViolation::getMessage).allMatch(message -> message.equals("NIN must have 2 letters + 6 numbers + 1 letter"));
	}
}
