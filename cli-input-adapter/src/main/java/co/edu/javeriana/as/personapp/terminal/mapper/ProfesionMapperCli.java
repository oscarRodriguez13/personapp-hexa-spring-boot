package co.edu.javeriana.as.personapp.terminal.mapper;

import org.springframework.stereotype.Component;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.terminal.model.ProfesionModelCli;

@Component
public class ProfesionMapperCli {

    public ProfesionModelCli fromDomainToAdapterCli(Profession profession) {
        return new ProfesionModelCli(
                profession.getIdentification(),
                profession.getName(),
                profession.getDescription()
        );
    }

    public Profession fromAdapterCliToDomain(ProfesionModelCli model) {
        return new Profession(
                model.getIdentification(),
                model.getName(),
                model.getDescription(),
                null 
        );
    }
}