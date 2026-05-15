

import finanzas.Divisa;
import finanzas.Valor;
import productos.ProductoMercadillo;
import relaciones.EstadoProducto;
import relaciones.Oferta;
import usuarios.Cliente;
import usuarios.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IntercambioProductoMercadilloTest
{
    private Cliente vendedor;
    private Cliente comprador;
    private ProductoMercadillo productoDemandado;
    private ProductoMercadillo productoOfertado;

    @BeforeEach
    void setUp()
    {
        vendedor = new Cliente("77777777G", 7, "Vendedor", Usuario.generarHash("pwd"), "vende");
        comprador = new Cliente("88888888H", 8, "Comprador", Usuario.generarHash("pwd"), "compra");
        productoDemandado = new ProductoMercadillo("Consola", "Usada", vendedor, EstadoProducto.BUEN_ESTADO);
        productoOfertado = new ProductoMercadillo("Juego", "Precintado", comprador, EstadoProducto.BUEN_ESTADO);
    }

    @Test
    void testProductoMercadilloConVendedorNuloLanzaExcepcion()
    {
        assertThrows(IllegalArgumentException.class,
                () -> new ProductoMercadillo("X", "Y", null, EstadoProducto.BUEN_ESTADO));
    }

    @Test
    void testRegistrarValoracionConEstadoPendienteLanzaExcepcion()
    {
        assertThrows(IllegalArgumentException.class, () ->
                productoDemandado.registrarValoracion(
                        EstadoProducto.PENDIENTE_VALORACION,
                        new Valor(new BigDecimal("10.00"), Divisa.EUR))
        );
    }

    @Test
    void testFlujoOfertaAprobadaPorTiendaYUsuario()
    {
        Oferta oferta = new Oferta(List.of(productoOfertado), comprador);
        productoDemandado.recibirOferta(oferta);

        assertTrue(productoDemandado.revisarOfertaTienda(oferta, true));
        assertTrue(productoDemandado.aceptarOfertaUsuario(oferta));
        assertTrue(oferta.estaAceptada());
    }

    @Test
    void testNoSePuedeAceptarOfertaSinAprobacionPreviaDeTienda()
    {
        Oferta oferta = new Oferta(List.of(productoOfertado), comprador);
        productoDemandado.recibirOferta(oferta);

        assertFalse(productoDemandado.aceptarOfertaUsuario(oferta));
    }

    @Test
    void testAceptarOfertaRechazaAutomaticamenteLasDemas()
    {
        ProductoMercadillo productoOfertado2 = new ProductoMercadillo(
                "Libro", "Nuevo", comprador, EstadoProducto.BUEN_ESTADO);

        Oferta oferta1 = new Oferta(List.of(productoOfertado), comprador);
        Oferta oferta2 = new Oferta(List.of(productoOfertado2), comprador);

        productoDemandado.recibirOferta(oferta1);
        productoDemandado.recibirOferta(oferta2);

        assertTrue(productoDemandado.revisarOfertaTienda(oferta1, true));
        assertTrue(productoDemandado.revisarOfertaTienda(oferta2, true));
        assertTrue(productoDemandado.aceptarOfertaUsuario(oferta1));

        assertTrue(oferta1.estaAceptada());
        assertFalse(oferta2.estaAceptada());
        assertFalse(productoDemandado.aceptarOfertaUsuario(oferta2));
    }
}
