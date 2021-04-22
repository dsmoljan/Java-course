package hr.fer.zemris.java.gui.layouts;

/**
 * Modelira ograničenja koja se koriste u CalcLayout layout manageru.
 * @author Dorian
 *
 */
public class RCPosition {
	
	private int row;
	private int column;
	
	public RCPosition(int row, int column) {
		this.row = row;
		this.column = column;
	}
	
	/**
	 * Metoda tvornica, prima tekstovnu specifikaciju (npr. "3,7") i vraća
	 * odgovarajući objekt tipa RCPosition
	 * @param s
	 * @return
	 */
	public static RCPosition fromTextSpec(String s) {
		
		
		int row = 0;
		int column = 0;
		
		try {
			String[] args = s.split(",");
			row = Integer.parseInt(args[0]);
			column = Integer.parseInt(args[1]);
		} catch (NumberFormatException | IndexOutOfBoundsException e) {
			throw new IllegalArgumentException("Error while parsing RCPosition!");
		}
		
		return new RCPosition(row, column);
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	@Override
	public String toString() {
		return "(row=" + row + ", column=" + column + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + column;
		result = prime * result + row;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RCPosition other = (RCPosition) obj;
		if (column != other.column)
			return false;
		if (row != other.row)
			return false;
		return true;
	}
	
	

	
}
