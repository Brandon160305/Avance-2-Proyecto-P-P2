// RegistroEstadoAnimo2.java
package solucion2;

import java.time.LocalDate;

public class RegistroEstadoAnimo2 {

    private String idEstudiante;
    private LocalDate fecha;   // CAMBIO: LocalDate
    private String estado;
    private String nota;

    public RegistroEstadoAnimo2(String idEstudiante, LocalDate fecha, String estado, String nota) {
        this.idEstudiante = idEstudiante;
        this.fecha = fecha;
        this.estado = estado;
        this.nota = nota;
    }

    public String getIdEstudiante() {
        return idEstudiante;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getEstado() {
        return estado;
    }

    public String getNota() {
        return nota;
    }

    // Más robusto: soporta "MUY TRISTE" y "MUY_TRISTE"
    public boolean esEstadoNegativo() {
        if (estado == null) return false;
        String e = estado.trim().toUpperCase().replace("_", " ");
        return e.equals("TRISTE") || e.equals("MUY TRISTE");
    }

    @Override
    public String toString() {
        return "[" + idEstudiante + "] " + (fecha == null ? "" : fecha) +
                " - " + estado + " - " + (nota == null ? "" : nota);
    }
}
