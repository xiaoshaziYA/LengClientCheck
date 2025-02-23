/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.leng;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class CommandHandler implements CommandExecutor {

    private final LengClientCheck plugin;

    public CommandHandler(LengClientCheck plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // 显示帮助页面
            sendHelpMessage(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "add":
                return handleAddCommand(sender, args);
            case "remove":
                return handleRemoveCommand(sender, args);
            case "list":
                return handleListCommand(sender);
            case "dupeip":
                return handleDupeIpCommand(sender, args);
            case "help":
                sendHelpMessage(sender);
                return true;
            default:
                sender.sendMessage(ChatColor.RED + "未知命令。使用 /lcc help 查看帮助。");
                return true;
        }
    }

    /**
     * 处理 /lcc add 命令
     */
    private boolean handleAddCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("lcc.staff")) {
            sender.sendMessage(ChatColor.RED + "你没有权限执行此命令。");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "用法: /lcc add <玩家>");
            return true;
        }

        String playerName = args[1];
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) {
            sender.sendMessage(ChatColor.RED + "未找到玩家: " + playerName);
            return true;
        }

        Set<String> staffMembers = plugin.getStaffMembers();

        if (staffMembers.contains(playerName)) {
            sender.sendMessage(ChatColor.RED + "玩家 " + playerName + " 已经是管理员。");
            return true;
        }

        staffMembers.add(playerName);
        plugin.getConfig().set("staff", staffMembers.stream().toList());
        plugin.saveConfig();
        sender.sendMessage(ChatColor.GREEN + "已添加管理员: " + playerName);
        return true;
    }

    /**
     * 处理 /lcc remove 命令
     */
    private boolean handleRemoveCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("lcc.staff")) {
            sender.sendMessage(ChatColor.RED + "你没有权限执行此命令。");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "用法: /lcc remove <玩家>");
            return true;
        }

        String playerName = args[1];
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) {
            sender.sendMessage(ChatColor.RED + "未找到玩家: " + playerName);
            return true;
        }

        Set<String> staffMembers = plugin.getStaffMembers();

        if (!staffMembers.contains(playerName)) {
            sender.sendMessage(ChatColor.RED + "玩家 " + playerName + " 不是管理员。");
            return true;
        }

        staffMembers.remove(playerName);
        plugin.getConfig().set("staff", staffMembers.stream().toList());
        plugin.saveConfig();
        sender.sendMessage(ChatColor.GREEN + "已移除管理员: " + playerName);
        return true;
    }

    /**
     * 处理 /lcc list 命令
     */
    private boolean handleListCommand(CommandSender sender) {
        if (!sender.hasPermission("lcc.staff")) {
            sender.sendMessage(ChatColor.RED + "你没有权限执行此命令。");
            return true;
        }

        Set<String> staffMembers = plugin.getStaffMembers();
        if (staffMembers.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "当前没有管理员。");
            return true;
        }

        sender.sendMessage(ChatColor.GREEN + "LCC 管理员列表:");
        for (String staffName : staffMembers) {
            sender.sendMessage(ChatColor.GOLD + "- " + staffName);
        }
        return true;
    }

    /**
     * 处理 /lcc dupeip 命令
     */
    private boolean handleDupeIpCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("lcc.staff")) {
            sender.sendMessage(ChatColor.RED + "你没有权限执行此命令。");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "用法: /lcc dupeip <玩家>");
            return true;
        }

        String targetName = args[1];
        Player targetPlayer = Bukkit.getPlayer(targetName);
        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "未找到在线玩家: " + targetName);
            return true;
        }

        String targetIp = targetPlayer.getAddress().getAddress().getHostAddress();
        Set<String> altAccounts = plugin.getAltAccounts(targetIp);

        if (altAccounts.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "玩家 " + targetName + " 的 IP 下没有其他小号。");
        } else {
            sender.sendMessage(ChatColor.GREEN + "玩家 " + targetName + " 的 IP 下的小号有:");
            for (String altName : altAccounts) {
                sender.sendMessage(ChatColor.GOLD + "- " + altName);
            }
        }
        return true;
    }

    /**
     * 显示帮助页面
     */
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "LengClientCheck 命令帮助:");
        sender.sendMessage(ChatColor.GOLD + "/lcc add <玩家> - 添加管理员");
        sender.sendMessage(ChatColor.GOLD + "/lcc remove <玩家> - 移除管理员");
        sender.sendMessage(ChatColor.GOLD + "/lcc list - 显示管理员列表");
        sender.sendMessage(ChatColor.GOLD + "/lcc dupeip <玩家> - 查询玩家 IP 下的小号");
        sender.sendMessage(ChatColor.GOLD + "/lcc help - 显示帮助页面");
    }
}