package eu.iwmedia.iweconomy.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {

    private static final String PREFIX = "<#d64200><bold>E</bold><#b6611d><bold>C</bold><#958039><bold>O</bold><#75a056><bold>N</bold><#55bf72><bold>O</bold><#34de8f><bold>M</bold><#14fdab><bold>Y</bold> <dark_gray>»</dark_gray> <gray>";
    private static final Pattern LEGACY_HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(Locale.GERMANY));

    private static String convertLegacyToMiniMessage(String input) {
        String result = input;
        Matcher matcher = LEGACY_HEX_PATTERN.matcher(result);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String hex = matcher.group(1);
            matcher.appendReplacement(sb, "<#" + hex + ">");
        }
        matcher.appendTail(sb);
        result = sb.toString();
        result = result.replace("&l", "<bold>")
                     .replace("&r", "<reset>")
                     .replace("&0", "<black>")
                     .replace("&1", "<dark_blue>")
                     .replace("&2", "<dark_green>")
                     .replace("&3", "<dark_aqua>")
                     .replace("&4", "<dark_red>")
                     .replace("&5", "<dark_purple>")
                     .replace("&6", "<gold>")
                     .replace("&7", "<gray>")
                     .replace("&8", "<dark_gray>")
                     .replace("&9", "<blue>")
                     .replace("&a", "<green>")
                     .replace("&b", "<aqua>")
                     .replace("&c", "<red>")
                     .replace("&d", "<light_purple>")
                     .replace("&e", "<yellow>")
                     .replace("&f", "<white>")
                     .replace("&k", "<obfuscated>")
                     .replace("&m", "<strikethrough>")
                     .replace("&n", "<underlined>")
                     .replace("&o", "<italic>");
        return result;
    }

    public static Component format(String message) {
        return MINI_MESSAGE.deserialize(convertLegacyToMiniMessage(PREFIX + message));
    }

    public static Component formatRaw(String message) {
        return MINI_MESSAGE.deserialize(convertLegacyToMiniMessage(message));
    }

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(format(message));
    }

    public static Component formatItemText(String message) {
        return MINI_MESSAGE.deserialize("<!italic>" + convertLegacyToMiniMessage(message));
    }

    public static ItemStack applyToItem(ItemStack item, String displayName, List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        if (displayName != null) {
            meta.displayName(formatItemText(displayName));
        }
        if (lore != null) {
            List<Component> components = new ArrayList<>();
            for (String line : lore) {
                components.add(formatItemText(line));
            }
            meta.lore(components);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static String formatMoney(double amount, String currencySymbol) {
        return MONEY_FORMAT.format(amount) + currencySymbol;
    }
}
