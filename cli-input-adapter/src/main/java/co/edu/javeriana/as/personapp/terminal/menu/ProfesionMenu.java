package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.InputMismatchException;
import java.util.Scanner;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.terminal.adapter.ProfesionInputAdapterCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProfesionMenu {
    
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
    private static final int OPCION_VER_ESTUDIOS = 7;
    
    public void iniciarMenu(ProfesionInputAdapterCli professionInputAdapterCli, Scanner keyboard) {
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
                        professionInputAdapterCli.setProfessionOutputPortInjection("MARIA");
                        menuOpciones(professionInputAdapterCli, keyboard);
                        break;
                    case PERSISTENCIA_MONGODB:
                        professionInputAdapterCli.setProfessionOutputPortInjection("MONGO");
                        menuOpciones(professionInputAdapterCli, keyboard);
                        break;
                    default:
                        System.out.println("Opción no válida. Seleccione una opción del menú.");
                }
            } catch (InvalidOptionException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    
    private void menuOpciones(ProfesionInputAdapterCli professionInputAdapterCli, Scanner keyboard) {
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
                        professionInputAdapterCli.historial();
                        break;
                    case OPCION_CREAR:
                        professionInputAdapterCli.crear(keyboard);
                        break;
                    case OPCION_EDITAR:
                        professionInputAdapterCli.editar(keyboard);
                        break;
                    case OPCION_ELIMINAR:
                        professionInputAdapterCli.eliminar(keyboard);
                        break;
                    case OPCION_BUSCAR:
                        professionInputAdapterCli.buscar(keyboard);
                        break;
                    case OPCION_CONTAR:
                        professionInputAdapterCli.contar();
                        break;
                    case OPCION_VER_ESTUDIOS:
                        professionInputAdapterCli.verEstudios(keyboard);
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
        System.out.println("       MÓDULO DE PROFESIONES           ");
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
        System.out.println("     OPERACIONES DE PROFESIONES        ");
        System.out.println("═══════════════════════════════════════");
        System.out.println(" " + OPCION_VER_TODAS + ". Ver todas las profesiones");
        System.out.println(" " + OPCION_CREAR + ". Crear nueva profesión");
        System.out.println(" " + OPCION_EDITAR + ". Editar profesión existente");
        System.out.println(" " + OPCION_ELIMINAR + ". Eliminar profesión");
        System.out.println(" " + OPCION_BUSCAR + ". Buscar profesión específica");
        System.out.println(" " + OPCION_CONTAR + ". Contar total de profesiones");
        System.out.println(" " + OPCION_VER_ESTUDIOS + ". Ver estudios por profesión");
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
