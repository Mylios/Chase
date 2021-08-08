package me.myrmylios.chase.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.myrmylios.chase.Main;
import me.myrmylios.chase.gameclasses.Game;
import me.myrmylios.chase.gameclasses.GameManager;
import me.myrmylios.chase.util.ChaseGear;
import me.myrmylios.chase.util.ConfigManager;
import me.myrmylios.chase.util.Reference;

public class Start {
	CommandSender host;
	List<Player> players;
	Game game;
	GameManager gm;
	ConfigManager cm;

	public boolean canWin = false;

	public CommandSender getHost() {
		return host;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public Game getGame() {
		return game;
	}

	public Player getChased() {
		return chased;
	}

	public List<Player> getActable() {
		return actable;
	}

	public Start(String[] args, CommandSender host, GameManager gm, ConfigManager cm) {
		players = new ArrayList<Player>();
		this.host = host;
		this.cm = cm;
		this.gm = gm;
		boolean addedGame = false;
		for(int i = 1; i < args.length; i++) {

			if(gm.getActivePlayers().contains(Bukkit.getPlayer(args[i]))) {
				host.sendMessage(Reference.PREFIX + "One or more of the given players are already in a game");
				return;
			}

			if(players.contains(Bukkit.getPlayer(args[i]))) {
				continue;
			}
			if(Bukkit.getPlayer(args[i]) != null) {
				players.add(Bukkit.getPlayer(args[i]));
				continue;
			}
			if(gm.getAvailibleGame(args[i]) != null) {
				game = gm.getGameByName(args[i]);
				continue;
			}
			addedGame = true;
		}

		if(game == null && addedGame) {
			host.sendMessage(Reference.PREFIX
					+ "Given game is not availible or the players given do not equal the game's player value");
			host.sendMessage(Reference.PREFIX + "Random game will be chosen");

		}else {
			host.sendMessage(Reference.PREFIX + "Random game will be chosen");
		}

		game = gm.getAvailibleGame(players.size());
		if(game == null) {
			host.sendMessage(Reference.PREFIX + "No game was availible.");
			return;
		}
		start();

	}

	BossBar gameBar = Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SOLID);

	Player chased;

	// A List of all players that need to get the bossbar setup, teleported to the
	// game spawn etc.
	List<Player> actable = new ArrayList<>();

	int startCounterID;
	// The delay before the hunters get teleported into the game.
	int teleportDelayID;
	int gameCounterID;
	int endCounterID;
	int counter = 15;
	int locationID;

	public void start() {
		if(game == null) {
			return;
		}
		gm.addHostGame(host, this);
		game.setStart(this);
		chased = players.get(new Random().nextInt(players.size()));
		if(players.contains(host)) {
			// If it is a player the host should not need to get all messages like who has
			// what role.
			host = null;
		}else {
			if(host instanceof Player) {
				actable.add((Player) host);
			}
		}

		if(host != null) {
			host.sendMessage(Reference.PREFIX + chased.getName() + " is the chased");
		}

		for(Player p : players) {
			if(p != chased) {
				if(host != null) {
					host.sendMessage(Reference.PREFIX + p.getName() + " is a hunter");
				}
				p.sendMessage(Reference.PREFIX + "You are a hunter");
				p.getInventory().addItem(new ChaseGear().getHItem());
			}
		}

		chased.sendMessage(Reference.PREFIX + "You are the chased");
		chased.getInventory().addItem(new ChaseGear().getCPotion());
		actable.addAll(players);
		players.forEach(p -> {
			gm.addActivePlayer(p, this);
		});
		gameBar.setTitle("Game starts in " + counter + " seconds");

		for(Player p : actable) {
			gameBar.addPlayer(p);
			p.setFoodLevel(20);
			p.setHealth(20);
		}

		startCounterID = Bukkit.getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getPlugin(Main.class),
				new Runnable() {

					@Override
					public void run() {
						counter--;
						gameBar.setTitle("Game starts in " + counter + " seconds");

						if(counter == 0) {
							chased.teleport(game.getChasedWarp());
							Bukkit.getScheduler().cancelTask(startCounterID);
							counter = 60;
						}
					}
				}, 20, 20);

