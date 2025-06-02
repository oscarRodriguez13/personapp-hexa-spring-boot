package co.edu.javeriana.as.personapp.terminal.menu;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import co.edu.javeriana.as.personapp.terminal.adapter.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MenuPrincipal {
    
    // Constantes de opciones del menú
    private static final int SALIR = 0;
    private static final int MODULO_PERSONA = 1;
    private static final int MODULO_PROFESION = 2;
    private static final int MODULO_TELEFONO = 3;
    private static final int MODULO_ESTUDIO = 4;
    
    // Dependencias inyectadas
    @Autowired
    private PersonaInputAdapterCli personaInputAdapterCli;
    @Autowired
    private TelefonoInputAdapterCli telefonoInputAdapterCli;
    @Autowired
    private ProfesionInputAdapterCli profesionInputAdapterCli;
    @Autowired
    private EstudioInputAdapterCli estudioInputAdapterCli;
    
    // Componentes del menú
    private final PersonaMenu personaMenu;
    private final TelefonoMenu telefonoMenu;
    private final ProfesionMenu profesionMenu;
    private final EstudioMenu estudioMenu;
    private final Scanner keyboard;
    
    public MenuPrincipal() {
        this.personaMenu = new PersonaMenu();
        this.profesionMenu = new ProfesionMenu();
        this.telefonoMenu = new TelefonoMenu();
        this.estudioMenu = new EstudioMenu();
        this.keyboard = new Scanner(System.in);
    }
    
    public void inicio() {
        boolean continuar = true;
        
        while (continuar) {
            mostrarMenu();
            int opcion = leerOpcion(keyboard);
            
            switch (opcion) {
                case SALIR:
                    continuar = false;
                    System.out.println("Gracias por usar el sistema. ¡Hasta pronto!");
                    break;
                case MODULO_PERSONA:
                    personaMenu.iniciarMenu(personaInputAdapterCli, keyboard);
                    break;
                case MODULO_PROFESION:
                    profesionMenu.iniciarMenu(profesionInputAdapterCli, keyboard);
                    break;
                case MODULO_TELEFONO:
                    telefonoMenu.iniciarMenu(telefonoInputAdapterCli, keyboard);
                    break;
                case MODULO_ESTUDIO:
                    estudioMenu.iniciarMenu(estudioInputAdapterCli, keyboard);
                    break;
                default:
                    System.out.println("Opción no válida. Por favor seleccione una opción del menú.");
            }
        }
    }
    
    private void mostrarMenu() {
        System.out.println();
        System.out.println("═══════════════════════════════════════");
        System.out.println("         SISTEMA DE GESTIÓN            ");
        System.out.println("═══════════════════════════════════════");
        System.out.println(" " + MODULO_PERSONA + ". Gestión de Personas");
        System.out.println(" " + MODULO_PROFESION + ". Gestión de Profesiones");
        System.out.println(" " + MODULO_TELEFONO + ". Gestión de Teléfonos");
        System.out.println(" " + MODULO_ESTUDIO + ". Gestión de Estudios");
        System.out.println(" " + SALIR + ". Salir del Sistema");
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
        } catch (NoSuchElementException e) {
            log.error("Error crítico: No hay más entrada disponible. Terminando aplicación...");
            System.exit(1);
            return -1;
        }
    }
}
