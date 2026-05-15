package relaciones;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Gestor de intercambios asociado a un ProductoMercadillo.
 * Se encarga de recibir, almacenar y mediar las ofertas en un proceso de dos pasos.
 * @author Izan Robles
 * @version 1.2
 */
public class Intercambio implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final List<Oferta> ofertasRecibidas;
    private Oferta ofertaAceptada;

    /**
     * Constructor estándar de la clase intercambio, previene nullpointers e inicializa con una lista vacía.
     */
    public Intercambio()
    {
        this.ofertasRecibidas = new ArrayList<>();
        this.ofertaAceptada = null;
    }

    /**
     * Método para añadir una oferta a un intercambio
     * @param oferta objeto Oferta a añadir en el intercambio.
     */
    public void anadirOferta(Oferta oferta)
    {
        if (oferta == null)
        {
            throw new IllegalArgumentException("No se puede registrar una oferta nula.");
        }
        if (this.ofertaAceptada != null)
        {
            throw new IllegalStateException("El intercambio ya está cerrado, no se admiten nuevas ofertas.");
        }

        this.ofertasRecibidas.add(oferta);
    }

    /**
     * Interacción de los empleados de la tienda para filtrar las ofertas.
     * @param oferta La oferta a revisar.
     * @param aprobada true para dar el visto bueno y enviarla al usuario, false para cancelarla.
     * @return true si la operación es válida.
     */
    public boolean revisarOfertaPorTienda(Oferta oferta, boolean aprobada)
    {
        if (oferta == null || !this.ofertasRecibidas.contains(oferta))
        {
            return false;
        }

        return oferta.revisarPorTienda(aprobada);
    }

    /**
     * El usuario acepta definitivamente una oferta que previamente fue aprobada por la tienda.
     * Al hacerlo, el resto de las ofertas pendientes se rechazan automáticamente.
     * @param oferta La oferta ganadora.
     * @return true si se acepta con éxito, false si el estado de la oferta no lo permite o ya había ganador.
     */
    public boolean aceptarOfertaPorUsuario(Oferta oferta)
    {
        if (oferta == null || !this.ofertasRecibidas.contains(oferta))
        {
            return false;
        }
        if (this.ofertaAceptada != null)
        {
            return false;
        }

        if (oferta.revisarPorUsuario(true))
        {
            this.ofertaAceptada = oferta;

            for (Oferta o : this.ofertasRecibidas)
            {
                if (o != oferta)
                {
                    o.rechazarAutomaticamente();
                }
            }
            return true;
        }

        return false;
    }

    /**
     * El usuario descarta una oferta. Esto no cierra el intercambio ni afecta a las demás.
     * @param oferta La oferta a descartar.
     * @return true si se ha rechazado correctamente.
     */
    public boolean rechazarOfertaPorUsuario(Oferta oferta)
    {
        if (oferta == null || !this.ofertasRecibidas.contains(oferta))
        {
            return false;
        }

        return oferta.revisarPorUsuario(false);
    }

    /**
     * Método para obtener de forma segura las ofertas asociadas a un intercambio
     * @return lista inmodificable con las ofertas asociadas.
     */
    public List<Oferta> obtenerOfertas()
    {
        return Collections.unmodifiableList(this.ofertasRecibidas);
    }
}