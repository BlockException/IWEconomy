package eu.iwmedia.iweconomy.storage;

import eu.iwmedia.iweconomy.IWEconomy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BalanceStorage {

    private static final String DATABASE_FILE = "money.sql";
    private final IWEconomy plugin;
    private final Map<UUID, Double> balances = new HashMap<>();
    private Connection connection;

    public BalanceStorage(IWEconomy plugin) {
        this.plugin = plugin;
        setupStorage();
        loadAll();
    }

    private void setupStorage() {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            plugin.getLogger().warning("Could not create plugin folder");
            return;
        }

        File databaseFile = new File(dataFolder, DATABASE_FILE);
        try {
            if (!databaseFile.exists()) {
                copyResourceToDataFolder(databaseFile);
            }
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getPath());
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS balances (uuid TEXT PRIMARY KEY, balance REAL NOT NULL)");
            }
        } catch (IOException | SQLException e) {
            plugin.getLogger().severe("Could not initialize balance storage: " + e.getMessage());
        }
    }

    private void copyResourceToDataFolder(File databaseFile) throws IOException {
        try (InputStream resourceStream = plugin.getResource(DATABASE_FILE)) {
            if (resourceStream == null) {
                if (!databaseFile.createNewFile()) {
                    throw new IOException("Could not create money.sql in data folder");
                }
                return;
            }
            Files.copy(resourceStream, databaseFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public void loadAll() {
        balances.clear();
        if (connection == null) {
            return;
        }

        String sql = "SELECT uuid, balance FROM balances";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                try {
                    UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                    double balance = resultSet.getDouble("balance");
                    balances.put(uuid, balance);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in money.sql");
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not load balances: " + e.getMessage());
        }
    }

    public void saveAll() {
        if (connection == null) {
            return;
        }

        String sql = "INSERT INTO balances(uuid, balance) VALUES (?, ?) ON CONFLICT(uuid) DO UPDATE SET balance = excluded.balance";
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (Map.Entry<UUID, Double> entry : balances.entrySet()) {
                    statement.setString(1, entry.getKey().toString());
                    statement.setDouble(2, entry.getValue());
                    statement.addBatch();
                }
                statement.executeBatch();
            }
            connection.commit();
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not save balances: " + e.getMessage());
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ignored) {
            }
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException ignored) {
            }
        }
    }

    private void saveBalance(UUID uuid, double amount) {
        if (connection == null) {
            return;
        }

        String sql = "INSERT INTO balances(uuid, balance) VALUES (?, ?) ON CONFLICT(uuid) DO UPDATE SET balance = excluded.balance";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid.toString());
            statement.setDouble(2, amount);
            statement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not save balance for " + uuid + ": " + e.getMessage());
        }
    }

    public double getBalance(UUID uuid) {
        return balances.getOrDefault(uuid, 0.0);
    }

    public void setBalance(UUID uuid, double amount) {
        balances.put(uuid, amount);
        saveBalance(uuid, amount);
    }

    public void addBalance(UUID uuid, double amount) {
        setBalance(uuid, getBalance(uuid) + amount);
    }

    public boolean removeBalance(UUID uuid, double amount) {
        double current = getBalance(uuid);
        if (current >= amount) {
            setBalance(uuid, current - amount);
            return true;
        }
        return false;
    }

    public void resetBalance(UUID uuid) {
        setBalance(uuid, 0.0);
    }

    public void close() {
        if (connection == null) {
            return;
        }
        try {
            connection.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not close balance storage: " + e.getMessage());
        }
    }
}
