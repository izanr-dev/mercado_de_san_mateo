import finanzas.Divisa;
import finanzas.InformacionBancaria;
import finanzas.Valor;
import productos.ProductoMercadillo;
import productos.ProductoTienda;
import relaciones.Aplicacion;
import relaciones.Carrito;
import relaciones.EstadoProducto;
import relaciones.Oferta;
import usuarios.Cliente;
import usuarios.Usuario;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Demostrador básico de la aplicación.
 * Ejecuta un flujo completo por consola sin alterar el diseño original.
 */
public class DemoAplicacion
{
    // Contadores para el resumen final
    private static int totalPruebas = 0;
    private static int pruebasExitosas = 0;

    public static void main(String[] args)
    {
        imprimirCartel();

        Aplicacion app = Aplicacion.getInstance();
        imprimirPaso("INICIO DEMO", "Arranca la simulación de la aplicación.");

        /* 1) Registro de clientes */
        imprimirPaso("1. Registro de clientes", "Se crean tres clientes de ejemplo.");
        boolean regAna = app.registrarCliente("11111111A", "Ana Perez", "ana", "ClaveAna123");
        boolean regLuis = app.registrarCliente("22222222B", "Luis Gomez", "luis", "ClaveLuis123");
        boolean regMarta = app.registrarCliente("33333333C", "Marta Ruiz", "marta", "ClaveMarta123");

        registrarPrueba(regAna, "Registro Ana: " + regAna);
        registrarPrueba(regLuis, "Registro Luis: " + regLuis);
        registrarPrueba(regMarta, "Registro Marta: " + regMarta);
        exigir(regAna && regLuis && regMarta, "No se pudieron registrar los clientes de la demo.");

        /* 2) Login de usuarios */
        imprimirPaso("2. Login de usuarios", "Se prueba login incorrecto y luego login correcto.");
        Usuario loginErroneo = app.iniciarSesion("11111111A", "ClaveIncorrecta");
        registrarPrueba(loginErroneo == null, "Login incorrecto (esperado null): " + (loginErroneo == null));

        Usuario usuarioAna = app.iniciarSesion("11111111A", "ClaveAna123");
        Usuario usuarioLuis = app.iniciarSesion("22222222B", "ClaveLuis123");
        Usuario usuarioMarta = app.iniciarSesion("33333333C", "ClaveMarta123");
        registrarPrueba(usuarioAna != null, "Login Ana correcto: " + (usuarioAna != null));
        registrarPrueba(usuarioLuis != null, "Login Luis correcto: " + (usuarioLuis != null));
        registrarPrueba(usuarioMarta != null, "Login Marta correcto: " + (usuarioMarta != null));

        exigir(usuarioAna instanceof Cliente, "Ana no pudo iniciar sesión como cliente.");
        exigir(usuarioLuis instanceof Cliente, "Luis no pudo iniciar sesión como cliente.");
        exigir(usuarioMarta instanceof Cliente, "Marta no pudo iniciar sesión como cliente.");
        Cliente ana = (Cliente) usuarioAna;
        Cliente luis = (Cliente) usuarioLuis;
        Cliente marta = (Cliente) usuarioMarta;

        /* 3) Actualización de catálogo de productos */
        imprimirPaso("3. Catalogo", "Se crean productos, se actualizan datos y se crea un pack.");
        boolean altaP1 = app.crearProductoTienda("Catan", "Juego de mesa de estrategia", new Valor(new BigDecimal("34.95"), Divisa.EUR), 10);
        boolean altaP2 = app.crearProductoTienda("Dixit", "Juego narrativo y creativo", new Valor(new BigDecimal("29.90"), Divisa.EUR), 8);
        boolean altaP3 = app.crearProductoTienda("Fundas Cartas", "Pack de fundas transparentes", new Valor(new BigDecimal("4.50"), Divisa.EUR), 50);
        exigir(altaP1 && altaP2 && altaP3, "No se pudieron crear todos los productos iniciales.");

        List<ProductoTienda> catalogo = app.verCatalogo();
        System.out.println("[DEBUG] Productos en catalogo tras alta: " + catalogo.size());

        ProductoTienda p1 = buscarProductoUnico(app, "Catan");
        ProductoTienda p2 = buscarProductoUnico(app, "Dixit");
        ProductoTienda p3 = buscarProductoUnico(app, "Fundas Cartas");
        exigir(p1 != null && p2 != null && p3 != null, "No se pudieron localizar los productos por búsqueda.");

        boolean modOk = app.modificarProductoTienda(p3, "Fundas Premium", "Fundas premium para cartas (100u)");
        exigir(modOk, "No se pudo modificar el producto de fundas.");
        app.reponerStock(p1, 5);
        boolean packOk = app.crearPack(Arrays.asList(p1, p2));
        exigir(packOk, "No se pudo crear el pack de catalogo.");
        System.out.println("[DEBUG] Productos en catalogo tras actualizar: " + app.verCatalogo().size());

        /* 4) Gestion de descuentos */
        imprimirPaso("4. Descuentos", "Se define descuento vigente y se comprueba uno no vigente.");
        boolean descVigente = app.definirDescuentoPorValorPorcentual(
                new Valor(new BigDecimal("50.00"), Divisa.EUR),
                10,
                LocalDate.now(),
                LocalDate.now().plusDays(7)
        );
        exigir(descVigente, "No se pudo crear descuento vigente.");

        Carrito carritoDemo = new Carrito(new ArrayList<>(), ana);
        carritoDemo.anadirProducto(p1, 1);
        carritoDemo.anadirProducto(p2, 1);

        Valor precioBase = carritoDemo.calcularPrecioCarrito();
        Valor precioConDescuento = app.aplicarDescuentos(carritoDemo);
        exigir(precioBase != null && precioConDescuento != null, "No se pudo calcular precio base o con descuento.");
        exigir(esCuantia(precioConDescuento, "58.37"), "El precio con descuento no coincide con el esperado.");
        System.out.println("[DEBUG] Precio base carrito demo: " + formatValor(precioBase));
        System.out.println("[DEBUG] Mejor precio con descuentos: " + formatValor(precioConDescuento));

        boolean descNoVigente = app.definirDescuentoPorValorPorcentual(
                new Valor(new BigDecimal("10.00"), Divisa.EUR),
                25,
                LocalDate.now().plusDays(15),
                LocalDate.now().plusDays(20)
        );
        exigir(descNoVigente, "No se pudo crear descuento no vigente de prueba.");
        Valor precioTrasDescNoVigente = app.aplicarDescuentos(carritoDemo);
        boolean descuentoNoVigenteAfecta = precioConDescuento.compararCuantias(precioTrasDescNoVigente) != 0;
        registrarPrueba(!descuentoNoVigenteAfecta, "Descuento no vigente aplicado (esperado false): " + descuentoNoVigenteAfecta);
        exigir(!descuentoNoVigenteAfecta, "Un descuento no vigente ha afectado al precio.");
        carritoDemo.vaciarCarrito();

        /* 5) Gestion de pedidos (crear y consultar) */
        imprimirPaso("5. Pedidos", "Ana compra productos y se consulta el resultado de su actividad.");
        boolean add1 = ana.anadirProductoAlCarrito(p1, 1);
        boolean add2 = ana.anadirProductoAlCarrito(p2, 1);
        exigir(add1 && add2, "No se pudieron añadir productos al carrito principal.");

        int reservadoAntesFallo = p1.getNoProductosReservados() + p2.getNoProductosReservados();

        InformacionBancaria tarjetaCaducada = new InformacionBancaria(
                "1234567812345678",
                "123",
                LocalDate.now().minusDays(1)
        );
        boolean compraFallidaEsperada = ana.efectuarCompra(tarjetaCaducada);
        int reservadoDespuesFallo = p1.getNoProductosReservados() + p2.getNoProductosReservados();
        registrarPrueba(!compraFallidaEsperada, "Compra con tarjeta caducada (esperado false): " + compraFallidaEsperada);
        registrarPrueba(reservadoAntesFallo == reservadoDespuesFallo, "Reservas intactas tras fallo de pago: " + (reservadoAntesFallo == reservadoDespuesFallo));
        exigir(!compraFallidaEsperada, "La compra con tarjeta caducada no debería completarse.");
        exigir(reservadoAntesFallo == reservadoDespuesFallo, "El fallo de pago altero reservas de forma incorrecta.");

        InformacionBancaria tarjetaAna = new InformacionBancaria(
                "1234567812345678",
                "123",
                LocalDate.now().plusYears(2)
        );
        boolean compraOk = ana.efectuarCompra(tarjetaAna);
        registrarPrueba(add1, "Añadir Catan al carrito: " + add1);
        registrarPrueba(add2, "Añadir Dixit al carrito: " + add2);
        registrarPrueba(compraOk, "Compra realizada correctamente: " + compraOk);
        exigir(compraOk, "La compra principal no se pudo completar.");
        System.out.println("[DEBUG] Categorías en pedidos de Ana: " + ana.obtenerCategoriasDePedidos().size());

        Path exportCliente = Path.of("resources", "demo_cliente_ana.txt");
        boolean exportado = ana.exportarMisDatos(exportCliente.toString());
        registrarPrueba(exportado, "Exportación de datos cliente: " + exportado);
        exigir(exportado, "No se pudieron exportar los datos del cliente.");
        if (exportado)
        {
            System.out.println("[DEBUG] Resumen exportado:");
            imprimirFichero(exportCliente);
            int pedidosExportados = leerCampoEntero(exportCliente, "pedidos");
            registrarPrueba(pedidosExportados >= 1, "Pedidos exportados (esperado >= 1): " + pedidosExportados);
            exigir(pedidosExportados >= 1, "No se refleja ningún pedido en los datos exportados.");
        }

        /* 6) Intercambio de objetos de segunda mano */
        imprimirPaso("6. Mercadillo/Intercambio", "Se simulan escenarios de aceptación y rechazo.");
        ProductoMercadillo productoAna = new ProductoMercadillo(
                "Consola Retro",
                "Consola de segunda mano en buen estado",
                ana,
                EstadoProducto.PENDIENTE_VALORACION
        );
        productoAna.registrarValoracion(
                EstadoProducto.BUEN_ESTADO,
                new Valor(new BigDecimal("90.00"), Divisa.EUR)
        );

        ProductoMercadillo productoLuis = new ProductoMercadillo(
                "Mando Inalámbrico",
                "Mando compatible con varias consolas",
                luis,
                EstadoProducto.BUEN_ESTADO
        );
        ProductoMercadillo productoMarta = new ProductoMercadillo(
                "Juego Clasico",
                "Juego retro en buen estado",
                marta,
                EstadoProducto.BUEN_ESTADO
        );

        Oferta oferta = new Oferta(List.of(productoLuis), luis);
        productoAna.recibirOferta(oferta);
        Oferta ofertaMarta = new Oferta(List.of(productoMarta), marta);
        productoAna.recibirOferta(ofertaMarta);
        boolean ofertaAprobadaTienda = productoAna.revisarOfertaTienda(oferta, true);
        boolean ofertaMartaAprobada = productoAna.revisarOfertaTienda(ofertaMarta, true);
        ana.rechazarOferta(productoAna, ofertaMarta);
        ana.aceptarOferta(productoAna, oferta);

        registrarPrueba(ofertaAprobadaTienda, "Oferta principal aprobada por tienda: " + ofertaAprobadaTienda);
        registrarPrueba(oferta.estaAceptada(), "Oferta aceptada por usuario: " + oferta.estaAceptada());
        registrarPrueba(ofertaMartaAprobada && !ofertaMarta.estaAceptada(), "Oferta alternativa aprobada y luego rechazada: " + (ofertaMartaAprobada && !ofertaMarta.estaAceptada()));
        exigir(ofertaAprobadaTienda && oferta.estaAceptada(), "El intercambio no pudo cerrarse correctamente.");
        exigir(ofertaMartaAprobada && !ofertaMarta.estaAceptada(), "El escenario de rechazo no se ha comportado como se esperaba.");

        /* 7) Persistencia de datos (guardar y cargar) */
        imprimirPaso("7. Persistencia", "Se guarda estado, se altera y se recarga verificando mas de un aspecto.");
        Path estado = Path.of("resources", "demo_estado_app.bin");
        int tamCatalogoAntesDump = app.verCatalogo().size();
        boolean dumpOk = app.dump(estado.toString());
        exigir(dumpOk, "No se pudo hacer dump del estado.");

        /* Alteración intencionada para comprobar carga */
        app.retirarProductoTienda(p1);
        int tamCatalogoTrasCambio = app.verCatalogo().size();

        boolean loadOk = app.load(estado.toString());
        Aplicacion appRecargada = Aplicacion.getInstance();
        int tamCatalogoTrasLoad = appRecargada.verCatalogo().size();

        registrarPrueba(dumpOk, "Dump correcto: " + dumpOk);
        System.out.println("[DEBUG] Tam. catalogo antes dump: " + tamCatalogoAntesDump);
        System.out.println("[DEBUG] Tam. catalogo tras cambio local: " + tamCatalogoTrasCambio);
        registrarPrueba(loadOk, "Load correcto: " + loadOk);
        System.out.println("[DEBUG] Tam. catalogo tras load: " + tamCatalogoTrasLoad);
        exigir(loadOk, "No se pudo cargar el estado guardado.");
        exigir(tamCatalogoAntesDump == tamCatalogoTrasLoad, "La carga no ha restaurado correctamente el catalogo.");

        Usuario anaTrasLoad = appRecargada.iniciarSesion("11111111A", "ClaveAna123");
        registrarPrueba(anaTrasLoad != null, "Usuario Ana recuperado tras load: " + (anaTrasLoad != null));
        exigir(anaTrasLoad != null, "No se han recuperado usuarios tras la carga.");

        Cliente anaCargada = (Cliente) anaTrasLoad;
        Carrito carritoPostLoad = new Carrito(new ArrayList<>(), anaCargada);
        ProductoTienda p1PostLoad = buscarProductoUnico(appRecargada, "Catan");
        ProductoTienda p2PostLoad = buscarProductoUnico(appRecargada, "Dixit");
        exigir(p1PostLoad != null && p2PostLoad != null, "No se recuperaron productos esperados tras load.");
        carritoPostLoad.anadirProducto(p1PostLoad, 1);
        carritoPostLoad.anadirProducto(p2PostLoad, 1);
        Valor mejorPrecioPostLoad = appRecargada.aplicarDescuentos(carritoPostLoad);
        registrarPrueba(mejorPrecioPostLoad != null, "Descuentos siguen operativos tras load: " + (mejorPrecioPostLoad != null));
        exigir(mejorPrecioPostLoad != null, "No se pudo aplicar descuentos tras load.");
        exigir(esCuantia(mejorPrecioPostLoad, "58.37"), "El precio con descuentos tras load no coincide con el esperado.");
        carritoPostLoad.vaciarCarrito();

        /* FIN DEMO Y RESUMEN */
        imprimirPaso("RESUMEN DE EJECUCIÓN", "Estadísticas de las pruebas realizadas.");
        double porcentajeExito = totalPruebas == 0 ? 0.0 : ((double) pruebasExitosas / totalPruebas) * 100.0;

        System.out.printf("[DEBUG] Pruebas totales realizadas: %d%n", totalPruebas);
        System.out.printf("[DEBUG] Pruebas superadas con éxito: %d%n", pruebasExitosas);
        System.out.printf("[DEBUG] Porcentaje de éxito: %.2f%%%n", porcentajeExito);

        imprimirPaso("FIN DEMO", "Demostrador ejecutado completamente.");
    }

