package me.doozzik;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.sql.*;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Main {
    public static void main(String[] args) throws IOException, ParseException, SQLException, ClassNotFoundException {
        List<String> links = new ArrayList<>();
        links.add("https://mods.curse.com/mc-mods/MINECRAFT");
        for (int i = 2; i <= 305; i++){
            links.add("https://mods.curse.com/mc-mods/MINECRAFT?page=" + i);
        }

        List<Mod> mods = new ArrayList<>();
        for (String linkPage : links){
            Document doc = Jsoup.connect(linkPage)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                    .validateTLSCertificates(false).get();
            String page = doc.body().html();

            for (int i = 0; i < 20; i++){
                String index = "<li class=\"title\"><h4><a href=\"";
                page = page.substring(page.indexOf(index) + index.length());
                index = "\"";
                String link = "https://mods.curse.com" + page.substring(0, page.indexOf(index));

                index = "\">";
                page = page.substring(page.indexOf(index) + index.length());
                index = "<";
                String name = page.substring(0, page.indexOf(index));

                index = "average-downloads\">";
                page = page.substring(page.indexOf(index) + index.length());
                index = "Monthly";
                int monthly = Integer.parseInt(page.substring(0, page.indexOf(index)).trim().replace(",", ""));

                index = "download-total\">";
                page = page.substring(page.indexOf(index) + index.length());
                index = "Total";
                int total = Integer.parseInt(page.substring(0, page.indexOf(index)).trim().replace(",", ""));

                index = "Updated";
                page = page.substring(page.indexOf(index) + index.length());
                index = "<";
                DateFormat format = new SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH);
                Date updated = format.parse(page.substring(0, page.indexOf(index)).trim());

                index = "Created";
                page = page.substring(page.indexOf(index) + index.length());
                index = "<";
                Date created = format.parse(page.substring(0, page.indexOf(index)).trim());

                index = "grats\">";
                page = page.substring(page.indexOf(index) + index.length());
                index = "Like";
                int likes = Integer.parseInt(page.substring(0, page.indexOf(index)).trim());

                index = "Supports:";
                page = page.substring(page.indexOf(index) + index.length());
                index = "<";
                String version = page.substring(0, page.indexOf(index)).trim();

                mods.add(new Mod(name, link, monthly, total, likes, version, created, updated));
            }
        }

        createBase();
        createTable();

        for (Mod m : mods){
            addToBase(m);
        }
    }

    private static void createBase() throws SQLException, ClassNotFoundException {
        Connection con = null;
        Class.forName("org.sqlite.JDBC");
        con = DriverManager.getConnection("jdbc:sqlite:" + "mods.db");
    }

    private static void createTable() throws ClassNotFoundException, SQLException {
        Connection con = null;
        Statement stmt = null;

        Class.forName("org.sqlite.JDBC");
        con = DriverManager.getConnection("jdbc:sqlite:" + "mods.db");

        stmt = con.createStatement();
        String sql = "CREATE TABLE " + "Mods" +
                " (Link TEXT PRIMARY KEY     NOT NULL," +
                " Name         TEXT, " +
                " Monthly   INTEGER, " +
                " Total   INTEGER, " +
                " Likes   INTEGER, " +
                " Version   TEXT, " +
                " Created   TEXT, " +
                " Updated   TEXT)";
        try{
            stmt.executeUpdate(sql);
        }catch (SQLException e){
            stmt.close();
            con.close();
        }

        stmt.close();
        con.close();
    }

    private static void addToBase(Mod m) throws ClassNotFoundException, SQLException {
        Connection con = null;
        Statement stmt = null;

        Class.forName("org.sqlite.JDBC");
        con = DriverManager.getConnection("jdbc:sqlite:" + "mods.db");
        con.setAutoCommit(false);

        stmt = con.createStatement(); // replace("\"", "\"\"")
        String sql = "INSERT INTO " + "Mods" + " (Link,Name,Monthly,Total,Likes,Version,Created,Updated) " +
                "VALUES ('" + m.getLink() + "','" + m.getName() + "'," + m.getMonthlyDown() + "," +
                m.getTotalDown() + "," + m.getLikes() + ",'" + m.getVersion() + "','" + m.getCreated() +
                "','" + m.getUpdated() + "')";
        try{
            stmt.executeUpdate(sql);
        }finally{
            stmt.close();
            con.commit();
            con.close();
        }
    }
}
