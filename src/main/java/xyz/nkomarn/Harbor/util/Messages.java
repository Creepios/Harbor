package xyz.nkomarn.Harbor.util;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import xyz.nkomarn.Harbor.Harbor;
import xyz.nkomarn.Harbor.task.Checker;

import java.util.List;
import java.util.Random;

public class Messages {
    public static void sendRandomChatMessage(final World world, final String messageList) {
        final List<String> messages = Config.getConfig().getStringList(messageList);
        if (messages.size() < 1) return;
        final int index = new Random().nextInt(Math.max(0, messages.size()));
        sendWorldChatMessage(world, messages.get(index));
    }

    public static void sendWorldChatMessage(final World world, final String message) {
        if (!Config.getBoolean("messages.chat.enabled") || message.length() < 1) return;
        world.getPlayers().forEach(player -> player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                prepareMessage(world, message))));
    }

    public static void sendActionBarMessage(final World world, final String message) {
        if (!Config.getBoolean("messages.actionbar.enabled") || message.length() < 1) return;
        world.getPlayers().forEach(player -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                TextComponent.fromLegacyText(prepareMessage(world, message))));
    }

    public static void sendBossBarMessage(final World world, final String message, final String color, final double percentage) {
        if (!Config.getBoolean("messages.bossbar.enabled") || message.length() < 1) return;
        BossBar bar = Bukkit.createBossBar(Messages.prepareMessage(world, message), getBarColor(color), BarStyle.SOLID);
        bar.setProgress(percentage);
        world.getPlayers().forEach(bar::addPlayer);
        Bukkit.getScheduler().runTaskLater(Harbor.getHarbor(), bar::removeAll, Config.getInteger("interval") * 20);
    }

    private static String prepareMessage(final World world, final String message) {
        return ChatColor.translateAlternateColorCodes('&', message
                .replace("[sleeping]", String.valueOf(Checker.getSleeping(world).size()))
                .replace("[players]", String.valueOf(Checker.getPlayers(world)))
                .replace("[needed]", String.valueOf(Checker.getSkipAmount(world)))
                .replace("[more]", String.valueOf(Checker.getNeeded(world))));
    }

    private static BarColor getBarColor(final String enumString) {
        BarColor barColor;
        try {
            barColor = BarColor.valueOf(enumString.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            barColor = BarColor.BLUE;
        }
        return barColor;
    }
}
