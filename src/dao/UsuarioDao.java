package dao;

import config.DatabaseConnection;
import models.CredencialAcceso;
import models.Usuario;
import java.util.List;
import java.sql.*;
import java.sql.Connection;
import java.util.ArrayList;

public class UsuarioDao implements GenericDao<Usuario>{
    // Insertar o crear un nuevo entidad
    @Override
     public void crear(Usuario entidad) throws Exception {
     String sql = "INSERT INTO usuario (username, email, activo, fechaRegistro, credencial_id) VALUES (?,?,?,?,?,?);";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, entidad.getId());
            ps.setString(2, entidad.getUsuario());
            ps.setString(3, entidad.getEmail());
            ps.setBoolean(4, entidad.isActivo());
            ps.setTimestamp(5, Timestamp.valueOf(entidad.getFechaRegistro()));
        
            if (entidad.getCredencial() != null && entidad.getCredencial().getId() > 0) {
                ps.setLong(6, entidad.getCredencial().getId());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                entidad.setId(generatedKeys.getLong(1));
                System.out.println("Usuario insertado con ID: " + entidad.getId());
                } else {
                throw new SQLException("La inserción del usuario falló: no se generó ID.");
                }
            }
        }
    }
     
    //Buscar por ID y mostrar
    @Override
    public Usuario getById(long id) throws Exception {
        // Consulta con JOIN para obtener un usu y su credencial
        String sql = "SELECT usu.id, usu.username, usu.credencial_id " + 
                     "FROM usuario usu "+ 
                     "LEFT JOIN credencialAcceso c ON usu.credencial_id = c.id " +
                     "WHERE usu.id = ?";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            
            ps.setLong(1, id);
            
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()) {
                Usuario usu = new Usuario();
                usu.setId(rs.getLong("id"));
                usu.setUsuario(rs.getString("username"));
                long credencialId = rs.getLong("credencial_id");  
                    if (credencialId > 0) {
                        CredencialAcceso c =  new CredencialAcceso();
                        c.setId(credencialId);
                        usu.setCredencial(c);
                    } else {
                        usu.setCredencial(null);
                    }
                return usu;
                }
            }
        } catch(SQLException e ) {
            throw new Exception("Error al obtener el usuario por ID: " + e.getMessage(), e);
        }
        return null;
    }
    
    //Mostrar todo
    @Override
    public List<Usuario> getAll() throws Exception {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            
            while(rs.next()){
                Usuario usu = new Usuario();
                usu.setId(rs.getLong("id"));
                usu.setEliminado(rs.getBoolean("eliminado"));
                usu.setUsuario(rs.getString("username"));
                usu.setEmail(rs.getString("email"));
                usu.setActivo(rs.getBoolean("activo"));
                // Convertimos a la fecha local
                usu.setFechaRegistro(rs.getTimestamp("fechaRegistro").toLocalDateTime());
                long credencialId = rs.getLong("credencial_id");  
                    if (credencialId > 0) {
                        CredencialAcceso c = new CredencialAcceso();
                        c.setId(rs.getLong("id"));
                        usu.setCredencial(c);
                    } else {
                        usu.setCredencial(null);
                    }
                
                usuarios.add(usu);
            }
        }catch (SQLException e) {
                System.out.println("Error al leer usuarios: " + e.getMessage());
                e.printStackTrace();
        
        }
        return usuarios;
    }
    
    //Actualizar registro
    @Override
    public void actualizar(Usuario entidad) throws Exception {
        // Actualiza todos los campos excepto el ID
        String sql = "UPDATE usuario SET username = ?, email = ?, activo = ?, fechaRegistro = ?, credencial_id = ? WHERE id = ?;";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, entidad.getUsuario());
            ps.setString(2, entidad.getEmail());
            ps.setBoolean(3, entidad.isActivo());
            ps.setTimestamp(4, Timestamp.valueOf(entidad.getFechaRegistro()));

            if (entidad.getCredencial() != null && entidad.getCredencial().getId() > 0) {
                ps.setLong(5, entidad.getCredencial().getId());
            } else {
            ps.setNull(5, java.sql.Types.INTEGER);
            }   

            ps.setLong(6, entidad.getId());

            int filas = ps.executeUpdate();

            if (filas == 0) {
                    throw new SQLException("No se encontró usuario con ID: " + entidad.getId());
            }

        } catch (SQLException e) {
          throw new SQLException("No se pudo actualizar el usuario: " + e.getMessage(), e);
          }
    }
    
    //Eliminador logico de usuario por id
    @Override
    public void eliminar(long id) throws Exception {
        String sql = "UPDATE usuario SET eliminado = TRUE WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             ps.setLong(1, id);
             ps.executeUpdate();
             
        } catch (SQLException e) {
            throw new SQLException("No se pudo elimnar el usuario con ID " + id + ":" + e.getMessage(), e );
        }
    }
    
    @Override
    public void recuperar(long id) throws Exception{
        String sql = "UPDATE usuario SET eliminado = FALSE WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             ps.setLong(1, id);
             ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("No se pudo recuperar el usuario con ID " + id + ":" + e.getMessage(), e );
        }
    }
}
