package productos;


import finanzas.Valor;
import relaciones.Intercambio;
import relaciones.Oferta;
import usuarios.Cliente;
import relaciones.EstadoProducto;

/**
 * Clase para productos del mercadillo / usados / C2C
 * @author Izan Robles
 * @version 1.1
 */
public class ProductoMercadillo extends Producto
{
    private static final long serialVersionUID = 1L;

    /**Responsable de la venta, emisor de la venta (la persona que lo sube)*/
    private Cliente vendedor;
    /**Estado asociado al producto, determina su flujo de vida*/
    private EstadoProducto estadoProducto;
    /**Precio estimado por la tienda para el producto (no se muestra públicamente)*/
    private Valor precioEstimado;
    /**Gestor de intercambios asociado al producto, recoge las ofertas recibidas*/
    private Intercambio intercambio;

    /**
     * Constructor estándar de la clase ProductoMercadillo con control de errores.
     * @param nombre a asignar al producto (con sanitación automática).
     * @param descripcion a asignar al producto (con sanitación automática).
     * @param vendedor responsable de efectuar la valoración del producto.
     * @param estadoProducto estado asociado al producto (relevante para la interfaz).
     * @throws  IllegalArgumentException si alguno de los argumentos es nulo.
     */
    public ProductoMercadillo(String nombre, String descripcion, Cliente vendedor, EstadoProducto estadoProducto)
    {
    	super(nombre, descripcion);
    	
    	if (vendedor == null)
        {
            throw new IllegalArgumentException("Error de integridad: El producto debe tener un vendedor asignado.");
        }

        if (estadoProducto == null)
        {
            throw new IllegalArgumentException("Error de estado: El producto debe nacer con un estado definido.");
        }

        this.vendedor = vendedor;
        this.estadoProducto = estadoProducto;

        this.intercambio = new Intercambio();
    }

    /**
     * Recibe una oferta externa y la delega al gestor de intercambios.
     * @param oferta a añadir en el producto.
     */
    public void recibirOferta(Oferta oferta)
    {
        this.intercambio.anadirOferta(oferta);
    }

    /**
     * Método para comprobar si un producto es publicable de forma simple.
     * @return true si el producto puede publicarse (ha sido valorado y no rechazado), false en cualquier otro caso.
     */
    public boolean estaDisponible()
    {
        return this.estadoProducto == EstadoProducto.BUEN_ESTADO || this.estadoProducto == EstadoProducto.MAL_ESTADO;
    }

    /**
     * Permite a los empleados de la tienda revisar una oferta.
     * @param oferta a revisar.
     * @param aprobada booleano indicando si ha sido o no acceptada la oferta.
     * @return booleano indicando se ha podido revisar la oferta.
     */
    public boolean revisarOfertaTienda(Oferta oferta, boolean aprobada)
    {
        return this.intercambio.revisarOfertaPorTienda(oferta, aprobada);
    }

    /**
     * Permite al usuario propietario aceptar una oferta (si ya fue aprobada por tienda).
     * @param oferta a aceptar por el usuario.
     * @return booleano indicando si se ha podido aceptar la oferta
     */
    public boolean aceptarOfertaUsuario(Oferta oferta)
    {
        return this.intercambio.aceptarOfertaPorUsuario(oferta);
    }

    /**
     * Permite al usuario propietario rechazar una oferta.
     * @param oferta a rechazar por el usuario.
     * @return boolean indicando si la oferta se ha rechazado o no.
     */
    public boolean rechazarOfertaUsuario(Oferta oferta)
    {
        return this.intercambio.rechazarOfertaPorUsuario(oferta);
    }

    /**
     * Registra la valoración del producto tras su revisión por los empleados autorizados de la tienda.
     * @param nuevoEstado El estado tras la revisión.
     * @param precioEstimado El valor monetario asignado simbólicamente por la tienda.
     * @throws IllegalArgumentException Si algún argumento es nulo o el estado no es válido para este método.
     */
    public void registrarValoracion(EstadoProducto nuevoEstado, Valor precioEstimado)
    {
        if (nuevoEstado == null)
        {
            throw new IllegalArgumentException("El nuevo estado no puede ser nulo.");
        }

        if (precioEstimado == null)
        {
            throw new IllegalArgumentException("Debe asignarse un precio estimado al valorar el producto.");
        }

        if (nuevoEstado == EstadoProducto.PENDIENTE_VALORACION)
        {
            throw new IllegalArgumentException("Debe asignarse un estado válido en la valoración.");
        }

        this.estadoProducto = nuevoEstado;
        this.precioEstimado = precioEstimado.copiarInformacion();
    }
}
