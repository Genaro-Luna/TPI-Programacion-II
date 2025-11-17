package dao;

import java.sql.Connection;
import config.DatabaseConnection;
import models.CredencialAcceso;
import java.sql.Statement;
import java.util.List;
import java.sql.*;
import java.util.ArrayList;

public class CredencialAccesoDao implements GenericDao<CredencialAcceso>{
    // Inserta una nueva credencial a la BD
    @Override
    public void crear(CredencialAcceso entidad) throws Exception {
        String sql = "INSERT INTO credencialAcceso (id, hashPassword, salt, ultimoCambio, requiereReset) VALUES (?,?,?,?,?)";
        
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setLong(1, entidad.getId());
            ps.setString(2, entidad.getHashPassword());
            ps.setString(3, entidad.getSalt());
            ps.setTimestamp(4, Timestamp.valueOf(entidad.getUltimoCambio()));
            ps.setBoolean(5, entidad.isRequiereReset());
            
            ps.executeUpdate();
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()){
                if (generatedKeys.next()) {
                    entidad.setId(generatedKeys.getLong(1));
                    System.out.println("Credencial insertado con id: " + entidad.getId());
                } else {
                    throw new SQLException("La insercion de la credencial fallo");
                }
            }
        } 
    }

    
    @Override
    public CredencialAcceso getById(long id) throws Exception {
        String sql = "SELECT id, hashPassword, salt, ultimoCambio, requiereReset FROM credencialAcceso WHERE id = ?";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return new CredencialAcceso(
                        rs.getLong("id"),
                        rs.getString("hashPassword"), 
                        rs.getString("salt"),             
                        rs.getTimestamp("ultimoCambio").toLocalDateTime(), 
                        rs.getBoolean("requiereReset")
                );
            }
        } catch(SQLException e ) {
            throw new Exception("Error al obtener la credencial por ID: " + e.getMessage(), e);
        }
        return null;
    }  
            
    
    @Override
    public List<CredencialAcceso> getAll() throws Exception {
        String sql = "SELECT id, hashPassword, salt, ultimoCambio, requiereReset FROM credencialAcceso WHERE eliminado = FALSE";
        
        try(Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement()) {
            ResultSet rs =  stmt.executeQuery(sql);
            
            List<CredencialAcceso> credenciales = new ArrayList<>();
            
            while(rs.next()) {
                 credenciales.add(new CredencialAcceso(
                         rs.getLong("id"),
                         rs.getString("hashPassword"), 
                         rs.getString("salt"),       
                         rs.getTimestamp("ultimoCambio").toLocalDateTime(), 
                         rs.getBoolean("requiereReset")
                 ));     
            }
            return credenciales;
        }
    }

    @Override
    public void actualizar(CredencialAcceso entidad) throws Exception {
        String sql = "UPDATE credencialAcceso SET hashPassword = ?, salt = ? ultimoCambio = ?, requiereReset = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, entidad.getHashPassword());
            ps.setString(2, entidad.getSalt());
            ps.setTimestamp(3, Timestamp.valueOf(entidad.getUltimoCambio()));
            ps.setBoolean(4, entidad.isRequiereReset());
            ps.setLong(5, entidad.getId());
            
            int filas = ps.executeUpdate();
            if(filas == 0) {
            throw new SQLException("No existe credencial con ID: " + entidad.getId());
            }    
        } 
    }

    @Override
    public void eliminar(long id) throws Exception {
        String sql = "UPDATE credencialAcceso SET eliminado = TRUE WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            
            ps.setLong(1, id);
            int filas = ps.executeUpdate();
            if(filas == 0) {
                throw new SQLException("No existe credencial con ID: " + id);
            }
        }
    }
}
    