package com.general_hello.commands.OtherEvents;

import com.general_hello.Bot;
import com.general_hello.commands.Database.DataUtils;
import com.general_hello.commands.Database.SQLiteDataSource;
import com.general_hello.commands.Items.Initializer;
import com.general_hello.commands.Objects.Object;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class OnReadyEvent extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(OnReadyEvent.class);

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        Bot.jda = event.getJDA();
        LOGGER.info("Starting the bot...");
        try {
            final File dbFile = new File("database.db");

            if (!dbFile.exists()) {
                if (dbFile.createNewFile()) {
                    LOGGER.info("Created database file");
                } else {
                    LOGGER.info("Could not create database file");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            SQLiteDataSource.connection = DriverManager.getConnection("jdbc:sqlite:database.db");
        } catch (Exception e) {
            e.printStackTrace();
        }

        LOGGER.info("Opened database successfully");

        try (final PreparedStatement preparedStatement = SQLiteDataSource.getConnection()
                .prepareStatement("""
                        CREATE TABLE IF NOT EXISTS Player (
                        userId INTEGER,
                        hp	INTEGER DEFAULT 100,
                        melee	INTEGER DEFAULT 500,
                        magic	INTEGER DEFAULT -1,
                        neoDevilFruit	INTEGER DEFAULT -1,
                        professionExp	INTEGER DEFAULT -1,
                        strength	INTEGER DEFAULT 500,
                        endurance	INTEGER DEFAULT 500,
                        intelligence	INTEGER DEFAULT 500,
                        willpower	INTEGER DEFAULT 500,
                        speed	INTEGER DEFAULT 500,
                        rank	INTEGER DEFAULT 1,
                        exp	INTEGER DEFAULT 1,
                        aiDefeated	INTEGER DEFAULT 0,
                        achievementTitle	TEXT DEFAULT 'NONE',
                     berri	INTEGER DEFAULT 0,
                     rainbowShards	INTEGER DEFAULT 0,
                     skillSlotsCap	INTEGER DEFAULT 4,
                     pvpWon	INTEGER DEFAULT 0,
                     pvpLost	INTEGER DEFAULT 0,
                     rankWins	INTEGER DEFAULT 0,
                     rankLost	INTEGER DEFAULT 0,
                     bounty	INTEGER DEFAULT '1/-1',
                     likes	INTEGER DEFAULT 0,
                     marriagePartnerId	INTEGER DEFAULT -1,
                     senseiId	INTEGER DEFAULT -1,
                     profession	TEXT DEFAULT 'NONE'
                );"""
        )) {
            LOGGER.info("Made a new table (Player)");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (final PreparedStatement preparedStatement = SQLiteDataSource.getConnection()
                .prepareStatement("CREATE TABLE IF NOT EXISTS Skills (" +
                        "userId INTEGER," + skills() +
                        ");"
                )) {
            LOGGER.info("Made a new table (Skills)");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String skills() {
        StringBuilder items = new StringBuilder();

        int x = 0;
        ArrayList<String> names = Initializer.allNames;
        while (x < names.size()) {
            Object object = Initializer.allObjects.get(names.get(x));
            items.append(DataUtils.filter(object.getName())).append(" INTEGER DEFAULT 0");
            if (x + 1 < names.size()) {
                items.append(", ");
            }
            x++;
        }

        return items.toString();
    }
}
