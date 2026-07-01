package com.simulacion.bolivia.gui;

import com.simulacion.bolivia.models.EstacionServicio;
import com.simulacion.bolivia.models.Surtidor;
import com.simulacion.bolivia.models.TanqueCombustible;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Diálogo modal para la configuración y edición de surtidores individuales de una estación.
 */
public class DialogGestorSurtidores extends JDialog {
    private JList<Surtidor> listaSurtidores;
    private DefaultListModel<Surtidor> modeloLista;
    private List<Surtidor> surtidores;
    private EstacionServicio estacion;

    // Campos del formulario
    private JTextField txtId;
    private JTextField txtPrecio;
    private JTextField txtCaudal;
    private JComboBox<String> comboTanque;
    private JButton btnGuardar;
    private JButton btnAgregar;
    private JButton btnEliminar;

    private Surtidor surtidorSeleccionado = null;

    public DialogGestorSurtidores(JDialog owner, EstacionServicio estacion) {
        super(owner, "Configurar Surtidores - " + estacion.getNombre(), true);
        this.estacion = estacion;
        this.surtidores = estacion.getSurtidores();

        setSize(650, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(Color.WHITE);

        // --- PANEL IZQUIERDO: LISTA DE SURTIDORES ---
        JPanel panelIzquierdo = new JPanel(new BorderLayout(10, 10));
        panelIzquierdo.setBackground(Color.WHITE);
        panelIzquierdo.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 0));
        panelIzquierdo.setPreferredSize(new Dimension(220, 0));

        modeloLista = new DefaultListModel<>();
        for (Surtidor s : surtidores) {
            modeloLista.addElement(s);
        }

        listaSurtidores = new JList<>(modeloLista);
        listaSurtidores.setFont(new Font("SansSerif", Font.PLAIN, 11));
        listaSurtidores.setBorder(BorderFactory.createEtchedBorder());
        JScrollPane scroll = new JScrollPane(listaSurtidores);
        panelIzquierdo.add(scroll, BorderLayout.CENTER);

        // Botones Agregar / Eliminar
        JPanel panelCRUD = new JPanel(new GridLayout(1, 2, 10, 0));
        panelCRUD.setBackground(Color.WHITE);

        btnAgregar = new JButton("➕ Agregar") {
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
        btnAgregar.setBackground(new Color(0, 123, 255));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnAgregar.setContentAreaFilled(false);
        btnAgregar.setBorderPainted(false);

        btnEliminar = new JButton("🗑️ Quitar") {
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
        btnEliminar.setBackground(new Color(220, 53, 69));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnEliminar.setContentAreaFilled(false);
        btnEliminar.setBorderPainted(false);

        panelCRUD.add(btnAgregar);
        panelCRUD.add(btnEliminar);
        panelIzquierdo.add(panelCRUD, BorderLayout.SOUTH);

        add(panelIzquierdo, BorderLayout.WEST);

        // --- PANEL DERECHO: FORMULARIO DE EDICIÓN ---
        JPanel panelDerecho = new JPanel(new BorderLayout(15, 15));
        panelDerecho.setBackground(Color.WHITE);
        panelDerecho.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Propiedades del Surtidor"),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JPanel panelForm = new JPanel(new GridLayout(4, 2, 10, 15));
        panelForm.setBackground(Color.WHITE);

        txtId = new JTextField();
        txtPrecio = new JTextField();
        txtCaudal = new JTextField();
        
        comboTanque = new JComboBox<>();
        comboTanque.setBackground(Color.WHITE);

        panelForm.add(new JLabel("ID Surtidor:"));
        panelForm.add(txtId);

        panelForm.add(new JLabel("Precio por Litro (Bs):"));
        panelForm.add(txtPrecio);

        panelForm.add(new JLabel("Caudal (L/min):"));
        panelForm.add(txtCaudal);

        panelForm.add(new JLabel("Tanque Asignado:"));
        panelForm.add(comboTanque);

        panelDerecho.add(panelForm, BorderLayout.NORTH);

        btnGuardar = new JButton("💾 Guardar Surtidor") {
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
        btnGuardar.setBackground(new Color(40, 167, 69));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnGuardar.setContentAreaFilled(false);
        btnGuardar.setBorderPainted(false);
        panelDerecho.add(btnGuardar, BorderLayout.SOUTH);

        add(panelDerecho, BorderLayout.CENTER);

        // --- CONTROL DE ESTADOS DE ENTRADAS ---
        setFieldsEnabled(false);

        listaSurtidores.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                surtidorSeleccionado = listaSurtidores.getSelectedValue();
                if (surtidorSeleccionado == null) {
                    setFieldsEnabled(false);
                    clearForm();
                } else {
                    setFieldsEnabled(true);
                    populateForm(surtidorSeleccionado);
                }
            }
        });

