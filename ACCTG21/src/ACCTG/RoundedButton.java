package ACCTG;

import javax.swing.*;
import java.awt.*;

public class RoundedButton extends JButton {

    private boolean hover;

    public RoundedButton(String text) {
        super(text);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 13));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover animation
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                hover = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 🔹 Theme colors (match nav bar)
        Color base1 = new Color(0, 170, 170);
        Color base2 = new Color(0, 140, 140);
        Color hover1 = new Color(0, 190, 190);
        Color hover2 = new Color(0, 160, 160);

        // Gradient background (changes on hover)
        GradientPaint gradient = new GradientPaint(
                0, 0,
                hover ? hover1 : base1,
                0, getHeight(),
                hover ? hover2 : base2
        );

        // Rounded rectangle
        int arc = 12;
        g2.setPaint(gradient);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

        // Soft shadow / outline
        g2.setColor(new Color(0, 0, 0, 50));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

        // Draw text centered
        FontMetrics fm = g2.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(getText())) / 2;
        int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
        g2.setColor(getForeground());
        g2.drawString(getText(), textX, textY);

        g2.dispose();
    }
}
