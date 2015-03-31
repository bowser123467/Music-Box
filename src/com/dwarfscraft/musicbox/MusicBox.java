package com.dwarfscraft.musicbox;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.xxmicloxx.NoteBlockAPI.Song;

public class MusicBox extends JavaPlugin {

	private static MusicBox instance;
	
	private MusicThread musicThread;
	
	public void onEnable(){
		instance = this;
		
		if(!getSongFolder().exists()){
			getSongFolder().mkdirs();
		}
		
		musicThread = new MusicThread(getSongFolder());
		
		Bukkit.getPluginManager().registerEvents(new MusicBoxListener(), this);
		
		Bukkit.getScheduler().runTaskTimer(this, musicThread, 0, 20);
		
	}

	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		if(command.getName().equals("nextsong")){
			
			if(sender.hasPermission("musicbox.commands.nextsong") || sender.isOp()){
				
				int times = 1;
				
				if(args.length > 0){
					try {
						times = Integer.parseInt(args[0]);
					} catch (NumberFormatException e) {
						return false;
					}
				}
				
				getMusicThread().getSongPlayer().setPlaying(false);
				getMusicThread().nextSong(times);
				//Will cause the song to skip
				sender.sendMessage(ChatColor.GREEN+"Song flagged for skip.");
			}else{
				sender.sendMessage(ChatColor.DARK_RED+"You don't have permission to do that!");
			}
			
			return true;
			
		}else if(command.getName().equals("reloadsongs")){
			
			if(sender.hasPermission("musicbox.commands.reloadsongs") || sender.isOp()){
				
				musicThread.getSongPlayer().setPlaying(false);
				
				Bukkit.getScheduler().cancelTasks(this);
				
				musicThread = new MusicThread(getSongFolder());
				
				Bukkit.getScheduler().runTaskTimer(this, musicThread, 0, 20);
				
				sender.sendMessage(ChatColor.GREEN+"Songs reloaded!");
				
				
				
			}else{
				sender.sendMessage(ChatColor.DARK_RED+"You don't have permission to do that!");
			}
			
			return true;
			
		}else if(command.getName().equals("playing")){
			if(sender.hasPermission("musicbox.commands.playing")){
				
				String title = getMusicThread().getCurrentSong().getTitle();
				
				if(title.isEmpty()){
					title = ChatColor.RED+"[No Name]";
				}
				
				sender.sendMessage(ChatColor.GREEN+"Now playing: "+title);
				
				
			}else{
				sender.sendMessage(ChatColor.DARK_RED+"You don't have permission to do that!");
			}
			
			return true;
		}else if(command.getName().equals("songs")){
			if(sender.hasPermission("musicbox.commands.playing")){
				
				StringBuffer buf = new StringBuffer();
				
				buf.append(ChatColor.GREEN+"Loaded songs: ");
				
				Song[] songs = getMusicThread().getSongs();
				
				for(int i = 0; i < songs.length;i++){
					
					if(i % 2 == 0){
						buf.append(ChatColor.GRAY);
					}else{
						buf.append(ChatColor.DARK_GRAY);
					}
					
					buf.append(songs[i].getTitle());
					
					if(i < songs.length-1){
						buf.append(", ");
					}
					
					
				}
				
				sender.sendMessage(buf.toString());
				
			}else{
				sender.sendMessage(ChatColor.DARK_RED+"You don't have permission to do that!");
			}
			
			return true;
			
		}else if(command.getName().equals("setsong")){
			if (sender.hasPermission("musicbox.commands.setsong")) {
				if(args.length > 0){
					
					String songName = "";
					
					for(int i = 0; i < args.length;i++){
						songName += args[i];
						if(i < args.length-1){
							songName += " ";
						}
					}
					
					boolean success = getMusicThread().trySetSong(songName);
					
					if(success){
						sender.sendMessage(ChatColor.GREEN+"Song set to "+songName);
					}else{
						sender.sendMessage(ChatColor.RED+"Song named "+songName+" does not exist");
					}
					
					
				}else{
					return false;
				}
			} else {
				sender.sendMessage(ChatColor.DARK_RED+"You don't have permission to do that!");
			}
			
			return true;
		}
		
		
		return super.onCommand(sender, command, label, args);
	}



	public static MusicBox getInstance() {
		return instance;
	}

	public static MusicThread getMusicThread() {
		return instance.musicThread;
	}
	
	public static File getSongFolder(){
		return new File(getInstance().getDataFolder(), "songs/");
	}
	
}
