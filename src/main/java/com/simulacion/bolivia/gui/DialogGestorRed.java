package com.simulacion.bolivia.gui;

import com.simulacion.bolivia.engine.MotorSimulacion;
import com.simulacion.bolivia.models.EstacionServicio;
import com.simulacion.bolivia.models.Surtidor;
import com.simulacion.bolivia.models.TanqueCombustible;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Diálogo modal para la gestión avanzada (CRUD) de la red de estaciones de servicio.
 */
public class DialogGestorRed extends JDialog {
    private JList<EstacionServicio> listaEstaciones;
    private DefaultListModel<EstacionServicio> modeloLista;
    private List<EstacionServicio> estaciones;
    private final double precioGasDefault;
    private MotorSimulacion motor;

    // Campos del Formulario
    private JTextField txtNombre;
    private JTextField txtDistancia;
    private JTextField txtApertura;
    private JTextField txtCierre;
    private JCheckBox chkGasolina;
    private JCheckBox chkDiesel;
    private JTextField txtCapGas;
    private JTextField txtCapDie;
    private JTextField txtBombas;
    private JComboBox<String> comboConectadoA;
    private JButton btnPersonalizarSurtidores;
    private JButton btnGuardar;
    private JButton btnNueva;
    private JButton btnEliminar;
    private JButton btnConfigurarInternacional;

    private EstacionServicio estacionSeleccionada = null;

