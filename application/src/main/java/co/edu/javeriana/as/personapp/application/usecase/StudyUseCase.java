package co.edu.javeriana.as.personapp.application.usecase;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.StudyInputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
public class StudyUseCase implements StudyInputPort {

	private StudyOutputPort studyPersintence;
	private PersonOutputPort personPersintence;
	private ProfessionOutputPort professionPersintence;
	
	public StudyUseCase(@Qualifier("studyOutputAdapterMaria") StudyOutputPort studyPersintence,
						@Qualifier("personOutputAdapterMaria") PersonOutputPort personPersintence,
						@Qualifier("professionOutputAdapterMaria") ProfessionOutputPort professionPersintence) {
		this.studyPersintence = studyPersintence;
		this.personPersintence = personPersintence;
		this.professionPersintence = professionPersintence;
	}
	
	@Override
	public void setPersintence(StudyOutputPort studyPersintence) {
		this.studyPersintence = studyPersintence;
	}

	@Override
	public Study create(Study study) {
		log.debug("Into create on Application Domain");
		return studyPersintence.save(study);
	}

	@Override
	public Study edit(Integer personId, Integer professionId, Study study) throws NoExistException {
		Study oldStudy = studyPersintence.findById(personId, professionId);
		if (oldStudy != null)
			return studyPersintence.save(study);
		throw new NoExistException(
				"The study with person id " + personId + " and profession id " + professionId + " does not exist into db, cannot be edited");
	}

	@Override
	public Boolean drop(Integer personId, Integer professionId) throws NoExistException {
		Study oldStudy = studyPersintence.findById(personId, professionId);
		if (oldStudy != null)
			return studyPersintence.delete(personId, professionId);
		throw new NoExistException(
				"The study with person id " + personId + " and profession id " + professionId + " does not exist into db, cannot be dropped");
	}

	@Override
	public List<Study> findAll() {
		log.info("Output: " + studyPersintence.getClass());
		return studyPersintence.find();
	}

	@Override
	public Study findOne(Integer personId, Integer professionId) throws NoExistException {
		Study oldStudy = studyPersintence.findById(personId, professionId);
		if (oldStudy != null)
			return oldStudy;
		throw new NoExistException("The study with person id " + personId + " and profession id " + professionId + " does not exist into db, cannot be found");
	}

	@Override
	public Integer count() {
		return findAll().size();
	}

	@Override
	public List<Study> findByPerson(Integer personId) throws NoExistException {
		Person person = personPersintence.findById(personId);
		if (person != null)
			return studyPersintence.findByPersonId(personId);
		throw new NoExistException(
				"The person with id " + personId + " does not exist into db, cannot be getting studies");
	}

	@Override
	public List<Study> findByProfession(Integer professionId) throws NoExistException {
		Profession profession = professionPersintence.findById(professionId);
		if (profession != null)
			return studyPersintence.findByProfessionId(professionId);
		throw new NoExistException(
				"The profession with id " + professionId + " does not exist into db, cannot be getting studies");
	}
}