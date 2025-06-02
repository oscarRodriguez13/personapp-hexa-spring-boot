package co.edu.javeriana.as.personapp.terminal.mapper;

import org.springframework.stereotype.Component;
import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.terminal.model.PersonaModelCli;

@Component
@Mapper
public class PersonaMapperCli {
    
    public PersonaModelCli fromDomainToAdapterCli(Person person) {
        PersonaModelCli personaModelCli = new PersonaModelCli();
        personaModelCli.setCc(person.getIdentification());
        personaModelCli.setNombre(person.getFirstName());
        personaModelCli.setApellido(person.getLastName());
        personaModelCli.setGenero(person.getGender().toString());
        personaModelCli.setEdad(person.getAge());
        return personaModelCli;
    }
    
    public Person fromAdapterCliToDomain(PersonaModelCli cliModel) {
        Gender gender = Gender.valueOf(cliModel.getGenero().toUpperCase());
        return new Person(
            cliModel.getCc(),
            cliModel.getNombre(), 
            cliModel.getApellido(),
            gender,
            cliModel.getEdad(),
			null,
			null
        );
    }
}