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

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ClientBrandListener extends PacketAdapter {

    private final LengClientCheck plugin;

    public ClientBrandListener(LengClientCheck plugin) {
        super(plugin, PacketType.Play.Client.CUSTOM_PAYLOAD);
        this.plugin = plugin;
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        Player player = event.getPlayer();
        String channel = event.getPacket().getStrings().read(0);

        if (channel.equals("MC|Brand")) {
            String clientBrand = WrappedChatComponent.fromHandle(event.getPacket().getSpecificModifier(Object.class).read(0)).getJson();
            clientBrand = clientBrand.replace("\"", ""); // 去除引号

            // 检查违禁Mod
            if (plugin.getBannedMods().contains(clientBrand.toLowerCase())) {
                player.kickPlayer(ChatColor.RED + "检测到违禁Mod: " + clientBrand);
                event.setCancelled(true);
            }
        }
    }

    public void register() {
        plugin.getProtocolManager().addPacketListener(this);
    }
}