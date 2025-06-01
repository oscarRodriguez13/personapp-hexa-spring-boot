package co.edu.javeriana.as.personapp.adapter;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.in.PhoneInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PersonUseCase;
import co.edu.javeriana.as.personapp.application.usecase.PhoneUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mapper.PhoneMapperRest;
import co.edu.javeriana.as.personapp.model.request.PhoneRequest;
import co.edu.javeriana.as.personapp.model.response.PhoneResponse;
import co.edu.javeriana.as.personapp.model.response.Response;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Adapter
public class PhoneInputAdapterRest {

    @Autowired
    @Qualifier("phoneOutputAdapterMaria")
    private PhoneOutputPort phoneOutputPortMaria;

    @Autowired
    @Qualifier("phoneOutputAdapterMongo")
    private PhoneOutputPort phoneOutputPortMongo;

    @Autowired
    @Qualifier("personOutputAdapterMaria")
    private PersonOutputPort personOutputPortMaria;

    @Autowired
    @Qualifier("personOutputAdapterMongo")
    private PersonOutputPort personOutputPortMongo;

    @Autowired
    private PhoneMapperRest phoneMapperRest;

    private PhoneInputPort phoneInputPort;
    private PersonInputPort personInputPort;
    private String currentDatabase;

    private void setPhoneOutputPortInjection(String dbOption) throws InvalidOptionException {
        if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
            phoneInputPort = new PhoneUseCase(phoneOutputPortMaria, personOutputPortMaria);
            personInputPort = new PersonUseCase(personOutputPortMaria);
            currentDatabase = DatabaseOption.MARIA.toString();
        } else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
            phoneInputPort = new PhoneUseCase(phoneOutputPortMongo, personOutputPortMongo);
            personInputPort = new PersonUseCase(personOutputPortMongo);
            currentDatabase = DatabaseOption.MONGO.toString();
        } else {
            throw new InvalidOptionException("Invalid database option: " + dbOption);
        }
    }

    public List<PhoneResponse> historial(String database) {
        try {
            setPhoneOutputPortInjection(database);
            return phoneInputPort.findAll().stream()
                    .map(phone -> phoneMapperRest.fromDomainToAdapterRest(phone, currentDatabase))
                    .collect(Collectors.toList());
        } catch (InvalidOptionException e) {
            log.error("Database inválida: {}", e.getMessage());
            return List.of();
        }
    }

    public ResponseEntity<?> crearPhone(PhoneRequest request, String database) {
        try {
            setPhoneOutputPortInjection(database);
            Person owner = personInputPort.findOne(request.getOwnerCc());
            Phone phone = new Phone(request.getNumber(), request.getOperator(), owner);
            Phone creado = phoneInputPort.create(phone);
            return ResponseEntity.ok(phoneMapperRest.fromDomainToAdapterRest(creado, currentDatabase));
        } catch (NoExistException e) {
            return ResponseEntity.status(404).body(new Response("404", e.getMessage(), LocalDateTime.now()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response("500", "Error creando teléfono", LocalDateTime.now()));
        }
    }

    public ResponseEntity<?> obtenerPhone(String database, String number) {
        try {
            setPhoneOutputPortInjection(database);
            Phone phone = phoneInputPort.findOne(number);
            return ResponseEntity.ok(phoneMapperRest.fromDomainToAdapterRest(phone, currentDatabase));
        } catch (NoExistException e) {
            return ResponseEntity.status(404).body(new Response("404", e.getMessage(), LocalDateTime.now()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response("500", "Error interno", LocalDateTime.now()));
        }
    }

    public ResponseEntity<?> actualizarPhone(String database, String number, PhoneRequest request) {
        try {
            setPhoneOutputPortInjection(database);
            Person owner = personInputPort.findOne(request.getOwnerCc());
            Phone actualizado = new Phone(request.getNumber(), request.getOperator(), owner);
            Phone result = phoneInputPort.edit(number, actualizado);
            return ResponseEntity.ok(phoneMapperRest.fromDomainToAdapterRest(result, currentDatabase));
        } catch (NoExistException e) {
            return ResponseEntity.status(404).body(new Response("404", e.getMessage(), LocalDateTime.now()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response("500", "Error actualizando teléfono", LocalDateTime.now()));
        }
    }

    public ResponseEntity<?> eliminarPhone(String database, String number) {
        try {
            setPhoneOutputPortInjection(database);
            phoneInputPort.drop(number);
            return ResponseEntity.ok(new Response("200", "Teléfono eliminado", LocalDateTime.now()));
        } catch (NoExistException e) {
            return ResponseEntity.status(404).body(new Response("404", e.getMessage(), LocalDateTime.now()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response("500", "Error eliminando teléfono", LocalDateTime.now()));
        }
    }
}