package co.edu.javeriana.as.personapp.mongo.adapter;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import com.mongodb.MongoWriteException;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;
import co.edu.javeriana.as.personapp.mongo.mapper.EstudiosMapperMongo;
import co.edu.javeriana.as.personapp.mongo.repository.EstudiosRepositoryMongo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter("studyOutputAdapterMongo")
public class StudyOutputAdapterMongo implements StudyOutputPort {
    
    @Autowired
    private EstudiosRepositoryMongo estudiosRepositoryMongo;
    
    @Autowired
    private EstudiosMapperMongo estudiosMapperMongo;
    
    @Override
    public Study save(Study study) {
        log.debug("Into save on Adapter MongoDB");
        try {
            EstudiosDocument persistedEstudios = estudiosRepositoryMongo.save(estudiosMapperMongo.fromDomainToAdapter(study));
            return estudiosMapperMongo.fromAdapterToDomain(persistedEstudios);
        } catch (MongoWriteException e) {
            log.warn(e.getMessage());
            return study;
        }
    }
    
    @Override
    public Boolean delete(Integer personId, Integer professionId) {
        log.debug("Into delete on Adapter MongoDB");
        // En MongoDB, necesitamos crear un ID compuesto o buscar por ambos campos
        String compositeId = personId + "-" + professionId;
        estudiosRepositoryMongo.deleteById(compositeId);
        return estudiosRepositoryMongo.findById(compositeId).isEmpty();
    }
    
    @Override
    public List<Study> find() {
        log.debug("Into findAll on Adapter MongoDB");
        return estudiosRepositoryMongo.findAll().stream()
                .map(estudiosMapperMongo::fromAdapterToDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public Study findById(Integer personId, Integer professionId) {
        log.debug("Into findById on Adapter MongoDB");
        // En MongoDB, necesitamos crear un ID compuesto o buscar por ambos campos
        String compositeId = personId + "-" + professionId;
        if (estudiosRepositoryMongo.findById(compositeId).isEmpty()) {
            return null;
        } else {
            return estudiosMapperMongo.fromAdapterToDomain(estudiosRepositoryMongo.findById(compositeId).get());
        }
    }
    
    @Override
    public List<Study> findByPersonId(Integer personId) {
        log.debug("Into findByPersonId on Adapter MongoDB");
        return estudiosRepositoryMongo.findByPrimaryPersonaId(personId).stream()
                .map(estudiosMapperMongo::fromAdapterToDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Study> findByProfessionId(Integer professionId) {
        log.debug("Into findByProfessionId on Adapter MongoDB");
        return estudiosRepositoryMongo.findByPrimaryProfesionId(professionId).stream()
                .map(estudiosMapperMongo::fromAdapterToDomain)
                .collect(Collectors.toList());
    }
}