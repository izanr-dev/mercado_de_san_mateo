package usuarios;

/**
 * Enum con los permisos que se pueden asignar a un Empleado.
 * Se usan para controlar qué acciones puede realizar en la aplicación.
 * 
 * @author Bruno Montero
 * @version 1.0
 */
public enum Autorizacion
{
    /**
     * Permiso para gestionar stock
     */
    STOCK,

    /**
     * Permiso para gestionar pedidos
     */
    PEDIDOS,

    /**
     * Permiso para gestionar valoraciones/opiniones de los productos
     */
    VALORACIONES,

    /**
     * Permiso para mediar en ofertas e intercambios.
     */
    MEDIACIONES,
}