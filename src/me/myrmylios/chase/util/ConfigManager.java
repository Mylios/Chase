package me.myrmylios.chase.util;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import me.myrmylios.chase.gameclasses.Game;
import me.myrmylios.chase.gameclasses.GameManager;

public class ConfigManager {
	File games = new File("plugins/Chase/games.yml");
	YamlConfiguration config;
	GameManager gm;

	public ConfigManager(GameManager gm) {
		this.gm = gm;
		if(!games.exists()) {
			try {
				new File("plugins/Chase").mkdir();
				games.createNewFile();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Game getGame(String name) {
		try {
			config = new YamlConfiguration();
			config.load(games);

			Location cWarp = null;
			if(config.isSet("games." + name + ".chasedwarp.world")) {
				double chasedX = config.getInt("games." + name + ".chasedwarp.x");
				double chasedY = config.getInt("games." + name + ".chasedwarp.y");
				double chasedZ = config.getInt("games." + name + ".chasedwarp.z");
				String chasedWorld = config.getString("games." + name + ".chasedwarp.world");
				cWarp = new Location(Bukkit.getWorld(chasedWorld), chasedX, chasedY, chasedZ);
			}

			Location hWarp = null;
			if(config.isSet("games." + name + ".hunterwarp.world")) {
				double hunterX = config.getInt("games." + name + ".hunterwarp.x");
				double hunterY = config.getInt("games." + name + ".hunterwarp.y");
				double hunterZ = config.getInt("games." + name + ".hunterwarp.z");
				String hunterWorld = config.getString("games." + name + ".hunterwarp.world");
				hWarp = new Location(Bukkit.getWorld(hunterWorld), hunterX, hunterY, hunterZ);
			}

			int players = config.getInt("games." + name + ".players");

			return new Game(name, players, cWarp, hWarp);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setupGame(String name) {
		try {
			config = new YamlConfiguration();
			config.load(games);

			Location cWarp = null;
			if(config.isSet("games." + name + ".chasedwarp.world")) {
				double chasedX = config.getInt("games." + name + ".chasedwarp.x");
				double chasedY = config.getInt("games." + name + ".chasedwarp.y");
				double chasedZ = config.getInt("games." + name + ".chasedwarp.z");
				String chasedWorld = config.getString("games." + name + ".chasedwarp.world");
				cWarp = new Location(Bukkit.getWorld(chasedWorld), chasedX, chasedY, chasedZ);
			}

			Location hWarp = null;
			if(config.isSet("games." + name + ".hunterwarp.world")) {
				double hunterX = config.getInt("games." + name + ".hunterwarp.x");
				double hunterY = config.getInt("games." + name + ".hunterwarp.y");
				double hunterZ = config.getInt("games." + name + ".hunterwarp.z");
				String hunterWorld = config.getString("games." + name + ".hunterwarp.world");
				hWarp = new Location(Bukkit.getWorld(hunterWorld), hunterX, hunterY, hunterZ);
			}

			int players = config.getInt("games." + name + ".players");

			gm.addGame(new Game(name, players, cWarp, hWarp));
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void loadGames() {
		try {
			config = new YamlConfiguration();
			config.load(games);
			ConfigurationSection c = config.getConfigurationSection("games");
			if(c == null) {
				return;
			}
			if(c.getKeys(false).size() > 0) {
				c.getKeys(false).forEach(s -> setupGame(s));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void setGame(Game game) {
		try {
			config = new YamlConfiguration();
			config.load(games);
			config.set("games." + game.getName() + ".players", game.getPlayers());

			if(game.getChasedWarp() != null) {
				config.set("games." + game.getName() + ".chasedwarp.x", game.getChasedWarp().getBlockX());
				config.set("games." + game.getName() + ".chasedwarp.y", game.getChasedWarp().getBlockY());
				config.set("games." + game.getName() + ".chasedwarp.z", game.getChasedWarp().getBlockZ());
				config.set("games." + game.getName() + ".chasedwarp.world", game.getChasedWarp().getWorld().getName());
			}

			if(game.getHunterWarp() != null) {
				config.set("games." + game.getName() + ".hunterwarp.x", game.getHunterWarp().getBlockX());
				config.set("games." + game.getName() + ".hunterwarp.y", game.getHunterWarp().getBlockY());
				config.set("games." + game.getName() + ".hunterwarp.z", game.getHunterWarp().getBlockZ());
				config.set("games." + game.getName() + ".hunterwarp.world", game.getHunterWarp().getWorld().getName());
			}

			config.save(games);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public boolean deleteGame(Game g) {
		try {
			config = new YamlConfiguration();
			config.load(games);
			config.set("games." + g.getName(), null);
			config.save(games);
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void setGameSpawn(Location l) {
		try {
			config = new YamlConfiguration();
			config.load(games);
			config.set("spawn.x", l.getBlockX());
			config.set("spawn.y", l.getBlockY());
			config.set("spawn.z", l.getBlockZ());
			config.set("spawn.world", l.getWorld().getName().toString());
			config.save(games);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public Location getGameSpawn() {
		try {
			config = new YamlConfiguration();
			config.load(games);
			double x = config.getInt("spawn.x");
			double y = config.getInt("spawn.y");
			double z = config.getInt("spawn.z");
			String world = config.getString("spawn.world");

			if(world == null) {
				world = Bukkit.getWorlds().get(0).getName().toString();
			}

			return new Location(Bukkit.getWorld(world), x, y, z);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
