

import finanzas.Divisa;
import finanzas.Valor;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValorTest
{
    @Test
    void testConstructorConCuantiaNegativaLanzaExcepcion()
    {
        assertThrows(IllegalArgumentException.class,
                () -> new Valor(new BigDecimal("-1.00"), Divisa.EUR));
    }

    @Test
    void testIncrementarConMismaDivisaActualizaCuantia()
    {
        Valor base = new Valor(new BigDecimal("10.00"), Divisa.EUR);
        Valor sumando = new Valor(new BigDecimal("2.50"), Divisa.EUR);

        base.incrementar(sumando);

        assertEquals(0, base.obtenerCuantia().compareTo(new BigDecimal("12.50")));
    }

    @Test
    void testIncrementarConDivisaDistintaLanzaExcepcion()
    {
        Valor eur = new Valor(new BigDecimal("10.00"), Divisa.EUR);
        Valor usd = new Valor(new BigDecimal("1.00"), Divisa.USD);

        assertThrows(IllegalArgumentException.class, () -> eur.incrementar(usd));
    }

    @Test
    void testDecrementarPorDebajoDeCeroLanzaExcepcion()
    {
        Valor base = new Valor(new BigDecimal("5.00"), Divisa.EUR);
        Valor sustraendo = new Valor(new BigDecimal("10.00"), Divisa.EUR);

        assertThrows(IllegalStateException.class, () -> base.decrementar(sustraendo));
    }

    @Test
    void testAplicarDescuentoPorcentualValidoRedondeaCorrectamente()
    {
        Valor base = new Valor(new BigDecimal("100.00"), Divisa.EUR);

        base.aplicarDescuentoPorcentual(15);

        assertEquals(0, base.obtenerCuantia().compareTo(new BigDecimal("85.00")));
    }

    @Test
    void testAplicarDescuentoPorcentualInvalidoLanzaExcepcion()
    {
        Valor base = new Valor(new BigDecimal("10.00"), Divisa.EUR);

        assertThrows(IllegalArgumentException.class, () -> base.aplicarDescuentoPorcentual(101));
    }
}
