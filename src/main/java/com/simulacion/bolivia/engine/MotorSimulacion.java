package com.simulacion.bolivia.engine;

import com.simulacion.bolivia.logic.MotorRuteo;
import com.simulacion.bolivia.models.Surtidor;
import com.simulacion.bolivia.models.TanqueCombustible;
import com.simulacion.bolivia.models.Vehiculo;
import com.simulacion.bolivia.utils.GeneradorEstocastico;
import com.simulacion.bolivia.gui.SimulacionListener;
import com.simulacion.bolivia.models.EstacionServicio;
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
    private final List<EstacionServicio> redEstaciones = new ArrayList<>();
    private Surtidor surtidorInternacional;
    private TanqueCombustible tanqueInternacional;
    private EstacionServicio estacionInternacional;

    private String nombreInternacional = "Estación Internacional";
    private double capacidadInternacional = 150000.0;
    private String idSurtidorInternacional = "S_Int";
    private double caudalSurtidorInternacional = 60.0;
    
    private double precioGasolinaSubv = 6.96;
    private double precioDieselSubv = 9.80;
    private double precioInternacional = 12.50;
    private double capacidadInicialGasolina = 30000.0;
    private double capacidadInicialDiesel = 30000.0;

    // Cuotas de entrega de YPFB (Déficit de suministro)
    private double porcentajeEntregaYpfbGas = 100.0;
    private double porcentajeEntregaYpfbDiesel = 60.0;

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
    private double ingresosAcumuladosSubv = 0.0;

    /**
     * Inicializa los componentes del sistema (tanques, surtidores y eventos iniciales).
     */
    public void inicializarSistema(List<EstacionServicio> estacionesConfiguradas) {
        this.reloj = 0.0;
        this.calendarioEventos.clear();
        this.esperandoCisterna = false;
        this.contadorVehiculos = 0;
        
        // Resetear KPIs
        this.vehiculosAtendidosSubv = 0;
        this.vehiculosAtendidosInt = 0;
        this.tiempoEsperaTotal = 0.0;
        this.litrosVendidosSubv = 0.0;
        this.litrosVendidosInt = 0.0;
        this.vecesDesabastecido = 0;
        this.ingresosAcumuladosSubv = 0.0;

        this.redEstaciones.clear();

        if (estacionesConfiguradas != null) {
            this.redEstaciones.addAll(estacionesConfiguradas);
        }

        // 3. Inicializar / refrescar la Estación Internacional y sus surtidores
        EstacionServicio estInt = getEstacionInternacional();
        estInt.setNombre(nombreInternacional);
        
        // Refrescar o crear su tanque
        if (estInt.getTanqueGasolina() == null) {
            estInt.setTanqueGasolina(new TanqueCombustible("Gasolina Internacional", capacidadInternacional, capacidadInternacional));
        } else {
            estInt.getTanqueGasolina().setCapacidadMaxima(capacidadInternacional);
            estInt.getTanqueGasolina().setNivelActual(capacidadInternacional);
        }
        
        // Si por alguna razón se quedaron vacíos los surtidores, agregar uno por defecto
        if (estInt.getSurtidores().isEmpty()) {
            Surtidor sDefault = new Surtidor(idSurtidorInternacional, "Internacional", precioInternacional, estInt.getTanqueGasolina());
            sDefault.setCaudalLitrosPorMinuto(caudalSurtidorInternacional);
            estInt.getSurtidores().add(sDefault);
        }

        // Configurar, limpiar colas y asegurar el tanque correcto en los surtidores internacionales
        for (Surtidor s : estInt.getSurtidores()) {
            s.getFilaEspera().clear();
            s.setEstaOcupado(false);
            s.setTipo("Internacional");
            s.setPrecioPorLitro(precioInternacional);
            s.setTanqueAsignado(estInt.getTanqueGasolina());
        }

        // Limpiar colas de todos los surtidores subvencionados de la red
        for (EstacionServicio es : redEstaciones) {
            if (es.getTanqueGasolina() != null) {
                es.getTanqueGasolina().setNivelActual(es.getTanqueGasolina().getCapacidadMaxima());
            }
            if (es.getTanqueDiesel() != null) {
                es.getTanqueDiesel().setNivelActual(es.getTanqueDiesel().getCapacidadMaxima());
            }
            for (Surtidor s : es.getSurtidores()) {
                s.getFilaEspera().clear();
                s.setEstaOcupado(false);
            }
        }

        // Guardar referencias de compatibilidad
        this.tanqueInternacional = estInt.getTanqueGasolina();
        this.surtidorInternacional = getSurtidorInternacional();
        
        // 4. Agendar los primeros eventos de arribo
        double arriboPart = GeneradorEstocastico.generarInterArriboParticular();
        double arriboPes = GeneradorEstocastico.generarInterArriboPesado();

        this.calendarioEventos.add(new Evento(arriboPart, TipoEvento.ARRIBO_PARTICULAR, null, null));
        this.calendarioEventos.add(new Evento(arriboPes, TipoEvento.ARRIBO_PESADO, null, null));

        System.out.println("Sistema Inicializado:");
        System.out.println(" - Estaciones creadas: " + redEstaciones.size());
        System.out.println(" - Surtidores internacionales: " + estInt.getSurtidores().size());
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
        for (EstacionServicio es : redEstaciones) {
            System.out.println(" - " + es.getNombre() + ":");
            if (es.getTanqueGasolina() != null) {
                System.out.println("   Tanque Gasolina nivel final: " + String.format("%.2f", es.getTanqueGasolina().getNivelActual()) + " L");
            }
            if (es.getTanqueDiesel() != null) {
                System.out.println("   Tanque Diésel nivel final: " + String.format("%.2f", es.getTanqueDiesel().getNivelActual()) + " L");
            }
        }
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
                
        boolean esExtranjero = Math.random() < 0.05; // 5% de probabilidad
        Vehiculo vehiculo = new Vehiculo(idVehiculo, perfil, volumen, reloj, esExtranjero);

        // Enrutar al mejor surtidor según el algoritmo de costo de oportunidad con precios dinámicos y estaciones
        Surtidor elegido = MotorRuteo.elegirMejorSurtidor(vehiculo, redEstaciones, surtidorInternacional, precioGasolinaSubv, precioDieselSubv, precioInternacional, reloj);
        
        if (elegido == null) {
            System.out.println("[RELOJ: " + String.format("%.2f", reloj) + "] Arribo " + vehiculo.getId() 
                    + " (" + vehiculo.getPerfil() + ") se va sin cargar (estaciones cerradas o sin el combustible requerido).");
            return;
        }

        // Encolar el vehículo en el surtidor elegido
        elegido.getFilaEspera().add(vehiculo);
        
        System.out.println("[RELOJ: " + String.format("%.2f", reloj) + "] Arribo " + vehiculo.getId() 
                + " (" + vehiculo.getPerfil() + (vehiculo.isEsExtranjero() ? " - EXTRANJERO" : "") + ", req: " + String.format("%.2f", vehiculo.getVolumenRequerido()) + " L) "
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

        // Determinar qué tanque usar
        TanqueCombustible tanqueAUsar = null;
        if ("Internacional".equals(surtidor.getTipo())) {
            tanqueAUsar = surtidor.getTanqueAsignado();
        } else {
            // Buscar la estación que posee este surtidor
            for (EstacionServicio estacion : redEstaciones) {
                if (estacion.getSurtidores().contains(surtidor)) {
                    tanqueAUsar = "Particular".equals(vehiculo.getPerfil()) 
                            ? estacion.getTanqueGasolina() 
                            : estacion.getTanqueDiesel();
                    break;
                }
            }
        }

        // Verificar si el tanque correspondiente tiene stock suficiente
        if (tanqueAUsar.hayStock(vehiculo.getVolumenRequerido())) {
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
                        + "Vehículo " + vehiculo.getId() + " requiere " + String.format("%.2f", vehiculo.getVolumenRequerido()) + " L de " + tanqueAUsar.getTipoCombustible() + ", "
                        + "pero el tanque tiene sólo " + String.format("%.2f", tanqueAUsar.getNivelActual()) + " L. "
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
            // Determinar qué tanque usar
            TanqueCombustible tanqueAUsar = null;
            if ("Internacional".equals(surtidor.getTipo())) {
                tanqueAUsar = surtidor.getTanqueAsignado();
            } else {
                for (EstacionServicio estacion : redEstaciones) {
                    if (estacion.getSurtidores().contains(surtidor)) {
                        tanqueAUsar = "Particular".equals(vehiculo.getPerfil()) 
                                ? estacion.getTanqueGasolina() 
                                : estacion.getTanqueDiesel();
                        break;
                    }
                }
            }
            
            // Descontar combustible del tanque correspondiente
            tanqueAUsar.descontarStock(vehiculo.getVolumenRequerido());
            
            // Recopilar KPIs
            double esperaReal = reloj - vehiculo.getHoraArriboSimulacion();
            tiempoEsperaTotal += esperaReal;

            if ("Subvencionado".equalsIgnoreCase(surtidor.getTipo())) {
                vehiculosAtendidosSubv++;
                litrosVendidosSubv += vehiculo.getVolumenRequerido();
                double precioAplicadoSubv = "Particular".equalsIgnoreCase(vehiculo.getPerfil()) ? precioGasolinaSubv : precioDieselSubv;
                ingresosAcumuladosSubv += vehiculo.getVolumenRequerido() * precioAplicadoSubv;
            } else if ("Internacional".equalsIgnoreCase(surtidor.getTipo())) {
                vehiculosAtendidosInt++;
                litrosVendidosInt += vehiculo.getVolumenRequerido();
            }
            
            System.out.println("[RELOJ: " + String.format("%.2f", reloj) + "] Fin servicio " + vehiculo.getId() 
                    + " en " + surtidor.getId() + ". Consumo: " + String.format("%.2f", vehiculo.getVolumenRequerido()) + " L. "
                    + "Stock " + tanqueAUsar.getTipoCombustible() + ": " + String.format("%.2f", tanqueAUsar.getNivelActual()) + " L");
        }

        surtidor.setEstaOcupado(false);

        // Atender al siguiente de la cola (si existe)
        iniciarServicio(surtidor);
    }

    private void procesarCisterna() {
        esperandoCisterna = false;
        
        System.out.println("[RELOJ: " + String.format("%.2f", reloj) + "] === LLEGADA DE CISTERNA === "
                + "Abasteciendo tanques en la red de estaciones.");

        for (EstacionServicio estacion : redEstaciones) {
            double capGas = 0;
            double capDie = 0;
            double nivelGas = 0;
            double nivelDie = 0;
            if (estacion.getTanqueGasolina() != null) {
                capGas = estacion.getTanqueGasolina().getCapacidadMaxima();
                nivelGas = capGas * (porcentajeEntregaYpfbGas / 100.0);
                estacion.getTanqueGasolina().setNivelActual(nivelGas);
            }
            if (estacion.getTanqueDiesel() != null) {
                capDie = estacion.getTanqueDiesel().getCapacidadMaxima();
                nivelDie = capDie * (porcentajeEntregaYpfbDiesel / 100.0);
                estacion.getTanqueDiesel().setNivelActual(nivelDie);
            }
            
            System.out.println(" - " + estacion.getNombre() 
                    + ": Gasolina reabastecida a " + String.format("%.1f", nivelGas) + " L (" + porcentajeEntregaYpfbGas + "%), "
                    + "Diésel reabastecido a " + String.format("%.1f", nivelDie) + " L (" + porcentajeEntregaYpfbDiesel + "%).");
        }

        System.out.println("Reanudando servicios en cola.");

        // Intentar iniciar el servicio en todos los surtidores subvencionados desocupados que tengan fila
        for (EstacionServicio estacion : redEstaciones) {
            for (Surtidor s : estacion.getSurtidores()) {
                if (!s.isEstaOcupado()) {
                    iniciarServicio(s);
                }
            }
        }
    }

    /**
     * Genera e imprime en consola el reporte final con métricas y monetización.
     */
    public void generarReporteFinal() {
        int totalVehiculos = vehiculosAtendidosSubv + vehiculosAtendidosInt;
        double esperaMedia = totalVehiculos > 0 ? (tiempoEsperaTotal / totalVehiculos) : 0.0;
        double ingresosSubv = ingresosAcumuladosSubv;
        double ingresosInt = litrosVendidosInt * precioInternacional;
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

    public TanqueCombustible getTanqueGasolina() {
        if (!redEstaciones.isEmpty()) {
            return redEstaciones.get(0).getTanqueGasolina();
        }
        return null;
    }

    public TanqueCombustible getTanqueDiesel() {
        if (!redEstaciones.isEmpty()) {
            return redEstaciones.get(0).getTanqueDiesel();
        }
        return null;
    }

    public List<Surtidor> getSurtidoresSubvencionados() {
        List<Surtidor> all = new ArrayList<>();
        for (EstacionServicio es : redEstaciones) {
            all.addAll(es.getSurtidores());
        }
        return all;
    }

    public List<EstacionServicio> getRedEstaciones() {
        return redEstaciones;
    }

    public EstacionServicio getEstacionInternacional() {
        if (estacionInternacional == null) {
            TanqueCombustible tanqueInt = new TanqueCombustible("Gasolina Internacional", capacidadInternacional, capacidadInternacional);
            estacionInternacional = new EstacionServicio(nombreInternacional, 0.0, 24.0, true, false, tanqueInt, null);
            Surtidor s = new Surtidor(idSurtidorInternacional, "Internacional", precioInternacional, tanqueInt);
            s.setCaudalLitrosPorMinuto(caudalSurtidorInternacional);
            estacionInternacional.getSurtidores().add(s);
        }
        return estacionInternacional;
    }

    public Surtidor getSurtidorInternacional() {
        Surtidor mejor = null;
        for (Surtidor s : getEstacionInternacional().getSurtidores()) {
            if (mejor == null || s.getTamanoFila() < mejor.getTamanoFila()) {
                mejor = s;
            }
        }
        return mejor;
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

    public double getIngresosAcumuladosSubv() {
        return ingresosAcumuladosSubv;
    }

    public double getPrecioGasolinaSubv() {
        return precioGasolinaSubv;
    }

    public void setPrecioGasolinaSubv(double precioGasolinaSubv) {
        this.precioGasolinaSubv = precioGasolinaSubv;
    }

    public double getPrecioDieselSubv() {
        return precioDieselSubv;
    }

    public void setPrecioDieselSubv(double precioDieselSubv) {
        this.precioDieselSubv = precioDieselSubv;
    }

    public double getPrecioInternacional() {
        return precioInternacional;
    }

    public void setPrecioInternacional(double precioInternacional) {
        this.precioInternacional = precioInternacional;
    }

    public double getCapacidadInicialGasolina() {
        return capacidadInicialGasolina;
    }

    public void setCapacidadInicialGasolina(double capacidadInicialGasolina) {
        this.capacidadInicialGasolina = capacidadInicialGasolina;
    }

    public double getCapacidadInicialDiesel() {
        return capacidadInicialDiesel;
    }

    public void setCapacidadInicialDiesel(double capacidadInicialDiesel) {
        this.capacidadInicialDiesel = capacidadInicialDiesel;
    }

    public double getPorcentajeEntregaYpfbGas() {
        return porcentajeEntregaYpfbGas;
    }

    public void setPorcentajeEntregaYpfbGas(double porcentajeEntregaYpfbGas) {
        this.porcentajeEntregaYpfbGas = porcentajeEntregaYpfbGas;
    }

    public double getPorcentajeEntregaYpfbDiesel() {
        return porcentajeEntregaYpfbDiesel;
    }

    public void setPorcentajeEntregaYpfbDiesel(double porcentajeEntregaYpfbDiesel) {
        this.porcentajeEntregaYpfbDiesel = porcentajeEntregaYpfbDiesel;
    }

    public TanqueCombustible getTanqueInternacional() {
        return tanqueInternacional;
    }

    public void setTanqueInternacional(TanqueCombustible tanqueInternacional) {
        this.tanqueInternacional = tanqueInternacional;
    }

    public String getNombreInternacional() {
        return nombreInternacional;
    }

    public void setNombreInternacional(String nombreInternacional) {
        this.nombreInternacional = nombreInternacional;
    }

    public double getCapacidadInternacional() {
        return capacidadInternacional;
    }

    public void setCapacidadInternacional(double capacidadInternacional) {
        this.capacidadInternacional = capacidadInternacional;
    }

    public String getIdSurtidorInternacional() {
        return idSurtidorInternacional;
    }

    public void setIdSurtidorInternacional(String idSurtidorInternacional) {
        this.idSurtidorInternacional = idSurtidorInternacional;
    }

    public double getCaudalSurtidorInternacional() {
        return caudalSurtidorInternacional;
    }

    public void setCaudalSurtidorInternacional(double caudalSurtidorInternacional) {
        this.caudalSurtidorInternacional = caudalSurtidorInternacional;
    }
}
