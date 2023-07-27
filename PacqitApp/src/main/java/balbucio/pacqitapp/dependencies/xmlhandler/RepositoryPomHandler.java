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

    @Override
    public void startElement(String uri, String lName, String qName, Attributes attr) throws SAXException {
        if(qName.equalsIgnoreCase("project")){
            this.dependency = new MavenDependency();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if(localName.equalsIgnoreCase("project")) {
            switch (qName) {
                case "groupId" -> dependency.setPckg(elementValue.toString());
                case "artifactId" -> dependency.setArtifact(elementValue.toString());
                case "version" -> dependency.setVersion(elementValue.toString());
            }
        } else if(localName.equalsIgnoreCase("dependency")){

        }
    }

}
