package balbucio.pacqitapp.page;

import balbucio.org.ejsl.utils.ImageUtils;
import balbucio.pacqitapp.dependencies.DependencyManager;
import balbucio.pacqitapp.model.dependency.DependencyReceiver;
import balbucio.pacqitapp.swing.ListDialog;
import balbucio.paginatedlist.PaginatedList;
import de.milchreis.uibooster.model.ListElement;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class DependencySearchPage extends JFrame implements ActionListener, DocumentListener {

    private DependencyReceiver receiver;
    private DependencyManager manager;

    public DependencySearchPage(DependencyManager manager, DependencyReceiver receiver){
        this.manager = manager;
        this.receiver = receiver;
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setIconImage(ImageUtils.getImage(this.getClass().getResourceAsStream("/pacqit.png")));
        this.setSize(380, 640);
        this.setLayout(new BorderLayout());
        this.add(centerPanel(), BorderLayout.CENTER);
        this.setVisible(true);
    }

    private PaginatedList<ListElement> pages;
    private JList<ListElement> list;
    private JTextField bar;
    private JPanel buttons;
    private JButton previous;
    private JLabel pageInfo;
    private JButton next;
    private int page = 1;

    public JPanel centerPanel() {
        JPanel main = new JPanel();
        BoxLayout layout = new BoxLayout(main, BoxLayout.Y_AXIS);
        main.setLayout(layout);
        JLabel label1 = new JLabel("Search by dependency:");
        JLabel label2 = new JLabel("Here are the dependencies that Pacqit was able to index on your computer and the internet. If you don't find what you want, you can add it manually.");
        main.add(label1);
        main.add(label2);
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bar = new JTextField();
        bar.getDocument().addDocumentListener(this);
        JButton search = new JButton("Search");
        search.addActionListener(this);
        searchBar.add(bar);
        searchBar.add(search);
        main.add(searchBar);
        List<ListElement> element = new ArrayList<>();
       manager.mostUsedDependencies().forEach(dependency -> {
            StringBuilder description = new StringBuilder();
            description.append("Indexed or created in: " + dependency.getToolName() + "\n");
            description.append("Language: " + dependency.getLanguage());
            description.append("Package: " + dependency.getPackage() + "\n");
            description.append("Version: " + dependency.getVersion() + "\n");
            description.append("Number of uses: " + dependency.getUses());
            element.add(new ListElement(dependency.getName() + " - " + dependency.getVersion(), description.toString(), dependency.getLogo()));
        });

        pages = new PaginatedList<>(element, 50);
        list = ListDialog.createList(null, pages.getPage(page));
        JScrollPane pane = new JScrollPane(list);
        buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 10));
        previous = new JButton("<");
        previous.setVisible(false);
        previous.addActionListener(e -> {
            page--;
            switchPage();
        });
        buttons.add(previous);
        pageInfo = new JLabel("page %d of %s".replace("%d", String.valueOf(page)).replace("%s", String.valueOf(pages.getMaxPages())));
        buttons.add(pageInfo);
        next = new JButton(">");
        if (pages.hasNext(page)) {
            next.setVisible(true);
        }
        next.addActionListener(e -> {
            page++;
            switchPage();
        });
        buttons.add(next);
        main.add(buttons);
        main.add(pane);
        return main;
    }

    public void switchPage(){
        if(page != 1){
            previous.setVisible(true);
        }
        if(pages.hasNext(page)){
            next.setVisible(true);
        }
        pageInfo.setText("page %d of %s".replace("%d", String.valueOf(page)).replace("%s", String.valueOf(pages.getMaxPages())));
        list.setListData(new Vector<>(pages.getPage(page)));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        List<ListElement> element = new ArrayList<>();
        manager.getDependencies(bar.getText()).forEach( dependency -> {
            StringBuilder description = new StringBuilder();
            description.append("Indexed or created in: "+dependency.getToolName()+"\n");
            description.append("Language: "+dependency.getLanguage());
            description.append("Package: "+dependency.getPackage()+"\n");
            description.append("Version: "+dependency.getVersion()+"\n");
            description.append("Number of uses: "+dependency.getUses());
            element.add(new ListElement(dependency.getName()+" - "+dependency.getVersion(), description.toString(), dependency.getLogo()));
        });

        pages = new PaginatedList<>(element, 50);
        page = 1;
        if(page != 1){
            previous.setVisible(true);
        }
        if(pages.hasNext(page)){
            next.setVisible(true);
        }
        pageInfo.setText("page %d of %s".replace("%d", String.valueOf(page)).replace("%s", String.valueOf(pages.getMaxPages())));
        list.setListData(new Vector<>(pages.getPage(page)));
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        actionPerformed(null);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        actionPerformed(null);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        actionPerformed(null);
    }
}
