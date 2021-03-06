/**
 * Copyright (c) 2015-2018 Bosch Software Innovations GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * The Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Bosch Software Innovations GmbH - Please refer to git log
 */
package org.eclipse.vorto.mapping.engine;

import java.io.InputStream;
import java.util.Optional;

import org.eclipse.vorto.mapping.engine.converter.JavascriptEvalProvider;
import org.eclipse.vorto.mapping.engine.model.FunctionblockProperty;
import org.eclipse.vorto.mapping.engine.model.InfomodelData;
import org.eclipse.vorto.mapping.engine.model.spec.IMappingSpecification;

public final class MappingEngine {

	private IDataMapper mapper;
	
	private MappingEngine(IMappingSpecification specification) {
		mapper = IDataMapper.newBuilder().registerScriptEvalProvider(new JavascriptEvalProvider()).withSpecification(specification).build();
	}
	
	public static MappingEngine create(IMappingSpecification specification) {
		return new MappingEngine(specification);	
	}
	
	public static MappingEngine createFromInputStream(InputStream inputStream) {
		IMappingSpecification spec = IMappingSpecification.newBuilder().fromInputStream(inputStream).build();
		return new MappingEngine(spec);
	}
	
	/**
	 * Maps the given device source object to Vorto compliant Information Model data.
	 * @param input source input data that is supposed to get mapped.
	 * @return mapped payload that complies to Vorto Information Model
	 */
	public InfomodelData map(Object deviceData) {
		return mapper.mapSource(deviceData);
	}
	
	/**
	 * Maps the given Functionblock Property to device specific object.
	 * @param newValue the value to map
	 * @param oldValue the value that is currently set on the device
	 * @return the mapped device specific object
	 */
	public Object mapTarget(FunctionblockProperty newValue, Optional<FunctionblockProperty> oldValue) {
		return mapper.mapTarget(newValue,oldValue);
	}
}
