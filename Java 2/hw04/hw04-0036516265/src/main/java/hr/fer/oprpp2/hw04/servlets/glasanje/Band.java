package hr.fer.oprpp2.hw04.servlets.glasanje;

public class Band {
	
	private int UID;
	private String name;
	private String songURL;
	private int score = 0;
	
	public Band(int UID, String name, String songURL, int score) {
		this.UID = UID;
		this.name = name;
		this.songURL = songURL;
		this.score = 0;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getUID() {
		return UID;
	}

	public String getName() {
		return name;
	}

	public String getSongURL() {
		return songURL;
	}
	
	
	
	

}
