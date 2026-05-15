package relaciones;

import finanzas.Valor;
import productos.ProductoGenerico;
import productos.ProductoMercadillo;
import productos.ProductoTienda;
import usuarios.Autorizacion;
import usuarios.Cliente;
import usuarios.Empleado;
import usuarios.Usuario;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.time.LocalDate;

import descuentos.Descuento;
import descuentos.DescuentoPorProducto;
import descuentos.DescuentoPorCantidadCuantitativo;
import descuentos.DescuentoPorCantidadPorcentual;
import descuentos.DescuentoPorValorCuantitativo;
import descuentos.DescuentoPorValorPorcentual;

/**
 * Singleton que actúa como fachada principal de la aplicación Mercado de San Mateo.
 * Centraliza la gestión de usuarios, catálogo, descuentos y productos del mercadillo.
 * Protege el encapsulamiento interno: no expone getters ni setters de datos sensibles.
 *
 * @author Izan Robles
 * @version 1.19
 */
public class Aplicacion implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** Instancia única del Singleton */
    private static Aplicacion instancia;

    /** Tiempo de reserva en minutos aplicado a las nuevas reservas del carrito */
    private long tiempoReserva;

    /** Lista completa de usuarios registrados (clientes y empleados) */
    private List<Usuario> usuarios;

    /** Usuario que ha iniciado sesión actualmente (null si no hay sesión activa) */
    private Usuario sesionActual;

    /** Catálogo de productos disponibles en la tienda */
    private List<ProductoTienda> productosTienda;

    /** Productos del mercadillo que ya han sido autorizados (publicados) */
    private List<ProductoMercadillo> productosMercadilloAutorizados;

    /** Productos del mercadillo pendientes de valoración por empleados */
    private List<ProductoMercadillo> productosMercadilloSinValoracion;

    /** Descuentos globales vigentes aplicables al carrito */
    private List<Descuento> descuentosGlobales;

    /** Algoritmos de recomendación disponibles en la aplicación */
    private List<AlgoritmoRecomendador> algoritmos;

    /** Contador interno para generar IDs únicos de usuario */
    private int contadorIdUsuario;

    /** Contador interno para generar IDs únicos de producto */
    private int contadorIdProducto;

    /**
     * Constructor privado del Singleton.
     * Inicializa todas las listas internas y los contadores.
     */
    private Aplicacion()
    {
        this.usuarios = new ArrayList<>();
        this.productosTienda = new ArrayList<>();
        this.productosMercadilloAutorizados = new ArrayList<>();
        this.productosMercadilloSinValoracion = new ArrayList<>();
        this.descuentosGlobales = new ArrayList<>();
        this.algoritmos = new ArrayList<>();
        this.sesionActual = null;
        this.tiempoReserva = 30;
        this.contadorIdUsuario = 1;
        this.contadorIdProducto = 1;
    }

    /**
     * Obtiene la instancia única de la aplicación (patrón Singleton).
     *
     * @return instancia única de Aplicacion
     */
    public static Aplicacion getInstance()
    {
        if (instancia == null)
        {
            instancia = new Aplicacion();
        }
        return instancia;
    }

    /**
     * Inicia sesión comprobando las credenciales del usuario identificado por su DNI.
     * Si las credenciales son correctas, se guarda la sesión activa internamente.
     *
     * @param dni DNI del usuario que intenta iniciar sesión
     * @param textoPlano contraseña en texto plano para verificar
     * @return el Usuario autenticado, o null si las credenciales son incorrectas o el DNI no existe
     */
    public Usuario iniciarSesion(String dni, String textoPlano)
    {
        if (dni == null || textoPlano == null)
        {
            return null;
        }

        for (Usuario u : this.usuarios)
        {
            if (u.coincideDni(dni))
            {
                if (u.comprobarCredenciales(textoPlano))
                {
                    this.sesionActual = u;
                    return u;
                }
                return null;
            }
        }
        return null;
    }

    /**
     * Busca productos en el catálogo de la tienda cuyo nombre o descripción
     * contengan al menos una de las keywords proporcionadas.
     *
     * @param keywords lista de términos de búsqueda
     * @return lista de productos que coinciden con alguna keyword, vacía si no hay resultados
     */
    public List<ProductoTienda> buscarProductos(List<String> keywords)
    {
        if (keywords == null || keywords.isEmpty())
        {
            return new ArrayList<>();
        }

        List<ProductoTienda> resultados = new ArrayList<>();

        for (ProductoTienda producto : this.productosTienda)
        {
            if (producto == null)
            {
                continue;
            }
            for (String keyword : keywords)
            {
                if (producto.contieneKeyword(keyword))
                {
                    resultados.add(producto);
                    break;
                }
            }
        }

        return resultados;
    }

    /**
     * Registra un nuevo cliente en la aplicación.
     * Comprueba que no exista ya un usuario con el mismo DNI ni un cliente con el mismo nick.
     *
     * @param dni DNI del nuevo cliente
     * @param nombre nombre completo del cliente
     * @param nick nombre de usuario público
     * @param contTextoPlano contraseña en texto plano (se almacena solo su hash)
     * @return true si el registro se ha completado, false si los datos son inválidos o están duplicados
     */
    public boolean registrarCliente(String dni, String nombre, String nick, String contTextoPlano)
    {
        if (dni == null || dni.isBlank() || nombre == null || nombre.isBlank()
                || nick == null || nick.isBlank() || contTextoPlano == null || contTextoPlano.isBlank())
        {
            return false;
        }

        /* Comprobamos que no haya duplicados de DNI ni de nick. */
        for (Usuario u : this.usuarios)
        {
            if (u.coincideDni(dni))
            {
                return false;
            }
            if (u instanceof Cliente && ((Cliente) u).coincideNick(nick))
            {
                return false;
            }
        }

        String hash = Usuario.generarHash(contTextoPlano);
        Cliente nuevoCliente = new Cliente(dni, this.contadorIdUsuario++, nombre, hash, nick);
        this.usuarios.add(nuevoCliente);
        return true;
    }

    /**
     * Exporta los datos del cliente con sesión activa a un fichero de texto.
     * Solo funciona si el usuario actual es un cliente.
     *
     * @param rutaDestino ruta del fichero donde se exportarán los datos
     * @return true si se han exportado correctamente, false si no hay sesión o no es un cliente
     */
    public boolean exportarDatos(String rutaDestino)
    {
        if (rutaDestino == null || rutaDestino.isBlank())
        {
            return false;
        }

        if (this.sesionActual instanceof Cliente)
        {
            return ((Cliente) this.sesionActual).exportarMisDatos(rutaDestino);
        }
        return false;
    }

    /**
     * Registra un producto del mercadillo en la lista de productos pendientes de valoración.
     *
     * @param producto producto del mercadillo a registrar
     */
    public void registrarProductoMercadillo(ProductoMercadillo producto)
    {
        if (producto == null)
        {
            return;
        }
        this.productosMercadilloSinValoracion.add(producto);
    }

    /**
     * Comprueba si un empleado tiene un permiso específico.
     * Delega en el método propio del empleado para no acceder a sus datos privados.
     *
     * @param empleado empleado al que se quiere comprobar el permiso
     * @param aut autorización/permiso que se quiere verificar
     * @return true si el empleado tiene el permiso, false en caso contrario o si algún argumento es null
     */
    public boolean tienePermisoA(Empleado empleado, Autorizacion aut)
    {
        if (empleado == null || aut == null)
        {
            return false;
        }
        return empleado.tienePermiso(aut);
    }

    /**
     * Aplica todos los descuentos globales vigentes al carrito dado y devuelve
     * el mejor precio posible (el más bajo resultante de todos los descuentos).
     *
     * @param carrito carrito sobre el que calcular los descuentos
     * @return Valor con el precio final más favorable, o null si el carrito está vacío
     */
    public Valor aplicarDescuentos(Carrito carrito)
    {
        if (carrito == null)
        {
            return null;
        }

        Valor precioBase = carrito.calcularPrecioCarrito();
        if (precioBase == null)
        {
            return null;
        }

        /* Empezamos con el precio sin descuento y buscamos el mejor. */
        Valor mejorPrecio = precioBase.copiarInformacion();

        for (Descuento d : this.descuentosGlobales)
        {
            if (d == null || !d.estaVigente(LocalDate.now()))
            {
                continue;
            }
            Valor precioConDescuento = d.calcularPrecioConDescuento(carrito);
            if (precioConDescuento != null && precioConDescuento.compararCuantias(mejorPrecio) < 0)
            {
                mejorPrecio = precioConDescuento;
            }
        }

        return mejorPrecio;
    }

    /**
     * Devuelve una vista inmodificable del catálogo completo de productos de la tienda.
     *
     * @return lista inmodificable de ProductoTienda
     */
    public List<ProductoTienda> verCatalogo()
    {
        return Collections.unmodifiableList(this.productosTienda);
    }

    /**
     * Crea un nuevo producto genérico en la tienda y lo añade al catálogo.
     *
     * @param nombre nombre del producto
     * @param desc descripción del producto
     * @param precio precio inicial del producto
     * @param stockInicial unidades iniciales disponibles
     * @return true si el producto se ha creado y añadido, false si los datos son inválidos
     */
    public boolean crearProductoTienda(String nombre, String desc, Valor precio, int stockInicial)
    {
        if (nombre == null || nombre.isBlank() || desc == null || desc.isBlank()
                || precio == null || stockInicial < 0)
        {
            return false;
        }

        ProductoGenerico nuevo = new ProductoGenerico(nombre, desc, stockInicial, this.contadorIdProducto++, precio);
        this.productosTienda.add(nuevo);
        return true;
    }

    /**
     * Modifica el nombre y/o la descripción de un producto existente en la tienda.
     *
     * @param producto producto a modificar
     * @param nuevoNombre nuevo nombre (puede ser null para no cambiar)
     * @param nuevaDesc nueva descripción (puede ser null para no cambiar)
     * @return true si se ha realizado alguna modificación, false si el producto es null o no está en el catálogo
     */
    public boolean modificarProductoTienda(ProductoTienda producto, String nuevoNombre, String nuevaDesc)
    {
        if (producto == null || !this.productosTienda.contains(producto))
        {
            return false;
        }

        producto.actualizarDatos(nuevoNombre, nuevaDesc);
        return true;
    }

    /**
     * Retira un producto del catálogo de la tienda.
     *
     * @param producto producto a retirar
     */
    public void retirarProductoTienda(ProductoTienda producto)
    {
        if (producto == null)
        {
            return;
        }
        this.productosTienda.remove(producto);
    }

    /**
     * Registra un nuevo empleado en la aplicación con los permisos indicados.
     * Comprueba que no exista ya un usuario con el mismo DNI.
     *
     * @param dni DNI del nuevo empleado
     * @param nombre nombre completo del empleado
     * @param contrasena contraseña en texto plano (se almacena solo su hash)
     * @param permisos lista de permisos iniciales a asignar
     * @return true si el empleado se ha registrado correctamente, false si los datos son inválidos o el DNI ya existe
     */
    public boolean registrarEmpleado(String dni, String nombre, String contrasena, List<Autorizacion> permisos)
    {
        if (dni == null || dni.isBlank() || nombre == null || nombre.isBlank()
                || contrasena == null || contrasena.isBlank())
        {
            return false;
        }

        /* Comprobamos que no haya duplicado de DNI. */
        for (Usuario u : this.usuarios)
        {
            if (u.coincideDni(dni))
            {
                return false;
            }
        }

        String hash = Usuario.generarHash(contrasena);
        Empleado nuevoEmpleado = new Empleado(dni, this.contadorIdUsuario++, nombre, hash);

        if (permisos != null && !permisos.isEmpty())
        {
            nuevoEmpleado.actualizarPermisos(permisos);
        }

        this.usuarios.add(nuevoEmpleado);
        return true;
    }

    /**
     * Da de baja a un empleado eliminándolo de la lista de usuarios.
     *
     * @param empleado empleado a despedir
     */
    public void despedirEmpleado(Empleado empleado)
    {
        if (empleado == null)
        {
            return;
        }
        this.usuarios.remove(empleado);
    }

    /**
     * Restablece la contraseña de un empleado sin necesidad de conocer la antigua.
     * Operación administrativa que genera el hash de la nueva contraseña y lo asigna.
     *
     * @param empleado empleado cuya contraseña se quiere restablecer
     * @param nuevaContrasena nueva contraseña en texto plano
     */
    public void restablecerContrasena(Empleado empleado, String nuevaContrasena)
    {
        if (empleado == null || nuevaContrasena == null || nuevaContrasena.isBlank())
        {
            return;
        }

        String nuevoHash = Usuario.generarHash(nuevaContrasena);
        empleado.restablecerHash(nuevoHash);
    }

    /**
     * Define un descuento porcentual aplicable a un producto específico del catálogo.
     *
     * @param producto producto al que se aplica el descuento
     * @param valor porcentaje de descuento (entre 1 y 100)
     * @param inicio fecha de inicio de la promoción
     * @param fin fecha de fin de la promoción
     * @return true si el descuento se ha creado correctamente, false si los datos son inválidos
     */
    public boolean definirDescuentoPorProducto(ProductoTienda producto, int valor, LocalDate inicio, LocalDate fin)
    {
        if (producto == null || inicio == null || fin == null)
        {
            return false;
        }

        try
        {
            DescuentoPorProducto descuento = new DescuentoPorProducto(inicio, fin, producto, valor);
            this.descuentosGlobales.add(descuento);
            return true;
        }
        catch (IllegalArgumentException e)
        {
            return false;
        }
    }

    /**
     * Define un descuento cuantitativo por cantidad mínima de productos en el carrito.
     *
     * @param cantidadMinima número mínimo de productos para activar el descuento
     * @param descuentoAsociado monto fijo a descontar
     * @param inicio fecha de inicio de la promoción
     * @param fin fecha de fin de la promoción
     * @return true si el descuento se ha creado correctamente, false si los datos son inválidos
     */
    public boolean definirDescuentoPorCantidadCuantitativo(int cantidadMinima, Valor descuentoAsociado, LocalDate inicio, LocalDate fin)
    {
        if (descuentoAsociado == null || inicio == null || fin == null)
        {
            return false;
        }

        try
        {
            DescuentoPorCantidadCuantitativo descuento = new DescuentoPorCantidadCuantitativo(inicio, fin, cantidadMinima, descuentoAsociado);
            this.descuentosGlobales.add(descuento);
            return true;
        }
        catch (IllegalArgumentException e)
        {
            return false;
        }
    }

    /**
     * Define un descuento porcentual por cantidad mínima de productos en el carrito.
     *
     * @param cantidadMinima número mínimo de productos para activar el descuento
     * @param porcentaje porcentaje a descontar (entre 1 y 100)
     * @param inicio fecha de inicio de la promoción
     * @param fin fecha de fin de la promoción
     * @return true si el descuento se ha creado correctamente, false si los datos son inválidos
     */
    public boolean definirDescuentoPorCantidadPorcentual(int cantidadMinima, int porcentaje, LocalDate inicio, LocalDate fin)
    {
        if (inicio == null || fin == null)
        {
            return false;
        }

        try
        {
            DescuentoPorCantidadPorcentual descuento = new DescuentoPorCantidadPorcentual(inicio, fin, cantidadMinima, porcentaje);
            this.descuentosGlobales.add(descuento);
            return true;
        }
        catch (IllegalArgumentException e)
        {
            return false;
        }
    }

    /**
     * Define un descuento cuantitativo por importe mínimo del carrito.
     *
     * @param valorMinimo importe mínimo que debe alcanzar el carrito
     * @param descuentoAsociado monto fijo a descontar
     * @param inicio fecha de inicio de la promoción
     * @param fin fecha de fin de la promoción
     * @return true si el descuento se ha creado correctamente, false si los datos son inválidos
     */
    public boolean definirDescuentoPorValorCuantitativo(Valor valorMinimo, Valor descuentoAsociado, LocalDate inicio, LocalDate fin)
    {
        if (valorMinimo == null || descuentoAsociado == null || inicio == null || fin == null)
        {
            return false;
        }

        try
        {
            DescuentoPorValorCuantitativo descuento = new DescuentoPorValorCuantitativo(inicio, fin, valorMinimo, descuentoAsociado);
            this.descuentosGlobales.add(descuento);
            return true;
        }
        catch (IllegalArgumentException e)
        {
            return false;
        }
    }

    /**
     * Define un descuento porcentual por importe mínimo del carrito.
     *
     * @param valorMinimo importe mínimo que debe alcanzar el carrito
     * @param porcentaje porcentaje a descontar (entre 1 y 100)
     * @param inicio fecha de inicio de la promoción
     * @param fin fecha de fin de la promoción
     * @return true si el descuento se ha creado correctamente, false si los datos son inválidos
     */
    public boolean definirDescuentoPorValorPorcentual(Valor valorMinimo, int porcentaje, LocalDate inicio, LocalDate fin)
    {
        if (valorMinimo == null || inicio == null || fin == null)
        {
            return false;
        }

        try
        {
            DescuentoPorValorPorcentual descuento = new DescuentoPorValorPorcentual(inicio, fin, valorMinimo, porcentaje);
            this.descuentosGlobales.add(descuento);
            return true;
        }
        catch (IllegalArgumentException e)
        {
            return false;
        }
    }

    /**
     * Establece el tiempo de reserva en minutos para las nuevas reservas del carrito.
     *
     * @param tiempo tiempo en minutos
     * @return true siempre (la operación no puede fallar)
     */
    public boolean definirTiempoReservas(long tiempo)
    {
        if (tiempo <= 0)
        {
            return false;
        }
        this.tiempoReserva = tiempo;
        return true;
    }

    /**
     * Obtiene el tiempo de reserva configurado actualmente en minutos.
     *
     * @return tiempo de reserva en minutos
     */
    public long getTiempoReserva()
    {
        return this.tiempoReserva;
    }

    /**
     * Crea un pack de productos a partir de una lista de productos de la tienda.
     * El pack se añade automáticamente al catálogo de la tienda.
     * Su precio será la suma de los precios individuales de sus componentes.
     *
     * @param productos lista de productos que componen el pack
     * @return true si el pack se ha creado y añadido al catálogo, false si la lista es inválida
     */
    public boolean crearPack(List<ProductoTienda> productos)
    {
        if (productos == null || productos.size() < 2)
        {
            return false;
        }

        /* Comprobamos que todos los productos existan en el catálogo. */
        for (ProductoTienda p : productos)
        {
            if (p == null || !this.productosTienda.contains(p))
            {
                return false;
            }
        }

        /* Calculamos el precio del pack como suma de sus componentes. */
        Valor precioPack = productos.get(0).getPrecio();
        try
        {
            for (int i = 1; i < productos.size(); i++)
            {
                precioPack.incrementar(productos.get(i).getPrecio());
            }
        }
        catch (IllegalArgumentException e)
        {
            return false;
        }

        productos.Pack pack = new productos.Pack(
                "Pack", "Pack de productos variados",
                1, this.contadorIdProducto++, precioPack, new ArrayList<>(productos)
        );
        this.productosTienda.add(pack);
        return true;
    }

    /**
     * Repone stock de un producto de la tienda, incrementando sus unidades disponibles.
     *
     * @param producto producto al que se repone stock
     * @param cantidad número de unidades a añadir
     */
    public void reponerStock(ProductoTienda producto, int cantidad)
    {
        if (producto == null || cantidad <= 0)
        {
            return;
        }

        if (!this.productosTienda.contains(producto))
        {
            return;
        }

        producto.incrementarStock(cantidad);
    }

    /**
     * Retira stock de un producto de la tienda, decrementando sus unidades disponibles.
     * No permite retirar más unidades de las que hay disponibles (sin contar las reservadas).
     *
     * @param producto producto del que se retira stock
     * @param cantidad número de unidades a retirar
     * @return true si se han retirado las unidades correctamente, false en caso contrario
     */
    public boolean retirarStock(ProductoTienda producto, int cantidad)
    {
        if (producto == null || cantidad <= 0)
        {
            return false;
        }

        if (!this.productosTienda.contains(producto))
        {
            return false;
        }

        return producto.decrementarStock(cantidad);
    }

    /**
     * Guarda el estado de la plataforma en cualquier momento (dump) en el fichero indicado.
     * Utiliza la serialización de objetos de Java para persistir todos los datos de la instancia actual.
     *
     * @param rutaFichero ruta completa o relativa del fichero donde se guardará el estado
     * @return true si la operación se ha completado correctamente, false en caso de error
     */
    public boolean dump(String rutaFichero)
    {
        if (rutaFichero == null || rutaFichero.isBlank())
        {
            return false;
        }

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(rutaFichero)))
        {
            out.writeObject(this);
            return true;
        }
        catch (IOException e)
        {
            return false;
        }
    }

    /**
     * Carga el estado de la plataforma desde el archivo especificado (load)
     * y reemplaza la instancia Singleton en ejecución con los datos cargados.
     *
     * @param rutaFichero ruta completa o relativa del fichero desde donde se cargará el estado
     * @return true si la operación se ha completado correctamente, false en caso de error
     */
    public boolean load(String rutaFichero)
    {
        if (rutaFichero == null || rutaFichero.isBlank())
        {
            return false;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(rutaFichero)))
        {
            instancia = (Aplicacion) in.readObject();
            return true;
        }
        catch (IOException | ClassNotFoundException e)
        {
            return false;
        }
    }
}