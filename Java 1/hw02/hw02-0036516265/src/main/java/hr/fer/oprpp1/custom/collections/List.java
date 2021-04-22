package hr.fer.oprpp1.custom.collections;

/**
 * Interface which models a apstract list. Extends interface Collection
 * @author dorian
 *
 */
public interface List extends Collection{ //as this interface extends interface collection, it specifies that classes which implement it need to implement all of Collection's method and all of its methods
	
	Object get(int index);
	void insert (Object value, int position);
	int indexOf(Object value);
	void remove(int index);

}
