package org.sam.odf_doclet.bindings;

/**
 * @param <T> sdfasd
 */
interface Filter<T>{
	boolean validate(T t);
}