package usuarios;


import finanzas.InformacionBancaria;
import finanzas.Divisa;
import finanzas.Valor;
import productos.Categoria;
import productos.ProductoMercadillo;
import relaciones.*;
import productos.Imagen;
import productos.ProductoTienda;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Cliente de la aplicación.
 * 
 * @author Bruno Montero
 * @version 1.0
 */
public class Cliente extends Usuario
{
    private static final long serialVersionUID = 1L;

    /**
     * Nick del cliente
     */
    private String nick;

    /**
     * Lista de notificaciones del cliente
     */
    private List<Notificacion> misNotificaciones;

    /**
     * Pedidos realizados por el cliente
     */
    private List<Pedido> misPedidos;

    /**
     * Carrito actual del cliente
     */
    private Carrito carrito;

    /**
     * Constructor de cliente.
     *
     * @param dni DNI del cliente
     * @param id identificador interno
     * @param nombre nombre del cliente
     * @param hash hash (SHA-256) de la contraseña
     * @param nick nombre de usuario público
     */
    public Cliente(String dni, int id, String nombre, String hash, String nick)
    {
        super(dni, id, nombre, hash);
        this.nick = nick;
        this.misNotificaciones = new ArrayList<>();
        this.misPedidos = new ArrayList<>();
        this.carrito = new Carrito(new ArrayList<>(), this);
    }

    /**
     * Crea una opinión sobre un producto de tienda.
     *
     * @param producto producto a valorar
     * @param valor puntuación (normalmente 0..5)
     * @return true si se ha podido registrar la opinión, false si el producto es null o el producto lo rechaza
     */
    public boolean opinarProducto(ProductoTienda producto, float valor)
    {
        /* Si no hay producto, no se puede opinar. */
        if (producto == null)
        {
            return false;
        }
        /* Delegamos en el producto: el producto es quien "guarda" la reseña. */
        return producto.crearOpinion(valor, this, null);
    }

    /**
     * Sube un producto al mercadillo.
     *
     * @param nombre nombre del producto
     * @param descripcion descripción del producto
     * @param imagenes imágenes asociadas (puede ser null)
     * @param informacionBancaria información bancaria para el proceso de venta
     * @return true si se ha podido crear y registrar, false si faltan datos mínimos
     */
    public boolean subirProducto(String nombre, String descripcion, List<Imagen> imagenes, InformacionBancaria informacionBancaria)
    {
        /* Pedimos nombre e información bancaria, porque subir sin eso no tendría sentido. */
        if (nombre == null || nombre.isBlank() || descripcion == null || descripcion.isBlank() || informacionBancaria == null)
        {
            return false;
        }

        /* Queda pendiente de valoración (la tienda lo revisa). */
        ProductoMercadillo producto = new ProductoMercadillo(nombre, descripcion, this, EstadoProducto.PENDIENTE_VALORACION);
        
        Aplicacion.getInstance().registrarProductoMercadillo(producto);
        misNotificaciones.add(new Notificacion("Producto subido al mercadillo: " + nombre, LocalDateTime.now()));
        return true;
    }

    /**
     * Método para hacer ofertas sobre productos del mercadillo.
     * 
     * @param demandado producto en el que está interesado el cliente.
     * @param ofertados productos ofertados como constituyentes de la oferta.
     * @return true si se ha podido efectuar la oferta (no indica que se haya aceptado), false si ha ocurrido algún error.
     */
    public boolean hacerOferta(ProductoMercadillo demandado, List<ProductoMercadillo> ofertados)
    {
        if (demandado == null || ofertados == null || ofertados.isEmpty() || !demandado.estaDisponible())
        {
            return false;
        }
        for (ProductoMercadillo p : ofertados)
        {
            if (p == null || !p.estaDisponible()) return false;
        }

        Oferta nuevaOferta = new Oferta(ofertados, this);

        demandado.recibirOferta(nuevaOferta);

        misNotificaciones.add(new Notificacion("Oferta enviada por un producto del mercadillo", LocalDateTime.now()));
        return true;
    }

    /**
     * Añade un producto al carrito del cliente.
     * 
     * @param producto producto a reservar.
     * @param cantidad unidades a reservar.
     * @return true si se añade correctamente, false en caso de error de validación o stock.
     */
    public boolean anadirProductoAlCarrito(ProductoTienda producto, int cantidad)
    {
        try
        {
            this.carrito.anadirProducto(producto, cantidad);
            return true;
        }
        catch (IllegalArgumentException | IllegalStateException e)
        {
            return false;
        }
    }

    /**
     * Método para aceptar una oferta por parte del cliente, finalizando el intercambio.
     * 
     * @param miProducto Producto asociado a la oferta.
     * @param oferta a aceptar.
     */
    public void aceptarOferta(ProductoMercadillo miProducto, Oferta oferta)
    {
        if (miProducto == null || oferta == null)
        {
            return;
        }

        if (miProducto.aceptarOfertaUsuario(oferta))
        {
            misNotificaciones.add(new Notificacion("Oferta aceptada", LocalDateTime.now()));
        }
    }

