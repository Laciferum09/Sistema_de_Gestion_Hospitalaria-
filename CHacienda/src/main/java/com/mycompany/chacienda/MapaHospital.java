/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/*
 * Sistema de Gestión Hospitalaria
 */
package com.mycompany.chacienda;



import javax.swing.*;
import java.awt.*;

/**
 * MapaHospital - Ventana Swing que dibuja el grafo del hospital.
 * Muestra nodos, flechas y resalta la ruta óptima.
 * Tiene botón para cerrar y panel de info lateral.
 */
public class MapaHospital extends JFrame {

    private final GrafoHospital grafo;
    private final String[]      rutaResaltada;
    private final String        infoRuta;

    private static final int RADIO = 42;
    private static final int MAPA_ANCHO = 820;
    private static final int ALTO       = 560;

    public MapaHospital(GrafoHospital grafo, String[] rutaResaltada, String infoRuta) {
        this.grafo         = grafo;
        this.rutaResaltada = rutaResaltada != null ? rutaResaltada : new String[0];
        this.infoRuta      = infoRuta != null ? infoRuta : "";

        setTitle("🏥 Mapa del Hospital");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        // Panel del mapa
        PanelMapa panelMapa = new PanelMapa();
        panelMapa.setPreferredSize(new Dimension(MAPA_ANCHO, ALTO));
        add(panelMapa, BorderLayout.CENTER);

        // Panel lateral derecho con info
        JPanel panelInfo = crearPanelInfo();
        add(panelInfo, BorderLayout.EAST);

        // Botón cerrar abajo
        JButton btnCerrar = new JButton("✖  Cerrar Mapa");
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 14));
        btnCerrar.setBackground(new Color(180, 50, 50));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.setPreferredSize(new Dimension(200, 42));
        btnCerrar.addActionListener(e -> dispose());

        JPanel panelBoton = new JPanel();
        panelBoton.setBackground(new Color(18, 28, 48));
        panelBoton.add(btnCerrar);
        add(panelBoton, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel crearPanelInfo() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(220, ALTO));
        panel.setBackground(new Color(25, 40, 70));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 12, 20, 12));

        // Título panel
        JLabel titulo = new JLabel("Información");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setForeground(new Color(100, 180, 255));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(10));

        // Separador
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(60, 80, 130));
        sep.setMaximumSize(new Dimension(200, 2));
        panel.add(sep);
        panel.add(Box.createVerticalStrut(12));

        if (rutaResaltada.length > 0) {
            // Mostrar ruta
            JLabel lblRuta = new JLabel("Ruta óptima:");
            lblRuta.setFont(new Font("Arial", Font.BOLD, 13));
            lblRuta.setForeground(new Color(255, 200, 80));
            lblRuta.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(lblRuta);
            panel.add(Box.createVerticalStrut(8));

            for (int i = 0; i < rutaResaltada.length; i++) {
                String prefijo = (i == 0) ? "🟢 " : (i == rutaResaltada.length - 1) ? "🔴 " : "🟠 ";
                JLabel nodo = new JLabel("<html>" + prefijo + rutaResaltada[i] + "</html>");
                nodo.setFont(new Font("Arial", Font.PLAIN, 12));
                nodo.setForeground(Color.WHITE);
                nodo.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(nodo);
                if (i < rutaResaltada.length - 1) {
                    JLabel flecha = new JLabel("      ↓");
                    flecha.setFont(new Font("Arial", Font.PLAIN, 11));
                    flecha.setForeground(new Color(255, 160, 30));
                    flecha.setAlignmentX(Component.LEFT_ALIGNMENT);
                    panel.add(flecha);
                }
            }

            panel.add(Box.createVerticalStrut(16));
            JSeparator sep2 = new JSeparator();
            sep2.setForeground(new Color(60, 80, 130));
            sep2.setMaximumSize(new Dimension(200, 2));
            panel.add(sep2);
            panel.add(Box.createVerticalStrut(10));

            // Distancia total
            if (!infoRuta.isEmpty()) {
                JLabel lblDist = new JLabel("<html><b>Distancia total:</b></html>");
                lblDist.setFont(new Font("Arial", Font.PLAIN, 12));
                lblDist.setForeground(new Color(180, 220, 255));
                lblDist.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(lblDist);

                // Extraer número de distancia del texto
                String dist = infoRuta.contains("Distancia total:")
                    ? infoRuta.substring(infoRuta.indexOf("Distancia total:") + 16).trim()
                    : "";
                JLabel lblNum = new JLabel(dist);
                lblNum.setFont(new Font("Arial", Font.BOLD, 14));
                lblNum.setForeground(new Color(100, 255, 160));
                lblNum.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(lblNum);
            }
        } else {
            // Solo mostrar áreas
            JLabel lblAreas = new JLabel("Áreas del hospital:");
            lblAreas.setFont(new Font("Arial", Font.BOLD, 13));
            lblAreas.setForeground(new Color(100, 180, 255));
            lblAreas.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(lblAreas);
            panel.add(Box.createVerticalStrut(8));

            for (int i = 0; i < grafo.getNumAreas(); i++) {
                JLabel lbl = new JLabel("• " + grafo.getArea(i));
                lbl.setFont(new Font("Arial", Font.PLAIN, 12));
                lbl.setForeground(Color.WHITE);
                lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(lbl);
                panel.add(Box.createVerticalStrut(3));
            }
        }

        // Leyenda abajo
        panel.add(Box.createVerticalGlue());
        JSeparator sep3 = new JSeparator();
        sep3.setForeground(new Color(60, 80, 130));
        sep3.setMaximumSize(new Dimension(200, 2));
        panel.add(sep3);
        panel.add(Box.createVerticalStrut(10));

        JLabel leyTitulo = new JLabel("Leyenda:");
        leyTitulo.setFont(new Font("Arial", Font.BOLD, 12));
        leyTitulo.setForeground(new Color(160, 180, 220));
        leyTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(leyTitulo);
        panel.add(Box.createVerticalStrut(5));

        agregarLeyenda(panel, new Color(40, 80, 160),       "Área normal");
        agregarLeyenda(panel, new Color(255, 140, 20),      "En ruta óptima");
        agregarLeyenda(panel, new Color(50, 200, 100),      "Origen / Destino");

        return panel;
    }

    private void agregarLeyenda(JPanel panel, Color color, String texto) {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        fila.setBackground(new Color(25, 40, 70));
        fila.setMaximumSize(new Dimension(200, 20));

        JPanel circulo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(color);
                g.fillOval(0, 2, 12, 12);
            }
        };
        circulo.setPreferredSize(new Dimension(14, 16));
        circulo.setBackground(new Color(25, 40, 70));

        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));
        lbl.setForeground(Color.WHITE);

        fila.add(circulo);
        fila.add(lbl);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(fila);
        panel.add(Box.createVerticalStrut(3));
    }

    // -------------------------------------------------------
    // PANEL DE DIBUJO DEL GRAFO
    // -------------------------------------------------------
    private class PanelMapa extends JPanel {

        public PanelMapa() {
            setBackground(new Color(18, 28, 48));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int num = grafo.getNumAreas();
            if (num == 0) {
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 18));
                g2.drawString("No hay áreas registradas.", 220, 260);
                return;
            }

            // Título
            g2.setColor(new Color(100, 180, 255));
            g2.setFont(new Font("Arial", Font.BOLD, 22));
            g2.drawString("Mapa del Hospital", MAPA_ANCHO / 2 - 105, 45);

            // Aristas primero
            dibujarAristas(g2, num);
            // Nodos encima
            dibujarNodos(g2, num);
        }

        private void dibujarAristas(Graphics2D g2, int num) {
            for (int i = 0; i < num; i++) {
                for (int j = 0; j < num; j++) {
                    int peso = grafo.getPeso(i, j);
                    if (peso <= 0 || peso >= 999) continue;

                    int[] pi = getPosicion(i, num);
                    int[] pj = getPosicion(j, num);

                    boolean enRuta = sonConsecutivosEnRuta(grafo.getArea(i), grafo.getArea(j));

                    if (enRuta) {
                        g2.setColor(new Color(255, 160, 30));
                        g2.setStroke(new BasicStroke(3.5f));
                    } else {
                        g2.setColor(new Color(70, 100, 170));
                        g2.setStroke(new BasicStroke(1.5f));
                    }

                    dibujarFlecha(g2, pi[0], pi[1], pj[0], pj[1]);

                    // Peso
                    int mx = (pi[0] + pj[0]) / 2;
                    int my = (pi[1] + pj[1]) / 2;
                    g2.setFont(new Font("Arial", Font.BOLD, 11));
                    g2.setColor(enRuta ? new Color(255, 230, 100) : new Color(150, 180, 240));
                    g2.drawString(String.valueOf(peso), mx + 4, my - 4);
                }
            }
        }

        private void dibujarNodos(Graphics2D g2, int num) {
            for (int i = 0; i < num; i++) {
                int[]   pos  = getPosicion(i, num);
                String  area = grafo.getArea(i);
                boolean enRuta    = estaEnRuta(area);
                boolean esExtremo = esOrigenODestino(area);

                // Sombra
                g2.setColor(new Color(0, 0, 0, 90));
                g2.fillOval(pos[0] - RADIO + 5, pos[1] - RADIO + 5, RADIO * 2, RADIO * 2);

                // Relleno
                Color color = esExtremo ? new Color(50, 200, 100)
                            : enRuta    ? new Color(255, 140, 20)
                            :             new Color(40, 80, 160);
                g2.setColor(color);
                g2.fillOval(pos[0] - RADIO, pos[1] - RADIO, RADIO * 2, RADIO * 2);

                // Borde
                g2.setColor(esExtremo ? new Color(150, 255, 180)
                           : enRuta   ? new Color(255, 210, 100)
                           :            new Color(100, 140, 220));
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawOval(pos[0] - RADIO, pos[1] - RADIO, RADIO * 2, RADIO * 2);

                // Texto
                g2.setFont(new Font("Arial", Font.BOLD, 11));
                g2.setColor(Color.WHITE);
                String[] partes = partirTexto(area);
                FontMetrics fm = g2.getFontMetrics();
                if (partes.length == 1) {
                    int tw = fm.stringWidth(partes[0]);
                    g2.drawString(partes[0], pos[0] - tw / 2, pos[1] + 4);
                } else {
                    int tw1 = fm.stringWidth(partes[0]);
                    int tw2 = fm.stringWidth(partes[1]);
                    g2.drawString(partes[0], pos[0] - tw1 / 2, pos[1] - 4);
                    g2.drawString(partes[1], pos[0] - tw2 / 2, pos[1] + 11);
                }
            }
        }

        private void dibujarFlecha(Graphics2D g2, int x1, int y1, int x2, int y2) {
            double dx   = x2 - x1, dy = y2 - y1;
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist == 0) return;
            double ux = dx / dist, uy = dy / dist;

            int sx = (int)(x1 + ux * RADIO);
            int sy = (int)(y1 + uy * RADIO);
            int ex = (int)(x2 - ux * RADIO);
            int ey = (int)(y2 - uy * RADIO);

            g2.drawLine(sx, sy, ex, ey);

            double ang = Math.atan2(ey - sy, ex - sx);
            int tam = 11;
            int[] xp = {ex,
                (int)(ex - tam * Math.cos(ang - 0.45)),
                (int)(ex - tam * Math.cos(ang + 0.45))};
            int[] yp = {ey,
                (int)(ey - tam * Math.sin(ang - 0.45)),
                (int)(ey - tam * Math.sin(ang + 0.45))};
            g2.fillPolygon(xp, yp, 3);
        }

        private int[] getPosicion(int i, int num) {
            int x = grafo.getPosX(i);
            int y = grafo.getPosY(i);
            if (x > 0 && y > 0) return new int[]{x, y}; // posición manual guardada
            // Fallback: distribuir en círculo automáticamente
            double ang = 2 * Math.PI * i / num;
            return new int[]{
                MAPA_ANCHO / 2 + (int)(260 * Math.cos(ang)),
                ALTO       / 2 + (int)(180 * Math.sin(ang))
            };
        }

        private String[] partirTexto(String t) {
            if (t.length() <= 9) return new String[]{t};
            int esp = t.indexOf(' ');
            if (esp == -1) return new String[]{t};
            return new String[]{t.substring(0, esp), t.substring(esp + 1)};
        }

        private boolean estaEnRuta(String area) {
            for (String r : rutaResaltada)
                if (r.equalsIgnoreCase(area)) return true;
            return false;
        }

        private boolean esOrigenODestino(String area) {
            if (rutaResaltada.length == 0) return false;
            return area.equalsIgnoreCase(rutaResaltada[0])
                || area.equalsIgnoreCase(rutaResaltada[rutaResaltada.length - 1]);
        }

        private boolean sonConsecutivosEnRuta(String a, String b) {
            for (int i = 0; i < rutaResaltada.length - 1; i++)
                if (rutaResaltada[i].equalsIgnoreCase(a)
                 && rutaResaltada[i+1].equalsIgnoreCase(b)) return true;
            return false;
        }
    }
}
