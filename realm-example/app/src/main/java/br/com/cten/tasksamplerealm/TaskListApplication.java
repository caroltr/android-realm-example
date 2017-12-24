package br.com.cten.tasksamplerealm;

import android.app.Application;

import br.com.cten.tasksamplerealm.model.Migration;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Carol on 21/12/2017.
 */

public class TaskListApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("tasky.realm")
                .schemaVersion(1)
                .migration(new Migration())
                // .deleteRealmIfMigrationNeeded() // delete data when there are any changes on schema
                .build();
        // Realm.deleteRealm(realmConfig); // To remove data, while in development
        Realm.setDefaultConfiguration(realmConfig);
    }
}
