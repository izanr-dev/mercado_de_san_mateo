

import finanzas.Divisa;
import finanzas.Valor;
import productos.ProductoGenerico;
import relaciones.Carrito;
import relaciones.Reserva;
import usuarios.Cliente;
import usuarios.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CarritoTest
{
    private Cliente cliente;
    private ProductoGenerico productoEur;
    private ProductoGenerico productoUsd;
    private Carrito carrito;

    @BeforeEach
    void setUp()
    {
        cliente = new Cliente("22222222B", 2, "Cliente", Usuario.generarHash("abcd"), "cliente");
        productoEur = new ProductoGenerico("P1", "Desc1", 10, 10, new Valor(new BigDecimal("5.00"), Divisa.EUR));
        productoUsd = new ProductoGenerico("P2", "Desc2", 10, 11, new Valor(new BigDecimal("6.00"), Divisa.USD));
        carrito = new Carrito(new ArrayList<>(), cliente);
    }

    @Test
    void testCalcularPrecioCarritoVacioDevuelveNull()
    {
        assertNull(carrito.calcularPrecioCarrito());
    }

    @Test
    void testAnadirProductoValidoIncrementaElementos()
    {
        carrito.anadirProducto(productoEur, 2);

        assertEquals(2, carrito.contarElementos());
        assertNotNull(carrito.calcularPrecioCarrito());
    }

    @Test
    void testAnadirProductoDuplicadoLanzaExcepcion()
    {
        carrito.anadirProducto(productoEur, 1);

        assertThrows(IllegalArgumentException.class, () -> carrito.anadirProducto(productoEur, 1));
    }

    @Test
    void testAnadirProductoConDivisaDistintaLanzaExcepcion()
    {
        carrito.anadirProducto(productoEur, 1);

        assertThrows(IllegalArgumentException.class, () -> carrito.anadirProducto(productoUsd, 1));
    }

    @Test
    void testReservaCaducadaSeLimpiaEnCalculo()
    {
        productoEur.reservarUnidades(1);
        Reserva caducada = new Reserva(LocalDateTime.now().minusMinutes(5), 1, productoEur);
        Carrito carritoConCaducada = new Carrito(new ArrayList<>(List.of(caducada)), cliente);

        assertNull(carritoConCaducada.calcularPrecioCarrito());
        assertEquals(0, productoEur.getNoProductosReservados());
    }

    @Test
    void testConfirmarCompraVaciaReservasYLiberaStockReservado()
    {
        carrito.anadirProducto(productoEur, 3);
        assertEquals(3, productoEur.getNoProductosReservados());

        carrito.confirmarCompra();

        assertEquals(0, carrito.contarElementos());
        assertEquals(0, productoEur.getNoProductosReservados());
        assertTrue(productoEur.hayStock());
    }

    @Test
    void testLiberarProductoNoExistenteLanzaExcepcionYNoModificaEstado()
    {
        carrito.anadirProducto(productoEur, 1);
        int reservadoAntes = productoEur.getNoProductosReservados();

        assertThrows(IllegalArgumentException.class, () -> carrito.liberar(productoUsd));

        assertEquals(reservadoAntes, productoEur.getNoProductosReservados());
        assertEquals(1, carrito.contarElementos());
    }

    @Test
    void testVaciarCarritoLiberaTodasLasReservas()
    {
        ProductoGenerico productoEur2 = new ProductoGenerico(
                "P3", "Desc3", 10, 12, new Valor(new BigDecimal("4.00"), Divisa.EUR));

        carrito.anadirProducto(productoEur, 2);
        carrito.anadirProducto(productoEur2, 1);

        carrito.vaciarCarrito();

        assertEquals(0, carrito.contarElementos());
        assertEquals(0, productoEur.getNoProductosReservados());
        assertEquals(0, productoEur2.getNoProductosReservados());
    }

    @Test
    void testAnadirProductoSinStockLanzaExcepcionYNoInsertaReserva()
    {
        productoEur.reservarUnidades(10);

        assertThrows(IllegalStateException.class, () -> carrito.anadirProducto(productoEur, 1));

        assertEquals(10, productoEur.getNoProductosReservados());
        assertEquals(0, carrito.contarElementos());
    }
}
