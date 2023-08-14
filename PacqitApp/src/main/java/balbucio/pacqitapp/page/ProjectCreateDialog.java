package balbucio.pacqitapp.page;

import com.github.cjwizard.PageFactory;
import com.github.cjwizard.WizardContainer;
import com.github.cjwizard.WizardPage;
import com.github.cjwizard.WizardSettings;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectCreateDialog extends JDialog {

    private PageFactory factory;
    private WizardContainer container;

    public ProjectCreateDialog(JFrame owner){
        super(owner, "Project creation wizard");
        factory = new CreateFactory();
        container = new WizardContainer(factory);
        this.getContentPane().add(container);
        this.pack();
        this.setVisible(true);
    }

    class CreateFactory implements PageFactory{

        private List<WizardPage> pages = new ArrayList<>();

        public CreateFactory(){
            pages.add(new WizardPage("Initial information", "Enter basic project information and proceed to the next step.") {
                {
                    JTextField name = new JTextField("");
                    add(new JLabel("Project Name: "));
                    add(name);
                }
            });
        }

        @Override
        public WizardPage createPage(List<WizardPage> list, WizardSettings wizardSettings) {

            return pages.get(list.size());
        }

        @Override
        public boolean isTransient(List<WizardPage> list, WizardSettings wizardSettings) {
            return false;
        }
    }
}
