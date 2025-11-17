
package models;

import java.time.LocalDateTime;

public class Usuario extends Base{
    // Atributos de la clase Usuario
    private boolean eliminado;
    private String email;
    private String usuario;
    private boolean activo;
    private LocalDateTime fechaRegistro;
    private CredencialAcceso credencial;
    
    //Constructores
    public Usuario(long id, String email, String usuario, boolean activo, LocalDateTime fechaRegistro, CredencialAcceso credencial) {
        super(id, false);
        this.email = email;
        this.usuario = usuario;
        this.activo = activo;
        this.fechaRegistro = fechaRegistro;
        this.credencial = credencial;
    }
    
    public Usuario() {
        super();
    }
    
    // Getters y setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public CredencialAcceso getCredencial() {
        return credencial;
    }

    public void setCredencial(CredencialAcceso credencial) {
        this.credencial = credencial;
    }

    @Override
    public String toString() {
        return "Usuario{" + "id=" + getId() + ", eliminado=" + eliminado + ", email=" + email + ", usuario=" + usuario + ", activo=" + activo + 
                            ", fechaRegistro=" + fechaRegistro + ", credencial=" + credencial + '}';
    }
   
}
