package balbucio.pacqit.utils.swing;

import balbucio.pacqit.page.DependencySearchPage;
import de.milchreis.uibooster.model.ListElement;
import de.milchreis.uibooster.model.SelectElementListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class ListDialog {

    public static JList<ListElement> createList(SelectElementListener selectElementListener, List<ListElement> elements) {
        DefaultListModel<ListElement> listModel = createListModel(elements);
        JList<ListElement> list = new JList(listModel);
        if (selectElementListener != null) {
            list.addListSelectionListener((e) -> {
                if (e.getValueIsAdjusting()) {
                    selectElementListener.onSelected(elements.get(e.getFirstIndex()));
                }

            });
        }

        boolean hasAtLeasedOneIcon = elements.stream().anyMatch((e) -> {
            return e.getImage() != null;
        });
        list.setSelectionMode(0);
        list.setCellRenderer((list1, listElement, index, isSelected, cellHasFocus) -> {
            JPanel row = new JPanel(new BorderLayout());
            Box vBox = Box.createVerticalBox();
            vBox.setAlignmentY(0.0F);
            vBox.add(new JMultilineLabel(listElement.getTitle(), true));
            vBox.add(new JMultilineLabel(listElement.getMessage(), false));
            row.add(vBox, "Center");
            Image preview = listElement.getImage() != null ? listElement.getImage().getScaledInstance(80, 80, 2) : new BufferedImage(80, 80, 6);
            JLabel image = new JLabel(new ImageIcon((Image)preview));
            image.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 3));
            image.setVerticalAlignment(1);
            if (hasAtLeasedOneIcon) {
                row.add(image, "West");
            }

            if (isSelected) {
                row.setBackground(list.getSelectionBackground());
                row.setForeground(list.getSelectionForeground());
            } else {
                row.setBackground(list.getBackground());
                row.setForeground(list.getForeground());
            }

            row.setEnabled(list.isEnabled());
            row.setFont(list.getFont());
            row.setOpaque(true);
            row.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));
            return row;
        });
        return list;
    }

    public static DefaultListModel<ListElement> createListModel(List<ListElement> elements) {
        DefaultListModel<ListElement> listModel = new DefaultListModel();
        elements.forEach(listModel::addElement);
        return listModel;
    }

    public static class JMultilineLabel extends JLabel {
        private static final long serialVersionUID = 1L;

        public JMultilineLabel(String text, boolean bold) {
            if (text != null) {
                String prepared = "<html>" + text.replace("\r", "").replace("\n", "<br>") + "</html>";
                this.setText(prepared);
            }

            this.setCursor((Cursor)null);
            this.setOpaque(false);
            this.setFocusable(false);
            this.setFont(UIManager.getFont("Label.font"));
            if (!bold) {
                this.setFont(this.getFont().deriveFont(this.getFont().getStyle() & -2));
            }

            this.setBorder(new EmptyBorder(5, 5, 0, 5));
            this.setAlignmentY(0.5F);
        }
    }
}
