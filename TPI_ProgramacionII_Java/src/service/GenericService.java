
package service;

import java.util.List;

public interface GenericService<T>{
    void crear(T entidad) throws Exception;

    void actualizar(T entidad) throws Exception;

    void eliminar(long id) throws Exception;

    T getById(long id) throws Exception;

    List<T> getAll() throws Exception;
}

