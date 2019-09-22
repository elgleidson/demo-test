package com.github.elgleidson.demo.test.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.github.elgleidson.demo.test.domain.Person;
import com.github.elgleidson.demo.test.repository.PersonRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PersonServiceTest {
	
	private static final Long ID = 1L;
	private static final String NAME = "Agent Smith";
	private static final LocalDate BIRTHDAY = LocalDate.now().minusYears(30);
	private static final String PHONE = "1234567890";
	private static final String NIN = "AB123456C";
	
	@Autowired
	private PersonService service;
	
	@MockBean
	private PersonRepository repository;
	
	private Person getPerson() {
		Person person = new Person().name(NAME).birthday(BIRTHDAY).phone(PHONE).nin(NIN);
		ReflectionTestUtils.setField(person, "id", ID);
		return person;
	}

	@Test
	public void testFindAllWithoutRegisters() {
		List<Person> findAll = service.findAll();
		assertThat(findAll).isEmpty();
	}

	@Test
	public void testFindAllWithOneRegister() {
		Person person = getPerson();
		doAnswer(i -> Lists.list(person)).when(repository).findAll();
		
		List<Person> findAll = service.findAll();
		assertThat(findAll).isNotEmpty();
		assertThat(findAll).hasSize(1);
		assertThat(findAll).containsOnly(person);
	}
	
	@Test
	public void testFindAllWithSomeRegisters() {
		Person person1 = getPerson();
		Person person2 = getPerson();
		Person person3 = getPerson();
		doAnswer(i -> Lists.list(person1, person2, person3)).when(repository).findAll();
		
		List<Person> findAll = service.findAll();
		assertThat(findAll).isNotEmpty();
		assertThat(findAll).hasSize(3);
		assertThat(findAll).containsExactlyInAnyOrder(person1, person2, person3);
	}
	
	@Test
	public void testFindByIdWithoutRegisters() {
		Optional<Person> findById = service.findById(ID);
		assertThat(findById).isNotPresent();
	}

	@Test
	public void testFindByIdWithOnePersonWithThisId() {
		Person person = getPerson();
		doAnswer(i -> Optional.of(person)).when(repository).findById(ID);
		
		Optional<Person> findById = service.findById(ID);
		assertThat(findById).isPresent();
		assertThat(findById.get().getId()).isEqualTo(ID);
	}
	
	@Test
	public void testFindByIdWithOnePersonWithAnotherId() {
		Person person = getPerson();
		
		doAnswer(i -> {
			Long id = i.getArgument(0);
			if (Objects.equals(id, person.getId())) {
				return Optional.of(person);
			} else {
				return Optional.empty();
			}			
		}).when(repository).findById(Mockito.anyLong());
		
		Optional<Person> findById = service.findById(ID+1);
		assertThat(findById).isNotPresent();
	}
	
	@Test
	public void testFindByNinWithoutRegisters() {
		Optional<Person> findById = service.findByNin(NIN);
		assertThat(findById).isNotPresent();
	}

	@Test
	public void testFindByNinWithOnePersonWithThisNin() {
		Person person = getPerson();
		doAnswer(i -> Optional.of(person)).when(repository).findByNin(NIN);
		
		Optional<Person> findById = service.findByNin(NIN);
		assertThat(findById).isPresent();
		assertThat(findById.get().getNin()).isEqualTo(NIN);
	}
	
	@Test
	public void testFindByNinWithOnePersonWithAnotherNin() {
		Person person = getPerson();
		
		doAnswer(i -> {
			String nin = i.getArgument(0);
			if (Objects.equals(nin, person.getNin())) {
				return Optional.of(person);
			} else {
				return Optional.empty();
			}			
		}).when(repository).findByNin(Mockito.anyString());
		
		Optional<Person> findById = service.findByNin("XY987654Z");
		assertThat(findById).isNotPresent();
	}
	
	@Test
	public void testFindByNinWithOnePersonWithoutNin() {
		Person person = getPerson().nin(null);
		
		doAnswer(i -> {
			String nin = i.getArgument(0);
			if (Objects.equals(nin, person.getNin())) {
				return Optional.of(person);
			} else {
				return Optional.empty();
			}			
		}).when(repository).findByNin(Mockito.anyString());
		
		Optional<Person> findById = service.findByNin(NIN);
		assertThat(findById).isNotPresent();
	}
	
	@Test
	public void testSaveNewPerson() {
		Person person = new Person().name(NAME).birthday(BIRTHDAY).phone(PHONE).nin(NIN);
		
		doAnswer(i -> {
			Person p = i.getArgument(0);
			ReflectionTestUtils.setField(p, "id", ID);
			return p;
		}).when(repository).save(Mockito.any());
		
		Person save = service.save(person);
		assertThat(save).isNotNull();
		assertThat(save.getId()).isNotNull();
	}
	
	@Test
	public void testSaveExistingPerson() {
		Person person = getPerson();		
		doReturn(person).when(repository).save(Mockito.any());
		
		Person save = service.save(person);
		assertThat(save).isNotNull();
		assertThat(save.getId()).isNotNull();
	}
	
	@Test
	public void testDeleteByIdWithOnePersonWithThisId() {
		Person person = getPerson();
		List<Person> persons = Lists.list(person);
				
		doAnswer(i -> {
			Long id = i.getArgument(0);
			persons.removeIf(p -> Objects.equals(id, p.getId()));
			return null;
		}).when(repository).deleteById(Mockito.anyLong());
		
		service.deleteById(ID);
		assertThat(persons).doesNotContain(person);
	}
	
	@Test
	public void testDeleteByIdWithOnePersonWithAnotherId() {
		Person person = getPerson();
		List<Person> persons = Lists.list(person);
				
		doAnswer(i -> {
			Long id = i.getArgument(0);
			persons.removeIf(p -> Objects.equals(id, p.getId()));
			return null;
		}).when(repository).deleteById(Mockito.anyLong());
		
		service.deleteById(ID+1);
		assertThat(persons).containsOnly(person);
	}
}
