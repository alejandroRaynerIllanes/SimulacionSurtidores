package com.simulacion.bolivia.engine;

import com.simulacion.bolivia.logic.MotorRuteo;
import com.simulacion.bolivia.models.Surtidor;
import com.simulacion.bolivia.models.TanqueCombustible;
import com.simulacion.bolivia.models.Vehiculo;
import com.simulacion.bolivia.utils.GeneradorEstocastico;
import com.simulacion.bolivia.gui.SimulacionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Motor principal de Simulación de Eventos Discretos (SED) para la estación de servicio.
 * Administra la línea de tiempo de la simulación mediante una cola de prioridad.
 */
public class MotorSimulacion {

    private final PriorityQueue<Evento> calendarioEventos = new PriorityQueue<>();
    private double reloj = 0.0;
    private final List<Surtidor> surtidoresSubvencionados = new ArrayList<>();
    private Surtidor surtidorInternacional;
    private TanqueCombustible tanquePrincipal;
    
    private final double precioSubvActual = 9.66; // Tarifa del Trimestre 3
    private final double precioIntActual = 12.50;
    private boolean esperandoCisterna = false;
    private int contadorVehiculos = 0;

    // Soporte para GUI
    private SimulacionListener listener;
    private int velocidadDelayMs = 50;

    // Variables de recopilación de KPIs
    private int vehiculosAtendidosSubv = 0;
    private int vehiculosAtendidosInt = 0;
    private double tiempoEsperaTotal = 0.0;
    private double litrosVendidosSubv = 0.0;
    private double litrosVendidosInt = 0.0;
    private int vecesDesabastecido = 0;

    /**
     * Inicializa los componentes del sistema (tanques, surtidores y eventos iniciales).
     */
    public void inicializarSistema() {
        this.reloj = 0.0;
        this.calendarioEventos.clear();
        this.surtidoresSubvencionados.clear();
        this.esperandoCisterna = false;
        this.contadorVehiculos = 0;
        
        // Resetear KPIs
        this.vehiculosAtendidosSubv = 0;
        this.vehiculosAtendidosInt = 0;
        this.tiempoEsperaTotal = 0.0;
        this.litrosVendidosSubv = 0.0;
        this.litrosVendidosInt = 0.0;
        this.vecesDesabastecido = 0;

        // 1. Instanciar tanque principal subvencionado (Gasolina Especial, 30000L por defecto)
        this.tanquePrincipal = new TanqueCombustible("Gasolina Especial");

        // 2. Crear 4 surtidores subvencionados asignados al tanque principal
        for (int i = 1; i <= 4; i++) {
            this.surtidoresSubvencionados.add(new Surtidor("S" + i, "Subvencionado", precioSubvActual, tanquePrincipal));
        }

        // 3. Crear 1 surtidor internacional con su propio tanque infinito
        TanqueCombustible tanqueInfinito = new TanqueCombustible("Gasolina Especial", Double.MAX_VALUE, Double.MAX_VALUE);
        this.surtidorInternacional = new Surtidor("S_Int", "Internacional", precioIntActual, tanqueInfinito);

        // 4. Agendar los primeros eventos de arribo
        double arriboPart = GeneradorEstocastico.generarInterArriboParticular();
        double arriboPes = GeneradorEstocastico.generarInterArriboPesado();

        this.calendarioEventos.add(new Evento(arriboPart, TipoEvento.ARRIBO_PARTICULAR, null, null));
        this.calendarioEventos.add(new Evento(arriboPes, TipoEvento.ARRIBO_PESADO, null, null));

        System.out.println("Sistema Inicializado:");
        System.out.println(" - Tanque Principal: " + tanquePrincipal.getNivelActual() + "/" + tanquePrincipal.getCapacidadMaxima() + " L");
        System.out.println(" - Surtidores Subvencionados creados: 4 (S1 a S4)");
        System.out.println(" - Surtidor Internacional creado: S_Int (Tanque Infinito)");
        System.out.println(" - Primer Arribo Particular programado en: " + String.format("%.2f", arriboPart) + " min");
        System.out.println(" - Primer Arribo Pesado programado en: " + String.format("%.2f", arriboPes) + " min\n");
    }

