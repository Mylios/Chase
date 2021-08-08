package me.myrmylios.chase.gameclasses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.myrmylios.chase.commands.Start;

public class GameManager {

	public Map<Game, Boolean> games = new HashMap<>();
	public Map<CommandSender, Start> hostedGames = new HashMap<>();
	public List<Player> activePlayers = new ArrayList<>();
	public Map<Player, Start> playerGames = new HashMap<>();

	public boolean createGame(String name, int players) {
		Game game = new Game(name, players);
		addGame(game);
		return true;
	}

	public Game getHostGame(CommandSender sender) {
		if(hostedGames.containsKey(sender)) {
			return hostedGames.get(sender).getGame();
		}
		return null;
	}

	public void addHostGame(CommandSender sender, Start start) {
		this.hostedGames.put(sender, start);
	}

	public void removeHostGame(CommandSender sender) {
		this.hostedGames.remove(sender);
	}

	public void addActivePlayer(Player p, Start s) {
		activePlayers.add(p);
		playerGames.put(p, s);
	}

	public void removeActivePlayer(Player p) {
		activePlayers.remove(p);
		playerGames.remove(p);
	}

	public Map<Player, Start> getPlayerGames() {
		return this.playerGames;
	}

	public List<Player> getActivePlayers() {
		return this.activePlayers;
	}

	public boolean isValid(Game g) {
		for(Game game : games.keySet()) {
			if(game.getName().equalsIgnoreCase(g.getName())) {
				return false;
			}
		}
		return true;
	}

	public boolean addGame(Game game) {
		if(!isValid(game)) {
			return false;
		}
		games.put(game, false);
		return true;
	}

	public boolean removeGame(Game game) {
		if(games.containsKey(game)) {
			games.remove(game);
			return true;
		}
		return false;
	}

	public List<Game> getGames() {
		List<Game> games = new ArrayList<>();
		this.games.forEach((k, v) -> games.add(k));
		return games;
	}

	public Game getGameByName(String name) {
		for(Game g : games.keySet()) {
			if(g.getName().equalsIgnoreCase(name)) {
				return g;
			}
		}
		return null;
	}

	public List<Game> getGameByPlayers(int players) {
		List<Game> games = new ArrayList<>();
		for(Game g : this.games.keySet()) {
			if(g.getPlayers() == players) {
				games.add(g);
			}
		}
		return games;
	}

	public Game getAvailibleGame(int players) {
		for(Game g : getGameByPlayers(players)) {
			if(!games.get(g)) {
				return g;
			}
		}
		return null;
	}

	public Game getAvailibleGame(String name) {
		if(!games.get(getGameByName(name))) {
			return getGameByName(name);
		}
		return null;
	}

	public List<Game> getActiveGames() {
		List<Game> games = new ArrayList<>();
		for(Start s : hostedGames.values()) {
			games.add(s.getGame());
		}
		return games;
	}

	public boolean isActivePlayer(Player p) {
		if(activePlayers.contains(p)) {
			return true;
		}
		return false;
	}

}
