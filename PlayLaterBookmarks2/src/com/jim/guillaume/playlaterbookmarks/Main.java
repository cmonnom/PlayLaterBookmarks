package com.jim.guillaume.playlaterbookmarks;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.jim.guillaume.playlaterbookmarks.ui.MainFrame;

public class Main {

	private static final String FILE_NAME = "search_queries.txt";
	private File file;
	private static Main main;
	private List<String> searches = new ArrayList<String>();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//side bar that stays next to the Playlater window
		//when click on a search : copy and paste it to the playter window (3tabs needed) and submit the query
		//textField to add more searches
		//queries are added to a text file for easy editing
		main = new Main();
		main.getQueryList();
		MainFrame mainFrame = new MainFrame(main.listToArray());
		mainFrame.setVisible(true);

	}
	
	public static Main getMain() {
		return main;
	}
	
	private void getQueryList() {
//		Object test1 = getClass().getResource("../" + FILE_NAME)
//		String test = getClass().getResource("../" + FILE_NAME).getFile();
//		file = new File(getClass().getResource("../" + FILE_NAME).getFile());
		file = new File(FILE_NAME);
		try {
			searches = FileUtils.readLines(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String[] listToArray() {
		String[] array = new String[searches.size()];
		for (int i = 0; i< array.length; i++) {
			array[i] = searches.get(i);
		}
		return array;
	}
	
	public void addSearchToList(String search) {
		try {
			FileUtils.write(file, search + System.getProperty("line.separator"), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		searches.add(search);
	}
	
	public void deleteSearchFromList(int index) {
		searches.remove(index);
		try {
			FileUtils.writeLines(file, searches, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteAllSearchesFromList() {
		searches.clear();
		try {
			FileUtils.writeLines(file, searches, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public List<String> getSearches() {
		return searches;
	}

	public void setSearches(List<String> searches) {
		this.searches = searches;
	}

	public static String getFileName() {
		return FILE_NAME;
	}

	public static void setMain(Main main) {
		Main.main = main;
	}
	
	
	
	
	
	
	
}
