package com.redesweden.swedenbans.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.redesweden.swedenbans.util.Util;
import org.bukkit.ChatColor;

public class DatabaseHelper
{
    public static void setup(final Database db) throws SQLException {
        createTables(db);
    }
    
    public static void createTables(final Database db) throws SQLException {
        if(!db.hasTable("punicoes")) {
            createPunicoesTable(db);
        }
        if (!db.hasTable("bans")) {
            createBanTable(db);
        }
        if (!db.hasTable("ipbans")) {
            createIPBanTable(db);
        }
        if (!db.hasTable("mutes")) {
            createMuteTable(db);
        }
        if (!db.hasColumn("mutes", "reason")) {
            try {
                db.getConnection().prepareStatement("ALTER TABLE mutes ADD COLUMN reason TEXT(100)").execute();
                System.out.println("Updating mutes table (Adding reason column)");
            }
            catch (SQLException ex) {}
        }
        if (!db.hasTable("iphistory")) {
            createIPHistoryTable(db);
        }
        if (!db.hasTable("warnings")) {
            createWarningsTable(db);
        }
        if (!db.hasTable("proxys")) {
            createProxysTable(db);
        }
        if (!db.hasTable("history")) {
            createHistoryTable(db);
        }
        if (!db.hasTable("rangebans")) {
            createRangeBansTable(db);
        }
        if (!db.hasTable("whitelist")) {
            createWhitelistTable(db);
        }
        if (!db.hasTable("players")) {
            createPlayersTable(db);
            final ResultSet rs = db.getConnection().prepareStatement("SELECT * FROM iphistory").executeQuery();
            final List<String> names = new ArrayList<String>();
            while (rs.next()) {
                names.add(rs.getString("name"));
            }
            rs.close();
            if (!names.isEmpty()) {
                System.out.println("Created players table. Now converting old player list. Size: " + names.size() + ", please wait :)");
                int n = 0;
                final PreparedStatement ps = db.getConnection().prepareStatement("INSERT INTO players (name, actual) VALUES (?, ?)");
                for (final String name : names) {
                    ++n;
                    ps.setString(1, name);
                    ps.setString(2, name);
                    ps.addBatch();
                    if (n % 100 == 0) {
                        final long start = System.currentTimeMillis();
                        ps.executeBatch();
                        final long end = System.currentTimeMillis();
                        final long remaining = (names.size() - n) / 100 * (end - start);
                        System.out.println(String.valueOf(n) + " records copied... Remaining: " + Util.getTime(remaining));
                    }
                }
                ps.executeBatch();
                rs.close();
            }
        }
        if (!db.hasColumn("warnings", "expires")) {
            try {
                db.getConnection().prepareStatement("ALTER TABLE warnings ADD expires long").execute();
            }
            catch (SQLException ex2) {}
        }
        if (!db.hasColumn("history", "name")) {
            try {
                db.getConnection().prepareStatement("ALTER TABLE history ADD banner TEXT(30)").execute();
                db.getConnection().prepareStatement("ALTER TABLE history ADD name TEXT(30)").execute();
                db.getConnection().prepareStatement("UPDATE history SET banner = 'unknown', name = 'unknown'").execute();
                System.out.println("History has no banner/name, adding them...");
            }
            catch (SQLException ex3) {}
        }
    }
    
