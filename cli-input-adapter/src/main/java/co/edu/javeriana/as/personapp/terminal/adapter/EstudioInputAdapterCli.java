package co.edu.javeriana.as.personapp.terminal.adapter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.in.StudyInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PersonUseCase;
import co.edu.javeriana.as.personapp.application.usecase.ProfessionUseCase;
import co.edu.javeriana.as.personapp.application.usecase.StudyUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.terminal.mapper.EstudioMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.EstudioModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class EstudioInputAdapterCli {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Autowired
    @Qualifier("studyOutputAdapterMaria")
    private StudyOutputPort studyOutputPortMaria;
    
    @Autowired
    @Qualifier("studyOutputAdapterMongo")
    private StudyOutputPort studyOutputPortMongo;
    
    @Autowired
    @Qualifier("personOutputAdapterMaria")
    private PersonOutputPort personOutputPortMaria;
    
    @Autowired
    @Qualifier("personOutputAdapterMongo")
    private PersonOutputPort personOutputPortMongo;
    
    @Autowired
    @Qualifier("professionOutputAdapterMaria")
    private ProfessionOutputPort professionOutputPortMaria;
    
    @Autowired
    @Qualifier("professionOutputAdapterMongo")
    private ProfessionOutputPort professionOutputPortMongo;
    
    @Autowired
    private EstudioMapperCli estudioMapperCli;
    
    private StudyInputPort studyInputPort;
    private PersonInputPort personInputPort;
    private ProfessionInputPort professionInputPort;
    
    public void setStudyOutputPortInjection(String dbOption) throws InvalidOptionException {
        if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
            studyInputPort = new StudyUseCase(studyOutputPortMaria, personOutputPortMaria, professionOutputPortMaria);
            personInputPort = new PersonUseCase(personOutputPortMaria);
            professionInputPort = new ProfessionUseCase(professionOutputPortMaria);
        } else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
            studyInputPort = new StudyUseCase(studyOutputPortMongo, personOutputPortMongo, professionOutputPortMongo);
            personInputPort = new PersonUseCase(personOutputPortMongo);
            professionInputPort = new ProfessionUseCase(professionOutputPortMongo);
        } else {
            throw new InvalidOptionException("Invalid database option: " + dbOption);
        }
    }
    
    public void historial() {
        log.info("Into historial StudyEntity in Input Adapter");
        try {
            studyInputPort.findAll().stream()
                .map(study -> estudioMapperCli.fromDomainToAdapterCli(study))
                .forEach(System.out::println);
        } catch (Exception e) {
            log.error("Error al obtener historial de estudios: {}", e.getMessage());
            System.out.println("Error al obtener el historial de estudios.");
        }
    }
    
    public void crear(Scanner keyboard) {
        log.info("Into crear StudyEntity in Input Adapter");
        
        try {
            System.out.print("Cédula de la persona: ");
            String personIdStr = keyboard.nextLine().trim();
            Integer personId = Integer.parseInt(personIdStr);
            
            System.out.print("ID de la profesión: ");
            String professionIdStr = keyboard.nextLine().trim();
            Integer professionId = Integer.parseInt(professionIdStr);
            
            System.out.print("Fecha de graduación (yyyy-MM-dd): ");
            String graduationDateStr = keyboard.nextLine().trim();
            LocalDate graduationDate = parseDate(graduationDateStr);
            
            System.out.print("Nombre de la universidad: ");
            String universityName = keyboard.nextLine().trim();
            
            // Buscar la persona y la profesión
            Person person = personInputPort.findOne(personId);
            Profession profession = professionInputPort.findOne(professionId);
            
            // Crear el estudio en el dominio
            Study study = new Study(person, profession, graduationDate, universityName);
            
            // Guardar usando studyInputPort
            Study createdStudy = studyInputPort.create(study);
            
            // Convertir a modelo CLI y mostrar
            EstudioModelCli estudioModel = estudioMapperCli.fromDomainToAdapterCli(createdStudy);
            System.out.println("Estudio creado exitosamente: " + estudioModel);
            
            log.info("Estudio creado exitosamente para persona {} y profesión {}", personId, professionId);
            
        } catch (NoExistException e) {
            log.error("Entidad no encontrada: {}", e.getMessage());
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            log.error("Formato de número inválido: {}", e.getMessage());
            System.out.println("Error: Los IDs deben ser números válidos.");
        } catch (DateTimeParseException e) {
            log.error("Formato de fecha inválido: {}", e.getMessage());
            System.out.println("Error: La fecha debe tener el formato yyyy-MM-dd.");
        } catch (Exception e) {
            log.error("Error al crear estudio: {}", e.getMessage());
            System.out.println("Error al crear el estudio: " + e.getMessage());
        }
    }
    
    public void buscar(Scanner keyboard) {
        log.info("Into buscar StudyEntity in Input Adapter");
        
        try {
            System.out.print("Cédula de la persona: ");
            String personIdStr = keyboard.nextLine().trim();
            Integer personId = Integer.parseInt(personIdStr);
            
            System.out.print("ID de la profesión: ");
            String professionIdStr = keyboard.nextLine().trim();
            Integer professionId = Integer.parseInt(professionIdStr);
            
            // Buscar usando studyInputPort
            Study study = studyInputPort.findOne(personId, professionId);
            
            // Convertir a modelo CLI y mostrar
            EstudioModelCli estudioModel = estudioMapperCli.fromDomainToAdapterCli(study);
            System.out.println("Estudio encontrado: " + estudioModel);
            
            log.info("Búsqueda completada exitosamente para persona {} y profesión {}", personId, professionId);
            
        } catch (NoExistException e) {
            log.error("Estudio no encontrado: {}", e.getMessage());
            System.out.println("Error: Estudio no encontrado.");
        } catch (NumberFormatException e) {
            log.error("Formato de número inválido: {}", e.getMessage());
            System.out.println("Error: Los IDs deben ser números válidos.");
        } catch (Exception e) {
            log.error("Error al buscar estudio: {}", e.getMessage());
            System.out.println("Error al buscar el estudio: " + e.getMessage());
        }
    }
    
    public void editar(Scanner keyboard) {
        log.info("Into editar StudyEntity in Input Adapter");
        
        try {
            System.out.print("Cédula de la persona del estudio a editar: ");
            String personIdStr = keyboard.nextLine().trim();
            Integer personId = Integer.parseInt(personIdStr);
            
            System.out.print("ID de la profesión del estudio a editar: ");
            String professionIdStr = keyboard.nextLine().trim();
            Integer professionId = Integer.parseInt(professionIdStr);
            
            // Verificar que el estudio existe
            Study existingStudy = studyInputPort.findOne(personId, professionId);
            
            System.out.print("Nueva fecha de graduación (actual: " + 
                           (existingStudy.getGraduationDate() != null ? existingStudy.getGraduationDate().format(FORMATTER) : "No definida") + 
                           ") [yyyy-MM-dd]: ");
            String newGraduationDateStr = keyboard.nextLine().trim();
            LocalDate newGraduationDate = newGraduationDateStr.isEmpty() ? 
                existingStudy.getGraduationDate() : parseDate(newGraduationDateStr);
            
            System.out.print("Nuevo nombre de universidad (actual: " + 
                           (existingStudy.getUniversityName() != null ? existingStudy.getUniversityName() : "No definida") + 
                           "): ");
            String newUniversityName = keyboard.nextLine().trim();
            if (newUniversityName.isEmpty()) {
                newUniversityName = existingStudy.getUniversityName();
            }
            
            // Crear el estudio actualizado
            Study updatedStudy = new Study(existingStudy.getPerson(), existingStudy.getProfession(), 
                                         newGraduationDate, newUniversityName);
            
            // Actualizar usando studyInputPort
            Study result = studyInputPort.edit(personId, professionId, updatedStudy);
            
            // Convertir a modelo CLI y mostrar
            EstudioModelCli estudioModel = estudioMapperCli.fromDomainToAdapterCli(result);
            System.out.println("Estudio editado exitosamente: " + estudioModel);
            
            log.info("Estudio editado exitosamente para persona {} y profesión {}", personId, professionId);
            
        } catch (NoExistException e) {
            log.error("Error de existencia: {}", e.getMessage());
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            log.error("Formato de número inválido: {}", e.getMessage());
            System.out.println("Error: Los IDs deben ser números válidos.");
        } catch (DateTimeParseException e) {
            log.error("Formato de fecha inválido: {}", e.getMessage());
            System.out.println("Error: La fecha debe tener el formato yyyy-MM-dd.");
        } catch (Exception e) {
            log.error("Error al editar estudio: {}", e.getMessage());
            System.out.println("Error al editar el estudio: " + e.getMessage());
        }
    }
    
    public void eliminar(Scanner keyboard) {
        log.info("Into eliminar StudyEntity in Input Adapter");
        
        try {
            System.out.print("Cédula de la persona del estudio a eliminar: ");
            String personIdStr = keyboard.nextLine().trim();
            Integer personId = Integer.parseInt(personIdStr);
            
            System.out.print("ID de la profesión del estudio a eliminar: ");
            String professionIdStr = keyboard.nextLine().trim();
            Integer professionId = Integer.parseInt(professionIdStr);
            
            // Verificar que el estudio existe antes de mostrar confirmación
            Study existingStudy = studyInputPort.findOne(personId, professionId);
            
            // Confirmar eliminación
            System.out.print("¿Está seguro de eliminar el estudio de " + 
                           existingStudy.getPerson().getFirstName() + " " + existingStudy.getPerson().getLastName() +
                           " en " + existingStudy.getProfession().getName() + "? (s/n): ");
            String confirmation = keyboard.nextLine().trim().toLowerCase();
            
            if (confirmation.equals("s") || confirmation.equals("si") || confirmation.equals("y") || confirmation.equals("yes")) {
                // Eliminar usando studyInputPort
                Boolean deleted = studyInputPort.drop(personId, professionId);
                
                if (deleted) {
                    System.out.println("Estudio eliminado exitosamente.");
                    log.info("Estudio eliminado exitosamente para persona {} y profesión {}", personId, professionId);
                } else {
                    System.out.println("No se pudo eliminar el estudio.");
                    log.warn("No se pudo eliminar el estudio para persona {} y profesión {}", personId, professionId);
                }
            } else {
                System.out.println("Eliminación cancelada.");
                log.info("Eliminación cancelada por el usuario para persona {} y profesión {}", personId, professionId);
            }
            
        } catch (NoExistException e) {
            log.error("Estudio no encontrado: {}", e.getMessage());
            System.out.println("Error: Estudio no encontrado.");
        } catch (NumberFormatException e) {
            log.error("Formato de número inválido: {}", e.getMessage());
            System.out.println("Error: Los IDs deben ser números válidos.");
        } catch (Exception e) {
            log.error("Error al eliminar estudio: {}", e.getMessage());
            System.out.println("Error al eliminar el estudio: " + e.getMessage());
        }
    }
    
    // Método adicional para contar estudios
    public void contar() {
        log.info("Into contar StudyEntity in Input Adapter");
        try {
            Integer count = studyInputPort.count();
            System.out.println("Total de estudios registrados: " + count);
            log.info("Total de estudios: {}", count);
        } catch (Exception e) {
            log.error("Error al contar estudios: {}", e.getMessage());
            System.out.println("Error al contar los estudios.");
        }
    }
    
    // Método adicional para buscar estudios por persona
    public void buscarPorPersona(Scanner keyboard) {
        log.info("Into buscarPorPersona StudyEntity in Input Adapter");
        
        try {
            System.out.print("Cédula de la persona: ");
            String personIdStr = keyboard.nextLine().trim();
            Integer personId = Integer.parseInt(personIdStr);
            
            // Buscar estudios por persona
            List<Study> studies = studyInputPort.findByPerson(personId);
            
            if (studies.isEmpty()) {
                System.out.println("No se encontraron estudios para la persona con cédula: " + personId);
            } else {
                System.out.println("Estudios encontrados para la persona con cédula " + personId + ":");
                studies.stream()
                    .map(study -> estudioMapperCli.fromDomainToAdapterCli(study))
                    .forEach(System.out::println);
            }
            
            log.info("Búsqueda por persona completada para cédula: {}", personId);
            
        } catch (NoExistException e) {
            log.error("Persona no encontrada: {}", e.getMessage());
            System.out.println("Error: No se encontraron estudios para esa persona.");
        } catch (NumberFormatException e) {
            log.error("Formato de cédula inválido: {}", e.getMessage());
            System.out.println("Error: La cédula debe ser un número válido.");
        } catch (Exception e) {
            log.error("Error al buscar por persona: {}", e.getMessage());
            System.out.println("Error al buscar estudios por persona: " + e.getMessage());
        }
    }
    
    // Método adicional para buscar estudios por profesión
    public void buscarPorProfesion(Scanner keyboard) {
        log.info("Into buscarPorProfesion StudyEntity in Input Adapter");
        
        try {
            System.out.print("ID de la profesión: ");
            String professionIdStr = keyboard.nextLine().trim();
            Integer professionId = Integer.parseInt(professionIdStr);
            
            // Buscar estudios por profesión
            List<Study> studies = studyInputPort.findByProfession(professionId);
            
            if (studies.isEmpty()) {
                System.out.println("No se encontraron estudios para la profesión con ID: " + professionId);
            } else {
                System.out.println("Estudios encontrados para la profesión ID " + professionId + ":");
                studies.stream()
                    .map(study -> estudioMapperCli.fromDomainToAdapterCli(study))
                    .forEach(System.out::println);
            }
            
            log.info("Búsqueda por profesión completada para ID: {}", professionId);
            
        } catch (NoExistException e) {
            log.error("Profesión no encontrada: {}", e.getMessage());
            System.out.println("Error: No se encontraron estudios para esa profesión.");
        } catch (NumberFormatException e) {
            log.error("Formato de ID inválido: {}", e.getMessage());
            System.out.println("Error: El ID debe ser un número válido.");
        } catch (Exception e) {
            log.error("Error al buscar por profesión: {}", e.getMessage());
            System.out.println("Error al buscar estudios por profesión: " + e.getMessage());
        }
    }
    
    private LocalDate parseDate(String dateString) throws DateTimeParseException {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateString.trim(), FORMATTER);
    }
}