		teleportDelayID = Bukkit.getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getPlugin(Main.class),
				new Runnable() {

					@Override
					public void run() {

						gameBar.setTitle("The hunters will be let free in " + counter + " seconds");
						counter--;
						if(counter == 0) {
							for(Player p : players) {
								if(p == chased) {
									continue;
								}
								p.teleport(game.getHunterWarp());
							}
							counter = 600;
							canWin = true;
							Bukkit.getScheduler().cancelTask(teleportDelayID);
						}

					}

				}, 20 * 15, 20);

		gameCounterID = Bukkit.getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getPlugin(Main.class),
				new Runnable() {

					@Override
					public void run() {
						if(counter / 60 != 0) {
							gameBar.setTitle(
									"Game ends in " + counter / 60 + " minutes and " + counter % 60 + " seconds");
						}else {
							gameBar.setTitle("Game ends in " + counter % 60 + " seconds");
						}
						counter--;
						if(counter == 0) {
							finish(0);
						}

					}
				}, 75 * 20, 20);

		locationID = Bukkit.getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getPlugin(Main.class), new Runnable() {

			@Override
			public void run() {
				Location c = chased.getLocation();
				for(Player p : players) {
					if(p == chased) {
						continue;
					}
					p.setCompassTarget(c);
					p.sendMessage(Reference.PREFIX + "The chased's location is X" + c.getBlockX() + " Y" + c.getBlockY()
							+ " Z" + c.getBlockZ());

				}
				chased.sendMessage(Reference.PREFIX + "Your location has been sent to the hunter");
			}

		}, 75 * 20, 20 * 60);

	}

	String winner = "";

	public void finish(int winValue) {
		switch(winValue) {
		// Chased win
		case 0:
			winner = "The chased has won the game";
			break;
		// Hunter win
		case 1:
			winner = "The hunters have won the game";
			break;
		// Game stop
		case 2:
			winner = "Nobody has won, the game was stopped";
			break;
		}
		stopTasksNoEnd();
		counter = 15;
		canWin = false;
		endCounterID = Bukkit.getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getPlugin(Main.class),
				new Runnable() {

					@Override
					public void run() {
						if(counter == 0) {

							for(Player p : actable) {
								p.teleport(cm.getGameSpawn());
								gameBar.setVisible(false);
								gameBar.removePlayer(p);
								gm.removeActivePlayer(p);
								if(p != chased) {
									p.getInventory().removeItem(new ChaseGear().getHItem());
								}else if(p.getInventory().contains(new ChaseGear().getCPotion())) {
									p.getInventory().removeItem(new ChaseGear().getCPotion());
								}

								p.setDisplayName(p.getName());
							}
							game.setStart(null);
							gm.removeHostGame(host);
							Bukkit.getScheduler().cancelTask(endCounterID);
						}

						gameBar.setTitle(winner + ", teleporting back in " + counter + " seconds");

						counter--;
					}
				}, 20, 20);

	}

	public void stopTasksNoEnd() {
		Bukkit.getScheduler().cancelTask(startCounterID);
		Bukkit.getScheduler().cancelTask(gameCounterID);
		Bukkit.getScheduler().cancelTask(locationID);
		Bukkit.getScheduler().cancelTask(teleportDelayID);
	}

	public void cancelAllTasks() {
		stopTasksNoEnd();
		Bukkit.getScheduler().cancelTask(endCounterID);
	}

	int counter2 = 30;
	BossBar invisBar = Bukkit.createBossBar("You are invisible", BarColor.WHITE, BarStyle.SOLID);
	int invistimer;

	public void startInvisTimer() {

		for(Player p : players) {
			if(p == chased) {
				continue;
			}
			p.hidePlayer(JavaPlugin.getPlugin(Main.class), chased);
		}
		invisBar.addPlayer(chased);
		invistimer = Bukkit.getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getPlugin(Main.class), new Runnable() {

			@Override
			public void run() {
				if(counter2 == 0) {
					invisBar.removePlayer(chased);
					chased.sendMessage(Reference.PREFIX + "You are no longer invisible");
					for(Player p : players) {
						if(p == chased) {
							continue;
						}
						p.showPlayer(JavaPlugin.getPlugin(Main.class), chased);
					}
					Bukkit.getScheduler().cancelTask(invistimer);
				}
				invisBar.setProgress((double) counter2 / 30);
				counter2--;
			}
		}, 20, 20);
	}
}
