package usuarios;


/**
 * Usuario con rol de administrador.
 *
 * @author Bruno Montero
 * @version 1.0
 */
public class Administrador extends Empleado
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructor de la clase Administrador.
     *
     * @param dni DNI del administrador
     * @param id identificador interno
     * @param nombre nombre del administrador
     * @param hash hash (SHA-256) de la contraseña
     */
    public Administrador(String dni, int id, String nombre, String hash)
    {
        super(dni, id, nombre, hash);
    }
}
