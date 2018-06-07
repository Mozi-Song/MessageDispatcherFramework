package util;
/**
 * Processes a message. 
 * @author smo1szh
 *
 * @param <T>
 */
@FunctionalInterface
public interface Handler<T> {
	void process(T data);
	
	/**
	 * A default Handler of type E.
	 */
	static <E> Handler<E> empty(){
		return (data) -> {System.out.println("empty handler");};
	}
}
