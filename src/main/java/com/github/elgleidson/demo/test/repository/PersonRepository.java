package com.github.elgleidson.demo.test.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.elgleidson.demo.test.domain.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long>{

	Optional<Person> findByNin(String nin);
	
}
