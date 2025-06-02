package co.edu.javeriana.as.personapp.terminal.adapter;

import java.util.List;
import java.util.Scanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PersonUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.terminal.mapper.PersonaMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.PersonaModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class PersonaInputAdapterCli {
    
    @Autowired
    @Qualifier("personOutputAdapterMaria")
    private PersonOutputPort personOutputPortMaria;
    
    @Autowired
    @Qualifier("personOutputAdapterMongo")
    private PersonOutputPort personOutputPortMongo;
    
    @Autowired
    private PersonaMapperCli personaMapperCli;
    
    private PersonInputPort personInputPort;

    public void setPersonOutputPortInjection(String dbOption) throws InvalidOptionException {
        if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
            personInputPort = new PersonUseCase(personOutputPortMaria);
        } else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
            personInputPort = new PersonUseCase(personOutputPortMongo);
        } else {
            throw new InvalidOptionException("Invalid database option: " + dbOption);
        }
    }

    public void historial() {
        log.info("Into historial PersonaEntity in Input Adapter");
        try {
            personInputPort.findAll().stream()
                .map(personaMapperCli::fromDomainToAdapterCli)
                .forEach(System.out::println);
        } catch (Exception e) {
            log.error("Error al obtener historial de personas: {}", e.getMessage());
            System.out.println("Error al obtener el historial de personas.");
        }
    }
    
    public void crear(Scanner keyboard) {
        log.info("Into crear PersonaEntity in Input Adapter");
        
        try {
            System.out.print("Número de cédula: ");
            String ccStr = keyboard.nextLine().trim();
            Integer cc = Integer.parseInt(ccStr);
            
            System.out.print("Nombre: ");
            String nombre = keyboard.nextLine().trim();
            
            System.out.print("Apellido: ");
            String apellido = keyboard.nextLine().trim();
            
            System.out.print("Género (MALE/FEMALE): ");
            String generoStr = keyboard.nextLine().trim().toUpperCase();
            Gender genero = Gender.valueOf(generoStr);
            
            System.out.print("Edad: ");
            String edadStr = keyboard.nextLine().trim();
            Integer edad = Integer.parseInt(edadStr);
            
            // Crear la persona en el dominio
            Person person = new Person(cc, nombre, apellido, genero, edad, null, null);
            
            // Guardar usando personInputPort
            Person createdPerson = personInputPort.create(person);
            
            // Convertir a modelo CLI y mostrar
            PersonaModelCli personaModel = personaMapperCli.fromDomainToAdapterCli(createdPerson);
            System.out.println("Persona creada exitosamente: " + personaModel);
            
            log.info("Persona creada exitosamente: {}", createdPerson.getIdentification());
            
        } catch (IllegalArgumentException e) {
            log.error("Género inválido: {}", e.getMessage());
            System.out.println("Error: El género debe ser MALE o FEMALE.");
        } catch (Exception e) {
            log.error("Error al crear persona: {}", e.getMessage());
            System.out.println("Error al crear la persona: " + e.getMessage());
        }
    }
    
    public void buscar(Scanner keyboard) {
        log.info("Into buscar PersonaEntity in Input Adapter");
        
        try {
            System.out.print("Cédula de la persona a buscar: ");
            String ccStr = keyboard.nextLine().trim();
            Integer cc = Integer.parseInt(ccStr);
            
            // Buscar usando personInputPort
            Person person = personInputPort.findOne(cc);
            
            // Convertir a modelo CLI y mostrar
            PersonaModelCli personaModel = personaMapperCli.fromDomainToAdapterCli(person);
            System.out.println("Persona encontrada: " + personaModel);
            
            log.info("Búsqueda completada exitosamente para cédula: {}", cc);
            
        } catch (NoExistException e) {
            log.error("Persona no encontrada: {}", e.getMessage());
            System.out.println("Error: Persona no encontrada.");
        } catch (NumberFormatException e) {
            log.error("Formato de cédula inválido: {}", e.getMessage());
            System.out.println("Error: La cédula debe ser un número válido.");
        } catch (Exception e) {
            log.error("Error al buscar persona: {}", e.getMessage());
            System.out.println("Error al buscar la persona: " + e.getMessage());
        }
    }
    
    public void editar(Scanner keyboard) {
        log.info("Into editar PersonaEntity in Input Adapter");
        
        try {
            System.out.print("Cédula de la persona a editar: ");
            String ccStr = keyboard.nextLine().trim();
            Integer cc = Integer.parseInt(ccStr);
            
            // Verificar que la persona existe
            Person existingPerson = personInputPort.findOne(cc);
            System.out.println("Persona actual: " + personaMapperCli.fromDomainToAdapterCli(existingPerson));
            
            System.out.print("Nuevo nombre (actual: " + existingPerson.getFirstName() + "): ");
            String nuevoNombre = keyboard.nextLine().trim();
            if (nuevoNombre.isEmpty()) nuevoNombre = existingPerson.getFirstName();
            
            System.out.print("Nuevo apellido (actual: " + existingPerson.getLastName() + "): ");
            String nuevoApellido = keyboard.nextLine().trim();
            if (nuevoApellido.isEmpty()) nuevoApellido = existingPerson.getLastName();
            
            System.out.print("Nuevo género MALE/FEMALE (actual: " + existingPerson.getGender() + "): ");
            String nuevoGeneroStr = keyboard.nextLine().trim().toUpperCase();
            Gender nuevoGenero = nuevoGeneroStr.isEmpty() ? existingPerson.getGender() : Gender.valueOf(nuevoGeneroStr);
            
            System.out.print("Nueva edad (actual: " + existingPerson.getAge() + "): ");
            String nuevaEdadStr = keyboard.nextLine().trim();
            Integer nuevaEdad = nuevaEdadStr.isEmpty() ? existingPerson.getAge() : Integer.parseInt(nuevaEdadStr);
            
            // Crear la persona actualizada
            Person updatedPerson = new Person(cc, nuevoNombre, nuevoApellido, nuevoGenero, nuevaEdad, null, null);
            
            // Actualizar usando personInputPort
            Person result = personInputPort.edit(cc, updatedPerson);
            
            // Convertir a modelo CLI y mostrar
            PersonaModelCli personaModel = personaMapperCli.fromDomainToAdapterCli(result);
            System.out.println("Persona editada exitosamente: " + personaModel);
            
            log.info("Persona editada exitosamente: {}", cc);
            
        } catch (NoExistException e) {
            log.error("Error de existencia: {}", e.getMessage());
            System.out.println("Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Género inválido: {}", e.getMessage());
            System.out.println("Error: El género debe ser MALE o FEMALE.");
        } catch (Exception e) {
            log.error("Error al editar persona: {}", e.getMessage());
            System.out.println("Error al editar la persona: " + e.getMessage());
        }
    }
    
    public void eliminar(Scanner keyboard) {
        log.info("Into eliminar PersonaEntity in Input Adapter");
        
        try {
            System.out.print("Cédula de la persona a eliminar: ");
            String ccStr = keyboard.nextLine().trim();
            Integer cc = Integer.parseInt(ccStr);
            
            // Verificar que la persona existe antes de eliminar
            Person existingPerson = personInputPort.findOne(cc);
            PersonaModelCli personaModel = personaMapperCli.fromDomainToAdapterCli(existingPerson);
            System.out.println("Persona a eliminar: " + personaModel);
            
            // Confirmar eliminación
            System.out.print("¿Está seguro de eliminar esta persona? (S/N): ");
            String confirmation = keyboard.nextLine().trim().toLowerCase();
            
            if (confirmation.equals("s") || confirmation.equals("si") || confirmation.equals("y") || confirmation.equals("yes")) {
                // Eliminar usando personInputPort
                Boolean deleted = personInputPort.drop(cc);
                
                if (deleted) {
                    System.out.println("Persona eliminada exitosamente: " + cc);
                    log.info("Persona eliminada exitosamente: {}", cc);
                } else {
                    System.out.println("No se pudo eliminar la persona.");
                    log.warn("No se pudo eliminar la persona: {}", cc);
                }
            } else {
                System.out.println("Eliminación cancelada.");
                log.info("Eliminación cancelada por el usuario para: {}", cc);
            }
            
        } catch (NoExistException e) {
            log.error("Persona no encontrada: {}", e.getMessage());
            System.out.println("Error: Persona no encontrada.");
        } catch (NumberFormatException e) {
            log.error("Formato de cédula inválido: {}", e.getMessage());
            System.out.println("Error: La cédula debe ser un número válido.");
        } catch (Exception e) {
            log.error("Error al eliminar persona: {}", e.getMessage());
            System.out.println("Error al eliminar la persona: " + e.getMessage());
        }
    }
    
    public void contar() {
        log.info("Into contar PersonaEntity in Input Adapter");
        try {
            Integer count = personInputPort.count();
            System.out.println("Total de personas registradas: " + count);
            log.info("Total de personas: {}", count);
        } catch (Exception e) {
            log.error("Error al contar personas: {}", e.getMessage());
            System.out.println("Error al contar las personas.");
        }
    }
    
    public void verTelefonos(Scanner keyboard) {
        log.info("Into verTelefonos PersonaEntity in Input Adapter");
        
        try {
            System.out.print("Cédula de la persona: ");
            String ccStr = keyboard.nextLine().trim();
            Integer cc = Integer.parseInt(ccStr);
            
            // Obtener teléfonos de la persona
            List<Phone> phones = personInputPort.getPhones(cc);
            
            if (phones.isEmpty()) {
                System.out.println("No se encontraron teléfonos para la cédula: " + cc);
            } else {
                System.out.println("Teléfonos de la persona con cédula " + cc + ":");
                phones.forEach(phone -> {
                    System.out.println("- Número: " + phone.getNumber() + 
                                     ", Operador: " + phone.getCompany());
                });
            }
            
            log.info("Consulta de teléfonos completada para cédula: {}", cc);
            
        } catch (NoExistException e) {
            log.error("Persona no encontrada: {}", e.getMessage());
            System.out.println("Error: Persona no encontrada.");
        } catch (NumberFormatException e) {
            log.error("Formato de cédula inválido: {}", e.getMessage());
            System.out.println("Error: La cédula debe ser un número válido.");
        } catch (Exception e) {
            log.error("Error al consultar teléfonos: {}", e.getMessage());
            System.out.println("Error al consultar los teléfonos: " + e.getMessage());
        }
    }
    
    public void verEstudios(Scanner keyboard) {
        log.info("Into verEstudios PersonaEntity in Input Adapter");
        
        try {
            System.out.print("Cédula de la persona: ");
            String ccStr = keyboard.nextLine().trim();
            Integer cc = Integer.parseInt(ccStr);
            
            // Obtener estudios de la persona
            List<Study> studies = personInputPort.getStudies(cc);
            
            if (studies.isEmpty()) {
                System.out.println("No se encontraron estudios para la cédula: " + cc);
            } else {
                System.out.println("Estudios de la persona con cédula " + cc + ":");
                studies.forEach(study -> {
                    System.out.println("- Profesión: " + study.getProfession().getName() + 
                                     ", Universidad: " + study.getUniversityName() +
                                     ", Fecha de graduación: " + study.getGraduationDate());
                });
            }
            
            log.info("Consulta de estudios completada para cédula: {}", cc);
            
        } catch (NoExistException e) {
            log.error("Persona no encontrada: {}", e.getMessage());
            System.out.println("Error: Persona no encontrada.");
        } catch (NumberFormatException e) {
            log.error("Formato de cédula inválido: {}", e.getMessage());
            System.out.println("Error: La cédula debe ser un número válido.");
        } catch (Exception e) {
            log.error("Error al consultar estudios: {}", e.getMessage());
            System.out.println("Error al consultar los estudios: " + e.getMessage());
        }
    }
}