    /**
     * Método para rechazar una oferta por parte del cliente.
     * 
     * @param miProducto producto en el que se buscará la oferta
     * @param oferta a rechazar.
     */
    public void rechazarOferta(ProductoMercadillo miProducto, Oferta oferta)
    {
        if (miProducto == null || oferta == null)
        {
            return;
        }

        if (miProducto.rechazarOfertaUsuario(oferta))
        {
            misNotificaciones.add(new Notificacion("Oferta rechazada", LocalDateTime.now()));
        }
    }

    /**
     * Retira un producto del mercadillo (en esta versión se registra una notificación).
     *
     * @param producto producto a retirar
     */
    public void retirarProducto(ProductoMercadillo producto)
    {
        if (producto == null)
        {
            return;
        }

        misNotificaciones.add(new Notificacion("Producto retirado del mercadillo", LocalDateTime.now()));
    }

    /**
     * Exporta datos del cliente a un fichero de texto.
     *
     * @param rutaDestino ruta del fichero destino
     * @return true si se ha escrito el fichero, false si la ruta es inválida o hay error de escritura
     */
    public boolean exportarMisDatos(String rutaDestino)
    {
        if (rutaDestino == null || rutaDestino.isBlank())
        {
            return false;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("nick=").append(nick).append('\n');
        if (misPedidos != null)
        {
            sb.append("pedidos=").append(misPedidos.size()).append('\n');
        }
        else
        {
            sb.append("pedidos=").append(0).append('\n');
        }
        if (misNotificaciones != null)
        {
            sb.append("notificaciones=").append(misNotificaciones.size()).append('\n');
        }
        else
        {
            sb.append("notificaciones=").append(0).append('\n');
        }

        try
        {
            Files.writeString(Path.of(rutaDestino), sb.toString(), StandardCharsets.UTF_8);
            return true;
        }
        catch (IOException e)
        {
            return false;
        }
    }

    /**
     * Efectúa una compra con el carrito actual y genera el pedido en cliente.
     *
     * @param informacionBancaria información bancaria para el pago
     * @return true si se ha generado el pedido, false si falta información o no hay nada que comprar
     */
    public boolean efectuarCompra(InformacionBancaria informacionBancaria)
    {
        if (informacionBancaria == null || carrito == null)
        {
            return false;
        }

        Valor totalCompra = carrito.calcularPrecioCarrito();
        if (totalCompra == null)
        {
            return false;
        }
        if (totalCompra.obtenerDivisa() != Divisa.EUR)
        {
            return false;
        }
        if (!informacionBancaria.procesarCobroSimulado("Compra Mercado de San Mateo", totalCompra.obtenerCuantia().doubleValue()))
        {
            return false;
        }

        List<Reserva> reservas = new ArrayList<>(carrito.mostrarReservas());
        if (reservas.isEmpty())
        {
            return false;
        }

        Pedido pedido = new Pedido();
        for (Reserva reserva : reservas)
        {
            if (reserva == null)
            {
                continue;
            }
            pedido.addLinea(new LineaPedido(reserva.obtenerNumReservados(), reserva.obtenerProducto()));
        }

        if (pedido.getLineas().isEmpty())
        {
            return false;
        }

        carrito.confirmarCompra();
        misPedidos.add(pedido);
        misNotificaciones.add(new Notificacion("Compra realizada. Pedido generado.", LocalDateTime.now()));
        return true;
    }

    /**
     * Extrae todas las categorías de los productos comprados en los pedidos del cliente
     * 
     * @return Lista con las categorías de los productos ya comprados.
     */
    public List<Categoria> obtenerCategoriasDePedidos()
    {
        List<Categoria> categoriasPedidos = new ArrayList<>();

        if (this.misPedidos != null)
        {
            for (Pedido p : this.misPedidos)
            {
                if (p != null)
                {
                    List<LineaPedido> lineas = p.getLineas();
                    if (lineas != null)
                    {
                        for (LineaPedido l : lineas)
                        {
                            if (l != null)
                            {
                                List<Categoria> categorias = l.obtenerCategoriasProducto();
                                if (categorias != null && !categorias.isEmpty())
                                {
                                    categoriasPedidos.addAll(categorias);
                                }
                            }
                        }
                    }
                }
            }
        }

        return categoriasPedidos;
    }

    /**
     * Extrae de forma segura todas las categorías de los productos que el cliente tiene en su carrito.
     * 
     * @return Lista con las categorías de los productos en el carrito.
     */
    public List<Categoria> obtenerCategoriasDelCarrito()
    {
        List<Categoria> categoriasCarrito = new ArrayList<>();

        if (this.carrito != null)
        {
            List<Reserva> reservas = this.carrito.mostrarReservas();

            if (reservas != null)
            {
                for (Reserva r : reservas)
                {
                    if (r != null)
                    {
                        List<Categoria> categoriasReserva = r.mostrarCategorias();
                        if (categoriasReserva != null && !categoriasReserva.isEmpty())
                        {
                            categoriasCarrito.addAll(categoriasReserva);
                        }
                    }
                }
            }
        }

        return categoriasCarrito;
    }

    /**
     * Comprueba si el nick de este cliente coincide con el proporcionado.
     *
     * @param nick nick a comparar
     * @return true si coinciden, false en caso contrario o si alguno es null
     */
    public boolean coincideNick(String nick)
    {
        if (nick == null || this.nick == null)
        {
            return false;
        }
        return this.nick.equals(nick);
    }
}
