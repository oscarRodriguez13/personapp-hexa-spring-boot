package co.edu.javeriana.as.personapp.mongo.adapter;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import com.mongodb.MongoWriteException;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument;
import co.edu.javeriana.as.personapp.mongo.mapper.TelefonoMapperMongo;
import co.edu.javeriana.as.personapp.mongo.repository.TelefonoRepositoryMongo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter("phoneOutputAdapterMongo")
public class PhoneOutputAdapterMongo implements PhoneOutputPort {
    
    @Autowired
    private TelefonoRepositoryMongo telefonoRepositoryMongo;
    
    @Autowired
    private TelefonoMapperMongo telefonoMapperMongo;
    
    @Override
    public Phone save(Phone phone) {
        log.debug("Into save on Adapter MongoDB");
        try {
            TelefonoDocument persistedTelefono = telefonoRepositoryMongo.save(telefonoMapperMongo.fromDomainToAdapter(phone));
            return telefonoMapperMongo.fromAdapterToDomain(persistedTelefono);
        } catch (MongoWriteException e) {
            log.warn(e.getMessage());
            return phone;
        }
    }
    
    @Override
    public Boolean delete(String number) {
        log.debug("Into delete on Adapter MongoDB");
        telefonoRepositoryMongo.deleteById(number);
        return telefonoRepositoryMongo.findById(number).isEmpty();
    }
    
    @Override
    public List<Phone> find() {
        log.debug("Into findAll on Adapter MongoDB");
        return telefonoRepositoryMongo.findAll().stream()
                .map(telefonoMapperMongo::fromAdapterToDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public Phone findById(String number) {
        log.debug("Into findById on Adapter MongoDB");
        if (telefonoRepositoryMongo.findById(number).isEmpty()) {
            return null;
        } else {
            return telefonoMapperMongo.fromAdapterToDomain(telefonoRepositoryMongo.findById(number).get());
        }
    }
    
    @Override
    public List<Phone> findByOwnerId(Integer ownerIdentification) {
        log.debug("Into findByOwnerId on Adapter MongoDB");
        return telefonoRepositoryMongo.findByPrimaryDuenioId(ownerIdentification).stream()
                .map(telefonoMapperMongo::fromAdapterToDomain)
                .collect(Collectors.toList());
    }
}
