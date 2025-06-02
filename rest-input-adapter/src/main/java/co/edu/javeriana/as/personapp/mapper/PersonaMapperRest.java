package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;

@Mapper
public class PersonaMapperRest {
	
	public PersonaResponse fromDomainToAdapterRestMaria(Person person) {
		return fromDomainToAdapterRest(person, "MariaDB");
	}
	
	public PersonaResponse fromDomainToAdapterRestMongo(Person person) {
		return fromDomainToAdapterRest(person, "MongoDB");
	}
	
	public PersonaResponse fromDomainToAdapterRest(Person person, String database) {
		return new PersonaResponse(
				person.getIdentification()+"", 
				person.getFirstName(), 
				person.getLastName(), 
				person.getAge()+"", 
				person.getGender().toString(), 
				database,
				"OK");
	}
	
	public Person fromAdapterToDomain(PersonaRequest request) {
		Person person = new Person();
		
		// Mapear identificación
		if (request.getDni() != null && !request.getDni().isEmpty()) {
			try {
				person.setIdentification(Integer.parseInt(request.getDni()));
			} catch (NumberFormatException e) {
				// Manejar error de conversión
				person.setIdentification(0);
			}
		}
		
		// Mapear nombres
		person.setFirstName(request.getFirstName() != null ? request.getFirstName() : "");
		person.setLastName(request.getLastName() != null ? request.getLastName() : "");
		
		// Mapear edad
		if (request.getAge() != null && !request.getAge().isEmpty()) {
			try {
				person.setAge(Integer.parseInt(request.getAge()));
			} catch (NumberFormatException e) {
				// Manejar error de conversión
				person.setAge(0);
			}
		}
		
		// Mapear género
		if (request.getGender() != null && !request.getGender().isEmpty()) {
			try {
				person.setGender(Gender.valueOf(request.getGender().toUpperCase()));
			} catch (IllegalArgumentException e) {
				// Valor por defecto si no es válido
				person.setGender(Gender.OTHER);
			}
		} else {
			person.setGender(Gender.OTHER);
		}
		
		return person;
	}
}