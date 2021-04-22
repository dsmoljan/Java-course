package hr.fer.oprpp1.hw04.db;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to parse a query statement
 * @author dorian
 *
 */
public class QueryParser {

	private String query;
	
	char[] queryAsCharArray;
	ArrayList<ConditionalExpression> resultList;


	private int currentIndex;


	public QueryParser(String query) {
		this.query = query.replace("query", "");
		currentIndex = 0;
		queryAsCharArray = this.query.toCharArray();
		resultList = new ArrayList<ConditionalExpression>();
		
		QueryToken token;
		
		IFieldValueGetter fieldGetter = null;
		String string = null;
		IComparisonOperator operator = null;
		
		while (currentIndex < queryAsCharArray.length - 1) {
			token = getNextToken();
		
			switch(token.getType())
			{
				case VALUE_TYPE:
					if (token.getValue().equals("jmbag")) {
						fieldGetter = FieldValueGetters.JMBAG;
					} else if (token.getValue().equals("firstName")) {
						fieldGetter = FieldValueGetters.FIRST_NAME;
					} else if (token.getValue().equals("lastName")) {
						fieldGetter = FieldValueGetters.LAST_NAME;
					} else {
						throw new RuntimeException("Error while processing value type of the query");
					}
					break;
				case STRING:
					string = token.getValue();
					break;
				case OPERATOR:
					switch (token.getValue())
					{
						case ">":
							operator = ComparisonOperators.GREATER;
							break;
						case "<":
							operator = ComparisonOperators.LESS;
							break;
						case ">=":
							operator = ComparisonOperators.GREATER_OR_EQUALS;
							break;
						case "<=":
							operator = ComparisonOperators.LESS_OR_EQUALS;
							break;
						case "!=":
							operator = ComparisonOperators.NOT_EQUALS;
							break;
						case "=":
							operator = ComparisonOperators.EQUALS;
							break;
						case "LIKE":
							operator = ComparisonOperators.LIKE;
							break;
					}
					break;
				case AND:
					if (fieldGetter == null || string == null  || operator == null) {
						throw new NullPointerException("One of the conditional expression variables hasn't been initialized");
					}
					resultList.add(new ConditionalExpression(fieldGetter, string, operator));
					break;
					
					
			}
			
		}
		resultList.add(new ConditionalExpression(fieldGetter, string, operator));
	}

	/**
	 * Returns true if query was of the form jmbag="xxx"
	 * @return
	 */
	public boolean isDirectQuery() {
		if (resultList.size() == 1 && resultList.get(0).getFieldGetter().equals(FieldValueGetters.JMBAG) && resultList.get(0).getComparisonOperator().equals(ComparisonOperators.EQUALS)) {
			return true;
		}
		
		return false;
	}

	/**
	 * Returns the string which was given in equality comparison in the direct query. If the query was not direct, method throws IllegalStateException
	 * @return string given in equality comparison (jmbag)
	 * @throws IllegalStateExcepton if the last query wasn't a direct one
	 */
	public String getQueriedJMBAG() {
		if (this.isDirectQuery() == false) {
			throw new IllegalStateException("Cannot call method getQueriedJMBAG on a non-direct query");
		}
		
		return this.resultList.get(0).getStringLiteral();
	}

	/**
	 * For all queries, including direct, method returns a list of conditional expressions from a query.
	 * For direct queries the list has only one element
	 * @return
	 */
	public List<ConditionalExpression> getQuery(){ //ovo je kao parser, on treba pozivati interni lexer
		return this.resultList;

	}


	private QueryToken getNextToken() {
		this.skipBlankSpaces();

		String value = "";

		//ovaj ga povuče, netko očito ne poveća dobro currentIndex, pa se ni ne pozove skipBlankSpaces, jer ostane na zadnjem znaku koji nije praznina
		//moguće da je baš ova metoda
		if (queryAsCharArray[currentIndex] == '\"') { //check if the next token is a string
			currentIndex++;
			while (currentIndex < queryAsCharArray.length && queryAsCharArray[currentIndex] != '\"') {
				value += queryAsCharArray[currentIndex++];
			}
			currentIndex++;
			return new QueryToken(QueryTokenType.STRING, value);
			
		} else if (Character.isLetter(queryAsCharArray[currentIndex]) && queryAsCharArray[currentIndex] != 'L') { //check if it is a field name (jmbag, firstName, lastName) or and
			while (queryAsCharArray[currentIndex] != ' ' && queryAsCharArray[currentIndex] != '>'
					&& queryAsCharArray[currentIndex] != '<' && queryAsCharArray[currentIndex] != '='
					&& queryAsCharArray[currentIndex] != '!' && queryAsCharArray[currentIndex] != 'L') {
				
				value += queryAsCharArray[currentIndex++];
				
				if (value.toLowerCase().equals("and")){
					return new QueryToken(QueryTokenType.AND, value);
				} else if (value.equals("jmbag") || value.equals("firstName") || value.equals("lastName")) {
					return new QueryToken(QueryTokenType.VALUE_TYPE, value);
				}
			}
			
			//System.out.println("Value is " + value);
			throw new IllegalArgumentException("Error while parsing query! Check your syntax!");
			
			
		} else if (queryAsCharArray[currentIndex] == '>' || queryAsCharArray[currentIndex] == '<' ||  //check if the next token is an operator
				queryAsCharArray[currentIndex] == '!' || queryAsCharArray[currentIndex] == '=' || queryAsCharArray[currentIndex] == 'L') {
			value += queryAsCharArray[currentIndex++];
			
			if (value.equals(">") || value.equals("<") || value.equals("!")) {
				if (currentIndex < queryAsCharArray.length) {
					if (queryAsCharArray[currentIndex] == '=') {
						value += "=";	
						currentIndex++;
					} else {
						if (value.equals("!")) {
							throw new IllegalArgumentException("! can only be used as !=");
						}
					}
				}
				
				return new QueryToken(QueryTokenType.OPERATOR, value);
			} else if (value.equals("=")) {
				return new QueryToken(QueryTokenType.OPERATOR, value);
			} else if (value.equals("L")) {						//L
				if (currentIndex < queryAsCharArray.length - 2) {
					value += queryAsCharArray[currentIndex++];  //I
					value += queryAsCharArray[currentIndex++];  //K
					value += queryAsCharArray[currentIndex++];  //E
				}
				
				return new QueryToken(QueryTokenType.OPERATOR, value);
			}
			
		} else {
			throw new IllegalArgumentException("Error while parsing query! Check your syntax!");
		}
		throw new IllegalArgumentException("Error while parsing query! Check your syntax!");

	}

	//ja bi prvo napravio ovo getQuery
	//jer kad imam njega, vrlo lako mogu provjeriti radi li se o jmbag==xxx itd.


	/**
	 * Private method used to skip \n, \r, \t, and ' ' in text
	 */
	private void skipBlankSpaces() {

		while (currentIndex < queryAsCharArray.length && (queryAsCharArray[currentIndex] == ' ' || queryAsCharArray[currentIndex] == '\n' || queryAsCharArray[currentIndex] == '\r' || queryAsCharArray[currentIndex] == '\t')) {
			this.currentIndex++;
		}

	}
}
