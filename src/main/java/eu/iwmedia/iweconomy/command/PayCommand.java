package eu.iwmedia.iweconomy.command;

import eu.iwmedia.iweconomy.IWEconomy;
import eu.iwmedia.iweconomy.util.ColorUtil;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class PayCommand implements CommandExecutor {

    private final IWEconomy plugin;

    public PayCommand(IWEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            ColorUtil.sendMessage(sender, plugin.getLanguageManager().getMessage("only-players"));
            return true;
        }
        if (!player.hasPermission("iweconomy.pay")) {
            ColorUtil.sendMessage(player, plugin.getLanguageManager().getMessage("no-permission"));
            return true;
        }
        if (args.length != 1) {
            ColorUtil.sendMessage(player, plugin.getLanguageManager().getMessage("pay-usage"));
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            ColorUtil.sendMessage(player, plugin.getLanguageManager().getMessage("player-not-found"));
            return true;
        }
        if (target.getUniqueId().equals(player.getUniqueId())) {
            ColorUtil.sendMessage(player, plugin.getLanguageManager().getMessage("pay-self"));
            return true;
        }
        openPayMenu(player, target);
        return true;
    }

    private void openPayMenu(Player player, Player target) {
        ItemStack item = new ItemStack(Material.SUNFLOWER);
        item = ColorUtil.applyToItem(item, 
                plugin.getLanguageManager().getMessage("enter-amount"), 
                Collections.singletonList(plugin.getLanguageManager().getMessage("enter-amount-lore")));

        new AnvilGUI.Builder()
                .plugin(plugin)
                .title(plugin.getLanguageManager().getMessage("pay-menu-title"))
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
                        if (plugin.getBalanceStorage().getBalance(p.getUniqueId()) < amount) {
                            ColorUtil.sendMessage(p, plugin.getLanguageManager().getMessage("insufficient-funds"));
                            return Arrays.asList(AnvilGUI.ResponseAction.close());
                        }
                        plugin.getBalanceStorage().removeBalance(p.getUniqueId(), amount);
        plugin.getBalanceStorage().addBalance(target.getUniqueId(), amount);
        ColorUtil.sendMessage(p, plugin.getLanguageManager().getMessage("pay-success", Map.of(
                "amount", ColorUtil.formatMoney(amount, plugin.getCurrencySymbol()),
                "target", target.getName())));
        ColorUtil.sendMessage(target, plugin.getLanguageManager().getMessage("pay-received", Map.of(
                "amount", ColorUtil.formatMoney(amount, plugin.getCurrencySymbol()),
                "sender", p.getName())));
                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                    } catch (NumberFormatException e) {
                        ColorUtil.sendMessage(p, plugin.getLanguageManager().getMessage("invalid-number"));
                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                    }
                })
                .open(player);
    }
}
