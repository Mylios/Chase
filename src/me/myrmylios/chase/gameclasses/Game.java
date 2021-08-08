package me.myrmylios.chase.gameclasses;

import org.bukkit.Location;

import me.myrmylios.chase.commands.Start;

public class Game {
	public Location chasedWarp;
	public Location hunterWarp;
	public String name;
	public int players;
	public boolean valid;
	public Start start;
	
	


	public Game(String name, int players) {
		this(name, players, null, null);
	}

	
	public Game(String name, int players, Location warp, boolean chased) {
		this.name = name;
		this.players = players;
		if(chased) {
			this.chasedWarp = warp;
		}else {
			this.hunterWarp = warp;
		}

		valid = false;
	}

	public Game(String name, int players, Location chasedWarp, Location hunterWarp) {
		this.name = name;
		this.players = players;
		this.chasedWarp = chasedWarp;
		this.hunterWarp = hunterWarp;
		if(this.chasedWarp != null && this.hunterWarp != null) {
			valid = true;
		}else {
			valid = false;
		}
	}

	
	public Start getStart() {
		return start;
	}


	public void setStart(Start start) {
		this.start = start;
	}
	
	public Location getChasedWarp() {
		return chasedWarp;
	}

	public void setChasedWarp(Location chasedWarp) {
		if(valid == false && hunterWarp != null) {
			valid = true;
		}
		this.chasedWarp = chasedWarp;
	}

	public Location getHunterWarp() {
		return hunterWarp;
	}

	public void setHunterWarp(Location hunterWarp) {
		if(valid == false && chasedWarp != null) {
			valid = true;
		}
		this.hunterWarp = hunterWarp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPlayers() {
		return players;
	}

	public void setPlayers(int players) {
		this.players = players;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

}
