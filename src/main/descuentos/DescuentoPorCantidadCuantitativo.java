package descuentos;


import finanzas.Valor;
import relaciones.Carrito;

import java.time.LocalDate;

/**
 * Clase para definir descuentos dependientes de la cantidad comprada cuyo valor es numérico, fijo. (ej. Compra 3, te regalamos 5€)
 * @author Izan Robles
 * @version 1.1
 */
public class DescuentoPorCantidadCuantitativo extends DescuentoPorCantidad
{
    private static final long serialVersionUID = 1L;

    /**Objeto valor con la cantidad asociada al descuento*/
    private final Valor descuentoAsociado;

    /**
     * Constructor con información añadida de cantidad mínima.
     * @param fechaComienzo Fecha de inicio (no puede ser nula ni anterior a la fecha actual).
     * @param fechaFin Fecha de finalización (debe ser posterior o igual a la de comienzo).
     * @param cantidadMinima necesaria para hacer el descuento efectivo.
     * @param cantidadDescontada Valor asociado al descuento (dinero devuelto en caso de aplicar)
     * @throws IllegalArgumentException si las fechas son nulas, el rango es inverso o
     * si la fecha de comienzo ya ha pasado.
     */
    public DescuentoPorCantidadCuantitativo(LocalDate fechaComienzo, LocalDate fechaFin, int cantidadMinima, Valor cantidadDescontada)
    {
        super(fechaComienzo, fechaFin, cantidadMinima);

        if (cantidadDescontada == null)
        {
            throw new IllegalArgumentException("El valor a descontar no puede ser nulo.");
        }

        this.descuentoAsociado = cantidadDescontada.copiarInformacion();
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
            if (precioFinal.compararCuantias(this.descuentoAsociado) >= 0)
            {
                precioFinal.decrementar(this.descuentoAsociado);
            }

            else
            {
                precioFinal.decrementar(precioFinal.copiarInformacion());
            }
        }

        return precioFinal;
    }
}