        btnGuardar.addActionListener(e -> {
            if (surtidorSeleccionado != null) {
                try {
                    String newId = txtId.getText().trim();
                    double newPrecio = Double.parseDouble(txtPrecio.getText().trim());
                    double newCaudal = Double.parseDouble(txtCaudal.getText().trim());
                    String tanqueSeleccionado = (String) comboTanque.getSelectedItem();

                    if (newId.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "El ID no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    surtidorSeleccionado.setId(newId);
                    surtidorSeleccionado.setPrecioPorLitro(newPrecio);
                    surtidorSeleccionado.setCaudalLitrosPorMinuto(newCaudal);

                    // Reasignar tanque
                    if ("Gasolina".equals(tanqueSeleccionado)) {
                        surtidorSeleccionado.setTanqueAsignado(estacion.getTanqueGasolina());
                    } else if ("Diésel".equals(tanqueSeleccionado)) {
                        surtidorSeleccionado.setTanqueAsignado(estacion.getTanqueDiesel());
                    } else {
                        surtidorSeleccionado.setTanqueAsignado(null);
                    }

                    // Forzar refrescado en JList
                    int index = listaSurtidores.getSelectedIndex();
                    modeloLista.setElementAt(surtidorSeleccionado, index);
                    listaSurtidores.repaint();

                    JOptionPane.showMessageDialog(this, "Surtidor guardado correctamente.", "Guardado", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Por favor, introduce valores válidos para precio y caudal.", "Formato Inválido", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnAgregar.addActionListener(e -> {
            TanqueCombustible tDef = estacion.getTanqueGasolina() != null ? estacion.getTanqueGasolina() : estacion.getTanqueDiesel();
            boolean isInt = tDef != null && ("Gasolina Internacional".equalsIgnoreCase(tDef.getTipoCombustible()) || "Internacional".equalsIgnoreCase(tDef.getTipoCombustible()));
            
            String tipo = isInt ? "Internacional" : "Subvencionado";
            double precio = isInt ? 12.50 : 3.74;
            if (!surtidores.isEmpty()) {
                precio = surtidores.get(0).getPrecioPorLitro();
            }

            Surtidor nuevo = new Surtidor(
                    "S" + (surtidores.size() + 1) + "_" + estacion.getNombre().replaceAll("\\s+", ""),
                    tipo,
                    precio,
                    tDef
            );
            
            double caudalDef = isInt ? 60.0 : 40.0;
            if (!surtidores.isEmpty()) {
                caudalDef = surtidores.get(0).getCaudalLitrosPorMinuto();
            }
            nuevo.setCaudalLitrosPorMinuto(caudalDef);

            surtidores.add(nuevo);
            modeloLista.addElement(nuevo);
            listaSurtidores.setSelectedValue(nuevo, true);
        });

        btnEliminar.addActionListener(e -> {
            int sel = listaSurtidores.getSelectedIndex();
            if (sel != -1) {
                Surtidor s = modeloLista.get(sel);
                surtidores.remove(s);
                modeloLista.remove(sel);
            }
        });

        if (!modeloLista.isEmpty()) {
            listaSurtidores.setSelectedIndex(0);
        }
    }

    private void setFieldsEnabled(boolean enabled) {
        txtId.setEnabled(enabled);
        txtPrecio.setEnabled(enabled);
        txtCaudal.setEnabled(enabled);
        comboTanque.setEnabled(enabled);
        btnGuardar.setEnabled(enabled);
    }

    private void clearForm() {
        txtId.setText("");
        txtPrecio.setText("");
        txtCaudal.setText("");
        comboTanque.removeAllItems();
    }

    private void populateForm(Surtidor s) {
        txtId.setText(s.getId());
        txtPrecio.setText(String.valueOf(s.getPrecioPorLitro()));
        txtCaudal.setText(String.valueOf(s.getCaudalLitrosPorMinuto()));

        comboTanque.removeAllItems();
        comboTanque.addItem("Ninguno");

        if (estacion.getTanqueGasolina() != null) {
            comboTanque.addItem("Gasolina");
        }
        if (estacion.getTanqueDiesel() != null) {
            comboTanque.addItem("Diésel");
        }

        // Seleccionar tanque actual
        if (s.getTanqueAsignado() != null) {
            String fuelType = s.getTanqueAsignado().getTipoCombustible();
            if ("Gasolina Especial".equalsIgnoreCase(fuelType) || "Gasolina Internacional".equalsIgnoreCase(fuelType)) {
                comboTanque.setSelectedItem("Gasolina");
            } else if ("Diésel".equalsIgnoreCase(fuelType)) {
                comboTanque.setSelectedItem("Diésel");
            }
        } else {
            comboTanque.setSelectedItem("Ninguno");
        }
    }
}
