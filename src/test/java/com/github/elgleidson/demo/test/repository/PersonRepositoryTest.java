package com.github.elgleidson.demo.test.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.elgleidson.demo.test.domain.Person;

@RunWith(SpringRunner.class)
@DataJpaTest
public class PersonRepositoryTest {
	
	private static final String NAME = "Agent Smith";
	private static final LocalDate BIRTHDAY = LocalDate.now().minusYears(30);
	private static final String PHONE = "1234567890";
	private static final String NIN = "AB123456C";
	
	@Autowired
    private TestEntityManager entityManager;
	
	@Autowired
	private PersonRepository repository;
	
	private Person getPerson() {
		return new Person().name(NAME).birthday(BIRTHDAY).phone(PHONE).nin(NIN);
	}
	
	@Test
	public void testFindByNinWithoutRegisters() {
		Optional<Person> findByNin = repository.findByNin(NIN);
		assertThat(findByNin).isNotNull();
		assertThat(findByNin).isNotPresent();
	}

	@Test
	public void testFindByNinWithExistingNin() {
		Person person = getPerson();
		entityManager.persist(person);
		
		Optional<Person> findByNin = repository.findByNin(NIN);
		assertThat(findByNin).isNotNull();
		assertThat(findByNin).isPresent();
		assertThat(findByNin.get().getNin()).isEqualTo(NIN);
	}
	
	@Test
	public void testFindByNinWithNonExistingNin() {
		Person person = getPerson();
		entityManager.persist(person);
		
		Optional<Person> findByNin = repository.findByNin("XY98765432Z");
		assertThat(findByNin).isNotNull();
		assertThat(findByNin).isNotPresent();
	}
	
	@Test
	public void testFindByNinWithPersonWithoutNin() {
		Person person = getPerson();
		person.setNin(null);
		entityManager.persist(person);
		
		Optional<Person> findByNin = repository.findByNin(NIN);
		assertThat(findByNin).isNotNull();
		assertThat(findByNin).isNotPresent();
	}

}
