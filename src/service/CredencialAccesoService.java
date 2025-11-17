
package service;

import dao.GenericDao;
import java.util.List;
import models.CredencialAcceso;


public class CredencialAccesoService implements GenericService<CredencialAcceso>{
    private final GenericDao<CredencialAcceso> credencialDao;

    public CredencialAccesoService(GenericDao<CredencialAcceso> credencialDao) {
        if(credencialDao == null){
            throw new IllegalArgumentException("El domicilio no puede ser nulo");
        }
        this.credencialDao = credencialDao;
    }
    
    @Override
    public void insertar(CredencialAcceso credencial) throws Exception {
        validarCredencial(credencial);
        credencialDao.crear(credencial);
    }

    @Override
    public void actualizar(CredencialAcceso credencial) throws Exception {
        validarCredencial(credencial);
        credencialDao.actualizar(credencial);
    }

    @Override
    public void eliminar(long id) throws Exception {
        validarID(id);
        credencialDao.eliminar(id);
    }

    @Override
    public CredencialAcceso getById(long id) throws Exception {
        validarID(id);
        return credencialDao.getById(id);
    }

    @Override
    public List<CredencialAcceso> getAll() throws Exception {
        return credencialDao.getAll();
    }
    
    public void validarCredencial(CredencialAcceso credencial){
        if(credencial == null){
            throw new IllegalArgumentException("ERROR: La credencial no puede ser nula");
        }
        if(credencial.getId() <= 0){
            throw new IllegalArgumentException("ERROR: ID credencial menor o igual a cero");
        }
    }
    
    public void validarID(long id){
        if(id <= 0){
            throw new IllegalArgumentException("ERROR: el id debe ser mayor a cero");
        }
    }
}
