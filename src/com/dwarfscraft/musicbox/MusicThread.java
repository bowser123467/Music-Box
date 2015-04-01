package com.dwarfscraft.musicbox;

import io.puharesource.mc.titlemanager.api.TitleObject;

import java.io.File;
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

import com.xxmicloxx.NoteBlockAPI.*;

public class MusicThread implements Runnable{

	private SongPlayer songPlayer;
	private Song[] loadedSongs;
	private int currentSong;
	
	public MusicThread(File songFolder)
	{
		currentSong = 0;
		loadedSongs = new Song[1];
		loadSongs(songFolder);
	}
	
	public void run(){
		
		if(!songPlayer.isPlaying()){

			
			nextSong();
			
			for(Player player : Bukkit.getOnlinePlayers()){
				songPlayer.addPlayer(player);
			}
			
			songPlayer.setPlaying(true);

			
			popupMessage();
			
		}
		
	}
	
	public Song getCurrentSong(){
		return getSongPlayer().getSong();
	}
	
	public SongPlayer getSongPlayer(){
		return songPlayer;
	}
	
	private void loadSongs(File songFolder){
		File[] files = songFolder.listFiles();
		List<Song> songs = new ArrayList<Song>();
		
		MusicBox.getInstance().getLogger().info("Loading songs from "+songFolder.getPath());
		
		for(File file : files){
			Song song = null;
			try {
				song = NBSDecoder.parse(file);
			} catch (Exception e) {
				
				MusicBox.getInstance().getLogger().severe("ERROR: Failed to load song "+file.getPath()+"... "+e.getMessage());
				
				continue;
			}
			
			songs.add(song);
		}
		
		MusicBox.getInstance().getLogger().info("Loaded "+songs.size()+" songs!");
		
		loadedSongs = songs.toArray(loadedSongs);
		
		songPlayer = new RadioSongPlayer(loadedSongs[0]);

		songPlayer.setPlaying(true);

	}
	
	private TitleObject getTitleObject(){
		TitleObject to = new TitleObject(ChatColor.AQUA+"Now Playing:", ChatColor.GREEN+songPlayer.getSong().getTitle());
		
		if(getCurrentSong().getTitle().isEmpty()){
			to.setSubtitle(ChatColor.RED+"[Song Name]");
		}
		
		to.setFadeIn(40);
		to.setFadeOut(60);
		to.setStay(120);
		
		return to;
	}
	
	private void popupMessage(){
		getTitleObject().broadcast();
	}
	
	public void popupMessage(Player player){
		getTitleObject().send(player);
	}
	
	public void nextSong(int times){
		currentSong += times;
		
		if(currentSong >= loadedSongs.length){
			currentSong = 0;
		}
		
		songPlayer = new RadioSongPlayer(loadedSongs[currentSong]);
	}
	
	private void nextSong(){
		nextSong(1);
		
	}

	public boolean trySetSong(String songName) {
		
		for(Song song : loadedSongs){
			if(song.getTitle().equalsIgnoreCase(songName)){
				songPlayer.setPlaying(false);
				songPlayer = new RadioSongPlayer(song);
				songPlayer.setPlaying(true);
				return true;
			}
		}
		
		if(!songName.endsWith(".nbs")){
			songName = songName + ".nbs";
		}
		
		for(Song song : loadedSongs){
			if(song.getPath().getName().equalsIgnoreCase(songName)){
				songPlayer.setPlaying(false);
				songPlayer = new RadioSongPlayer(song);
				songPlayer.setPlaying(true);
				return true;
			}
		}
		
		return false;
	}

	public Song[] getSongs() {
		return loadedSongs;
	}
	
}
