/**
 * Copyright (c) 2015-2016 Bosch Software Innovations GmbH and others.
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
package org.eclipse.vorto.repository.web.generation;

import java.util.Collection;

import org.eclipse.vorto.repository.api.generation.GeneratorInfo;
import org.eclipse.vorto.repository.generation.IGeneratorService;
import org.eclipse.vorto.repository.web.AbstractRepositoryController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @author Alexander Edelmann - Robert Bosch (SEA) Pte. Ltd.
 */
@Api(value="/generate", description="Generate code from information models")
@RestController("internal.GeneratorController")
@RequestMapping(value = "/rest/{tenant}/generators")
public class GeneratorController extends AbstractRepositoryController {
	
	@Autowired
	private IGeneratorService generatorService;
		
	@ApiOperation(value = "Returns the rank of code generators by usage")
	@RequestMapping(value = "/rankings/{top}", method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
	public Collection<GeneratorInfo> getMostlyUsedGenerators(@ApiParam(value = "The upper limit number of top code generator list", required = true) final @PathVariable int top) {
		return this.generatorService.getMostlyUsedGenerators(top);
	}
	
	@ApiOperation(value = "Register a code generator",hidden=true)
	@RequestMapping(value = "/{serviceKey}", method = RequestMethod.PUT)
	public void registerGenerator(	@ApiParam(value = "Service key for a specified platform, e.g. lwm2m", required = true) final @PathVariable String serviceKey,
									@ApiParam(value = "The URL links to a specified code generator", required = true) final @RequestBody String baseUrl) {
		this.generatorService.registerGenerator(serviceKey, baseUrl);
	}

	@ApiOperation(value = "Deregister a code generator",hidden=true)
	@RequestMapping(value = "/{serviceKey}", method = RequestMethod.DELETE)
	public boolean deregisterGenerator(@ApiParam(value = "Service key for a specified platform, e.g. lwm2m", required = true) final @PathVariable String serviceKey) {
		this.generatorService.unregisterGenerator(serviceKey);
		return true;
	}
}
