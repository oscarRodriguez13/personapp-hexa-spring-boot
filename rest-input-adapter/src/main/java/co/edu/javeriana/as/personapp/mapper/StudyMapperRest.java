package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.model.request.StudyRequest;
import co.edu.javeriana.as.personapp.model.response.StudyResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Mapper
public class StudyMapperRest {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public StudyResponse fromDomainToAdapterRestMaria(Study study) {
        return fromDomainToAdapterRest(study, "MariaDB");
    }

    public StudyResponse fromDomainToAdapterRestMongo(Study study) {
        return fromDomainToAdapterRest(study, "MongoDB");
    }

    public StudyResponse fromDomainToAdapterRest(Study study, String database) {
        String graduationDate = study.getGraduationDate() != null
                ? study.getGraduationDate().format(FORMATTER)
                : null;

        return new StudyResponse(
                study.getPerson().getIdentification(),
                study.getProfession().getIdentification(),
                graduationDate,
                study.getUniversityName(),
                database,
                "OK"
        );
    }

    public Study fromAdapterToDomain(StudyRequest request) {
        Person person = new Person();
        person.setIdentification(request.getPersonId());

        Profession profession = new Profession();
        profession.setIdentification(request.getProfessionId());

        LocalDate date = request.getGraduationDate() != null
                ? LocalDate.parse(request.getGraduationDate(), FORMATTER)
                : null;

        return new Study(
                person,
                profession,
                date,
                request.getUniversityName()
        );
    }
}