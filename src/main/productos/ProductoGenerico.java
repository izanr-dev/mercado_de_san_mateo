package productos;


import finanzas.Valor;

/**
 * Producto genérico de la tienda sin atributos específicos de subtipo.
 * Utilizado cuando se crea un producto desde la aplicación sin información
 * adicional de una subclase concreta (Comic, Figura, Juego...).
 * @author Izan Robles
 * @version 1.0
 */
public class ProductoGenerico extends ProductoTienda
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructor de un producto genérico de tienda.
     *
     * @param nombre nombre del producto
     * @param descripcion descripción del producto
     * @param stockTotal stock inicial disponible
     * @param id identificador único del producto
     * @param precio precio inicial del producto
     */
    public ProductoGenerico(String nombre, String descripcion, int stockTotal, int id, Valor precio)
    {
        super(nombre, descripcion, stockTotal, id, precio);
    }
}
