package io.github.bagas123.roulette;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.bagas123.roulette.api.RouletteAPI;
import net.md_5.bungee.api.ChatColor;

public class RouletteGUI implements Listener {

    public static Inventory rouletteGUI = Bukkit.createInventory(null, 45, ChatColor.BOLD + "Roulette");
    String winsound = Main.instance.getConfig().getString("win-sound");
    String losesound = Main.instance.getConfig().getString("lose-sound");
    String addbetsound = Main.instance.getConfig().getString("addbetpush-sound");
    String addbetbalsound = Main.instance.getConfig().getString("addbet-sound");
    String resetbetsound = Main.instance.getConfig().getString("resetbet-sound");
    String lostmessage = Main.instance.getConfig().getString("lost-message");

    RouletteAPI RouletteAPI = new RouletteAPI();

    public static ItemStack[] items = new ItemStack[9];
    {
	items[0] = red12;
	items[1] = black35;
	items[2] = red3;
	items[3] = black26;
	items[4] = green;
	items[5] = red32;
	items[6] = black15;
	items[7] = red19;
	items[8] = black4;
    }
    public static int itemIndex = 0;

    public static ItemStack createItem(ItemStack item, String name, String[] lore) {
	ItemMeta im = item.getItemMeta();
	im.setDisplayName(name);
	im.setLore(Arrays.asList(lore));
	item.setItemMeta(im);
	return item;
    }

    static ItemStack white = createItem(new ItemStack(Material.STAINED_GLASS_PANE), ChatColor.BOLD + "",
	    new String[] {});

