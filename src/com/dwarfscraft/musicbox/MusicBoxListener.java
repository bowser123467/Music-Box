package com.dwarfscraft.musicbox;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MusicBoxListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		
		MusicBox.getMusicThread().getSongPlayer().addPlayer(player);
		
		MusicBox.getMusicThread().popupMessage(player);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		
		MusicBox.getMusicThread().getSongPlayer().removePlayer(player);
	}
	
	
}
