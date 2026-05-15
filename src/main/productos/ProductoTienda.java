package productos;


import finanzas.Valor;
import relaciones.Opinion;
import usuarios.Cliente;

import java.util.List;

/**
 * Clase para almacenar los datos de un tipo de producto
 * @author Daniel Martín Jaén
 * @version 1.0
 */
public abstract class ProductoTienda extends Producto
{
    private static final long serialVersionUID = 1L;

    /**
     * Número de productos de este tipo
     */
    private int stockTotal;

    /**
     * Número de productos de este tipo reservados
     */
    private int stockReservado;

    /**
     * Id único de este tipo de producto
     */
    private final int id;

    /**
     * Objeto valor con el precio del tipo de producto
     */
    private final Valor precio;

    /**
     * Lista de reseñas de los clientes sobre este tipo de producto
     */
    private List<Opinion> reseñas;

    /**
     * Categoría del tipo de producto
     */
    private List<Categoria> categoria;

    /**
     * Constructor de la clase ProductoTienda. Toma los atributos como argumentos
     * y comprueba que sean válidos antes de crear el objeto.
     * @param nombre Nombre del tipo de producto
     * @param descripcion Descripción del tipo de producto
     * @param stockTotal Stock inicial del producto
     * @param id único del tipo de producto
     * @param precio Objeto valor con el precio inicial del producto
     * 
     * StockReservado se inicializa a 0 debido a que inicialmente no hay productos reservados.
     */
    public ProductoTienda(String nombre, String descripcion, int stockTotal, int id, Valor precio)
    {
        super(nombre, descripcion);
        if (precio == null)
        {
            throw new IllegalArgumentException("El precio no puede ser nulo.");
        }
        this.stockTotal = stockTotal;
        this.stockReservado = 0;
        this.id = id;
        this.precio = precio.copiarInformacion();

        this.reseñas = new java.util.ArrayList<>();
        this.categoria = new java.util.ArrayList<>();
    }

    /**
     * Calcula la media de todas las reseñas dentro de la lista de reseñas
     * @return devuelve la media de los valores de todas las reseñas
     */
    public float getMediaOpiniones()
    {
        if (this.reseñas == null || this.reseñas.isEmpty())
        {
            return 0f;
        }

        float sum = 0f;
        int n = 0;

        for(Opinion opinion : this.reseñas)
        {
            sum += opinion.getValor();
            n++;
        }

        return sum / n;
    }

    /**
     * Incrementa el valor de StockTotal según cantidad.
     * @param cantidad valor sumado a StockTotal
     */
    public void incrementarStock(int cantidad)
    {
        if (cantidad <= 0)
        {
            return;
        }
        this.stockTotal += cantidad;
    }

    /**
     * Decrementa el valor de StockTotal según cantidad.
     * @param cantidad valor restado a StockTotal
     * @return devuelve true si ha cambiado el valor o false si cantidad es mayor
     * a StockTotal o a StockTotal-StockReservado.
     */
    public boolean decrementarStock(int cantidad)
    {
        if (cantidad <= 0)
        {
            return false;
        }

        int disp;

        disp = this.stockTotal - this.stockReservado;
        if(cantidad > this.stockTotal || cantidad > disp)
        {
            return false;
        }

        this.stockTotal -= cantidad;
        return true;
    }

    /**
     * Comprueba si queda Stock de un producto
     * @return true si hay más stockTotal que stockReservado
     *         false si hay menos stockTotal que stockReservado
     */
    public boolean hayStock()
    {
        return this.stockReservado < this.stockTotal;
    }

    /**
     * Getter del atributo stockReservado
     * @return el valor de stockReservado
     */
    public int getNoProductosReservados()
    {
        return this.stockReservado;
    }

    /**
     * Reserva unidades de un producto, incrementando el valor de stockReservado
     * @param cantidad número de unidades que se quieren reservar
     * @return true si se han reservado las unidades correctamente
     *         false si no se han podido reservar las unidades
     */
    public boolean reservarUnidades(int cantidad)
    {
        if (cantidad <= 0)
        {
            return false;
        }

        int disp;

        //Se comprueba que el número de unidades disponibles no sea menor que cantidad
        disp = this.stockTotal - this.stockReservado;
        if(cantidad > disp || cantidad > this.stockTotal) 
        {
            return false;
        }

        this.stockReservado += cantidad;
        return true;
    }

