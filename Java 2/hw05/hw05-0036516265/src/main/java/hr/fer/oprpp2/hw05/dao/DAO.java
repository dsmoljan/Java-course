package hr.fer.oprpp2.hw05.dao;


import java.util.List;

import hr.fer.oprpp2.hw05.model.Poll;
import hr.fer.oprpp2.hw05.model.PollOption;

/**
 * Sučelje prema podsustavu za perzistenciju podataka.
 * Sučelje između servisnog sloja i sloja za pohranu podataka
 * Ovo je kao razredi iz paketa repository iz springa, samo još moraš pisati svoje implementacije
 * 
 * @author marcupic
 *
 */
public interface DAO {
	
	/**
	 * Gets all stored polls
	 * @return
	 * @throws DAOException
	 */
	public List<Poll> getAllPolls() throws DAOException;
	
	/**
	 * Returns the poll with the given id, or null if it doesn't exist
	 * @param poolId
	 * @return
	 * @throws DAOException
	 */
	public Poll getPoolById(long poolId) throws DAOException;

	/**
	 * Returns all poll options belonging to the pool with the given id
	 * @param pollId
	 * @return
	 * @throws DAOException
	 */
	public List<PollOption> getPoolOptionsByPollId(long pollId) throws DAOException;
	
	/**
	 * Votes for the poll option with the given id. Poll id isn't neccessary, but it serves as an additional check
	 * @param optionId
	 * @return
	 * @throws DAOException
	 */
	public boolean voteForOption(long pollid, long optionId) throws DAOException;

}