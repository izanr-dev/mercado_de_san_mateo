

import usuarios.Autorizacion;
import usuarios.Empleado;
import usuarios.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsuarioEmpleadoTest
{
    private Empleado empleado;

    @BeforeEach
    void setUp()
    {
        String hash = Usuario.generarHash("secreto");
        empleado = new Empleado("99999999Z", 9, "Empleado", hash);
    }

    @Test
    void testGenerarHashConNullDevuelveNull()
    {
        assertNull(Usuario.generarHash(null));
    }

    @Test
    void testGenerarHashValidoDevuelveCadenaNoNula()
    {
        assertNotNull(Usuario.generarHash("abc123"));
    }

    @Test
    void testCambiarContrasenaConActualCorrectaDevuelveTrue()
    {
        assertTrue(empleado.cambiarContrasena("secreto", "nuevoSecreto"));
        assertTrue(empleado.comprobarContrasena("nuevoSecreto"));
    }

    @Test
    void testCambiarContrasenaConActualIncorrectaDevuelveFalse()
    {
        assertFalse(empleado.cambiarContrasena("incorrecta", "nuevoSecreto"));
    }

    @Test
    void testRestablecerHashVacioLanzaExcepcion()
    {
        assertThrows(IllegalArgumentException.class, () -> empleado.restablecerHash(" "));
    }

    @Test
    void testActualizarPermisosYComprobarPermiso()
    {
        empleado.actualizarPermisos(List.of(Autorizacion.STOCK, Autorizacion.PEDIDOS));

        assertTrue(empleado.tienePermiso(Autorizacion.STOCK));
        assertFalse(empleado.tienePermiso(Autorizacion.MEDIACIONES));
    }

    @Test
    void testActualizarPermisosConNullDejaListaVacia()
    {
        empleado.actualizarPermisos(null);

        assertFalse(empleado.tienePermiso(Autorizacion.STOCK));
    }

    @Test
    void testRestablecerHashActualizaCredenciales()
    {
        String nuevoHash = Usuario.generarHash("otraClave");
        empleado.restablecerHash(nuevoHash);

        assertTrue(empleado.comprobarContrasena("otraClave"));
        assertFalse(empleado.comprobarContrasena("secreto"));
    }
}