    /**
     * Decrementa el valor de stockReservado según cantidad
     * @param cantidad número de unidades a liberar
     * @return true si se han podido liberar las unidades
     *         false si no se han podido liberar
     */
    public boolean liberarUnidades(int cantidad)
    {
        if(cantidad <= 0 || cantidad > this.stockReservado)
        {
            return false;
        }

        this.stockReservado -= cantidad;
        return true;
    }

    /**
     * Crea un objeto opinion y lo añade a la lista de reseñas
     * @param valor puntuación de la opinion
     * @param autor nombre del usuario que ha hecho la opinion
     * @param comentario comentarios de la opinion
     * @return true si se ha podido crear y añadir opinion a la lista de reseñas
     *         false en caso contrario
     */
    public boolean crearOpinion(float valor, Cliente autor, String comentario)
    {
        //Se comprueba que los valores sean válidos
        if(valor < 0 || autor == null) 
        {
            return false;
        } 

        Opinion op1 = new Opinion(valor, comentario, autor);

        this.reseñas.add(op1);
        return true;
    }

    /**
     * Método seguro para obtener el precio de un producto sin exponer referencias directas.
     * @return objeto Valor con la información del precio copiada del producto.
     * */
    public Valor getPrecio ()
    {
        return precio.copiarInformacion();
    }

    /**
     * Actualiza el precio del producto de forma controlada.
     * El campo precio es final (la referencia no cambia), pero el objeto Valor
     * es mutable, por lo que actualizamos su cuantía interna.
     *
     * @param nuevoPrecio Valor con el nuevo precio a asignar
     * @throws IllegalArgumentException si el nuevo precio es nulo
     */
    public void actualizarPrecio(Valor nuevoPrecio)
    {
        if (nuevoPrecio == null)
        {
            throw new IllegalArgumentException("El nuevo precio no puede ser nulo.");
        }
        /* Delegamos en la mutabilidad controlada de Valor. */
        Valor copiaNuevo = nuevoPrecio.copiarInformacion();
        /* Primero ponemos a cero y luego incrementamos con el nuevo. */
        this.precio.multiplicar(0);
        this.precio.incrementar(copiaNuevo);
    }

    /**
     * Calcula el valor económico total del stock actual del producto (precio * stockTotal).
     * Utiliza getPrecio() para operar sobre una copia segura sin alterar el original.
     * @return Objeto Valor con el importe total.
     * @throws IllegalStateException si el producto no tiene un precio definido.
     */
    public Valor calcularValorStockTotal()
    {
        Valor valorTotal = this.getPrecio(); // Obtenemos la copia segura

        if (valorTotal == null)
        {
            throw new IllegalStateException("El producto no tiene un precio definido para calcular el valor del stock.");
        }

        valorTotal.multiplicar(this.stockTotal);

        return valorTotal;
    }

    /**
     * Obtiene una copia inmodificable de las categorías asociadas al producto.
     * @return Lista inmodificable de categorías, o una lista vacía si no hay ninguna asignada.
     */
    public List<Categoria> getCategorias()
    {
        if (this.categoria == null)
        {
            return java.util.List.of();
        }
        return java.util.List.copyOf(this.categoria);
    }

    /**
     * Actualiza el nombre y/o la descripción del producto de forma controlada.
     * Si alguno de los parámetros es null, se mantiene el valor actual.
     *
     * @param nuevoNombre nuevo nombre (puede ser null para no cambiar)
     * @param nuevaDesc nueva descripción (puede ser null para no cambiar)
     */
    public void actualizarDatos(String nuevoNombre, String nuevaDesc)
    {
        if (nuevoNombre != null && !nuevoNombre.isBlank())
        {
            this.actualizarNombre(nuevoNombre);
        }
        if (nuevaDesc != null && !nuevaDesc.isBlank())
        {
            this.actualizarDescripcion(nuevaDesc);
        }
    }
}