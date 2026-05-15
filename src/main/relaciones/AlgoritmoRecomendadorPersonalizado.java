package relaciones;


import productos.Categoria;
import productos.ProductoTienda;
import usuarios.Cliente;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementación de un algoritmo de recomendación personalizado.
 * Extrae las categorías favoritas del historial del cliente de forma segura y recomienda
 * productos nuevos del catálogo que encajen con esos gustos.
 * @author Izan Robles
 * @version 1.9
 */
public class AlgoritmoRecomendadorPersonalizado implements AlgoritmoRecomendador {
    private static final long serialVersionUID = 1L;


    /**
     * Constructor vacío para la clase (evita warnings de javadoc)
     */
    public AlgoritmoRecomendadorPersonalizado() {
    }

    @Override
    public List<ProductoTienda> generarRecomendaciones(Cliente cliente, List<ProductoTienda> catalogo, int cantidad)
    {
        if (cliente == null || catalogo == null || cantidad <= 0)
        {
            return new ArrayList<>();
        }

        List<Categoria> categoriasInteres = new ArrayList<>();

        // Obtenemos categorías desde pedidos
        List<Categoria> categoriasPedidos = cliente.obtenerCategoriasDePedidos();
        if (categoriasPedidos != null && !categoriasPedidos.isEmpty())
        {
            categoriasInteres.addAll(categoriasPedidos);
        }

        // Obtenemos categorías desde carrito
        List<Categoria> categoriasCarrito = cliente.obtenerCategoriasDelCarrito();
        if (categoriasCarrito != null && !categoriasCarrito.isEmpty())
        {
            categoriasInteres.addAll(categoriasCarrito);
        }

        if (categoriasInteres.isEmpty())
        {
            return new ArrayList<>();
        }

        // Creamos un diccionario con las frecuencias de aparición de las categorías
        Map<Categoria, Integer> frecuenciasCategorias = new HashMap<>();
        for (Categoria c : categoriasInteres)
        {
            if (c != null)
            {
                frecuenciasCategorias.put(c, frecuenciasCategorias.getOrDefault(c, 0) + 1);
            }
        }

        List<ProductoTienda> catalogoPuntuado = new ArrayList<>(catalogo);

        // Ordenamos el catálogo según la puntuación para el cliente
        catalogoPuntuado.sort((p1, p2) -> {
            int puntuacionP1 = calcularPuntuacion(p1, frecuenciasCategorias);
            int puntuacionP2 = calcularPuntuacion(p2, frecuenciasCategorias);

            return Integer.compare(puntuacionP2, puntuacionP1);
        });

        // Tomamos los 'n' elementos indicados por argumento
        List<ProductoTienda> recomendacionesFinales = new ArrayList<>();

        for (ProductoTienda producto : catalogoPuntuado)
        {
            //Comprobación para asegurarnos de que realmente puede interesar al cliente
            if (calcularPuntuacion(producto, frecuenciasCategorias) > 0)
            {
                recomendacionesFinales.add(producto);
            }

            if (recomendacionesFinales.size() == cantidad)
            {
                break;
            }
        }

        return recomendacionesFinales;
    }

    /**
     * Método auxiliar privado para calcular qué tanto un product coincide con los intereses del cliente.
     * Cuantas más categorías comparta con el historial y más repetidas estén, mayor puntuación.
     */
    private int calcularPuntuacion(ProductoTienda producto, Map<Categoria, Integer> frecuencias)
    {
        if (producto == null) return 0;

        int puntuacion = 0;
        List<Categoria> categoriasProducto = producto.getCategorias();

        if (categoriasProducto != null)
        {
            for (Categoria c : categoriasProducto)
            {
                puntuacion += frecuencias.getOrDefault(c, 0);
            }
        }

        return puntuacion;
    }
}