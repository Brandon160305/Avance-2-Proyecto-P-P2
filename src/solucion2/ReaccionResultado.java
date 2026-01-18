package solucion2;

public class ReaccionResultado {
    private final boolean ok;
    private final String mensaje;

    public ReaccionResultado(boolean ok, String mensaje) {
        this.ok = ok;
        this.mensaje = mensaje;
    }

    public boolean isOk() {
        return ok;
    }

    public String getMensaje() {
        return mensaje;
    }
}
