package descuentos;

import java.io.Serializable;

import finanzas.Valor;
import relaciones.Carrito;

import java.time.LocalDate;

/**
 * Clase abstracta para definir atributos y métodos fundamentales en todos
 * los tipos de descuento
 * @author Izan Robles
 * @version 1.2
 */
public abstract class Descuento implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**Fecha asociada al comienzo del descuento, permite programar descuentos automáticos*/
    private final LocalDate fechaComienzo;
    /**Fecha de final del descuento*/
    private final LocalDate fechaFin;

    /**
     * Constructor para inicializar un periodo de descuento con control de integridad temporal.
     * @param fechaComienzo Fecha de inicio (no puede ser nula ni anterior a la fecha actual).
     * @param fechaFin Fecha de finalización (debe ser posterior o igual a la de comienzo).
     * @throws IllegalArgumentException si las fechas son nulas, el rango es inverso o
     * si la fecha de comienzo ya ha pasado.
     */
    public Descuento(LocalDate fechaComienzo, LocalDate fechaFin)
    {
        LocalDate hoy = LocalDate.now();

        if (fechaComienzo == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas de vigencia no pueden ser nulas.");
        }

        if (fechaComienzo.isBefore(hoy)) {
            throw new IllegalArgumentException("La fecha de comienzo (" + fechaComienzo +
                    ") no puede ser anterior a la fecha actual (" + hoy + ").");
        }

        if (fechaFin.isBefore(fechaComienzo)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de comienzo.");
        }

        this.fechaComienzo = fechaComienzo;
        this.fechaFin = fechaFin;
    }

    /**
     * Comprueba si el descuento está vigente para una fecha dada.
     * @param fecha fecha a comprobar.
     * @return true si la fecha está dentro del rango de vigencia (incluyendo extremos).
     */
    public boolean estaVigente(LocalDate fecha)
    {
        if (fecha == null)
        {
            return false;
        }
        return !fecha.isBefore(this.fechaComienzo) && !fecha.isAfter(this.fechaFin);
    }

    /**
     * Método abstracto para calcular el valor final tras aplicar la promoción.
     * @param carrito El contenedor de productos sobre el que se aplica el descuento.
     * @return El objeto Valor con el precio resultante tras la aplicación del descuento.
     */
    public abstract Valor calcularPrecioConDescuento(Carrito carrito);
}