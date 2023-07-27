package balbucio.pacqitapp.dependencies;

import balbucio.pacqitapp.dependencies.logger.LoggerFormat;
import balbucio.pacqitapp.dependencies.utils.MavenUtils;
import balbucio.pacqitapp.dependencies.xmlhandler.RepositoryPomHandler;
import balbucio.pacqitapp.model.dependency.Dependency;
import balbucio.pacqitapp.model.dependency.DependencyReceiver;
import balbucio.pacqitapp.model.dependency.GradleDependency;
import balbucio.pacqitapp.model.dependency.MavenDependency;
import balbucio.pacqitapp.page.DependencySearchPage;
import balbucio.sqlapi.model.ConditionModifier;
import balbucio.sqlapi.model.ConditionValue;
import balbucio.sqlapi.model.ResultValue;
import balbucio.sqlapi.sqlite.HikariSQLiteInstance;
import balbucio.sqlapi.sqlite.SqliteConfig;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class DependencyManager {
    private File dbFile = new File(System.getenv("APPDATA") + "/Pacqit", "dependencies.db");
    private HikariSQLiteInstance database;
    private Logger LOGGER;

    public DependencyManager(){
        configureLogger();
        SqliteConfig config = new SqliteConfig(dbFile);
        config.createFile();
        database = new HikariSQLiteInstance(config);
        config.setMaxRows(150);
        database.createTable("registeredDependencies", "type VARCHAR(255), name VARCHAR(255), package VARCHAR(255), version VARCHAR(255), uses BIGINT");
    }

    private void configureLogger(){
        LOGGER = Logger.getLogger("DEPENDENCIES");
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        LoggerFormat format = new LoggerFormat();
        handler.setFormatter(format);
        LOGGER.addHandler(handler);
    }

    public void loadMavenDependencies(){
        String userHome = System.getProperty("user.home");
        String m2Repository = userHome + File.separator + ".m2" + File.separator + "repository";
        List<File> pomFiles = MavenUtils.findPomFiles(m2Repository);
        pomFiles.forEach(pom -> {
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser saxParser = factory.newSAXParser();
                RepositoryPomHandler handler = new RepositoryPomHandler();
                saxParser.parse(pom, handler);
                MavenDependency dependency = handler.getDependency();
                if(dependency != null) {
                    LOGGER.info("Nova dependência carregada: "+dependency.getName()+":"+dependency.getPackage()+":"+dependency.getVersion());
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    public List<Dependency> mostUsedDependencies() {
        List<Dependency> dep = new ArrayList<>();
        List<ResultValue> values = database.getAllValuesOrderedBy("uses", "registeredDependencies");
        AtomicInteger i = new AtomicInteger(50);
        values.forEach(r -> {
            if (i.get() == 0) {
                return;
            }
            dep.add(getDependencyFromResultValue(r));
            i.getAndDecrement();
        });
        return dep;
    }

    private ConditionValue[] searchdependency = new ConditionValue[]{
            new ConditionValue("name", ConditionValue.Conditional.EQUALS, "", ConditionValue.Operator.NULL),
            new ConditionValue("package", ConditionValue.Conditional.EQUALS, "", ConditionValue.Operator.OR),
            new ConditionValue("version", ConditionValue.Conditional.EQUALS, "", ConditionValue.Operator.OR)
    };

    public List<Dependency> getDependencies(String query) {
        List<Dependency> dep = new ArrayList<>();
        List<ResultValue> values = database.getAllValuesFromColumns("registeredDependencies", new ConditionModifier(searchdependency, new String(query), new String(query), new String(query)).done());
        values.forEach(r -> {
            dep.add(getDependencyFromResultValue(r));
        });
        return dep;
    }

    public Dependency getDependencyFromResultValue(ResultValue r) {
        String type = r.asString("type");
        if(type.equals("MAVEN")){
            return new MavenDependency(r.asString("package"), r.asString("name"), r.asString("version"), r.asLong("uses"));
        } else if(type.equals("GRADLE")){
            return new GradleDependency(r.asString("package"), r.asString("name"), r.asString("version"), r.asLong("uses"));
        } else if(type.equals("LOCAL")){
            return null;
        } else{
            return null;
        }
    }

    private DependencySearchPage searchPage;
    public void searchAndAddDependecy(DependencyReceiver receiver){
        if(searchPage != null){
            searchPage.requestFocus();
            return;
        }
        this.searchPage = new DependencySearchPage(this, receiver);
    }
}
