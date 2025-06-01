package co.edu.javeriana.as.personapp.controller;

import co.edu.javeriana.as.personapp.adapter.ProfessionInputAdapterRest;
import co.edu.javeriana.as.personapp.model.request.ProfessionRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/profession")
public class ProfessionControllerV1 {

    @Autowired
    private ProfessionInputAdapterRest professionInputAdapterRest;

    // GET: Obtener todas las profesiones
    @GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll(@PathVariable String database) {
        log.info("GET /api/v1/profession/{}", database);
        return ResponseEntity.ok(professionInputAdapterRest.historial(database));
    }

    // GET: Obtener una profesión por ID
    @GetMapping(path = "/{database}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getOne(@PathVariable String database, @PathVariable int id) {
        log.info("GET /api/v1/profession/{}/{}", database, id);
        return professionInputAdapterRest.obtenerProfession(database, id);
    }

    // POST: Crear nueva profesión
    @PostMapping(path = "/{database}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@PathVariable String database, @RequestBody ProfessionRequest request) {
        log.info("POST /api/v1/profession/{}", database);
        return professionInputAdapterRest.crearProfession(request, database);
    }

    // PUT: Actualizar profesión existente
    @PutMapping(path = "/{database}/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable String database, @PathVariable int id, @RequestBody ProfessionRequest request) {
        log.info("PUT /api/v1/profession/{}/{}", database, id);
        return professionInputAdapterRest.actualizarProfession(database, id, request);
    }

    // DELETE: Eliminar profesión por ID
    @DeleteMapping(path = "/{database}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> delete(@PathVariable String database, @PathVariable int id) {
        log.info("DELETE /api/v1/profession/{}/{}", database, id);
        return professionInputAdapterRest.eliminarProfession(database, id);
    }
}