    /**
     * Auxiliar para registrar y contabilizar las pruebas.
     */
    private static void registrarPrueba(boolean exito, String mensaje)
    {
        totalPruebas++;
        if (exito)
        {
            pruebasExitosas++;
            System.out.println("[ÉXITO] " + mensaje);
        }
        else
        {
            System.out.println("[ERROR] " + mensaje);
        }
    }

    private static void imprimirCartel()
    {
        System.out.println("[DEBUG]\n" +
                "  ███╗   ███╗███████╗██████╗  ██████╗ █████╗ ██████╗  ██████╗ \n" +
                "  ████╗ ████║██╔════╝██╔══██╗██╔════╝██╔══██╗██╔══██╗██╔═══██╗\n" +
                "  ██╔████╔██║█████╗  ██████╔╝██║     ███████║██║  ██║██║   ██║\n" +
                "  ██║╚██╔╝██║██╔══╝  ██╔══██╗██║     ██╔══██║██║  ██║██║   ██║\n" +
                "  ██║ ╚═╝ ██║███████╗██║  ██║╚██████╗██║  ██║██████╔╝╚██████╔╝\n" +
                "  ╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝ ╚═════╝╚═╝  ╚═╝╚═════╝  ╚═════╝ \n" +
                "                                                              \n" +
                "  ██████╗ ███████╗    ███████╗ █████╗ ███╗   ██╗               \n" +
                "  ██╔══██╗██╔════╝    ██╔════╝██╔══██╗████╗  ██║               \n" +
                "  ██║  ██║█████╗      ███████╗███████║██╔██╗ ██║               \n" +
                "  ██║  ██║██╔══╝      ╚════██║██╔══██║██║╚██╗██║               \n" +
                "  ██████╔╝███████╗    ███████║██║  ██║██║ ╚████║               \n" +
                "  ╚═════╝ ╚══════╝    ╚══════╝╚═╝  ╚═╝╚═╝  ╚═══╝               \n" +
                "                                                              \n" +
                "  ███╗   ███╗ █████╗ ████████╗███████╗ ██████╗                  \n" +
                "  ████╗ ████║██╔══██╗╚══██╔══╝██╔════╝██╔═══██╗                 \n" +
                "  ██╔████╔██║███████║   ██║   █████╗  ██║   ██║                 \n" +
                "  ██║╚██╔╝██║██╔══██║   ██║   ██╔══╝  ██║   ██║                 \n" +
                "  ██║ ╚═╝ ██║██║  ██║   ██║   ███████╗╚██████╔╝                 \n" +
                "  ╚═╝     ╚═╝╚═╝  ╚═╝   ╚═╝   ╚══════╝ ╚═════╝                  ");
    }

