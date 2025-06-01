package co.edu.javeriana.as.personapp.application.usecase;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PhoneInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Person;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
public class PhoneUseCase implements PhoneInputPort {

	private PhoneOutputPort phonePersintence;
	private PersonOutputPort personPersintence;
	
	public PhoneUseCase(@Qualifier("phoneOutputAdapterMaria") PhoneOutputPort phonePersintence,
						@Qualifier("personOutputAdapterMaria") PersonOutputPort personPersintence) {
		this.phonePersintence = phonePersintence;
		this.personPersintence = personPersintence;
	}
	
	@Override
	public void setPersintence(PhoneOutputPort phonePersintence) {
		this.phonePersintence = phonePersintence;
	}

	@Override
	public Phone create(Phone phone) {
		log.debug("Into create on Application Domain");
		return phonePersintence.save(phone);
	}

	@Override
	public Phone edit(String number, Phone phone) throws NoExistException {
		Phone oldPhone = phonePersintence.findById(number);
		if (oldPhone != null)
			return phonePersintence.save(phone);
		throw new NoExistException(
				"The phone with number " + number + " does not exist into db, cannot be edited");
	}

	@Override
	public Boolean drop(String number) throws NoExistException {
		Phone oldPhone = phonePersintence.findById(number);
		if (oldPhone != null)
			return phonePersintence.delete(number);
		throw new NoExistException(
				"The phone with number " + number + " does not exist into db, cannot be dropped");
	}

	@Override
	public List<Phone> findAll() {
		log.info("Output: " + phonePersintence.getClass());
		return phonePersintence.find();
	}

	@Override
	public Phone findOne(String number) throws NoExistException {
		Phone oldPhone = phonePersintence.findById(number);
		if (oldPhone != null)
			return oldPhone;
		throw new NoExistException("The phone with number " + number + " does not exist into db, cannot be found");
	}

	@Override
	public Integer count() {
		return findAll().size();
	}

	@Override
	public List<Phone> findByOwner(Integer ownerIdentification) throws NoExistException {
		Person owner = personPersintence.findById(ownerIdentification);
		if (owner != null)
			return phonePersintence.findByOwnerId(ownerIdentification);
		throw new NoExistException(
				"The person with id " + ownerIdentification + " does not exist into db, cannot be getting phones");
	}
}