

import finanzas.Divisa;
import finanzas.Valor;
import productos.ProductoTienda;
import relaciones.Aplicacion;
import relaciones.Carrito;
import usuarios.Cliente;
import usuarios.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AplicacionTest
{
    private Aplicacion app;

    @BeforeEach
    void setUp() throws Exception
    {
        resetAplicacionSingleton();
        app = Aplicacion.getInstance();
    }

    @Test
    void testDefinirTiempoReservasNoPositivoDevuelveFalse()
    {
        assertFalse(app.definirTiempoReservas(0));
        assertFalse(app.definirTiempoReservas(-10));
    }

    @Test
    void testCrearProductoTiendaValidoYBuscarPorKeyword()
    {
        boolean creado = app.crearProductoTienda(
                "Comic Batman",
                "Edicion especial",
                new Valor(new BigDecimal("9.99"), Divisa.EUR),
                5
        );

        List<ProductoTienda> resultados = app.buscarProductos(List.of("Batman"));

        assertTrue(creado);
        assertEquals(1, resultados.size());
    }

    @Test
    void testAplicarDescuentosConCarritoNullDevuelveNull()
    {
        assertNull(app.aplicarDescuentos(null));
    }

    @Test
    void testAplicarDescuentosIgnoraDescuentoNoVigente()
    {
        app.crearProductoTienda("Producto", "Descripcion", new Valor(new BigDecimal("100.00"), Divisa.EUR), 10);
        ProductoTienda producto = app.verCatalogo().get(0);

        Cliente cliente = new Cliente("44444444D", 4, "Cliente", Usuario.generarHash("pwd"), "nick");
        Carrito carrito = new Carrito(new java.util.ArrayList<>(), cliente);
        carrito.anadirProducto(producto, 1);

        app.definirDescuentoPorValorPorcentual(
                new Valor(new BigDecimal("50.00"), Divisa.EUR),
                50,
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(10)
        );

        Valor base = carrito.calcularPrecioCarrito();
        Valor aplicado = app.aplicarDescuentos(carrito);

        assertEquals(0, aplicado.compararCuantias(base));
    }

    @Test
    void testCrearPackConDivisasDistintasDevuelveFalse()
    {
        app.crearProductoTienda("A", "Desc A", new Valor(new BigDecimal("5.00"), Divisa.EUR), 5);
        app.crearProductoTienda("B", "Desc B", new Valor(new BigDecimal("3.00"), Divisa.USD), 5);
        List<ProductoTienda> catalogo = app.verCatalogo();

        assertFalse(app.crearPack(Arrays.asList(catalogo.get(0), catalogo.get(1))));
    }

    @Test
    void testCrearPackValidoIncrementaCatalogo()
    {
        app.crearProductoTienda("A", "Desc A", new Valor(new BigDecimal("5.00"), Divisa.EUR), 5);
        app.crearProductoTienda("B", "Desc B", new Valor(new BigDecimal("3.00"), Divisa.EUR), 5);

        int tamAntes = app.verCatalogo().size();
        List<ProductoTienda> catalogo = app.verCatalogo();

        assertTrue(app.crearPack(Arrays.asList(catalogo.get(0), catalogo.get(1))));
        assertEquals(tamAntes + 1, app.verCatalogo().size());
    }

    @Test
    void testAplicarDescuentosDevuelveElMejorPrecio()
    {
        app.crearProductoTienda("Producto", "Desc", new Valor(new BigDecimal("100.00"), Divisa.EUR), 10);
        ProductoTienda producto = app.verCatalogo().get(0);

        Cliente cliente = new Cliente("12121212A", 12, "Cliente", Usuario.generarHash("pwd"), "nick12");
        Carrito carrito = new Carrito(new java.util.ArrayList<>(), cliente);
        carrito.anadirProducto(producto, 1);

        assertTrue(app.definirDescuentoPorValorPorcentual(
                new Valor(new BigDecimal("50.00"), Divisa.EUR), 10,
                LocalDate.now(), LocalDate.now().plusDays(2)));

        assertTrue(app.definirDescuentoPorCantidadCuantitativo(
                1, new Valor(new BigDecimal("30.00"), Divisa.EUR),
                LocalDate.now(), LocalDate.now().plusDays(2)));

        Valor resultado = app.aplicarDescuentos(carrito);
        assertEquals(0, resultado.obtenerCuantia().compareTo(new BigDecimal("70.00")));
    }

    private static void resetAplicacionSingleton() throws Exception
    {
        Field campoInstancia = Aplicacion.class.getDeclaredField("instancia");
        campoInstancia.setAccessible(true);
        campoInstancia.set(null, null);
    }
}
