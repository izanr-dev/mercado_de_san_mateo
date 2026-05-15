package usuarios;

import java.io.Serializable;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Clase genérica para cualquier tipo de usuario (cliente/empleado/admin).
 *
 * Para no tener problemas de seguridad:
 * NO guardamos la contraseña en texto plano.
 * Guardamos solo un hash de la contraseña.
 * Cuando el usuario escribe su contraseña, calculamos su hash y comparamos huellas.
 * 
 * @author Bruno Montero
 * @version 1.3
 */
public abstract class Usuario implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * DNI del usuario registrado
     */
    private String dni;

    /**
     * ID única del usuario
     */
    private int id;

    /**
     * Nombre del usuario
     */
    private String nombre;

    /**
     * Hash de la contraseña del usuario
     */
    private String hash;

    /**
     * Constructor de la clase Usuario.
     * Aquí recibimos ya el hash (no la contraseña), porque la contraseña "real"
     * NO debería almacenarse en ninguna parte.
     *
     * @param dni DNI del usuario
     * @param id identificador interno
     * @param nombre nombre del usuario
     * @param hash hash (SHA-256) de la contraseña
     */
    public Usuario(String dni, int id, String nombre, String hash)
    {
        this.dni = dni;
        this.id = id;
        this.nombre = nombre;
        this.hash = hash;
    }

    /**
     * Comprueba credenciales (contraseña en texto plano) contra el hash almacenado.
     * 
     * @param textoPlano contraseña en texto plano
     * @return true si coincide con el hash almacenado
     */
    public boolean comprobarCredenciales(String textoPlano)
    {
        return comprobarContrasena(textoPlano);
    }

    /**
     * Cambia la contraseña sustituyendo el hash almacenado por el hash de la nueva contraseña.
     * 
     * @param contAntigua contraseña antigua en texto plano
     * @param contNueva contraseña nueva en texto plano
     * @return true si se ha cambiado correctamente
     */
    public boolean cambiarContrasena(String contAntigua, String contNueva)
    {
        /* Primero confirmamos que la contraseñaantigua es correcta. */
        if (!comprobarContrasena(contAntigua))
        {
            return false;
        }
        
        if (contNueva == null || contNueva.isBlank())
        {
            return false;
        }
        /* Guardamos SOLO el hash de la nueva contraseña. */
        this.hash = hashTextoPlano(contNueva);
        return true;
    }

    /**
     * Comprueba una contraseña en texto plano contra el hash almacenado.
     * 
     * @param textoPlano contraseña en texto plano
     * @return true si coincide con el hash almacenado
     */
    public boolean comprobarContrasena(String textoPlano)
    {
        if (textoPlano == null || hash == null)
        {
            return false;
        }
        String hashed = hashTextoPlano(textoPlano);
        return hash.equals(hashed);
    }

    /**
     * Función que convierte una contraseña en una huella (hash) usando SHA-256.
     *
     * @param textoPlano contraseña en texto plano
     * @return hash de la contraseña
     */
    protected static String hashTextoPlano(String textoPlano)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(textoPlano.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes)
            {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }

    /**
     * Genera el hash (SHA-256) de una contraseña en texto plano.
     *
     * @param textoPlano contraseña en texto plano
     * @return hash SHA-256 de la contraseña, o null si el texto es null
     */
    public static String generarHash(String textoPlano)
    {
        if (textoPlano == null)
        {
            return null;
        }
        return hashTextoPlano(textoPlano);
    }

    /**
     * Comprueba si el DNI de este usuario coincide con el proporcionado.
     *
     * @param dni DNI a comparar
     * @return true si coinciden, false en caso contrario o si alguno es null
     */
    public boolean coincideDni(String dni)
    {
        if (dni == null || this.dni == null)
        {
            return false;
        }
        return this.dni.equals(dni);
    }

    /**
     * Restablece la contraseña del usuario sustituyendo el hash almacenado.
     *
     * @param nuevoHash nuevo hash (SHA-256) de la contraseña
     * @throws IllegalArgumentException si el hash proporcionado es null o vacío
     */
    public void restablecerHash(String nuevoHash)
    {
        if (nuevoHash == null || nuevoHash.isBlank())
        {
            throw new IllegalArgumentException("El nuevo hash no puede ser nulo o vacío.");
        }
        this.hash = nuevoHash;
    }
}