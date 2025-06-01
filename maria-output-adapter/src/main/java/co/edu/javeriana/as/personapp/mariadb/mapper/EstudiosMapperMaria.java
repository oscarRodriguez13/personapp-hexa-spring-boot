package co.edu.javeriana.as.personapp.mariadb.mapper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntityPK;
import co.edu.javeriana.as.personapp.mariadb.entity.PersonaEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.ProfesionEntity;
import lombok.NonNull;

@Mapper
public class EstudiosMapperMaria {

    private PersonaMapperMaria personaMapperMaria;
    private ProfesionMapperMaria profesionMapperMaria;

    public void setPersonaMapperMaria(PersonaMapperMaria personaMapperMaria) {
        this.personaMapperMaria = personaMapperMaria;
    }

    public void setProfesionMapperMaria(ProfesionMapperMaria profesionMapperMaria) {
        this.profesionMapperMaria = profesionMapperMaria;
    }

    public EstudiosEntity fromDomainToAdapter(Study study) {
        EstudiosEntityPK estudioPK = new EstudiosEntityPK();
        estudioPK.setCcPer(study.getPerson().getIdentification());
        estudioPK.setIdProf(study.getProfession().getIdentification());
        
        EstudiosEntity estudio = new EstudiosEntity();
        estudio.setEstudiosPK(estudioPK);
        estudio.setFecha(validateFecha(study.getGraduationDate()));
        estudio.setUniver(validateUniver(study.getUniversityName()));
        estudio.setPersona(validatePersona(study.getPerson()));
        estudio.setProfesion(validateProfesion(study.getProfession()));
        
        return estudio;
    }

    private PersonaEntity validatePersona(@NonNull Person person) {
        if (person != null) {
            PersonaEntity entity = new PersonaEntity();
            entity.setCc(person.getIdentification());
            return entity;
        }
        return new PersonaEntity();
    }

    private ProfesionEntity validateProfesion(@NonNull Profession profession) {
        if (profession != null) {
            ProfesionEntity entity = new ProfesionEntity();
            entity.setId(profession.getIdentification());
            return entity;
        }
        return new ProfesionEntity();
    }

    private Date validateFecha(LocalDate graduationDate) {
        return graduationDate != null
                ? Date.from(graduationDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                : null;
    }

    private String validateUniver(String universityName) {
        return universityName != null ? universityName : "";
    }

    public Study fromAdapterToDomain(EstudiosEntity estudiosEntity) {
        Study study = new Study();
        study.setPerson(validatePerson(estudiosEntity.getPersona()));
        study.setProfession(validateProfession(estudiosEntity.getProfesion()));
        study.setGraduationDate(validateGraduationDate(estudiosEntity.getFecha()));
        study.setUniversityName(validateUniversityName(estudiosEntity.getUniver()));
        return study; // Corregido: retorna el objeto study en lugar de null
    }

    private Person validatePerson(PersonaEntity personaEntity) {
        if (personaEntity != null) {
            Person person = new Person();
            person.setIdentification(personaEntity.getCc());
            return person;
        }
        return new Person();
    }

    private Profession validateProfession(ProfesionEntity profesionEntity) {
        if (profesionEntity != null) {
            Profession profession = new Profession();
            profession.setIdentification(profesionEntity.getId());
            return profession;
        }
        return new Profession();
    }

    private LocalDate validateGraduationDate(Date fecha) {
        if (fecha != null) {
            // Si es java.sql.Date, convertir a java.util.Date primero
            if (fecha instanceof java.sql.Date) {
                return ((java.sql.Date) fecha).toLocalDate();
            }
            // Si es java.util.Date, usar toInstant()
            return fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return null;
    }

    private String validateUniversityName(String univer) {
        return univer != null ? univer : "";
    }
}