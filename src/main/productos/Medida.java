package productos;

import java.io.Serializable;

/**
 * Clase que representa la medida en tres dimensiones de una figura
 * Maneja el calculo del volumen de la figura
 * @author Daniel Martín Jaén
 * @version 1.0
 */
public class Medida implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * Altura de la figura en centímetros
     */
    private final float alto;

    /**
     * Ancho de la figura en centímetros
     */
    private final float ancho;

    /**
     * Largo de la figura en centímetros
     */
    private final float largo;

    /**
     * Constructor de clase, instancia un objeto medida
     * @param alto Altura de la figura en centímetros
     * @param ancho Ancho de la figura en centímetros
     * @param largo Largo de la figura en centímetros
     */
    public Medida(float alto, float ancho, float largo)
    {
        this.alto = alto;
        this.ancho = ancho;
        this.largo = largo;
    }

    /**
     * Calcula el volumen de la figura
     * @return el volumen de la figura
     */
    public float calcularVolumen()
    {
        return this.largo * this.ancho * this.alto;
    }
}
