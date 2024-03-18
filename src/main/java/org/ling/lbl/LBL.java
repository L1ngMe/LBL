package org.ling.lbl;

import org.bukkit.plugin.java.JavaPlugin;
import org.ling.lbl.commands.LBLCommand;

import java.sql.SQLException;

public final class LBL extends JavaPlugin {

    private DataBase dataBase;

    public DataBase getDataBase() {
        return dataBase;
    }

    public static LBL getInstance() {
        return getPlugin(LBL.class);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        {
            try {
                dataBase = new DataBase(getDataFolder().getAbsolutePath() + "/" + DataBase.getTableName() + ".db");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        new LBLCommand();
    }

    @Override
    public void onDisable() {
        try {
            dataBase.closeConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
