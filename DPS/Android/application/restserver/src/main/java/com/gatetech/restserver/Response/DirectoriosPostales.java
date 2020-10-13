package com.gatetech.restserver.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DirectoriosPostales {

    @SerializedName("Estado")
    @Expose
    private Integer estado;
    @SerializedName("Excepcion")
    @Expose
    private String excepcion;
    @SerializedName("DirectorioPostal")
    @Expose
    private List<DirectorioPostal> directorioPostal = null;

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

    public List<DirectorioPostal> getDirectorioPostal() {
        return directorioPostal;
    }

    public void setDirectorioPostal(List<DirectorioPostal> directorioPostal) {
        this.directorioPostal = directorioPostal;
    }

    public DirectorioPostal getDirectorioPostal(String DAsentamiento){

       if (directorioPostal != null) {
           for (DirectorioPostal dp : directorioPostal) {
               if (dp.getDAsenta().equals( DAsentamiento )){
                   return dp;
               }
           }
       }

        return null;
    }


   static  public class DirectorioPostal {

        @SerializedName("D_Codigo")
        @Expose
        private String dCodigo;
        @SerializedName("D_Asenta")
        @Expose
        private String dAsenta;
        @SerializedName("D_Tipo_Asenta")
        @Expose
        private String dTipoAsenta;
        @SerializedName("D_mnpio")
        @Expose
        private String dMnpio;
        @SerializedName("D_estado")
        @Expose
        private String dEstado;
        @SerializedName("D_ciudad")
        @Expose
        private String dCiudad;
        @SerializedName("C_estado")
        @Expose
        private String cEstado;
        @SerializedName("C_tipo_asenta")
        @Expose
        private String cTipoAsenta;
        @SerializedName("C_mnpio")
        @Expose
        private String cMnpio;
        @SerializedName("Id_asenta_cpcons")
        @Expose
        private String idAsentaCpcons;
        @SerializedName("D_zona")
        @Expose
        private String dZona;
        @SerializedName("C_cve_ciudad")
        @Expose
        private String cCveCiudad;

        public String getDCodigo() {
            return dCodigo;
        }

        public void setDCodigo(String dCodigo) {
            this.dCodigo = dCodigo;
        }

        public String getDAsenta() {
            return dAsenta;
        }

        public void setDAsenta(String dAsenta) {
            this.dAsenta = dAsenta;
        }

        public String getDTipoAsenta() {
            return dTipoAsenta;
        }

        public void setDTipoAsenta(String dTipoAsenta) {
            this.dTipoAsenta = dTipoAsenta;
        }

        public String getDMnpio() {
            return dMnpio;
        }

        public void setDMnpio(String dMnpio) {
            this.dMnpio = dMnpio;
        }

        public String getDEstado() {
            return dEstado;
        }

        public void setDEstado(String dEstado) {
            this.dEstado = dEstado;
        }

        public String getDCiudad() {
            return dCiudad;
        }

        public void setDCiudad(String dCiudad) {
            this.dCiudad = dCiudad;
        }

        public String getCEstado() {
            return cEstado;
        }

        public void setCEstado(String cEstado) {
            this.cEstado = cEstado;
        }

        public String getCTipoAsenta() {
            return cTipoAsenta;
        }

        public void setCTipoAsenta(String cTipoAsenta) {
            this.cTipoAsenta = cTipoAsenta;
        }

        public String getCMnpio() {
            return cMnpio;
        }

        public void setCMnpio(String cMnpio) {
            this.cMnpio = cMnpio;
        }

        public String getIdAsentaCpcons() {
            return idAsentaCpcons;
        }

        public void setIdAsentaCpcons(String idAsentaCpcons) {
            this.idAsentaCpcons = idAsentaCpcons;
        }

        public String getDZona() {
            return dZona;
        }

        public void setDZona(String dZona) {
            this.dZona = dZona;
        }

        public String getCCveCiudad() {
            return cCveCiudad;
        }

        public void setCCveCiudad(String cCveCiudad) {
            this.cCveCiudad = cCveCiudad;
        }

    }

}


