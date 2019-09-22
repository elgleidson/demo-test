package com.github.elgleidson.demo.test.web;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.elgleidson.demo.test.domain.Person;
import com.github.elgleidson.demo.test.service.PersonService;

@RestController
@RequestMapping("/api/persons")
public class PersonResource {
	
	@Autowired
	private PersonService service;
	
	@GetMapping
    public ResponseEntity<List<Person>> getAll() {
        List<Person> findAll = service.findAll();
        return ResponseEntity.ok(findAll);
    }
	
	@GetMapping("/{id}")
    public ResponseEntity<Person> getById(@PathVariable("id") Long id) {
        Optional<Person> found = service.findById(id);
        if (!found.isPresent()) {
        	return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(found.get());
    }
    
	@GetMapping("/nin/{nin}")
	public ResponseEntity<Person> getByNin(@PathVariable("nin") String nin) {
        Optional<Person> found = service.findByNin(nin);
        if (!found.isPresent()) {
        	return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(found.get());
    }
	
	@PostMapping
    public ResponseEntity<Person> create(@Valid @RequestBody Person person) {
		if (Objects.nonNull(person.getId())) {
			return ResponseEntity.badRequest().build();
		}
		
		Person created = service.save(person);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);        
    }
    
	@PutMapping("/{id}")
    public ResponseEntity<Person> update(@PathVariable("id") Long id, @Valid @RequestBody Person person) {
		Optional<Person> existing = service.findById(id);
		if (!existing.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		
		Person updated = existing.map(p -> {
			p.setName(person.getName());
			p.setBirthday(person.getBirthday());
			p.setPhone(person.getPhone());
			p.setNin(person.getNin());
			return service.save(p);
		}).get();
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(updated);
    }

	@DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
