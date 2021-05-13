package callgraph;

import java.util.Objects;

public class MethodReport {
    private String nombre;
    private String paquete;
    private int LOC;
    private String resultado;
    private int lineaClase;
    private String tipo;

    public MethodReport() {

    }

    public MethodReport(String nombre, String paquete, int lOC, String resultado, int lineaClase, String tipo) {
        this.nombre = nombre;
        this.paquete = paquete;
        LOC = lOC;
        this.resultado = resultado;
        this.lineaClase = lineaClase;
        this.tipo = tipo;
    }

    public MethodReport(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPaquete() {
        return paquete;
    }

    public void setPaquete(String paquete) {
        this.paquete = paquete;
    }

    public int getLOC() {
        return LOC;
    }

    public void setLOC(int lOC) {
        LOC = lOC;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public int getLineaClase() {
        return lineaClase;
    }

    public void setLineaClase(int lineaClase) {
        this.lineaClase = lineaClase;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MethodReport)) {
            return false;
        }
        MethodReport m = (MethodReport) obj;
        return this.nombre.equals(m.getNombre());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.nombre);
    }

    @Override
    public String toString() {
        return this.paquete+this.nombre;
    }
}
