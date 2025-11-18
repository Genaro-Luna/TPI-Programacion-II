package service;

import config.DatabaseConnection;
import dao.CredencialAccesoDao;
import dao.UsuarioDao;
import java.util.List;
import models.Usuario;
import java.sql.Connection;
import models.CredencialAcceso;

public class UsuarioService extends BaseSv implements GenericService<Usuario>{
    private final UsuarioDao usuarioDao;
    private final CredencialAccesoDao credencialDao;

    public UsuarioService(UsuarioDao usuarioDao, CredencialAccesoDao credencialDao) {
        if(usuarioDao == null){
            throw new IllegalArgumentException("UsuarioDao no puede ser nulo");
        }
        if(credencialDao == null){
            throw new IllegalArgumentException("CredencialAccesoDao no puede ser nulo");
        }
        this.usuarioDao = usuarioDao;
        this.credencialDao = credencialDao;
    }

    public void crearUsuarioCompleto(Usuario user, CredencialAcceso credencial) throws Exception {
        // 1. Validaciones primero
        validarUsuario(user);
        if (credencial == null) {
            throw new IllegalArgumentException("La credencial no puede ser nula");
        }

        Connection conn = null;
        try {
            // 2. Iniciar la transacción manualmente
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 3. Ejecutar las operaciones (pasando la MISMA conexión)
            
            // Paso A: Crear el usuario (esto genera el ID)
            usuarioDao.crear(user, conn); 

            // Paso B: Asignar el ID generado a la credencial
            credencial.setId(user.getId());

            // Paso C: Crear la credencial
            credencialDao.crear(credencial, conn);

            // Paso D: Actualizar el usuario para vincular credencial_id
            user.setCredencial(credencial);
            usuarioDao.actualizar(user, conn);

            // 4. Si todo salió bien, confirmar
            conn.commit();

        } catch (Exception e) {
            // 5. Si algo falló, revertir
            if (conn != null) {
                conn.rollback();
            }
            // Propagar el error
            throw new Exception("Error en la transacción 'crearUsuarioCompleto': " + e.getMessage(), e);
        } finally {
            // 6. Cerrar y restaurar
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    /**
     * MÉTODO COMPUESTO (TRANSACCIONAL)
     * Elimina un Usuario y su Credencial en una sola transacción.
     */
    public void eliminarUsuarioCompleto(long id) throws Exception {
        validarId(id); // Validar el ID

        Connection conn = null;
        try {
            // 1. Iniciar la transacción manualmente
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 2. Ejecutar las operaciones (pasando la MISMA conexión)
            // Es importante eliminar la credencial primero (o la FK de usuario)
            // para evitar violaciones de integridad referencial.
            
            // Paso A: Eliminar la Credencial
            credencialDao.eliminar(id, conn);

            // Paso B: Eliminar el Usuario
            usuarioDao.eliminar(id, conn);
            
            // 3. Si todo salió bien, confirmar
            conn.commit();

        } catch (Exception e) {
            // 4. Si algo falló, revertir
            if (conn != null) {
                conn.rollback();
            }
            throw new Exception("Error en la transacción 'eliminarUsuarioCompleto': " + e.getMessage(), e);
        } finally {
            // 5. Cerrar y restaurar
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
    
    @Override
    public void crear(Usuario user) throws Exception {
        validarUsuario(user);
        ejecutarTransaccion(conn -> {
            usuarioDao.crear(user, conn);
        });
    }

    @Override
    public void actualizar(Usuario user) throws Exception {
        validarUsuario(user);
        validarId(user.getId());
        ejecutarTransaccion(conn -> {
            usuarioDao.actualizar(user, conn);
        });
    }

    @Override
    public void eliminar(long id) throws Exception {
         validarId(id);
         ejecutarTransaccion(conn -> {
            usuarioDao.eliminar(id, conn);
        });
    }

    @Override
    public Usuario getById(long id) throws Exception {
         validarId(id);
         return usuarioDao.getById(id);
    }

    @Override
    public List<Usuario> getAll() throws Exception {
        return usuarioDao.getAll();
    }
    
    
    //Validaciones
    public void validarUsuario(Usuario user){
        if(user == null){
            throw new IllegalArgumentException("ERROR: user no puede ser null");
        }
        if(user.getEmail() == null || user.getEmail().isBlank()){
            throw new IllegalArgumentException("ERROR: el campo 'Email' esta vacio");
        }
    }
    
    public void validarId(long id){
        if(id <= 0){
            throw new IllegalArgumentException("ERROR: el id no puede ser menor o igual a cero");
        }
    }
}
