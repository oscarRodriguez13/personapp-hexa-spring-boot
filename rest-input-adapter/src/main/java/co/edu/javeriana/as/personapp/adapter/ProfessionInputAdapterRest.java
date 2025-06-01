package co.edu.javeriana.as.personapp.adapter;

import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.ProfessionUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.mapper.ProfessionMapperRest;
import co.edu.javeriana.as.personapp.model.request.ProfessionRequest;
import co.edu.javeriana.as.personapp.model.response.ProfessionResponse;
import co.edu.javeriana.as.personapp.model.response.Response;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Adapter
public class ProfessionInputAdapterRest {

    @Autowired
    @Qualifier("professionOutputAdapterMaria")
    private ProfessionOutputPort professionOutputPortMaria;

    @Autowired
    @Qualifier("professionOutputAdapterMongo")
    private ProfessionOutputPort professionOutputPortMongo;

    @Autowired
    private ProfessionMapperRest professionMapperRest;

    private ProfessionInputPort professionInputPort;
    private String currentDatabase;

    private void setProfessionOutputPortInjection(String dbOption) throws InvalidOptionException {
        if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
            professionInputPort = new ProfessionUseCase(professionOutputPortMaria);
            currentDatabase = DatabaseOption.MARIA.toString();
        } else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
            professionInputPort = new ProfessionUseCase(professionOutputPortMongo);
            currentDatabase = DatabaseOption.MONGO.toString();
        } else {
            throw new InvalidOptionException("Invalid database option: " + dbOption);
        }
    }

    public List<ProfessionResponse> historial(String database) {
        try {
            setProfessionOutputPortInjection(database);
            return professionInputPort.findAll().stream()
                    .map(prof -> professionMapperRest.fromDomainToAdapterRest(prof, currentDatabase))
                    .collect(Collectors.toList());
        } catch (InvalidOptionException e) {
            log.error("Base de datos inválida: {}", e.getMessage());
            return List.of();
        }
    }

    public ResponseEntity<?> crearProfession(ProfessionRequest request, String database) {
        try {
            setProfessionOutputPortInjection(database);
            Profession nueva = professionMapperRest.fromAdapterToDomain(request);
            Profession creada = professionInputPort.create(nueva);
            return ResponseEntity.ok(professionMapperRest.fromDomainToAdapterRest(creada, currentDatabase));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response("500", "Error creando profesión", LocalDateTime.now()));
        }
    }

    public ResponseEntity<?> obtenerProfession(String database, int id) {
        try {
            setProfessionOutputPortInjection(database);
            Profession encontrada = professionInputPort.findOne(id);
            return ResponseEntity.ok(professionMapperRest.fromDomainToAdapterRest(encontrada, currentDatabase));
        } catch (NoExistException e) {
            return ResponseEntity.status(404).body(new Response("404", e.getMessage(), LocalDateTime.now()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response("500", "Error interno", LocalDateTime.now()));
        }
    }

    public ResponseEntity<?> actualizarProfession(String database, int id, ProfessionRequest request) {
        try {
            setProfessionOutputPortInjection(database);
            Profession actualizada = professionMapperRest.fromAdapterToDomain(request);
            Profession resultado = professionInputPort.edit(id, actualizada);
            return ResponseEntity.ok(professionMapperRest.fromDomainToAdapterRest(resultado, currentDatabase));
        } catch (NoExistException e) {
            return ResponseEntity.status(404).body(new Response("404", e.getMessage(), LocalDateTime.now()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response("500", "Error actualizando profesión", LocalDateTime.now()));
        }
    }

    public ResponseEntity<?> eliminarProfession(String database, int id) {
        try {
            setProfessionOutputPortInjection(database);
            professionInputPort.drop(id);
            return ResponseEntity.ok(new Response("200", "Profesión eliminada", LocalDateTime.now()));
        } catch (NoExistException e) {
            return ResponseEntity.status(404).body(new Response("404", e.getMessage(), LocalDateTime.now()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response("500", "Error eliminando profesión", LocalDateTime.now()));
        }
    }
}