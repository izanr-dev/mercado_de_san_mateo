package descuentos;


import finanzas.Valor;
import productos.ProductoTienda;
import relaciones.Carrito;

import java.time.LocalDate;

/**
 * Aplica un descuento porcentual únicamente sobre las unidades de un
 * producto específico dentro del carrito.
 * @author Izan Robles
 * @version 1.1
 */
public class DescuentoPorProducto extends Descuento
{
    private static final long serialVersionUID = 1L;

    /** Producto sobre el que recae la rebaja */
    private final ProductoTienda productoAsociado;

    /** Porcentaje a descontar del subtotal del producto (entre 1 y 100) */
    private final int descuentoAsociado;

    /**
     * Constructor con control de errores para el producto y el porcentaje.
     * @param fechaComienzo Fecha de inicio de la promoción.
     * @param fechaFin Fecha de finalización.
     * @param producto Producto al que se aplica el descuento (no puede ser nulo).
     * @param descuentoAsociado Porcentaje de descuento (debe estar entre 1 y 100).
     * @throws IllegalArgumentException En caso de que los argumentos no cumplan las características necesarias.
     */
    public DescuentoPorProducto(LocalDate fechaComienzo, LocalDate fechaFin, ProductoTienda producto, int descuentoAsociado)
    {
        super(fechaComienzo, fechaFin);

        if (producto == null)
        {
            throw new IllegalArgumentException("El producto asociado al descuento no puede ser nulo.");
        }

        if (descuentoAsociado <= 0 || descuentoAsociado > 100)
        {
            throw new IllegalArgumentException("El porcentaje de descuento debe estar comprendido entre 1 y 100.");
        }

        this.productoAsociado = producto;
        this.descuentoAsociado = descuentoAsociado;
    }

    @Override
    public Valor calcularPrecioConDescuento(Carrito carrito)
    {
        if (carrito == null)
        {
            throw new IllegalArgumentException("El carrito no puede ser nulo.");
        }

        Valor precioTotal = carrito.calcularPrecioCarrito();

        if (precioTotal == null)
        {
            return null;
        }

        Valor precioFinal = precioTotal.copiarInformacion();

        Valor subtotalProducto = carrito.obtenerSubtotalDeProducto(this.productoAsociado);

        if (subtotalProducto != null)
        {
            Valor rebajaGenerada = subtotalProducto.copiarInformacion();
            subtotalProducto.aplicarDescuentoPorcentual(this.descuentoAsociado);
            rebajaGenerada.decrementar(subtotalProducto);
            precioFinal.decrementar(rebajaGenerada);
        }

        return precioFinal;
    }
}