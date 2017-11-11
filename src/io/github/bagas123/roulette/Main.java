package io.github.bagas123.roulette;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
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
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class Main extends JavaPlugin {
	
	public static HashMap<UUID, String> rolplayers = new HashMap<UUID, String>();
	public static HashMap<UUID, String> rollers = new HashMap<UUID, String>();
	public static HashMap<UUID, Integer> betplayer = new HashMap<UUID, Integer>();
	public static Main instance;
	public static Permission permission = null;
    public static Economy economy = null;
    public static Chat chat = null;
    boolean started = false;
    DecimalFormat formatter = new DecimalFormat("#,###.00");
	int seconds = 10;
    double delay = 0;
    int ticks = 0;
    int tickloop = 0;
    boolean done = false;
    public static boolean onspin = false;
    boolean showup = false;
    Short result = 0;
    File file = new File(getDataFolder(), "leaderboards.yml");
    FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
    HashMap<String, Double> players = new HashMap<String, Double>();
    Double maxWin=0.0;
    String maxWinPlayer = "";
    
    private File files;
    private FileConfiguration config;
    
    String winsound = this.getConfig().getString("win-sound");
    String losesound = this.getConfig().getString("lose-sound");
    String addbetsound = this.getConfig().getString("addbetpush-sound");
    String addbetbalspund = this.getConfig().getString("addbet-sound");
    String resetbetsound = this.getConfig().getString("resetbet-sound");
    String lostmessage = this.getConfig().getString("lost-message");


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
		createDatabase();

		int minplayers = this.getConfig().getInt("min-players");
		String minbetm = this.getConfig().getString("min-bet-message");
		int minbet = this.getConfig().getInt("min-bet");
		
		this.getConfig().set("min-players", minplayers);
		this.getConfig().set("min-bet-message", minbetm);
		this.getConfig().set("min-bet", minbet);
		
		
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
	        public void run() {
	        	Bukkit.getOnlinePlayers().forEach( player -> {
	            if(betplayer.get(player.getUniqueId()) == null) {
	            	betplayer.put(player.getUniqueId(), 0);
	            }
	            
	            if(betplayer.get(player.getUniqueId()) < 0) {
	            	betplayer.put(player.getUniqueId(), 0);
	            }
	            
	  		});
	        	
	        	if(rollers.size() < (minplayers - 1)) {
	        		RouletteGUI.rouletteGUI.setItem(4, hopper);
	        	}
	        	
	        	if(showup == true) {
	        		for(UUID players : rollers.keySet()) {
	        			 Player player = Bukkit.getPlayer(players);
	        			 player.openInventory(RouletteGUI.rouletteGUI);
	        			 showup = false;
	        		}
	        	}
	        	
	        	if(rollers.size() > (minplayers - 1) && started == false) {
	        		onspin = true;
		                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.this,new Runnable(){
		        	            @SuppressWarnings("deprecation")
								@Override
		        	            public void run() {
		        	              if (done)
		        	                return;
		        	              ticks++;
		        	              if(ticks == 1) {
			        	              EnchantGlow.addGlow(hoppermoving);
			        	              RouletteGUI.rouletteGUI.setItem(4, hoppermoving);
			        	              tickloop = 0;
		        	            	  showup = true;
		        	              }
		        	              
		        	              if(ticks == 4) {shuffle();}
		        	              
		        	              if (ticks > 2 && ticks < 100) {
		        	            	  tickloop++;
		        	            	  if(tickloop == 2) {
				        	                for (int itemstacks = 9; itemstacks < 18; itemstacks++)
				        	                	RouletteGUI.rouletteGUI.setItem(itemstacks,
				        	                			RouletteGUI.items[(itemstacks + RouletteGUI.itemIndex)
				        	                          % RouletteGUI.items.length]);
				        	                
				        	                RouletteGUI.itemIndex++;
				        	                tickloop = 0;
		        	            	  }
		        	              }
		        	              
		        	              if(ticks == 100) {
		        	            	  tickloop = 0;
		        	              }
		        	              
		        	              if(ticks > 100 && ticks < 160) {
		        	            	  tickloop++;
		        	            	  if(tickloop == 3) {
				        	                for (int itemstacks = 9; itemstacks < 18; itemstacks++)
				        	                	RouletteGUI.rouletteGUI.setItem(itemstacks,
				        	                			RouletteGUI.items[(itemstacks + RouletteGUI.itemIndex)
				        	                          % RouletteGUI.items.length]);
				        	                
				        	                RouletteGUI.itemIndex++;
				        	                tickloop = 0;
		        	            	  }
		        	              }
		        	              
		        	              if(ticks == 160) {
		        	            	  tickloop = 0;
		        	              }
		        	              
		        	              if(ticks > 160 && ticks < 250) {
		        	            	  tickloop++;
		        	            	  if(tickloop == 5) {
				        	                for (int itemstacks = 9; itemstacks < 18; itemstacks++)
				        	                	RouletteGUI.rouletteGUI.setItem(itemstacks,
				        	                			RouletteGUI.items[(itemstacks + RouletteGUI.itemIndex)
				        	                          % RouletteGUI.items.length]);
				        	                
				        	                RouletteGUI.itemIndex++;
				        	                tickloop = 0;
		        	            	  }
		        	              }
		        	              
		        	              if(ticks == 250) {
		        	            	  tickloop = 0;
		        	              }
		        	              
		        	              if(ticks > 250 && ticks < 300) {
		        	            	  tickloop++;
		        	            	  if(tickloop == 8) {
				        	                for (int itemstacks = 9; itemstacks < 18; itemstacks++)
				        	                	RouletteGUI.rouletteGUI.setItem(itemstacks,
				        	                			RouletteGUI.items[(itemstacks + RouletteGUI.itemIndex)
				        	                          % RouletteGUI.items.length]);
				        	                
				        	                RouletteGUI.itemIndex++;
				        	                tickloop = 0;
		        	            	  }
		        	              }
		        	              
		        	              if(ticks == 300) {
		        	            	  tickloop = 0;
		        	              }
		        	              
		        	              if(ticks > 300 && ticks < 480) {
		        	            	  tickloop++;
		        	            	  if(tickloop == 14) {
				        	                for (int itemstacks = 9; itemstacks < 18; itemstacks++)
				        	                	RouletteGUI.rouletteGUI.setItem(itemstacks,
				        	                			RouletteGUI.items[(itemstacks + RouletteGUI.itemIndex)
				        	                          % RouletteGUI.items.length]);
				        	                
				        	                RouletteGUI.itemIndex++;
				        	                tickloop = 0;
		        	            	  }
		        	              }
		        	              
		        	              if(ticks == 480) {
		        	            	  tickloop = 0;
		        	              }
		        	              
		        	              if(ticks > 480 && ticks < 500) {
		        	            	  tickloop++;
		        	            	  if(tickloop == 20) {
				        	                for (int itemstacks = 9; itemstacks < 18; itemstacks++)
				        	                	RouletteGUI.rouletteGUI.setItem(itemstacks,
				        	                			RouletteGUI.items[(itemstacks + RouletteGUI.itemIndex)
				        	                          % RouletteGUI.items.length]);
				        	                
				        	                RouletteGUI.itemIndex++;
				        	                tickloop = 0;
		        	            	  }
		        	              }
		        	              
		        	              if(ticks == 500) {
		        	            	  tickloop = 0;
		        	              }
		        	              
		        	              if(ticks > 500 && ticks < 600) {
		        	            	  tickloop++;
		        	            	  if(tickloop == 25) {
				        	                for (int itemstacks = 9; itemstacks < 18; itemstacks++)
				        	                	RouletteGUI.rouletteGUI.setItem(itemstacks,
				        	                			RouletteGUI.items[(itemstacks + RouletteGUI.itemIndex)
				        	                          % RouletteGUI.items.length]);
				        	                
				        	                RouletteGUI.itemIndex++;
				        	                tickloop = 0;
		        	            	  }
		        	              }
		        	              
		        	              if(ticks == 600) {
		        	            	  tickloop = 0;
		        	              }
		        	              		        	              
		        	              if(ticks > 600 && ticks < 650) {
		        	            	  tickloop++;
		        	            	  if(tickloop == 29) {
				        	                for (int itemstacks = 9; itemstacks < 18; itemstacks++)
				        	                	RouletteGUI.rouletteGUI.setItem(itemstacks,
				        	                			RouletteGUI.items[(itemstacks + RouletteGUI.itemIndex)
				        	                          % RouletteGUI.items.length]);
				        	                
				        	                RouletteGUI.itemIndex++;
				        	                tickloop = 0;
		        	            	  }
		        	              }
		        	              
		        	              if(ticks == 650) {
		        	            	  tickloop = 0;
		        	              }
		        	               
		        	               if(ticks == 650) {
		            	                for (int itemstacks = 9; itemstacks < 18; itemstacks++)
		            	                	RouletteGUI.rouletteGUI.setItem(itemstacks,
		            	                			RouletteGUI.items[(itemstacks + RouletteGUI.itemIndex)
		            	                          % RouletteGUI.items.length]);
		            	                
		            	               RouletteGUI.itemIndex++;
		            	                
		        	            	   done = true;
		          	            	   started = true;
		          	            	   result = RouletteGUI.rouletteGUI.getItem(13).getDurability();
		          	            	   if(result == 15) {
		        	            	    ItemStack hopperresult = createItem(new ItemStack(Material.HOPPER), ChatColor.translateAlternateColorCodes('&', "&f&6&lStatus"), new String[] {
		        	            	    		"",
		        	            	    		ChatColor.translateAlternateColorCodes('&', "&6&lResult: &7&lBlack")
		        	            	    });
		        	            	    
		        	            	    RouletteGUI.rouletteGUI.setItem(4, hopperresult);
		          	            	   }
		          	            	   if(result == 5) {
		        	            	    ItemStack hopperresult = createItem(new ItemStack(Material.HOPPER), ChatColor.translateAlternateColorCodes('&', "&f&6&lStatus"), new String[] {
		        	            	    		"",
		        	            	    		ChatColor.translateAlternateColorCodes('&', "&6&lResult: &a&lGreen")
		        	            	    });
		        	            	    
		        	            	    RouletteGUI.rouletteGUI.setItem(4, hopperresult);
		          	            	   }
		          	            	   if(result == 14) {
		        	            	    ItemStack hopperresult = createItem(new ItemStack(Material.HOPPER), ChatColor.translateAlternateColorCodes('&', "&f&6&lStatus"), new String[] {
		        	            	    		"",
		        	            	    		ChatColor.translateAlternateColorCodes('&', "&6&lResult: &c&lRed")
		        	            	    });
		        	            	    
		        	            	    RouletteGUI.rouletteGUI.setItem(4, hopperresult);
		          	            	   }
		          	               }
		        	            }
		        	          }, 0L);
	        	}
	        	
	        	if(result == 15) {
	        		for(UUID winners : getKeysByValue(rollers,"15")) {
	        			Player p = Bukkit.getPlayer(winners);
	        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&m-----------------------------"));
	        			p.sendMessage("");
	        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lYou won the roulette!"));
	        			p.playSound(p.getLocation(), Sound.valueOf(winsound), 1, 1);
	        				Player priz = Bukkit.getPlayer(winners);
	        				Integer prize = betplayer.get(winners);
	        				economy.depositPlayer(priz, prize * 1.5);
	        				priz.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l⛃ &a&l" + formatter.format(prize * 1.5) + " &6&lfor winning the roulette!"));
	        				if(!cfg.contains("players." + priz.getName() + ".winnings")) {
		        				cfg.set("players." + priz.getName() + ".winnings", prize * 1.5);
		        				try {
									cfg.save(file);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
	        				} else {
	        					if(cfg.getDouble("players." + priz.getName() + ".winnings") < prize * 1.5) {
			        				cfg.set("players." + priz.getName() + ".winnings", prize * 1.5);
			        				try {
										cfg.save(file);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
	        					} else {
	        						
	        					}
	        				}
	        			p.sendMessage("");
	        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&m-----------------------------"));
	        		}
	        		
	        		for(UUID winners : getKeysByValue(rollers,"14")) {
	        			Player p = Bukkit.getPlayer(winners);
	        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&m-----------------------------"));
	        			p.sendMessage("");
	        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', lostmessage));
	        			p.playSound(p.getLocation(), Sound.valueOf(losesound), 1, 1);
	        			p.sendMessage("");
	        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&m-----------------------------"));
	        		}
	        		
	        		for(UUID winners : getKeysByValue(rollers,"5")) {
	        			Player p = Bukkit.getPlayer(winners);
	        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&m-----------------------------"));
	        			p.sendMessage("");
	        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', lostmessage));
	        			p.playSound(p.getLocation(), Sound.valueOf(losesound), 1, 1);
	        			p.sendMessage("");
	        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&m-----------------------------"));
	        		}
        			rollers.clear();
        			betplayer.clear();
        			rolplayers.clear();
        			onspin = false;
        			started = false;
        			done = false;
        			ticks = 0;
        			result = 0;
	        	}
	        	
	        	if(result == 14) {
	        		for(UUID winners : getKeysByValue(rollers,"14")) {
	        			Player p = Bukkit.getPlayer(winners);
	        			
	        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&m-----------------------------"));
	        			p.sendMessage("");
	        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lYou won the roulette!"));
	        			p.playSound(p.getLocation(), Sound.valueOf(winsound), 1, 1);
	        				Player priz = Bukkit.getPlayer(winners);
	        				Integer prize = betplayer.get(winners);
	        				economy.depositPlayer(priz, prize * 1.5);
	        				priz.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l⛃ &a&l" + formatter.format(prize * 1.5) + " &6&lfor winning the roulette!"));
	        				if(!cfg.contains("players." + priz.getName() + ".winnings")) {
		        				cfg.set("players." + priz.getName() + ".winnings", prize * 1.5);
		        				try {
									cfg.save(file);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
	        				} else {
	        					if(cfg.getDouble("players." + priz.getName() + ".winnings") < prize * 1.5) {
			        				cfg.set("players." + priz.getName() + ".winnings", prize * 1.5);
			        				try {
										cfg.save(file);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
	        					} else {
	        						
	        					}
	        				}
	        			p.sendMessage("");
	        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&m-----------------------------"));
	        		}
	        		for(UUID winners : getKeysByValue(rollers,"15")) {
	        			Player p = Bukkit.getPlayer(winners);
	        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&m-----------------------------"));
	        			p.sendMessage("");
	        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', lostmessage));
	        			p.playSound(p.getLocation(), Sound.valueOf(losesound), 1, 1);
	        			p.sendMessage("");
	        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&m-----------------------------"));
	        		}
	        		
	        		for(UUID winners : getKeysByValue(rollers,"5")) {
	        			Player p = Bukkit.getPlayer(winners);
	        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&m-----------------------------"));
	        			p.sendMessage("");
	        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', lostmessage));
	        			p.playSound(p.getLocation(), Sound.valueOf(losesound), 1, 1);
	        			p.sendMessage("");
	        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&m-----------------------------"));
	        		}
        			rollers.clear();
        			betplayer.clear();
        			rolplayers.clear();
        			onspin = false;
        			started = false;
        			done = false;
        			ticks = 0;
        			result = 0;
	        	}
	        	
	        	if(result == 5) {
	        		for(UUID winners : getKeysByValue(rollers,"5")) {
	        			Player p = Bukkit.getPlayer(winners);
	        			
	        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&m-----------------------------"));
	        			p.sendMessage("");
	        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lYou won the roulette!"));
	        			p.playSound(p.getLocation(), Sound.valueOf(winsound), 1, 1);
	        				Player priz = Bukkit.getPlayer(winners);
	        				Integer prize = betplayer.get(winners);
	        				economy.depositPlayer(priz, prize * 4.0);
	        				priz.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l⛃ &a&l" + formatter.format(prize * 4.0) + " &6&lfor winning the roulette!"));
	        				if(!cfg.contains("players." + priz.getName() + ".winnings")) {
		        				cfg.set("players." + priz.getName() + ".winnings", prize * 4.0);
		        				try {
									cfg.save(file);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
	        				} else {
	        					if(cfg.getDouble("players." + priz.getName() + ".winnings") < prize * 4.0) {
			        				cfg.set("players." + priz.getName() + ".winnings", prize * 4.0);
			        				try {
										cfg.save(file);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
	        					} else {
	        						
	        					}
	        				}
	        			p.sendMessage("");
	        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&m-----------------------------"));
	        		}
	        		for(UUID winners : getKeysByValue(rollers,"14")) {
	        			Player p = Bukkit.getPlayer(winners);
	        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&m-----------------------------"));
	        			p.sendMessage("");
	        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', lostmessage));
	        			p.playSound(p.getLocation(), Sound.valueOf(losesound), 1, 1);
	        			p.sendMessage("");
	        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&m-----------------------------"));
	        		}
	        		
	        		for(UUID winners : getKeysByValue(rollers,"15")) {
	        			Player p = Bukkit.getPlayer(winners);
	        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&m-----------------------------"));
	        			p.sendMessage("");
	        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', lostmessage));
	        			p.playSound(p.getLocation(), Sound.valueOf(losesound), 1, 1);
	        			p.sendMessage("");
	        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&m-----------------------------"));
	        		}
        			rollers.clear();
        			betplayer.clear();
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
        int startingIndex = ThreadLocalRandom.current().nextInt(
            RouletteGUI.items.length);
        for (int index = 0; index < startingIndex; index++) {
          for (int itemstacks = 9; itemstacks < 18; itemstacks++) {
        	  RouletteGUI.rouletteGUI.setItem(itemstacks, RouletteGUI.items[(itemstacks + RouletteGUI.itemIndex)
                % RouletteGUI.items.length]);
          }
          RouletteGUI.itemIndex++;
        }
      }
    
    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    private boolean setupChat()
    {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

        return (chat != null);
    }

    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
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

	
    ItemStack white = createItem(new ItemStack(Material.STAINED_GLASS_PANE), ChatColor.BOLD + "", new String[] {
    });

    ItemStack black26 = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)15 ), ChatColor.translateAlternateColorCodes('&', "&8&l26"), new String[] {
    });
    
    ItemStack red3 = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)14 ), ChatColor.translateAlternateColorCodes('&', "&c&l3"), new String[] {
    });
    
    ItemStack black35 = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)15 ), ChatColor.translateAlternateColorCodes('&', "&8&l35"), new String[] {
    });
    
    ItemStack red12 = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)14 ), ChatColor.translateAlternateColorCodes('&', "&c&l12"), new String[] {
    });
    
    ItemStack green = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)5 ), ChatColor.translateAlternateColorCodes('&', "&a&l0"), new String[] {
    });
    
    ItemStack red32 = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)14 ), ChatColor.translateAlternateColorCodes('&', "&c&l32"), new String[] {
    });
    
    ItemStack black15 = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)15 ), ChatColor.translateAlternateColorCodes('&', "&8&l15"), new String[] {
    });
    
    ItemStack red19 = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)14 ), ChatColor.translateAlternateColorCodes('&', "&c&l19"), new String[] {
    });
    
    ItemStack redclear = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)14 ), ChatColor.translateAlternateColorCodes('&', "&c&lReset bet"), new String[] {
    });
    
    ItemStack goback = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)14 ), ChatColor.translateAlternateColorCodes('&', "&c&lGo back"), new String[] {
    });
    
    ItemStack black4 = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)15 ), ChatColor.translateAlternateColorCodes('&', "&8&l4"), new String[] {
    });
    
    static ItemStack orange1 = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)1 ), ChatColor.translateAlternateColorCodes('&', "&6&l⛂ &a&l500"), new String[] {
    		"",
    		ChatColor.translateAlternateColorCodes('&', "&a&lAdd &6&l⛂ &a&l500 to your bet."),
    });
   
    static ItemStack orange10 = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)1 ), ChatColor.translateAlternateColorCodes('&', "&6&l⛃ &a&l1.000"), new String[] {
    		"",
    		ChatColor.translateAlternateColorCodes('&', "&a&lAdd &6&l⛃ &a&l1.000 to your bet."),
    });
    
    static ItemStack orange50 = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)1 ), ChatColor.translateAlternateColorCodes('&', "&6&l⛃ &a&l10.000"), new String[] {
    		"",
    		ChatColor.translateAlternateColorCodes('&', "&a&lAdd &6&l⛃ &a&l10.000 to your bet."),
    });
    
    static ItemStack orange100 = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)1 ), ChatColor.translateAlternateColorCodes('&', "&6&l⛃ &a&l100.000"), new String[] {
    		"",
    		ChatColor.translateAlternateColorCodes('&', "&a&lAdd &6&l⛃ &a&l100.000 to your bet."),
    		
    		
    });
    
    
    ItemStack hopper = createItem(new ItemStack(Material.HOPPER), ChatColor.translateAlternateColorCodes('&', "&f&6&lStatus"), new String[] {
    		"",
    		ChatColor.translateAlternateColorCodes('&', "&c&lNot enough players to start roulette!")
    });
    
    ItemStack hopperstart = createItem(new ItemStack(Material.HOPPER), ChatColor.translateAlternateColorCodes('&', "&f&6&lStatus"), new String[] {
    		"",
    		ChatColor.translateAlternateColorCodes('&', "&a&lPress &6&lAdd bet &a&lto join in.")
    });
    
    static ItemStack hoppermoving = createItem(new ItemStack(Material.HOPPER), ChatColor.translateAlternateColorCodes('&', "&f&6&lStatus"), new String[] {
    		"",
    		ChatColor.translateAlternateColorCodes('&', "&c&lSpinning..")
    });
    
    public static int getMinBet() {
    	int minbet = instance.getConfig().getInt("min-bet");
    	return minbet;
    }
    
    
    public boolean onCommand(CommandSender sender,
            Command command,
            String label,
            String[] args) {

    	Player player = (Player) sender;
    	String name = (String) sender.getName();
        String string = "";
        String cmd = command.getName();
        
        int x = 0;
        
        if(cmd.equalsIgnoreCase("rconfigreload")) {
        	this.reloadConfig();
        	player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&l[&4&lRoulette&8&l] &a&lConfig successfully reloaded."));
        	return true;
        }
        
        if(cmd.equalsIgnoreCase("roulette")) {
        	if(args.length == 0) {
        	if(!onspin && !rollers.containsKey(player.getUniqueId())) {        		
        	Inventory custom = Bukkit.createInventory(null, 45, ChatColor.BOLD + "Roulette - " + name);
        	Double bal = economy.getBalance(player);
        	if(betplayer.get(player.getUniqueId()) > 1) {
            ItemStack bet = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)5 ), ChatColor.translateAlternateColorCodes('&', "&a&lAdd bet"), new String[] {
						"",
						ChatColor.translateAlternateColorCodes('&', "&f&lBet balance: &6&l⛃ &f&l" + formatter.format(Math.max(0, betplayer.get(player.getUniqueId())))),
						ChatColor.translateAlternateColorCodes('&', "&f&lYour balance: &6&l⛃ &f&l" + formatter.format(bal.longValue())),
						"",
						ChatColor.translateAlternateColorCodes('&', "&7&lRight click &8&l| &c&lRED"),
						ChatColor.translateAlternateColorCodes('&', "&7&lMiddle click &8&l| &a&lGREEN"),
						ChatColor.translateAlternateColorCodes('&', "&7&lLeft click &8&l| &8&lBLACK")
            });
            
            ItemStack book = createItem(new ItemStack(Material.BOOK), ChatColor.translateAlternateColorCodes('&', "&6&lInfo"), new String[] {
					"",
					ChatColor.translateAlternateColorCodes('&', "&f&lBet balance: &6&l⛃ &f&l" + formatter.format(Math.max(0, betplayer.get(player.getUniqueId())))),
					ChatColor.translateAlternateColorCodes('&', "&f&lYour balance: &6&l⛃ &f&l" + formatter.format(bal.longValue())),
            		
            		
            });
            
            EnchantGlow.addGlow(book);
            EnchantGlow.addGlow(bet);
        	custom.setItem(8, book);

        	custom.setItem(34, bet);
        	}
        	
        	if(betplayer.get(player.getUniqueId()) == 0) {
                ItemStack bet = createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)5 ), ChatColor.translateAlternateColorCodes('&', "&a&lAdd bet"), new String[] {
    						"",
    						ChatColor.translateAlternateColorCodes('&', "&f&lBet balance: &6&l⛃ &f&l" + Math.max(0, betplayer.get(player.getUniqueId()))),
    						ChatColor.translateAlternateColorCodes('&', "&f&lYour balance: &6&l⛃ &f&l" + formatter.format(bal.longValue())),
    						"",
    						ChatColor.translateAlternateColorCodes('&', "&7&lRight click &8&l| &c&lRED"),
    						ChatColor.translateAlternateColorCodes('&', "&7&lMiddle click &8&l| &a&lGREEN"),
    						ChatColor.translateAlternateColorCodes('&', "&7&lLeft click &8&l| &8&lBLACK")
                });
                
                ItemStack book = createItem(new ItemStack(Material.BOOK), ChatColor.translateAlternateColorCodes('&', "&6&lInfo"), new String[] {
    					"",
    					ChatColor.translateAlternateColorCodes('&', "&f&lBet balance: &6&l⛃ &f&l" + Math.max(0, betplayer.get(player.getUniqueId()))),
    					ChatColor.translateAlternateColorCodes('&', "&f&lYour balance: &6&l⛃ &f&l" + formatter.format(bal.longValue())),
                		
                		
                });
                
                EnchantGlow.addGlow(book);
                EnchantGlow.addGlow(bet);
                
            	custom.setItem(8, book);

            	custom.setItem(34, bet);
            	}
            
            int num = betplayer.get(player.getUniqueId());
            
           
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
        	
        	while(custom.firstEmpty() != -1){
        		custom.setItem(custom.firstEmpty(), white);
        		}
        	player.openInventory(custom);
        	}
        	if(onspin) {
        	player.openInventory(RouletteGUI.rouletteGUI);
        	}
        	
        	if(rollers.containsKey(player.getUniqueId())) {
        		player.openInventory(RouletteGUI.rouletteGUI);
        	}
        	} else {
        		if(args[0].equals("top")) {
        			if(sender.hasPermission("roulette.top")) {
        	        for(String playerName:cfg.getConfigurationSection("players").getKeys(false)){
        	        	players.put(playerName, cfg.getDouble("players."+playerName+".winnings"));
        	        }
        	        
        	        
        	        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l&m-----------&8&l[&4&lTop Winnings&8&l]&6&l&m-----------"));

        	        String nextTop = "";
        	        Double nextTopWin = 0.0;
        	        
            	        for(int i = 1; i < 11; i++){
                	        for(String playerName: players.keySet()){
                	        if(players.get(playerName) > nextTopWin){
                	        nextTop = playerName;
                	        nextTopWin = players.get(playerName);
                	        }
                	        }
                	        if(!nextTop.isEmpty()) {
                	        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', i + ". " + nextTop + ", &6&l⛃ &a&l" + formatter.format(nextTopWin.longValue())));
                	        players.remove(nextTop);
                	        nextTop = "";
                	        nextTopWin = 0.0;
                	        }
            	        }
        			} else {
        				sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cYou don't have permission!"));
        			}
        		}
        	}
        	return true;
        }

		return false;
    }
    
    
    private void createConfig() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                getLogger().info("[Roulette] Config.yml not found, creating!");
        		this.getConfig().set("min-players", 5);
        		this.getConfig().set("min-bet", 1000);
        		this.getConfig().set("min-bet-message", "&4Minimum bet is 1000.");
                saveDefaultConfig();
            } else {
                getLogger().info("[Roulette] Config.yml found, loading!");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

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
    
    public static boolean isNumeric(String str)  
    {  
    	  return str.matches("-?\\d+(\\.\\d+)?");
    }
}
