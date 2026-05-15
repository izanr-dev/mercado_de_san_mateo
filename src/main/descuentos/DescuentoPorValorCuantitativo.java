package descuentos;


import finanzas.Valor;
import relaciones.Carrito;

import java.time.LocalDate;

/**
 * Clase para descuentos dependientes del valor total gastado de tipo cuantitativo.
 * (ej. Si gastas más de 100€, te restamos 15€ de tu compra).
 * @author Izan Robles
 * @version 1.1
 */
public class DescuentoPorValorCuantitativo extends DescuentoPorValor
{
    private static final long serialVersionUID = 1L;

    /** Dinero que se restará del precio total */
    private final Valor cantidadDescontada;

    /**
     * Descuento estándar para descuentos por valor cuantitativos
     * @param fechaComienzo Fecha de inicio de la promoción (permitiendo programar con antelación).
     * @param fechaFin Fecha de fin de la promoción (permitiendo automatizar cierres).
     * @param valorMinimo Importe mínimo requerido (no puede ser nulo).
     * @param cantidadDescontada Cantidad a descontar en caso de aplicarse.
     * @throws IllegalArgumentException si las fechas son inválidas o el valor mínimo es nulo.
     */
    public DescuentoPorValorCuantitativo(LocalDate fechaComienzo, LocalDate fechaFin, Valor valorMinimo, Valor cantidadDescontada)
    {
        super(fechaComienzo, fechaFin, valorMinimo);

        if (cantidadDescontada == null)
        {
            throw new IllegalArgumentException("La cantidad a descontar no puede ser nula.");
        }

        this.cantidadDescontada = cantidadDescontada.copiarInformacion();
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

        if (precioFinal.compararCuantias(this.obtenerValorMinimo()) >= 0)
        {
            if (precioFinal.compararCuantias(this.cantidadDescontada) >= 0)
            {
                precioFinal.decrementar(this.cantidadDescontada);
            }

            else
            {
                precioFinal.decrementar(precioFinal.copiarInformacion()); // Lo deja a 0
            }
        }

        return precioFinal;
    }
}