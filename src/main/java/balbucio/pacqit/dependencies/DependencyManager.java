package balbucio.pacqit.dependencies;

import balbucio.pacqit.Main;
import balbucio.pacqit.model.dependency.Dependency;
import balbucio.pacqit.model.dependency.DependencyReceiver;
import balbucio.pacqit.model.dependency.GradleDependency;
import balbucio.pacqit.model.dependency.MavenDependency;
import balbucio.pacqit.page.DependencySearchPage;
import balbucio.sqlapi.model.ResultValue;
import balbucio.sqlapi.sqlite.HikariSQLiteInstance;
import balbucio.sqlapi.sqlite.SqliteConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DependencyManager {
    private File dbFile = new File(System.getenv("APPDATA")+"/Pacqit", "dependencies.db");
    private HikariSQLiteInstance database;
    private Main app;

    public DependencyManager(Main app) {
        this.app = app;
        SqliteConfig config = new SqliteConfig(dbFile);
        config.createFile();
        database = new HikariSQLiteInstance(config);
        config.setMaxRows(150);
        database.createTable("registeredDependencies", "type VARCHAR(255), name VARCHAR(255), package VARCHAR(255), version VARCHAR(255), uses BIGINT(MAX)");
    }

    public List<Dependency> mostUsedDependencies(){
        List<Dependency> dep = new ArrayList<>();
        List<ResultValue> values = database.getAllValuesOrderedBy("uses", "registeredDependencies");
        AtomicInteger i = new AtomicInteger(50);
        values.forEach(r -> {
            if(i.get() == 0){
                return;
            }
            String type = r.asString("type");
            if(type.equals("MAVEN")){
                dep.add(new MavenDependency(r.asString("package"), r.asString("name"), r.asString("version"), r.asLong("uses")));
            } else if(type.equals("GRADLE")){
                dep.add(new GradleDependency(r.asString("package"), r.asString("name"), r.asString("version"), r.asLong("uses")));
            } else if(type.equals("LOCAL")){

            }
            i.getAndDecrement();
        });
        return dep;
    }

    private DependencySearchPage searchPage;

    public void searchAndAddDependecy(DependencyReceiver receiver){
        if(searchPage != null){
            searchPage.requestFocus();
            return;
        }
        this.searchPage = new DependencySearchPage(app, receiver);
    }
}
