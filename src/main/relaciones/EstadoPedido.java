package relaciones;

/**
 * Enumeración para los estados de un pedido en la tienda
 */
public enum EstadoPedido
{
    /**
     * Estado para periodo entre la compra y la recogida en tienda.
     */
    PENDIENTE,

    /**
     * Estado para pedidos ya recogidos, indican fin del flujo de vida.
     */
    RECOGIDO,
}

