package com.github.elgleidson.demo.test.web;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

import org.assertj.core.util.Lists;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.github.elgleidson.demo.test.domain.Person;
import com.github.elgleidson.demo.test.service.PersonService;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = PersonResource.class)
public class PersonResourceTest {
	
	private static final String BASE_URI = "/api/persons";
	private static final Long ID = 1L;
	private static final String NAME = "Agent Smith";
	private static final LocalDate BIRTHDAY = LocalDate.now().minusYears(30);
	private static final String PHONE = "1234567890";
	private static final String NIN = "AB123456C";
	
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private PersonService service;
	
	private Person getPerson() {
		return getPerson(ID, NAME, BIRTHDAY, PHONE, NIN);
	}
	
	private Person getPerson(Long id, String name, LocalDate birthday, String phone, String nin) {
		Person person = new Person().name(name).birthday(birthday).phone(phone).nin(nin);
		ReflectionTestUtils.setField(person, "id", id);
		return person;
	}
	
	private String birthDayFormat(LocalDate birthday) {
		return birthday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}
	
	private String phoneFormat(String phone) {
		return phone;
	}
	
	private String ninFormat(String nin) {
		return nin;
	}

	@Test
	public void testGetAllWithoutRegisters() throws Exception {		
		mvc.perform(get(BASE_URI).contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
			    .andExpect(jsonPath("$", is(empty())));
	}
	
	@Test
	public void testGetAllWithOneRegister() throws Exception {
		Person person = getPerson();
		doAnswer(i -> Lists.list(person)).when(service).findAll();
		
		mvc.perform(get(BASE_URI).contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
			    .andExpect(jsonPath("$", hasSize(1)))
			    .andExpect(jsonPath("$[0].id", is(ID.intValue())))
			    .andExpect(jsonPath("$[0].name", is(NAME)))
			    .andExpect(jsonPath("$[0].birthday", is(birthDayFormat(BIRTHDAY))))
			    .andExpect(jsonPath("$[0].phone", is(phoneFormat(PHONE))))
			    .andExpect(jsonPath("$[0].nin", is(ninFormat(NIN))));
	}
	
	@Test
	public void testGetAllWithTwoRegister() throws Exception {
		final Long id = ID+1;
		final String name = "Thomas Anderson";
		final LocalDate birthday = LocalDate.now().minusYears(30);
		final String phone = "9876543210";
		final String nin = "XY987654Z";
		
		Person person1 = getPerson();
		Person person2 = getPerson(id, name, birthday, phone, nin);
		doAnswer(i -> Lists.list(person1, person2)).when(service).findAll();
		
		mvc.perform(get(BASE_URI).contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
			    .andExpect(jsonPath("$", hasSize(2)))
			    .andExpect(jsonPath("$[0].id", is(ID.intValue())))
			    .andExpect(jsonPath("$[0].name", is(NAME)))
			    .andExpect(jsonPath("$[0].birthday", is(birthDayFormat(BIRTHDAY))))
			    .andExpect(jsonPath("$[0].phone", is(phoneFormat(PHONE))))
			    .andExpect(jsonPath("$[0].nin", is(ninFormat(NIN))))
			    .andExpect(jsonPath("$[1].id", is(id.intValue())))
			    .andExpect(jsonPath("$[1].name", is(name)))
			    .andExpect(jsonPath("$[1].birthday", is(birthDayFormat(birthday))))
			    .andExpect(jsonPath("$[1].phone", is(phoneFormat(phone))))
			    .andExpect(jsonPath("$[1].nin", is(ninFormat(nin))));
	}
	
	@Test
	public void testGetAllWithOneRegisterWithNullFields() throws Exception {
		Person person = getPerson();
		person.setPhone(null);
		person.setNin(null);
		doAnswer(i -> Lists.list(person)).when(service).findAll();
		
		mvc.perform(get(BASE_URI).contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
			    .andExpect(jsonPath("$", hasSize(1)))
			    .andExpect(jsonPath("$[0].id", is(ID.intValue())))
			    .andExpect(jsonPath("$[0].name", is(NAME)))
			    .andExpect(jsonPath("$[0].birthday", is(birthDayFormat(BIRTHDAY))))
			    .andExpect(jsonPath("$[0].phone", is(nullValue())))
			    .andExpect(jsonPath("$[0].nin", is(nullValue())));
	}
	
	@Test
	public void testGetByIdWithOnePersonWithThisId() throws Exception {
		Person person = getPerson();
		doAnswer(i -> Optional.of(person)).when(service).findById(Mockito.anyLong());
		
		mvc.perform(get(BASE_URI+"/{id}", ID).contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(ID.intValue())))
			    .andExpect(jsonPath("$.name", is(NAME)))
			    .andExpect(jsonPath("$.birthday", is(birthDayFormat(BIRTHDAY))))
			    .andExpect(jsonPath("$.phone", is(phoneFormat(PHONE))))
			    .andExpect(jsonPath("$.nin", is(ninFormat(NIN))));
	}
	
	@Test
	public void testGetByIdWithOnePersonWithAnotherId() throws Exception {
		Person person = getPerson();
		doAnswer(i -> {
			Long id = i.getArgument(0);
			if (Objects.equals(id, person.getId())) {
				return Optional.of(person);
			} else {
				return Optional.empty();
			}
		}).when(service).findById(Mockito.anyLong());
		
		mvc.perform(get(BASE_URI+"/{id}", (ID+1)).contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	public void testGetByIdWithOnePersonWithThisIdAndNullFields() throws Exception {
		Person person = getPerson();
		person.setPhone(null);
		person.setNin(null);
		doAnswer(i -> Optional.of(person)).when(service).findById(Mockito.anyLong());
		
		mvc.perform(get(BASE_URI+"/{id}", ID).contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(ID.intValue())))
			    .andExpect(jsonPath("$.name", is(NAME)))
			    .andExpect(jsonPath("$.birthday", is(birthDayFormat(BIRTHDAY))))
			    .andExpect(jsonPath("$.phone", is(nullValue())))
			    .andExpect(jsonPath("$.nin", is(nullValue())));
	}
	
	@Test
	public void testGetByNinWithOnePersonWithThisNin() throws Exception {
		Person person = getPerson();
		doAnswer(i -> Optional.of(person)).when(service).findByNin(Mockito.anyString());
		
		mvc.perform(get(BASE_URI+"/nin/{nin}", NIN).contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(ID.intValue())))
			    .andExpect(jsonPath("$.name", is(NAME)))
			    .andExpect(jsonPath("$.birthday", is(birthDayFormat(BIRTHDAY))))
			    .andExpect(jsonPath("$.phone", is(phoneFormat(PHONE))))
			    .andExpect(jsonPath("$.nin", is(ninFormat(NIN))));
	}
	
	@Test
	public void testGetByNinWithOnePersonWithAnotherNin() throws Exception {
		Person person = getPerson();
		doAnswer(i -> {
			String nin = i.getArgument(0);
			if (Objects.equals(nin, person.getNin())) {
				return Optional.of(person);
			} else {
				return Optional.empty();
			}
		}).when(service).findByNin(Mockito.anyString());
		
		mvc.perform(get(BASE_URI+"/nin/{nin}", "XY987654Z").contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isNotFound());
	}
	
	@Test
	public void testGetByNinWithOnePersonWithNinNull() throws Exception {
		Person person = getPerson();
		person.setNin(null);
		doAnswer(i -> {
			String nin = i.getArgument(0);
			if (Objects.equals(nin, person.getNin())) {
				return Optional.of(person);
			} else {
				return Optional.empty();
			}
		}).when(service).findByNin(Mockito.anyString());
		
		mvc.perform(get(BASE_URI+"/nin/"+NIN).contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	public void testGetByNinWithOnePersonWithThisNinAndNullFields() throws Exception {
		Person person = getPerson();
		person.setPhone(null);
		doAnswer(i -> Optional.of(person)).when(service).findByNin(Mockito.anyString());
		
		mvc.perform(get(BASE_URI+"/nin/{nin}", NIN).contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(ID.intValue())))
			    .andExpect(jsonPath("$.name", is(NAME)))
			    .andExpect(jsonPath("$.birthday", is(birthDayFormat(BIRTHDAY))))
			    .andExpect(jsonPath("$.phone", is(nullValue())))
			    .andExpect(jsonPath("$.nin", is(ninFormat(NIN))));
	}
	
	@Test
	public void testCreateAPerson() throws Exception {
		doAnswer(i -> {
			Person person = i.getArgument(0);
			ReflectionTestUtils.setField(person, "id", ID);
			return person;
		}).when(service).save(Mockito.any());
		
		String json = new JSONObject()
				.put("name", NAME)
				.put("birthday", BIRTHDAY)
				.put("phone", PHONE)
				.put("nin", NIN)
				.toString();
		
		mvc.perform(post(BASE_URI).contentType(MediaType.APPLICATION_JSON).content(json))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id", is(ID.intValue())))
				.andExpect(jsonPath("$.name", is(NAME)))
			    .andExpect(jsonPath("$.birthday", is(birthDayFormat(BIRTHDAY))))
			    .andExpect(jsonPath("$.phone", is(phoneFormat(PHONE))))
			    .andExpect(jsonPath("$.nin", is(ninFormat(NIN))));
	}
	
	@Test
	public void testCreateAPersonWithId() throws Exception {
		doAnswer(i -> {
			Person person = i.getArgument(0);
			ReflectionTestUtils.setField(person, "id", ID);
			return person;
		}).when(service).save(Mockito.any());
		
		String json = new JSONObject()
				.put("id", ID)
				.put("name", NAME)
				.put("birthday", BIRTHDAY)
				.put("phone", PHONE)
				.put("nin", NIN)
				.toString();
				
		mvc.perform(post(BASE_URI).contentType(MediaType.APPLICATION_JSON).content(json))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}
	
	@Test
	public void testCreateAPersonWithoutPhone() throws Exception {
		doAnswer(i -> {
			Person person = i.getArgument(0);
			ReflectionTestUtils.setField(person, "id", ID);
			return person;
		}).when(service).save(Mockito.any());
		
		String json = new JSONObject()
				.put("name", NAME)
				.put("birthday", BIRTHDAY)
				.put("nin", NIN)
				.toString();
		
		mvc.perform(post(BASE_URI).contentType(MediaType.APPLICATION_JSON).content(json))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id", is(ID.intValue())))
				.andExpect(jsonPath("$.name", is(NAME)))
			    .andExpect(jsonPath("$.birthday", is(birthDayFormat(BIRTHDAY))))
			    .andExpect(jsonPath("$.phone", is(nullValue())))
			    .andExpect(jsonPath("$.nin", is(ninFormat(NIN))));
	}
	
	@Test
	public void testCreateAPersonWithPhoneNull() throws Exception {
		doAnswer(i -> {
			Person person = i.getArgument(0);
			ReflectionTestUtils.setField(person, "id", ID);
			return person;
		}).when(service).save(Mockito.any());
		
		String json = new JSONObject()
				.put("name", NAME)
				.put("birthday", BIRTHDAY)
				.put("phone", null)
				.put("nin", NIN)
				.toString();
		
		mvc.perform(post(BASE_URI).contentType(MediaType.APPLICATION_JSON).content(json))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id", is(ID.intValue())))
				.andExpect(jsonPath("$.name", is(NAME)))
			    .andExpect(jsonPath("$.birthday", is(birthDayFormat(BIRTHDAY))))
			    .andExpect(jsonPath("$.phone", is(nullValue())))
			    .andExpect(jsonPath("$.nin", is(ninFormat(NIN))));
	}
	
	@Test
	public void testCreateAPersonWithoutNin() throws Exception {
		doAnswer(i -> {
			Person person = i.getArgument(0);
			ReflectionTestUtils.setField(person, "id", ID);
			return person;
		}).when(service).save(Mockito.any());
		
		String json = new JSONObject()
				.put("name", NAME)
				.put("birthday", BIRTHDAY)
				.put("phone", PHONE)
				.toString();
		
		mvc.perform(post(BASE_URI).contentType(MediaType.APPLICATION_JSON).content(json))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id", is(ID.intValue())))
				.andExpect(jsonPath("$.name", is(NAME)))
			    .andExpect(jsonPath("$.birthday", is(birthDayFormat(BIRTHDAY))))
			    .andExpect(jsonPath("$.phone", is(phoneFormat(PHONE))))
			    .andExpect(jsonPath("$.nin", is(nullValue())));
	}
	
	@Test
	public void testCreateAPersonWithNinNull() throws Exception {
		doAnswer(i -> {
			Person person = i.getArgument(0);
			ReflectionTestUtils.setField(person, "id", ID);
			return person;
		}).when(service).save(Mockito.any());
		
		String json = new JSONObject()
				.put("name", NAME)
				.put("birthday", BIRTHDAY)
				.put("phone", PHONE)
				.put("nin", null)
				.toString();
		
		mvc.perform(post(BASE_URI).contentType(MediaType.APPLICATION_JSON).content(json))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id", is(ID.intValue())))
				.andExpect(jsonPath("$.name", is(NAME)))
			    .andExpect(jsonPath("$.birthday", is(birthDayFormat(BIRTHDAY))))
			    .andExpect(jsonPath("$.phone", is(phoneFormat(PHONE))))
			    .andExpect(jsonPath("$.nin", is(nullValue())));
	}
	
	@Test
	public void testCreateAPersonWithoutName() throws Exception {
		doAnswer(i -> {
			Person person = i.getArgument(0);
			ReflectionTestUtils.setField(person, "id", ID);
			return person;
		}).when(service).save(Mockito.any());
		
		String json = new JSONObject()
				.put("birthday", BIRTHDAY)
				.put("phone", PHONE)
				.put("nin", NIN)
				.toString();
		
		mvc.perform(post(BASE_URI).contentType(MediaType.APPLICATION_JSON).content(json))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}
	
	@Test
	public void testCreateAPersonWithNameNull() throws Exception {
		doAnswer(i -> {
			Person person = i.getArgument(0);
			ReflectionTestUtils.setField(person, "id", ID);
			return person;
		}).when(service).save(Mockito.any());
		
		String json = new JSONObject()
				.put("name", null)
				.put("birthday", BIRTHDAY)
				.put("phone", PHONE)
				.put("nin", NIN)
				.toString();
		
		mvc.perform(post(BASE_URI).contentType(MediaType.APPLICATION_JSON).content(json))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}
	
	@Test
	public void testCreateAPersonWithoutBirthday() throws Exception {
		doAnswer(i -> {
			Person person = i.getArgument(0);
			ReflectionTestUtils.setField(person, "id", ID);
			return person;
		}).when(service).save(Mockito.any());
		
		String json = new JSONObject()
				.put("name", NAME)
				.put("phone", PHONE)
				.put("nin", NIN)
				.toString();
		
		mvc.perform(post(BASE_URI).contentType(MediaType.APPLICATION_JSON).content(json))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}
	
	@Test
	public void testCreateAPersonWithBirthdayNull() throws Exception {
		doAnswer(i -> {
			Person person = i.getArgument(0);
			ReflectionTestUtils.setField(person, "id", ID);
			return person;
		}).when(service).save(Mockito.any());
		
		String json = new JSONObject()
				.put("name", NAME)
				.put("birthday", null)
				.put("phone", PHONE)
				.put("nin", NIN)
				.toString();
		
		mvc.perform(post(BASE_URI).contentType(MediaType.APPLICATION_JSON).content(json))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}
	
	@Test
	public void testUpdateAnExistentPerson() throws Exception {
		Person person = getPerson();
		doAnswer(i -> Optional.of(person)).when(service).findById(Mockito.anyLong());
		doAnswer(i -> i.getArgument(0)).when(service).save(Mockito.any());
		
		final String name = "Thomas Anderson";
		final LocalDate birthday = LocalDate.now().minusYears(30);
		final String phone = "9876543210";
		final String nin = "XY987654Z";
		
		String json = new JSONObject()
				.put("name", name)
				.put("birthday", birthday)
				.put("phone", phone)
				.put("nin", nin)
				.toString();
				
		mvc.perform(put(BASE_URI+"/{id}", ID).contentType(MediaType.APPLICATION_JSON).content(json))
				.andDo(print())
				.andExpect(status().isAccepted())
				.andExpect(jsonPath("$.id", is(ID.intValue())))
				.andExpect(jsonPath("$.name", is(name)))
			    .andExpect(jsonPath("$.birthday", is(birthDayFormat(birthday))))
			    .andExpect(jsonPath("$.phone", is(phoneFormat(phone))))
			    .andExpect(jsonPath("$.nin", is(ninFormat(nin))));
	}
	
	@Test
	public void testUpdateANonExistentPerson() throws Exception {
		doAnswer(i -> Optional.empty()).when(service).findById(Mockito.anyLong());
		doAnswer(i -> i.getArgument(0)).when(service).save(Mockito.any());
		
		final String name = "Thomas Anderson";
		final LocalDate birthday = LocalDate.now().minusYears(30);
		final String phone = "9876543210";
		final String nin = "XY987654Z";
		
		String json = new JSONObject()
				.put("name", name)
				.put("birthday", birthday)
				.put("phone", phone)
				.put("nin", nin)
				.toString();
				
		mvc.perform(put(BASE_URI+"/{id}", (ID+1)).contentType(MediaType.APPLICATION_JSON).content(json))
				.andDo(print())
				.andExpect(status().isNotFound());
	}
	
	@Test
	public void testDeleteAnExistentPerson() throws Exception {
		Person person = getPerson();
		doAnswer(i -> Optional.of(person)).when(service).findById(Mockito.anyLong());
		doNothing().when(service).deleteById(Mockito.anyLong());
				
		mvc.perform(delete(BASE_URI+"/{id}", ID).contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isNoContent());
	}
	
	@Test
	public void testDeleteANonExistentPerson() throws Exception {
		doAnswer(i -> Optional.empty()).when(service).findById(Mockito.anyLong());
		doNothing().when(service).deleteById(Mockito.anyLong());
				
		mvc.perform(delete(BASE_URI+"/{id}", ID).contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isNoContent());
	}
}
