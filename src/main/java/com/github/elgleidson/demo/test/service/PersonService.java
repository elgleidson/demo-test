package com.github.elgleidson.demo.test.service;

import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.elgleidson.demo.test.domain.Person;
import com.github.elgleidson.demo.test.repository.PersonRepository;

@Service
public class PersonService {
	
	@Autowired
	private PersonRepository repository;
	
	@Transactional
    public Person save(@NotNull Person Person) {
        Person = repository.save(Person);
        return Person;
    }

    @Transactional
    public void deleteById(@NotNull Long id) {
        repository.deleteById(id);
    }

    public Optional<Person> findById(@NotNull Long id) {
        return repository.findById(id);
    }
    
    public Optional<Person> findByNin(@NotNull String nin) {
        return repository.findByNin(nin);
    }

    public List<Person> findAll() {
        return repository.findAll();
    }

}
