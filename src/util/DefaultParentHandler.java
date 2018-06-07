package util;

import java.util.function.Function;

public class DefaultParentHandler<T> implements ParentHandler<T>{

	private HandlerDispatcher<T> dispatcher;
	private Resolver<T> resolver;
	
	public DefaultParentHandler(HandlerDispatcher<T> dispatcher, Resolver<T> resolver){
		this.dispatcher = dispatcher;
		this.resolver = resolver;
	}
	
	@Override
	public String resolve(T data) {
		return resolver.apply(data);
	}

	@Override
	public HandlerDispatcher<T> getHandlerDispatcher() {
		return dispatcher;
	}
	
	/**
	 * Defines the rule of resolving.
	 * @author smo1szh
	 *
	 * @param <T>
	 */
	public interface Resolver<T> extends Function<T, String>{}

}
