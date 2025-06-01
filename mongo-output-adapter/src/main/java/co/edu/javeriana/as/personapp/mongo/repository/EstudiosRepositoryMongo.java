package co.edu.javeriana.as.personapp.mongo.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument;

public interface EstudiosRepositoryMongo extends MongoRepository<EstudiosDocument, String> {
    
    @Query("{ 'primaryPersona.$id' : ?0 }")
    List<EstudiosDocument> findByPrimaryPersonaId(Integer personId);
    
    @Query("{ 'primaryProfesion.$id' : ?0 }")
    List<EstudiosDocument> findByPrimaryProfesionId(Integer professionId);
}