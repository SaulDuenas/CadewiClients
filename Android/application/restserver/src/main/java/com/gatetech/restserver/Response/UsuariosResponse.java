package com.gatetech.restserver.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class UsuariosResponse {

    @SerializedName("Estado")
    @Expose
    private Integer estado;
    @SerializedName("Excepcion")
    @Expose
    private String excepcion;
    @SerializedName("Usuarios")
    @Expose
    private List<Usuario> usuarios = null;

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public String getExcepcion() {
        return excepcion;
    }

    public void setExcepcion(String excepcion) {
        this.excepcion = excepcion;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    /**
     * A Usuario item representing a piece of content.
     */

    static public class Usuario {

        @SerializedName("Correo")
        @Expose
        private String correo;
        @SerializedName("Nombre")
        @Expose
        private String nombre;
        @SerializedName("Apellidos")
        @Expose
        private String apellidos;
        @SerializedName("Password")
        @Expose
        private String password;
        @SerializedName("SesionKey")
        @Expose
        private Object sesionKey;
        @SerializedName("FechaHora")
        @Expose
        private String fechaHora;
        @SerializedName("Eliminado")
        @Expose
        private String eliminado;
        @SerializedName("Usuario")
        @Expose
        private String usuario;
        @SerializedName("Perfil")
        @Expose
        private Object perfil;

        public String getCorreo() {
            return correo;
        }

        public void setCorreo(String correo) {
            this.correo = correo;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getApellidos() {
            return apellidos;
        }

        public void setApellidos(String apellidos) {
            this.apellidos = apellidos;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Object getSesionKey() {
            return sesionKey;
        }

        public void setSesionKey(Object sesionKey) {
            this.sesionKey = sesionKey == null?"":sesionKey.toString();
        }

        public String getFechaHora() {
            return fechaHora;
        }


        public void setFechaHora(String fechaHora) {

                this.fechaHora =   "";
        }

        public String getEliminado() {
            return eliminado;
        }

        public void setEliminado(String eliminado) {
            this.eliminado = eliminado;
        }

        public String getUsuario() {
            return usuario;
        }

        public void setUsuario(String usuario) {
            this.usuario = usuario;
        }

        public Object getPerfil() {
            return perfil;
        }

        public void setPerfil(Object perfil) {
            this.perfil =  perfil == null?"":perfil.toString();
        }

    }

}
