package descuentos;


import finanzas.Valor;

import java.time.LocalDate;

/**
 * Clase abstracta para definir descuentos dependientes del dinero total gastado.
 * @author Izan Robles
 * @version 1.2
 */
public abstract class DescuentoPorValor extends Descuento
{
    private static final long serialVersionUID = 1L;

    /** Cuantía económica mínima que debe alcanzar el carrito para aplicar el descuento */
    private final Valor valorMinimo;

    /**
     * Constructor con control de nulos para el valor mínimo.
     * @param fechaComienzo Fecha de inicio de la promoción (permitiendo programar con antelación).
     * @param fechaFin Fecha de fin de la promoción (permitiendo automatizar cierres).
     * @param valorMinimo Importe mínimo requerido (no puede ser nulo).
     * @throws IllegalArgumentException si las fechas son inválidas o el valor mínimo es nulo.
     */
    public DescuentoPorValor(LocalDate fechaComienzo, LocalDate fechaFin, Valor valorMinimo)
    {
        super(fechaComienzo, fechaFin);

        if (valorMinimo == null)
        {
            throw new IllegalArgumentException("El valor mínimo exigido para el descuento no puede ser nulo.");
        }

        this.valorMinimo = valorMinimo.copiarInformacion();
    }

    /**
     * Permite a las subclases acceder al valor mínimo requerido.
     * @return Objeto Valor con una copia del importe mínimo.
     */
    public Valor obtenerValorMinimo()
    {
        return this.valorMinimo.copiarInformacion();
    }
}