package service;

import dao.UsuarioDao;
import java.util.List;
import models.Usuario;

public class UsuarioService implements GenericService<Usuario>{
    private final UsuarioDao usuarioDao;
    private final CredencialAccesoService credencialSv;

    public UsuarioService(UsuarioDao usuarioDao, CredencialAccesoService credencialSv) {
        if(usuarioDao == null){
            throw new IllegalArgumentException("UsuarioDao no puede ser nulo");
        }
        if(credencialSv == null){
            throw new IllegalArgumentException("UsuarioDao no puede ser nulo");
        }
        this.usuarioDao = usuarioDao;
        this.credencialSv = credencialSv;
    }

    
    @Override
    public void insertar(Usuario user) throws Exception {
        validarUsuario(user);
        validarId(user.getId());
        usuarioDao.crear(user);
    }

    @Override
    public void actualizar(Usuario user) throws Exception {
        validarUsuario(user);
        validarId(user.getId());
        usuarioDao.actualizar(user);
    }

    @Override
    public void eliminar(long id) throws Exception {
         validarId(id);
         usuarioDao.eliminar(id);
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
