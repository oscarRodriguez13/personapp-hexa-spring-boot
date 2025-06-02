package co.edu.javeriana.as.personapp.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PersonUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mapper.PersonaMapperRest;
import co.edu.javeriana.as.personapp.mapper.PhoneMapperRest;
import co.edu.javeriana.as.personapp.mapper.StudyMapperRest;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import co.edu.javeriana.as.personapp.model.response.PhoneResponse;
import co.edu.javeriana.as.personapp.model.response.StudyResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class PersonaInputAdapterRest {
	
	@Autowired
	@Qualifier("personOutputAdapterMaria")
	private PersonOutputPort personOutputPortMaria;
	
	@Autowired
	@Qualifier("personOutputAdapterMongo")
	private PersonOutputPort personOutputPortMongo;
	
	@Autowired
	private PersonaMapperRest personaMapperRest;
	
	@Autowired
	private PhoneMapperRest phoneMapperRest;
	
	@Autowired
	private StudyMapperRest studyMapperRest;
	
	PersonInputPort personInputPort;
	
	private String setPersonOutputPortInjection(String dbOption) throws InvalidOptionException {
		if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
			personInputPort = new PersonUseCase(personOutputPortMaria);
			return DatabaseOption.MARIA.toString();
		} else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
			personInputPort = new PersonUseCase(personOutputPortMongo);
			return DatabaseOption.MONGO.toString();
		} else {
			throw new InvalidOptionException("Invalid database option: " + dbOption);
		}
	}
	
	public List<PersonaResponse> historial(String database) {
		log.info("Into historial PersonaEntity in Input Adapter");
		try {
			if(setPersonOutputPortInjection(database).equalsIgnoreCase(DatabaseOption.MARIA.toString())){
				return personInputPort.findAll().stream().map(personaMapperRest::fromDomainToAdapterRestMaria)
						.collect(Collectors.toList());
			}else {
				return personInputPort.findAll().stream().map(personaMapperRest::fromDomainToAdapterRestMongo)
						.collect(Collectors.toList());
			}
		} catch (InvalidOptionException e) {
			log.warn(e.getMessage());
			return new ArrayList<PersonaResponse>();
		}
	}
	
	public PersonaResponse crearPersona(PersonaRequest request) {
		log.info("Into crearPersona in Input Adapter");
		try {
			String dbType = setPersonOutputPortInjection(request.getDatabase());
			Person person = personInputPort.create(personaMapperRest.fromAdapterToDomain(request));
			
			if(dbType.equalsIgnoreCase(DatabaseOption.MARIA.toString())){
				return personaMapperRest.fromDomainToAdapterRestMaria(person);
			} else {
				return personaMapperRest.fromDomainToAdapterRestMongo(person);
			}
		} catch (InvalidOptionException e) {
			log.warn(e.getMessage());
			return new PersonaResponse("", "", "", "", "", request.getDatabase(), "ERROR");
		}
	}
	
	public PersonaResponse obtenerPersona(String database, Integer identification) throws NoExistException {
		log.info("Into obtenerPersona in Input Adapter");
		try {
			String dbType = setPersonOutputPortInjection(database);
			Person person = personInputPort.findOne(identification);
			
			if(dbType.equalsIgnoreCase(DatabaseOption.MARIA.toString())){
				return personaMapperRest.fromDomainToAdapterRestMaria(person);
			} else {
				return personaMapperRest.fromDomainToAdapterRestMongo(person);
			}
		} catch (InvalidOptionException e) {
			log.warn(e.getMessage());
			throw new NoExistException("Database option invalid: " + database);
		}
	}
	
	public PersonaResponse editarPersona(Integer identification, PersonaRequest request) throws NoExistException {
		log.info("Into editarPersona in Input Adapter");
		try {
			String dbType = setPersonOutputPortInjection(request.getDatabase());
			Person person = personInputPort.edit(identification, personaMapperRest.fromAdapterToDomain(request));
			
			if(dbType.equalsIgnoreCase(DatabaseOption.MARIA.toString())){
				return personaMapperRest.fromDomainToAdapterRestMaria(person);
			} else {
				return personaMapperRest.fromDomainToAdapterRestMongo(person);
			}
		} catch (InvalidOptionException e) {
			log.warn(e.getMessage());
			throw new NoExistException("Database option invalid: " + request.getDatabase());
		}
	}
	
	public Boolean eliminarPersona(String database, Integer identification) throws NoExistException {
		log.info("Into eliminarPersona in Input Adapter");
		try {
			setPersonOutputPortInjection(database);
			return personInputPort.drop(identification);
		} catch (InvalidOptionException e) {
			log.warn(e.getMessage());
			throw new NoExistException("Database option invalid: " + database);
		}
	}
	
	public Integer contarPersonas(String database) {
		log.info("Into contarPersonas in Input Adapter");
		try {
			setPersonOutputPortInjection(database);
			return personInputPort.count();
		} catch (InvalidOptionException e) {
			log.warn(e.getMessage());
			return 0;
		}
	}
	
	public List<PhoneResponse> obtenerTelefonos(String database, Integer identification) throws NoExistException {
		log.info("Into obtenerTelefonos in Input Adapter");
		try {
			String dbType = setPersonOutputPortInjection(database);
			List<Phone> phones = personInputPort.getPhones(identification);
			
			if(dbType.equalsIgnoreCase(DatabaseOption.MARIA.toString())){
				return phones.stream().map(phoneMapperRest::fromDomainToAdapterRestMaria)
						.collect(Collectors.toList());
			} else {
				return phones.stream().map(phoneMapperRest::fromDomainToAdapterRestMongo)
						.collect(Collectors.toList());
			}
		} catch (InvalidOptionException e) {
			log.warn(e.getMessage());
			throw new NoExistException("Database option invalid: " + database);
		}
	}
	
	public List<StudyResponse> obtenerEstudios(String database, Integer identification) throws NoExistException {
		log.info("Into obtenerEstudios in Input Adapter");
		try {
			String dbType = setPersonOutputPortInjection(database);
			List<Study> studies = personInputPort.getStudies(identification);
			
			if(dbType.equalsIgnoreCase(DatabaseOption.MARIA.toString())){
				return studies.stream().map(studyMapperRest::fromDomainToAdapterRestMaria)
						.collect(Collectors.toList());
			} else {
				return studies.stream().map(studyMapperRest::fromDomainToAdapterRestMongo)
						.collect(Collectors.toList());
			}
		} catch (InvalidOptionException e) {
			log.warn(e.getMessage());
			throw new NoExistException("Database option invalid: " + database);
		}
	}
}