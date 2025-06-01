package co.edu.javeriana.as.personapp.mariadb.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mariadb.entity.PersonaEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.TelefonoEntity;
import lombok.NonNull;

@Mapper
public class TelefonoMapperMaria {

    private PersonaMapperMaria personaMapperMaria;

    public void setPersonaMapperMaria(PersonaMapperMaria personaMapperMaria) {
        this.personaMapperMaria = personaMapperMaria;
    }

    public TelefonoEntity fromDomainToAdapter(Phone phone) {
        TelefonoEntity telefonoEntity = new TelefonoEntity();
        telefonoEntity.setNum(phone.getNumber());
        telefonoEntity.setOper(phone.getCompany());
        telefonoEntity.setDuenio(validateDuenio(phone.getOwner()));
        return telefonoEntity;
    }

    private PersonaEntity validateDuenio(@NonNull Person owner) {
		if (owner != null) {
			PersonaEntity entity = new PersonaEntity();
			entity.setCc(owner.getIdentification());
			return entity;
		}
		return new PersonaEntity();
	}


    public Phone fromAdapterToDomain(TelefonoEntity telefonoEntity) {
        Phone phone = new Phone();
        phone.setNumber(telefonoEntity.getNum());
        phone.setCompany(telefonoEntity.getOper());
        phone.setOwner(validateOwner(telefonoEntity.getDuenio()));
        return phone;
    }

    private Person validateOwner(PersonaEntity duenio) {
		if (duenio != null) {
			Person person = new Person();
			person.setIdentification(duenio.getCc());
			return person;
		}
		return new Person();
	}

}
