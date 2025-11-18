
package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/tpi"; // URL para conectar la base de datos MySQL y la base llamada tpi
    private static final String USER = "root"; // Nombre del usuario
    private static final String PASS = ""; // Contraseña del usuario
    
    // Bloque estático para cargar el driver JDBC una sola vez al iniciar la clase
    static {
        try {
            // Se registra el driver MySQL en el DriverManager ----> mysql-connector-j-8.4.0-jar
            // El driver deberia estar en la carpeta Libraries
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException driver) {
            // Si no se encuentra el driver, lanza excepción en tiempo de ejecución
            throw new RuntimeException("Error: no se encontro el driver, ", driver);
        }
    }
    public static Connection getConnection() throws SQLException {
        // Validación simple para evitar URLs o credenciales vacías
        if (URL == null || URL.isEmpty() || USER == null || USER.isEmpty() || PASS == null) {
            throw new SQLException("Configuración de la base de datos incompleta o inválida");
        }
        // Si la configuracion de la base de datos esta todo correcto,
        // se obtendria la conexion con el DriverManager usando la URL, USER, PASS
        return DriverManager.getConnection(URL, USER, PASS);
    }
    
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error cerrando conexión: " + e.getMessage());
            }
        }
    }
}



