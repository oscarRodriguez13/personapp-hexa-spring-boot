package co.edu.javeriana.as.personapp.adapter;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.in.StudyInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PersonUseCase;
import co.edu.javeriana.as.personapp.application.usecase.ProfessionUseCase;
import co.edu.javeriana.as.personapp.application.usecase.StudyUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mapper.StudyMapperRest;
import co.edu.javeriana.as.personapp.model.request.StudyRequest;
import co.edu.javeriana.as.personapp.model.response.StudyResponse;
import co.edu.javeriana.as.personapp.model.response.Response;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Adapter
public class StudyInputAdapterRest {

    @Autowired
    @Qualifier("studyOutputAdapterMaria")
    private StudyOutputPort studyOutputPortMaria;

    @Autowired
    @Qualifier("studyOutputAdapterMongo")
    private StudyOutputPort studyOutputPortMongo;

    @Autowired
    @Qualifier("personOutputAdapterMaria")
    private PersonOutputPort personOutputPortMaria;

    @Autowired
    @Qualifier("personOutputAdapterMongo")
    private PersonOutputPort personOutputPortMongo;

    @Autowired
    @Qualifier("professionOutputAdapterMaria")
    private ProfessionOutputPort professionOutputPortMaria;

    @Autowired
    @Qualifier("professionOutputAdapterMongo")
    private ProfessionOutputPort professionOutputPortMongo;

    @Autowired
    private StudyMapperRest studyMapperRest;

    private StudyInputPort studyInputPort;
    private PersonInputPort personInputPort;
    private ProfessionInputPort professionInputPort;
    private String currentDatabase;

    private void setStudyOutputPortInjection(String dbOption) throws InvalidOptionException {
        if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
            studyInputPort = new StudyUseCase(studyOutputPortMaria, personOutputPortMaria, professionOutputPortMaria);
            personInputPort = new PersonUseCase(personOutputPortMaria);
            professionInputPort = new ProfessionUseCase(professionOutputPortMaria);
            currentDatabase = DatabaseOption.MARIA.toString();
        } else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
            studyInputPort = new StudyUseCase(studyOutputPortMongo, personOutputPortMongo, professionOutputPortMongo);
            personInputPort = new PersonUseCase(personOutputPortMongo);
            professionInputPort = new ProfessionUseCase(professionOutputPortMongo);
            currentDatabase = DatabaseOption.MONGO.toString();
        } else {
            throw new InvalidOptionException("Invalid database option: " + dbOption);
        }
    }

    public List<StudyResponse> historial(String database) {
        try {
            setStudyOutputPortInjection(database);
            return studyInputPort.findAll().stream()
                    .map(study -> studyMapperRest.fromDomainToAdapterRest(study, currentDatabase))
                    .collect(Collectors.toList());
        } catch (InvalidOptionException e) {
            log.error("Base de datos inválida: {}", e.getMessage());
            return List.of();
        }
    }

    public ResponseEntity<?> crearStudy(StudyRequest request, String database) {
        try {
            setStudyOutputPortInjection(database);

            Person person = personInputPort.findOne(request.getPersonId());
            Profession profession = professionInputPort.findOne(request.getProfessionId());

            if (person == null || profession == null) {
                throw new NoExistException("Persona o profesión no encontrada.");
            }

            LocalDate date = request.getGraduationDate() != null
                    ? LocalDate.parse(request.getGraduationDate())
                    : null;

            Study nueva = new Study(
                    person,
                    profession,
                    date,
                    request.getUniversityName()
            );

            Study creada = studyInputPort.create(nueva);
            return ResponseEntity.ok(studyMapperRest.fromDomainToAdapterRest(creada, currentDatabase));

        } catch (NoExistException e) {
            return ResponseEntity.status(404).body(new Response("404", e.getMessage(), LocalDateTime.now()));
        } catch (Exception e) {
            log.error("Error creando estudio", e);
            return ResponseEntity.status(500).body(new Response("500", "Error creando estudio", LocalDateTime.now()));
        }
    }

    public ResponseEntity<?> obtenerStudy(String database, int personId, int professionId) {
        try {
            setStudyOutputPortInjection(database);
            Study encontrada = studyInputPort.findOne(personId, professionId);
            return ResponseEntity.ok(studyMapperRest.fromDomainToAdapterRest(encontrada, currentDatabase));
        } catch (NoExistException e) {
            return ResponseEntity.status(404).body(new Response("404", e.getMessage(), LocalDateTime.now()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response("500", "Error interno", LocalDateTime.now()));
        }
    }

    public ResponseEntity<?> eliminarStudy(String database, int personId, int professionId) {
        try {
            setStudyOutputPortInjection(database);
            studyInputPort.drop(personId, professionId);
            return ResponseEntity.ok(new Response("200", "Estudio eliminado", LocalDateTime.now()));
        } catch (NoExistException e) {
            return ResponseEntity.status(404).body(new Response("404", e.getMessage(), LocalDateTime.now()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response("500", "Error eliminando estudio", LocalDateTime.now()));
        }
    }

    public ResponseEntity<?> actualizarStudy(String database, int personId, int professionId, StudyRequest request) {
        try {
            setStudyOutputPortInjection(database);

            Person person = personInputPort.findOne(request.getPersonId());
            Profession profession = professionInputPort.findOne(request.getProfessionId());

            if (person == null || profession == null) {
                throw new NoExistException("Persona o profesión no encontrada.");
            }

            LocalDate date = request.getGraduationDate() != null
                    ? LocalDate.parse(request.getGraduationDate())
                    : null;

            Study actualizado = new Study(
                    person,
                    profession,
                    date,
                    request.getUniversityName()
            );

            Study resultado = studyInputPort.edit(person.getIdentification(), profession.getIdentification(),actualizado);
            return ResponseEntity.ok(studyMapperRest.fromDomainToAdapterRest(resultado, currentDatabase));

        } catch (NoExistException e) {
            return ResponseEntity.status(404).body(new Response("404", e.getMessage(), LocalDateTime.now()));
        } catch (Exception e) {
            log.error("Error actualizando estudio", e);
            return ResponseEntity.status(500).body(new Response("500", "Error actualizando estudio", LocalDateTime.now()));
        }
    }
}