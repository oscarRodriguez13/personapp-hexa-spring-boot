package co.edu.javeriana.as.personapp.mongo.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument;

public interface TelefonoRepositoryMongo extends MongoRepository<TelefonoDocument, String> {
    
    @Query("{ 'primaryDuenio.$id' : ?0 }")
    List<TelefonoDocument> findByPrimaryDuenioId(Integer ownerId);
}