    public static void createWhitelistTable(final Database db) {
        final String query = "CREATE TABLE whitelist (name TEXT(30) NOT NULL)";
        try {
            final Statement st = db.getConnection().createStatement();
            st.execute(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println(ChatColor.RED + "Could not create whitelist table.");
        }
    }
    
    public static void createRangeBansTable(final Database db) {
        final String query = "CREATE TABLE rangebans (banner TEXT(100) NOT NULL, reason TEXT(100), start TEXT(30), end TEXT(30), created BIGINT NOT NULL, expires BIGINT NOT NULL)";
        try {
            final Statement st = db.getConnection().createStatement();
            st.execute(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println(ChatColor.RED + "Could not create rangebans table.");
        }
    }
    
    public static void createHistoryTable(final Database db) {
        final String query = "CREATE TABLE history (created BIGINT NOT NULL, message TEXT(100));";
        try {
            final Statement st = db.getConnection().createStatement();
            st.execute(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println(ChatColor.RED + "Could not create history table.");
        }
    }
    
    public static void createPlayersTable(final Database db) {
        final String query = "CREATE TABLE players (name TEXT(30) NOT NULL, actual TEXT(30) NOT NULL);";
        try {
            final Statement st = db.getConnection().createStatement();
            st.execute(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println(ChatColor.RED + "Could not create players table.");
        }
    }

    public static void createPunicoesTable(final Database db) {
        final String query = "CREATE TABLE punicoes (id INT NOT NULL AUTO_INCREMENT, uniqueId VARCHAR(8), player VARCHAR(32) NOT NULL, staff VARCHAR(32) NOT NULL, tipo VARCHAR(64) NOT NULL, motivo VARCHAR(64) NOT NULL, provas VARCHAR(128), anunciado BOOLEAN NOT NULL DEFAULT 0, ativo BOOLEAN NOT NULL DEFAULT 1, PRIMARY KEY (id))";
        final String trigger = "CREATE TRIGGER before_insert_punicoes BEFORE INSERT ON punicoes FOR EACH ROW BEGIN SET NEW.uniqueId = UPPER(SUBSTRING(MD5(RAND()) FROM 1 FOR 8)); END;";
        try {
            final Statement st = db.getConnection().createStatement();
            st.execute(query);
            st.execute(trigger);
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println(ChatColor.RED + "Could not create players table.");
        }
    }
    
    public static void createBanTable(final Database db) {
        final String query = "CREATE TABLE bans ( name  TEXT(30) NOT NULL, reason  TEXT(100), banner  TEXT(30), time  BIGINT NOT NULL DEFAULT 0, expires  BIGINT NOT NULL DEFAULT 0 );";
        try {
            final Statement st = db.getConnection().createStatement();
            st.execute(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println(ChatColor.RED + "Could not create bans table.");
        }
    }
    
    public static void createProxysTable(final Database db) {
        final String query = "CREATE TABLE proxys (ip TEXT(30) NOT NULL, status TEXT(30), created BIGINT NOT NULL)";
        try {
            final Statement st = db.getConnection().createStatement();
            st.execute(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println(ChatColor.RED + "Could not create proxys table.");
        }
    }
    
    public static void createIPBanTable(final Database db) {
        final String query = "CREATE TABLE ipbans ( ip  TEXT(20) NOT NULL, reason  TEXT(100), banner  TEXT(30), time  BIGINT NOT NULL DEFAULT 0, expires  BIGINT NOT NULL DEFAULT 0 );";
        try {
            final Statement st = db.getConnection().createStatement();
            st.execute(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println(ChatColor.RED + "Could not create ipbans table.");
        }
    }
    
    public static void createMuteTable(final Database db) {
        final String query = "CREATE TABLE mutes ( name  TEXT(30) NOT NULL, muter  TEXT(30), time  BIGINT DEFAULT 0, expires  BIGINT DEFAULT 0, reason  TEXT(100) NOT NULL );";
        try {
            final Statement st = db.getConnection().createStatement();
            st.execute(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println(ChatColor.RED + "Could not create mutes table.");
        }
    }
    
    public static void createIPHistoryTable(final Database db) {
        final String query = "CREATE TABLE iphistory ( name  TEXT(30) NOT NULL, ip  TEXT(20) NOT NULL);";
        try {
            final Statement st = db.getConnection().createStatement();
            st.execute(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println(ChatColor.RED + "Could not create iphistory table.");
        }
    }
    
    public static void createWarningsTable(final Database db) {
        final String query = "CREATE TABLE warnings (name TEXT(30) NOT NULL, reason TEXT(100) NOT NULL, banner TEXT(30) NOT NULL, expires BIGINT(30));";
        try {
            final Statement st = db.getConnection().createStatement();
            st.execute(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println(ChatColor.RED + "Could not create warnings table.");
        }
    }
}
