package co.edu.javeriana.as.personapp.controller;

import co.edu.javeriana.as.personapp.adapter.PhoneInputAdapterRest;
import co.edu.javeriana.as.personapp.model.request.PhoneRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/phone")
public class PhoneControllerV1 {

    @Autowired
    private PhoneInputAdapterRest phoneInputAdapterRest;

    // GET: Obtener todos los teléfonos
    @GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll(@PathVariable String database) {
        log.info("GET /api/v1/phone/{}", database);
        return ResponseEntity.ok(phoneInputAdapterRest.historial(database));
    }

    // GET: Obtener un teléfono por número
    @GetMapping(path = "/{database}/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getOne(@PathVariable String database, @PathVariable String number) {
        log.info("GET /api/v1/phone/{}/{}", database, number);
        return phoneInputAdapterRest.obtenerPhone(database, number);
    }

    // POST: Crear nuevo teléfono
    @PostMapping(path = "/{database}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@PathVariable String database, @RequestBody PhoneRequest request) {
        log.info("POST /api/v1/phone/{}", database);
        return phoneInputAdapterRest.crearPhone(request, database);
    }

    // PUT: Actualizar teléfono existente
    @PutMapping(path = "/{database}/{number}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable String database, @PathVariable String number, @RequestBody PhoneRequest request) {
        log.info("PUT /api/v1/phone/{}/{}", database, number);
        return phoneInputAdapterRest.actualizarPhone(database, number, request);
    }

    // DELETE: Eliminar teléfono por número
    @DeleteMapping(path = "/{database}/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> delete(@PathVariable String database, @PathVariable String number) {
        log.info("DELETE /api/v1/phone/{}/{}", database, number);
        return phoneInputAdapterRest.eliminarPhone(database, number);
    }
}