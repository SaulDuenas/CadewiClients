package com.gatetech.restserver.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ClientesResponse {

    @SerializedName("Estado")
    @Expose
    private Integer estado;
    @SerializedName("Excepcion")
    @Expose
    private String excepcion;
    @SerializedName("Clientes")
    @Expose
    private List<Cliente> clientes = null;

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

    public List<Cliente> getClientes() {
        return clientes;
    }


    public Cliente getCliente(String clientId) {

        for (Cliente cln: clientes) {

            if (cln.cliente.equals(clientId) ) {
                return cln;
            }


        }

        return null;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }


    static public class Cliente {

        @SerializedName("Cliente")
        @Expose
        private String cliente;
        @SerializedName("Rfc")
        @Expose
        private String rfc;
        @SerializedName("Nombre")
        @Expose
        private String nombre;
        @SerializedName("FechaHora")
        @Expose
        private String fechaHora;
        @SerializedName("CorreoAgente")
        @Expose
        private String correoAgente;
        @SerializedName("Eliminado")
        @Expose
        private String eliminado;
        @SerializedName("Direccion")
        @Expose
        private List<Direccion> direccion = null;
        @SerializedName("Contacto")
        @Expose
        private List<Contacto> contacto = null;


        public String getCliente() {
            return cliente;
        }

        public void setCliente(String cliente) {
            this.cliente = cliente;
        }

        public String getRfc() {
            return rfc;
        }

        public void setRfc(String rfc) {
            this.rfc = rfc;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getFechaHora() {
            return fechaHora;
        }

        public void setFechaHora(String fechaHora) {
            this.fechaHora = fechaHora;
        }

        public String getCorreoAgente() {
            return correoAgente;
        }

        public void setCorreoAgente(String correoAgente) {
            this.correoAgente = correoAgente;
        }

        public String getEliminado() {
            return eliminado;
        }

        public void setEliminado(String eliminado) {
            this.eliminado = eliminado;
        }

        public List<Direccion> getDireccion() { return direccion; }

        public void setDireccion(List<Direccion> direccion) { this.direccion = direccion; }

        public List<Contacto> getContacto() { return contacto;  }

        public void setContacto(List<Contacto> contacto) { this.contacto = contacto; }

    }


    static public class Contacto {

        @SerializedName("Contacto")
        @Expose
        private String contacto;
        @SerializedName("Cliente")
        @Expose
        private String cliente;
        @SerializedName("Tipo")
        @Expose
        private String tipo;
        @SerializedName("Valor")
        @Expose
        private String valor;
        @SerializedName("FechaHora")
        @Expose
        private String fechaHora;

        public String getContacto() {
            return contacto;
        }

        public void setContacto(String contacto) {
            this.contacto = contacto;
        }

        public String getCliente() {
            return cliente;
        }

        public void setCliente(String cliente) {
            this.cliente = cliente;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public String getValor() {
            return valor;
        }

        public void setValor(String valor) {
            this.valor = valor;
        }

        public String getFechaHora() {
            return fechaHora;
        }

        public void setFechaHora(String fechaHora) {
            this.fechaHora = fechaHora;
        }

    }

    static public class Direccion {

        @SerializedName("Direccion")
        @Expose
        private String direccion;
        @SerializedName("Cliente")
        @Expose
        private String cliente;
        @SerializedName("Pais")
        @Expose
        private String pais;
        @SerializedName("CodigoPostal")
        @Expose
        private String codigoPostal;
        @SerializedName("Calle")
        @Expose
        private String calle;
        @SerializedName("Municipio")
        @Expose
        private String municipio;
        @SerializedName("Estado")
        @Expose
        private String estado;
        @SerializedName("Ciudad")
        @Expose
        private String ciudad;
        @SerializedName("Colonia")
        @Expose
        private String colonia;
        @SerializedName("Detalles")
        @Expose
        private Object detalles;
        @SerializedName("Longitud")
        @Expose
        private Object longitud;
        @SerializedName("Latitud")
        @Expose
        private Object latitud;
        @SerializedName("LocEditable")
        @Expose
        private Object locEditable;
        @SerializedName("FechaHora")
        @Expose
        private String fechaHora;

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

        public String getPais() {
            return pais;
        }

        public void setPais(String pais) {
            this.pais = pais;
        }

        public String getCodigoPostal() {
            return codigoPostal;
        }

        public void setCodigoPostal(String codigoPostal) {
            this.codigoPostal = codigoPostal;
        }

        public String getCalle() {
            return calle;
        }

        public void setCalle(String calle) {
            this.calle = calle;
        }

        public String getMunicipio() {
            return municipio;
        }

        public void setMunicipio(String municipio) {
            this.municipio = municipio;
        }

        public String getEstado() {
            return estado;
        }

        public void setEstado(String estado) {
            this.estado = estado;
        }

        public String getCiudad() {
            return ciudad;
        }

        public void setCiudad(String ciudad) {
            this.ciudad = ciudad;
        }

        public String getColonia() {
            return colonia;
        }

        public void setColonia(String colonia) {
            this.colonia = colonia;
        }

        public Object getDetalles() {
            return this.detalles == null?"":this.detalles;

        }

        public void setDetalles(Object detalles) {
            this.detalles = detalles;
        }

        public Object getLongitud() {
            return (this.longitud == null?"":this.longitud);
        }

        public void setLongitud(Object longitud) { this.longitud = longitud; }

        public Object getLatitud() { return   (this.latitud == null)?"":this.latitud; }

        public void setLatitud(Object latitud) {
            this.latitud = latitud;
        }

        public Object getLocEditable() { return (this.locEditable == null)?"":this.locEditable; }

        public void setLocEditable(Object locEditable) {
            this.locEditable = locEditable;
        }

        public String getFechaHora() {
            return fechaHora;
        }

        public void setFechaHora(String fechaHora) {
            this.fechaHora = fechaHora;
        }

    }

}