    /**
     * Ejecuta el bucle principal de la simulación hasta alcanzar el tiempo límite.
     * 
     * @param tiempoLimite tiempo máximo de simulación en minutos.
     */
    public void ejecutarSimulacion(double tiempoLimite) {
        System.out.println("=== INICIO DE LA SIMULACIÓN (Límite: " + tiempoLimite + " minutos) ===");
        
        while (!calendarioEventos.isEmpty() && reloj <= tiempoLimite) {
            Evento evento = calendarioEventos.poll();
            
            // Si el evento está fuera del límite de tiempo, detenemos la simulación
            if (evento.getTiempo() > tiempoLimite) {
                break;
            }
            
            // Actualizar reloj de la simulación
            this.reloj = evento.getTiempo();
            
            // Procesar el evento
            switch (evento.getTipo()) {
                case ARRIBO_PARTICULAR:
                    procesarArribo(TipoEvento.ARRIBO_PARTICULAR);
                    break;
                case ARRIBO_PESADO:
                    procesarArribo(TipoEvento.ARRIBO_PESADO);
                    break;
                case FIN_SERVICIO:
                    procesarFinServicio(evento);
                    break;
                case LLEGADA_CISTERNA:
                    procesarCisterna();
                    break;
            }

            // Notificar a la interfaz gráfica y pausar según velocidadDelayMs
            if (listener != null) {
                listener.onEstadoActualizado();
                try {
                    Thread.sleep(velocidadDelayMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        System.out.println("\n=== FIN DE LA SIMULACIÓN ===");
        System.out.println("Reloj final de simulación: " + String.format("%.2f", reloj) + " minutos");
        System.out.println("Tanque principal nivel final: " + String.format("%.2f", tanquePrincipal.getNivelActual()) + " L");
    }

    /**
     * Procesa la llegada de un vehículo a la estación de servicio.
     */
    private void procesarArribo(TipoEvento tipoArribo) {
        contadorVehiculos++;
        String idVehiculo = "V" + contadorVehiculos;
        String perfil = (tipoArribo == TipoEvento.ARRIBO_PARTICULAR) ? "Particular" : "Transporte Pesado";
        
        double volumen = (tipoArribo == TipoEvento.ARRIBO_PARTICULAR) 
                ? GeneradorEstocastico.generarVolumenParticular() 
                : GeneradorEstocastico.generarVolumenPesado();
                
        Vehiculo vehiculo = new Vehiculo(idVehiculo, perfil, volumen, reloj);

        // Enrutar al mejor surtidor según el algoritmo de costo de oportunidad
        Surtidor elegido = MotorRuteo.elegirMejorSurtidor(vehiculo, surtidoresSubvencionados, surtidorInternacional, precioSubvActual);
        
        // Encolar el vehículo en el surtidor elegido
        elegido.getFilaEspera().add(vehiculo);
        
        System.out.println("[RELOJ: " + String.format("%.2f", reloj) + "] Arribo " + vehiculo.getId() 
                + " (" + vehiculo.getPerfil() + ", req: " + String.format("%.2f", vehiculo.getVolumenRequerido()) + " L) "
                + "enrutado a " + elegido.getId() + " (" + elegido.getTipo() + "). Fila actual: " + elegido.getTamanoFila());

        // Si el surtidor está libre, comenzar inmediatamente el servicio
        if (!elegido.isEstaOcupado()) {
            iniciarServicio(elegido);
        }

        // Programar el siguiente arribo de este tipo
        double interArribo = (tipoArribo == TipoEvento.ARRIBO_PARTICULAR)
                ? GeneradorEstocastico.generarInterArriboParticular()
                : GeneradorEstocastico.generarInterArriboPesado();
                
        this.calendarioEventos.add(new Evento(reloj + interArribo, tipoArribo, null, null));
    }

    /**
     * Inicia la carga del vehículo que está en el frente de la cola de un surtidor.
     */
    private void iniciarServicio(Surtidor surtidor) {
        Vehiculo vehiculo = surtidor.getFilaEspera().peek();
        if (vehiculo == null) {
            return;
        }

        // Verificar si el tanque asignado al surtidor tiene stock suficiente
        if (surtidor.getTanqueAsignado().hayStock(vehiculo.getVolumenRequerido())) {
            surtidor.setEstaOcupado(true);
            double duracionServicio = vehiculo.getVolumenRequerido() / surtidor.getCaudalLitrosPorMinuto();
            
            // Programar evento de fin de servicio
            this.calendarioEventos.add(new Evento(reloj + duracionServicio, TipoEvento.FIN_SERVICIO, vehiculo, surtidor));
            
            System.out.println("  -> [RELOJ: " + String.format("%.2f", reloj) + "] Inicia servicio de " + vehiculo.getId() 
                    + " en surtidor " + surtidor.getId() + ". Duración estimada: " + String.format("%.2f", duracionServicio) + " min");
        } else {
            // Desabastecimiento: si no hay stock y no estamos esperando la cisterna, la solicitamos
            if (!esperandoCisterna && !"Internacional".equals(surtidor.getTipo())) {
                System.out.println("  -> [RELOJ: " + String.format("%.2f", reloj) + "] !!! ALERTA DE DESABASTECIMIENTO !!! "
                        + "Vehículo " + vehiculo.getId() + " requiere " + String.format("%.2f", vehiculo.getVolumenRequerido()) + " L, "
                        + "pero el tanque principal tiene sólo " + String.format("%.2f", tanquePrincipal.getNivelActual()) + " L. "
                        + "Solicitando Cisterna (Demora: 24 horas)...");
                
                esperandoCisterna = true;
                vecesDesabastecido++;
                // Programar llegada de la cisterna dentro de 24 horas (1440.0 minutos)
                this.calendarioEventos.add(new Evento(reloj + 1440.0, TipoEvento.LLEGADA_CISTERNA, null, null));
            }
        }
    }

    /**
     * Concluye el servicio de un vehículo, descuenta el stock y libera el surtidor.
     */
    private void procesarFinServicio(Evento evento) {
        Surtidor surtidor = evento.getSurtidor();
        Vehiculo vehiculo = surtidor.getFilaEspera().poll(); // Retirar de la fila de espera

        if (vehiculo != null) {
            // Descontar combustible del tanque asignado
            surtidor.getTanqueAsignado().descontarStock(vehiculo.getVolumenRequerido());
            
            // Recopilar KPIs
            double esperaReal = reloj - vehiculo.getHoraArriboSimulacion();
            tiempoEsperaTotal += esperaReal;

            if ("Subvencionado".equalsIgnoreCase(surtidor.getTipo())) {
                vehiculosAtendidosSubv++;
                litrosVendidosSubv += vehiculo.getVolumenRequerido();
            } else if ("Internacional".equalsIgnoreCase(surtidor.getTipo())) {
                vehiculosAtendidosInt++;
                litrosVendidosInt += vehiculo.getVolumenRequerido();
            }
            
            System.out.println("[RELOJ: " + String.format("%.2f", reloj) + "] Fin servicio " + vehiculo.getId() 
                    + " en " + surtidor.getId() + ". Consumo: " + String.format("%.2f", vehiculo.getVolumenRequerido()) + " L. "
                    + "Stock tanque: " + String.format("%.2f", surtidor.getTanqueAsignado().getNivelActual()) + " L");
        }

        surtidor.setEstaOcupado(false);

        // Atender al siguiente de la cola (si existe)
        iniciarServicio(surtidor);
    }

    /**
     * Procesa la llegada de la cisterna reabasteciendo el tanque principal y reanudando colas.
     */
    private void procesarCisterna() {
        tanquePrincipal.setNivelActual(tanquePrincipal.getCapacidadMaxima());
        esperandoCisterna = false;
        
        System.out.println("[RELOJ: " + String.format("%.2f", reloj) + "] === LLEGADA DE CISTERNA === "
                + "Tanque principal reabastecido al máximo: " + tanquePrincipal.getNivelActual() + " L. "
                + "Reanudando servicios en cola.");

        // Intentar iniciar el servicio en todos los surtidores subvencionados desocupados que tengan fila
        for (Surtidor s : surtidoresSubvencionados) {
            if (!s.isEstaOcupado()) {
                iniciarServicio(s);
            }
        }
    }

    /**
     * Genera e imprime en consola el reporte final con métricas y monetización.
     */
    public void generarReporteFinal() {
        int totalVehiculos = vehiculosAtendidosSubv + vehiculosAtendidosInt;
        double esperaMedia = totalVehiculos > 0 ? (tiempoEsperaTotal / totalVehiculos) : 0.0;
        double ingresosSubv = litrosVendidosSubv * precioSubvActual;
        double ingresosInt = litrosVendidosInt * precioIntActual;
        double ingresosTotales = ingresosSubv + ingresosInt;
        double diasSimulados = reloj / 1440.0;

        System.out.println("\n========================================================");
        System.out.println("            REPORTE FINAL DE LA SIMULACIÓN");
        System.out.println("========================================================");
        System.out.printf("Tiempo total simulado:          %.2f minutos (%.2f días)\n", reloj, diasSimulados);
        System.out.println("Vehículos atendidos (Subv.):    " + vehiculosAtendidosSubv);
        System.out.println("Vehículos atendidos (Int.):     " + vehiculosAtendidosInt);
        System.out.println("Total vehículos atendidos:      " + totalVehiculos);
        System.out.printf("Tiempo medio de espera:         %.2f minutos\n", esperaMedia);
        System.out.printf("Litros vendidos (Subv.):        %.2f L\n", litrosVendidosSubv);
        System.out.printf("Litros vendidos (Int.):         %.2f L\n", litrosVendidosInt);
        System.out.printf("Ingresos brutos (Subv.):        %.2f Bs.\n", ingresosSubv);
        System.out.printf("Ingresos brutos (Int.):         %.2f Bs.\n", ingresosInt);
        System.out.printf("Ingresos brutos totales:        %.2f Bs.\n", ingresosTotales);
        System.out.println("Veces que se desabasteció:      " + vecesDesabastecido);
        System.out.println("========================================================\n");
    }

    // Getters auxiliares para propósitos de verificación e inspección
    public double getReloj() {
        return reloj;
    }

    public TanqueCombustible getTanquePrincipal() {
        return tanquePrincipal;
    }

    public List<Surtidor> getSurtidoresSubvencionados() {
        return surtidoresSubvencionados;
    }

    public Surtidor getSurtidorInternacional() {
        return surtidorInternacional;
    }

    public boolean isEsperandoCisterna() {
        return esperandoCisterna;
    }

    public void setListener(SimulacionListener listener) {
        this.listener = listener;
    }

    public void setVelocidadDelayMs(int velocidadDelayMs) {
        this.velocidadDelayMs = velocidadDelayMs;
    }

    public int getVelocidadDelayMs() {
        return velocidadDelayMs;
    }

    public int getVehiculosAtendidosSubv() {
        return vehiculosAtendidosSubv;
    }

    public int getVehiculosAtendidosInt() {
        return vehiculosAtendidosInt;
    }

    public double getTiempoEsperaTotal() {
        return tiempoEsperaTotal;
    }

    public double getLitrosVendidosSubv() {
        return litrosVendidosSubv;
    }

    public double getLitrosVendidosInt() {
        return litrosVendidosInt;
    }

    public int getVecesDesabastecido() {
        return vecesDesabastecido;
    }

    public double getPrecioSubvActual() {
        return precioSubvActual;
    }

    public double getPrecioIntActual() {
        return precioIntActual;
    }
}
