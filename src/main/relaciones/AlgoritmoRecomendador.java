package relaciones;

import java.io.Serializable;

import productos.ProductoTienda;
import usuarios.Cliente;

import java.util.List;

/**
 * Interfaz que define el contrato para los distintos algoritmos de recomendación.
 * @author Izan Robles
 * @version 1.1
 */
public interface AlgoritmoRecomendador extends Serializable
{
    /**
     * Genera una lista de productos recomendados para un cliente específico.
     * @param cliente El usuario para el que se generan las recomendaciones.
     * @param catalogo La lista completa de productos disponibles en la tienda.
     * @param cantidad El número máximo de recomendaciones a generar.
     * @return Una lista de ProductoTienda seleccionados por el algoritmo.
     */
    List<ProductoTienda> generarRecomendaciones(Cliente cliente, List<ProductoTienda> catalogo, int cantidad);
}