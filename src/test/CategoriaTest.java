

import finanzas.Divisa;
import finanzas.Valor;
import productos.Categoria;
import productos.ProductoGenerico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoriaTest
{
    private Categoria categoria;
    private ProductoGenerico producto;

    @BeforeEach
    void setUp()
    {
        categoria = new Categoria("Comics");
        producto = new ProductoGenerico("Batman", "Comic", 10, 901,
                new Valor(new BigDecimal("9.95"), Divisa.EUR));
    }

    @Test
    void testConstructorConNombreVacioLanzaExcepcion()
    {
        assertThrows(IllegalArgumentException.class, () -> new Categoria("   "));
    }

    @Test
    void testAgregarProductoNoDuplica()
    {
        categoria.agregarProducto(producto);
        categoria.agregarProducto(producto);

        assertEquals(1, categoria.getProductos().size());
    }

    @Test
    void testEliminarProductoConNullLanzaExcepcion()
    {
        assertThrows(IllegalArgumentException.class, () -> categoria.eliminarProducto(null));
    }

    @Test
    void testJerarquiaPadreHijoSeEnlazaCorrectamente()
    {
        Categoria subcategoria = new Categoria("Manga", categoria);

        assertTrue(categoria.getSubcategorias().contains(subcategoria));
        assertEquals("Manga", subcategoria.getNombre());
    }

    @Test
    void testGetProductosDevuelveListaInmodificable()
    {
        categoria.agregarProducto(producto);

        assertThrows(UnsupportedOperationException.class,
                () -> categoria.getProductos().add(producto));
    }
}
