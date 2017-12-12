package io.github.bagas123.roulette;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.bagas123.roulette.api.HiddenStringUtils;
import io.github.bagas123.roulette.api.RouletteAPI;
import io.github.bagas123.roulette.commands.RouletteCommand;
import io.github.bagas123.roulette.listener.PlayerTalkEvent;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class Main extends JavaPlugin {

    public static HashMap<UUID, String> rolplayers = new HashMap<UUID, String>();
    public static HashMap<UUID, String> rollers = new HashMap<UUID, String>();
    public static HashMap<UUID, Integer> betplayer = new HashMap<UUID, Integer>();

    public static HashMap<UUID, Integer> color = new HashMap<UUID, Integer>();

    public static HashMap<UUID, Integer> betting = new HashMap<UUID, Integer>();

    public static HashMap<String, Double> players = new HashMap<String, Double>();

    public static Main instance;
    private Permission permission = null;
    public static Economy economy = null;
    private Chat chat = null;
    private boolean started = false;
    public static DecimalFormat formatter = new DecimalFormat("#,###");
    RouletteAPI RouletteAPI = new RouletteAPI();
    private int ticks = 0;
    private int tickloop = 0;
    private boolean done = false;
    static boolean onspin = false;
    private boolean showup = false;
    private Short result = 0;
    private File file = new File(getDataFolder(), "leaderboards.yml");
    private FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

    public File balfile = new File(getDataFolder(), "database.yml");
    public FileConfiguration bal = YamlConfiguration.loadConfiguration(balfile);

    private File files;
    private FileConfiguration config;

    String winsound = this.getConfig().getString("win-sound");
    String losesound = this.getConfig().getString("lose-sound");

    private final ItemStack white = createItem(new ItemStack(Material.STAINED_GLASS_PANE), ChatColor.BOLD + "",
	    new String[] {});

    private final ItemStack barrier = createItem(new ItemStack(Material.BARRIER),
	    ChatColor.translateAlternateColorCodes('&', "&cGame has already started. Please wait!"), new String[] {});

    private final ItemStack hopperstarted = createItem(new ItemStack(Material.HOPPER),
	    ChatColor.translateAlternateColorCodes('&', "&f&6&lStatus"), new String[] { "",
		    ChatColor.translateAlternateColorCodes('&', "&cGame has already started. Please wait!") });

    private final ItemStack spectate = createItem(new ItemStack(Material.WATCH),
	    ChatColor.translateAlternateColorCodes('&', "&f&3&lSpectate"),
	    new String[] { "", ChatColor.translateAlternateColorCodes('&', "&7Spectate the game.") });

    private final ItemStack back = createItem(new ItemStack(Material.DARK_OAK_DOOR_ITEM),
	    ChatColor.translateAlternateColorCodes('&', "&f&c&lGo back"),
	    new String[] { "", ChatColor.translateAlternateColorCodes('&', "&7Quit spectating.") });

    @Override
    public void onEnable() {

	saveResource("config.yml", false);
	this.files = new File(getDataFolder(), "config.yml");
	this.config = YamlConfiguration.loadConfiguration(this.files);

	instance = this;
	setupEconomy();
	setupPermissions();
	setupChat();
	Bukkit.getServer().getPluginManager().registerEvents(new RouletteGUI(), this);
	Bukkit.getServer().getPluginManager().registerEvents(new PlayerTalkEvent(), this);
	createDatabase();

	this.getCommand("roulette").setExecutor(new RouletteCommand());

	int minplayers = this.getConfig().getInt("min-players");
	String minbetm = this.getConfig().getString("min-bet-message");
	int minbet = this.getConfig().getInt("min-bet");

	this.getConfig().set("min-players", minplayers);
	this.getConfig().set("min-bet-message", minbetm);
	this.getConfig().set("min-bet", minbet);

	this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
	    public void run() {
		Bukkit.getOnlinePlayers().forEach(player -> {
		    if (betplayer.get(player.getUniqueId()) == null) {
			betplayer.put(player.getUniqueId(), 0);
		    }

		    if (betplayer.get(player.getUniqueId()) < 0) {
			betplayer.put(player.getUniqueId(), 0);
		    }

		    String encoded;
		    if (("Roulette ".length() + HiddenStringUtils.encodeString(player.getName()).length()) > 31) {
			String id = "Roulette " + HiddenStringUtils.encodeString(player.getName());
			encoded = id.substring(0, 23);
		    } else {
			encoded = "Roulette " + HiddenStringUtils.encodeString(player.getName());
		    }

		    if (player.getOpenInventory().getTitle().equals(ChatColor.BOLD + encoded)) {
			ItemStack count = Main.createItem(
				new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal()),
				ChatColor.translateAlternateColorCodes('&', "&f&6&lPlayers"),
				new String[] { "", ChatColor.translateAlternateColorCodes('&',
					"&a" + rollers.size() + "/" + minplayers) });
			player.getOpenInventory().setItem(36, count);

			if (onspin == true) {
			    if (Main.instance.color.containsKey(player.getUniqueId())) {
				player.getOpenInventory().setItem(34, barrier);
				player.getOpenInventory().setItem(4, hopperstarted);
				player.getOpenInventory().setItem(38, spectate);
			    }
			} else {
			    if (Main.instance.color.containsKey(player.getUniqueId())) {
				player.getOpenInventory().setItem(4, hopperstart);
				if (Main.instance.color.get(player.getUniqueId()) == 14) {
				    ItemStack bet = Main.createItem(
					    new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5),
					    ChatColor.translateAlternateColorCodes('&', "&a&lPush bet"),
					    new String[] { "",
						    ChatColor.translateAlternateColorCodes('&',
							    "&f&m------&8&lINFO&f&m------"),
						    ChatColor.translateAlternateColorCodes('&',
							    "&7Betting on &c&lRED."),
						    ChatColor.translateAlternateColorCodes('&', "&7Bet : &6&l⛃ "
							    + Main.betplayer.get(player.getUniqueId())) });

				    ItemStack book = Main.createItem(new ItemStack(Material.BOOK),
					    ChatColor.translateAlternateColorCodes('&', "&6&lInfo"),
					    new String[] { "",
						    ChatColor.translateAlternateColorCodes('&',
							    "&7Betting on &c&lRED."),
						    ChatColor.translateAlternateColorCodes('&', "&7Bet : &6&l⛃ "
							    + Main.betplayer.get(player.getUniqueId())) });

				    EnchantGlow.addGlow(book);
				    EnchantGlow.addGlow(bet);

				    player.getOpenInventory().setItem(37, book);
				    player.getOpenInventory().setItem(34, bet);
				} else if (Main.color.get(player.getUniqueId()) == 15) {
				    ItemStack bet = Main.createItem(
					    new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5),
					    ChatColor.translateAlternateColorCodes('&', "&a&lPush bet"),
					    new String[] { "",
						    ChatColor.translateAlternateColorCodes('&',
							    "&f&m------&8&lINFO&f&m------"),
						    ChatColor.translateAlternateColorCodes('&',
							    "&7Betting on &8&lBLACK."),
						    ChatColor.translateAlternateColorCodes('&', "&7Bet : &6&l⛃ "
							    + Main.betplayer.get(player.getUniqueId())) });

				    ItemStack book = Main.createItem(new ItemStack(Material.BOOK),
					    ChatColor.translateAlternateColorCodes('&', "&6&lInfo"),
					    new String[] { "",
						    ChatColor.translateAlternateColorCodes('&',
							    "&7Betting on &8&lBLACK."),
						    ChatColor.translateAlternateColorCodes('&', "&7Bet : &6&l⛃ "
							    + Main.betplayer.get(player.getUniqueId())) });

				    EnchantGlow.addGlow(book);
				    EnchantGlow.addGlow(bet);

				    player.getOpenInventory().setItem(37, book);
				    player.getOpenInventory().setItem(34, bet);
				} else if (Main.instance.color.get(player.getUniqueId()) == 5) {
				    ItemStack bet = Main.createItem(
					    new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5),
					    ChatColor.translateAlternateColorCodes('&', "&a&lPush bet"),
					    new String[] { "",
						    ChatColor.translateAlternateColorCodes('&',
							    "&f&m------&8&lINFO&f&m------"),
						    ChatColor.translateAlternateColorCodes('&',
							    "&7Betting on &a&lGREEN."),
						    ChatColor.translateAlternateColorCodes('&', "&7Bet : &6&l⛃ "
							    + Main.betplayer.get(player.getUniqueId())) });

				    ItemStack book = Main.createItem(new ItemStack(Material.BOOK),
					    ChatColor.translateAlternateColorCodes('&', "&6&lInfo"),
					    new String[] { "",
						    ChatColor.translateAlternateColorCodes('&',
							    "&7Betting on &a&lGREEN."),
						    ChatColor.translateAlternateColorCodes('&', "&7Bet : &6&l⛃ "
							    + Main.betplayer.get(player.getUniqueId())) });

				    EnchantGlow.addGlow(book);
				    EnchantGlow.addGlow(bet);

				    player.getOpenInventory().setItem(37, book);
				    player.getOpenInventory().setItem(34, bet);
				}
			    }
			}
		    }

		    if (player.getOpenInventory().getTitle().equals(ChatColor.BOLD + "Roulette")) {
			ItemStack count = Main.createItem(
				new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal()),
				ChatColor.translateAlternateColorCodes('&', "&f&6&lPlayers"),
				new String[] { "", ChatColor.translateAlternateColorCodes('&',
					"&a" + rollers.size() + "/" + minplayers) });
			player.getOpenInventory().setItem(36, count);
		    }

		});

		if (rollers.size() < (minplayers - 1)) {
		    RouletteGUI.rouletteGUI.setItem(4, hopper);
		}

		if (onspin == false && rollers.size() > 0) {
		    for (int itemstacks = 9; itemstacks < 18; itemstacks++)
			RouletteGUI.rouletteGUI.setItem(itemstacks,
				RouletteGUI.items[(itemstacks + RouletteGUI.itemIndex - 1)
					% RouletteGUI.items.length]);
		    RouletteGUI.rouletteGUI.setItem(37, white);
		}

		if (showup == true) {
		    for (UUID players : rollers.keySet()) {
			Player player = Bukkit.getPlayer(players);
			player.openInventory(RouletteGUI.rouletteGUI);
			showup = false;
		    }
		}

		if (rollers.size() > (minplayers - 1) && started == false) {
		    onspin = true;
		    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.this, new Runnable() {
			@Override
			public void run() {
			    if (done)
				return;
			    ticks++;
			    if (ticks == 1) {
				RouletteGUI.rouletteGUI.setItem(37, white);
				EnchantGlow.addGlow(hoppermoving);
				RouletteGUI.rouletteGUI.setItem(4, hoppermoving);
				tickloop = 0;
				showup = true;
			    }

			    if (ticks == 4) {
				shuffle();
				shuffle();
				shuffle();
			    }

			    if (ticks > 2 && ticks < 100) {
				tickloop++;
				if (tickloop == 2) {
				    for (int itemstacks = 9; itemstacks < 18; itemstacks++)
					RouletteGUI.rouletteGUI.setItem(itemstacks,
						RouletteGUI.items[(itemstacks + RouletteGUI.itemIndex)
							% RouletteGUI.items.length]);

				    RouletteGUI.itemIndex++;
				    tickloop = 0;
				}
			    }

			    if (ticks == 100) {
				tickloop = 0;
			    }

			    if (ticks > 100 && ticks < 140) {
				tickloop++;
				if (tickloop == 3) {
				    for (int itemstacks = 9; itemstacks < 18; itemstacks++)
					RouletteGUI.rouletteGUI.setItem(itemstacks,
						RouletteGUI.items[(itemstacks + RouletteGUI.itemIndex)
							% RouletteGUI.items.length]);

				    RouletteGUI.itemIndex++;
				    tickloop = 0;
				}
			    }

			    if (ticks == 140) {
				tickloop = 0;
			    }

			    if (ticks > 140 && ticks < 190) {
				tickloop++;
				if (tickloop == 6) {
				    for (int itemstacks = 9; itemstacks < 18; itemstacks++)
					RouletteGUI.rouletteGUI.setItem(itemstacks,
						RouletteGUI.items[(itemstacks + RouletteGUI.itemIndex)
							% RouletteGUI.items.length]);

				    RouletteGUI.itemIndex++;
				    tickloop = 0;
				}
			    }

			    if (ticks == 250) {
				tickloop = 0;
			    }

			    if (ticks > 190 && ticks < 300) {
				tickloop++;
				if (tickloop == 8) {
				    for (int itemstacks = 9; itemstacks < 18; itemstacks++)
					RouletteGUI.rouletteGUI.setItem(itemstacks,
						RouletteGUI.items[(itemstacks + RouletteGUI.itemIndex)
							% RouletteGUI.items.length]);

				    RouletteGUI.itemIndex++;
				    tickloop = 0;
				}
			    }

			    if (ticks == 300) {
				tickloop = 0;
			    }

			    if (ticks > 300 && ticks < 480) {
				tickloop++;
				if (tickloop == 12) {
				    for (int itemstacks = 9; itemstacks < 18; itemstacks++)
					RouletteGUI.rouletteGUI.setItem(itemstacks,
						RouletteGUI.items[(itemstacks + RouletteGUI.itemIndex)
							% RouletteGUI.items.length]);

				    RouletteGUI.itemIndex++;
				    tickloop = 0;
				}
			    }

			    if (ticks == 480) {
				tickloop = 0;
			    }

			    if (ticks > 480 && ticks < 500) {
				tickloop++;
				if (tickloop == 15) {
				    for (int itemstacks = 9; itemstacks < 18; itemstacks++)
					RouletteGUI.rouletteGUI.setItem(itemstacks,
						RouletteGUI.items[(itemstacks + RouletteGUI.itemIndex)
							% RouletteGUI.items.length]);

				    RouletteGUI.itemIndex++;
				    tickloop = 0;
				}
			    }

			    if (ticks == 500) {
				tickloop = 0;
			    }

			    if (ticks > 500 && ticks < 560) {
				tickloop++;
				if (tickloop == 19) {
				    for (int itemstacks = 9; itemstacks < 18; itemstacks++)
					RouletteGUI.rouletteGUI.setItem(itemstacks,
						RouletteGUI.items[(itemstacks + RouletteGUI.itemIndex)
							% RouletteGUI.items.length]);

				    RouletteGUI.itemIndex++;
				    tickloop = 0;
				}
			    }

			    if (ticks == 560) {
				tickloop = 0;
			    }

			    if (ticks > 560 && ticks < 600) {
				tickloop++;
				if (tickloop == 22) {
				    for (int itemstacks = 9; itemstacks < 18; itemstacks++)
					RouletteGUI.rouletteGUI.setItem(itemstacks,
						RouletteGUI.items[(itemstacks + RouletteGUI.itemIndex)
							% RouletteGUI.items.length]);

				    RouletteGUI.itemIndex++;
				    tickloop = 0;
				}
			    }

			    if (ticks == 610) {
				tickloop = 0;
			    }

			    if (ticks == 610) {
				for (int itemstacks = 9; itemstacks < 18; itemstacks++)
				    RouletteGUI.rouletteGUI.setItem(itemstacks,
					    RouletteGUI.items[(itemstacks + RouletteGUI.itemIndex)
						    % RouletteGUI.items.length]);

				RouletteGUI.itemIndex++;

				done = true;
				started = true;
				result = RouletteGUI.rouletteGUI.getItem(13).getDurability();
				RouletteGUI.rouletteGUI.setItem(37, back);
				if (result == 15) {
				    ItemStack hopperresult = createItem(new ItemStack(Material.HOPPER),
					    ChatColor.translateAlternateColorCodes('&', "&f&6&lStatus"),
					    new String[] { "", ChatColor.translateAlternateColorCodes('&',
						    "&6Result: &7&lBLACK") });

				    RouletteGUI.rouletteGUI.setItem(4, hopperresult);
				}
				if (result == 5) {
				    ItemStack hopperresult = createItem(new ItemStack(Material.HOPPER),
					    ChatColor.translateAlternateColorCodes('&', "&f&6&lStatus"),
					    new String[] { "", ChatColor.translateAlternateColorCodes('&',
						    "&6Result: &a&lGREEN") });

				    RouletteGUI.rouletteGUI.setItem(4, hopperresult);
				}
				if (result == 14) {
				    ItemStack hopperresult = createItem(new ItemStack(Material.HOPPER),
					    ChatColor.translateAlternateColorCodes('&', "&f&6&lStatus"),
					    new String[] { "",
						    ChatColor.translateAlternateColorCodes('&', "&6Result: &c&lRED") });

				    RouletteGUI.rouletteGUI.setItem(4, hopperresult);
				}
			    }
			}
		    }, 0L);
		}

		if (result == 15) {
		    for (Player broadcast : Bukkit.getOnlinePlayers()) {
			broadcast.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&8[&4&lRoulette&8] &6The roulette ball landed on &8&lBLACK&6."));
		    }

		    for (UUID winners : getKeysByValue(rollers, "15")) {
			Player p = Bukkit.getPlayer(winners);
			p.playSound(p.getLocation(),
				Sound.valueOf(Bukkit.getVersion().contains("1.8") ? "LEVEL_UP" : winsound), 1, 1);
			Player priz = Bukkit.getPlayer(winners);
			Integer prize = betplayer.get(winners);
			RouletteAPI.addTokenBal(p.getName(), prize * 2);
			priz.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&8[&4&lRoulette&8] &aYou have won at the roulette! &6&l⛃ &6&l"
					+ formatter.format(prize * 2) + " &afor winning, congratulations!"));
			if (!cfg.contains("players." + priz.getName() + ".winnings")) {
			    cfg.set("players." + priz.getName() + ".winnings", prize * 2);
			    try {
				cfg.save(file);
			    } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			    }
			} else {
			    if (cfg.getDouble("players." + priz.getName() + ".winnings") < prize * 2) {
				cfg.set("players." + priz.getName() + ".winnings", prize * 2);
				try {
				    cfg.save(file);
				} catch (IOException e) {
				    // TODO Auto-generated catch block
				    e.printStackTrace();
				}
			    } else {

			    }
			}
			rollers.remove(p.getUniqueId());
			betplayer.remove(p.getUniqueId());
		    }

		    for (UUID winners : getKeysByValue(rollers, "14")) {
			Player p = Bukkit.getPlayer(winners);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&8[&4&lRoulette&8] &cYou lost at the roulette."));
			p.playSound(p.getLocation(),
				Sound.valueOf(Bukkit.getVersion().contains("1.8") ? "ANVIL_LAND" : losesound), 1, 1);
			rollers.remove(p.getUniqueId());
			betplayer.remove(p.getUniqueId());
		    }

		    for (UUID winners : getKeysByValue(rollers, "5")) {
			Player p = Bukkit.getPlayer(winners);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&8[&4&lRoulette&8] &cYou lost at the roulette."));
			p.playSound(p.getLocation(),
				Sound.valueOf(Bukkit.getVersion().contains("1.8") ? "ANVIL_LAND" : losesound), 1, 1);
			rollers.remove(p.getUniqueId());
			betplayer.remove(p.getUniqueId());
		    }
		    rolplayers.clear();
		    onspin = false;
		    started = false;
		    done = false;
		    ticks = 0;
		    result = 0;
		}

		if (result == 14) {
		    for (Player broadcast : Bukkit.getOnlinePlayers()) {
			broadcast.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&8[&4&lRoulette&8] &6The roulette ball landed on &c&lRED&6."));
		    }
		    for (UUID winners : getKeysByValue(rollers, "14")) {
			Player p = Bukkit.getPlayer(winners);
			p.playSound(p.getLocation(),
				Sound.valueOf(Bukkit.getVersion().contains("1.8") ? "LEVEL_UP" : winsound), 1, 1);
			Player priz = Bukkit.getPlayer(winners);
			Integer prize = betplayer.get(winners);
			RouletteAPI.addTokenBal(p.getName(), prize * 2);
			priz.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&8[&4&lRoulette&8] &aYou have won at the roulette! &6&l⛃ &6&l"
					+ formatter.format(prize * 2) + " &afor winning, congratulations!"));
			if (!cfg.contains("players." + priz.getName() + ".winnings")) {
			    cfg.set("players." + priz.getName() + ".winnings", prize * 2);
			    try {
				cfg.save(file);
			    } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			    }
			} else {
			    if (cfg.getDouble("players." + priz.getName() + ".winnings") < prize * 2) {
				cfg.set("players." + priz.getName() + ".winnings", prize * 2);
				try {
				    cfg.save(file);
				} catch (IOException e) {
				    // TODO Auto-generated catch block
				    e.printStackTrace();
				}
			    } else {

			    }
			}
			rollers.remove(p.getUniqueId());
			betplayer.remove(p.getUniqueId());
		    }
		    for (UUID winners : getKeysByValue(rollers, "15")) {
			Player p = Bukkit.getPlayer(winners);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&8[&4&lRoulette&8] &cYou lost at the roulette."));
			p.playSound(p.getLocation(),
				Sound.valueOf(Bukkit.getVersion().contains("1.8") ? "ANVIL_LAND" : losesound), 1, 1);
			rollers.remove(p.getUniqueId());
			betplayer.remove(p.getUniqueId());
		    }

		    for (UUID winners : getKeysByValue(rollers, "5")) {
			Player p = Bukkit.getPlayer(winners);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&8[&4&lRoulette&8] &cYou lost at the roulette."));
			p.playSound(p.getLocation(),
				Sound.valueOf(Bukkit.getVersion().contains("1.8") ? "ANVIL_LAND" : losesound), 1, 1);
			rollers.remove(p.getUniqueId());
			betplayer.remove(p.getUniqueId());
		    }
		    rolplayers.clear();
		    onspin = false;
		    started = false;
		    done = false;
		    ticks = 0;
		    result = 0;
		}

		if (result == 5) {
		    for (Player broadcast : Bukkit.getOnlinePlayers()) {
			broadcast.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&8[&4&lRoulette&8] &6The roulette ball landed on &a&lGREEN&6."));
		    }
		    for (UUID winners : getKeysByValue(rollers, "5")) {
			Player p = Bukkit.getPlayer(winners);

			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lYou won the roulette!"));
			p.playSound(p.getLocation(),
				Sound.valueOf(Bukkit.getVersion().contains("1.8") ? "LEVEL_UP" : winsound), 1, 1);
			Player priz = Bukkit.getPlayer(winners);
			Integer prize = betplayer.get(winners);
			RouletteAPI.addTokenBal(p.getName(), prize * 4);
			priz.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&8[&4&lRoulette&8] &aYou have won at the roulette! &6&l⛃ &6&l"
					+ formatter.format(prize * 4) + " &afor winning, congratulations!"));
			if (!cfg.contains("players." + priz.getName() + ".winnings")) {
			    cfg.set("players." + priz.getName() + ".winnings", prize * 4);
			    try {
				cfg.save(file);
			    } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			    }
			} else {
			    if (cfg.getDouble("players." + priz.getName() + ".winnings") < prize * 4) {
				cfg.set("players." + priz.getName() + ".winnings", prize * 4);
				try {
				    cfg.save(file);
				} catch (IOException e) {
				    // TODO Auto-generated catch block
				    e.printStackTrace();
				}
			    } else {

			    }
			}
			rollers.remove(p.getUniqueId());
			betplayer.remove(p.getUniqueId());
		    }
		    for (UUID winners : getKeysByValue(rollers, "14")) {
			Player p = Bukkit.getPlayer(winners);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&8[&4&lRoulette&8] &cYou lost at the roulette."));
			p.playSound(p.getLocation(),
				Sound.valueOf(Bukkit.getVersion().contains("1.8") ? "ANVIL_LAND" : losesound), 1, 1);
			rollers.remove(p.getUniqueId());
			betplayer.remove(p.getUniqueId());
		    }

		    for (UUID winners : getKeysByValue(rollers, "15")) {
			Player p = Bukkit.getPlayer(winners);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&8[&4&lRoulette&8] &cYou lost at the roulette."));
			p.playSound(p.getLocation(),
				Sound.valueOf(Bukkit.getVersion().contains("1.8") ? "ANVIL_LAND" : losesound), 1, 1);
			rollers.remove(p.getUniqueId());
			betplayer.remove(p.getUniqueId());
		    }
		    rolplayers.clear();
		    onspin = false;
		    started = false;
		    done = false;
		    ticks = 0;
		    result = 0;
		}
	    }
	}, 0, 0);
    }

    @Override
    public void onDisable() {

    }

    public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
	Set<T> keys = new HashSet<T>();
	for (Entry<T, E> entry : map.entrySet()) {
	    if (Objects.equals(value, entry.getValue())) {
		keys.add(entry.getKey());
	    }
	}
	return keys;
    }

    public void shuffle() {
	int startingIndex = ThreadLocalRandom.current().nextInt(RouletteGUI.items.length);
	for (int index = 0; index < startingIndex; index++) {
	    for (int itemstacks = 9; itemstacks < 18; itemstacks++) {
		RouletteGUI.rouletteGUI.setItem(itemstacks,
			RouletteGUI.items[(itemstacks + RouletteGUI.itemIndex) % RouletteGUI.items.length]);
	    }
	    RouletteGUI.itemIndex++;
	}
    }

    private boolean setupPermissions() {
	RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager()
		.getRegistration(net.milkbowl.vault.permission.Permission.class);
	if (permissionProvider != null) {
	    permission = permissionProvider.getProvider();
	}
	return (permission != null);
    }

    private boolean setupChat() {
	RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager()
		.getRegistration(net.milkbowl.vault.chat.Chat.class);
	if (chatProvider != null) {
	    chat = chatProvider.getProvider();
	}

	return (chat != null);
    }

    private boolean setupEconomy() {
	RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
		.getRegistration(net.milkbowl.vault.economy.Economy.class);
	if (economyProvider != null) {
	    economy = economyProvider.getProvider();
	}

	return (economy != null);
    }

    Random rand = new Random();

    public static ItemStack createItem(ItemStack item, String name, String[] lore) {
	ItemMeta im = item.getItemMeta();
	im.setDisplayName(name);
	im.setLore(Arrays.asList(lore));
	item.setItemMeta(im);
	return item;
    }

    ItemStack hopper = createItem(new ItemStack(Material.HOPPER),
	    ChatColor.translateAlternateColorCodes('&', "&f&6&lStatus"), new String[] { "",
		    ChatColor.translateAlternateColorCodes('&', "&cNot enough players to start roulette!") });

    ItemStack hopperstart = createItem(new ItemStack(Material.HOPPER),
	    ChatColor.translateAlternateColorCodes('&', "&f&6&lStatus"),
	    new String[] { "", ChatColor.translateAlternateColorCodes('&', "&aPress &6&lPush bet &ato join in.") });

    static ItemStack hoppermoving = createItem(new ItemStack(Material.HOPPER),
	    ChatColor.translateAlternateColorCodes('&', "&f&6&lStatus"),
	    new String[] { "", ChatColor.translateAlternateColorCodes('&', "&cSpinning..") });

    public static int getMinBet() {
	int minbet = instance.getConfig().getInt("min-bet");
	return minbet;
    }

    public void createDatabase() {
	File locations = new File(getDataFolder(), "leaderboards.yml");
	if (!locations.exists()) {
	    try {
		locations.createNewFile();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	    FileConfiguration loc = YamlConfiguration.loadConfiguration(locations);
	    try {
		loc.save(locations);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }

    public static boolean isNumeric(String str) {
	return str.matches("-?\\d+(\\.\\d+)?");
    }

}
