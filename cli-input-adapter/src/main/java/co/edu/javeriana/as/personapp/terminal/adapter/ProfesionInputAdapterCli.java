package co.edu.javeriana.as.personapp.terminal.adapter;

import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.ProfessionUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.terminal.mapper.ProfesionMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.ProfesionModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class ProfesionInputAdapterCli {
    
    @Autowired
    @Qualifier("professionOutputAdapterMaria")
    private ProfessionOutputPort professionOutputPortMaria;
    
    @Autowired
    @Qualifier("professionOutputAdapterMongo")
    private ProfessionOutputPort professionOutputPortMongo;
    
    @Autowired
    private ProfesionMapperCli profesionMapperCli;
    
    private ProfessionInputPort professionInputPort;
    
    public void setProfessionOutputPortInjection(String dbOption) throws InvalidOptionException {
        if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
            professionInputPort = new ProfessionUseCase(professionOutputPortMaria);
        } else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
            professionInputPort = new ProfessionUseCase(professionOutputPortMongo);
        } else {
            throw new InvalidOptionException("Invalid database option: " + dbOption);
        }
    }
    
    public void historial() {
        log.info("Into historial ProfessionEntity in Input Adapter");
        try {
            professionInputPort.findAll().stream()
                .map(profession -> profesionMapperCli.fromDomainToAdapterCli(profession))
                .forEach(System.out::println);
        } catch (Exception e) {
            log.error("Error al obtener historial de profesiones: {}", e.getMessage());
            System.out.println("Error al obtener el historial de profesiones.");
        }
    }
    
    public void crear(Scanner keyboard) {
        log.info("Into crear ProfessionEntity in Input Adapter");
        
        try {
            System.out.print("ID de la profesión: ");
            String idStr = keyboard.nextLine().trim();
            Integer id = Integer.parseInt(idStr);
            
            System.out.print("Nombre de la profesión: ");
            String name = keyboard.nextLine().trim();
            
            System.out.print("Descripción: ");
            String description = keyboard.nextLine().trim();
            
            // Crear la profesión en el dominio
            Profession profession = new Profession(id, name, description, null);
            
            // Guardar usando professionInputPort
            Profession createdProfession = professionInputPort.create(profession);
            
            // Convertir a modelo CLI y mostrar
            ProfesionModelCli profesionModel = profesionMapperCli.fromDomainToAdapterCli(createdProfession);
            System.out.println("Profesión creada exitosamente: " + profesionModel);
            
            log.info("Profesión creada exitosamente: {}", createdProfession.getIdentification());
            
        } catch (NumberFormatException e) {
            log.error("Formato de ID inválido: {}", e.getMessage());
            System.out.println("Error: El ID debe ser un número válido.");
        } catch (Exception e) {
            log.error("Error al crear profesión: {}", e.getMessage());
            System.out.println("Error al crear la profesión: " + e.getMessage());
        }
    }
    
    public void buscar(Scanner keyboard) {
        log.info("Into buscar ProfessionEntity in Input Adapter");
        
        try {
            System.out.print("ID de la profesión a buscar: ");
            String idStr = keyboard.nextLine().trim();
            Integer id = Integer.parseInt(idStr);
            
            // Buscar usando professionInputPort
            Profession profession = professionInputPort.findOne(id);
            
            // Convertir a modelo CLI y mostrar
            ProfesionModelCli profesionModel = profesionMapperCli.fromDomainToAdapterCli(profession);
            System.out.println("Profesión encontrada: " + profesionModel);
            
            log.info("Búsqueda completada exitosamente para ID: {}", id);
            
        } catch (NoExistException e) {
            log.error("Profesión no encontrada: {}", e.getMessage());
            System.out.println("Error: Profesión no encontrada.");
        } catch (NumberFormatException e) {
            log.error("Formato de ID inválido: {}", e.getMessage());
            System.out.println("Error: El ID debe ser un número válido.");
        } catch (Exception e) {
            log.error("Error al buscar profesión: {}", e.getMessage());
            System.out.println("Error al buscar la profesión: " + e.getMessage());
        }
    }
    
    public void editar(Scanner keyboard) {
        log.info("Into editar ProfessionEntity in Input Adapter");
        
        try {
            System.out.print("ID de la profesión a editar: ");
            String idStr = keyboard.nextLine().trim();
            Integer id = Integer.parseInt(idStr);
            
            // Verificar que la profesión existe
            Profession existingProfession = professionInputPort.findOne(id);
            
            System.out.print("Nuevo nombre (actual: " + existingProfession.getName() + "): ");
            String newName = keyboard.nextLine().trim();
            if (newName.isEmpty()) {
                newName = existingProfession.getName();
            }
            
            System.out.print("Nueva descripción (actual: " + existingProfession.getDescription() + "): ");
            String newDescription = keyboard.nextLine().trim();
            if (newDescription.isEmpty()) {
                newDescription = existingProfession.getDescription();
            }
            
            // Crear la profesión actualizada
            Profession updatedProfession = new Profession(id, newName, newDescription, null);
            
            // Actualizar usando professionInputPort
            Profession result = professionInputPort.edit(id, updatedProfession);
            
            // Convertir a modelo CLI y mostrar
            ProfesionModelCli profesionModel = profesionMapperCli.fromDomainToAdapterCli(result);
            System.out.println("Profesión editada exitosamente: " + profesionModel);
            
            log.info("Profesión editada exitosamente: {}", id);
            
        } catch (NoExistException e) {
            log.error("Error de existencia: {}", e.getMessage());
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            log.error("Formato de ID inválido: {}", e.getMessage());
            System.out.println("Error: El ID debe ser un número válido.");
        } catch (Exception e) {
            log.error("Error al editar profesión: {}", e.getMessage());
            System.out.println("Error al editar la profesión: " + e.getMessage());
        }
    }
    
    public void eliminar(Scanner keyboard) {
        log.info("Into eliminar ProfessionEntity in Input Adapter");
        
        try {
            System.out.print("ID de la profesión a eliminar: ");
            String idStr = keyboard.nextLine().trim();
            Integer id = Integer.parseInt(idStr);
            
            // Verificar que la profesión existe antes de mostrar confirmación
            Profession existingProfession = professionInputPort.findOne(id);
            
            // Confirmar eliminación
            System.out.print("¿Está seguro de eliminar la profesión '" + existingProfession.getName() + "' (ID: " + id + ")? (s/n): ");
            String confirmation = keyboard.nextLine().trim().toLowerCase();
            
            if (confirmation.equals("s") || confirmation.equals("si") || confirmation.equals("y") || confirmation.equals("yes")) {
                // Eliminar usando professionInputPort
                Boolean deleted = professionInputPort.drop(id);
                
                if (deleted) {
                    System.out.println("Profesión eliminada exitosamente: " + existingProfession.getName());
                    log.info("Profesión eliminada exitosamente: {}", id);
                } else {
                    System.out.println("No se pudo eliminar la profesión.");
                    log.warn("No se pudo eliminar la profesión: {}", id);
                }
            } else {
                System.out.println("Eliminación cancelada.");
                log.info("Eliminación cancelada por el usuario para: {}", id);
            }
            
        } catch (NoExistException e) {
            log.error("Profesión no encontrada: {}", e.getMessage());
            System.out.println("Error: Profesión no encontrada.");
        } catch (NumberFormatException e) {
            log.error("Formato de ID inválido: {}", e.getMessage());
            System.out.println("Error: El ID debe ser un número válido.");
        } catch (Exception e) {
            log.error("Error al eliminar profesión: {}", e.getMessage());
            System.out.println("Error al eliminar la profesión: " + e.getMessage());
        }
    }
    
    // Método adicional para contar profesiones
    public void contar() {
        log.info("Into contar ProfessionEntity in Input Adapter");
        try {
            Integer count = professionInputPort.count();
            System.out.println("Total de profesiones registradas: " + count);
            log.info("Total de profesiones: {}", count);
        } catch (Exception e) {
            log.error("Error al contar profesiones: {}", e.getMessage());
            System.out.println("Error al contar las profesiones.");
        }
    }
    
    // Método adicional para ver estudios de una profesión
    public void verEstudios(Scanner keyboard) {
        log.info("Into verEstudios ProfessionEntity in Input Adapter");
        
        try {
            System.out.print("ID de la profesión: ");
            String idStr = keyboard.nextLine().trim();
            Integer id = Integer.parseInt(idStr);
            
            // Buscar estudios de la profesión
            List<Study> studies = professionInputPort.getStudies(id);
            
            if (studies.isEmpty()) {
                System.out.println("No se encontraron estudios para la profesión con ID: " + id);
            } else {
                System.out.println("Estudios encontrados para la profesión ID " + id + ":");
                studies.forEach(study -> {
                    System.out.println("- Persona: " + study.getPerson().getFirstName() + 
                                     " " + study.getPerson().getLastName() + 
                                     " (CC: " + study.getPerson().getIdentification() + ")" +
                                     " - Fecha graduación: " + study.getGraduationDate() +
                                     " - Universidad: " + study.getUniversityName());
                });
            }
            
            log.info("Consulta de estudios completada para profesión ID: {}", id);
            
        } catch (NoExistException e) {
            log.error("Profesión no encontrada: {}", e.getMessage());
            System.out.println("Error: No se encontraron estudios para esa profesión.");
        } catch (NumberFormatException e) {
            log.error("Formato de ID inválido: {}", e.getMessage());
            System.out.println("Error: El ID debe ser un número válido.");
        } catch (Exception e) {
            log.error("Error al consultar estudios: {}", e.getMessage());
            System.out.println("Error al consultar estudios de la profesión: " + e.getMessage());
        }
    }
}