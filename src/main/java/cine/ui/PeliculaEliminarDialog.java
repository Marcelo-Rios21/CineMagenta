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

public class PeliculaEliminarDialog extends JDialog {
    
    //BUSQUEDA
    private final JSpinner spId = new JSpinner(new SpinnerNumberModel(1L, 1L, Long.MAX_VALUE, 1L));
    private final JTextField txtBuscarTitulo = new JTextField();
    private final JButton btnBuscar = new JButton("Buscar");
    private final JButton btnLimpiarBusqueda = new JButton("Limpiar");

    private final JLabel lbTituloValor   = new JLabel("-");
    private final JLabel lbDirectorValor = new JLabel("-");
    private final JLabel lbAnioValor     = new JLabel("-");
    private final JLabel lbDuracionValor = new JLabel("-");
    private final JLabel lbGeneroValor   = new JLabel("-");

    //ACCIONES
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnCerrar   = new JButton("Cerrar");

    private final PeliculaService service = new PeliculaService();

    //ESTADO
    private Long currentId = null;

    public PeliculaEliminarDialog(Frame owner) {
        super(owner, "Eliminar película", true);
        setSize(600, 460);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        // Acciones
        btnBuscar.addActionListener(e -> onBuscar());
        btnLimpiarBusqueda.addActionListener(e -> limpiarBusqueda());
        btnEliminar.addActionListener(e -> onEliminar());
        btnCerrar.addActionListener(e -> dispose());

        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        // Estado inicial
        habilitarAcciones(false);
    }

    private JComponent buildHeader() {
        var header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(16, 16, 8, 16));
        var titulo = new JLabel("Eliminar película");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        var sub = new JLabel("Busca por ID (principal). También puedes buscar por TÍTULO exacto (extra).");
        sub.setForeground(new Color(100,100,100));
        header.add(titulo, BorderLayout.NORTH);
        header.add(sub, BorderLayout.CENTER);
        return header;
    }

    private JComponent buildContent() {
        var root = new JPanel(new BorderLayout());
        root.add(buildSearch(), BorderLayout.NORTH);
        root.add(buildDetail(), BorderLayout.CENTER);
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

        addField(p, c, row++, "ID *", spId);
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

    private JComponent buildDetail() {
        var form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(8, 16, 8, 16));
        var c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addReadOnly(form, c, row++, "Título",   lbTituloValor);
        addReadOnly(form, c, row++, "Director", lbDirectorValor);
        addReadOnly(form, c, row++, "Año",      lbAnioValor);
        addReadOnly(form, c, row++, "Duración", lbDuracionValor);
        addReadOnly(form, c, row++, "Género",   lbGeneroValor);

        return form;
    }

    private JComponent buildButtons() {
        var panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBorder(new EmptyBorder(4, 12, 12, 12));
        btnEliminar.setMnemonic('E');
        btnCerrar.setMnemonic('C');

        panel.add(btnCerrar);
        panel.add(btnEliminar);
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

    private void addReadOnly(JPanel panel, GridBagConstraints c, int row, String label, JLabel valueLabel) {
        c.gridx = 0; c.gridy = row; c.weightx = 0; c.gridwidth = 1;
        var l = new JLabel(label);
        panel.add(l, c);

        c.gridx = 1; c.weightx = 1; c.gridwidth = 2;
        valueLabel.setPreferredSize(new Dimension(340, 30));
        valueLabel.setForeground(new Color(40, 40, 40));
        panel.add(valueLabel, c);
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
                        "No se encontró la pelicula. Verifica el ID o el Titulo exacto.",
                        "Busqueda", JOptionPane.INFORMATION_MESSAGE);
                limpiarDetalle();
                habilitarAcciones(false);
                currentId = null;
                return;
            }

            Pelicula p = opt.get();
            currentId = p.getId();
            cargarDetalle(p);
            habilitarAcciones(true);

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Validacion", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al buscar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEliminar() {
        if (currentId == null || currentId <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Debes buscar y cargar una pelicula antes de eliminar.",
                    "Eliminar", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resp = JOptionPane.showConfirmDialog(
                this,
                "Confirma eliminar la película con ID " + currentId + "?\n" +
                "Esta accion no se puede deshacer.",
                "Confirmar eliminacion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (resp != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            service.eliminarPorId(currentId);
            JOptionPane.showMessageDialog(this,
                    "Pelicula eliminada correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Validacion", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al eliminar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarBusqueda() {
        spId.setValue(1L);
        txtBuscarTitulo.setText("");
        currentId = null;
        limpiarDetalle();
        habilitarAcciones(false);
        spId.requestFocusInWindow();
    }

    private void limpiarDetalle() {
        lbTituloValor.setText("-");
        lbDirectorValor.setText("-");
        lbAnioValor.setText("-");
        lbDuracionValor.setText("-");
        lbGeneroValor.setText("-");
    }

    private void cargarDetalle(Pelicula p) {
        lbTituloValor.setText(nullToDash(p.getTitulo()));
        lbDirectorValor.setText(nullToDash(p.getDirector()));
        lbAnioValor.setText(p.getAnio() != null ? String.valueOf(p.getAnio()) : "-");
        lbDuracionValor.setText(p.getDuracionMin() != null ? p.getDuracionMin() + " min" : "-");
        lbGeneroValor.setText(nullToDash(p.getGenero()));
    }

    private String nullToDash(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }

    private void habilitarAcciones(boolean enabled) {
        btnEliminar.setEnabled(enabled);
    }
}
