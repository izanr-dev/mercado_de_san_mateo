package relaciones;

/**
 * Enumeración para guardar estados de los productos de Mercadillo,
 * representan su flujo de vida y su estado en el mercado.
 * @author Bruno Montero
 * @version 1.1
 */
public enum EstadoProducto
{
    /**Valoración pagada pendiente de ser efectuada por los empleados autorizados (no publicado)*/
    PENDIENTE_VALORACION,
    /**Valoración realizada, producto sin daños relevantes ni desgaste (publicado)*/
    BUEN_ESTADO,
    /**Valoración realizada, producto con daños sutiles o desgaste (publicado)*/
    MAL_ESTADO,
    /**Valoración realizada, no apto de ser intercambiado en la plataforma (no publicado)*/
    RECHAZADO,
}
