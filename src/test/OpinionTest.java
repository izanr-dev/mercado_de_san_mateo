

import relaciones.Opinion;
import usuarios.Cliente;
import usuarios.Usuario;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class OpinionTest
{
    @Test
    void testConstructorConComentarioNullGuardaCadenaVacia() throws Exception
    {
        Cliente autor = new Cliente("10101010A", 101, "Autor", Usuario.generarHash("clave"), "autor");
        Opinion opinion = new Opinion(4.5f, null, autor);

        Field campoComentario = Opinion.class.getDeclaredField("comentario");
        campoComentario.setAccessible(true);
        String comentarioInterno = (String) campoComentario.get(opinion);

        assertEquals("", comentarioInterno);
    }

    @Test
    void testGetValorDevuelveValorAsignado()
    {
        Opinion opinion = new Opinion(3.25f, "Correcto", null);

        assertEquals(3.25f, opinion.getValor(), 0.0001f);
    }

    @Test
    void testConstructorGuardaAutorRecibido() throws Exception
    {
        Cliente autor = new Cliente("20202020B", 202, "Autor2", Usuario.generarHash("clave2"), "autor2");
        Opinion opinion = new Opinion(5f, "Excelente", autor);

        Field campoAutor = Opinion.class.getDeclaredField("autor");
        campoAutor.setAccessible(true);
        Object autorInterno = campoAutor.get(opinion);

        assertNotNull(autorInterno);
        assertSame(autor, autorInterno);
    }
}
