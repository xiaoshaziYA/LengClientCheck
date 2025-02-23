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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashSet;
import java.util.Set;

public class PlayerJoinListener implements Listener {

    private final LengClientCheck plugin;

    public PlayerJoinListener(LengClientCheck plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();

        // 获取相同IP下的小号
        Set<String> altAccounts = getAltAccounts(player);

        // 向管理员发送消息
        String message = ChatColor.YELLOW + String.format("玩家: %s, 进入服务器。其IP下小号有: %s",
                playerName, String.join(", ", altAccounts));

        for (String staffName : plugin.getStaffMembers()) {
            Player staffPlayer = Bukkit.getPlayer(staffName);
            if (staffPlayer != null && staffPlayer.hasPermission("lcc.staff")) {
                staffPlayer.sendMessage(message);
            }
        }
    }

    /**
     * 获取相同IP下的小号
     */
    private Set<String> getAltAccounts(Player player) {
        Set<String> altAccounts = new HashSet<>();
        String ip = player.getAddress().getAddress().getHostAddress();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getAddress().getAddress().getHostAddress().equals(ip)) {
                altAccounts.add(onlinePlayer.getName());
            }
        }

        return altAccounts;
    }
}