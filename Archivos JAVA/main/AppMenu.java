/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package main;

import dao.CredencialAccesoDao;
import dao.UsuarioDao;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import models.CredencialAcceso;
import models.Usuario;
import service.CredencialAccesoService;
import service.UsuarioService;

public class AppMenu {

    /**
     * @param args the command line arguments
     */
    
    private final UsuarioService usuarioService;
    private final CredencialAccesoService credencialService;
    private final Scanner scanner;
    
    public static void main(String[] args) {
    //Configuración de Dependencias (DAO)
        UsuarioDao usuarioDao = new UsuarioDao();
        CredencialAccesoDao credencialDao = new CredencialAccesoDao();
        
        //Configuración de Servicios
        CredencialAccesoService credencialService = new CredencialAccesoService(credencialDao);
        UsuarioService usuarioService = new UsuarioService(usuarioDao, credencialDao);
        
        //Creamos el scan
        Scanner scanner = new Scanner(System.in);
        
        //Instanciación e inicio de la App
        AppMenu app = new AppMenu(usuarioService, credencialService, scanner);
        app.mostrarMenu();
        
        //Cerramos el scan cuando finalice
        scanner.close();
    }
    //Constructor para la inyeccion
    public AppMenu(UsuarioService usuarioService, CredencialAccesoService credencialService, Scanner scanner) {
        this.usuarioService = usuarioService;
        this.credencialService = credencialService;
        this.scanner = scanner;
    }

