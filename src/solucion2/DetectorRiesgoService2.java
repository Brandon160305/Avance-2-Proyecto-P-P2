// DetectorRiesgoService2.java
package solucion2;

import java.time.LocalDate;
import java.util.*;

public class DetectorRiesgoService2 {

    private List<RegistroEstadoAnimo2> historial;
    private List<String> fechasDisponibles;

    // Prioridad real (por diasRacha desc, luego ultimaFecha desc)
    private PriorityQueue<CasoRiesgo2> colaRiesgo;

    // Caso único por estudiante
    private Map<String, CasoRiesgo2> casosPorEstudiante;

    private CasoRiesgo2 ultimoCasoDetectado;

    public DetectorRiesgoService2() {
        historial = new ArrayList<>();
        fechasDisponibles = new ArrayList<>();

        // CAMBIO: ultimaFecha ya es LocalDate
        colaRiesgo = new PriorityQueue<>(
                Comparator.comparingInt(CasoRiesgo2::getDiasRacha).reversed()
                        .thenComparing(CasoRiesgo2::getUltimaFecha, Comparator.nullsLast(Comparator.reverseOrder()))
        );

        casosPorEstudiante = new HashMap<>();
    }

    public CasoRiesgo2 getUltimoCasoDetectado() {
        return ultimoCasoDetectado;
    }

    public boolean agregarFecha(String fecha) {
        try {
            LocalDate.parse(fecha);
        } catch (Exception ex) {
            return false;
        }

        if (!fechasDisponibles.contains(fecha)) {
            fechasDisponibles.add(fecha);
            Collections.sort(fechasDisponibles);
        }
        return true;
    }

    public List<String> getFechasDisponibles() {
        return fechasDisponibles;
    }

    public List<RegistroEstadoAnimo2> getHistorial() {
        return historial;
    }

    // Snapshot ordenado por prioridad real
    public List<CasoRiesgo2> getCasosPendientesSnapshot() {
        List<CasoRiesgo2> lista = new ArrayList<>(colaRiesgo);
        lista.sort(
                Comparator.comparingInt(CasoRiesgo2::getDiasRacha).reversed()
                        .thenComparing(CasoRiesgo2::getUltimaFecha, Comparator.nullsLast(Comparator.reverseOrder()))
        );
        return lista;
    }

    public CasoRiesgo2 obtenerSiguienteCaso() {
        CasoRiesgo2 c = colaRiesgo.poll();
        if (c != null) {
            casosPorEstudiante.remove(c.getIdEstudiante());
        }
        return c;
    }

    /**
     * CAMBIO: ya no falla silencioso.
     * Retorna null si OK, o un mensaje de error si algo está mal.
     */
    public String registrarEstado(String idEstudiante, String fecha, String estado, String nota) {

        if (idEstudiante == null || idEstudiante.trim().isEmpty())
            return "Ingresa el ID del estudiante.";

        if (fecha == null || fecha.trim().isEmpty())
            return "Selecciona/ingresa una fecha válida.";

        if (estado == null || estado.trim().isEmpty())
            return "Selecciona un estado de ánimo.";

        idEstudiante = idEstudiante.trim();
        fecha = fecha.trim();
        estado = estado.trim();

        LocalDate fechaLD;
        try {
            fechaLD = LocalDate.parse(fecha);
        } catch (Exception ex) {
            return "La fecha NO es válida.\nEjemplo: 2025-11-29 (YYYY-MM-DD)";
        }

        RegistroEstadoAnimo2 reg = new RegistroEstadoAnimo2(idEstudiante, fechaLD, estado, nota);
        historial.add(reg);

        // Mantener lista de fechas ordenada SIEMPRE
        if (!fechasDisponibles.contains(fecha)) {
            fechasDisponibles.add(fecha);
            Collections.sort(fechasDisponibles);
        }

        ultimoCasoDetectado = null;

        evaluarRachasConsecutivas(idEstudiante);

        return null; // OK
    }

    /**
     * CAMBIO: detección robusta de rachas consecutivas (sin Stack).
     * - Filtra negativos
     * - Elimina duplicados por fecha
     * - Ordena fechas
     * - Calcula la racha más larga y su última fecha
     * - Si racha >= 3 => crea/actualiza caso
     *
     * También actualiza el caso si:
     * - crece la racha, o
     * - la racha es igual pero la última fecha es más reciente
     */
    private void evaluarRachasConsecutivas(String idEstudiante) {

        // Usamos Set para eliminar duplicados de fecha
        Set<LocalDate> setFechasTristes = new HashSet<>();

        for (RegistroEstadoAnimo2 r : historial) {
            if (r.getIdEstudiante().equals(idEstudiante) && r.esEstadoNegativo()) {
                setFechasTristes.add(r.getFecha());
            }
        }

        if (setFechasTristes.size() < 3) return;

        List<LocalDate> fechas = new ArrayList<>(setFechasTristes);
        Collections.sort(fechas);

        int maxRacha = 1;
        int rachaActual = 1;

        LocalDate ultimaFechaMax = fechas.get(0);
        LocalDate ultimaFechaActual = fechas.get(0);

        for (int i = 1; i < fechas.size(); i++) {
            LocalDate prev = fechas.get(i - 1);
            LocalDate curr = fechas.get(i);

            if (curr.equals(prev.plusDays(1))) {
                rachaActual++;
                ultimaFechaActual = curr;
            } else {
                // cerrar racha actual
                if (rachaActual > maxRacha ||
                        (rachaActual == maxRacha && ultimaFechaActual.isAfter(ultimaFechaMax))) {
                    maxRacha = rachaActual;
                    ultimaFechaMax = ultimaFechaActual;
                }
                // reiniciar
                rachaActual = 1;
                ultimaFechaActual = curr;
            }
        }

        // comparar la última racha al final
        if (rachaActual > maxRacha ||
                (rachaActual == maxRacha && ultimaFechaActual.isAfter(ultimaFechaMax))) {
            maxRacha = rachaActual;
            ultimaFechaMax = ultimaFechaActual;
        }

        if (maxRacha < 3) return;

        String motivo = "Racha de " + maxRacha + " días con estado TRISTE/MUY TRISTE";

        CasoRiesgo2 existente = casosPorEstudiante.get(idEstudiante);

        if (existente == null) {
            CasoRiesgo2 nuevo = new CasoRiesgo2(idEstudiante, maxRacha, motivo, ultimaFechaMax);
            casosPorEstudiante.put(idEstudiante, nuevo);
            colaRiesgo.add(nuevo);
            ultimoCasoDetectado = nuevo;
            return;
        }

        boolean crecio = maxRacha > existente.getDiasRacha();
        boolean mismaRachaMasReciente =
                (maxRacha == existente.getDiasRacha()) &&
                        (existente.getUltimaFecha() == null || ultimaFechaMax.isAfter(existente.getUltimaFecha()));

        if (crecio || mismaRachaMasReciente) {
            colaRiesgo.remove(existente); // funciona por equals/hashCode (idEstudiante)
            existente.setDiasRacha(maxRacha);
            existente.setUltimaFecha(ultimaFechaMax);
            existente.setMotivo(motivo);
            colaRiesgo.add(existente);
            ultimoCasoDetectado = existente;
        } else {
            ultimoCasoDetectado = null;
        }
    }
}
