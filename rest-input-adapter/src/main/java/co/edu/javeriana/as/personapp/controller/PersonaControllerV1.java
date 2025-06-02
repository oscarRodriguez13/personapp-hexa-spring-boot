package co.edu.javeriana.as.personapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.as.personapp.adapter.PersonaInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import co.edu.javeriana.as.personapp.model.response.PhoneResponse;
import co.edu.javeriana.as.personapp.model.response.StudyResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/persona")
public class PersonaControllerV1 {
	
	@Autowired
	private PersonaInputAdapterRest personaInputAdapterRest;
	
	@ResponseBody
	@GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<PersonaResponse> personas(@PathVariable String database) {
		log.info("Into personas REST API");
		return personaInputAdapterRest.historial(database.toUpperCase());
	}
	
	@ResponseBody
	@PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public PersonaResponse crearPersona(@RequestBody PersonaRequest request) {
		log.info("Into crearPersona REST API");
		return personaInputAdapterRest.crearPersona(request);
	}
	
	@ResponseBody
	@GetMapping(path = "/{database}/{identification}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PersonaResponse> obtenerPersona(@PathVariable String database, @PathVariable Integer identification) {
		log.info("Into obtenerPersona REST API");
		try {
			PersonaResponse persona = personaInputAdapterRest.obtenerPersona(database.toUpperCase(), identification);
			return ResponseEntity.ok(persona);
		} catch (NoExistException e) {
			log.warn("Persona no encontrada: " + e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}
	
	@ResponseBody
	@PutMapping(path = "/{database}/{identification}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PersonaResponse> editarPersona(@PathVariable String database, @PathVariable Integer identification, @RequestBody PersonaRequest request) {
		log.info("Into editarPersona REST API");
		try {
			// Asegurar que el database del request coincida con el del path
			request.setDatabase(database.toUpperCase());
			PersonaResponse persona = personaInputAdapterRest.editarPersona(identification, request);
			return ResponseEntity.ok(persona);
		} catch (NoExistException e) {
			log.warn("Persona no encontrada para editar: " + e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}
	
	@ResponseBody
	@DeleteMapping(path = "/{database}/{identification}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PersonaResponse> eliminarPersona(@PathVariable String database, @PathVariable Integer identification) {
		log.info("Into eliminarPersona REST API");
		try {
			Boolean eliminado = personaInputAdapterRest.eliminarPersona(database.toUpperCase(), identification);
			if (eliminado) {
				PersonaResponse response = new PersonaResponse(identification.toString(), "", "", "", "", database.toUpperCase(), "DELETED");
				return ResponseEntity.ok(response);
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (NoExistException e) {
			log.warn("Persona no encontrada para eliminar: " + e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}
	
	@ResponseBody
	@GetMapping(path = "/{database}/count", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Integer> contarPersonas(@PathVariable String database) {
		log.info("Into contarPersonas REST API");
		Integer count = personaInputAdapterRest.contarPersonas(database.toUpperCase());
		return ResponseEntity.ok(count);
	}
	
	@ResponseBody
	@GetMapping(path = "/{database}/{identification}/phones", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PhoneResponse>> obtenerTelefonos(@PathVariable String database, @PathVariable Integer identification) {
		log.info("Into obtenerTelefonos REST API");
		try {
			List<PhoneResponse> telefonos = personaInputAdapterRest.obtenerTelefonos(database.toUpperCase(), identification);
			return ResponseEntity.ok(telefonos);
		} catch (NoExistException e) {
			log.warn("Persona no encontrada para obtener teléfonos: " + e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}
	
	@ResponseBody
	@GetMapping(path = "/{database}/{identification}/studies", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<StudyResponse>> obtenerEstudios(@PathVariable String database, @PathVariable Integer identification) {
		log.info("Into obtenerEstudios REST API");
		try {
			List<StudyResponse> estudios = personaInputAdapterRest.obtenerEstudios(database.toUpperCase(), identification);
			return ResponseEntity.ok(estudios);
		} catch (NoExistException e) {
			log.warn("Persona no encontrada para obtener estudios: " + e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}
}