

import usuarios.Administrador;
import usuarios.Autorizacion;
import usuarios.Empleado;
import usuarios.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdministradorTest
{
    private Administrador administrador;

    @BeforeEach
    void setUp()
    {
        administrador = new Administrador("30303030C", 303, "Admin", Usuario.generarHash("admin123"));
    }

    @Test
    void testAdministradorEsInstanciaDeEmpleado()
    {
        assertTrue(administrador instanceof Empleado);
    }

    @Test
    void testAdministradorComprobarCredencialesFunciona()
    {
        assertTrue(administrador.comprobarCredenciales("admin123"));
        assertFalse(administrador.comprobarCredenciales("incorrecta"));
    }

    @Test
    void testAdministradorPuedeGestionarPermisosComoEmpleado()
    {
        administrador.actualizarPermisos(List.of(Autorizacion.PEDIDOS, Autorizacion.STOCK));

        assertTrue(administrador.tienePermiso(Autorizacion.PEDIDOS));
        assertTrue(administrador.tienePermiso(Autorizacion.STOCK));
        assertFalse(administrador.tienePermiso(Autorizacion.MEDIACIONES));
    }
}
