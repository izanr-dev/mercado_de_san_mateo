package descuentos;


import finanzas.Valor;
import relaciones.Carrito;

import java.time.LocalDate;

/**
 * Reduce en un porcentaje el precio total si el carrito supera cierto importe.
 * (ej. Si gastas más de 50€, tienes un 20% de descuento).
 * @author izanrobles
 * @version 1.1
 */
public class DescuentoPorValorPorcentual extends DescuentoPorValor
{
    private static final long serialVersionUID = 1L;

    /** Porcentaje a descontar (entre 1 y 100) */
    private final int porcentaje;

    /**
     * Descuento estándar para descuentos por valor cuantitativos
     * @param fechaComienzo Fecha de inicio de la promoción (permitiendo programar con antelación).
     * @param fechaFin Fecha de fin de la promoción (permitiendo automatizar cierres).
     * @param valorMinimo Importe mínimo requerido (no puede ser nulo).
     * @param porcentaje Porcentaje con el descuento a aplicar en caso de ser efectivo.
     * @throws IllegalArgumentException si las fechas son inválidas o el valor mínimo es nulo.
     */
    public DescuentoPorValorPorcentual(LocalDate fechaComienzo, LocalDate fechaFin, Valor valorMinimo, int porcentaje)
    {
        super(fechaComienzo, fechaFin, valorMinimo);

        if (porcentaje <= 0 || porcentaje > 100)
        {
            throw new IllegalArgumentException("El porcentaje de descuento debe estar comprendido entre 1 y 100.");
        }

        this.porcentaje = porcentaje;
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
            precioFinal.aplicarDescuentoPorcentual(this.porcentaje);
        }

        return precioFinal;
    }
}