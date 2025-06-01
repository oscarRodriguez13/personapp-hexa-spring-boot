package co.edu.javeriana.as.personapp.controller;

import co.edu.javeriana.as.personapp.adapter.StudyInputAdapterRest;
import co.edu.javeriana.as.personapp.model.request.StudyRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/study")
public class StudyControllerV1 {

    @Autowired
    private StudyInputAdapterRest studyInputAdapterRest;

    // GET: Obtener todos los estudios
    @GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll(@PathVariable String database) {
        log.info("GET /api/v1/study/{}", database);
        return ResponseEntity.ok(studyInputAdapterRest.historial(database));
    }

    // GET: Obtener un estudio por ID de persona y profesión
    @GetMapping(path = "/{database}/{personId}/{professionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getOne(@PathVariable String database,
                                    @PathVariable int personId,
                                    @PathVariable int professionId) {
        log.info("GET /api/v1/study/{}/{}/{}", database, personId, professionId);
        return studyInputAdapterRest.obtenerStudy(database, personId, professionId);
    }

    // POST: Crear nuevo estudio
    @PostMapping(path = "/{database}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@PathVariable String database, @RequestBody StudyRequest request) {
        log.info("POST /api/v1/study/{}", database);
        return studyInputAdapterRest.crearStudy(request, database);
    }

    // PUT: Actualizar estudio existente
    @PutMapping(path = "/{database}/{personId}/{professionId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable String database,
                                    @PathVariable int personId,
                                    @PathVariable int professionId,
                                    @RequestBody StudyRequest request) {
        log.info("PUT /api/v1/study/{}/{}/{}", database, personId, professionId);
        return studyInputAdapterRest.actualizarStudy(database, personId, professionId, request);
    }

    // DELETE: Eliminar estudio por ID de persona y profesión
    @DeleteMapping(path = "/{database}/{personId}/{professionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> delete(@PathVariable String database,
                                    @PathVariable int personId,
                                    @PathVariable int professionId) {
        log.info("DELETE /api/v1/study/{}/{}/{}", database, personId, professionId);
        return studyInputAdapterRest.eliminarStudy(database, personId, professionId);
    }
}