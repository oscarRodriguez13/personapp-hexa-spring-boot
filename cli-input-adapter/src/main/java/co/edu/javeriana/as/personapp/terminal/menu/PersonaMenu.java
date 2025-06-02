package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.InputMismatchException;
import java.util.Scanner;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.terminal.adapter.PersonaInputAdapterCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PersonaMenu {
    
    // Constantes para navegación
    private static final int OPCION_REGRESAR_MODULOS = 0;
    private static final int PERSISTENCIA_MARIADB = 1;
    private static final int PERSISTENCIA_MONGODB = 2;
    
    // Constantes para operaciones CRUD
    private static final int OPCION_REGRESAR_MOTOR_PERSISTENCIA = 0;
    private static final int OPCION_VER_TODAS = 1;
    private static final int OPCION_CREAR = 2;
    private static final int OPCION_EDITAR = 3;
    private static final int OPCION_ELIMINAR = 4;
    private static final int OPCION_BUSCAR = 5;
    private static final int OPCION_CONTAR = 6;
    private static final int OPCION_VER_TELEFONOS = 7;
    private static final int OPCION_VER_ESTUDIOS = 8;

    public void iniciarMenu(PersonaInputAdapterCli personaInputAdapterCli, Scanner keyboard) {
        boolean continuar = true;
        
        while (continuar) {
            try {
                mostrarMenuMotorPersistencia();
                int opcion = leerOpcion(keyboard);
                
                switch (opcion) {
                    case OPCION_REGRESAR_MODULOS:
                        continuar = false;
                        break;
                    case PERSISTENCIA_MARIADB:
                        personaInputAdapterCli.setPersonOutputPortInjection("MARIA");
                        menuOpciones(personaInputAdapterCli, keyboard);
                        break;
                    case PERSISTENCIA_MONGODB:
                        personaInputAdapterCli.setPersonOutputPortInjection("MONGO");
                        menuOpciones(personaInputAdapterCli, keyboard);
                        break;
                    default:
                        System.out.println("Opción no válida. Seleccione una opción del menú.");
                }
            } catch (InvalidOptionException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void menuOpciones(PersonaInputAdapterCli personaInputAdapterCli, Scanner keyboard) {
        boolean continuar = true;
        
        while (continuar) {
            try {
                mostrarMenuOpciones();
                int opcion = leerOpcion(keyboard);
                
                switch (opcion) {
                    case OPCION_REGRESAR_MOTOR_PERSISTENCIA:
                        continuar = false;
                        break;
                    case OPCION_VER_TODAS:
                        personaInputAdapterCli.historial();
                        break;
                    case OPCION_CREAR:
                        personaInputAdapterCli.crear(keyboard);
                        break;
                    case OPCION_EDITAR:
                        personaInputAdapterCli.editar(keyboard);
                        break;
                    case OPCION_ELIMINAR:
                        personaInputAdapterCli.eliminar(keyboard);
                        break;
                    case OPCION_BUSCAR:
                        personaInputAdapterCli.buscar(keyboard);
                        break;
                    case OPCION_CONTAR:
                        personaInputAdapterCli.contar();
                        break;
                    case OPCION_VER_TELEFONOS:
                        personaInputAdapterCli.verTelefonos(keyboard);
                        break;
                    case OPCION_VER_ESTUDIOS:
                        personaInputAdapterCli.verEstudios(keyboard);
                        break;
                    default:
                        System.out.println("Opción no válida. Seleccione una opción del menú.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Debe ingresar un número válido.");
                keyboard.nextLine(); // Limpiar buffer
            }
        }
    }

    private void mostrarMenuMotorPersistencia() {
        System.out.println();
        System.out.println("═══════════════════════════════════════");
        System.out.println("        MÓDULO DE PERSONAS             ");
        System.out.println("═══════════════════════════════════════");
        System.out.println("Seleccione el motor de persistencia:");
        System.out.println();
        System.out.println(" " + PERSISTENCIA_MARIADB + ". MariaDB");
        System.out.println(" " + PERSISTENCIA_MONGODB + ". MongoDB");
        System.out.println(" " + OPCION_REGRESAR_MODULOS + ". Regresar al menú principal");
        System.out.println("═══════════════════════════════════════");
    }

    private void mostrarMenuOpciones() {
        System.out.println();
        System.out.println("═══════════════════════════════════════");
        System.out.println("      OPERACIONES DE PERSONAS          ");
        System.out.println("═══════════════════════════════════════");
        System.out.println(" " + OPCION_VER_TODAS + ". Ver todas las personas");
        System.out.println(" " + OPCION_CREAR + ". Crear nueva persona");
        System.out.println(" " + OPCION_EDITAR + ". Editar persona existente");
        System.out.println(" " + OPCION_ELIMINAR + ". Eliminar persona");
        System.out.println(" " + OPCION_BUSCAR + ". Buscar persona específica");
        System.out.println(" " + OPCION_CONTAR + ". Contar personas registradas");
        System.out.println(" " + OPCION_VER_TELEFONOS + ". Ver teléfonos de una persona");
        System.out.println(" " + OPCION_VER_ESTUDIOS + ". Ver estudios de una persona");
        System.out.println(" " + OPCION_REGRESAR_MOTOR_PERSISTENCIA + ". Regresar a selección de BD");
        System.out.println("═══════════════════════════════════════");
    }

    private int leerOpcion(Scanner keyboard) {
        try {
            System.out.print("Seleccione una opción: ");
            int opcion = keyboard.nextInt();
            keyboard.nextLine(); // Limpiar buffer
            return opcion;
        } catch (InputMismatchException e) {
            System.out.println("Error: Debe ingresar un número válido.");
            keyboard.nextLine(); // Limpiar buffer
            return -1; // Opción inválida
        }
    }
}