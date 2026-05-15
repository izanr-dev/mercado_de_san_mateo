package finanzas;

import java.io.Serializable;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Clase para gestionar precios y valores económicos de forma segura.
 * @author Izan Robles
 * @version 1.4
 */
public class Valor implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * Valor exacto con dato apto para producción BigDecimal.
     */
    private BigDecimal cuantia;

    /**
     * Divisa asociada al valor exacto
     */
    private Divisa divisa;

    /**
     * Constructor para objetos Valor con datos de inicialización comprobados.
     * @param cuantia dato BigDecimal para almacenar el valor numérico.
     * @param divisa asociada al pago.
     */
    public Valor(BigDecimal cuantia, Divisa divisa)
    {
        if (cuantia == null)
        {
            throw new IllegalArgumentException("La cuantía no puede ser nula.");
        }
        if (divisa == null)
        {
            throw new IllegalArgumentException("La divisa no puede ser nula.");
        }

        if (cuantia.compareTo(BigDecimal.ZERO) < 0)
        {
            throw new IllegalArgumentException("La cuantía no puede ser negativa.");
        }

        this.cuantia = cuantia;
        this.divisa = divisa;
    }

    /**
     * Método para incrementar el valor numérico de forma segura
     * @param valor clase segura Valor que actúa como sumando.
     */
    public void incrementar(Valor valor)
    {
        if (valor == null || valor.cuantia == null || valor.divisa == null)
        {
            throw new IllegalArgumentException("El valor a incrementar es nulo o está corrupto.");
        }

        if (this.cuantia == null || this.divisa == null)
        {
            throw new IllegalStateException("El valor actual no está inicializado.");
        }

        if (this.divisa != valor.divisa)
        {
            throw new IllegalArgumentException("Incompatibilidad de divisas: no se pueden sumar "
                    + this.divisa + " con " + valor.divisa + ".");
        }

        this.cuantia = this.cuantia.add(valor.cuantia);
    }

    /**
     * Método para decrementar el valor numérico de forma segura, evitando valores negativos.
     * @param valor clase segura Valor que actúa como sustraendo.
     */
    public void decrementar(Valor valor)
    {
        if (valor == null || valor.cuantia == null || valor.divisa == null)
        {
            throw new IllegalArgumentException("El valor a decrementar es nulo o está corrupto.");
        }

        if (this.cuantia == null || this.divisa == null)
        {
            throw new IllegalStateException("El valor actual no está inicializado.");
        }

        if (this.divisa != valor.divisa)
        {
            throw new IllegalArgumentException("Incompatibilidad de divisas: no se pueden restar "
                    + this.divisa + " con " + valor.divisa + ".");
        }

        BigDecimal resultado = this.cuantia.subtract(valor.cuantia);
        if (resultado.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("La operación de decremento resultaría en un valor negativo.");
        }

        this.cuantia = resultado;
    }

    /**
     * Método seguro para efectuar multiplicaciones de valores económicos.
     * @param factor entero positivo a usar como factor en la operación.
     */
    public void multiplicar(int factor)
    {
        if (this.cuantia == null)
        {
            throw new IllegalStateException("El valor actual no está inicializado.");
        }

        if (factor < 0)
        {
            throw new IllegalArgumentException("El factor de multiplicación no puede ser negativo.");
        }

        this.cuantia = this.cuantia.multiply(new BigDecimal(factor));
    }

    /**
     * Método seguro para obtener el valor de un precio o valor económico sin
     * exponer la referencia original
     * @return objeto Valor con duplicado de la información del original.
     */
    public Valor copiarInformacion()
    {
        if (this.cuantia == null || this.divisa == null)
        {
            throw new IllegalStateException("No se puede copiar un valor que no está inicializado.");
        }
        return new Valor(this.cuantia, this.divisa);
    }

    /**
     * Compara este valor con otro para determinar cuál es mayor.
     * @param otro Valor a comparar.
     * @return 1 si este es mayor, -1 si es menor, 0 si son iguales.
     * @throws IllegalArgumentException si las divisas no coinciden.
     */
    public int compararCuantias(Valor otro)
    {
        if (otro == null || otro.cuantia == null) {
            return 1; // Un valor inicializado siempre es mayor que uno nulo
        }
        if (this.cuantia == null) {
            return -1;
        }
        if (this.divisa != otro.divisa) {
            throw new IllegalArgumentException("No se pueden comparar valores con distintas divisas.");
        }

        return this.cuantia.compareTo(otro.cuantia);
    }

    /**
     * Método seguro para aplicar un descuento porcentual sobre la cuantía actual.
     * @param porcentaje entero entre 0 y 100.
     * @throws IllegalStateException si el valor no está inicializado.
     * @throws IllegalArgumentException si el porcentaje es inválido.
     */
    public void aplicarDescuentoPorcentual(int porcentaje)
    {
        if (this.cuantia == null)
        {
            throw new IllegalStateException("El valor actual no está inicializado.");
        }

        if (porcentaje < 0 || porcentaje > 100)
        {
            throw new IllegalArgumentException("El porcentaje debe estar comprendido entre 0 y 100.");
        }

        BigDecimal multiplicador = BigDecimal.valueOf(100 - porcentaje);

        this.cuantia = this.cuantia.multiply(multiplicador).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    /**
     * Actualiza la cuantía del valor de forma controlada.
     * Pensado para operaciones administrativas como la modificación de precios.
     *
     * @param nuevaCuantia nuevo importe a establecer
     * @throws IllegalArgumentException si la cuantía es nula o negativa
     */
    public void actualizarCuantia(BigDecimal nuevaCuantia)
    {
        if (nuevaCuantia == null)
        {
            throw new IllegalArgumentException("La cuantía no puede ser nula.");
        }
        if (nuevaCuantia.compareTo(BigDecimal.ZERO) < 0)
        {
            throw new IllegalArgumentException("La cuantía no puede ser negativa.");
        }
        this.cuantia = nuevaCuantia;
    }

    /**
     * Obtiene la cuantía actual.
     * @return cuantía exacta en BigDecimal.
     */
    public BigDecimal obtenerCuantia()
    {
        return this.cuantia;
    }

    /**
     * Obtiene la divisa actual.
     * @return divisa asociada al valor.
     */
    public Divisa obtenerDivisa()
    {
        return this.divisa;
    }
}