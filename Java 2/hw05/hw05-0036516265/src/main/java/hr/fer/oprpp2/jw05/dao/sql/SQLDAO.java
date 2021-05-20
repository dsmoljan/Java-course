package hr.fer.oprpp2.jw05.dao.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import hr.fer.oprpp2.hw05.dao.DAO;
import hr.fer.oprpp2.hw05.dao.DAOException;
import hr.fer.oprpp2.hw05.model.Poll;
import hr.fer.oprpp2.hw05.model.PollOption;

/**
 * Ovo je implementacija podsustava DAO uporabom tehnologije SQL. Ova
 * konkretna implementacija očekuje da joj veza stoji na raspolaganju
 * preko {@link SQLConnectionProvider} razreda, što znači da bi netko
 * prije no što izvođenje dođe do ove točke to trebao tamo postaviti.
 * U web-aplikacijama tipično rješenje je konfigurirati jedan filter 
 * koji će presresti pozive servleta i prije toga ovdje ubaciti jednu
 * vezu iz connection-poola, a po zavrsetku obrade je maknuti.
 *  
 * @author dsmoljan
 */
public class SQLDAO implements DAO {
	

	@Override
	public List<Poll> getAllPolls() throws DAOException {
		List<Poll> entries = new ArrayList<Poll>();
		Connection con = SQLConnectionProvider.getConnection();
		try(PreparedStatement pst = con.prepareStatement("select id, title, message from Polls order by id")){
			try(ResultSet rset = pst.executeQuery()){
				while (rset != null && rset.next()) {
					Poll poll = new Poll();
					poll.setId(rset.getLong(1));
					poll.setTitle(rset.getString(2));
					poll.setMessage(rset.getString(3));
					entries.add(poll);
				}
			}
		}catch(Exception e) {
			throw new DAOException("Error while attempting to fetch poll list!");
		}
		return entries;
	}

	@Override
	public Poll getPoolById(long pollId) throws DAOException {
		Poll poll = null;
		Connection con = SQLConnectionProvider.getConnection();
		try(PreparedStatement pst = con.prepareStatement("select id, title, message from Polls where id = ?")){
			pst.setLong(1, pollId);
			try(ResultSet rset = pst.executeQuery()){
				if(rset!=null && rset.next()) {
					poll = new Poll();
					poll.setId(rset.getLong(1));
					poll.setTitle(rset.getString(2));
					poll.setMessage(rset.getString(3));
				}
			}
		}catch(Exception e) {
			throw new DAOException("Error while attempting to fetch poll with id " + Long.toString(pollId));
		}
		
		return poll;
	}
	
	@Override
	public List<PollOption> getPoolOptionsByPollId(long pollId) throws DAOException{
		List<PollOption> entries = new ArrayList<PollOption>();
		Connection con = SQLConnectionProvider.getConnection();
		try(PreparedStatement pst = con.prepareStatement("select id, optionTitle, optionLink, votesCount from PollOptions where pollID = ?")){
			pst.setLong(1, pollId);
			try(ResultSet rset = pst.executeQuery()){
				while(rset!=null && rset.next()) {
					PollOption option = new PollOption();
					option.setId(rset.getLong(1));
					option.setOptionTitle(rset.getString(2));
					option.setOptionLink(rset.getString(3));
					option.setVotesCount(rset.getLong(4));
					entries.add(option);
				}
			}
		}catch(Exception e) {
			throw new DAOException("Error while attempting to fetch poll options for poll with id " + Long.toString(pollId));
		}
		
		return entries;
	}

	//TODO...
	@Override
	public boolean voteForOption(long pollId, long optionId) throws DAOException {
		Connection con = SQLConnectionProvider.getConnection();
		Long votesCount = null;
		//prvo dohvaćamo postojeći broj glasova
		try(PreparedStatement pst = con.prepareStatement("select votesCount from PollOptions where pollID = ? and id = ?")){
			pst.setLong(1, pollId);
			pst.setLong(2, optionId);
			try(ResultSet rset = pst.executeQuery()){
				if(rset!=null && rset.next()) {
					votesCount = (rset.getLong(1));
				}
			}
		} catch (SQLException e) {
			throw new DAOException("Error while attempting to get current number of votes!");
		}
		
		if (votesCount == null) {
			throw new DAOException("Error while attempting to get current number of votes!");
		}
		
		votesCount++;
		try(PreparedStatement pst = con.prepareStatement("UPDATE PollOptions set votesCount = ? WHERE id=?")){
			pst.setLong(1, votesCount);
			pst.setLong(2, optionId);
			int numberOfAffectedRows = pst.executeUpdate();
			// Ocekujemo da je numberOfAffectedRows=1
			if (numberOfAffectedRows == 1) {
				return true;
			}else {
				return false;
			}
		} catch (SQLException e) {
			throw new DAOException("Error while updating votes count for poll option with id " + Long.toString(optionId));
		}
		
	}



}