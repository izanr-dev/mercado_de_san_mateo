package productos;


import finanzas.Valor;

import java.util.List;

/**
 * Clase que representa una figura
 * Almacena la información relacionada con esta
 * @author Daniel Martín Jaén
 * @version 1.0
 */
public class Figura extends ProductoTienda
{
    private static final long serialVersionUID = 1L;

    /**
     * Objecto medida que almacena los datos relacionados con las dimensiones de la figura
     */
    private Medida medida;

    /**
     * Lista de materiales usados en la figura
     */
    private List<Material> materiales;

    /**
     * Constructor de figura, instancia un objeto figura a partir de los datos proporcionados
     * @param nombre Nombre de la figura
     * @param descripcion Descripción de la figura
     * @param stockTotal Número inicial de unidades de la figura
     * @param id Identificador único de la figura
     * @param precio Precio inicial de la figura
     * @param medida Objeto medida con las dimensiones de la figura
     * @param materiales Lista de elementos de la enumeración Material
     */
    public Figura(String nombre, String descripcion, int stockTotal, int id, Valor precio, Medida medida, List<Material> materiales)
    {
        super(nombre, descripcion, stockTotal, id, precio);
        this.medida = medida;
        this.materiales = materiales;
    }
}
