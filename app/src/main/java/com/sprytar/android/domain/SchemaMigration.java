package com.sprytar.android.domain;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class SchemaMigration implements RealmMigration{

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        //if (oldVersion == 0) {
        schema.create("Faq")
                .addField("id", int.class, FieldAttribute.PRIMARY_KEY)
                .addField("question", String.class)
                .addField("answer", String.class);

//        schema.create("LocationBoundary")
//                .addField("latitude", double.class)
//                .addField("longitude", double.class);

        schema.create("Location")
                .addField("id", int.class, FieldAttribute.PRIMARY_KEY)
                .addField("name", String.class);
                //.addRealmListField("venue_boundries", schema.get("LocationBoundary"));

//            oldVersion++;
//        }
//        if (oldVersion == 1) {
//
//            oldVersion++;
//        }

    }
}
