
package models;

import java.time.LocalDateTime;

public class CredencialAcceso extends Base{
    
    // Atributos de la clase CredencialAcceso
    private boolean eliminado;
    private String hashPassword;
    private String salt;
    private LocalDateTime ultimoCambio;
    private boolean requiereReset;
    
    //Constructores
    public CredencialAcceso(int id, String hashPassword, String salt, LocalDateTime ultimoCambio, boolean requiereReset) {
        super(id, false);
        this.hashPassword = hashPassword;
        this.salt = salt;
        this.ultimoCambio = ultimoCambio;
        this.requiereReset = requiereReset;
    }
    
    public CredencialAcceso() {
        super();
    }
    
    // Getters y setters
    public String getHashPassword() {
        return hashPassword;
    }

    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public LocalDateTime getUltimoCambio() {
        return ultimoCambio;
    }

    public void setUltimoCambio(LocalDateTime ultimoCambio) {
        this.ultimoCambio = ultimoCambio;
    }

    public boolean isRequiereReset() {
        return requiereReset;
    }

    public void setRequiereReset(boolean requiereReset) {
        this.requiereReset = requiereReset;
    }
    
    @Override
    public String toString() {
        return "CredencialAcceso{" + "id=" + getId() + ", eliminado=" + eliminado + ", hashPassword=" + hashPassword + ", salt=" + salt 
                                   + ", ultimoCambio=" + ultimoCambio + ", requiereReset=" + requiereReset + '}';
    }
    
    
}
