package productos;


import finanzas.Valor;

import java.util.List;

/**
 * Clase que representa un pack de productos de la tienda
 * @author Daniel Martín Jaén
 * @version 1.0
 */
public class Pack extends ProductoTienda
{
    private static final long serialVersionUID = 1L;

    /**
     * Lista de productos de la tienda en el pack
     */
    private List<ProductoTienda> productos;

    /**
     * Constructor de clase, instancia un objeto Pack
     * @param nombre Nombre del pack
     * @param descripcion Descripción del pack
     * @param stockTotal Número inicial de packs disponibles
     * @param id Identificador único del pack
     * @param precio Precio inicial del pack
     * @param productos Lista con los productos contenidos en el pack
     */
    public Pack(String nombre, String descripcion, int stockTotal, int id, Valor precio, List<ProductoTienda> productos)
    {
        super(nombre, descripcion, stockTotal, id, precio);
        this.productos = productos;
    }
}
