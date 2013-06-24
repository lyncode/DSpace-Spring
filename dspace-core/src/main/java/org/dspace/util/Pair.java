package org.dspace.util;

import java.io.Serializable;

public class Pair<F,S> implements Serializable {
	private static final long serialVersionUID = -8143841060602760271L;
	private F first;
	private S second;
	
	
	public Pair() {}
	public Pair(F f, S s) {
		this.first = f;
		this.second = s;
	}
	/**
	 * @return the first
	 */
	public F getFirst() {
		return first;
	}
	/**
	 * @param first the first to set
	 */
	public void setFirst(F first) {
		this.first = first;
	}
	/**
	 * @return the second
	 */
	public S getSecond() {
		return second;
	}
	/**
	 * @param second the second to set
	 */
	public void setSecond(S second) {
		this.second = second;
	}
	
	public boolean equals (Object obj) {
		if (obj instanceof Pair) {
			Pair<?, ?> p = (Pair<?,?>) obj;
			return (p.getFirst().equals(getFirst()) && p.getSecond().equals(getSecond()));
		}
		return super.equals(obj);
	}

}
