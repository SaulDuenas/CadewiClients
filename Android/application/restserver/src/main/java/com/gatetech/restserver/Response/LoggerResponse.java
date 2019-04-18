package com.gatetech.restserver.Response;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoggerResponse {


    @SerializedName("Estado")
    @Expose
    private Integer estado;
    @SerializedName("Excepcion")
    @Expose
    private String excepcion;
    @SerializedName("Logger")
    @Expose
    private List<Logger> logger = null;

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

    public List<Logger> getLogger() {
        return logger;
    }

    public void setLogger(List<Logger> logger) {
        this.logger = logger;
    }

    static public class Logger {

        @SerializedName("log")
        @Expose
        private String log;
        @SerializedName("Correo")
        @Expose
        private String correo;
        @SerializedName("Tipo")
        @Expose
        private String tipo;
        @SerializedName("Categoria")
        @Expose
        private String categoria;
        @SerializedName("Dispositivo")
        @Expose
        private String dispositivo;
        @SerializedName("Mensaje")
        @Expose
        private String mensaje;
        @SerializedName("FechaHora")
        @Expose
        private String fechaHora;

        public String getLog() {
            return log;
        }

        public void setLog(String log) {
            this.log = log;
        }

        public String getCorreo() {
            return correo;
        }

        public void setCorreo(String correo) {
            this.correo = correo;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public String getCategoria() {
            return categoria;
        }

        public void setCategoria(String categoria) {
            this.categoria = categoria;
        }

        public String getDispositivo() {
            return dispositivo;
        }

        public void setDispositivo(String dispositivo) {
            this.dispositivo = dispositivo;
        }

        public String getMensaje() {
            return mensaje;
        }

        public void setMensaje(String mensaje) {
            this.mensaje = mensaje;
        }

        public String getFechaHora() {
            return fechaHora;
        }

        public void setFechaHora(String fechaHora) {
            this.fechaHora = fechaHora;
        }

    }

}
