package productos;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Clase para gestionar las categorías de la tienda.
 * Implementa una estructura jerárquica segura y mantiene un encapsulamiento estricto.
 * @author Izan Robles
 * @version 1.3
 */
public class Categoria implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**Nombre de la categoría*/
    private final String nombre;
    /**Lista de productos contenidos (puede estar vacía)*/
    private final List<ProductoTienda> productos;
    /**Categoría padre (puede ser null)*/
    private final Categoria categoriaPadre;
    /**Lista de subcategorías (puede estar vacía)*/
    private final List<Categoria> subcategorias;

    /**
     * Constructor para una categoría sin padre.
     * @param nombre Nombre de la categoría.
     * @throws IllegalArgumentException si el nombre es nulo o vacío.
     */
    public Categoria(String nombre)
    {
        this(nombre, null);
    }

    /**
     * Constructor completo para crear una categoría jerárquica.
     * @param nombre Nombre de la categoría.
     * @param categoriaPadre Categoría de la que depende (puede ser null).
     * @throws IllegalArgumentException si el nombre es nulo o vacío.
     */
    public Categoria(String nombre, Categoria categoriaPadre)
    {
        if (nombre == null || nombre.trim().isEmpty())
        {
            throw new IllegalArgumentException("El nombre de la categoría no puede ser nulo ni estar vacío.");
        }

        this.nombre = nombre.trim();
        this.categoriaPadre = categoriaPadre;
        this.productos = new ArrayList<>();
        this.subcategorias = new ArrayList<>();

        if (this.categoriaPadre != null)
        {
            this.categoriaPadre.agregarSubcategoriaInterna(this);
        }
    }

    /**
     * Getter seguro para el nombre, al ser un String no hay problema de seguridad.
     * @return String con el nombre de la categoría.
     */
    public String getNombre()
    {
        return this.nombre;
    }

    /**
     * Devuelve una vista de SOLO LECTURA de los productos asociados.
     * @return Lista no modificable de ProductoTienda.
     */
    public List<ProductoTienda> getProductos()
    {
        return Collections.unmodifiableList(this.productos);
    }

    /**
     * Devuelve una vista de SOLO LECTURA de las categorías hijas.
     * @return Lista no modificable de Categoria.
     */
    public List<Categoria> getSubcategorias()
    {
        return Collections.unmodifiableList(this.subcategorias);
    }

    /**
     * Añade un producto a la categoría previa validación.
     * @param producto ProductoTienda a añadir.
     * @throws IllegalArgumentException si el producto es nulo.
     */
    public void agregarProducto(ProductoTienda producto)
    {
        if (producto == null)
        {
            throw new IllegalArgumentException("Seguridad: No se puede añadir un producto nulo a la categoría.");
        }

        if (!this.productos.contains(producto))
        {
            this.productos.add(producto);
        }
    }

    /**
     * Elimina un producto de la categoría de forma controlada.
     * @param producto ProductoTienda a retirar.
     * @throws IllegalArgumentException si el producto proporcionado es nulo.
     */
    public void eliminarProducto(ProductoTienda producto)
    {
        if (producto == null)
        {
            throw new IllegalArgumentException("Seguridad: El producto a eliminar no puede ser nulo.");
        }

        this.productos.remove(producto);
    }

    /**
     * Método privado y oculto al exterior para gestionar la jerarquía.
     * Solo lo usa el propio constructor para auto-enlazarse con su padre.
     * @param subcategoria Categoría hija a añadir.
     * @throws IllegalArgumentException si la subcategoría es nula o se intenta añadir a sí misma.
     */
    private void agregarSubcategoriaInterna(Categoria subcategoria)
    {
        if (subcategoria == null)
        {
            throw new IllegalArgumentException("Seguridad: La subcategoría a enlazar no puede ser nula.");
        }

        if (subcategoria == this)
        {
            throw new IllegalArgumentException("Integridad: Una categoría no puede ser padre de sí misma.");
        }

        if (!this.subcategorias.contains(subcategoria))
        {
            this.subcategorias.add(subcategoria);
        }
    }
}