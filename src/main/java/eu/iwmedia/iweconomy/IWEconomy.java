package eu.iwmedia.iweconomy;

import eu.iwmedia.iweconomy.command.BalanceCommand;
import eu.iwmedia.iweconomy.command.EcoCommand;
import eu.iwmedia.iweconomy.command.PayCommand;
import eu.iwmedia.iweconomy.command.ReloadCommand;
import eu.iwmedia.iweconomy.economy.EconomyHandler;
import eu.iwmedia.iweconomy.language.LanguageManager;
import eu.iwmedia.iweconomy.placeholder.IWEconomyExpansion;
import eu.iwmedia.iweconomy.storage.BalanceStorage;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public final class IWEconomy extends JavaPlugin {

    private static IWEconomy instance;
    private BalanceStorage balanceStorage;
    private LanguageManager languageManager;
    private EconomyHandler economyHandler;
    private String currencySymbol;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        currencySymbol = getConfig().getString("currency-symbol", "$");
        languageManager = new LanguageManager(this);
        balanceStorage = new BalanceStorage(this);
        economyHandler = new EconomyHandler(this);
        setupVault();
        getCommand("pay").setExecutor(new PayCommand(this));
        getCommand("eco").setExecutor(new EcoCommand(this));
        getCommand("ecoreload").setExecutor(new ReloadCommand(this));
        getCommand("balance").setExecutor(new BalanceCommand(this));
        getCommand("bal").setExecutor(new BalanceCommand(this));
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new IWEconomyExpansion(this).register();
        }
    }

    @Override
    public void onDisable() {
        if (balanceStorage != null) {
            balanceStorage.saveAll();
        }
    }

    private void setupVault() {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            Bukkit.getServicesManager().register(Economy.class, economyHandler, this, ServicePriority.Highest);
            getLogger().info("Vault hooked successfully!");
        } else {
            getLogger().warning("Vault not found! Economy features may not work.");
        }
    }

    public static IWEconomy getInstance() {
        return instance;
    }

    public BalanceStorage getBalanceStorage() {
        return balanceStorage;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }
}
