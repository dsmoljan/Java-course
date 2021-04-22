package hr.fer.oprpp1.custom.collections;

/**
 * Model of an object (processor) which does something on the given value
 * @author dorian
 *
 */
public interface Processor<T> {
	
	void process(T value);

}
