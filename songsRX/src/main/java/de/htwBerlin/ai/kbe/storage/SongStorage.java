package de.htwBerlin.ai.kbe.storage;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.htwBerlin.ai.kbe.Song;

public class SongStorage {

	private static Map<Integer, Song> storage;
	private static SongStorage instance = null;

	private SongStorage() {
		storage = new HashMap<>();
		initSomeSongs();
	}

	public synchronized static SongStorage getInstance() {
		if (instance == null) {
			instance = new SongStorage();
		}
		return instance;
	}

	private void initSomeSongs() {
		List<Song> listOfSongs = new ArrayList<>();
		try {
			listOfSongs = readJSONToSongs(getClass().getClassLoader().getResource("songs.json").getFile());
		} catch (IOException e) {
			System.out.println("SystemInitError");
		}
		for (Song songIterator : listOfSongs) {
			if (songIterator instanceof Song) {
				storage.put(songIterator.getId(), songIterator);
			}
		}
		System.out.println("Songs are stored. \nInit Finished.");
	}

	// Reads a list of songs from a JSON-file into List<Song>
	static List<Song> readJSONToSongs(String fileName) throws FileNotFoundException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		try (InputStream is = new BufferedInputStream(new FileInputStream(fileName))) {
			return objectMapper.readValue(is, new TypeReference<List<Song>>() {
			});
		}
	}

	public Song getSong(Integer id) {
		return storage.get(id);
	}

	public Collection<Song> getAllSongs() {
		return storage.values();
	}

	public Integer addSong(Song song) {
		System.out.println(song.getId());
		storage.put(song.getId(), song);
		return song.getId();
	}

	// returns true (success), when song exists in map and was updated
	// returns false, when song does not exist in map
	public boolean updateSong(Song song) {
		if(createIDList().contains(song.getId())) {
			storage.replace(song.getId(), song);
			return true;
		} else {
			return false; 
		}
	}

	// returns deleted song
	public Song deleteSong(Integer id) {
		Song deletedSong;
		if (createIDList().contains(id)) {
			deletedSong = storage.get(id);
			storage.remove(id);
			System.out.println("song deleted");
			return deletedSong;
		}
		return null;
	}
	
	private List<Integer> createIDList() {
		List<Integer> idList = new ArrayList<>();
                storage.entrySet().forEach((e) -> {
                    idList.add(e.getKey());
            });
		return idList;
	}
}
