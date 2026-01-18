// CasoRiesgo2.java
package solucion2;

import java.time.LocalDate;
import java.util.Objects;

public class CasoRiesgo2 {

    private String idEstudiante;
    private int diasRacha;
    private String motivo;
    private LocalDate ultimaFecha; // CAMBIO: LocalDate

    public CasoRiesgo2(String idEstudiante, int diasRacha, String motivo, LocalDate ultimaFecha) {
        this.idEstudiante = idEstudiante;
        this.diasRacha = diasRacha;
        this.motivo = motivo;
        this.ultimaFecha = ultimaFecha;
    }

    public String getIdEstudiante() {
        return idEstudiante;
    }

    public int getDiasRacha() {
        return diasRacha;
    }

    public String getMotivo() {
        return motivo;
    }

    public LocalDate getUltimaFecha() {
        return ultimaFecha;
    }

    public void setDiasRacha(int diasRacha) {
        this.diasRacha = diasRacha;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public void setUltimaFecha(LocalDate ultimaFecha) {
        this.ultimaFecha = ultimaFecha;
    }

    // CLAVE: para poder remover/actualizar en PriorityQueue por estudiante
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CasoRiesgo2)) return false;
        CasoRiesgo2 that = (CasoRiesgo2) o;
        return Objects.equals(idEstudiante, that.idEstudiante);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEstudiante);
    }

    @Override
    public String toString() {
        return "[" + idEstudiante + "] Racha de " + diasRacha +
                " días - Última fecha: " + (ultimaFecha == null ? "" : ultimaFecha) +
                " - " + motivo;
    }
}
