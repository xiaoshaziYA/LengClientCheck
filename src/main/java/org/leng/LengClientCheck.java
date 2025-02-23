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

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LengClientCheck extends JavaPlugin {

    private List<String> bannedMods;
    private Set<String> staffMembers; // 使用玩家名称保存管理员列表
    private ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        // 插件启动提示
        getLogger().info(ChatColor.GREEN + "LengClientCheck 插件已启动！");

        // 加载配置文件
        saveDefaultConfig(); // 确保 config.yml 文件存在
        loadConfig();

        // 初始化 ProtocolLib
        protocolManager = ProtocolLibrary.getProtocolManager();

        // 注册事件监听器
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        // 注册命令处理器
        getCommand("lcc").setExecutor(new CommandHandler(this));

        // 初始化管理员列表
        staffMembers = loadStaffMembers();

        // 注册 ProtocolLib 数据包监听器
        new ClientBrandListener(this).register();
    }

    @Override
    public void onDisable() {
        // 插件卸载提示
        getLogger().info(ChatColor.RED + "LengClientCheck 插件已卸载！");
    }

    private void loadConfig() {
        // 加载违禁Mod列表
        bannedMods = getConfig().getStringList("banned-mods");
    }

    private Set<String> loadStaffMembers() {
        // 从配置文件加载管理员列表（玩家名称）
        return new HashSet<>(getConfig().getStringList("staff"));
    }

    /**
     * 获取某个 IP 下的所有小号
     */
    public Set<String> getAltAccounts(String ip) {
        Set<String> altAccounts = new HashSet<>();
        for (Player onlinePlayer : getServer().getOnlinePlayers()) {
            if (onlinePlayer.getAddress().getAddress().getHostAddress().equals(ip)) {
                altAccounts.add(onlinePlayer.getName());
            }
        }
        return altAccounts;
    }

    public List<String> getBannedMods() {
        return bannedMods;
    }

    public Set<String> getStaffMembers() {
        return staffMembers;
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }
}