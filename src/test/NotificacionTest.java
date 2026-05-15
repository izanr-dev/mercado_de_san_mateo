

import usuarios.Notificacion;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NotificacionTest
{
    @Test
    void testConstructorConContenidoVacioLanzaExcepcion()
    {
        assertThrows(IllegalArgumentException.class, () -> new Notificacion(" ", LocalDateTime.now()));
    }

    @Test
    void testConstructorConFechaNullLanzaExcepcion()
    {
        assertThrows(IllegalArgumentException.class, () -> new Notificacion("Aviso", null));
    }

    @Test
    void testConstructorValidoGuardaContenidoYFecha() throws Exception
    {
        LocalDateTime fecha = LocalDateTime.of(2026, 3, 29, 12, 30);
        Notificacion notificacion = new Notificacion("Pedido enviado", fecha);

        Field campoContenido = Notificacion.class.getDeclaredField("contenido");
        Field campoFecha = Notificacion.class.getDeclaredField("fecha");
        campoContenido.setAccessible(true);
        campoFecha.setAccessible(true);

        assertEquals("Pedido enviado", campoContenido.get(notificacion));
        assertEquals(fecha, campoFecha.get(notificacion));
    }
}
