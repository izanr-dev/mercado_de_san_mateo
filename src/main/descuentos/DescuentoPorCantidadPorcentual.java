package descuentos;


import finanzas.Valor;
import relaciones.Carrito;

import java.time.LocalDate;

/**
 * Clase para definir descuentos porcentuales en compras que superen
 * una cantidad mínima de productos en el carrito. (ej. Compra 5 cosas y te regalo un 15%).
 * @author Izan Robles
 * @version 1.3
 */
public class DescuentoPorCantidadPorcentual extends DescuentoPorCantidad
{
    private static final long serialVersionUID = 1L;

    /** Porcentaje de descuento a aplicar (debe estar entre 1 y 100) */
    private final int porcentaje;

    /**
     * Constructor estándar para descuentos por cantidad de tipo porcentual.
     * @param fechaComienzo Fecha de inicio (no puede ser nula ni anterior a la fecha actual).
     * @param fechaFin Fecha de finalización (debe ser posterior o igual a la de comienzo).
     * @param cantidadMinima necesaria para hacer el descuento efectivo.
     * @param porcentaje asociado al descuento a aplicar a la compra en caso de ser efectivo.
     * @throws IllegalArgumentException si las fechas son nulas, el rango es inverso,
     */
    public DescuentoPorCantidadPorcentual(LocalDate fechaComienzo, LocalDate fechaFin, int cantidadMinima, int porcentaje)
    {
        super(fechaComienzo, fechaFin, cantidadMinima);

        if (porcentaje <= 0 || porcentaje > 100)
        {
            throw new IllegalArgumentException("El porcentaje de descuento debe estar entre 1 y 100.");
        }

        this.porcentaje = porcentaje;
    }

    @Override
    public Valor calcularPrecioConDescuento(Carrito carrito)
    {
        if (carrito == null)
        {
            throw new IllegalArgumentException("El carrito no puede ser nulo al calcular el descuento.");
        }

        Valor precioTotal = carrito.calcularPrecioCarrito();


        if (precioTotal == null)
        {
            return null;
        }

        Valor precioFinal = precioTotal.copiarInformacion();

        if (carrito.contarElementos() >= this.obtenerCantidadMinima())
        {
            precioFinal.aplicarDescuentoPorcentual(this.porcentaje);
        }

        return precioFinal;
    }
}