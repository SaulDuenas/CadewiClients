package com.gatetech.restserver.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class photoResponse {

    @SerializedName("Estado")
    @Expose
    private Integer estado;
    @SerializedName("Excepcion")
    @Expose
    private String excepcion;
    @SerializedName("Fotos")
    @Expose
    private List<photoItem> photoItems = null;

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

    public List<photoItem> getPhotoItems() {
        return photoItems;
    }

    public void setPhotoItems(List<photoItem> photoItems) {
        this.photoItems = photoItems;
    }



    public class photoItem {

        @SerializedName("Imagen")
        @Expose
        private String imagen;
        @SerializedName("Direccion")
        @Expose
        private String direccion;
        @SerializedName("Cliente")
        @Expose
        private String cliente;
        @SerializedName("Bynary")
        @Expose
        private Object bynary;
        @SerializedName("Nombre")
        @Expose
        private String nombre;
        @SerializedName("Ruta")
        @Expose
        private String ruta;
        @SerializedName("Nota")
        @Expose
        private String nota;
        @SerializedName("FechaHora")
        @Expose
        private String fechaHora;

        public String getImagen() {
            return imagen;
        }

        public void setImagen(String imagen) {
            this.imagen = imagen;
        }

        public String getDireccion() {
            return direccion;
        }

        public void setDireccion(String direccion) {
            this.direccion = direccion;
        }

        public String getCliente() {
            return cliente;
        }

        public void setCliente(String cliente) {
            this.cliente = cliente;
        }

        public Object getBynary() {
            return bynary;
        }

        public void setBynary(Object bynary) {
            this.bynary = bynary;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getRuta() {
            return ruta;
        }

        public void setRuta(String ruta) {
            this.ruta = ruta;
        }

        public String getNota() {
            return nota;
        }

        public void setNota(String nota) {
            this.nota = nota;
        }

        public String getFechaHora() {
            return fechaHora;
        }

        public void setFechaHora(String fechaHora) {
            this.fechaHora = fechaHora;
        }

    }

}
