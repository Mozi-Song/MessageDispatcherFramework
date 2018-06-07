package util;

/**
 * Routes to the right {@link Handler}.
 * @author smo1szh
 *
 */
public interface HandlerDispatcher<V> {
	/**
	 * Loads all annotated {@link Handler}s
	 * from given Object.
	 * @param instance
	 */
	void load(Object instance);
	/**
	 * Get top-level {@link Handler} 
	 * corresponding with key.
	 * @param key
	 * @return
	 */
	Handler<V> getHandler(String key);
	
	/**
	 * Get sub-handler of given {@link Handler}.
	 * @param parentHandler, subKey
	 * @return
	 */
	Handler<V> getSubHandler(Handler<V> parentHandler, String subKey);
	
	Handler<V> getSubHandlerOrDefault(Handler<V> parentHandler, String subKey, Handler<V> defaultHandler);
	
	/**
	 * Get top-level {@link Handler}.
	 * If the top-level Handler is null, return defaultHandler.
	 * @param key
	 * @param defaultHandler
	 * @return
	 */
	default Handler<V> getHanlderOrDefault(String key, Handler<V> defaultHandler){
		Handler<V> handler = getHandler(key);
		return (handler == null) ? defaultHandler : handler;
	}
	
	
}
