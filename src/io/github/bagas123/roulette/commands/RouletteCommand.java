package io.github.bagas123.roulette.commands;

import java.io.File;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.bagas123.roulette.EnchantGlow;
import io.github.bagas123.roulette.Main;
import io.github.bagas123.roulette.RouletteGUI;
import io.github.bagas123.roulette.api.RouletteAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class RouletteCommand implements CommandExecutor {

    File file = new File(Main.instance.getDataFolder(), "leaderboards.yml");

    RouletteAPI RouletteAPI = new RouletteAPI();

    FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

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

    static ItemStack orange1 = Main.createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 1),
	    ChatColor.translateAlternateColorCodes('&', "&6&l⛂ &a&l1"),
	    new String[] { "", ChatColor.translateAlternateColorCodes('&', "&a&lAdd &6&l⛂ &a&l1 to your bet."), });

    static ItemStack orange10 = Main.createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 1),
	    ChatColor.translateAlternateColorCodes('&', "&6&l⛃ &a&l5"),
	    new String[] { "", ChatColor.translateAlternateColorCodes('&', "&a&lAdd &6&l⛃ &a&l5 to your bet."), });

    static ItemStack orange50 = Main.createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 1),
	    ChatColor.translateAlternateColorCodes('&', "&6&l⛃ &a&l10"),
	    new String[] { "", ChatColor.translateAlternateColorCodes('&', "&a&lAdd &6&l⛃ &a&l10 to your bet."), });

    static ItemStack orange100 = Main.createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 1),
	    ChatColor.translateAlternateColorCodes('&', "&6&l⛃ &a&l20"),
	    new String[] { "", ChatColor.translateAlternateColorCodes('&', "&a&lAdd &6&l⛃ &a&l20 to your bet."),

	    });

    ItemStack hopper = Main.createItem(new ItemStack(Material.HOPPER),
	    ChatColor.translateAlternateColorCodes('&', "&f&6&lStatus"), new String[] { "",
		    ChatColor.translateAlternateColorCodes('&', "&c&lNot enough players to start roulette!") });

    ItemStack hopperstart = Main.createItem(new ItemStack(Material.HOPPER),
	    ChatColor.translateAlternateColorCodes('&', "&f&6&lStatus"),
	    new String[] { "", ChatColor.translateAlternateColorCodes('&', "&a&lPress &6&lPush bet &a&lto join in.") });

    static ItemStack hoppermoving = Main.createItem(new ItemStack(Material.HOPPER),
	    ChatColor.translateAlternateColorCodes('&', "&f&6&lStatus"),
	    new String[] { "", ChatColor.translateAlternateColorCodes('&', "&c&lSpinning..") });

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

	Player player = (Player) sender;
	String name = (String) sender.getName();
	String string = "";
	String cmd = command.getName();

	if (sender instanceof Player) {
	    if (args.length == 0) {
		if (!Main.onspin && !Main.rollers.containsKey(player.getUniqueId())) {
		    Inventory custom = Bukkit.createInventory(null, 45, ChatColor.BOLD + "Roulette - " + name);
		    Double bal = Main.economy.getBalance(player);
		    if (Main.betplayer.get(player.getUniqueId()) > 0) {
			ItemStack bet = Main.createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5),
				ChatColor.translateAlternateColorCodes('&', "&a&lPush bet"),
				new String[] { "",
					ChatColor.translateAlternateColorCodes('&',
						"&f&lTotal bet: &6&l⛃ &f&l" + Main.formatter
							.format(Math.max(0, Main.betplayer.get(player.getUniqueId())))),
					ChatColor.translateAlternateColorCodes('&',
						"&f&lYour balance: &6&l⛃ &f&l" + Main.formatter
							.format(RouletteAPI.getTokenBal(player.getName()))),
					"",
					ChatColor.translateAlternateColorCodes('&', "&7&lRight click &8&l| &c&lRED"),
					ChatColor.translateAlternateColorCodes('&', "&7&lMiddle click &8&l| &a&lGREEN"),
					ChatColor.translateAlternateColorCodes('&',
						"&7&lLeft click &8&l| &8&lBLACK") });

			ItemStack book = Main.createItem(new ItemStack(Material.BOOK),
				ChatColor.translateAlternateColorCodes('&', "&6&lInfo"),
				new String[] { "",
					ChatColor.translateAlternateColorCodes(
						'&',
						"&f&lTotal bet: &6&l⛃ &f&l" + Main.formatter
							.format(Math.max(0, Main.betplayer.get(player.getUniqueId())))),
					ChatColor.translateAlternateColorCodes('&', "&f&lYour balance: &6&l⛃ &f&l"
						+ Main.formatter.format(RouletteAPI.getTokenBal(player.getName()))),

				});

			EnchantGlow.addGlow(book);
			EnchantGlow.addGlow(bet);
			custom.setItem(8, book);

			custom.setItem(34, bet);
		    }

		    if (Main.betplayer.get(player.getUniqueId()) == 0) {
			ItemStack bet = Main
				.createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5),
					ChatColor.translateAlternateColorCodes('&', "&a&lPush bet"),
					new String[] { "", ChatColor.translateAlternateColorCodes('&',
						"&f&lTotal bet: &6&l⛃ &f&l"
							+ Math.max(0, Main.betplayer.get(player.getUniqueId()))),
						ChatColor.translateAlternateColorCodes(
							'&',
							"&f&lYour balance: &6&l⛃ &f&l" + Main.formatter
								.format(RouletteAPI.getTokenBal(player.getName()))),
						"",
						ChatColor.translateAlternateColorCodes('&',
							"&7&lRight click &8&l| &c&lRED"),
						ChatColor.translateAlternateColorCodes('&',
							"&7&lMiddle click &8&l| &a&lGREEN"),
						ChatColor.translateAlternateColorCodes('&',
							"&7&lLeft click &8&l| &8&lBLACK") });

			ItemStack book = Main.createItem(new ItemStack(Material.BOOK),
				ChatColor.translateAlternateColorCodes('&', "&6&lInfo"),
				new String[] { "",
					ChatColor.translateAlternateColorCodes('&',
						"&f&lTotal bet: &6&l⛃ &f&l"
							+ Math.max(0, Main.betplayer.get(player.getUniqueId()))),
					ChatColor.translateAlternateColorCodes('&', "&f&lYour balance: &6&l⛃ &f&l"
						+ Main.formatter.format(RouletteAPI.getTokenBal(player.getName()))),

				});

			EnchantGlow.addGlow(book);
			EnchantGlow.addGlow(bet);

			custom.setItem(8, book);

			custom.setItem(34, bet);
		    }

		    int num = Main.betplayer.get(player.getUniqueId());

		    custom.setItem(4, hopperstart);
		    custom.setItem(9, red12);
		    custom.setItem(10, black35);
		    custom.setItem(11, red3);
		    custom.setItem(12, black26);
		    custom.setItem(13, green);
		    custom.setItem(14, red32);
		    custom.setItem(15, black15);
		    custom.setItem(16, red19);
		    custom.setItem(17, black4);
		    custom.setItem(28, orange1);
		    custom.setItem(29, orange10);
		    custom.setItem(30, orange50);
		    custom.setItem(31, orange100);
		    custom.setItem(33, redclear);

		    while (custom.firstEmpty() != -1) {
			custom.setItem(custom.firstEmpty(), white);
		    }
		    player.openInventory(custom);
		}
		if (Main.onspin) {
		    player.openInventory(RouletteGUI.rouletteGUI);
		}

		if (Main.rollers.containsKey(player.getUniqueId())) {
		    player.openInventory(RouletteGUI.rouletteGUI);
		}
	    } else if (args[0].equals("top")) {
		if (sender.hasPermission("roulette.top")) {
		    for (String playerName : cfg.getConfigurationSection("players").getKeys(false)) {
			Main.players.put(playerName, cfg.getDouble("players." + playerName + ".winnings"));
		    }

		    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
			    "&6&l&m-----------&8&l[&4&lTop Winnings&8&l]&6&l&m-----------"));

		    String nextTop = "";
		    Double nextTopWin = 0.0;

		    for (int i = 1; i < 11; i++) {
			for (String playerName : Main.players.keySet()) {
			    if (Main.players.get(playerName) > nextTopWin) {
				nextTop = playerName;
				nextTopWin = Main.players.get(playerName);
			    }
			}
			if (!nextTop.isEmpty()) {
			    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', i + ". " + nextTop
				    + ", &6&l⛃ &a&l" + Main.formatter.format(nextTopWin.longValue())));
			    Main.players.remove(nextTop);
			    nextTop = "";
			    nextTopWin = 0.0;
			}
		    }
		} else {
		    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have permission!"));
		}
	    } else if (args[0].equals("buy")) {
		if (sender.hasPermission("roulette.buy")) {
		    if (args.length == 2) {
			if (RouletteAPI.isNumeric(args[1])) {
			    if ((Main.instance.getConfig().getInt("token-price")
				    * Integer.parseInt(args[1]) < Main.economy.getBalance(player) + 1)) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
					"&8&l[&4&lRoulette&8&l] &aYou have bought &6&l⛃ &6&l" + args[1]
						+ " &achips for &6$" + Main.instance.getConfig().getInt("token-price")
							* Integer.parseInt(args[1])));
				RouletteAPI.addTokenBal(sender.getName(), Integer.parseInt(args[1]));
				Main.economy.withdrawPlayer(player,
					Main.instance.getConfig().getInt("token-price") * Integer.parseInt(args[1]));
			    } else {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
					"&8&l[&4&lRoulette&8&l] &cYou don't have enough money!"));
			    }
			} else {
			    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
				    "&8&l[&4&lRoulette&8&l] &aWrong usage!"));
			    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
				    "&8&l[&4&lRoulette&8&l] &3/roulette buy <amount>"));
			}
		    } else {
			player.sendMessage(
				ChatColor.translateAlternateColorCodes('&', "&8&l[&4&lRoulette&8&l] &cWrong usage!"));
			player.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&8&l[&4&lRoulette&8&l] &3/roulette buy <amount>"));
		    }
		}
	    } else if (args[0].equals("reload")) {
		if (sender.hasPermission("roulette.reload")) {
		    Main.instance.reloadConfig();
		    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
			    "&8&l[&4&lRoulette&8&l] &a&lConfig successfully reloaded."));
		}
	    } else if (args[0].equals("bal")) {
		if (sender.hasPermission("roulette.bal")) {
		    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
			    "&8&l[&4&lRoulette&8&l] &aYour chip balance is &6&l⛃ &6&l"
				    + RouletteAPI.getTokenBal(sender.getName()) + "&a."));
		}
	    }

	    else if (args[0].equals("sell")) {
		if (sender.hasPermission("roulette.sell")) {
		    if (args.length == 2) {
			if (RouletteAPI.isNumeric(args[1])) {
			    if (Integer.parseInt(args[1]) < (RouletteAPI.getTokenBal(player.getName()) + 1)) {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&',
					"&8&l[&4&lRoulette&8&l] &aYou have sold &6&l⛃ &6&l" + args[1]
						+ " &achips for &6$" + Main.instance.getConfig().getInt("token-price")
							* Integer.parseInt(args[1])));
				Main.economy.depositPlayer(player,
					Main.instance.getConfig().getInt("token-price") * Integer.parseInt(args[1]));
				RouletteAPI.removeTokenBal(sender.getName(), Integer.parseInt(args[1]));
			    } else {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&',
					"&8&l[&4&lRoulette&8&l] &cYou don't have enough chips!"));
			    }
			} else {
			    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
				    "&8&l[&4&lRoulette&8&l] &aWrong usage!"));
			    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
				    "&8&l[&4&lRoulette&8&l] &3/roulette sell <amount>"));
			}
		    } else {
			player.sendMessage(
				ChatColor.translateAlternateColorCodes('&', "&8&l[&4&lRoulette&8&l] &cWrong usage!"));
			player.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&8&l[&4&lRoulette&8&l] &3/roulette sell <amount>"));
		    }
		}
	    } else if (args[0].equalsIgnoreCase("help")) {
		if (sender.hasPermission("roulette.help")) {
		    sender.sendMessage(
			    ChatColor.translateAlternateColorCodes('&', "&8-----------------------------------"));
		    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
			    "&6/roulette help <page>&4: List roulette commands."));
		    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
			    "&6/roulette buy <amount>&4: Buy chips for betting on roulette."));
		    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
			    "&6/roulette sell <amount>&4: Sell your chips for money."));
		    sender.sendMessage(
			    ChatColor.translateAlternateColorCodes('&', "&6/roulette bal&4: Show your chip balance."));
		    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
			    "&6/roulette top&4: Show the top player winnings leaderboard."));
		    sender.sendMessage(
			    ChatColor.translateAlternateColorCodes('&', "&6/roulette reload &4: Reload config file."));
		    sender.sendMessage(
			    ChatColor.translateAlternateColorCodes('&', "&8-----------------------------------"));
		    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
			    "&6Type &c/roulette help 2&6 to read the next page."));
		}
	    }
	}
	return true;
    }
}