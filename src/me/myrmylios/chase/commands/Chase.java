package me.myrmylios.chase.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.myrmylios.chase.gameclasses.Game;
import me.myrmylios.chase.gameclasses.GameManager;
import me.myrmylios.chase.util.ConfigManager;
import me.myrmylios.chase.util.Reference;
import net.md_5.bungee.api.ChatColor;

public class Chase implements CommandExecutor {

	private GameManager gm;
	private ConfigManager cm;

	public Chase(GameManager gm, ConfigManager cm) {
		this.gm = gm;
		this.cm = cm;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(args.length == 0) {
			sender.sendMessage(options());
			return true;
		}

		switch(args[0].toLowerCase()) {
		case "start":
			if(!sender.hasPermission("Chase.game.start")) {
				sender.sendMessage(Reference.PREFIX + "You don't have permission to use this command");
				return true;
			}

			if(gm.getGames().size() == 0) {
				sender.sendMessage(Reference.PREFIX + "There are no games to be hosted, create a game and try again");
				return true;
			}

			new Start(args, sender, gm, cm);
			return true;
		case "stop":
			if(!sender.hasPermission("Chase.game.stop")) {
				sender.sendMessage(Reference.PREFIX + "You don't have permission to use this command");
				return true;
			}

			if(gm.getActiveGames().size() == 0) {
				sender.sendMessage(Reference.PREFIX + "There are no games hosted at this moment");
				return true;
			}

			String s;
			if(args.length > 1) {
				s = args[1];
			}else {
				s = null;
			}

			boolean b = new Stop().stop(gm, sender, s);

			if(b) {
				sender.sendMessage(Reference.PREFIX + "Game has been stopped succesfuly");
			}else {
				sender.sendMessage(Reference.PREFIX + "Game doesn't exist, or could not be stopped");
			}
			return true;
		case "create": {
			if(!sender.hasPermission("Chase.game.create")) {
				sender.sendMessage(Reference.PREFIX + "You don't have permission to use this command");
				return true;
			}
			if(!(args.length == 3)) {
				sender.sendMessage(Reference.PREFIX
						+ "You command has to follow the /chased create <String name> <int players> format");
				return true;
			}
			try {
				gm.createGame(args[1], Integer.parseInt(args[2]));
				cm.setGame(gm.getGameByName(args[1]));
			}catch(Exception e) {
				return false;
			}
			sender.sendMessage(Reference.PREFIX + "The game " + args[1] + " has been created");
			return true;
		}
		case "delete": {
			if(!sender.hasPermission("Chase.game.delete")) {
				sender.sendMessage(Reference.PREFIX + "You don't have permission to use this command");
				return true;
			}

			if(gm.getGames().size() == 0) {
				sender.sendMessage(Reference.PREFIX + "There are no games to be deleted");
				return true;
			}
			Game g = gm.getGameByName(args[1]);

			if(g == null) {
				sender.sendMessage(Reference.PREFIX + "This game does not exist");
				return true;
			}

			cm.deleteGame(g);

			sender.sendMessage(Reference.PREFIX + "The game has been deleted");
			return gm.removeGame(gm.getGameByName(args[1]));
		}
		case "setspawn": {
			if(!sender.hasPermission("Chase.location.setspawn")) {
				sender.sendMessage(Reference.PREFIX + "You don't have permission to use this command");
				return true;
			}
			if(!(sender instanceof Player)) {
				return false;
			}
			cm.setGameSpawn(((Player) sender).getLocation());
			sender.sendMessage(Reference.PREFIX + "The spawn has succesfully been set");
			return true;

		}
		case "setchasedwarp": {
			if(!sender.hasPermission("Chase.location.setchasedwarp")) {
				sender.sendMessage(Reference.PREFIX + "You don't have permission to use this command");
				return true;
			}

			if(gm.getGames().size() == 0) {
				sender.sendMessage(
						Reference.PREFIX + "There are no games to add a warp to, create a game and try again");
				return true;
			}

			if(!(sender instanceof Player)) {
				return false;
			}
			Game g = gm.getGameByName(args[1]);
			if(g == null) {
				return false;
			}
			g.setChasedWarp(((Player) sender).getLocation());
			cm.setGame(g);
			sender.sendMessage(Reference.PREFIX + "The chased warp has succesfully been set");
			return true;
		}
		case "sethunterwarp": {
			if(!sender.hasPermission("Chase.location.sethunterwarp")) {
				sender.sendMessage(Reference.PREFIX + "You don't have permission to use this command");
				return true;
			}

			if(gm.getGames().size() == 0) {
				sender.sendMessage(
						Reference.PREFIX + "There are no games to add a warp to, create a game and try again");
				return true;
			}

			if(!(sender instanceof Player)) {
				return false;
			}
			Game g = gm.getGameByName(args[1]);
			if(g == null) {
				return false;
			}
			g.setHunterWarp(((Player) sender).getLocation());
			cm.setGame(g);
			sender.sendMessage(Reference.PREFIX + "The hunter warp has succesfully been set");

			return true;
		}
		case "games":
			if(!sender.hasPermission("Chase.game.games")) {
				sender.sendMessage(Reference.PREFIX + "You don't have permission to use this command");
				return true;
			}

			if(gm.getGames().size() == 0) {
				sender.sendMessage(Reference.PREFIX + "There are no games");
				return true;
			}

			sender.sendMessage(Reference.PREFIX + "Games");
			gm.getGames().forEach(g -> sender.sendMessage("Name: " + g.getName() + " Players: " + g.getPlayers()));
			sender.sendMessage(Reference.PREFIX + "-----");
			return true;

		default:
			sender.sendMessage(Reference.PREFIX + options());
		}

		return false;
	}

	public String options() {
		return ChatColor.AQUA + "----------\n" + ChatColor.WHITE
				+ "/chase -> Display this menu \n/chase create <name> <players> -> Create a new game \n/chase delete <name> -> Delete the game with the given name \n"
				+ "/chase setspawn -> Set the spawn location for all games to your location \n/chase setchasedwarp <game name>-> Set the warp for the chased on your location\n"
				+ "/chase sethunterwarp <game name> -> Set the hunter warp on your location\n/chase start <player1> <player2>... (game name) -> Start a game with the given players, if a game name was provided will that game be the first pick \n"
				+ "/chase stop (game) -> Stops the game you are hosting or the game you provided \n/chased games -> Shows a list of all games"
				+ ChatColor.AQUA + "\n----------";
	}

}
