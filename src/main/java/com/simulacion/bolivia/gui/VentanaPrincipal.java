package com.simulacion.bolivia.gui;

import com.simulacion.bolivia.engine.MotorSimulacion;
import com.simulacion.bolivia.models.Surtidor;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Ventana principal en Swing que representa la interfaz de usuario de la simulación.
 */
public class VentanaPrincipal extends JFrame implements SimulacionListener {
    private final MotorSimulacion motor;

    // Componentes del Dashboard (Norte)
    private JLabel lblReloj;
    private JLabel lblTanque;
    private JLabel lblAlarmaCisterna;

    // Componentes de Surtidores (Centro)
    private JPanel panelSurtidores;
    private final List<PanelSurtidor> panelesSurtidores = new ArrayList<>();

    // Componentes de Controles y KPIs (Sur)
    private JLabel lblVehiculosSub;
    private JLabel lblVehiculosInt;
    private JLabel lblIngresos;
    private JLabel lblEsperaMedia;
    private JButton btnIniciar;
    private JSlider sliderVelocidad;

    private boolean simIniciada = false;

    public VentanaPrincipal() {
        // 1. Instanciar el motor
        this.motor = new MotorSimulacion();
        this.motor.setListener(this);
        this.motor.inicializarSistema();

        // 2. Configurar el frame
        setTitle("Simulación Estación de Servicio - Bolivia (Trimestre 3)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 3. Crear secciones
        crearPanelNorte();
        crearPanelCentro();
        crearPanelSur();

        // Actualizar la interfaz inicial con el estado del sistema cargado
        onEstadoActualizado();
    }

    private void crearPanelNorte() {
        JPanel panelNorte = new JPanel(new GridLayout(1, 3, 10, 10));
        panelNorte.setBackground(new Color(40, 44, 52));
        panelNorte.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        lblReloj = new JLabel("Reloj: 0.00 min");
        lblReloj.setForeground(Color.WHITE);
        lblReloj.setFont(new Font("SansSerif", Font.BOLD, 14));

        lblTanque = new JLabel("Tanque Principal: 30000.00 L");
        lblTanque.setForeground(Color.WHITE);
        lblTanque.setFont(new Font("SansSerif", Font.BOLD, 14));

        lblAlarmaCisterna = new JLabel("ESTADO T. SUBVENCIONADO: OK");
        lblAlarmaCisterna.setForeground(new Color(40, 167, 69)); // Verde
        lblAlarmaCisterna.setFont(new Font("SansSerif", Font.BOLD, 14));

        panelNorte.add(lblReloj);
        panelNorte.add(lblTanque);
        panelNorte.add(lblAlarmaCisterna);

        add(panelNorte, BorderLayout.NORTH);
    }

    private void crearPanelCentro() {
        panelSurtidores = new JPanel(new GridLayout(1, 5));
        
        // Agregar surtidores subvencionados (4)
        for (Surtidor s : motor.getSurtidoresSubvencionados()) {
            PanelSurtidor ps = new PanelSurtidor(s);
            panelesSurtidores.add(ps);
            panelSurtidores.add(ps);
        }

        // Agregar surtidor internacional (1)
        PanelSurtidor psInt = new PanelSurtidor(motor.getSurtidorInternacional());
        panelesSurtidores.add(psInt);
        panelSurtidores.add(psInt);

        add(panelSurtidores, BorderLayout.CENTER);
    }

    private void crearPanelSur() {
        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        panelSur.setBackground(Color.WHITE);

        // Panel de KPIs
        JPanel panelKPIs = new JPanel(new GridLayout(2, 2, 10, 5));
        panelKPIs.setBackground(Color.WHITE);

        lblVehiculosSub = new JLabel("Vehículos Subvencionados: 0");
        lblVehiculosSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblVehiculosInt = new JLabel("Vehículos Internacionales: 0");
        lblVehiculosInt.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblIngresos = new JLabel("Ingresos totales: 0.00 Bs.");
        lblIngresos.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblEsperaMedia = new JLabel("Tiempo medio de espera: 0.00 min");
        lblEsperaMedia.setFont(new Font("SansSerif", Font.PLAIN, 12));

        panelKPIs.add(lblVehiculosSub);
        panelKPIs.add(lblVehiculosInt);
        panelKPIs.add(lblIngresos);
        panelKPIs.add(lblEsperaMedia);

        panelSur.add(panelKPIs, BorderLayout.CENTER);

        // Panel de Controles
        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        panelControles.setBackground(Color.WHITE);

        // Control de velocidad (delay ms)
        sliderVelocidad = new JSlider(JSlider.HORIZONTAL, 0, 500, 50);
        sliderVelocidad.setMajorTickSpacing(100);
        sliderVelocidad.setPaintTicks(true);
        sliderVelocidad.setPreferredSize(new Dimension(150, 45));
        sliderVelocidad.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(), "Retardo (ms)",
                0, 0, new Font("SansSerif", Font.PLAIN, 9)
        ));
        sliderVelocidad.addChangeListener(e -> {
            int delay = sliderVelocidad.getValue();
            motor.setVelocidadDelayMs(delay);
        });

