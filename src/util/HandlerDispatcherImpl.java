package util;

import static util.util.Logger.log;
import static util.util.Logger.out;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import util.annotation.Mapped;
import util.annotation.Parent;
import util.collection.TreeNode;
import util.data.Result;

public class HandlerDispatcherImpl<V> implements HandlerDispatcher<V>{

	private Map<String, Handler<V>> topMap;
	private Map<Handler<V>, TreeNode<NodeData>> nodeMap;
	private TreeNode<NodeData> root;
	
	public HandlerDispatcherImpl(){
		topMap = new ConcurrentHashMap<>();
		nodeMap = new ConcurrentHashMap<>();
		root = new TreeNode<>(new NodeData(Mapped.NULL, Handler.empty()));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void load(Object instance) {
		Objects.requireNonNull(instance);
		//Scan all fields
		Class<?> instanceClass = instance.getClass();
		Field[] fields = instanceClass.getDeclaredFields();
		List<Field> topHandlerFields = new LinkedList<>();
		List<Field> subHandlerFields = new LinkedList<>();
		
		//Find fields with "@Mapped" annotation, insert them into 
		//topHandlerFields and subHandlerFields
		for(Field field : fields){
			Mapped mapped = field.getAnnotation(Mapped.class);
			if(mapped == null) continue;
			Class<?> fieldType = field.getType();
			if(!Handler.class.isAssignableFrom(fieldType)) //difference from instanceof?
				continue;
			Parent parent = field.getAnnotation(Parent.class);
			if(parent == null) topHandlerFields.add(field);
			else subHandlerFields.add(field);
		}
		//insert topHandlerFields into topMap; link them with tree
		topHandlerFields.forEach(field -> {
			String key = extractKeyFromField(field);
			try {
				field.setAccessible(true);
				Handler<V> handler = (Handler<V>) field.get(instance);
				topMap.put(key, handler);
				NodeData nodeData = new NodeData(key, handler);
				TreeNode<NodeData> treeNode = new TreeNode<NodeData>(root, nodeData);
				nodeMap.put(handler, treeNode);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace(out);
			}
		});
		//link subHandlerFields with tree
		List<Field> processed = new LinkedList<>();
		while(processed.size() < subHandlerFields.size()){
			subHandlerFields.stream().filter(field -> !processed.contains(field)).forEach(field -> {
				linkWithParent(field, processed, instance);
			});
		}
	}

	@SuppressWarnings("unchecked")
	private void linkWithParent(Field field, List<Field> processed, Object instance) {
		field.setAccessible(true);
		Class<?> instanceClass = instance.getClass();
		//Firstly, see if we could find parent field
		String key = extractKeyFromField(field);
		String parentName = field.getAnnotation(Parent.class).value();
		Field parentHandlerField = null;
		try {
			parentHandlerField = instanceClass.getDeclaredField(parentName);
		} catch (NoSuchFieldException | SecurityException e) {
			log("Dude, your parent " + parentName + " doesn't exist.");
			processed.add(field);
			e.printStackTrace(); return;
		}
		//Try to get parentHandler, and its corresponding value in nodeMap
		try {
			parentHandlerField.setAccessible(true);
			Handler<V> parentHandler = (Handler<V>) parentHandlerField.get(instance);
			TreeNode<NodeData> parentNode = nodeMap.get(parentHandler);
			if(parentNode == null){
				log("Parent " + parentName + " of field " + field + " not processed yet. Will wait a while.");
				return;
			}
			//Add subHandler as a child to parentHandler
			Handler<V> subHandler = null;
			subHandler = (Handler<V>) field.get(instance);
			NodeData subHandlerNodeData = new NodeData(key, subHandler);
			TreeNode<NodeData> childNode = new TreeNode<>(parentNode, subHandlerNodeData);
			nodeMap.put(subHandler, childNode);
			processed.add(field);
			log("Attached " + field);
		}catch (IllegalArgumentException | IllegalAccessException e) {
			//should not happen
			e.printStackTrace(out);
			processed.add(field);
		}
	}

	private String extractKeyFromField(Field field) {
		Mapped mapped = field.getAnnotation(Mapped.class);
		String key = mapped.value();
		return key;
	}

	@Override
	public Handler<V> getHandler(String key) {
		Objects.requireNonNull(key);
		return topMap.get(key);
	}

	
	class NodeData{
		Handler<V> handler;
		String key;

		public NodeData(String key, Handler<V> handler) {
			Objects.requireNonNull(key);
			this.key = key;
			this.handler = handler;
		}
		
		public boolean accept(String anotherKey){
			return anotherKey != null && key.equals(anotherKey);
		}

		public Handler<V> getHandler() {
			return handler;
		}
	}


	@Override
	public Handler<V> getSubHandler(Handler<V> parentHandler, String subKey) {
		TreeNode<NodeData> parentNode = nodeMap.get(parentHandler);
		final Predicate<TreeNode<NodeData>> SUBHANDLER_ACCEPTS_KEY = (treeNode) -> (
				((NodeData)treeNode.getData()).accept(subKey)
		);
		Result<TreeNode<NodeData>> result = new Result<>();
		parentNode.getChildren().stream().filter(SUBHANDLER_ACCEPTS_KEY).findFirst().ifPresent(result::set);
		return result.isNULL() ? null : result.get().getData().getHandler();
	}

	@Override
	public Handler<V> getSubHandlerOrDefault(Handler<V> parentHandler, String subKey, Handler<V> defaultHandler) {
		Handler<V> subHandler = getSubHandler(parentHandler, subKey);
		return (subHandler == null) ? defaultHandler : subHandler;
	}
}
