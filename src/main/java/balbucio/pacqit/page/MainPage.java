package balbucio.pacqit.page;

import balbucio.org.ejsl.component.JImage;
import balbucio.org.ejsl.utils.ImageUtils;
import balbucio.pacqit.Main;
import balbucio.pacqit.model.project.Project;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainPage extends JFrame {

    private Main app;

    public MainPage(Main app){
        super("Pacqit");
        this.app = app;
        app.getSettings().setThemeInApp(false);
        this.project = app.getProject();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setIconImage(ImageUtils.getImage(this.getClass().getResourceAsStream("/pacqit.png")));
        this.setSize(880, 480);
        this.setLayout(new BorderLayout());
        this.add(infoPanel(), BorderLayout.WEST);
        this.add(optionPanel(), BorderLayout.CENTER);
        this.setVisible(true);
    }

    private Project project;

    public JPanel infoPanel(){
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(15, 15, 15, 0));
        panel.setPreferredSize(new Dimension(180, 120));
        BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(layout);
        JPanel imag = new JPanel(new GridBagLayout());
        JImage image = new JImage(ImageUtils.getImage(this.getClass().getResourceAsStream("/pacqit.png")));
        image.setPreferredSize(new Dimension(100, 100));
        image.setCenter(true);
        imag.add(image);
        JLabel name = new JLabel("Project Name: "+project.getName());
        JLabel pckg = new JLabel("Project Package: "+project.getProjectPackage());
        JLabel version = new JLabel("Project Version: "+project.getVersion());
        JLabel javaTarget = new JLabel("Java Version: "+project.getJavaVersion());
        panel.add(imag);
        panel.add(name);
        panel.add(pckg);
        panel.add(version);
        panel.add(javaTarget);
        return panel;
    }

    public JScrollPane optionPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(15, 15,15 ,5));
        BoxLayout layout = new BoxLayout(mainPanel, BoxLayout.Y_AXIS);
        mainPanel.setLayout(layout);
        JPanel label = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel l = new JLabel("Project Options:");
        l.setFont(l.getFont().deriveFont(Font.BOLD, 16));
        label.add(l);
        mainPanel.add(label);
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        JButton build = new JButton("Build");
        build.addActionListener(e -> app.getProjectBuild().buildProject(true));
        JButton buildAndRun = new JButton("Build and run");
        buildAndRun.addActionListener(e -> {
            boolean pb = app.getProjectBuild().buildProject(true);
            if(pb){
                app.getProjectBuild().run(true);
            }
        });
        JButton buildAndObfuscate = new JButton("Build and obfuscate (beta)");
        buildAndObfuscate.addActionListener(e -> {
            boolean pb = app.getProjectBuild().buildProject(true);
            if(pb){
                app.getProjectBuild().createObsfucator().build(true);
            }
        });
        JButton run = new JButton("Run");
        run.addActionListener(e -> app.getProjectBuild().run(true));
        JButton obfuscate = new JButton("Obfuscate (beta)");
        obfuscate.addActionListener(e -> app.getProjectBuild().createObsfucator().build(true));
        JButton clean = new JButton("Clean");
        clean.addActionListener(e -> app.getProjectBuild().clean());
        JButton asm = new JButton("Convert to asm and build (alpha)");
        JButton openSettings = new JButton("Open project settings");
        openSettings.addActionListener(e -> app.projectSettingsForm());
        JButton dependencies = new JButton("Open dependency manager");
        dependencies.addActionListener(e -> app.openDependenciesForm());
        JButton module = new JButton("Open module manager");
        module.addActionListener(e -> app.moduleManagerForm());
        JButton implementer = new JButton("Open implementer manager");
        implementer.addActionListener(e -> app.implementerManagerForm());
        JButton openInAurora = new JButton("Open in Aurora");
        panel.add(build);
        panel.add(buildAndRun);
        panel.add(buildAndObfuscate);
        panel.add(run);
        panel.add(obfuscate);
        panel.add(clean);
        panel.add(asm);
        panel.add(openSettings);
        panel.add(dependencies);
        panel.add(module);
        panel.add(implementer);
        panel.add(openInAurora);
        mainPanel.add(panel);
        JPanel label2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel l2 = new JLabel("Pacqit Settings:");
        l2.setFont(l2.getFont().deriveFont(Font.BOLD, 16));
        label2.add(l2);
        mainPanel.add(label2);
        JPanel pacqit = new JPanel(new GridLayout(5, 2, 10, 10));
        JButton repo = new JButton("Manage Repositories");
        JButton settings = new JButton("Open settings");
        JButton folder = new JButton("Open Pacqit folder");
        JButton resolve = new JButton("Resolve crashes and dependencies");
        JButton theme = new JButton("Change theme");
        theme.addActionListener(e -> {
            app.openThemeForm();
            this.revalidate();
            this.repaint();
        });
        JButton github = new JButton("Open in GitHub");
        JButton close = new JButton("Close");
        close.addActionListener(e -> System.exit(0));
        pacqit.add(repo);
        pacqit.add(settings);
        pacqit.add(folder);
        pacqit.add(resolve);
        pacqit.add(theme);
        pacqit.add(github);
        pacqit.add(close);
        mainPanel.add(pacqit);
        JScrollPane pane = new JScrollPane(mainPanel);
        pane.setBorder(new EmptyBorder(0,0,0,0));
        return pane;
    }
}