    private static void imprimirPaso(String titulo, String detalle)
    {
        System.out.println();
        System.out.println("[DEBUG] ====================================================");
        System.out.println("[DEBUG] " + titulo);
        System.out.println("[DEBUG] " + detalle);
        System.out.println("[DEBUG] ====================================================");
    }

    private static String formatValor(Valor valor)
    {
        if (valor == null)
        {
            return "null";
        }
        return valor.obtenerCuantia() + " " + valor.obtenerDivisa();
    }

    private static void imprimirFichero(Path ruta)
    {
        try
        {
            List<String> lineas = Files.readAllLines(ruta);
            for (String linea : lineas)
            {
                System.out.println("[DEBUG]   " + linea);
            }
        }
        catch (Exception e)
        {
            System.err.println("[ERROR] No se pudo leer el fichero exportado: " + e.getMessage());
        }
    }

    private static void exigir(boolean condicion, String mensajeError)
    {
        if (!condicion)
        {
            System.err.println("[ERROR] " + mensajeError);
            throw new IllegalStateException("[ERROR] " + mensajeError);
        }
    }

    private static ProductoTienda buscarProductoUnico(Aplicacion app, String keyword)
    {
        List<ProductoTienda> encontrados = app.buscarProductos(List.of(keyword));
        if (encontrados == null || encontrados.isEmpty())
        {
            return null;
        }
        return encontrados.get(0);
    }

    private static int leerCampoEntero(Path ruta, String clave)
    {
        try
        {
            List<String> lineas = Files.readAllLines(ruta);
            for (String linea : lineas)
            {
                String prefijo = clave + "=";
                if (linea.startsWith(prefijo))
                {
                    return Integer.parseInt(linea.substring(prefijo.length()).trim());
                }
            }
        }
        catch (Exception e)
        {
            return -1;
        }
        return -1;
    }

    private static boolean esCuantia(Valor valor, String cuantiaEsperada)
    {
        if (valor == null || valor.obtenerCuantia() == null || cuantiaEsperada == null)
        {
            return false;
        }
        return valor.obtenerCuantia().compareTo(new BigDecimal(cuantiaEsperada)) == 0;
    }
}