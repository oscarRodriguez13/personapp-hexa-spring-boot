package co.edu.javeriana.as.personapp.terminal.mapper;

import org.springframework.stereotype.Component;

import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.terminal.model.TelefonoModelCli;

@Component
public class TelefonoMapperCli {

    public TelefonoModelCli fromDomainToAdapterCli(Phone phone) {
        return new TelefonoModelCli(
            phone.getNumber(),
            phone.getCompany(),
            phone.getOwner().getIdentification()
        );
    }

    public Phone fromAdapterCliToDomain(TelefonoModelCli cliModel) {
        Person owner = new Person(cliModel.getOwnerCc(), null, null, null); 
        return new Phone(cliModel.getNumber(), cliModel.getOperator(), owner);
    }
}