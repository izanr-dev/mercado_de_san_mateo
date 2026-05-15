package productos;

import java.io.Serializable;

import java.util.List;

/**
 * Clase abstracta para implementar atributos comunes a todos los productos.
 * @author Izan Robles
 * @version 1.2
 */
public abstract class Producto implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**Nombre asociado al producto*/
    private String nombre;
    /**Breve descripción del producto*/
    private String descripcion;
    /**Lista de clase Imagen con imágenes asociadas al producto*/
    private List<Imagen> imagenes;
    /**Tamaño máximo para los nombres de productos*/
    private static final int MAX_NOMBRE = 100;
    /**Tamaño máximo para descripciones de productos*/
    private static final int MAX_DESCRIPCION = 2000;

    /**
     * Constructor abstracto de la clase Producto con control de errores para y sanitación de datos.
     * @param nombre a asignar al producto.
     * @param descripcion a asignar al producto.
     */
    public Producto(String nombre, String descripcion)
    {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede estar vacía.");
        }

        if (nombre.length() > MAX_NOMBRE) {
            throw new IllegalArgumentException("El nombre excede el límite de " + MAX_NOMBRE + " caracteres.");
        }
        if (descripcion.length() > MAX_DESCRIPCION) {
            throw new IllegalArgumentException("La descripción excede el límite de " + MAX_DESCRIPCION + " caracteres.");
        }

        this.nombre = nombre.trim();
        this.descripcion = descripcion.trim();
    }

    /**
     * Comprueba si el nombre o la descripción del producto contienen la keyword indicada,
     * sin diferenciar mayúsculas de minúsculas. Útil para búsquedas internas.
     *
     * @param keyword término a buscar
     * @return true si el nombre o la descripción contienen la keyword, false en caso contrario
     */
    public boolean contieneKeyword(String keyword)
    {
        if (keyword == null || keyword.isBlank())
        {
            return false;
        }
        String keyLower = keyword.toLowerCase();
        boolean enNombre = this.nombre != null && this.nombre.toLowerCase().contains(keyLower);
        boolean enDesc = this.descripcion != null && this.descripcion.toLowerCase().contains(keyLower);
        return enNombre || enDesc;
    }

    /**
     * Actualiza el nombre del producto de forma controlada con validación y sanitación.
     *
     * @param nuevoNombre nuevo nombre a asignar
     * @throws IllegalArgumentException si el nuevo nombre es nulo, vacío o excede el límite
     */
    protected void actualizarNombre(String nuevoNombre)
    {
        if (nuevoNombre == null || nuevoNombre.trim().isEmpty())
        {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        if (nuevoNombre.length() > MAX_NOMBRE)
        {
            throw new IllegalArgumentException("El nombre excede el límite de " + MAX_NOMBRE + " caracteres.");
        }
        this.nombre = nuevoNombre.trim();
    }

    /**
     * Actualiza la descripción del producto de forma controlada con validación y sanitación.
     *
     * @param nuevaDescripcion nueva descripción a asignar
     * @throws IllegalArgumentException si la nueva descripción es nula, vacía o excede el límite
     */
    protected void actualizarDescripcion(String nuevaDescripcion)
    {
        if (nuevaDescripcion == null || nuevaDescripcion.trim().isEmpty())
        {
            throw new IllegalArgumentException("La descripción no puede estar vacía.");
        }
        if (nuevaDescripcion.length() > MAX_DESCRIPCION)
        {
            throw new IllegalArgumentException("La descripción excede el límite de " + MAX_DESCRIPCION + " caracteres.");
        }
        this.descripcion = nuevaDescripcion.trim();
    }
}
