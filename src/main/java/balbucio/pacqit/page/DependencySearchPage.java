package balbucio.pacqit.page;

import balbucio.org.ejsl.utils.ImageUtils;
import balbucio.pacqit.Main;
import balbucio.pacqit.model.dependency.DependencyReceiver;
import de.milchreis.uibooster.components.ListDialog;
import de.milchreis.uibooster.model.ListElement;

import javax.swing.*;
import java.awt.*;

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

    public JPanel centerPanel(){
        JPanel main = new JPanel();
        BoxLayout layout = new BoxLayout(main, BoxLayout.Y_AXIS);
        main.setLayout(layout);
        JLabel label1 = new JLabel("Search by dependency:");
        JLabel label2 = new JLabel("Here are the dependencies that Pacqit was able to index on your computer and the internet. If you don't find what you want, you can add it manually.");
        main.add(label1);
        main.add(label2);
        JList<ListElement> list = ListDialog.createList(null, new ListElement[]{});

        return main;
    }
}
