package productos;

import java.io.Serializable;

/**
 * Clase simple para almacenar una fotografía según su dirección pública.
 * @author Izan Robles
 * @version 1.1
 */
public class Imagen implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**String con el url asociado a la imagen*/
    private final String url;

    /**
     * Constructor estándar para imagen
     * @param url asignado a la imagen.
     */
    public Imagen(String url)
    {
        this.url = url;
    }
}
