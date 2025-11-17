
package dao;

import java.util.List;
import java.sql.Connection;

public interface GenericDao<T> {
    
    public void crear(T entidad) throws Exception;

    public T getById(long id) throws Exception;

    public List<T> getAll() throws Exception;

    public void actualizar(T entidad) throws Exception;

    public void eliminar(long id) throws Exception;
    
    public void recuperar(long id) throws Exception;
}

