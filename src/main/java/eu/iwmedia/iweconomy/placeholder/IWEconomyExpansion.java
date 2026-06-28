package eu.iwmedia.iweconomy.placeholder;

import eu.iwmedia.iweconomy.IWEconomy;
import eu.iwmedia.iweconomy.util.ColorUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IWEconomyExpansion extends PlaceholderExpansion {

    private final IWEconomy plugin;

    public IWEconomyExpansion(IWEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "iweconomy";
    }

    @Override
    public @NotNull String getAuthor() {
        return "BlockException_";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return null;

        double balance = plugin.getBalanceStorage().getBalance(player.getUniqueId());

        if (params.equalsIgnoreCase("balance")) {
            return ColorUtil.formatMoney(balance, plugin.getCurrencySymbol());
        } else if (params.equalsIgnoreCase("balanceformatted")) {
            return ColorUtil.formatMoney(balance, plugin.getCurrencySymbol());
        } else if (params.equalsIgnoreCase("balanceraw")) {
            return String.valueOf(balance);
        }

        return null;
    }
}
