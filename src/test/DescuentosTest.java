import  descuentos.DescuentoPorCantidadCuantitativo;
import descuentos.DescuentoPorCantidadPorcentual;
import descuentos.DescuentoPorProducto;
import descuentos.DescuentoPorValorPorcentual;
import finanzas.Divisa;
import finanzas.Valor;
import productos.ProductoGenerico;
import relaciones.Carrito;
import usuarios.Cliente;
import usuarios.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DescuentosTest
{
    private Carrito carrito;
    private ProductoGenerico p1;
    private ProductoGenerico p2;

    @BeforeEach
    void setUp()
    {
        Cliente cliente = new Cliente("55555555E", 5, "Cliente", Usuario.generarHash("pwd"), "nick5");
        carrito = new Carrito(new ArrayList<>(), cliente);
        p1 = new ProductoGenerico("P1", "Desc", 10, 101, new Valor(new BigDecimal("20.00"), Divisa.EUR));
        p2 = new ProductoGenerico("P2", "Desc", 10, 102, new Valor(new BigDecimal("10.00"), Divisa.EUR));
    }

    @Test
    void testDescuentoPorCantidadPorcentualAplicaCuandoSuperaMinimo()
    {
        carrito.anadirProducto(p1, 2);
        DescuentoPorCantidadPorcentual descuento = new DescuentoPorCantidadPorcentual(
                LocalDate.now(), LocalDate.now().plusDays(3), 2, 10);

        Valor resultado = descuento.calcularPrecioConDescuento(carrito);

        assertNotNull(resultado);
        assertEquals(0, resultado.obtenerCuantia().compareTo(new BigDecimal("36.00")));
    }

    @Test
    void testDescuentoPorCantidadCuantitativoNuncaDejaPrecioNegativo()
    {
        carrito.anadirProducto(p2, 1); // total 10
        DescuentoPorCantidadCuantitativo descuento = new DescuentoPorCantidadCuantitativo(
                LocalDate.now(), LocalDate.now().plusDays(3), 1,
                new Valor(new BigDecimal("50.00"), Divisa.EUR));

        Valor resultado = descuento.calcularPrecioConDescuento(carrito);

        assertNotNull(resultado);
        assertEquals(0, resultado.obtenerCuantia().compareTo(BigDecimal.ZERO));
    }

    @Test
    void testDescuentoPorProductoSoloAfectaProductoAsociado()
    {
        carrito.anadirProducto(p1, 1); // 20
        carrito.anadirProducto(p2, 1); // 10  => total 30
        DescuentoPorProducto descuento = new DescuentoPorProducto(
                LocalDate.now(), LocalDate.now().plusDays(3), p1, 50);

        Valor resultado = descuento.calcularPrecioConDescuento(carrito);

        assertNotNull(resultado);
        assertEquals(0, resultado.obtenerCuantia().compareTo(new BigDecimal("20.00")));
    }

    @Test
    void testDescuentoPorValorPorcentualConCarritoNuloLanzaExcepcion()
    {
        DescuentoPorValorPorcentual descuento = new DescuentoPorValorPorcentual(
                LocalDate.now(), LocalDate.now().plusDays(3),
                new Valor(new BigDecimal("10.00"), Divisa.EUR), 10);

        assertThrows(IllegalArgumentException.class, () -> descuento.calcularPrecioConDescuento(null));
    }

    @Test
    void testEstaVigenteIncluyeExtremosYExcluyeFueraDeRango()
    {
        DescuentoPorValorPorcentual descuento = new DescuentoPorValorPorcentual(
                LocalDate.now(), LocalDate.now().plusDays(2),
                new Valor(new BigDecimal("10.00"), Divisa.EUR), 10);

        assertTrue(descuento.estaVigente(LocalDate.now()));
        assertTrue(descuento.estaVigente(LocalDate.now().plusDays(2)));
        assertFalse(descuento.estaVigente(LocalDate.now().minusDays(1)));
        assertFalse(descuento.estaVigente(LocalDate.now().plusDays(3)));
    }
}
