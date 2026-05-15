package productos;

import finanzas.Valor;

/**
 * Clase que representa un juego de mesa
 * Almacena la información asociada a este
 * @author Daniel Martín Jaén
 * @version 1.0
 */
public class Juego extends ProductoTienda
{
    private static final long serialVersionUID = 1L;

    /**
     * Número mínimo y máximo de jugadores que pueden jugar a este juego
     */
    private int[] rangoNoJugadores;

    /**
     * Edad mínima para jugar a este juego
     */
    private int edadMinima;

    /**
     * Constructor de clase, instancia un objeto juego
     * @param nombre Nombre del juego
     * @param descripcion Descripción del juego
     * @param stockTotal Número inicial de unidades del juego
     * @param id Identificador único del juego
     * @param precio Precio inicial del juego
     * @param rangoNoJugadores Número mínimo y máximo de jugadores
     * @param edadMinima Edad mínima para este jeugo
     */
    public Juego(String nombre, String descripcion, int stockTotal, int id, Valor precio, int[] rangoNoJugadores, int edadMinima)
    {
        super(nombre, descripcion, stockTotal, id, precio);
        this.rangoNoJugadores = rangoNoJugadores;
        this.edadMinima = edadMinima;
    }
}