    static ItemStack black26 = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15),
	    ChatColor.translateAlternateColorCodes('&', "&8&l26"), new String[] {});

    static ItemStack red3 = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14),
	    ChatColor.translateAlternateColorCodes('&', "&c&l3"), new String[] {});

    static ItemStack black35 = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15),
	    ChatColor.translateAlternateColorCodes('&', "&8&l35"), new String[] {});

    static ItemStack red12 = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14),
	    ChatColor.translateAlternateColorCodes('&', "&c&l12"), new String[] {});

    static ItemStack green = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5),
	    ChatColor.translateAlternateColorCodes('&', "&a&l0"), new String[] {});

    static ItemStack red32 = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14),
	    ChatColor.translateAlternateColorCodes('&', "&c&l32"), new String[] {});

    static ItemStack black15 = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15),
	    ChatColor.translateAlternateColorCodes('&', "&8&l15"), new String[] {});

    static ItemStack red19 = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14),
	    ChatColor.translateAlternateColorCodes('&', "&c&l19"), new String[] {});

    static ItemStack black4 = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15),
	    ChatColor.translateAlternateColorCodes('&', "&8&l4"), new String[] {});

    static ItemStack barrier = createItem(new ItemStack(Material.BARRIER),
	    ChatColor.translateAlternateColorCodes('&', ""), new String[] {});

    static ItemStack notenough = createItem(new ItemStack(Material.BARRIER),
	    ChatColor.translateAlternateColorCodes('&', "&c&lNot enough money."), new String[] {});

    static ItemStack hopper = createItem(new ItemStack(Material.HOPPER),
	    ChatColor.translateAlternateColorCodes('&', "&f&6&lStatus"), new String[] { "",
		    ChatColor.translateAlternateColorCodes('&', "&c&lNot enough players to start roulette!") });

    static ItemStack hoppermoving = createItem(new ItemStack(Material.HOPPER),
	    ChatColor.translateAlternateColorCodes('&', "&f&6&lStatus"),
	    new String[] { "", ChatColor.translateAlternateColorCodes('&', "&c&lSpinning..") });

    static {

	rouletteGUI.setItem(4, hopper);
	rouletteGUI.setItem(9, red12);
	rouletteGUI.setItem(10, black35);
	rouletteGUI.setItem(11, red3);
	rouletteGUI.setItem(12, black26);
	rouletteGUI.setItem(13, green);
	rouletteGUI.setItem(14, red32);
	rouletteGUI.setItem(15, black15);
	rouletteGUI.setItem(16, red19);
	rouletteGUI.setItem(17, black4);

	while (rouletteGUI.firstEmpty() != -1) {
	    rouletteGUI.setItem(rouletteGUI.firstEmpty(), white);
	}
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
	Player p = (Player) event.getWhoClicked();
	ItemStack clicked = event.getCurrentItem();
	Inventory inventory = event.getInventory();

	if (inventory.getName().equals(ChatColor.BOLD + "Roulette")) {
	    event.setCancelled(true);
	}

	if (inventory.getName().equals(ChatColor.BOLD + "Roulette - " + p.getName())) {
	    event.setCancelled(true);
	    if (clicked.getItemMeta().getDisplayName()
		    .equals(ChatColor.translateAlternateColorCodes('&', "&a&lPush bet"))) {
		if (!Main.onspin) {
		    if (Main.betplayer.get(p.getUniqueId()) > 0) {
			if (event.isLeftClick()) {
			    p.openInventory(RouletteGUI.rouletteGUI);
			    RouletteAPI.removeTokenBal(p.getName(), Main.betplayer.get(p.getUniqueId()));
			    Main.rollers.put(p.getUniqueId(), "15");
			    p.playSound(p.getLocation(), Sound.valueOf(addbetsound), 1, 1);
			} else if (event.isRightClick()) {
			    p.openInventory(RouletteGUI.rouletteGUI);
			    RouletteAPI.removeTokenBal(p.getName(), Main.betplayer.get(p.getUniqueId()));
			    Main.rollers.put(p.getUniqueId(), "14");
			    p.playSound(p.getLocation(), Sound.valueOf(addbetsound), 1, 1);
			} else if (event.getClick().isCreativeAction()) {
			    p.openInventory(RouletteGUI.rouletteGUI);
			    RouletteAPI.removeTokenBal(p.getName(), Main.betplayer.get(p.getUniqueId()));
			    Main.rollers.put(p.getUniqueId(), "5");
			    p.playSound(p.getLocation(), Sound.valueOf(addbetsound), 1, 1);
			}
		    } else {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&8&l[&4&lRoulette&8&l] &4Minimum bet is 1 to join!"));
		    }
		} else {
		    p.closeInventory();
		    p.sendMessage(ChatColor.translateAlternateColorCodes('&',
			    "&8&l[&4&lRoulette&8&l] &4&lGame has already started. Try again soon."));
		}

	    }

	    if (clicked.getItemMeta().getDisplayName()
		    .equals(ChatColor.translateAlternateColorCodes('&', "&c&lReset bet"))) {
		Main.betplayer.put(p.getUniqueId(), 0);
		p.updateInventory();
		p.closeInventory();
		Bukkit.getServer().dispatchCommand(p, "roulette");
		p.playSound(p.getLocation(), Sound.valueOf(resetbetsound), 1, 1);
	    }
	    if (clicked.getItemMeta().getDisplayName()
		    .equals(ChatColor.translateAlternateColorCodes('&', "&6&l⛂ &a&l1"))) {
		Main.betplayer.put(p.getUniqueId(), Main.betplayer.get(p.getUniqueId()) + 1);
		if (RouletteAPI.getTokenBal(p.getName()) < Main.betplayer.get(p.getUniqueId())) {
		    Main.betplayer.put(p.getUniqueId(), RouletteAPI.getTokenBal(p.getName()));
		}
		p.updateInventory();
		p.closeInventory();
		Bukkit.getServer().dispatchCommand(p, "roulette");
		p.playSound(p.getLocation(), Sound.valueOf(addbetbalsound), 1, 1);
	    }
	    if (clicked.getItemMeta().getDisplayName()
		    .equals(ChatColor.translateAlternateColorCodes('&', "&6&l⛃ &a&l5"))) {
		Main.betplayer.put(p.getUniqueId(), Main.betplayer.get(p.getUniqueId()) + 5);
		if (RouletteAPI.getTokenBal(p.getName()) < Main.betplayer.get(p.getUniqueId())) {
		    Main.betplayer.put(p.getUniqueId(), RouletteAPI.getTokenBal(p.getName()));
		}
		p.updateInventory();
		p.closeInventory();
		Bukkit.getServer().dispatchCommand(p, "roulette");
		p.playSound(p.getLocation(), Sound.valueOf(addbetbalsound), 1, 1);
	    }
	    if (clicked.getItemMeta().getDisplayName()
		    .equals(ChatColor.translateAlternateColorCodes('&', "&6&l⛃ &a&l10"))) {
		Main.betplayer.put(p.getUniqueId(), Main.betplayer.get(p.getUniqueId()) + 10);
		if (RouletteAPI.getTokenBal(p.getName()) < Main.betplayer.get(p.getUniqueId())) {
		    Main.betplayer.put(p.getUniqueId(), RouletteAPI.getTokenBal(p.getName()));
		}
		p.updateInventory();
		p.closeInventory();
		Bukkit.getServer().dispatchCommand(p, "roulette");
		p.playSound(p.getLocation(), Sound.valueOf(addbetbalsound), 1, 1);
	    }
	    if (clicked.getItemMeta().getDisplayName()
		    .equals(ChatColor.translateAlternateColorCodes('&', "&6&l⛃ &a&l20"))) {
		Main.betplayer.put(p.getUniqueId(), Main.betplayer.get(p.getUniqueId()) + 20);
		if (RouletteAPI.getTokenBal(p.getName()) < Main.betplayer.get(p.getUniqueId())) {
		    Main.betplayer.put(p.getUniqueId(), RouletteAPI.getTokenBal(p.getName()));
		}
		p.updateInventory();
		p.closeInventory();
		Bukkit.getServer().dispatchCommand(p, "roulette");
		p.playSound(p.getLocation(), Sound.valueOf(addbetbalsound), 1, 1);

	    }
	}
    }
}
