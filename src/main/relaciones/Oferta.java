package relaciones;

import java.io.Serializable;

import productos.ProductoMercadillo;
import usuarios.Cliente;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Clase que representa una oferta realizada por un cliente para intercambiar
 * sus productos por un producto del mercadillo.
 * @author Izan Robles
 * @version 1.2
 */
public class Oferta implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final List<ProductoMercadillo> ofertado;
    private EstadoOferta estadoOferta;
    private final Cliente emisor;

    /**
     * Constructor estándar de la clase Oferta
     * @param ofertado lista de productos ofrecidos para el intercambio
     * @param emisor objeto Cliente autor de la oferta, aquel que ofrece los productos.
     */
    public Oferta(List<ProductoMercadillo> ofertado, Cliente emisor)
    {
        if (ofertado == null || ofertado.isEmpty())
        {
            throw new IllegalArgumentException("La oferta debe contener al menos un producto.");
        }
        if (emisor == null)
        {
            throw new IllegalArgumentException("El emisor de la oferta no puede ser nulo.");
        }

        this.ofertado = new ArrayList<>(ofertado);
        this.estadoOferta = EstadoOferta.SIN_MEDIAR;
        this.emisor = emisor;
    }

    /**
     * Primer paso: Revisión por parte de la tienda.
     * @param aprobada true si la tienda da el visto bueno, false si la rechaza por incumplir normas.
     * @return true si se ha podido cambiar el estado, false si la oferta no estaba pendiente de revisión.
     */
    public boolean revisarPorTienda(boolean aprobada)
    {
        if (this.estadoOferta != EstadoOferta.SIN_MEDIAR)
        {
            return false; // Solo se pueden revisar ofertas recién creadas
        }

        this.estadoOferta = aprobada ? EstadoOferta.APROBADO_POR_TIENDA : EstadoOferta.RECHAZADO;
        return true;
    }

    /**
     * Segundo paso: Decisión del usuario propietario del producto.
     * @param aprobada true si el usuario acepta el intercambio, false si lo rechaza.
     * @return true si se ha podido cambiar el estado, false si la oferta no estaba aprobada por la tienda.
     */
    public boolean revisarPorUsuario(boolean aprobada)
    {
        if (this.estadoOferta != EstadoOferta.APROBADO_POR_TIENDA)
        {
            return false; // El usuario solo puede revisar lo que la tienda ya aprobó
        }

        this.estadoOferta = aprobada ? EstadoOferta.APROBADO_POR_USUARIO : EstadoOferta.RECHAZADO;
        return true;
    }

    /**
     * Método interno para forzar el rechazo de una oferta (usado cuando el usuario acepta otra distinta).
     */
    public void rechazarAutomaticamente()
    {
        // Si ya ha sido aprobada finalmente o rechazada, no hacemos nada
        if (this.estadoOferta != EstadoOferta.APROBADO_POR_USUARIO && this.estadoOferta != EstadoOferta.RECHAZADO)
        {
            this.estadoOferta = EstadoOferta.RECHAZADO;
        }
    }

    /**
     * Método para obtener de forma segura una lista de los productos de una oferta.
     * @return Lista inmodificable con los productos ofertados.
     */
    public List<ProductoMercadillo> obtenerProductosOfertados()
    {
        return Collections.unmodifiableList(this.ofertado);
    }

    /**
     * Método para comprobar si una oferta ya ha sido aceptada previamente por el usuario.
     * @return true si ya ha sido aceptada antes, false si aún está disponible.
     */
    public boolean estaAceptada()
    {
        return this.estadoOferta == EstadoOferta.APROBADO_POR_USUARIO;
    }
}