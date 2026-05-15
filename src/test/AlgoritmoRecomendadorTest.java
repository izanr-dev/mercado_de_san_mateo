

import finanzas.Divisa;
import finanzas.Valor;
import productos.ProductoGenerico;
import productos.ProductoTienda;
import relaciones.AlgoritmoRecomendadorGeneral;
import relaciones.AlgoritmoRecomendadorPersonalizado;
import usuarios.Cliente;
import usuarios.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlgoritmoRecomendadorTest
{
    private Cliente cliente;
    private ProductoGenerico pValorAlto;
    private ProductoGenerico pValorBajo;
    private AlgoritmoRecomendadorGeneral general;
    private AlgoritmoRecomendadorPersonalizado personalizado;

    @BeforeEach
    void setUp()
    {
        cliente = new Cliente("66666666F", 6, "Cliente", Usuario.generarHash("pwd"), "nick6");
        pValorAlto = new ProductoGenerico("Alto", "Desc", 10, 401, new Valor(new BigDecimal("10.00"), Divisa.EUR));
        pValorBajo = new ProductoGenerico("Bajo", "Desc", 1, 402, new Valor(new BigDecimal("2.00"), Divisa.EUR));
        general = new AlgoritmoRecomendadorGeneral();
        personalizado = new AlgoritmoRecomendadorPersonalizado();
    }

    @Test
    void testAlgoritmoGeneralConCantidadNoPositivaDevuelveListaVacia()
    {
        List<ProductoTienda> resultado = general.generarRecomendaciones(
                cliente, List.of(pValorAlto, pValorBajo), 0);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void testAlgoritmoGeneralOrdenaPorValorDeStock()
    {
        List<ProductoTienda> resultado = general.generarRecomendaciones(
                cliente, List.of(pValorBajo, pValorAlto), 2);

        assertEquals(2, resultado.size());
        assertEquals(pValorAlto, resultado.get(0));
    }

    @Test
    void testAlgoritmoPersonalizadoSinHistorialDevuelveListaVacia()
    {
        List<ProductoTienda> resultado = personalizado.generarRecomendaciones(
                cliente, new ArrayList<>(List.of(pValorAlto, pValorBajo)), 2);

        assertTrue(resultado.isEmpty());
    }
}
