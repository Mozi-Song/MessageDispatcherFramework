package util;

import java.util.function.Supplier;

import util.DefaultParentHandler.Resolver;

public interface ParentHandler<T> extends Handler<T>{
	/**
	 * Defines how to map to a sub-handler.
	 * @param data
	 * @return
	 */
	String resolve(T data);
	
	HandlerDispatcher<T> getHandlerDispatcher();
	
	@Override
	default void process(T data) {
		getHandlerDispatcher().getSubHandlerOrDefault(this, resolve(data), Handler.empty()).process(data);
	}
	
	static <T> ParentHandler<T> build(final Supplier<HandlerDispatcher<T>> supplier, Resolver<T> resolver){
		ParentHandler<T> parentHandler = new ParentHandler<T>(){

			@Override
			public String resolve(T data) {
				return resolver.apply(data);
			}

			@Override
			public HandlerDispatcher<T> getHandlerDispatcher() {
				return supplier.get();
			}
			
		};
		return parentHandler;
	}
}
