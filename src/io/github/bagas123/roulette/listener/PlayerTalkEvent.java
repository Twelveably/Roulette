package io.github.bagas123.roulette.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.bagas123.roulette.EnchantGlow;
import io.github.bagas123.roulette.Main;
import io.github.bagas123.roulette.api.HiddenStringUtils;
import io.github.bagas123.roulette.api.RouletteAPI;
import net.md_5.bungee.api.ChatColor;

public class PlayerTalkEvent implements Listener {

    RouletteAPI RouletteAPI = new RouletteAPI();
    

    ItemStack white = Main.createItem(new ItemStack(Material.STAINED_GLASS_PANE), ChatColor.BOLD + "", new String[] {});

    ItemStack black26 = Main.createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15),
	    ChatColor.translateAlternateColorCodes('&', "&8&l26"), new String[] {});

    ItemStack red3 = Main.createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14),
	    ChatColor.translateAlternateColorCodes('&', "&c&l3"), new String[] {});

    ItemStack black35 = Main.createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15),
	    ChatColor.translateAlternateColorCodes('&', "&8&l35"), new String[] {});

    ItemStack red12 = Main.createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14),
	    ChatColor.translateAlternateColorCodes('&', "&c&l12"), new String[] {});

    ItemStack green = Main.createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5),
	    ChatColor.translateAlternateColorCodes('&', "&a&l0"), new String[] {});

    ItemStack red32 = Main.createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14),
	    ChatColor.translateAlternateColorCodes('&', "&c&l32"), new String[] {});

    ItemStack black15 = Main.createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15),
	    ChatColor.translateAlternateColorCodes('&', "&8&l15"), new String[] {});

    ItemStack red19 = Main.createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14),
	    ChatColor.translateAlternateColorCodes('&', "&c&l19"), new String[] {});

    ItemStack redclear = Main.createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14),
	    ChatColor.translateAlternateColorCodes('&', "&c&lReset bet"), new String[] {});

    ItemStack goback = Main.createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14),
	    ChatColor.translateAlternateColorCodes('&', "&c&lGo back"), new String[] {});

    ItemStack black4 = Main.createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15),
	    ChatColor.translateAlternateColorCodes('&', "&8&l4"), new String[] {});

    ItemStack hopper = Main.createItem(new ItemStack(Material.HOPPER),
	    ChatColor.translateAlternateColorCodes('&', "&f&6&lStatus"), new String[] { "",
		    ChatColor.translateAlternateColorCodes('&', "&c&lNot enough players to start roulette!") });
    
    ItemStack makebet = Main.createItem(new ItemStack(Material.HOPPER),
	    ChatColor.translateAlternateColorCodes('&', "&f&6&lStatus"),
	    new String[] { "", ChatColor.translateAlternateColorCodes('&', "&c&lYou have to make a bet to join.") });

    ItemStack hopperstart = Main.createItem(new ItemStack(Material.HOPPER),
	    ChatColor.translateAlternateColorCodes('&', "&f&6&lStatus"),
	    new String[] { "", ChatColor.translateAlternateColorCodes('&', "&a&lClick &6&lPush bet &a&lto join in.") });

    static ItemStack hoppermoving = Main.createItem(new ItemStack(Material.HOPPER),
	    ChatColor.translateAlternateColorCodes('&', "&f&6&lStatus"),
	    new String[] { "", ChatColor.translateAlternateColorCodes('&', "&c&lSpinning..") });


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
		    Inventory custom = Bukkit.createInventory(null, 45, ChatColor.BOLD + "Roulette " + HiddenStringUtils.encodeString(p.getName()));
		    Double bal = Main.economy.getBalance(p);
		    ItemStack redbet = Main.createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14),
			    ChatColor.translateAlternateColorCodes('&', "&c&lRED"), new String[] {"", ChatColor.translateAlternateColorCodes('&', "&7Click to make a bet on &c&lRED")});

		    ItemStack greenbet = Main.createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5),
			    ChatColor.translateAlternateColorCodes('&', "&a&lGREEN"), new String[] {"", ChatColor.translateAlternateColorCodes('&', "&7Click to make a bet on &a&lGREEN")});

		    ItemStack blackbet = Main.createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15),
			    ChatColor.translateAlternateColorCodes('&', "&8&lBLACK"), new String[] {"", ChatColor.translateAlternateColorCodes('&', "&7Click to make a bet on &8&lBLACK")});
		    
		    custom.setItem(4, makebet);

		    if (Main.instance.color.containsKey(p.getUniqueId())) {
			custom.setItem(33, redclear);
			custom.setItem(4, hopperstart);
			if (Main.instance.color.get(p.getUniqueId()) == 14) {
			    ItemStack bet = Main.createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5),
				    ChatColor.translateAlternateColorCodes('&', "&a&lPush bet"),
				    new String[] { "", ChatColor.translateAlternateColorCodes('&', "&f&m------&8&lINFO&f&m------"),
					    ChatColor.translateAlternateColorCodes('&', "&7Betting on &c&lRED."),
					    ChatColor.translateAlternateColorCodes('&',
						    "&7Bet : &6&l⛃ " + Main.betplayer.get(p.getUniqueId())) });

			    ItemStack book = Main.createItem(new ItemStack(Material.BOOK),
				    ChatColor.translateAlternateColorCodes('&', "&6&lInfo"),
				    new String[] { "",
					    ChatColor.translateAlternateColorCodes('&', "&7Betting on &c&lRED."),
					    ChatColor.translateAlternateColorCodes('&',
						    "&7Bet : &6&l⛃ " + Main.betplayer.get(p.getUniqueId())) });

			    EnchantGlow.addGlow(book);
			    EnchantGlow.addGlow(bet);

			    custom.setItem(8, book);
			    custom.setItem(34, bet);
			} else if (Main.instance.color.get(p.getUniqueId()) == 15) {
			    ItemStack bet = Main.createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5),
				    ChatColor.translateAlternateColorCodes('&', "&a&lPush bet"),
				    new String[] { "", ChatColor.translateAlternateColorCodes('&', "&f&m------&8&lINFO&f&m------"),
					    ChatColor.translateAlternateColorCodes('&', "&7Betting on &8&lBLACK."),
					    ChatColor.translateAlternateColorCodes('&',
						    "&7Bet : &6&l⛃ " + Main.betplayer.get(p.getUniqueId())) });

			    ItemStack book = Main.createItem(new ItemStack(Material.BOOK),
				    ChatColor.translateAlternateColorCodes('&', "&6&lInfo"),
				    new String[] { "",
					    ChatColor.translateAlternateColorCodes('&', "&7Betting on &8&lBLACK."),
					    ChatColor.translateAlternateColorCodes('&',
						    "&7Bet : &6&l⛃ " + Main.betplayer.get(p.getUniqueId())) });

			    EnchantGlow.addGlow(book);
			    EnchantGlow.addGlow(bet);

			    custom.setItem(8, book);
			    custom.setItem(34, bet);
			} else if (Main.instance.color.get(p.getUniqueId()) == 5) {
			    ItemStack bet = Main.createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5),
				    ChatColor.translateAlternateColorCodes('&', "&a&lPush bet"),
				    new String[] { "", ChatColor.translateAlternateColorCodes('&', "&f&m------&8&lINFO&f&m------"),
					    ChatColor.translateAlternateColorCodes('&', "&7Betting on &a&lGREEN."),
					    ChatColor.translateAlternateColorCodes('&',
						    "&7Bet : &6&l⛃ " + Main.betplayer.get(p.getUniqueId())) });

			    ItemStack book = Main.createItem(new ItemStack(Material.BOOK),
				    ChatColor.translateAlternateColorCodes('&', "&6&lInfo"),
				    new String[] { "",
					    ChatColor.translateAlternateColorCodes('&', "&7Betting on &a&lGREEN."),
					    ChatColor.translateAlternateColorCodes('&',
						    "&7Bet : &6&l⛃ " + Main.betplayer.get(p.getUniqueId())) });

			    EnchantGlow.addGlow(book);
			    EnchantGlow.addGlow(bet);

			    custom.setItem(8, book);
			    custom.setItem(34, bet);
			}
		    } else {
			custom.setItem(28, redbet);
			custom.setItem(29, greenbet);
			custom.setItem(30, blackbet);
		    }

		    int num = Main.betplayer.get(p.getUniqueId());

		    custom.setItem(9, red12);
		    custom.setItem(10, black35);
		    custom.setItem(11, red3);
		    custom.setItem(12, black26);
		    custom.setItem(13, green);
		    custom.setItem(14, red32);
		    custom.setItem(15, black15);
		    custom.setItem(16, red19);
		    custom.setItem(17, black4);

		    while (custom.firstEmpty() != -1) {
			custom.setItem(custom.firstEmpty(), white);
		    }
		    p.openInventory(custom);
		} else {
		    e.setCancelled(true);
		    p.sendMessage(ChatColor.translateAlternateColorCodes('&',
			    "&8[&4&lRoulette&8] &cInsufficient tokens. Type &6&lcancel &cto cancel."));
		}
	    } else if (e.getMessage().equalsIgnoreCase("Cancel")) {
		e.setCancelled(true);
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&4&lRoulette&8] &cBetting cancelled."));
		Main.instance.betting.remove(p.getUniqueId());
	    } else {
		e.setCancelled(true);
		p.sendMessage(ChatColor.translateAlternateColorCodes('&',
			"&8[&4&lRoulette&8] &cOnly numbers allowed. Type &6&lcancel &cto cancel."));
	    }
	}
    }
}
