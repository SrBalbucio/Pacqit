package balbucio.pacqit.page;

import balbucio.org.ejsl.utils.ImageUtils;
import balbucio.pacqit.Main;
import balbucio.pacqit.model.dependency.Dependency;
import balbucio.pacqit.model.dependency.DependencyReceiver;
import balbucio.paginatedlist.PaginatedList;
import de.milchreis.uibooster.components.ListDialog;
import de.milchreis.uibooster.model.ListElement;
import de.milchreis.uibooster.model.SelectElementListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class DependencySearchPage extends JFrame {

    private Main app;
    private DependencyReceiver receiver;

    public DependencySearchPage(Main app, DependencyReceiver receiver){
        this.app = app;
        this.receiver = receiver;
        app.getSettings().setThemeInApp(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setIconImage(ImageUtils.getImage(this.getClass().getResourceAsStream("/pacqit.png")));
        this.setSize(380, 640);
        this.setLayout(new BorderLayout());
        this.setVisible(true);
    }

    private PaginatedList<ListElement> pages;
    private int page = 1;

    public JPanel centerPanel(){
        JPanel main = new JPanel();
        BoxLayout layout = new BoxLayout(main, BoxLayout.Y_AXIS);
        main.setLayout(layout);
        JLabel label1 = new JLabel("Search by dependency:");
        JLabel label2 = new JLabel("Here are the dependencies that Pacqit was able to index on your computer and the internet. If you don't find what you want, you can add it manually.");
        main.add(label1);
        main.add(label2);
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JTextField bar = new JTextField();
        JButton search = new JButton("Search");
        searchBar.add(bar);
        searchBar.add(search);
        main.add(searchBar);
        List<Dependency> mostUsedDependencies = app.getDependencyManager().mostUsedDependencies();
        List<ListElement> element = new ArrayList<>();
        mostUsedDependencies.forEach( dependency -> {
            StringBuilder description = new StringBuilder();
            description.append("Indexed or created in: "+dependency.getToolName()+"\n");
            description.append("Language: "+dependency.getLanguage());
            description.append("Package: "+dependency.getPackage()+"\n");
            description.append("Version: "+dependency.getVersion()+"\n");
            description.append("Number of uses: "+dependency.getUses());
            element.add(new ListElement(dependency.getName()+" - "+dependency.getVersion(), description.toString(), dependency.getLogo()));
        });

        pages = new PaginatedList<>(element, 50);
        JList<ListElement> list = createList(null, pages.getPage(page));
        JScrollPane pane = new JScrollPane(list);
        main.add(pane);
        if(pages != null) {
            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 10));
            JButton previous = new JButton("<");
            JLabel pageInfo = new JLabel("page %d of %s");
            JButton next = new JButton(">");
            main.add(pane);
        }
        return main;
    }

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