    //Muestra el menú principal y maneja el bucle de interacción. 
    public void mostrarMenu() {
        while (true) {
            System.out.println("\n--- MENÚ PRINCIPAL (TFI) ---");
            System.out.println("--- Gestión de Usuarios ---");
            System.out.println("1. Crear Usuario (con Credencial)");
            System.out.println("2. Consultar Usuario por ID");
            System.out.println("3. Listar todos los Usuarios");
            System.out.println("4. Actualizar datos de Usuario");
            System.out.println("5. Eliminar Usuario (con Credencial)");
            System.out.println("--- Gestión de Credenciales ---");
            System.out.println("6. Actualizar Credencial (Resetear Password)");
            System.out.println("--- Búsquedas ---");
            System.out.println("7. Buscar Usuario por Username");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                // Leemos la opción como un long para robustez
                long opcion = leerLong(""); 

                switch ((int) opcion) {
                    case 1:
                        crearUsuario();
                        break;
                    case 2:
                        consultarUsuarioPorId();
                        break;
                    case 3:
                        listarUsuarios();
                        break;
                    case 4:
                        actualizarUsuario();
                        break;
                    case 5:
                        eliminarUsuario();
                        break;
                    case 6:
                        actualizarCredencial();
                        break;
                    case 7:
                        buscarUsuarioPorUsername();
                        break;
                    case 0:
                        System.out.println("Saliendo de la aplicación...");
                        return; //Sale del método y finaliza el bucle
                    default:
                        System.out.println("Opción no válida. Intente de nuevo.");
                }
            } catch (Exception e) {
                //Captura centralizada de todos los errores (Validación, BD, Parseo)
                System.err.println("\n--- ERROR ---");
                System.err.println("Ha ocurrido un error: " + e.getMessage());
                System.err.println("Volviendo al menú principal...");
            }
        }
    }

    //-Métodos de Usuario

    private void crearUsuario() throws Exception {
        System.out.println("\n--- 1. Crear Nuevo Usuario ---");
        
        //Pedimos los datos del Usuario
        String username = leerString("Ingrese Username:");
        String email = leerString("Ingrese Email:");
        // (En una app real, el password no se debe mostrar)
        String password = leerString("Ingrese Password:");

        //Preparamos los objetos
        Usuario user = new Usuario();
        user.setUsuario(username);
        user.setEmail(email);
        user.setActivo(true);
        user.setFechaRegistro(LocalDateTime.now());
        
        CredencialAcceso credencial = new CredencialAcceso();
        credencial.setSalt("SALT_PLACEHOLDER");
        credencial.setHashPassword(password);
        credencial.setUltimoCambio(LocalDateTime.now());
        credencial.setRequiereReset(false);
        
        usuarioService.crearUsuarioCompleto(user, credencial);
        System.out.println("\n¡ÉXITO: Transacción completada!");
        System.out.println("Usuario y Credencial creados con ID: " + user.getId());
    }

    private void consultarUsuarioPorId() throws Exception {
        System.out.println("\n--- 2. Consultar Usuario por ID ---");
        long id = leerLong("Ingrese el ID del Usuario:");
        
        Usuario user = usuarioService.getById(id);
        
        //Validamos si el id existe 
        if (user == null) {
            System.out.println("ERROR: No se encontró ningún Usuario con ID: " + id);
            return;
        }
        
        //Imprimimos el usuario
        System.out.println("Usuario encontrado:");
        System.out.println(user);
        
        //Aca podemos ofrecer ver la credencial completa
        if (user.getCredencial() != null) {
            String verCred = leerString("¿Desea ver los detalles de la Credencial? (S/N):");
            if (verCred.equalsIgnoreCase("S")) {
                //Hacemos la segunda consulta para traer el objeto completo
                CredencialAcceso cred = credencialService.getById(user.getCredencial().getId());
                if (cred != null) {
                    System.out.println("Detalles de la Credencial:");
                    System.out.println(cred);
                } else {
                    //Esto solo ocurre si la BD es inconsistente
                    System.out.println("No se encontraron detalles de la credencial (ID: " + user.getCredencial().getId() + ")");
                }
            }
        }
    }
    
    //Con esto podemos listar todos los usuarios
    private void listarUsuarios() throws Exception {
        System.out.println("\n--- 3. Listar todos los Usuarios ---");
        List<Usuario> usuarios = usuarioService.getAll();
        
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }
        
        //Iteramos e imprimimos todos los usuarios
        for (Usuario user : usuarios) {
            System.out.println(user);
            System.out.println("-----");
        }
    }

    //Actualizamos los usuarios con este metodo
    private void actualizarUsuario() throws Exception {
        System.out.println("\n--- 4. Actualizar datos de Usuario ---");
        long id = leerLong("Ingrese el ID del Usuario a actualizar:");
        
        Usuario user = usuarioService.getById(id);
        if (user == null) {
            throw new Exception("No se encontró usuario con ID: " + id);
        }//Dejamos esta validacion si el id ingresado no existe
        //Mostramos los datos
        System.out.println("Datos actuales: " + user);
        
        //Ahora solicitamos los datos nuevos
        String nuevoUsername = leerString("Ingrese nuevo Username (actual: " + user.getUsuario() + "):");
        String nuevoEmail = leerString("Ingrese nuevo Email (actual: " + user.getEmail() + "):");
        
        //Los seteamos y enviamos el objeto completo al servicio
        user.setUsuario(nuevoUsername);
        user.setEmail(nuevoEmail);
        usuarioService.actualizar(user);
        
        System.out.println("\n¡ÉXITO: Usuario actualizado!");
        System.out.println(user);
    }
    
    //Este metodo es el eliminado logico de usuario
    private void eliminarUsuario() throws Exception {
        System.out.println("\n--- 5. Eliminar Usuario (Baja Lógica) ---");
        long id = leerLong("Ingrese el ID del Usuario a eliminar:");
        
        //Validamos que existe antes de intentar borrar
        Usuario user = usuarioService.getById(id);
        if (user == null) {
            throw new Exception("No se encontró usuario con ID: " + id);
        }

        //Nos aseguramos de que se está eligiendo el usuario correcto y que se desea eliminarlo
        System.out.println("Se eliminará (baja lógica) al usuario: " + user);
        String confirma = leerString("¿Está seguro? (S/N):");
        if (!confirma.equalsIgnoreCase("S")) {
            System.out.println("Eliminación cancelada.");
            return;
        }
        
        usuarioService.eliminarUsuarioCompleto(id);
        System.out.println("\n¡ÉXITO: Usuario y Credencial eliminados (lógicamente)!");
    }



    //Métodos de Credencial
    private void actualizarCredencial() throws Exception {
        System.out.println("\n--- 6. Actualizar Credencial (Reset Password) ---");
        long id = leerLong("Ingrese el ID del USUARIO cuya credencial desea actualizar:");
        
        Usuario user = usuarioService.getById(id);
        if (user == null) {
            throw new Exception("No se encontró usuario con ID: " + id);
        }
        if (user.getCredencial() == null) {
            throw new Exception("El usuario " + user.getUsuario() + " no tiene una credencial asignada.");
        }
        
        //Pedimos la nueva contraseña
        String nuevoPassword = leerString("Ingrese el NUEVO Password:");

        //Obtenemos la credencial completa
        CredencialAcceso credencial = credencialService.getById(user.getCredencial().getId());
        if (credencial == null) {
            //Acá tamben dejamos esta validacion si llega haber una inconsistencia del ladod e la BD
            throw new Exception("Error: El usuario está vinculado a una credencial (ID " + user.getCredencial().getId() + ") que no existe.");
        }
        
        //Actualizamos los datos
        credencial.setHashPassword(nuevoPassword);
        credencial.setSalt("NEW_SALT_PLACEHOLDER");
        credencial.setUltimoCambio(LocalDateTime.now());
        credencial.setRequiereReset(false);
        
        credencialService.actualizar(credencial);
        
        System.out.println("\n¡ÉXITO: Password del usuario " + user.getUsuario() + " actualizado!");
    }

    //Métodos de Búsqueda
    private void buscarUsuarioPorUsername() throws Exception {
        System.out.println("\n--- 7. Buscar Usuario por Username ---");
        String username = leerString("Ingrese el Username a buscar:");
        
        List<Usuario> todosLosUsuarios = usuarioService.getAll();
        
        //Filtrar en memoria (Java Stream)
        //-.filter() busca coincidencias
        //-.findFirst() detiene la búsqueda en la primera
        Optional<Usuario> usuarioEncontrado = todosLosUsuarios.stream()
                .filter(user -> user.getUsuario().equalsIgnoreCase(username))
                .findFirst();
        
        //Mostramos los resultados
        if (usuarioEncontrado.isPresent()) {
            System.out.println("Usuario encontrado:");
            System.out.println(usuarioEncontrado.get());
        } else {
            System.out.println("No se encontró ningún usuario con el username: " + username);
        }
    }

    
    //Metodos de ayuda
    //-Este metodo lee el string para saber si está vacío o no
    private String leerString(String mensaje) {
        System.out.println(mensaje);
        String input = scanner.nextLine();
        if (input == null || input.isBlank()) {
            // Convierte las entradas a mayúsculas donde aplica (TFI [cite: 89])
            // En este caso, solo validamos que no esté vacío.
            System.out.println("La entrada no puede estar vacía.");
            return leerString(mensaje); // Vuelve a preguntar
        }
        return input;
    }

    //Validar que se ingreso un numero
    private long leerLong(String mensaje) throws Exception {
        if (!mensaje.isEmpty()) {
            System.out.println(mensaje);
        }
        
        String input = scanner.nextLine();
        
        try {
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            throw new Exception("Entrada inválida, se esperaba un número.");
        }
    }
}