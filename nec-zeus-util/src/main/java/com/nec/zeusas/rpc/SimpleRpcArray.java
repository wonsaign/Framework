package com.nec.zeusas.rpc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.nec.zeusas.rpc.GenericClass;

public class SimpleRpcArray<T> extends GenericClass<T> implements List<T> {

	private final List<T> array = new ArrayList<>();

	public SimpleRpcArray() {
		super();
	}

	@Override
	public int size() {
		return array.size();
	}

	@Override
	public boolean isEmpty() {
		return array.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return array.contains(o);
	}

	@Override
	public Iterator<T> iterator() {
		return array.iterator();
	}

	@Override
	public Object[] toArray() {
		return array.toArray();
	}

	@SuppressWarnings("hiding")
	@Override
	public <T> T[] toArray(T[] a) {
		return array.toArray(a);
	}

	@Override
	public boolean add(T e) {
		return array.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return array.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return array.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return array.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		return array.addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return array.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return array.retainAll(c);
	}

	@Override
	public void clear() {
		array.clear();
	}

	@Override
	public T get(int index) {
		return array.get(index);
	}

	@Override
	public T set(int index, T element) {
		return array.set(index, element);
	}

	@Override
	public void add(int index, T element) {
		array.add(index, element);
	}

	@Override
	public T remove(int index) {
		return array.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return array.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return array.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator() {
		return array.listIterator();
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return array.listIterator(index);
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return array.subList(fromIndex, toIndex);
	}

}
