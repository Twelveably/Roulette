package io.github.bagas123.roulette.listener;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import io.github.bagas123.roulette.Main;
import io.github.bagas123.roulette.api.RouletteAPI;
import net.md_5.bungee.api.ChatColor;

public class PlayerTalkEvent implements Listener {

    RouletteAPI RouletteAPI = new RouletteAPI();

    String winsound = Main.instance.getConfig().getString("win-sound");
    String losesound = Main.instance.getConfig().getString("lose-sound");
    String addbetsound = Main.instance.getConfig().getString("addbetpush-sound");
    String addbetbalspund = Main.instance.getConfig().getString("addbet-sound");
    String resetbetsound = Main.instance.getConfig().getString("resetbet-sound");
    String lostmessage = Main.instance.getConfig().getString("lost-message");

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {

	// PLAYER

	Player p = e.getPlayer();

	if (Main.instance.betting.containsKey(p.getUniqueId())) {
	    if (RouletteAPI.isNumeric(e.getMessage())) {
		if (RouletteAPI.getTokenBal(p.getName()) > Integer.parseInt(e.getMessage())) {
		    e.setCancelled(true);
		    p.playSound(p.getLocation(),
			    Sound.valueOf(Bukkit.getVersion().contains("1.11") ? addbetbalspund : "NOTE_PLING"), 1, 1);
		    RouletteAPI.removeTokenBal(p.getName(), Main.betplayer.get(p.getUniqueId()));
		    Main.betplayer.put(p.getUniqueId(), Integer.parseInt(e.getMessage()));
		    Main.color.put(p.getUniqueId(), Main.instance.betting.get(p.getUniqueId()));
		    Main.instance.betting.remove(p.getUniqueId());
		    Bukkit.getServer().dispatchCommand(p, "roulette");
		} else {
		    e.setCancelled(true);
		    p.sendMessage(ChatColor.translateAlternateColorCodes('&',
			    "&8[&4&lRoulette&8] &cInsufficient tokens. Type &6&lcancel &ato cancel."));
		}
	    } else if (e.getMessage().equalsIgnoreCase("Cancel")) {
		e.setCancelled(true);
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&4&lRoulette&8] &cBetting cancelled."));
		Main.instance.betting.remove(p.getUniqueId());
	    } else {
		e.setCancelled(true);
		p.sendMessage(ChatColor.translateAlternateColorCodes('&',
			"&8[&4&lRoulette&8] &cOnly numbers allowed. Type &6&lcancel &ato cancel."));
	    }
	}
    }
}
