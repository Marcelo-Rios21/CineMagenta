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
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import cine.model.Pelicula;
import cine.service.PeliculaService;

public class PeliculaListDialog extends JDialog {
    private final JComboBox<String> cbGenero = new JComboBox<>(new String[]{
            "TODOS","ACCION","DRAMA","COMEDIA","ANIMACION","TERROR","CIENCIA_FICCION"
    });
    private final JSpinner spDesde = new JSpinner(new SpinnerNumberModel(1900, 1900, 2100, 1));
    private final JSpinner spHasta = new JSpinner(new SpinnerNumberModel(2100, 1900, 2100, 1));
    private final JButton btnBuscar = new JButton("Buscar");
    private final JButton btnLimpiar = new JButton("Limpiar");
    private final JButton btnCerrar = new JButton("Cerrar");

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID","TÍTULO","DIRECTOR","AÑO","DURACIÓN (min)","GÉNERO"}, 0
    ) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
        @Override public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case 0, 3, 4 -> Integer.class; // ID, AÑO, DURACIÓN
                default -> String.class;
            };
        }
    };
    private final JTable table = new JTable(model);
    private final JLabel lblEstado = new JLabel("0 registros.");
    private final PeliculaService service = new PeliculaService();

    public PeliculaListDialog(Frame owner) {
        super(owner, "Listado de peliculas", true);
        setSize(820, 560);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        btnBuscar.addActionListener(e -> onBuscar());
        btnLimpiar.addActionListener(e -> onLimpiar());
        btnCerrar.addActionListener(e -> dispose());

        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        getRootPane().setDefaultButton(btnBuscar);
        var im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        var am = getRootPane().getActionMap();

        im.put(KeyStroke.getKeyStroke("F5"), "buscar");
        am.put("buscar", new javax.swing.AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                btnBuscar.doClick();
            }
        });

        im.put(KeyStroke.getKeyStroke("F9"), "limpiar");
        am.put("limpiar", new javax.swing.AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                btnLimpiar.doClick();
            }
        });

        cargarTodas();
    }

    private JComponent buildHeader() {
        var header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(16, 16, 6, 16));
        var titulo = new JLabel("Listado de peliculas");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        var sub = new JLabel("Aplicar filtros por genero y rango de años, o ver la lista completa.");
        sub.setForeground(new Color(100,100,100));
        header.add(titulo, BorderLayout.NORTH);
        header.add(sub, BorderLayout.CENTER);
        return header;
    }

    private JComponent buildCenter() {
        var root = new JPanel(new BorderLayout());
        root.add(buildFilters(), BorderLayout.NORTH);

        table.setFillsViewportHeight(true);
        table.setRowHeight(22);
        table.setAutoCreateRowSorter(true); 

        var hdr = (javax.swing.table.DefaultTableCellRenderer)
                table.getTableHeader().getDefaultRenderer();
        hdr.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        table.getTableHeader().setDefaultRenderer(hdr);

        var zebraRenderer = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    javax.swing.JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                var c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(javax.swing.SwingConstants.CENTER); // mantiene centrado
                if (!isSelected) {
                    c.setBackground((row % 2 == 0) ? java.awt.Color.WHITE : new java.awt.Color(245, 245, 245));
                }
                setToolTipText(value == null ? null : String.valueOf(value)); 
                return c;
            }
        };
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(zebraRenderer);
        }
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        var sp = new JScrollPane(table);
        sp.setBorder(new EmptyBorder(0, 16, 16, 16));
        root.add(sp, BorderLayout.CENTER);
        return root;
    }

    private JComponent buildFilters() {
        var p = new JPanel(new GridBagLayout());
        p.setBorder(new EmptyBorder(8, 16, 8, 16));
        var c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        int col = 0;

        c.gridx = col++; c.gridy = 0; c.weightx = 0;
        p.add(new JLabel("Género"), c);
        c.gridx = col++; c.weightx = 0.4;
        cbGenero.setPreferredSize(new Dimension(180, 28));
        p.add(cbGenero, c);

        c.gridx = col++; c.weightx = 0;
        p.add(new JLabel("Desde (año)"), c);
        c.gridx = col++; c.weightx = 0.2;
        ((JSpinner.DefaultEditor) spDesde.getEditor()).getTextField().setColumns(4);
        p.add(spDesde, c);

        c.gridx = col++; c.weightx = 0;
        p.add(new JLabel("Hasta (año)"), c);
        c.gridx = col++; c.weightx = 0.2;
        ((JSpinner.DefaultEditor) spHasta.getEditor()).getTextField().setColumns(4);
        p.add(spHasta, c);

        var actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.add(btnLimpiar);
        actions.add(btnBuscar);

        c.gridx = col++; c.weightx = 1.0; 
        p.add(new JPanel(), c);

        c.gridx = col; c.weightx = 0;
        p.add(actions, c);

        return p;
    }

    private JComponent buildFooter() {
        var south = new JPanel(new BorderLayout());
        south.setBorder(new EmptyBorder(6, 16, 12, 16));
        var statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        statusPanel.add(lblEstado);

        var buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.add(btnCerrar);

        south.add(statusPanel, BorderLayout.WEST);
        south.add(buttons, BorderLayout.EAST);
        return south;
    }

    private void cargarTodas() {
        try {
            List<Pelicula> data = service.listarTodas();
            setRows(data);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al cargar el listado: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onBuscar() {
        try {
            String genero = (String) cbGenero.getSelectedItem();
            Integer desde = (Integer) spDesde.getValue();
            Integer hasta = (Integer) spHasta.getValue();
            List<Pelicula> data = service.listarPorFiltros(genero, desde, hasta);
            setRows(data);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al aplicar filtros: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onLimpiar() {
        cbGenero.setSelectedIndex(0); // TODOS
        spDesde.setValue(1900);
        spHasta.setValue(2100);
        cargarTodas();
    }

    private void setRows(List<Pelicula> data) {
        model.setRowCount(0);
        if (data != null) {
            for (Pelicula p : data) {
                model.addRow(toRow(p));
            }
        }
        lblEstado.setText((data == null ? 0 : data.size()) + " registros.");
        // Ajuste de ancho de columnas
        if (table.getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setPreferredWidth(60);  // ID
            table.getColumnModel().getColumn(1).setPreferredWidth(260); // TITULO
            table.getColumnModel().getColumn(2).setPreferredWidth(200); // DIRECTOR
            table.getColumnModel().getColumn(3).setPreferredWidth(70);  // AÑO
            table.getColumnModel().getColumn(4).setPreferredWidth(110); // DURACION
            table.getColumnModel().getColumn(5).setPreferredWidth(120); // GENERO

            table.getColumnModel().getColumn(0).setPreferredWidth(70);   // ID
            table.getColumnModel().getColumn(1).setPreferredWidth(300);  // TITULO
            table.getColumnModel().getColumn(2).setPreferredWidth(220);  // DIRECTOR
            table.getColumnModel().getColumn(3).setPreferredWidth(80);   // AÑO
            table.getColumnModel().getColumn(4).setPreferredWidth(120);  // DURACION
            table.getColumnModel().getColumn(5).setPreferredWidth(120);  // GENERO
        }
    }

    private Object[] toRow(Pelicula p) {
        return new Object[]{
                (p.getId() == null ? null : p.getId().intValue()),
                safe(p.getTitulo()),
                safe(p.getDirector()),
                p.getAnio(),
                p.getDuracionMin(),
                safe(p.getGenero())
        };
    }

    private String safe(String s) {
        return (s == null) ? "" : s;
    }
}
