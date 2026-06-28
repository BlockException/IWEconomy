package eu.iwmedia.iweconomy.command;

import eu.iwmedia.iweconomy.IWEconomy;
import eu.iwmedia.iweconomy.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    private final IWEconomy plugin;

    public ReloadCommand(IWEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("iweconomy.manage.reload.admin")) {
            ColorUtil.sendMessage(sender, plugin.getLanguageManager().getMessage("no-permission"));
            return true;
        }

        plugin.reloadConfig();
        plugin.getLanguageManager().reloadLanguages();
        
        ColorUtil.sendMessage(sender, plugin.getLanguageManager().getMessage("reload-success"));
        return true;
    }
}
