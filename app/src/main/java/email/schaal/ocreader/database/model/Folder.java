/*
 * Copyright (C) 2015 Daniel Schaal <daniel@schaal.email>
 *
 * This file is part of OCReader.
 *
 * OCReader is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OCReader is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OCReader.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package email.schaal.ocreader.database.model;

import android.support.annotation.Nullable;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;

/**
 * RealmObject representing a Folder.
 */
public class Folder extends RealmObject implements TreeItem, Insertable {
    @PrimaryKey
    private long id;

    private String name;

    public Folder() {
    }

    public Folder(long id) {
        this.id = id;
    }

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    @Override
    public int getCount(Realm realm) {
        return realm.where(Feed.class).equalTo(Feed.FOLDER_ID, getId()).sum(Feed.UNREAD_COUNT).intValue();
    }

    @Override
    public List<Feed> getFeeds(Realm realm) {
        return realm.where(Feed.class).equalTo(Feed.FOLDER_ID, getId()).findAllSorted(Feed.NAME, Sort.ASCENDING);
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Folder)
            return ((Folder) obj).getId() == getId();
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Long.valueOf(getId()).hashCode();
    }

    @Override
    public void insert(Realm realm) {
        if(getName() != null)
            realm.insertOrUpdate(this);
    }

    @Override
    public void delete(Realm realm) {
        for(Feed feed: getFeeds(realm)) {
            feed.delete(realm);
        }
        deleteFromRealm();
    }

    @Nullable
    public static Folder get(Realm realm, long id) {
        return realm.where(Folder.class).equalTo(Folder.ID, id).findFirst();
    }
}