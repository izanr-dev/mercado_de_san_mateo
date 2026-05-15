

import finanzas.Divisa;
import finanzas.Valor;
import productos.ProductoGenerico;
import usuarios.Cliente;
import usuarios.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductoTiendaTest
{
    private ProductoGenerico producto;
    private Cliente autor;

    @BeforeEach
    void setUp()
    {
        producto = new ProductoGenerico(
                "Figura Test",
                "Descripcion valida",
                10,
                1,
                new Valor(new BigDecimal("10.00"), Divisa.EUR)
        );
        autor = new Cliente("11111111A", 1, "Autor", Usuario.generarHash("1234"), "autor");
    }

    @Test
    void testConstructorConPrecioNuloLanzaExcepcion()
    {
        assertThrows(IllegalArgumentException.class,
                () -> new ProductoGenerico("P", "D", 1, 99, null));
    }

    @Test
    void testGetMediaOpinionesSinResenasDevuelveCero()
    {
        assertEquals(0f, producto.getMediaOpiniones(), 0.0001f);
    }

    @Test
    void testCrearOpinionConAutorNuloDevuelveFalse()
    {
        assertFalse(producto.crearOpinion(3.5f, null, "Comentario"));
    }

    @Test
    void testCrearOpinionValidaActualizaMedia()
    {
        producto.crearOpinion(4f, autor, "Buena");
        producto.crearOpinion(2f, autor, "Regular");

        assertEquals(3f, producto.getMediaOpiniones(), 0.0001f);
    }

    @Test
    void testReservarYLiberarUnidadesActualizaReservado()
    {
        assertTrue(producto.reservarUnidades(3));
        assertEquals(3, producto.getNoProductosReservados());

        assertTrue(producto.liberarUnidades(2));
        assertEquals(1, producto.getNoProductosReservados());
    }

    @Test
    void testReservarUnidadesConCantidadNoPositivaDevuelveFalse()
    {
        assertFalse(producto.reservarUnidades(0));
        assertFalse(producto.reservarUnidades(-1));
    }

    @Test
    void testDecrementarStockPorEncimaDeDisponibleDevuelveFalse()
    {
        producto.reservarUnidades(9); // disponible real: 1

        assertFalse(producto.decrementarStock(2));
    }
}
