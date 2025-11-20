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
     public void crear(Usuario entidad, Connection conn) throws Exception {
     String sql = "INSERT INTO usuario (username, email, activo, fechaRegistro, credencial_id) VALUES (?,?,?,?,?);";
    
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entidad.getUsuario());
            ps.setString(2, entidad.getEmail());
            ps.setBoolean(3, entidad.isActivo());
            ps.setTimestamp(4, Timestamp.valueOf(entidad.getFechaRegistro()));
        
            if (entidad.getCredencial() != null && entidad.getCredencial().getId() > 0) {
                ps.setLong(5, entidad.getCredencial().getId());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
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
        String sql = "SELECT * FROM usuario WHERE id = ? AND eliminado = FALSE";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            
            ps.setLong(1, id);
            
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()) {
                Usuario usu = new Usuario();
                usu.setId(rs.getLong("id"));
                usu.setEliminado(rs.getBoolean("eliminado"));
                usu.setUsuario(rs.getString("username"));
                usu.setEmail(rs.getString("email"));
                usu.setActivo(rs.getBoolean("activo"));
                usu.setFechaRegistro(rs.getTimestamp("fechaRegistro").toLocalDateTime());
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
        String sql = "SELECT * FROM usuario WHERE eliminado = FALSE";
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
                        c.setId(credencialId);
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
    public void actualizar(Usuario entidad, Connection conn) throws Exception {
        // Actualiza todos los campos excepto el ID
        String sql = "UPDATE usuario SET username = ?, email = ?, activo = ?, fechaRegistro = ?, credencial_id = ? WHERE id = ?;";
    
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

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
    public void eliminar(long id, Connection conn) throws Exception {
        String sql = "UPDATE usuario SET eliminado = TRUE WHERE id = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
             ps.setLong(1, id);
             ps.executeUpdate();
             
        } catch (SQLException e) {
            throw new SQLException("No se pudo elimnar el usuario con ID " + id + ":" + e.getMessage(), e );
        }
    }
}
