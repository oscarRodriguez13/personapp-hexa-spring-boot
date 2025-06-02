package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.InputMismatchException;
import java.util.Scanner;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.terminal.adapter.TelefonoInputAdapterCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TelefonoMenu {

    private static final int OPCION_REGRESAR_MODULOS = 0;
    private static final int PERSISTENCIA_MARIADB = 1;
    private static final int PERSISTENCIA_MONGODB = 2;

    private static final int OPCION_REGRESAR_MOTOR_PERSISTENCIA = 0;
    private static final int OPCION_VER_TODOS = 1;
    private static final int OPCION_CREAR = 2;
    private static final int OPCION_EDITAR = 3;
    private static final int OPCION_ELIMINAR = 4;
    private static final int OPCION_BUSCAR = 5;

    public void iniciarMenu(TelefonoInputAdapterCli phoneInputAdapterCli, Scanner keyboard) {
        boolean isValid = false;
        do {
            try {
                mostrarMenuMotorPersistencia();
                int opcion = leerOpcion(keyboard);
                switch (opcion) {
                    case OPCION_REGRESAR_MODULOS:
                        isValid = true;
                        break;
                    case PERSISTENCIA_MARIADB:
                        phoneInputAdapterCli.setPhoneOutputPortInjection("MARIA");
                        menuOpciones(phoneInputAdapterCli, keyboard);
                        break;
                    case PERSISTENCIA_MONGODB:
                        phoneInputAdapterCli.setPhoneOutputPortInjection("MONGO");
                        menuOpciones(phoneInputAdapterCli, keyboard);
                        break;
                    default:
                        log.warn("La opción elegida no es válida.");
                }
            } catch (InvalidOptionException e) {
                log.warn(e.getMessage());
            }
        } while (!isValid);
    }

    private void menuOpciones(TelefonoInputAdapterCli phoneInputAdapterCli, Scanner keyboard) {
        boolean isValid = false;
        do {
            try {
                mostrarMenuOpciones();
                int opcion = leerOpcion(keyboard);
                switch (opcion) {
                    case OPCION_REGRESAR_MOTOR_PERSISTENCIA:
                        isValid = true;
                        break;
                    case OPCION_VER_TODOS:
                        phoneInputAdapterCli.historial();
                        break;
                    case OPCION_CREAR:
                        phoneInputAdapterCli.crear();
                        break;
                    case OPCION_EDITAR:
                        phoneInputAdapterCli.editar();
                        break;
                    case OPCION_ELIMINAR:
                        phoneInputAdapterCli.eliminar();
                        break;
                    case OPCION_BUSCAR:
                        phoneInputAdapterCli.buscar();
                        break;
                    default:
                        log.warn("La opción elegida no es válida.");
                }
            } catch (InputMismatchException e) {
                log.warn("Solo se permiten números.");
                //keyboard.nextLine(); // limpiar buffer
            }
        } while (!isValid);
    }

    private void mostrarMenuMotorPersistencia() {
        System.out.println("----------------------");
        System.out.println(PERSISTENCIA_MARIADB + " para MariaDB");
        System.out.println(PERSISTENCIA_MONGODB + " para MongoDB");
        System.out.println(OPCION_REGRESAR_MODULOS + " para regresar");
    }

    private void mostrarMenuOpciones() {
        System.out.println("----------------------");
        System.out.println(OPCION_VER_TODOS + " para ver todos los teléfonos");
        System.out.println(OPCION_CREAR + " para crear teléfono");
        System.out.println(OPCION_EDITAR + " para editar teléfono");
        System.out.println(OPCION_ELIMINAR + " para eliminar teléfono");
        System.out.println(OPCION_BUSCAR + " para buscar teléfono");
        System.out.println(OPCION_REGRESAR_MOTOR_PERSISTENCIA + " para regresar");
    }

    private int leerOpcion(Scanner keyboard) {
		try {
			System.out.print("Ingrese una opción: ");
			return keyboard.nextInt();
		} catch (InputMismatchException e) {
			log.warn("Solo se permiten números.");
			return leerOpcion(keyboard);
		}
	}

}