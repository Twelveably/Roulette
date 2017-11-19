package io.github.bagas123.roulette.api;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.github.bagas123.roulette.Main;

public class RouletteAPI {
    public void addTokenBal(String player, Integer amount) {
	Player p = Bukkit.getPlayer(player);
	Main.instance.bal.set("players." + p.getName() + ".betcoin.balance",
		Main.instance.bal.getInt("players." + p.getName() + ".betcoin.balance") + amount);
	try {
	    Main.instance.bal.save(Main.instance.balfile);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public void removeTokenBal(String player, Integer amount) {
	Player p = Bukkit.getPlayer(player);
	Main.instance.bal.set("players." + p.getName() + ".betcoin.balance",
		Main.instance.bal.getInt("players." + p.getName() + ".betcoin.balance") - amount);
	try {
	    Main.instance.bal.save(Main.instance.balfile);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public void setTokenBal(String player, Integer amount) {
	Player p = Bukkit.getPlayer(player);
	Main.instance.bal.set("players." + p.getName() + ".betcoin.balance", amount);
	try {
	    Main.instance.bal.save(Main.instance.balfile);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public int getTokenBal(String player) {
	Player p = Bukkit.getPlayer(player);
	return Main.instance.bal.getInt("players." + p.getName() + ".betcoin.balance");
    }

    public boolean isNumeric(String str) {
	return str.matches("-?\\d+(\\.\\d+)?");
    }

}
