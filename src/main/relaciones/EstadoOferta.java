package relaciones;

/**
 * Enumeración para guardar estados de las ofertas,
 * representan su flujo de vida.
 * @author Bruno Montero
 * @version 1.1
 */
public enum EstadoOferta
{
    /**Oferta realizada, no revisada por tienda*/
    SIN_MEDIAR,
    /**Oferta aprobada por tienda, se muestra al usuario, pendiente revisión del usuario*/
    APROBADO_POR_TIENDA,
    /**Aprobad por usuario (y por tienda), se efectúa el intercambio, fin del flujo de la oferta*/
    APROBADO_POR_USUARIO,
    /**Rechazado por la tienda o por el usuario, no avanza más, fin de su flujo de vida*/
    RECHAZADO,
}
