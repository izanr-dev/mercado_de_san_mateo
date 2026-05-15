package productos;


import finanzas.Valor;

/**
 * Clase que almacena los datos de un comic
 * @author Daniel Martín Jaén
 * @version 1.0
 */
public class Comic extends ProductoTienda
{
    private static final long serialVersionUID = 1L;

    /**
     * Identificador único del comic entre todos los libros
     */
    private String isbn;
    
    /**
     * Número de paginas del comic
     */
    private int noPaginas;

    /**
     * Constructor de la clase, crea un objecto comic a partir de los datos dados
     * @param nombre Nombre del comic
     * @param descripcion Descripción del comic
     * @param stockTotal Número de unidades inciales del comic
     * @param id Id única del comic
     * @param precio Precio inicial del comic
     * @param isbn Identificador único del comic
     * @param noPaginas Número de páginas del comic
     */
    public Comic(String nombre, String descripcion, int stockTotal, int id, Valor precio, String isbn, int noPaginas)
    {
        super(nombre, descripcion, stockTotal, id, precio);
        this.isbn = isbn;
        this.noPaginas = noPaginas;
    }
}
