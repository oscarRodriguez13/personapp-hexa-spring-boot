package co.edu.javeriana.as.personapp.application.port.in;

import java.util.List;

import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Port;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Study;

@Port
public interface StudyInputPort {

    public void setPersintence(StudyOutputPort phoneOutputPortPersintence);

    public Study create(Study study);

    public Study edit(Study study) throws NoExistException;

    public Boolean drop(Integer personidentification, Integer professionIdentification) throws NoExistException;

    public List<Study> findAll();

    public Study findOne(String number) throws NoExistException;

    public Integer count();
}
