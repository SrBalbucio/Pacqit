package balbucio.pacqitapp.dependencies.xmlhandler;

import balbucio.pacqitapp.model.dependency.MavenDependency;
import lombok.Getter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RepositoryPomHandler extends DefaultHandler {

    private StringBuilder elementValue;
    @Getter
    private MavenDependency dependency;

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (elementValue == null) {
            elementValue = new StringBuilder();
        } else {
            elementValue.append(ch, start, length);
        }
    }

    private String step = "";

    @Override
    public void startElement(String uri, String lName, String qName, Attributes attr) throws SAXException {
        step = qName;

        switch (qName) {
            case "groupId" -> dependency.setPckg(elementValue.toString().replace("\n", "").replace(" ", ""));
            case "artifactId" -> dependency.setArtifact(elementValue.toString().replace("\n", "").replace(" ", ""));
            case "version" -> dependency.setVersion(elementValue.toString().replace("\n", "").replace(" ", ""));
            case "project" -> {
                this.dependency = new MavenDependency();
            }
            case "dependency" -> otherDependecy = new MavenDependency();
        }
        elementValue = new StringBuilder();
    }

    private MavenDependency otherDependecy;

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if(step.equalsIgnoreCase("dependency")){
            switch (qName) {
                case "groupId" -> otherDependecy.setPckg(elementValue.toString());
                case "artifactId" -> otherDependecy.setArtifact(elementValue.toString());
                case "version" -> otherDependecy.setVersion(elementValue.toString());
            }
        }
    }

}
