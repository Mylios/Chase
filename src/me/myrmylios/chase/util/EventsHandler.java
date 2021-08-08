package me.myrmylios.chase.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import me.myrmylios.chase.Main;
import me.myrmylios.chase.commands.Start;
import me.myrmylios.chase.gameclasses.GameManager;

public class EventsHandler implements Listener {

	GameManager gm;
	ConfigManager cm;

	public EventsHandler(GameManager gm, ConfigManager cm) {
		Bukkit.getServer().getPluginManager().registerEvents(this, JavaPlugin.getPlugin(Main.class));
		this.gm = gm;
		this.cm = cm;
	}

	@EventHandler
	public void potiondrinkEvent(PlayerItemConsumeEvent e) {
		Player p = e.getPlayer();
		if(!(e.getItem().getType() == Material.POTION)) {
			return;
		}
		Start s = gm.getPlayerGames().get(p);
		if(s == null) {
			return;
		}
		if(s.getChased().getName() == p.getName()) {
			p.getInventory().removeItem(new ChaseGear().cPotion);
			p.sendMessage(Reference.PREFIX + "You are now invisible");
			s.startInvisTimer();

		}
	}

	@EventHandler
	public void onPlayerInteraction(PlayerInteractAtEntityEvent e) {
		if(!gm.isActivePlayer(e.getPlayer())) {
			return;
		}

		if(!(e.getRightClicked() instanceof Player)) {
			return;
		}
		Player cause = e.getPlayer();
		Player damaged = (Player) e.getRightClicked();
		if(!gm.isActivePlayer(damaged)) {
			return;
		}

		boolean isChased = (gm.getPlayerGames().get(damaged).getChased().getName() == damaged.getName()) ? true : false;

		if(isChased && gm.getPlayerGames().get(damaged).canWin) {
			gm.getPlayerGames().get(damaged).finish(1);
			damaged.sendMessage(Reference.PREFIX + "You have been hit by the hunters, you lost");
			for(Player p : gm.getPlayerGames().get(damaged).getPlayers()) {
				if(p != damaged) {
					p.sendMessage(Reference.PREFIX + "The chased has been caught");
				}
			}
			return;
		}

		if(!isChased) {
			cause.sendMessage(Reference.PREFIX + "You can't win the game by catching hunters");
		}
	}

	@EventHandler
	public void onPlayerInteraction(EntityDamageByEntityEvent e) {

		if(!(e.getDamager() instanceof Player)) {
			return;
		}
		Player cause = (Player) e.getDamager();
		if(!gm.isActivePlayer(cause)) {
			return;
		}

		if(!(e.getEntity() instanceof Player)) {
			return;
		}
		Player damaged = (Player) e.getEntity();
		if(!gm.isActivePlayer(damaged)) {
			return;
		}
		boolean isChased = (gm.getPlayerGames().get(damaged).getChased().getName() == damaged.getName()) ? true : false;
		if(isChased && gm.getPlayerGames().get(damaged).canWin) {
			gm.getPlayerGames().get(damaged).finish(1);
			damaged.sendMessage(Reference.PREFIX + "You have been hit by the hunters, you lost");
			for(Player p : gm.getPlayerGames().get(damaged).getPlayers()) {
				if(p != damaged) {
					p.sendMessage(Reference.PREFIX + "The chased has been caught");
				}
			}
			return;
		}
		if(!isChased) {
			cause.sendMessage(Reference.PREFIX + "You can't win the game by catching hunters");
		}
	}

	@EventHandler
	public void foodLoss(FoodLevelChangeEvent e) {
		if(!(e.getEntity() instanceof Player)) {
			return;
		}
		Player p = (Player) e.getEntity();
		if(gm.isActivePlayer(p)) {
			p.setFoodLevel(20);
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		if(gm.isActivePlayer(e.getPlayer())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if(!gm.isActivePlayer(e.getPlayer())) {
			return;
		}

		Player p = e.getPlayer();

		Start s = gm.getPlayerGames().get(p);

		p.teleport(cm.getGameSpawn());
		if(gm.getPlayerGames().get(p).getChased().getName() != p.getName()) {
			p.getInventory().remove(new ChaseGear().getHItem());
		}else if(p.getInventory().contains(new ChaseGear().getCPotion())) {
			p.getInventory().removeItem(new ChaseGear().getCPotion());
		}
		s.getPlayers().remove(p);
		s.getActable().remove(p);
		s.finish(2);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if(!gm.isActivePlayer(e.getEntity())) {
			return;
		}

		Player p = e.getEntity();

		boolean isChased = (gm.getPlayerGames().get(p).getChased().getName() == p.getName()) ? true : false;

		Start s = gm.getPlayerGames().get(p);
		if(isChased) {
			if(p.getInventory().contains(new ChaseGear().getCPotion())) {
				p.getInventory().removeItem(new ChaseGear().getCPotion());
			}
			s.finish(1);
			return;
		}

		p.getInventory().removeItem(new ChaseGear().getHItem());
		s.getPlayers().remove(p);
		s.getActable().remove(p);

		if(s.getPlayers().size() == 1) {
			s.finish(0);
		}

	}

}
