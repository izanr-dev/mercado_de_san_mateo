package finanzas;

import java.io.Serializable;

import java.time.LocalDate;
import es.uam.eps.padsof.telecard.TeleChargeAndPaySystem;

/**
 * Información bancaria para pagos (tarjeta).
 * 
 * @author Bruno Montero
 * @version 1.4
 */
public class InformacionBancaria implements Serializable
{
    private static final long serialVersionUID = 1L;

    /*
     * Número de tarjeta
     */
    private String numTarjeta;

    /*
     * CVV de la tarjeta
     */
    private String cvv;

    /*
     * Fecha de caducidad de la tarjeta
     */
    private LocalDate fechaCaducidad;

    /**
     * Constructor de la clase InformacionBancaria.
     *
     * @param numTarjeta número de tarjeta (se almacena como String)
     * @param cvv código CVV (se almacena como String)
     * @param fechaCaducidad fecha de caducidad de la tarjeta
     * @throws IllegalArgumentException si algún parámetro es inválido
     */
    public InformacionBancaria(String numTarjeta, String cvv, LocalDate fechaCaducidad)
    {
        if (numTarjeta == null || numTarjeta.isBlank())
        {
            throw new IllegalArgumentException("El numero de tarjeta no puede ser nulo o vacio");
        }
        if (cvv == null || cvv.isBlank())
        {
            throw new IllegalArgumentException("El CVV no puede ser nulo o vacío");
        }
        if (fechaCaducidad == null)
        {
            throw new IllegalArgumentException("La fecha de caducidad no puede ser nula");
        }
        this.numTarjeta = numTarjeta;
        this.cvv = cvv;
        this.fechaCaducidad = fechaCaducidad;
    }

    /**
     * Comprueba si la tarjeta está caducada comparando con una fecha dada.
     *
     * @param hoy fecha de referencia (normalmente LocalDate.now())
     * @return true si está caducada, false si no o si hoy es null
     */
    public boolean estaCaducada(LocalDate hoy)
    {
        if (hoy == null || fechaCaducidad == null)
        {
            return false;
        }
        return fechaCaducidad.isBefore(hoy);
    }

    /**
     * Simula el proceso de un cobro de una cantidad a una tarjeta.
     *
     * @param concepto texto descriptivo del cargo.
     * @param cantidad cantidad positiva a cobrar.
     * @return true si el cobro se procesa, false en cualquier error.
     */
    public boolean procesarCobroSimulado(String concepto, double cantidad)
    {
        if (concepto == null || concepto.isBlank() || cantidad <= 0)
        {
            return false;
        }
        if (this.estaCaducada(LocalDate.now()))
        {
            return false;
        }

        try
        {
            TeleChargeAndPaySystem.charge(this.numTarjeta, concepto, -cantidad, false);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
