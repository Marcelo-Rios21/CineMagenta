package cine.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import cine.ui.PeliculaForm;

public class MainWindow extends JFrame{
    public MainWindow() {
        super("Cine Magenta - Cartelera");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 560);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        aplicarFuenteBase();

        // ====== TOOLBAR ======
        var toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230,230,230)),
                new EmptyBorder(8, 12, 8, 12)
        ));

        var btnAgregar = crearBotonToolbar("➕ Agregar (Ctrl+N)", "Agregar una nueva pelicula");
        btnAgregar.addActionListener(e -> new PeliculaForm(this).setVisible(true));
        btnAgregar.setMnemonic('A');

        var im = toolbar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        var am = toolbar.getActionMap();
        im.put(KeyStroke.getKeyStroke("control N"), "new");
        am.put("new", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                btnAgregar.doClick();
            }
        });

        toolbar.add(btnAgregar);
        toolbar.addSeparator();

        add(toolbar, BorderLayout.NORTH);

        // ====== HEADER ======
        var header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(20, 24, 10, 24));
        header.setBackground(Color.WHITE);

        var lblTitulo = new JLabel("Cartelera – Modulo de Altas");
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 22f));
        var lblSub = new JLabel("Registra peliculas con validaciones. Edicion y listados llegaran en las siguientes etapas.");
        lblSub.setForeground(new Color(90, 90, 90));
        
        header.add(lblTitulo, BorderLayout.NORTH);
        header.add(lblSub, BorderLayout.CENTER);

        add(header, BorderLayout.PAGE_START);

        // ====== CONTENIDO (placeholder) ======
        var contenido = new JPanel(new GridBagLayout());
        contenido.setBorder(new EmptyBorder(30, 24, 24, 24));
        var msg = new JLabel("Usa el botón “➕ Agregar (Ctrl+N)” para registrar una pelicula.");
        msg.setFont(msg.getFont().deriveFont(Font.PLAIN, 16f));
        msg.setForeground(new Color(70, 70, 70));
        contenido.add(msg, new GridBagConstraints());
        add(contenido, BorderLayout.CENTER);

        // ====== STATUS BAR ======
        var status = new JPanel(new BorderLayout());
        status.setBorder(new EmptyBorder(6, 12, 6, 12));
        var lblStatus = new JLabel("Listo.");
        lblStatus.setForeground(new Color(110,110,110));
        status.add(new JSeparator(), BorderLayout.NORTH);
        status.add(lblStatus, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);
    }

    private void aplicarFuenteBase() {
        var defaults = UIManager.getDefaults();
        var base = defaults.getFont("Label.font");
        if (base != null) {
            var nueva = base.deriveFont(14f);
            defaults.forEach((k, v) -> {
                if (v instanceof Font f) UIManager.put(k, f.deriveFont(f.getStyle(), 14f));
            });
        }
    }

    private JButton crearBotonToolbar(String texto, String tooltip) {
        var b = new JButton(texto);
        b.setToolTipText(tooltip);
        b.setFocusable(false);
        b.setMargin(new Insets(8, 12, 8, 12));
        return b;
    }
}
