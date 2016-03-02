/*******************************************************************************
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
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
 *******************************************************************************/
package org.eclipse.vorto.codegen.examples.jsonschema.tasks;

import org.eclipse.vorto.codegen.api.AbstractTemplateGeneratorTask;
import org.eclipse.vorto.codegen.api.ITemplate;
import org.eclipse.vorto.codegen.examples.jsonschema.tasks.template.EntityValidationTemplate;
import org.eclipse.vorto.codegen.examples.jsonschema.tasks.template.EnumValidationTemplate;
import org.eclipse.vorto.codegen.examples.jsonschema.tasks.template.OperationReturnTypeValidation;
import org.eclipse.vorto.codegen.examples.jsonschema.tasks.template.PrimitiveTypeValidationTemplate;
import org.eclipse.vorto.core.api.model.functionblock.Operation;

public class OperationReturnTypeValidationTask extends
AbstractTemplateGeneratorTask<Operation> {
	
	private String jsonSchmaFileExtension;
	private String targetPath;
	
	public OperationReturnTypeValidationTask(String jsonSchmaFileExtension, 
			String targetPath) {
		super();
		this.jsonSchmaFileExtension = jsonSchmaFileExtension;
		this.targetPath = targetPath;
	}
	
	@Override
	public String getFileName(Operation operation) {
		return operation.getName()+ "-result" + jsonSchmaFileExtension;
	}

	@Override
	public String getPath(Operation operation) {
		return targetPath;
	}

	@Override
	public ITemplate<Operation> getTemplate() {
		OperationReturnTypeValidation operationReturnTypeValidation = 
				new OperationReturnTypeValidation(
						new EntityValidationTemplate(
								new EnumValidationTemplate(),
								new PrimitiveTypeValidationTemplate()), 
						new EnumValidationTemplate(),
						new PrimitiveTypeValidationTemplate());
		return operationReturnTypeValidation;
	}

}
