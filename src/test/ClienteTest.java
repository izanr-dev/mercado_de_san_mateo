

import finanzas.Divisa;
import finanzas.InformacionBancaria;
import finanzas.Valor;
import productos.ProductoGenerico;
import usuarios.Cliente;
import usuarios.Usuario;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClienteTest
{
    private Cliente cliente;
    private ProductoGenerico productoEur;
    private ProductoGenerico productoUsd;

    @BeforeEach
    void setUp()
    {
        cliente = new Cliente("33333333C", 3, "Cliente Test", Usuario.generarHash("pass"), "nickTest");
        productoEur = new ProductoGenerico("Producto EUR", "Descripcion", 10, 20,
                new Valor(new BigDecimal("15.00"), Divisa.EUR));
        productoUsd = new ProductoGenerico("Producto USD", "Descripcion", 10, 21,
                new Valor(new BigDecimal("15.00"), Divisa.USD));
    }

    @Test
    void testAnadirProductoAlCarritoValidoDevuelveTrue()
    {
        assertTrue(cliente.anadirProductoAlCarrito(productoEur, 2));
    }

    @Test
    void testAnadirProductoAlCarritoCantidadInvalidaDevuelveFalse()
    {
        assertFalse(cliente.anadirProductoAlCarrito(productoEur, 0));
    }

    @Test
    void testEfectuarCompraConCarritoVacioDevuelveFalse()
    {
        InformacionBancaria info = new InformacionBancaria("1234567812345678", "123", LocalDate.now().plusYears(1));

        assertFalse(cliente.efectuarCompra(info));
    }

    @Test
    void testEfectuarCompraConDivisaNoEuroDevuelveFalse()
    {
        cliente.anadirProductoAlCarrito(productoUsd, 1);
        InformacionBancaria info = new InformacionBancaria("1234567812345678", "123", LocalDate.now().plusYears(1));

        assertFalse(cliente.efectuarCompra(info));
    }

    @Test
    void testEfectuarCompraConTarjetaCaducadaDevuelveFalse()
    {
        cliente.anadirProductoAlCarrito(productoEur, 1);
        InformacionBancaria info = new InformacionBancaria("1234567812345678", "123", LocalDate.now().minusDays(1));

        assertFalse(cliente.efectuarCompra(info));
    }

    @Test
    void testEfectuarCompraConServicioDisponibleDevuelveTrue()
    {
        boolean servicioDisponible;
        try
        {
            Class.forName("es.uam.eps.padsof.telecard.TeleChargeAndPaySystem");
            servicioDisponible = true;
        }
        catch (ClassNotFoundException e)
        {
            servicioDisponible = false;
        }
        Assumptions.assumeTrue(servicioDisponible, "Servicio de pago simulado no disponible en classpath.");

        cliente.anadirProductoAlCarrito(productoEur, 2);
        InformacionBancaria info = new InformacionBancaria("1234567812345678", "123", LocalDate.now().plusYears(1));

        assertTrue(cliente.efectuarCompra(info));
    }

    @Test
    void testEfectuarCompraFallidaNoModificaReservas()
    {
        cliente.anadirProductoAlCarrito(productoEur, 1);
        int reservadoAntes = productoEur.getNoProductosReservados();

        InformacionBancaria infoCaducada = new InformacionBancaria(
                "1234567812345678", "123", LocalDate.now().minusDays(1));

        assertFalse(cliente.efectuarCompra(infoCaducada));
        assertEquals(reservadoAntes, productoEur.getNoProductosReservados());
    }

    @Test
    void testEfectuarCompraExitosaLiberaReservas()
    {
        boolean servicioDisponible;
        try
        {
            Class.forName("es.uam.eps.padsof.telecard.TeleChargeAndPaySystem");
            servicioDisponible = true;
        }
        catch (ClassNotFoundException e)
        {
            servicioDisponible = false;
        }
        Assumptions.assumeTrue(servicioDisponible, "Servicio de pago simulado no disponible en classpath.");

        cliente.anadirProductoAlCarrito(productoEur, 2);
        InformacionBancaria info = new InformacionBancaria(
                "1234567812345678", "123", LocalDate.now().plusYears(1));

        assertTrue(cliente.efectuarCompra(info));
        assertEquals(0, productoEur.getNoProductosReservados());
    }
}
