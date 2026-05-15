

import finanzas.Divisa;
import finanzas.Valor;
import productos.ProductoGenerico;
import relaciones.LineaPedido;
import relaciones.Pedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PedidoTest
{
    private Pedido pedido;
    private ProductoGenerico producto;

    @BeforeEach
    void setUp()
    {
        pedido = new Pedido();
        producto = new ProductoGenerico("Producto", "Descripcion", 10, 900,
                new Valor(new BigDecimal("12.00"), Divisa.EUR));
    }

    @Test
    void testAddLineaConNullLanzaExcepcion()
    {
        assertThrows(IllegalArgumentException.class, () -> pedido.addLinea(null));
    }

    @Test
    void testGetLineasDevuelveListaInmodificable()
    {
        pedido.addLinea(new LineaPedido(1, producto));
        List<LineaPedido> lineas = pedido.getLineas();

        assertThrows(UnsupportedOperationException.class,
                () -> lineas.add(new LineaPedido(1, producto)));
    }

    @Test
    void testCalcularPrecioTotalSumaTodasLasLineas()
    {
        pedido.addLinea(new LineaPedido(2, producto)); // 24
        pedido.addLinea(new LineaPedido(1, producto)); // 12

        Valor total = pedido.calcularPrecioTotal();

        assertEquals(0, total.obtenerCuantia().compareTo(new BigDecimal("36.00")));
    }
}
