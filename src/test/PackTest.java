

import finanzas.Divisa;
import finanzas.Valor;
import productos.Pack;
import productos.ProductoGenerico;
import productos.ProductoTienda;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PackTest
{
    @Test
    void testConstructorConPrecioNullLanzaExcepcion()
    {
        assertThrows(IllegalArgumentException.class, () -> new Pack("Pack A", "Descripcion", 3, 1, null, new ArrayList<>()));
    }

    @Test
    void testPackValidoPermiteReservarYLiberarUnidades()
    {
        Pack pack = new Pack(
                "Pack Retro",
                "Pack de prueba",
                5,
                10,
                new Valor(new BigDecimal("20.00"), Divisa.EUR),
                new ArrayList<>()
        );

        assertTrue(pack.reservarUnidades(2));
        assertEquals(2, pack.getNoProductosReservados());
        assertTrue(pack.liberarUnidades(1));
        assertEquals(1, pack.getNoProductosReservados());
    }

    @Test
    void testPackValidoConStockInicialCeroNoTieneStock()
    {
        Pack pack = new Pack(
                "Pack Sin Stock",
                "Agotado",
                0,
                11,
                new Valor(new BigDecimal("10.00"), Divisa.EUR),
                new ArrayList<>()
        );

        assertFalse(pack.hayStock());
    }

    @Test
    void testConstructorGuardaListaProductosRecibida() throws Exception
    {
        ProductoTienda producto = new ProductoGenerico(
                "Producto 1",
                "Desc",
                3,
                99,
                new Valor(new BigDecimal("5.00"), Divisa.EUR)
        );
        List<ProductoTienda> productos = new ArrayList<>();
        productos.add(producto);

        Pack pack = new Pack(
                "Pack Lista",
                "Pack con productos",
                2,
                12,
                new Valor(new BigDecimal("30.00"), Divisa.EUR),
                productos
        );

        Field campoProductos = Pack.class.getDeclaredField("productos");
        campoProductos.setAccessible(true);
        Object listaInterna = campoProductos.get(pack);

        assertSame(productos, listaInterna);
    }
}
