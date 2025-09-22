package cine.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import cine.model.Pelicula;
import cine.service.PeliculaService;

public class PeliculaForm extends JDialog{
    private final JTextField txtTitulo   = new JTextField();
    private final JTextField txtDirector = new JTextField();
    private final JSpinner   spAnio      = new JSpinner(new SpinnerNumberModel(2024, 1900, 2100, 1));
    private final JSpinner   spDuracion  = new JSpinner(new SpinnerNumberModel(120, 1, 500, 1));
    private final JComboBox<String> cbGenero = new JComboBox<>(new String[]{
            "ACCION","DRAMA","COMEDIA","ANIMACION","TERROR","CIENCIA_FICCION"
    });

    private final PeliculaService service = new PeliculaService();

    public PeliculaForm(Frame owner) {
        super(owner, "Nueva pelicula", true);
        setSize(520, 410);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // ====== ENCABEZADO ======
        var header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(16, 16, 8, 16));
        var titulo = new JLabel("Agregar pelicula");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        var sub = new JLabel("Completa todos los campos obligatorios (*)");
        sub.setForeground(new Color(100,100,100));
        header.add(titulo, BorderLayout.NORTH);
        header.add(sub, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // ====== FORM ======
        var form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(8, 16, 8, 16));
        var c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        addField(form, c, row++, "Titulo *", txtTitulo);
        addField(form, c, row++, "Director *", txtDirector);
        addField(form, c, row++, "Año *", spAnio);
        addField(form, c, row++, "Duracion (min) *", spDuracion);
        addField(form, c, row++, "Genero *", cbGenero);

        add(form, BorderLayout.CENTER);

        // ====== BOTONES ======
        var buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttons.setBorder(new EmptyBorder(4, 12, 12, 12));
        var btnGuardar = new JButton("Guardar");
        var btnLimpiar = new JButton("Limpiar");
        var btnCancelar= new JButton("Cancelar");

        btnGuardar.addActionListener(e -> onGuardar());
        btnLimpiar.addActionListener(e -> limpiar());
        btnCancelar.addActionListener(e -> dispose());

        // atajos
        btnGuardar.setMnemonic('G');
        btnCancelar.setMnemonic('C');
        getRootPane().setDefaultButton(btnGuardar);
        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        buttons.add(btnLimpiar);
        buttons.add(btnCancelar);
        buttons.add(btnGuardar);
        add(buttons, BorderLayout.SOUTH);
    }

    private void addField(JPanel panel, GridBagConstraints c, int row, String label, JComponent comp) {
        c.gridx = 0; c.gridy = row; c.weightx = 0; c.gridwidth = 1;
        var l = new JLabel(label);
        panel.add(l, c);

        c.gridx = 1; c.weightx = 1; c.gridwidth = 2;
        comp.setPreferredSize(new Dimension(280, 30));
        panel.add(comp, c);
    }

    private void limpiar() {
        txtTitulo.setText("");
        txtDirector.setText("");
        spAnio.setValue(2024);
        spDuracion.setValue(120);
        cbGenero.setSelectedIndex(0);
        txtTitulo.requestFocus();
    }

    private void onGuardar() {
        try {
            var p = new Pelicula(
                    txtTitulo.getText(),
                    txtDirector.getText(),
                    (Integer) spAnio.getValue(),
                    (Integer) spDuracion.getValue(),
                    (String) cbGenero.getSelectedItem()
            );
            long id = service.agregar(p);
            JOptionPane.showMessageDialog(this,
                    "Pelicula guardada con ID: " + id,
                    "Exito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validacion", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
