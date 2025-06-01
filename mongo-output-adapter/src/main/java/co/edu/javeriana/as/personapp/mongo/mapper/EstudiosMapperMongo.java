package co.edu.javeriana.as.personapp.mongo.mapper;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument;
import lombok.NonNull;

@Mapper
public class EstudiosMapperMongo {
    
    @Autowired
    private PersonaMapperMongo personaMapperMongo;
    
    @Autowired
    private ProfesionMapperMongo profesionMapperMongo;
    
    public EstudiosDocument fromDomainToAdapter(Study study) {
        EstudiosDocument estudio = new EstudiosDocument();
        estudio.setId(validateId(study.getPerson().getIdentification(), study.getProfession().getIdentification()));
        estudio.setPrimaryPersona(validatePrimaryPersona(study.getPerson()));
        estudio.setPrimaryProfesion(validatePrimaryProfesion(study.getProfession()));
        estudio.setFecha(validateFecha(study.getGraduationDate()));
        estudio.setUniver(validateUniver(study.getUniversityName()));
        return estudio;
    }
    
    private String validateId(@NonNull Integer identificationPerson, @NonNull Integer identificationProfession) {
        return identificationPerson + "-" + identificationProfession;
    }
    
    // Crear objeto básico sin referencias cruzadas - similar a tu solución MariaDB
    private PersonaDocument validatePrimaryPersona(@NonNull Person person) {
        if (person != null) {
            PersonaDocument personaDocument = new PersonaDocument();
            personaDocument.setId(person.getIdentification());
            // No setear teléfonos ni estudios para evitar referencias circulares
            return personaDocument;
        }
        return new PersonaDocument();
    }
    
    // Crear objeto básico sin referencias cruzadas - similar a tu solución MariaDB  
    private ProfesionDocument validatePrimaryProfesion(@NonNull Profession profession) {
        if (profession != null) {
            ProfesionDocument profesionDocument = new ProfesionDocument();
            profesionDocument.setId(profession.getIdentification());
            // Solo setear el ID, no otros campos que puedan causar referencias circulares
            return profesionDocument;
        }
        return new ProfesionDocument();
    }
    
    private LocalDate validateFecha(LocalDate graduationDate) {
        return graduationDate != null ? graduationDate : null;
    }
    
    private String validateUniver(String universityName) {
        return universityName != null ? universityName : "";
    }
    
    public Study fromAdapterToDomain(EstudiosDocument estudiosDocument) {
        Study study = new Study();
        study.setPerson(validatePerson(estudiosDocument.getPrimaryPersona()));
        study.setProfession(validateProfession(estudiosDocument.getPrimaryProfesion()));
        study.setGraduationDate(validateGraduationDate(estudiosDocument.getFecha()));
        study.setUniversityName(validateUniversityName(estudiosDocument.getUniver()));
        return study;
    }
    
    // Crear objetos básicos para evitar referencias circulares - patrón MariaDB
    private Person validatePerson(PersonaDocument personaDocument) {
        if (personaDocument != null) {
            Person person = new Person();
            person.setIdentification(personaDocument.getId());
            
            // Validar que los campos no sean null antes de setearlos
            if (personaDocument.getNombre() != null) {
                person.setFirstName(personaDocument.getNombre());
            } else {
                person.setFirstName(""); // Valor por defecto
            }
            
            if (personaDocument.getApellido() != null) {
                person.setLastName(personaDocument.getApellido());
            } else {
                person.setLastName(""); // Valor por defecto
            }
            
            // Setear género y edad si están disponibles
            if (personaDocument.getGenero() != null) {
                person.setGender(validateGender(personaDocument.getGenero()));
            } else {
                person.setGender(Gender.OTHER); // Valor por defecto
            }
            
            if (personaDocument.getEdad() != null) {
                person.setAge(personaDocument.getEdad());
            }
            
            // No setear teléfonos ni estudios para evitar recursión
            return person;
        }
        return new Person();
    }
    
    // Método helper para validar género (copiado de PersonaMapperMongo)
    private Gender validateGender(String genero) {
        return "F".equals(genero) ? Gender.FEMALE : "M".equals(genero) ? Gender.MALE : Gender.OTHER;
    }
    
    private Profession validateProfession(ProfesionDocument profesionDocument) {
        if (profesionDocument != null) {
            Profession profession = new Profession();
            profession.setIdentification(profesionDocument.getId());
            
            // Validar que los campos no sean null antes de setearlos
            if (profesionDocument.getNom() != null) {
                profession.setName(profesionDocument.getNom());
            } else {
                profession.setName(""); // Valor por defecto
            }
            
            if (profesionDocument.getDes() != null) {
                profession.setDescription(profesionDocument.getDes());
            } else {
                profession.setDescription(""); // Valor por defecto
            }
            
            // No setear estudios para evitar recursión
            return profession;
        }
        return new Profession();
    }
    
    private LocalDate validateGraduationDate(LocalDate fecha) {
        return fecha != null ? fecha : null;
    }
    
    private String validateUniversityName(String univer) {
        return univer != null ? univer : "";
    }
}