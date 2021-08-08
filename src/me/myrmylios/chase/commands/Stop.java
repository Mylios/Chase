package me.myrmylios.chase.commands;

import org.bukkit.command.CommandSender;

import me.myrmylios.chase.gameclasses.Game;
import me.myrmylios.chase.gameclasses.GameManager;

public class Stop {

	public boolean stop(GameManager gm, CommandSender sender, String game) {
		Game g = gm.getHostGame(sender);
		if(g != null) {
			g.getStart().finish(2);
			return true;
		}

		g = gm.getGameByName(game);

		if(g != null && g.getStart() != null) {
			g.getStart().finish(2);
			return true;
		}
		return false;
	}

}
