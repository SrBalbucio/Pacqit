package balbucio.pacqit.dependencies;

import balbucio.pacqit.Main;
import balbucio.pacqit.model.dependency.Dependency;
import balbucio.pacqit.model.dependency.DependencyReceiver;
import balbucio.pacqit.page.DependencySearchPage;
import balbucio.sqlapi.sqlite.HikariSQLiteInstance;
import balbucio.sqlapi.sqlite.SqliteConfig;
import de.milchreis.uibooster.model.ListElement;

import java.io.File;
import java.util.concurrent.CopyOnWriteArrayList;

public class DependencyManager {
    private File dbFile = new File(System.getenv("APPDATA")+"/Pacqit", "dependencies.db");
    private HikariSQLiteInstance instance;
    private Main app;

    public DependencyManager(Main app) {
        this.app = app;
        SqliteConfig config = new SqliteConfig(dbFile);
        config.createFile();
        instance = new HikariSQLiteInstance(config);
        instance.createTable("registeredDependencies", "");
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
