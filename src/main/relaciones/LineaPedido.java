package relaciones;

import java.io.Serializable;

import finanzas.Valor;
import productos.Categoria;
import productos.ProductoTienda;

import java.util.List;

/**
 * Clase que representa una línea individual dentro de un pedido.
 * Relaciona un producto específico con la cantidad de unidades compradas.
 * 
 * @author Izan Robles
 * @version 1.1
 */
public class LineaPedido implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * Cantidad de ejemplares comprados de este producto.
     * */
    private final int numComprados;

    /**
     * Referencia al producto que se está adquiriendo.
     * */
    private final ProductoTienda producto;

    /**
     * Crea una nueva línea de pedido validando los datos de entrada.
     * 
     * @param numComprados Cantidad de unidades (debe ser mayor que 0).
     * @param producto Producto a comprar (no puede ser nulo).
     * @throws IllegalArgumentException si la cantidad es menor o igual que 0 o el producto es nulo.
     */
    public LineaPedido(int numComprados, ProductoTienda producto)
    {
        if (numComprados <= 0)
        {
            throw new IllegalArgumentException("La cantidad comprada debe ser mayor que 0");
        }
        if (producto == null)
        {
            throw new IllegalArgumentException("El producto de la línea no puede ser nulo");
        }

        this.numComprados = numComprados;
        this.producto = producto;
    }

    /**
     * Calcula el precio total de la línea basándose en el precio actual del producto
     * y la cantidad de unidades. No tiene en cuenta los descuentos
     * 
     * @return Objeto Valor con el importe total de la línea.
     */
    public Valor calcularPrecioLinea()
    {
        Valor precioTotal = this.producto.getPrecio();
        precioTotal.multiplicar(this.numComprados);

        return precioTotal;
    }

    /**
     * Método para obtener las categorías asociadas a una línea de pedido
     * Devuelve una copia por el método seguro de ProductoTienda, no devuelve referencias directas sin protección.
     * @return lista inmodificable con las categorías asociadas al producto de la línea.
     */
    public List<Categoria> obtenerCategoriasProducto()
    {
        return this.producto.getCategorias();
    }
}
