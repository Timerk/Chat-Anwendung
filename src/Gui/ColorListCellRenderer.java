package Gui;

import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import java.awt.Color;
import java.awt.Component;


public class ColorListCellRenderer extends DefaultListCellRenderer {
    private Map<Integer, Color> colorMap = new HashMap<>();

    // Methode zum Festlegen der Schriftfarbe für ein bestimmtes Element
    public void setItemColor(int index, Color color) {
        colorMap.put(index, color);
    }

    // Methode zum Zurücksetzen der Schriftfarbe für ein bestimmtes Element auf die Standardfarbe
    public void resetItemColor(int index) {
        colorMap.remove(index);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        // Überprüfen ob eine benutzerdefinierte Farbe für das aktuelle Element festgelegt wurde
        if (colorMap.containsKey(index)) {
            setForeground(colorMap.get(index));
        } else {
            // Wenn keine benutzerdefinierte Farbe festgelegt ist wird Standardfarbe verwenden
            setForeground(list.getForeground());
        }

        return this;
    }
}
