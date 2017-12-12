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

import io.github.bagas123.roulette.api.HiddenStringUtils;
import io.github.bagas123.roulette.api.RouletteAPI;
import net.md_5.bungee.api.ChatColor;

public class RouletteGUI implements Listener {

    public static Inventory rouletteGUI = Bukkit.createInventory(null, 45, ChatColor.BOLD + "Roulette");

    RouletteAPI RouletteAPI = new RouletteAPI();

    String winsound = Main.instance.getConfig().getString("win-sound");
    String losesound = Main.instance.getConfig().getString("lose-sound");
    String addbetsound = Main.instance.getConfig().getString("addbetpush-sound");
    String addbetbalspund = Main.instance.getConfig().getString("addbet-sound");
    String resetbetsound = Main.instance.getConfig().getString("resetbet-sound");

    public static int itemIndex = 0;

    private static ItemStack createItem(ItemStack item, String name, String[] lore) {
	ItemMeta im = item.getItemMeta();
	im.setDisplayName(name);
	im.setLore(Arrays.asList(lore));
	item.setItemMeta(im);
	return item;
    }

    private final static ItemStack white = createItem(new ItemStack(Material.STAINED_GLASS_PANE), ChatColor.BOLD + "",
	    new String[] {});

    private final static ItemStack black26 = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15),
	    ChatColor.translateAlternateColorCodes('&', "&8&l26"), new String[] {});

    private final static ItemStack red3 = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14),
	    ChatColor.translateAlternateColorCodes('&', "&c&l3"), new String[] {});

    private final static ItemStack black35 = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15),
	    ChatColor.translateAlternateColorCodes('&', "&8&l35"), new String[] {});

    private final static ItemStack yellow = Main.createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 7),
	    ChatColor.BOLD + "", new String[] {});

    private final static ItemStack red12 = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14),
	    ChatColor.translateAlternateColorCodes('&', "&c&l12"), new String[] {});

    private final static ItemStack green = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5),
	    ChatColor.translateAlternateColorCodes('&', "&a&l0"), new String[] {});

    private final static ItemStack red32 = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14),
	    ChatColor.translateAlternateColorCodes('&', "&c&l32"), new String[] {});

    private final static ItemStack black15 = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15),
	    ChatColor.translateAlternateColorCodes('&', "&8&l15"), new String[] {});

    private final static ItemStack red19 = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14),
	    ChatColor.translateAlternateColorCodes('&', "&c&l19"), new String[] {});

    private final static ItemStack black4 = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15),
	    ChatColor.translateAlternateColorCodes('&', "&8&l4"), new String[] {});

    private final static ItemStack barrier = createItem(new ItemStack(Material.BARRIER),
	    ChatColor.translateAlternateColorCodes('&', ""), new String[] {});

    private final static ItemStack notenough = createItem(new ItemStack(Material.BARRIER),
	    ChatColor.translateAlternateColorCodes('&', "&c&lNot enough money."), new String[] {});

    private final static ItemStack hopper = createItem(new ItemStack(Material.HOPPER),
	    ChatColor.translateAlternateColorCodes('&', "&f&6&lStatus"), new String[] { "",
		    ChatColor.translateAlternateColorCodes('&', "&c&lNot enough players to start roulette!") });

    private final static ItemStack hoppermoving = createItem(new ItemStack(Material.HOPPER),
	    ChatColor.translateAlternateColorCodes('&', "&f&6&lStatus"),
	    new String[] { "", ChatColor.translateAlternateColorCodes('&', "&c&lSpinning..") });

    private final static ItemStack makebet = Main.createItem(new ItemStack(Material.HOPPER),
	    ChatColor.translateAlternateColorCodes('&', "&f&6&lStatus"),
	    new String[] { "", ChatColor.translateAlternateColorCodes('&', "&cYou have to make a bet to join.") });

    private final static ItemStack hopperstart = Main.createItem(new ItemStack(Material.HOPPER),
	    ChatColor.translateAlternateColorCodes('&', "&f&6&lStatus"),
	    new String[] { "", ChatColor.translateAlternateColorCodes('&', "&aClick &6&lPush bet &ato join in.") });

    static {

	while (rouletteGUI.firstEmpty() != -1) {
	    rouletteGUI.setItem(rouletteGUI.firstEmpty(), white);
	}
    }
    
    public static ItemStack[] items = new ItemStack[9];
    {
	items[0] = black35;
	items[1] = red3;
	items[2] = black26;
	items[3] = green;
	items[4] = red32;
	items[5] = black15;
	items[6] = red19;
	items[7] = black4;
	items[8] = red12;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
	Player p = (Player) event.getWhoClicked();
	ItemStack clicked = event.getCurrentItem();
	Inventory inventory = event.getInventory();

	if (inventory.getName().equals(ChatColor.BOLD + "Roulette")) {
	    event.setCancelled(true);
	    if (clicked.getItemMeta().getDisplayName()
		    .equals(ChatColor.translateAlternateColorCodes('&', "&f&c&lGo back"))) {
		Bukkit.dispatchCommand(p, "roulette");
	    }
	}

	String encoded;
	if ("Roulette ".length() + HiddenStringUtils.encodeString(p.getName()).length() > 31) {
	    String id = "Roulette " + HiddenStringUtils.encodeString(p.getName());
	    encoded = id.substring(0, 23);
	} else {
	    encoded = "Roulette " + HiddenStringUtils.encodeString(p.getName());
	}

	if (inventory.getName().equals(ChatColor.BOLD + encoded)) {
	    event.setCancelled(true);
	    if (clicked.getItemMeta().getDisplayName()
		    .equals(ChatColor.translateAlternateColorCodes('&', "&f&3&lSpectate"))) {
		p.openInventory(RouletteGUI.rouletteGUI);
	    }

	    if (clicked.getItemMeta().getDisplayName()
		    .equals(ChatColor.translateAlternateColorCodes('&', "&a&lPush bet"))) {
		if (!Main.onspin) {
		    if (Main.betplayer.get(p.getUniqueId()) > 0) {
			/*
			 * if (event.isLeftClick()) {
			 * p.openInventory(RouletteGUI.rouletteGUI);
			 * RouletteAPI.removeTokenBal(p.getName(),
			 * Main.betplayer.get(p.getUniqueId()));
			 * Main.rollers.put(p.getUniqueId(), "15");
			 * p.playSound(p.getLocation(),
			 * Sound.valueOf(Bukkit.getVersion().contains("1.11") ?
			 * addbetsound : "NOTE_BASS"), 1, 1); } else if
			 * (event.isRightClick()) {
			 * p.openInventory(RouletteGUI.rouletteGUI);
			 * RouletteAPI.removeTokenBal(p.getName(),
			 * Main.betplayer.get(p.getUniqueId()));
			 * Main.rollers.put(p.getUniqueId(), "14");
			 * p.playSound(p.getLocation(),
			 * Sound.valueOf(Bukkit.getVersion().contains("1.11") ?
			 * addbetsound : "NOTE_BASS"), 1, 1); } else if
			 * (event.getClick().isCreativeAction()) {
			 * p.openInventory(RouletteGUI.rouletteGUI);
			 * RouletteAPI.removeTokenBal(p.getName(),
			 * Main.betplayer.get(p.getUniqueId()));
			 * Main.rollers.put(p.getUniqueId(), "5");
			 * p.playSound(p.getLocation(),
			 * Sound.valueOf(Bukkit.getVersion().contains("1.11") ?
			 * addbetsound : "NOTE_BASS"), 1, 1); }
			 */
			Main.rollers.put(p.getUniqueId(), Integer.toString(Main.color.get(p.getUniqueId())));
			Main.color.remove(p.getUniqueId());
			p.openInventory(RouletteGUI.rouletteGUI);
			p.playSound(p.getLocation(),
				Sound.valueOf(Bukkit.getVersion().contains("1.8") ? "NOTE_BASS" : addbetsound), 1, 1);
			RouletteAPI.removeTokenBal(p.getName(), Main.betplayer.get(p.getUniqueId()));
		    } else {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&8[&4&lRoulette&8] &cMinimum bet is 1 to join!"));
		    }
		} else {
		    p.closeInventory();
		    p.sendMessage(ChatColor.translateAlternateColorCodes('&',
			    "&8[&4&lRoulette&8] &cGame has already started. Try again soon."));
		}

	    }

	    if (clicked.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&c&lRED"))) {
		Main.instance.betting.put(p.getUniqueId(), 14);
		p.sendMessage(ChatColor.translateAlternateColorCodes('&',
			"&8[&4&lRoulette&8] &aType in chat how much you want to bet on &c&lRED&a.\nType &6&lcancel &ato cancel."));
		p.closeInventory();

	    }

	    if (clicked.getItemMeta().getDisplayName()
		    .equals(ChatColor.translateAlternateColorCodes('&', "&a&lGREEN"))) {
		Main.instance.betting.put(p.getUniqueId(), 5);
		p.sendMessage(ChatColor.translateAlternateColorCodes('&',
			"&8[&4&lRoulette&8] &aType in chat how much you want to bet on &a&lGREEN&a.\nType &6&lcancel &ato cancel."));
		p.closeInventory();

	    }

	    if (clicked.getItemMeta().getDisplayName()
		    .equals(ChatColor.translateAlternateColorCodes('&', "&8&lBLACK"))) {
		Main.instance.betting.put(p.getUniqueId(), 15);
		p.sendMessage(ChatColor.translateAlternateColorCodes('&',
			"&8[&4&lRoulette&8] &aType in chat how much you want to bet on &8&lBLACK&a.\nType &6&lcancel &ato cancel."));
		p.closeInventory();
	    }

	    if (clicked.getItemMeta().getDisplayName()
		    .equals(ChatColor.translateAlternateColorCodes('&', "&c&lReset bet"))) {
		Main.betplayer.remove(p.getUniqueId());
		Main.color.remove(p.getUniqueId());

		ItemStack redbet = Main.createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14),
			ChatColor.translateAlternateColorCodes('&', "&c&lRED"), new String[] { "",
				ChatColor.translateAlternateColorCodes('&', "&7Click to make a bet on &c&lRED") });

		ItemStack greenbet = Main.createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5),
			ChatColor.translateAlternateColorCodes('&', "&a&lGREEN"), new String[] { "",
				ChatColor.translateAlternateColorCodes('&', "&7Click to make a bet on &a&lGREEN") });

		ItemStack blackbet = Main.createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15),
			ChatColor.translateAlternateColorCodes('&', "&8&lBLACK"), new String[] { "",
				ChatColor.translateAlternateColorCodes('&', "&7Click to make a bet on &8&lBLACK") });

		inventory.setItem(4, makebet);

		inventory.setItem(29, yellow);
		inventory.setItem(30, redbet);
		inventory.setItem(31, greenbet);
		inventory.setItem(32, blackbet);
		inventory.setItem(33, yellow);

		inventory.setItem(37, white);
		inventory.setItem(34, white);
		inventory.setItem(38, white);

		p.playSound(p.getLocation(),
			Sound.valueOf(Bukkit.getVersion().contains("1.8") ? "FIZZ" : resetbetsound), 1, 1);
	    }

	    /*
	     * if (clicked.getItemMeta().getDisplayName()
	     * .equals(ChatColor.translateAlternateColorCodes('&',
	     * "&c&lReset bet"))) { Main.betplayer.put(p.getUniqueId(), 0);
	     * p.updateInventory(); p.closeInventory();
	     * Bukkit.getServer().dispatchCommand(p, "roulette");
	     * p.playSound(p.getLocation(),
	     * Sound.valueOf(Bukkit.getVersion().contains("1.11") ?
	     * resetbetsound : "FIZZ"), 1, 1); } if
	     * (clicked.getItemMeta().getDisplayName()
	     * .equals(ChatColor.translateAlternateColorCodes('&',
	     * "&6&l⛂ &a&l1"))) { Main.betplayer.put(p.getUniqueId(),
	     * Main.betplayer.get(p.getUniqueId()) + 1); if
	     * (RouletteAPI.getTokenBal(p.getName()) <
	     * Main.betplayer.get(p.getUniqueId())) {
	     * Main.betplayer.put(p.getUniqueId(),
	     * RouletteAPI.getTokenBal(p.getName())); } p.updateInventory();
	     * p.closeInventory(); Bukkit.getServer().dispatchCommand(p,
	     * "roulette"); p.playSound(p.getLocation(),
	     * Sound.valueOf(Bukkit.getVersion().contains("1.11") ?
	     * addbetbalspund : "NOTE_BASS"), 1, 1); } if
	     * (clicked.getItemMeta().getDisplayName()
	     * .equals(ChatColor.translateAlternateColorCodes('&',
	     * "&6&l⛃ &a&l5"))) { Main.betplayer.put(p.getUniqueId(),
	     * Main.betplayer.get(p.getUniqueId()) + 5); if
	     * (RouletteAPI.getTokenBal(p.getName()) <
	     * Main.betplayer.get(p.getUniqueId())) {
	     * Main.betplayer.put(p.getUniqueId(),
	     * RouletteAPI.getTokenBal(p.getName())); } p.updateInventory();
	     * p.closeInventory(); Bukkit.getServer().dispatchCommand(p,
	     * "roulette"); p.playSound(p.getLocation(),
	     * Sound.valueOf(Bukkit.getVersion().contains("1.11") ?
	     * addbetbalspund : "NOTE_PLING"), 1, 1); } if
	     * (clicked.getItemMeta().getDisplayName()
	     * .equals(ChatColor.translateAlternateColorCodes('&',
	     * "&6&l⛃ &a&l10"))) { Main.betplayer.put(p.getUniqueId(),
	     * Main.betplayer.get(p.getUniqueId()) + 10); if
	     * (RouletteAPI.getTokenBal(p.getName()) <
	     * Main.betplayer.get(p.getUniqueId())) {
	     * Main.betplayer.put(p.getUniqueId(),
	     * RouletteAPI.getTokenBal(p.getName())); } p.updateInventory();
	     * p.closeInventory(); Bukkit.getServer().dispatchCommand(p,
	     * "roulette"); p.playSound(p.getLocation(),
	     * Sound.valueOf(Bukkit.getVersion().contains("1.11") ?
	     * addbetbalspund : "NOTE_PLING"), 1, 1); } if
	     * (clicked.getItemMeta().getDisplayName()
	     * .equals(ChatColor.translateAlternateColorCodes('&',
	     * "&6&l⛃ &a&l20"))) { Main.betplayer.put(p.getUniqueId(),
	     * Main.betplayer.get(p.getUniqueId()) + 20); if
	     * (RouletteAPI.getTokenBal(p.getName()) <
	     * Main.betplayer.get(p.getUniqueId())) {
	     * Main.betplayer.put(p.getUniqueId(),
	     * RouletteAPI.getTokenBal(p.getName())); } p.updateInventory();
	     * p.closeInventory(); Bukkit.getServer().dispatchCommand(p,
	     * "roulette"); p.playSound(p.getLocation(),
	     * Sound.valueOf(Bukkit.getVersion().contains("1.11") ?
	     * addbetbalspund : "NOTE_PLING"), 1, 1);
	     * 
	     * }
	     */
	}
    }
}
