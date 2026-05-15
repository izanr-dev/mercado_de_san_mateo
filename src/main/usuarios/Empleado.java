package usuarios;


import java.util.ArrayList;
import java.util.List;

/**
 * Empleado del sistema con un conjunto de permisos.
 * 
 * @author Bruno Montero
 * @version 1.0
 */
public class Empleado extends Usuario
{
    private static final long serialVersionUID = 1L;

    /**
     * Lista de permisos del empleado
     */
    private List<Autorizacion> permisos;

    /**
     * Constructor de la clase Empleado.
     *
     * @param dni DNI del empleado
     * @param id identificador interno
     * @param nombre nombre del empleado
     * @param hash hash (SHA-256) de la contraseña
     */
    public Empleado(String dni, int id, String nombre, String hash)
    {
        super(dni, id, nombre, hash);
        this.permisos = new ArrayList<>();
    }

    /**
     * Sustituye la lista de permisos del empleado.
     * Si la lista es nula, se deja una lista vacía.
     *
     * @param nuevosPermisos nueva lista de permisos (puede ser null)
     */
    public void actualizarPermisos(List<Autorizacion> nuevosPermisos)
    {
        if (nuevosPermisos != null)
        {
            this.permisos = new ArrayList<>(nuevosPermisos);
        }
        else
        {
            this.permisos = new ArrayList<>();
        }
    }

    /**
     * Comprueba si el empleado tiene un permiso especifico.
     *
     * @param permiso permiso a comprobar
     * @return true si el permiso está asignado, false en caso contrario
     */
    public boolean tienePermiso(Autorizacion permiso)
    {
        if (permiso == null || permisos == null)
        {
            return false;
        }
        return permisos.contains(permiso);
    }
}