    public DialogGestorRed(Frame parent, boolean modal, List<EstacionServicio> estaciones, MotorSimulacion motor) {
        super(parent, "Gestor de Red de Estaciones", modal);
        this.estaciones = estaciones;
        this.motor = motor;
        this.precioGasDefault = motor.getPrecioGasolinaSubv();

        setSize(800, 580);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(Color.WHITE);

        // --- SECCIÓN OESTE (LISTA DE ESTACIONES Y BOTONES CRUD) ---
        JPanel panelOeste = new JPanel(new BorderLayout(10, 10));
        panelOeste.setBackground(Color.WHITE);
        panelOeste.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 0));
        panelOeste.setPreferredSize(new Dimension(240, 0));

        modeloLista = new DefaultListModel<>();
        for (EstacionServicio es : estaciones) {
            modeloLista.addElement(es);
        }

        listaEstaciones = new JList<>(modeloLista);
        listaEstaciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaEstaciones.setFont(new Font("SansSerif", Font.PLAIN, 12));
        listaEstaciones.setBorder(BorderFactory.createEtchedBorder());
        JScrollPane scrollLista = new JScrollPane(listaEstaciones);
        panelOeste.add(scrollLista, BorderLayout.CENTER);

        // Botones Agregar / Eliminar
        JPanel panelBotonesCRUD = new JPanel(new GridLayout(1, 2, 10, 0));
        panelBotonesCRUD.setBackground(Color.WHITE);

        btnNueva = new JButton("➕ Nueva") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnNueva.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnNueva.setBackground(new Color(0, 123, 255)); // Azul
        btnNueva.setForeground(Color.WHITE);
        btnNueva.setContentAreaFilled(false);
        btnNueva.setBorderPainted(false);
        btnNueva.setFocusPainted(false);

        btnEliminar = new JButton("🗑️ Eliminar") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnEliminar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnEliminar.setBackground(new Color(220, 53, 69)); // Rojo
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setContentAreaFilled(false);
        btnEliminar.setBorderPainted(false);
        btnEliminar.setFocusPainted(false);

        panelBotonesCRUD.add(btnNueva);
        panelBotonesCRUD.add(btnEliminar);

        // Botón para configurar Estación Internacional
        btnConfigurarInternacional = new JButton("⚙️ Estación Internacional...") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnConfigurarInternacional.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnConfigurarInternacional.setBackground(new Color(108, 117, 125)); // Gris
        btnConfigurarInternacional.setForeground(Color.WHITE);
        btnConfigurarInternacional.setContentAreaFilled(false);
        btnConfigurarInternacional.setBorderPainted(false);
        btnConfigurarInternacional.setFocusPainted(false);

        JPanel panelControlesOeste = new JPanel(new GridLayout(2, 1, 0, 10));
        panelControlesOeste.setBackground(Color.WHITE);
        panelControlesOeste.add(panelBotonesCRUD);
        panelControlesOeste.add(btnConfigurarInternacional);

        panelOeste.add(panelControlesOeste, BorderLayout.SOUTH);

        add(panelOeste, BorderLayout.WEST);

        // --- SECCIÓN CENTRAL (FORMULARIO DE EDICIÓN) ---
        JPanel panelCentro = new JPanel(new BorderLayout(15, 15));
        panelCentro.setBackground(Color.WHITE);
        panelCentro.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Edición de Estación"),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Subimos a 9 filas en GridLayout para acomodar "Conectar a"
        JPanel panelForm = new JPanel(new GridLayout(9, 2, 10, 12));
        panelForm.setBackground(Color.WHITE);

        txtNombre = new JTextField();
        txtDistancia = new JTextField();
        txtApertura = new JTextField();
        txtCierre = new JTextField();
        
        chkGasolina = new JCheckBox("Habilitar Gasolina", true);
        chkGasolina.setBackground(Color.WHITE);
        chkDiesel = new JCheckBox("Habilitar Diésel", true);
        chkDiesel.setBackground(Color.WHITE);

        txtCapGas = new JTextField();
        txtCapDie = new JTextField();
        txtBombas = new JTextField();
        
        comboConectadoA = new JComboBox<>();
        comboConectadoA.setBackground(Color.WHITE);

        panelForm.add(new JLabel("Nombre Estación:"));
        panelForm.add(txtNombre);

        panelForm.add(new JLabel("Conectar a (Estación):"));
        panelForm.add(comboConectadoA);

        panelForm.add(new JLabel("Distancia a Conector (Km):"));
        panelForm.add(txtDistancia);

        panelForm.add(new JLabel("Hora Apertura (0-24):"));
        panelForm.add(txtApertura);

        panelForm.add(new JLabel("Hora Cierre (0-24):"));
        panelForm.add(txtCierre);

        panelForm.add(new JLabel("Gasolina:"));
        JPanel panelGas = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelGas.setBackground(Color.WHITE);
        panelGas.add(chkGasolina);
        panelForm.add(panelGas);

        panelForm.add(new JLabel("Capacidad Gasolina (L):"));
        panelForm.add(txtCapGas);

        panelForm.add(new JLabel("Diésel:"));
        JPanel panelDie = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelDie.setBackground(Color.WHITE);
        panelDie.add(chkDiesel);
        panelForm.add(panelDie);

        panelForm.add(new JLabel("Capacidad Diésel (L):"));
        panelForm.add(txtCapDie);

        panelCentro.add(panelForm, BorderLayout.NORTH);

        // Bombas y Botón de guardar abajo
        JPanel panelGuardarBox = new JPanel(new GridLayout(2, 1, 10, 15));
        panelGuardarBox.setBackground(Color.WHITE);

        // Línea de surtidores con botón de personalización
        JPanel panelBombasRow = new JPanel(new BorderLayout(10, 0));
        panelBombasRow.setBackground(Color.WHITE);
        
        txtBombas = new JTextField();
        panelBombasRow.add(txtBombas, BorderLayout.CENTER);
        
        btnPersonalizarSurtidores = new JButton("⚙️ Personalizar Surtidores...") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnPersonalizarSurtidores.setBackground(new Color(108, 117, 125)); // Gris
        btnPersonalizarSurtidores.setForeground(Color.WHITE);
        btnPersonalizarSurtidores.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnPersonalizarSurtidores.setContentAreaFilled(false);
        btnPersonalizarSurtidores.setBorderPainted(false);
        btnPersonalizarSurtidores.setFocusPainted(false);
        panelBombasRow.add(btnPersonalizarSurtidores, BorderLayout.EAST);

        JPanel panelBombasRowWrapper = new JPanel(new GridLayout(1, 2, 10, 0));
        panelBombasRowWrapper.setBackground(Color.WHITE);
        panelBombasRowWrapper.add(new JLabel("Número de Bombas (Surtidores):"));
        panelBombasRowWrapper.add(panelBombasRow);
        panelGuardarBox.add(panelBombasRowWrapper);

        btnGuardar = new JButton("💾 Guardar Cambios en Estación Seleccionada") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isEnabled()) {
                    g2.setColor(getBackground());
                } else {
                    g2.setColor(Color.LIGHT_GRAY);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnGuardar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnGuardar.setBackground(new Color(40, 167, 69)); // Verde
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setContentAreaFilled(false);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setFocusPainted(false);
        panelGuardarBox.add(btnGuardar);

        panelCentro.add(panelGuardarBox, BorderLayout.SOUTH);

        add(panelCentro, BorderLayout.CENTER);

        // --- LÓGICA DE INTERACCIÓN ---

        // Deshabilitar formulario por defecto
        setFormEnabled(false);

        // Evento de selección
        listaEstaciones.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                estacionSeleccionada = listaEstaciones.getSelectedValue();
                if (estacionSeleccionada == null) {
                    setFormEnabled(false);
                    clearForm();
                } else {
                    setFormEnabled(true);
                    populateForm(estacionSeleccionada);
                }
            }
        });

        // Evento de Guardado
        btnGuardar.addActionListener(e -> {
            if (estacionSeleccionada != null) {
                try {
                    String nombre = txtNombre.getText().trim();
                    double dist = Double.parseDouble(txtDistancia.getText().trim());
                    double ape = Double.parseDouble(txtApertura.getText().trim());
                    double cie = Double.parseDouble(txtCierre.getText().trim());
                    boolean vendeGas = chkGasolina.isSelected();
                    boolean vendeDie = chkDiesel.isSelected();
                    double capGas = Double.parseDouble(txtCapGas.getText().trim());
                    double capDie = Double.parseDouble(txtCapDie.getText().trim());
                    int numBombas = Integer.parseInt(txtBombas.getText().trim());
                    String conectado = (String) comboConectadoA.getSelectedItem();

                    if (nombre.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (!vendeGas && !vendeDie) {
                        JOptionPane.showMessageDialog(this, "Debe habilitar al menos un tipo de combustible.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (numBombas <= 0) {
                        JOptionPane.showMessageDialog(this, "Debe tener al menos 1 bomba.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Actualizar datos de la estación
                    estacionSeleccionada.setNombre(nombre);
                    estacionSeleccionada.setDistanciaKm(dist);
                    estacionSeleccionada.setHoraApertura(ape);
                    estacionSeleccionada.setHoraCierre(cie);
                    estacionSeleccionada.setVendeGasolina(vendeGas);
                    estacionSeleccionada.setVendeDiesel(vendeDie);
                    estacionSeleccionada.setConectadoA(conectado);

                    // Reinstanciar tanques
                    TanqueCombustible tg = vendeGas ? new TanqueCombustible("Gasolina Especial", capGas, capGas) : null;
                    TanqueCombustible td = vendeDie ? new TanqueCombustible("Diésel", capDie, capDie) : null;
                    estacionSeleccionada.setTanqueGasolina(tg);
                    estacionSeleccionada.setTanqueDiesel(td);

                    // Si el número de bombas cambió respecto al tamaño actual de la lista, re-generar por defecto
                    if (estacionSeleccionada.getSurtidores().size() != numBombas) {
                        estacionSeleccionada.getSurtidores().clear();
                        for (int j = 1; j <= numBombas; j++) {
                            TanqueCombustible tanqueAsignado = tg != null ? tg : td;
                            Surtidor s = new Surtidor("S" + j + "_" + nombre.replaceAll("\\s+", ""), "Subvencionado", precioGasDefault, tanqueAsignado);
                            estacionSeleccionada.getSurtidores().add(s);
                        }
                    }

                    // Refrescar JList
                    int selIndex = listaEstaciones.getSelectedIndex();
                    modeloLista.setElementAt(estacionSeleccionada, selIndex);
                    listaEstaciones.repaint();

                    JOptionPane.showMessageDialog(this, "Cambios guardados con éxito.", "Guardado", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Introduce valores numéricos válidos en distancias, horas, capacidades y bombas.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Evento de Nueva Estación
        btnNueva.addActionListener(e -> {
            TanqueCombustible tg = new TanqueCombustible("Gasolina Especial", 30000.0, 30000.0);
            TanqueCombustible td = new TanqueCombustible("Diésel", 30000.0, 30000.0);
            EstacionServicio es = new EstacionServicio("Nueva Estación " + (modeloLista.getSize() + 1), 0.0, 24.0, true, true, tg, td);
            es.setDistanciaKm(5.0);
            es.setConectadoA("Internacional");
            es.getSurtidores().add(new Surtidor("S1_NuevaEstacion", "Subvencionado", precioGasDefault, tg));
            es.getSurtidores().add(new Surtidor("S2_NuevaEstacion", "Subvencionado", precioGasDefault, tg));

            estaciones.add(es);
            modeloLista.addElement(es);
            listaEstaciones.setSelectedValue(es, true);
        });

        // Evento de Eliminación
        btnEliminar.addActionListener(e -> {
            int selIndex = listaEstaciones.getSelectedIndex();
            if (selIndex != -1) {
                EstacionServicio es = modeloLista.get(selIndex);
                estaciones.remove(es);
                modeloLista.remove(selIndex);
            }
        });

        // Evento de Personalización Individual de Surtidores
        btnPersonalizarSurtidores.addActionListener(e -> {
            if (estacionSeleccionada != null) {
                DialogGestorSurtidores dialogSurtidores = new DialogGestorSurtidores(this, estacionSeleccionada);
                dialogSurtidores.setVisible(true);
                // Actualizar la caja de texto de bombas con la cantidad final
                txtBombas.setText(String.valueOf(estacionSeleccionada.getSurtidores().size()));
            }
        });

        // Evento de Configuración de la Estación Internacional
        btnConfigurarInternacional.addActionListener(e -> {
            DialogConfiguracionInternacional diag = new DialogConfiguracionInternacional(this, motor);
            diag.setVisible(true);
        });

        // Auto-seleccionar primer elemento si existe
        if (!modeloLista.isEmpty()) {
            listaEstaciones.setSelectedIndex(0);
        }
    }

    private void setFormEnabled(boolean enabled) {
        txtNombre.setEnabled(enabled);
        txtDistancia.setEnabled(enabled);
        txtApertura.setEnabled(enabled);
        txtCierre.setEnabled(enabled);
        chkGasolina.setEnabled(enabled);
        chkDiesel.setEnabled(enabled);
        txtCapGas.setEnabled(enabled);
        txtCapDie.setEnabled(enabled);
        txtBombas.setEnabled(enabled);
        comboConectadoA.setEnabled(enabled);
        btnPersonalizarSurtidores.setEnabled(enabled);
        btnGuardar.setEnabled(enabled);
    }

    private void clearForm() {
        txtNombre.setText("");
        txtDistancia.setText("");
        txtApertura.setText("");
        txtCierre.setText("");
        chkGasolina.setSelected(false);
        chkDiesel.setSelected(false);
        txtCapGas.setText("");
        txtCapDie.setText("");
        txtBombas.setText("");
        comboConectadoA.removeAllItems();
    }

    private void populateForm(EstacionServicio es) {
        txtNombre.setText(es.getNombre());
        txtDistancia.setText(String.valueOf(es.getDistanciaKm()));
        txtApertura.setText(String.valueOf(es.getHoraApertura()));
        txtCierre.setText(String.valueOf(es.getHoraCierre()));
        chkGasolina.setSelected(es.isVendeGasolina());
        chkDiesel.setSelected(es.isVendeDiesel());

        double capGas = es.getTanqueGasolina() != null ? es.getTanqueGasolina().getCapacidadMaxima() : 0.0;
        double capDie = es.getTanqueDiesel() != null ? es.getTanqueDiesel().getCapacidadMaxima() : 0.0;

        txtCapGas.setText(String.valueOf(capGas));
        txtCapDie.setText(String.valueOf(capDie));
        txtBombas.setText(String.valueOf(es.getSurtidores().size()));

        // Popular JComboBox de conexiones excluyendo la actual
        comboConectadoA.removeAllItems();
        comboConectadoA.addItem("Internacional");
        for (EstacionServicio estacionRed : estaciones) {
            if (!estacionRed.getNombre().equalsIgnoreCase(es.getNombre())) {
                comboConectadoA.addItem(estacionRed.getNombre());
            }
        }

        // Seleccionar conexión guardada
        if (es.getConectadoA() != null) {
            comboConectadoA.setSelectedItem(es.getConectadoA());
        } else {
            comboConectadoA.setSelectedItem("Internacional");
        }
    }
}
