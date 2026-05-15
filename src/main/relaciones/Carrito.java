package relaciones;

import java.io.Serializable;

import finanzas.Valor;
import productos.ProductoTienda;
import usuarios.Cliente;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase para gestionar el carrito de un cliente.
 * Gestiona cada reserva de producto de forma independiente.
 * @author Izan Robles
 * @version 1.2
 */
public class Carrito implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * Lista de reservas de productos que forman el carrito.
     */
    private final List<Reserva> reservas;

    /**
     * Cliente propietario del carrito.
     */
    private final Cliente cliente;

    /**
     * Elimina reservas expiradas y libera su stock asociado.
     */
    private void limpiarReservasCaducadas()
    {
        for (int i = this.reservas.size() - 1; i >= 0; i--)
        {
            Reserva r = this.reservas.get(i);
            if (r == null || r.calcularTiempoRestante() <= 0)
            {
                if (r != null)
                {
                    r.cancelarReserva();
                }
                this.reservas.remove(i);
            }
        }
    }

    /**
     * Constructor para el carrito asociado a un cliente y con una lista inicial de reservas.
     * Comprueba que los argumentos sean válidos y realiza una copia defensiva de la lista.
     * @param reservas lista de objetos Reserva a incluir en el carrito, normalmente inicializada vacía.
     * @param cliente propietario del carrito.
     * @throws IllegalArgumentException si el cliente o la lista de reservas son nulos.
     */
    public Carrito(List<Reserva> reservas, Cliente cliente)
    {
        if (cliente == null)
        {
            throw new IllegalArgumentException("El cliente asociado no puede ser nulo.");
        }
        if (reservas == null)
        {
            throw new IllegalArgumentException("La lista de reservas no puede ser nula.");
        }

        this.reservas = new ArrayList<>(reservas);
        this.cliente = cliente;
    }

    /**
     * Calcula el precio total acumulado de todas las reservas en el carrito sin
     * considerar descuentos. Adapta la divisa base según el primer producto.
     * @return Valor total obtenido. Si el carrito está vacío, devuelve null.
     */
    public Valor calcularPrecioCarrito()
    {
        limpiarReservasCaducadas();

        if (this.reservas.isEmpty())
        {
            return null;
        }

        Valor precioBase = this.reservas.get(0).obtenerPrecioReserva().copiarInformacion();
        precioBase.multiplicar(0);

        for (Reserva r : reservas)
        {
            precioBase.incrementar(r.obtenerPrecioReserva());
        }

        return precioBase;
    }

    /**
     * Añade un producto al carrito creando una reserva y bloqueando el stock en la tienda.
     * @param producto Producto a añadir de la tienda.
     * @param cantidad Cantidad de unidades deseadas.
     * @throws IllegalArgumentException si el producto es nulo, la cantidad es inválida o el producto ya está en el carrito.
     * @throws IllegalStateException si no hay stock suficiente en la tienda para hacer la reserva.
     */
    public void anadirProducto(ProductoTienda producto, int cantidad)
    {
        limpiarReservasCaducadas();

        if (producto == null)
        {
            throw new IllegalArgumentException("El producto a añadir no puede ser nulo.");
        }

        if (cantidad <= 0)
        {
            throw new IllegalArgumentException("La cantidad a añadir debe ser estrictamente mayor que cero.");
        }

        for (Reserva r : this.reservas)
        {
            if (r.esDelProducto(producto))
            {
                throw new IllegalArgumentException("El producto ya se encuentra en el carrito.");
            }
        }

        if (!this.reservas.isEmpty())
        {
            try
            {
                this.reservas.get(0).obtenerPrecioReserva().compararCuantias(producto.getPrecio());
            }
            catch (IllegalArgumentException e)
            {
                throw new IllegalArgumentException("No se pueden mezclar productos con distinta divisa en el mismo carrito.");
            }
        }

        if (!producto.reservarUnidades(cantidad))
        {
            throw new IllegalStateException("No hay stock suficiente en la tienda para reservar " + cantidad + " unidades.");
        }

        long minutosReserva = Aplicacion.getInstance().getTiempoReserva();
        LocalDateTime fechaLimite = LocalDateTime.now().plusMinutes(minutosReserva);

        this.reservas.add(new Reserva(fechaLimite, cantidad, producto));
    }

    /**
     * Libera la reserva de un producto específico, devolviendo el stock a la tienda
     * y eliminándolo del carrito del cliente.
     * @param producto Producto que se desea eliminar del carrito.
     * @throws IllegalArgumentException si el producto es nulo o no se encuentra en las reservas.
     */
    public void liberar(ProductoTienda producto)
    {
        limpiarReservasCaducadas();

        if (producto == null)
        {
            throw new IllegalArgumentException("El producto a liberar no puede ser nulo.");
        }

        for (int i = 0; i < this.reservas.size(); i++)
        {
            Reserva r = this.reservas.get(i);

            if (r.esDelProducto(producto))
            {
                r.cancelarReserva();

                this.reservas.remove(i);
                return;
            }
        }

        throw new IllegalArgumentException("El producto especificado no se encuentra en el carrito.");
    }

    /**
     * Método para vaciar el carrito completamente.
     * Delega en cada reserva la liberación de su propio stock para mantener el encapsulamiento,
     * y posteriormente limpia la lista local.
     */
    public void vaciarCarrito()
    {
        limpiarReservasCaducadas();

        for (Reserva r : this.reservas)
        {
            r.cancelarReserva();
        }

        this.reservas.clear();
    }

    /**
     * Método para contar el número de elementos del carrito
     * @return entero con el número de productos reservados en el carrito.
     */
    public int contarElementos()
    {
        limpiarReservasCaducadas();

        int contador = 0;

        for (Reserva r : reservas)
        {
            contador += r.obtenerNumReservados();
        }

        return contador;
    }

    /**
     * Calcula el precio total acumulado de las reservas de un producto específico.
     * @param producto Producto a buscar en el carrito.
     * @throws IllegalArgumentException Si el producto introducido por argumento es nulo
     * @return Valor con el subtotal. Si el producto no está, devuelve null.
     */
    public Valor obtenerSubtotalDeProducto(ProductoTienda producto)
    {
        limpiarReservasCaducadas();

        if (producto == null)
        {
            throw new IllegalArgumentException("El producto a buscar no puede ser nulo.");
        }

        for (Reserva r : this.reservas)
        {
            if (r.esDelProducto(producto))
            {
                return r.obtenerPrecioReserva().copiarInformacion();
            }
        }

        return null;
    }

    /**
     * Obtiene una vista inmodificable de las reservas actuales en el carrito.
     * @return Lista inmodificable de reservas.
     */
    public List<Reserva> mostrarReservas()
    {
        limpiarReservasCaducadas();

        return java.util.Collections.unmodifiableList(this.reservas);
    }

    /**
     * Convierte las reservas vigentes en una compra confirmada y limpia el carrito.
     * Libera primero las unidades reservadas y después descuenta stock total.
     */
    public void confirmarCompra()
    {
        limpiarReservasCaducadas();

        for (Reserva reserva : this.reservas)
        {
            ProductoTienda producto = reserva.obtenerProducto();
            int cantidad = reserva.obtenerNumReservados();

            if (!producto.liberarUnidades(cantidad))
            {
                throw new IllegalStateException("No se han podido liberar las unidades reservadas para confirmar la compra.");
            }
            if (!producto.decrementarStock(cantidad))
            {
                throw new IllegalStateException("No se ha podido descontar el stock al confirmar la compra.");
            }
        }

        this.reservas.clear();
    }
}