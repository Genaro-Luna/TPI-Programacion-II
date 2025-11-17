
package service;

import dao.GenericDao;
import java.util.List;
import models.CredencialAcceso;


public class CredencialAccesoService implements GenericService<CredencialAcceso>{
    private final GenericDao<CredencialAcceso> credencialDao;

    public CredencialAccesoService(GenericDao<CredencialAcceso> credencialDao) {
        this.credencialDao = credencialDao;
    }
    
    @Override
    public void insertar(CredencialAcceso credencial) throws Exception {
        if(credencial.getId() <= 0){
            throw new IllegalArgumentException("El codigo no puede ser menor o igual a cero");
        }
        System.out.println("Creando nueva credencial: " + credencial.getId());
        credencialDao.insert(credencial)
    }

    @Override
    public void actualizar(CredencialAcceso entidad) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void eliminar(long id) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CredencialAcceso getById(long id) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<CredencialAcceso> getAll() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
