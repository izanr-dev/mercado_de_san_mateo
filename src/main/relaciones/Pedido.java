package relaciones;

import java.io.Serializable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import finanzas.Divisa;
import finanzas.Valor;

/**
 * Clase para gestionar pedidos completos.
 * @author Izan Robles
 * @version 1.1
 */
public class Pedido implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * Fecha exacta (día, hora, minuto y segundo) en la que se generó la compra.
     * Es inmutable tras la creación del pedido.
     */
    private final LocalDateTime fecha;

    /**
     * Estado actual en el ciclo de vida del pedido (ej. PENDIENTE, RECOGIDO).
     */
    private EstadoPedido estado;

    /**
     * Relación de productos adquiridos en el pedido junto con sus cantidades.
     */
    private final List<LineaPedido> lineas;

    /**
     * Crea un pedido con la fecha actual y estado PENDIENTE.
     */
    public Pedido()
    {
        this.fecha = LocalDateTime.now();
        this.estado = EstadoPedido.PENDIENTE;
        this.lineas = new ArrayList<>();
    }

    /**
     * Método para añadir una linea de pedido al pedido completo, permite
     * gestionar los diferentes productos comprados según en número de unidades,
     * almacenando la información sin duplicados para optimización.
     * @param linea a añadir al pedido (No puede ser nula).
     */
    public void addLinea(LineaPedido linea)
    {
        if (linea == null)
        {
            throw new IllegalArgumentException("La línea de pedido no puede ser nula");
        }

        this.lineas.add(linea);
    }

    /**
     * Método para obtener una lista con las lineas de pedido, útil
     * para imprimir información o revisar datos.
     * @return copia inmodificable de la lista de lineas de pedido.
     */
    public List<LineaPedido> getLineas()
    {
        return Collections.unmodifiableList(this.lineas);
    }

    /**
     * Método para obtener el precio total sin considerar descuentos.
     * @return objeto Valor con la suma de los precios de cada linea de pedido asociada.
     */
    public Valor calcularPrecioTotal()
    {
        Valor precioPedido = new Valor(BigDecimal.ZERO, Divisa.EUR);

        for (LineaPedido lp : lineas)
        {
            precioPedido.incrementar(lp.calcularPrecioLinea());
        }

        return precioPedido;
    }

    /**
     * Método para marcar un pedido como recogido.
     */
    public void marcarRecogido()
    {
        this.estado = EstadoPedido.RECOGIDO;
    }
}
