package co.edu.javeriana.as.personapp.terminal.adapter;

import java.util.List;
import java.util.Scanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import co.edu.javeriana.as.personapp.terminal.mapper.TelefonoMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.TelefonoModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class TelefonoInputAdapterCli {
    
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
    private TelefonoMapperCli telefonoMapperCli;
    
    private PhoneInputPort phoneInputPort;
    private PersonInputPort personInputPort;
    
    public void setPhoneOutputPortInjection(String dbOption) throws InvalidOptionException {
        if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
            phoneInputPort = new PhoneUseCase(phoneOutputPortMaria, personOutputPortMaria);
            personInputPort = new PersonUseCase(personOutputPortMaria);
        } else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
            phoneInputPort = new PhoneUseCase(phoneOutputPortMongo, personOutputPortMongo);
            personInputPort = new PersonUseCase(personOutputPortMongo);
        } else {
            throw new InvalidOptionException("Invalid database option: " + dbOption);
        }
    }
    
    public void historial() {
        log.info("Into historial TelefonoEntity in Input Adapter");
        try {
            phoneInputPort.findAll().stream()
                .map(phone -> telefonoMapperCli.fromDomainToAdapterCli((Phone) phone))
                .forEach(System.out::println);
        } catch (Exception e) {
            log.error("Error al obtener historial de teléfonos: {}", e.getMessage());
            System.out.println("Error al obtener el historial de teléfonos.");
        }
    }
    
    public void crear(Scanner keyboard) {
        log.info("Into crear TelefonoEntity in Input Adapter");
        
        try {
            System.out.print("Número de teléfono: ");
            String number = keyboard.nextLine().trim();
            
            System.out.print("Operador: ");
            String operator = keyboard.nextLine().trim();
            
            System.out.print("Cédula del propietario: ");
            String ownerCcStr = keyboard.nextLine().trim();
            Integer ownerCc = Integer.parseInt(ownerCcStr);
            
            // Buscar la persona propietaria
            Person owner = personInputPort.findOne(ownerCc);
            
            // Crear el teléfono en el dominio
            Phone phone = new Phone(number, operator, owner);
            
            // Guardar usando phoneInputPort
            Phone createdPhone = phoneInputPort.create(phone);
            
            // Convertir a modelo CLI y mostrar
            TelefonoModelCli telefonoModel = telefonoMapperCli.fromDomainToAdapterCli(createdPhone);
            System.out.println("Teléfono creado exitosamente: " + telefonoModel);
            
            log.info("Teléfono creado exitosamente: {}", createdPhone.getNumber());
            
        } catch (NoExistException e) {
            log.error("Persona no encontrada: {}", e.getMessage());
            System.out.println("Error: La persona con esa cédula no existe.");
        } catch (NumberFormatException e) {
            log.error("Formato de cédula inválido: {}", e.getMessage());
            System.out.println("Error: La cédula debe ser un número válido.");
        } catch (Exception e) {
            log.error("Error al crear teléfono: {}", e.getMessage());
            System.out.println("Error al crear el teléfono: " + e.getMessage());
        }
    }
    
    public void buscar(Scanner keyboard) {
        log.info("Into buscar TelefonoEntity in Input Adapter");
        
        try {
            System.out.print("Número de teléfono a buscar: ");
            String number = keyboard.nextLine().trim();
            
            // Buscar usando phoneInputPort
            Phone phone = phoneInputPort.findOne(number);
            
            // Convertir a modelo CLI y mostrar
            TelefonoModelCli telefonoModel = telefonoMapperCli.fromDomainToAdapterCli(phone);
            System.out.println("Teléfono encontrado: " + telefonoModel);
            
            log.info("Búsqueda completada exitosamente para número: {}", number);
            
        } catch (NoExistException e) {
            log.error("Teléfono no encontrado: {}", e.getMessage());
            System.out.println("Error: Teléfono no encontrado.");
        } catch (Exception e) {
            log.error("Error al buscar teléfono: {}", e.getMessage());
            System.out.println("Error al buscar el teléfono: " + e.getMessage());
        }
    }
    
    public void editar(Scanner keyboard) {
        log.info("Into editar TelefonoEntity in Input Adapter");
        
        try {
            System.out.print("Número de teléfono a editar: ");
            String number = keyboard.nextLine().trim();
            
            // Verificar que el teléfono existe
            Phone existingPhone = phoneInputPort.findOne(number);
            
            System.out.print("Nuevo operador (actual: " + existingPhone.getCompany() + "): ");
            String newOperator = keyboard.nextLine().trim();
            
            System.out.print("Nueva cédula del propietario (actual: " + existingPhone.getOwner().getIdentification() + "): ");
            String newOwnerCcStr = keyboard.nextLine().trim();
            Integer newOwnerCc = Integer.parseInt(newOwnerCcStr);
            
            // Buscar el nuevo propietario
            Person newOwner = personInputPort.findOne(newOwnerCc);
            
            // Crear el teléfono actualizado
            Phone updatedPhone = new Phone(number, newOperator, newOwner);
            
            // Actualizar usando phoneInputPort
            Phone result = phoneInputPort.edit(number, updatedPhone);
            
            // Convertir a modelo CLI y mostrar
            TelefonoModelCli telefonoModel = telefonoMapperCli.fromDomainToAdapterCli(result);
            System.out.println("Teléfono editado exitosamente: " + telefonoModel);
            
            log.info("Teléfono editado exitosamente: {}", number);
            
        } catch (NoExistException e) {
            log.error("Error de existencia: {}", e.getMessage());
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            log.error("Formato de cédula inválido: {}", e.getMessage());
            System.out.println("Error: La cédula debe ser un número válido.");
        } catch (Exception e) {
            log.error("Error al editar teléfono: {}", e.getMessage());
            System.out.println("Error al editar el teléfono: " + e.getMessage());
        }
    }
    
    public void eliminar(Scanner keyboard) {
        log.info("Into eliminar TelefonoEntity in Input Adapter");
        
        try {
            System.out.print("Número de teléfono a eliminar: ");
            String number = keyboard.nextLine().trim();
        
            
            // Confirmar eliminación
            System.out.print("¿Está seguro de eliminar el teléfono " + number);
            String confirmation = keyboard.nextLine().trim().toLowerCase();
            
            if (confirmation.equals("s") || confirmation.equals("si") || confirmation.equals("y") || confirmation.equals("yes")) {
                // Eliminar usando phoneInputPort
                Boolean deleted = phoneInputPort.drop(number);
                
                if (deleted) {
                    System.out.println("Teléfono eliminado exitosamente: " + number);
                    log.info("Teléfono eliminado exitosamente: {}", number);
                } else {
                    System.out.println("No se pudo eliminar el teléfono.");
                    log.warn("No se pudo eliminar el teléfono: {}", number);
                }
            } else {
                System.out.println("Eliminación cancelada.");
                log.info("Eliminación cancelada por el usuario para: {}", number);
            }
            
        } catch (NoExistException e) {
            log.error("Teléfono no encontrado: {}", e.getMessage());
            System.out.println("Error: Teléfono no encontrado.");
        } catch (Exception e) {
            log.error("Error al eliminar teléfono: {}", e.getMessage());
            System.out.println("Error al eliminar el teléfono: " + e.getMessage());
        }
    }
    
    // Método adicional para contar teléfonos
    public void contar() {
        log.info("Into contar TelefonoEntity in Input Adapter");
        try {
            Integer count = phoneInputPort.count();
            System.out.println("Total de teléfonos registrados: " + count);
            log.info("Total de teléfonos: {}", count);
        } catch (Exception e) {
            log.error("Error al contar teléfonos: {}", e.getMessage());
            System.out.println("Error al contar los teléfonos.");
        }
    }
    
    // Método adicional para buscar teléfonos por propietario
    public void buscarPorPropietario(Scanner keyboard) {
        log.info("Into buscarPorPropietario TelefonoEntity in Input Adapter");
        
        try {
            System.out.print("Cédula del propietario: ");
            String ownerCcStr = keyboard.nextLine().trim();
            Integer ownerCc = Integer.parseInt(ownerCcStr);
            
            // Buscar teléfonos por propietario
            List<Phone> phones = phoneInputPort.findByOwner(ownerCc);
            
            if (phones.isEmpty()) {
                System.out.println("No se encontraron teléfonos para la cédula: " + ownerCc);
            } else {
                System.out.println("Teléfonos encontrados para la cédula " + ownerCc + ":");
                phones.stream()
                    .map(phone -> telefonoMapperCli.fromDomainToAdapterCli(phone))
                    .forEach(System.out::println);
            }
            
            log.info("Búsqueda por propietario completada para cédula: {}", ownerCc);
            
        } catch (NoExistException e) {
            log.error("Propietario no encontrado: {}", e.getMessage());
            System.out.println("Error: No se encontraron teléfonos para esa cédula.");
        } catch (NumberFormatException e) {
            log.error("Formato de cédula inválido: {}", e.getMessage());
            System.out.println("Error: La cédula debe ser un número válido.");
        } catch (Exception e) {
            log.error("Error al buscar por propietario: {}", e.getMessage());
            System.out.println("Error al buscar teléfonos por propietario: " + e.getMessage());
        }
    }
}