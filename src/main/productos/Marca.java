package productos;

import java.io.Serializable;

import java.util.List;

/**
 * Clase representa una marca
 * Almacena la información de esta
 * @author Daniel Martín Jaén
 * @version 1.0
 */
public class Marca implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * Nombre de la marca
     */
    private final String nombre;

    /**
     * Lista de productos producidos por esta marca
     */
    private final List<Figura> productos;

    /**
     * Constructor de la clase, instancia un objeto marca
     * @param nombre Nombre de la marca
     * @param productos Lista con los productos producidos por esta marca
     */
    public Marca(String nombre, List<Figura> productos)
    {
        this.nombre = nombre;
        this.productos = productos;
    }
}
