package hr.fer.oprpp1.custom.collections;

/**
 * Interface which models a apstract list. Extends interface Collection
 * @author dorian
 *
 */
public interface List<T> extends Collection<T>{ //as this interface extends interface collection, it specifies that classes which implement it need to implement all of Collection's method and all of its methods
	
	Object get(int index);
	void insert (T value, int position);
	int indexOf(T value);
	void remove(int index);

}
