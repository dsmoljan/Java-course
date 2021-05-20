package hr.fer.oprpp2.hw05.model;

public class PollOption {
	
	private long id;
	private String optionTitle;
	private String optionLink;
	private long pollId;
	private long votesCount;
	
	public PollOption() {
	}
	

	public PollOption(long id, String optionTitle, String optionLink, long pollId, long votesCount) {
		super();
		this.id = id;
		this.optionTitle = optionTitle;
		this.optionLink = optionLink;
		this.pollId = pollId;
		this.votesCount = votesCount;
	}



	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getOptionTitle() {
		return optionTitle;
	}

	public void setOptionTitle(String optionTitle) {
		this.optionTitle = optionTitle;
	}

	public String getOptionLink() {
		return optionLink;
	}

	public void setOptionLink(String optionLink) {
		this.optionLink = optionLink;
	}

	public long getPollId() {
		return pollId;
	}

	public void setPollId(long pollId) {
		this.pollId = pollId;
	}

	public long getVotesCount() {
		return votesCount;
	}

	public void setVotesCount(long votesCount) {
		this.votesCount = votesCount;
	}
	
	
	
	

}
