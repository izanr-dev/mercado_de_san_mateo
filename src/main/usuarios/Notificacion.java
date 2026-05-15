package usuarios;

import java.io.Serializable;

import java.time.LocalDateTime;

/**
 * Notificación enviada a un usuario.
 * 
 * @author Bruno Montero
 * @version 1.0
 */
public class Notificacion implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * Texto de la notificación
     */
    private String contenido;

    /**
     * Fecha de la notificación
     */
    private LocalDateTime fecha;

    /**
     * Constructor de la clase Notificacion.
     *
     * @param contenido texto de la notificación
     * @param fecha fecha/hora de creación
     * @throws IllegalArgumentException si contenido es null/vacío o si fecha es null
     */
    public Notificacion(String contenido, LocalDateTime fecha)
    {
        if (contenido == null || contenido.isBlank())
        {
            throw new IllegalArgumentException("El contenido no puede ser nulo o vacío");
        }
        if (fecha == null)
        {
            throw new IllegalArgumentException("La fecha no puede ser nula");
        }
        this.contenido = contenido;
        this.fecha = fecha;
    }
}
