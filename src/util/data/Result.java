/*
 * Copyright (c) 2017, Robert Bosch (Suzhou) All Rights Reserved.
 * This software is property of Robert Bosch (Suzhou). 
 * Unauthorized duplication and disclosure to third parties is prohibited.
 */
package util.data;

/**
 * ClassName: Result <br>
 * Function: Hold a changeable filed as final result. <br>
 * Reason: most used in Lambda expression which need change outside reference
 * value. <br>
 * date: Oct 16, 2017 4:07:55 PM <br>
 * 
 * @since JDK 1.8
 */
public final class Result<T> {
	private T value;

	public Result(T value) {
		super();
		this.value = value;
	}

	public Result() {
		this(null);
	}

	public T get() {
		return value;
	}

	public void set(T value) {
		this.value = value;
	}

	public boolean isNULL() {
		return value == null;
	}

	public static final <T> Result<T> valueOf(T value) {
		return new Result<>(value);
	}
}
