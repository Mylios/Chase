package me.myrmylios.chase.util;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

public class ChaseGear {

	ItemStack cElytra = new ItemStack(Material.ELYTRA);
	ItemStack cPotion = new ItemStack(Material.POTION);

	public ItemStack getCPotion() {
		return cPotion;
	}

	ItemStack aCompass = new ItemStack(Material.COMPASS);

	public ChaseGear() {
		cElytra.setDurability((short) 412);
		PotionMeta bPotionMeta = (PotionMeta) cPotion.getItemMeta();
		bPotionMeta.setLore(Arrays.asList("Using this potion makes you invisible for 30 seconds."));
		bPotionMeta.setDisplayName("Invisibility Potion");
		cPotion.setItemMeta(bPotionMeta);

		ItemMeta compassMeta = aCompass.getItemMeta();
		compassMeta.setDisplayName(ChatColor.WHITE + "Player Tracker");
		compassMeta.setLore(Arrays.asList("This compass will show you the location of the chased every minute."));
		aCompass.setItemMeta(compassMeta);

	}

	public ItemStack[] getCItems() {
		return new ItemStack[] { cPotion, cElytra };
	}

	public ItemStack getHItem() {
		return this.aCompass;
	}

}
