

import finanzas.Divisa;
import finanzas.Valor;
import productos.ProductoGenerico;
import relaciones.LineaPedido;
import relaciones.Reserva;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReservaLineaPedidoTest
{
    private ProductoGenerico producto;

    @BeforeEach
    void setUp()
    {
        producto = new ProductoGenerico("Producto", "Desc", 10, 300,
                new Valor(new BigDecimal("7.50"), Divisa.EUR));
    }

    @Test
    void testReservaConCantidadNoValidaLanzaExcepcion()
    {
        assertThrows(IllegalArgumentException.class,
                () -> new Reserva(LocalDateTime.now().plusMinutes(10), 0, producto));
    }

    @Test
    void testCalcularTiempoRestanteReservaCaducadaDevuelveCero()
    {
        Reserva reserva = new Reserva(LocalDateTime.now().minusMinutes(1), 1, producto);

        assertEquals(0L, reserva.calcularTiempoRestante());
    }

    @Test
    void testObtenerPrecioReservaMultiplicaPorCantidad()
    {
        Reserva reserva = new Reserva(LocalDateTime.now().plusMinutes(10), 2, producto);

        Valor total = reserva.obtenerPrecioReserva();

        assertEquals(0, total.obtenerCuantia().compareTo(new BigDecimal("15.00")));
    }

    @Test
    void testCancelarReservaLiberaUnidades()
    {
        producto.reservarUnidades(2);
        Reserva reserva = new Reserva(LocalDateTime.now().plusMinutes(10), 2, producto);

        reserva.cancelarReserva();

        assertEquals(0, producto.getNoProductosReservados());
    }

    @Test
    void testLineaPedidoConProductoNuloLanzaExcepcion()
    {
        assertThrows(IllegalArgumentException.class, () -> new LineaPedido(1, null));
    }

    @Test
    void testCalcularPrecioLineaCalculaCorrectamente()
    {
        LineaPedido linea = new LineaPedido(3, producto);

        Valor total = linea.calcularPrecioLinea();

        assertEquals(0, total.obtenerCuantia().compareTo(new BigDecimal("22.50")));
        assertTrue(linea.obtenerCategoriasProducto().isEmpty());
    }
}
