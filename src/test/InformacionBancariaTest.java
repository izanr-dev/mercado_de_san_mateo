

import finanzas.InformacionBancaria;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InformacionBancariaTest
{
    private InformacionBancaria infoValida;

    @BeforeEach
    void setUp()
    {
        infoValida = new InformacionBancaria("1234567812345678", "123", LocalDate.now().plusYears(1));
    }

    private void assumeServicioDisponible()
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
    }

    @Test
    void testConstructorConNumeroTarjetaVacioLanzaExcepcion()
    {
        assertThrows(IllegalArgumentException.class,
                () -> new InformacionBancaria(" ", "123", LocalDate.now().plusYears(1)));
    }

    @Test
    void testEstaCaducadaConFechaPasadaDevuelveTrue()
    {
        InformacionBancaria info = new InformacionBancaria("1234567812345678", "123", LocalDate.now().minusDays(1));

        assertTrue(info.estaCaducada(LocalDate.now()));
    }

    @Test
    void testEstaCaducadaConFechaFuturaDevuelveFalse()
    {
        assertFalse(infoValida.estaCaducada(LocalDate.now()));
    }

    @Test
    void testProcesarCobroSimuladoConParametrosInvalidosDevuelveFalse()
    {
        assertFalse(infoValida.procesarCobroSimulado("", 10.0));
        assertFalse(infoValida.procesarCobroSimulado(null, 10.0));
        assertFalse(infoValida.procesarCobroSimulado("Compra", 0));
        assertFalse(infoValida.procesarCobroSimulado("Compra", -2.5));
    }

    @Test
    void testProcesarCobroSimuladoConTarjetaCaducadaDevuelveFalse()
    {
        InformacionBancaria info = new InformacionBancaria("1234567812345678", "123", LocalDate.now().minusDays(2));

        assertFalse(info.procesarCobroSimulado("Compra src.test", 10.0));
    }

    @Test
    void testProcesarCobroSimuladoPagoCorrectoDevuelveTrue()
    {
        assumeServicioDisponible();

        assertTrue(infoValida.procesarCobroSimulado("Compra de prueba", 12.5));
    }

    @Test
    void testProcesarCobroSimuladoPagoFallidoPorNumeroTarjetaInvalidoDevuelveFalse()
    {
        assumeServicioDisponible();

        InformacionBancaria infoTarjetaInvalida = new InformacionBancaria("123", "123", LocalDate.now().plusYears(1));

        assertFalse(infoTarjetaInvalida.procesarCobroSimulado("Compra con tarjeta invalida", 8.0));
    }

    @Test
    void testProcesarCobroSimuladoPagoFallidoPorRechazoDevuelveFalse()
    {
        assumeServicioDisponible();

        assertFalse(infoValida.procesarCobroSimulado("rechazo del pedido", 10.0));
    }

    @Test
    void testProcesarCobroSimuladoPagoFallidoPorErrorConexionDevuelveFalse()
    {
        assumeServicioDisponible();

        assertFalse(infoValida.procesarCobroSimulado("wifi caida", 10.0));
    }

    @Test
    void testProcesarCobroSimuladoAnteErroresNuncaPropagaExcepcion()
    {
        assumeServicioDisponible();

        assertDoesNotThrow(() -> infoValida.procesarCobroSimulado("rechazo controlado", 10.0));
        assertDoesNotThrow(() -> infoValida.procesarCobroSimulado("wifi caida", 10.0));
    }
}
