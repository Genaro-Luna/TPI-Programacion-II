/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;


import config.DatabaseConnection;
import java.sql.Connection;
import utils.OperacionTransaccion;

public abstract class BaseSv {
    // Dentro de tu clase CredencialAccesoService
    protected void ejecutarTransaccion(OperacionTransaccion operacion) throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // 1. Inicia la transacción
            // 2. EJECUTA LA OPERACIÓN ESPECÍFICA
            // (La que recibimos por parámetro)
            operacion.ejecutar(conn); 
            conn.commit(); // 3. Si todo va bien, confirma
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback(); // 4. Si algo falla, revierte
            }
            // Propagamos el error como una excepción general de servicio
            throw new Exception("Error en la transacción: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true); // 5. Restablece el auto-commit
                conn.close(); // 6. Cierra la conexión
            }
        }
    }
}
