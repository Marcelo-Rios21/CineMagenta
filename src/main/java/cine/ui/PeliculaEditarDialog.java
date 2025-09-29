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
import java.util.Optional;

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

public class PeliculaEditarDialog extends JDialog {
    //BUSQUEDA  
    private final JSpinner spId = new JSpinner(new SpinnerNumberModel(1L, 1L, Long.MAX_VALUE, 1L));
    private final JTextField txtBuscarTitulo = new JTextField();
    private final JButton btnBuscar = new JButton("Buscar");
    private final JButton btnLimpiarBusqueda = new JButton("Limpiar");

    //EDICION
    private final JTextField txtTitulo   = new JTextField();
    private final JTextField txtDirector = new JTextField();
    private final JSpinner   spAnio      = new JSpinner(new SpinnerNumberModel(2024, 1900, 2100, 1));
    private final JSpinner   spDuracion  = new JSpinner(new SpinnerNumberModel(120, 1, 500, 1));
    private final JComboBox<String> cbGenero = new JComboBox<>(new String[]{
            "ACCION","DRAMA","COMEDIA","ANIMACION","TERROR","CIENCIA_FICCION"
    });

    private final JButton btnGuardar = new JButton("Guardar cambios");
    private final JButton btnLimpiar = new JButton("Limpiar");
    private final JButton btnCerrar  = new JButton("Cerrar");

    private final PeliculaService service = new PeliculaService();

    //ESTADO
    private Long currentId = null;

    public PeliculaEditarDialog(Frame owner) {
        super(owner, "Modificar pelicula", true);
        setSize(620, 540);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        // Acciones
        btnBuscar.addActionListener(e -> onBuscar());
        btnLimpiarBusqueda.addActionListener(e -> limpiarBusqueda());
        btnGuardar.addActionListener(e -> onGuardar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnCerrar.addActionListener(e -> dispose());

        // Atajo ESC para cerrar
        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        // Al iniciar, el formulario debe estar deshabilitado hasta cargar una pelicula
        habilitarFormulario(false);
    }

    private JComponent buildHeader() {
        var header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(16, 16, 8, 16));
        var titulo = new JLabel("Modificar pelicula");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        var sub = new JLabel("Busca por ID (principal). Tambien puedes buscar por TITULO exacto (extra).");
        sub.setForeground(new Color(100,100,100));
        header.add(titulo, BorderLayout.NORTH);
        header.add(sub, BorderLayout.CENTER);
        return header;
    }

    private JComponent buildContent() {
        var root = new JPanel(new BorderLayout());
        root.add(buildSearch(), BorderLayout.NORTH);
        root.add(buildForm(), BorderLayout.CENTER);
        return root;
    }

    private JComponent buildSearch() {
        var p = new JPanel(new GridBagLayout());
        p.setBorder(new EmptyBorder(8, 16, 8, 16));
        var c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // ID
        addField(p, c, row++, "ID *", spId);

        // Título (extra A)
        addField(p, c, row++, "Título (exacto)", txtBuscarTitulo);

        // Botonera búsqueda
        c.gridx = 0; c.gridy = row; c.weightx = 0; c.gridwidth = 1;
        p.add(new JLabel("Acciones"), c);
        c.gridx = 1; c.weightx = 1; c.gridwidth = 2;
        var actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnBuscar.setMnemonic('B');
        btnLimpiarBusqueda.setMnemonic('L');
        actions.add(btnBuscar);
        actions.add(btnLimpiarBusqueda);
        p.add(actions, c);

        return p;
    }

    private JComponent buildForm() {
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

        return form;
    }

    private JComponent buildButtons() {
        var panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBorder(new EmptyBorder(4, 12, 12, 12));
        btnGuardar.setMnemonic('G');
        btnCerrar.setMnemonic('C');

        // Botón por defecto: Guardar
        getRootPane().setDefaultButton(btnGuardar);

        panel.add(btnLimpiar);
        panel.add(btnCerrar);
        panel.add(btnGuardar);
        return panel;
    }

    private void addField(JPanel panel, GridBagConstraints c, int row, String label, JComponent comp) {
        c.gridx = 0; c.gridy = row; c.weightx = 0; c.gridwidth = 1;
        var l = new JLabel(label);
        panel.add(l, c);

        c.gridx = 1; c.weightx = 1; c.gridwidth = 2;
        comp.setPreferredSize(new Dimension(340, 30));
        panel.add(comp, c);
    }

    //ACCIONES
    private void onBuscar() {
        try {
            Long id = ((Number) spId.getValue()).longValue();
            Optional<Pelicula> opt = Optional.empty();

            if (id != null && id > 0) {
                opt = service.buscarPorId(id);
            } else {
                String titulo = txtBuscarTitulo.getText();
                if (titulo != null && !titulo.isBlank()) {
                    opt = service.buscarPorTituloExacto(titulo);
                }
            }

            if (opt.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No se encontró la película. Verifica el ID o el Título exacto.",
                        "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                habilitarFormulario(false);
                currentId = null;
                return;
            }

            Pelicula p = opt.get();
            currentId = p.getId();
            cargarEnFormulario(p);
            habilitarFormulario(true);

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al buscar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onGuardar() {
        if (currentId == null || currentId <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Debes buscar y cargar una pelicula antes de guardar.",
                    "Edición", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Pelicula p = construirEntidadDesdeFormulario();
            p.setId(currentId);

            service.actualizar(p);

            JOptionPane.showMessageDialog(this,
                    "Cambios guardados correctamente.",
                    "Exito", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Validacion", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al guardar cambios: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarBusqueda() {
        spId.setValue(1L);
        txtBuscarTitulo.setText("");
        currentId = null;
        limpiarFormulario();
        habilitarFormulario(false);
        spId.requestFocusInWindow();
    }

    private void limpiarFormulario() {
        txtTitulo.setText("");
        txtDirector.setText("");
        spAnio.setValue(2024);
        spDuracion.setValue(120);
        cbGenero.setSelectedIndex(0);
    }

    private void habilitarFormulario(boolean enabled) {
        txtTitulo.setEnabled(enabled);
        txtDirector.setEnabled(enabled);
        spAnio.setEnabled(enabled);
        spDuracion.setEnabled(enabled);
        cbGenero.setEnabled(enabled);
        btnGuardar.setEnabled(enabled);
        btnLimpiar.setEnabled(enabled);
    }

    private void cargarEnFormulario(Pelicula p) {
        txtTitulo.setText(p.getTitulo());
        txtDirector.setText(p.getDirector());
        spAnio.setValue(p.getAnio());
        spDuracion.setValue(p.getDuracionMin());
        cbGenero.setSelectedItem(p.getGenero() != null ? p.getGenero().toUpperCase() : "ACCION");
    }

    private Pelicula construirEntidadDesdeFormulario() {
        return new Pelicula(
                txtTitulo.getText(),
                txtDirector.getText(),
                (Integer) spAnio.getValue(),
                (Integer) spDuracion.getValue(),
                (String) cbGenero.getSelectedItem()
        );
    }
}
