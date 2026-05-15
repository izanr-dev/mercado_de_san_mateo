package descuentos;


import java.time.LocalDate;

/**
 * Clase abstracta para definir atributos y métodos fundamentales en todos
 * los tipos de descuentos dependientes de la cantidad de la compra.
 * @author Izan Robles
 * @version 1.2
 */
public abstract class DescuentoPorCantidad extends Descuento
{
    private static final long serialVersionUID = 1L;

    /**Cantidad mínima de productos a comprar para hacer efectivo el descuento*/
    private final int cantidadMinima;

    /**
     * Constructor con información añadida de cantidad mínima.
     * @param fechaComienzo Fecha de inicio (no puede ser nula ni anterior a la fecha actual).
     * @param fechaFin Fecha de finalización (debe ser posterior o igual a la de comienzo).
     * @param cantidadMinima necesaria para hacer el descuento efectivo.
     * @throws IllegalArgumentException si las fechas son nulas, el rango es inverso,
     * si la fecha de comienzo ya ha pasado o si la cantidad mínima es igual o menor que 0.
     */
    public DescuentoPorCantidad(LocalDate fechaComienzo, LocalDate fechaFin, int cantidadMinima)
    {
        super(fechaComienzo, fechaFin);

        if (cantidadMinima <= 0)
        {
            throw new IllegalArgumentException("La cantidad mínima para aplicar un descuento debe ser estrictamente mayor que 0.");
        }

        this.cantidadMinima = cantidadMinima;
    }

    /**
     * Método para obtener la cantidad mínima necesaria para la aplicación del descuento
     * @return int con el número (pasa una copia, no la referencia, ya que es primitivo).
     */
    public int obtenerCantidadMinima()
    {
        return this.cantidadMinima;
    }
}
