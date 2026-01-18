// ReaccionService2.java
package solucion2;

import java.util.*;

public class ReaccionService2 {

    // postId -> (tipoReaccion -> contador)
    private final Map<String, Map<String, Integer>> conteosPorPost = new HashMap<>();

    // fecha -> set("reactorId|postId") para limitar 1 reacción por día a ese post (por reactor)
    private final Map<String, Set<String>> reaccionDelDia = new HashMap<>();

    public ReaccionResultado reaccionar(String postId, String fechaPost, String reactorId, String tipoReaccion) {
        if (postId == null || postId.trim().isEmpty())
            return new ReaccionResultado(false, "No se pudo identificar el registro.");
        if (fechaPost == null || fechaPost.trim().isEmpty())
            return new ReaccionResultado(false, "El registro no tiene fecha válida.");
        if (reactorId == null || reactorId.trim().isEmpty())
            return new ReaccionResultado(false, "Ingresa tu ID para reaccionar.");

        reactorId = reactorId.trim();

        // Permitimos que el combo tenga emojis: "❤️ APOYO"
        String tipo = (tipoReaccion == null) ? "" : tipoReaccion.trim().toUpperCase();
        tipo = tipo.replace("❤️", "")
                .replace("💪", "")
                .replace("🤝", "")
                .replace("👀", "")
                .trim();
        tipo = tipo.replace("Á", "A"); // por si pones "ÁNIMO"

        // Regla BeReal: 1 reacción por día a ese post (por reactor)
        Set<String> set = reaccionDelDia.computeIfAbsent(fechaPost, k -> new HashSet<>());
        String clave = reactorId + "|" + postId;

        if (set.contains(clave)) {
            return new ReaccionResultado(false, "Ya reaccionaste a este estado hoy. Vuelve mañana 🙂");
        }

        set.add(clave);

        Map<String, Integer> mapa = conteosPorPost.computeIfAbsent(postId, k -> new HashMap<>());
        mapa.put(tipo, mapa.getOrDefault(tipo, 0) + 1);

        return new ReaccionResultado(true, "¡Reacción registrada!");
    }

    public Map<String, Integer> getConteos(String postId) {
        Map<String, Integer> m = conteosPorPost.get(postId);
        if (m == null) return Collections.emptyMap();
        return new HashMap<>(m);
    }
}
