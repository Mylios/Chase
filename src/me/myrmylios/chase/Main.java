package me.myrmylios.chase;

import org.bukkit.plugin.java.JavaPlugin;

import me.myrmylios.chase.commands.Chase;
import me.myrmylios.chase.gameclasses.GameManager;
import me.myrmylios.chase.util.ConfigManager;
import me.myrmylios.chase.util.EventsHandler;

public class Main extends JavaPlugin {

	public GameManager gm = new GameManager();
	public ConfigManager cm = new ConfigManager(gm);

	public void onEnable() {
		getCommand("chase").setExecutor(new Chase(gm, cm));
		new EventsHandler(gm, cm);
		cm.loadGames();
	}
}