        btnIniciar = new JButton("Iniciar Simulación");
        btnIniciar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnIniciar.setBackground(new Color(0, 123, 255));
        btnIniciar.setForeground(Color.WHITE);
        btnIniciar.setFocusPainted(false);
        
        btnIniciar.addActionListener(e -> {
            if (!simIniciada) {
                simIniciada = true;
                btnIniciar.setEnabled(false);
                btnIniciar.setText("Simulación en curso...");
                
                // Ejecutar la simulación en un hilo secundario para no bloquear el EDT (Event Dispatch Thread)
                new Thread(() -> {
                    // Simular un trimestre completo: 129,600 minutos
                    motor.ejecutarSimulacion(129600.0);
                    
                    // Al finalizar, habilitar el botón y mostrar el reporte en consola
                    SwingUtilities.invokeLater(() -> {
                        btnIniciar.setText("Simulación Finalizada");
                        motor.generarReporteFinal();
                    });
                }).start();
            }
        });

        panelControles.add(sliderVelocidad);
        panelControles.add(btnIniciar);

        panelSur.add(panelControles, BorderLayout.EAST);

        add(panelSur, BorderLayout.SOUTH);
    }

    @Override
    public void onEstadoActualizado() {
        // Ejecutar actualizaciones de Swing de forma segura en el EDT
        SwingUtilities.invokeLater(() -> {
            double relojActual = motor.getReloj();
            
            // Convertir minutos a días, horas y minutos para una lectura más humana
            int dias = (int) (relojActual / 1440.0);
            int horas = (int) ((relojActual % 1440.0) / 60.0);
            int mins = (int) (relojActual % 60.0);
            
            lblReloj.setText(String.format("Reloj: Día %d (%02d:%02d) - [%.1f min]", dias + 1, horas, mins, relojActual));

            // Actualizar Tanque Principal
            double nivelTanque = motor.getTanquePrincipal().getNivelActual();
            double capMax = motor.getTanquePrincipal().getCapacidadMaxima();
            lblTanque.setText(String.format("Tanque Principal: %.2f / %.0f L", nivelTanque, capMax));

            // Actualizar Alerta de Cisterna
            if (motor.isEsperandoCisterna()) {
                lblAlarmaCisterna.setText("!!! DESABASTECIDO - SOLICITADO CISNERNA !!!");
                lblAlarmaCisterna.setForeground(new Color(220, 53, 69)); // Rojo
            } else {
                lblAlarmaCisterna.setText("ESTADO T. SUBVENCIONADO: OK");
                lblAlarmaCisterna.setForeground(new Color(40, 167, 69)); // Verde
            }

            // Actualizar KPIs en la UI
            int atendidosSubv = motor.getVehiculosAtendidosSubv();
            int atendidosInt = motor.getVehiculosAtendidosInt();
            int totalAtendidos = atendidosSubv + atendidosInt;
            
            lblVehiculosSub.setText("Vehículos Subvencionados: " + atendidosSubv);
            lblVehiculosInt.setText("Vehículos Internacionales: " + atendidosInt);

            double ingresosSub = motor.getLitrosVendidosSubv() * motor.getPrecioSubvActual();
            double ingresosInt = motor.getLitrosVendidosInt() * motor.getPrecioIntActual();
            double ingresosTotales = ingresosSub + ingresosInt;
            lblIngresos.setText(String.format("Ingresos totales: %.2f Bs.", ingresosTotales));

            double esperaMedia = totalAtendidos > 0 ? (motor.getTiempoEsperaTotal() / totalAtendidos) : 0.0;
            lblEsperaMedia.setText(String.format("Tiempo medio de espera: %.2f min", esperaMedia));

            // Repintar los componentes de surtidores
            panelSurtidores.repaint();
        });
    }
}
