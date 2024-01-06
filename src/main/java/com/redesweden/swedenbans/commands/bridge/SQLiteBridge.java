package com.redesweden.swedenbans.commands.bridge;

import java.sql.SQLException;

import com.redesweden.swedenbans.SwedenBans;
import com.redesweden.swedenbans.database.Database;

public class SQLiteBridge implements Bridge
{
    private Database db;
    
    public SQLiteBridge(final Database db) {
        super();
        this.db = db;
    }
    
    public void export() throws SQLException {
        SwedenBans.instance.getDB().copyTo(this.db);
    }
    
    public void load() throws SQLException {
        this.db.copyTo(SwedenBans.instance.getDB());
    }
}
