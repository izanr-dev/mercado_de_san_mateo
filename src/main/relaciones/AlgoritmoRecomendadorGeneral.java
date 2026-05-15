package relaciones;


import productos.ProductoTienda;
import usuarios.Cliente;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de un algoritmo de recomendación general.
 * Sugiere los 'n' productos con mayor valor acumulado en stock (stock * precio).
 * @author Izan Robles
 * @version 1.3
 */
public class AlgoritmoRecomendadorGeneral implements AlgoritmoRecomendador
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructor por defecto de la clase AlgoritmoRecomendadorGeneral.
     */
    public AlgoritmoRecomendadorGeneral()
    {
    }

    /**
     * Método para generar recomendaciones en función del valor del stock acumulado.
     * @param cliente El usuario para el que se generan las recomendaciones.
     * @param catalogo La lista completa de productos disponibles en la tienda.
     * @param cantidad El número máximo de recomendaciones a generar.
     * @return Una nueva lista de productos con las n recomendaciones generadas.
     * @throws IllegalArgumentException Si la cantidad es menor o igual a cero.
     */
    @Override
    public List<ProductoTienda> generarRecomendaciones(Cliente cliente, List<ProductoTienda> catalogo, int cantidad)
    {
        if (catalogo == null || catalogo.isEmpty())
        {
            return new ArrayList<>();
        }

        if (cantidad <= 0)
        {
            return new ArrayList<>();
        }

        List<ProductoTienda> productosValidos = new ArrayList<>();
        for (ProductoTienda producto : catalogo)
        {
            if (producto != null && producto.getPrecio() != null)
            {
                productosValidos.add(producto);
            }
        }

        productosValidos.sort((p1, p2) ->
                p2.calcularValorStockTotal().compararCuantias(p1.calcularValorStockTotal())
        );

        List<ProductoTienda> recomendaciones = new ArrayList<>();

        int limite = Math.min(cantidad, productosValidos.size());

        for (int i = 0; i < limite; i++)
        {
            recomendaciones.add(productosValidos.get(i));
        }

        return recomendaciones;
    }
}