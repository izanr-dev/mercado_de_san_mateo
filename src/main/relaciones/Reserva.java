package relaciones;

import java.io.Serializable;

import finanzas.Valor;
import productos.Categoria;
import productos.ProductoTienda;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;

/**
 * Clase para gestionar la reserva temporal de un producto específico.
 * @author Izan Robles
 * @version 1.3
 */
public class Reserva implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * Fecha exacta (día, hora, minuto y segundo) límite en la que expira la reserva.
     */
    private final LocalDateTime fechaLimite;

    /**
     * Cantidad de unidades del producto que han sido reservadas.
     */
    private final int numReservados;

    /**
     * Producto de la tienda asociado a esta reserva.
     */
    private final ProductoTienda producto;

    /**
     * Crea una nueva reserva para un producto específico, estableciendo su cantidad y caducidad.
     * Realiza comprobaciones de seguridad para evitar datos corruptos.
     * @param fechaLimite momento exacto en el que la reserva dejará de ser válida.
     * @param numReservados cantidad de unidades a reservar.
     * @param producto artículo de la tienda que se está reservando.
     * @throws IllegalArgumentException si la fecha o el producto son nulos, o si la cantidad es menor o igual a cero.
     */
    public Reserva(LocalDateTime fechaLimite, int numReservados, ProductoTienda producto)
    {
        if (fechaLimite == null)
        {
            throw new IllegalArgumentException("La fecha límite de la reserva no puede ser nula.");
        }

        if (producto == null)
        {
            throw new IllegalArgumentException("El producto de la reserva no puede ser nulo.");
        }

        if (numReservados <= 0)
        {
            throw new IllegalArgumentException("El número de productos reservados debe ser estrictamente mayor que cero.");
        }

        this.fechaLimite = fechaLimite;
        this.numReservados = numReservados;
        this.producto = producto;
    }

    /**
     * Método para calcular el tiempo de validez que le queda a la reserva actual en minutos.
     * @return tiempo restante de la reserva en minutos (0 si ya ha expirado).
     */
    public long calcularTiempoRestante()
    {
        long minutosRestantes = Duration.between(LocalDateTime.now(), this.fechaLimite).toMinutes();
        return minutosRestantes > 0 ? minutosRestantes : 0L;
    }

    /**
     * Método para obtener el precio total de la reserva calculando el valor del
     * producto multiplicado por la cantidad de unidades reservadas.
     * @return objeto Valor con el coste total calculado para esta reserva.
     * @throws IllegalStateException si el precio del producto asociado no está definido.
     */
    public Valor obtenerPrecioReserva()
    {
        if (this.producto.getPrecio() == null)
        {
            throw new IllegalStateException("El producto asociado no tiene un precio definido.");
        }

        Valor precioTotal = this.producto.getPrecio();
        precioTotal.multiplicar(this.numReservados);
        return precioTotal;
    }

    /**
     * Método para consultar la cantidad de unidades reservadas en esta instancia.
     * @return número entero de unidades reservadas.
     */
    public int obtenerNumReservados()
    {
        return this.numReservados;
    }

    /**
     * Obtiene el producto asociado a esta reserva.
     * @return producto reservado.
     */
    public ProductoTienda obtenerProducto()
    {
        return this.producto;
    }

    /**
     * Método para comprobar si esta reserva corresponde a un producto específico.
     * @param producto artículo de la tienda con el que se quiere comparar.
     * @return true si la reserva es del producto indicado, false en caso contrario.
     * @throws IllegalArgumentException si el producto a comparar es nulo.
     */
    public boolean esDelProducto(ProductoTienda producto)
    {
        if (producto == null)
        {
            throw new IllegalArgumentException("El producto a comparar no puede ser nulo.");
        }

        return this.producto.equals(producto);
    }

    /**
     * Cancela la reserva actual, devolviendo automáticamente las unidades
     * reservadas al stock del producto asociado en la tienda.
     * Este método mantiene un encapsulamiento estricto al no exponer el objeto ProductoTienda.
     */
    public void cancelarReserva()
    {
        this.producto.liberarUnidades(this.numReservados);
    }

    /**
     * Obtiene una lista inmodificable con las categorías asociadas al producto de la reserva.
     * @return lista inmodificable de categorías.
     */
    public List<Categoria> mostrarCategorias()
    {
        return this.producto.getCategorias();
    }
}