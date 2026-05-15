package relaciones;

import java.io.Serializable;

import usuarios.Cliente;
import java.util.Objects;

/**
 * Opinión/valoración de un cliente sobre un producto.
 * 
 * @author Bruno Montero
 * @version 1.0
 */
public class Opinion implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * Valor de la opinión (0-5).
     */
    private final float valor;

    /**
     * Comentario de la opinión.
     */
    private final String comentario;

    /**
     * Autor de la opinión.
     */
    
    private final Cliente autor;

    /**
     * Constructor de la clase Opinion.
     *
     * @param valor puntuación (normalmente 0..5)
     * @param comentario comentario textual (si es null se guarda como cadena vacía)
     * @param autor cliente autor de la opinión
     */
    public Opinion(float valor, String comentario, Cliente autor)
    {
        this.valor = valor;
        this.comentario = Objects.requireNonNullElse(comentario, "");
        this.autor = autor;
    }

    /**
     * Obtiene el valor de la opinión.
     * 
     * @return valor de la opinión
     */
    public float getValor() 
    {
        return this.valor;
    }
}
