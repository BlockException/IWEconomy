package eu.iwmedia.iweconomy.command;

import eu.iwmedia.iweconomy.IWEconomy;
import eu.iwmedia.iweconomy.util.ColorUtil;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class EcoCommand implements CommandExecutor {

    private final IWEconomy plugin;

    public EcoCommand(IWEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("iweconomy.manage.admin")) {
            ColorUtil.sendMessage(sender, plugin.getLanguageManager().getMessage("no-permission"));
            return true;
        }
        if (args.length != 2) {
            ColorUtil.sendMessage(sender, plugin.getLanguageManager().getMessage("eco-usage"));
            return true;
        }
        String action = args[0].toLowerCase();
        if (!action.equals("add") && !action.equals("set") && !action.equals("remove") && !action.equals("reset")) {
            ColorUtil.sendMessage(sender, plugin.getLanguageManager().getMessage("eco-usage"));
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            ColorUtil.sendMessage(sender, plugin.getLanguageManager().getMessage("player-not-found"));
            return true;
        }
        if (action.equals("reset")) {
            plugin.getBalanceStorage().resetBalance(target.getUniqueId());
            ColorUtil.sendMessage(sender, plugin.getLanguageManager().getMessage("eco-reset-success", Map.of(
                    "target", target.getName()
            )));
            return true;
        }
        if (sender instanceof Player player) {
            openEcoMenu(player, action, target);
        } else {
            ColorUtil.sendMessage(sender, plugin.getLanguageManager().getMessage("eco-console-usage"));
        }
        return true;
    }

    private void openEcoMenu(Player player, String action, OfflinePlayer target) {
        ItemStack item = new ItemStack(Material.SUNFLOWER);
        item = ColorUtil.applyToItem(item, 
                plugin.getLanguageManager().getMessage("enter-amount"), 
                Collections.singletonList(plugin.getLanguageManager().getMessage("enter-amount-lore")));

        new AnvilGUI.Builder()
                .plugin(plugin)
                .title(plugin.getLanguageManager().getMessage("eco-menu-title"))
                .text(net.md_5.bungee.api.ChatColor.of("#14fdab") + "0")
                .itemLeft(item)
                .onClick((slot, stateSnapshot) -> {
                    if (slot != AnvilGUI.Slot.OUTPUT) return Collections.emptyList();
                    Player p = stateSnapshot.getPlayer();
                    String input = stateSnapshot.getText().replaceAll("(?i)§[0-9A-FK-ORX]", "").replaceAll("&[0-9a-fk-orx]", "").trim();
                    try {
                        double amount = Double.parseDouble(input);
                        if (amount <= 0) {
                            ColorUtil.sendMessage(p, plugin.getLanguageManager().getMessage("amount-positive"));
                            return Arrays.asList(AnvilGUI.ResponseAction.close());
                        }
                        switch (action) {
            case "add" -> {
                plugin.getBalanceStorage().addBalance(target.getUniqueId(), amount);
                ColorUtil.sendMessage(p, plugin.getLanguageManager().getMessage("eco-add-success", Map.of(
                        "amount", ColorUtil.formatMoney(amount, plugin.getCurrencySymbol()),
                        "target", target.getName())));
            }
            case "set" -> {
                plugin.getBalanceStorage().setBalance(target.getUniqueId(), amount);
                ColorUtil.sendMessage(p, plugin.getLanguageManager().getMessage("eco-set-success", Map.of(
                        "amount", ColorUtil.formatMoney(amount, plugin.getCurrencySymbol()),
                        "target", target.getName())));
            }
            case "remove" -> {
                if (!plugin.getBalanceStorage().removeBalance(target.getUniqueId(), amount)) {
                    ColorUtil.sendMessage(p, plugin.getLanguageManager().getMessage("insufficient-funds-target"));
                    return Arrays.asList(AnvilGUI.ResponseAction.close());
                }
                ColorUtil.sendMessage(p, plugin.getLanguageManager().getMessage("eco-remove-success", Map.of(
                        "amount", ColorUtil.formatMoney(amount, plugin.getCurrencySymbol()),
                        "target", target.getName())));
            }
        }
                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                    } catch (NumberFormatException e) {
                        ColorUtil.sendMessage(p, plugin.getLanguageManager().getMessage("invalid-number"));
                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                    }
                })
                .open(player);
    }
}
