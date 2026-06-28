package eu.iwmedia.iweconomy.storage;

import eu.iwmedia.iweconomy.IWEconomy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BalanceStorage {

    private final IWEconomy plugin;
    private final Map<UUID, Double> balances = new HashMap<>();
    private File balanceFile;
    private FileConfiguration balanceConfig;

    public BalanceStorage(IWEconomy plugin) {
        this.plugin = plugin;
        setupStorage();
        loadAll();
    }

    private void setupStorage() {
        balanceFile = new File(plugin.getDataFolder(), "balances.yml");
        if (!balanceFile.exists()) {
            balanceFile.getParentFile().mkdirs();
            try {
                balanceFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        balanceConfig = YamlConfiguration.loadConfiguration(balanceFile);
    }

    public void loadAll() {
        balances.clear();
        for (String uuidString : balanceConfig.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidString);
                double balance = balanceConfig.getDouble(uuidString, 0.0);
                balances.put(uuid, balance);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid UUID in balances.yml: " + uuidString);
            }
        }
    }

    public void saveAll() {
        for (Map.Entry<UUID, Double> entry : balances.entrySet()) {
            balanceConfig.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            balanceConfig.save(balanceFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double getBalance(UUID uuid) {
        return balances.getOrDefault(uuid, 0.0);
    }

    public void setBalance(UUID uuid, double amount) {
        balances.put(uuid, amount);
        saveAll();
    }

    public void addBalance(UUID uuid, double amount) {
        double current = getBalance(uuid);
        setBalance(uuid, current + amount);
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
}
