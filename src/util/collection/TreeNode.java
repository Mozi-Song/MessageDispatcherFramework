package util.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class TreeNode<T> {
	T data;
	List<TreeNode<T>> children = new ArrayList<>();
	TreeNode<T> parent;
	
	public TreeNode(TreeNode<T> parent, T data){
		Objects.requireNonNull(parent);
		this.parent = parent;
		this.data = data;
		parent.children.add(this);
	}
	
	public TreeNode(T data){
		this.data = data;
	}

	public List<TreeNode<T>> getChildren() {
		return children;
	}

	public T getData() {
		return data;
	}
}
