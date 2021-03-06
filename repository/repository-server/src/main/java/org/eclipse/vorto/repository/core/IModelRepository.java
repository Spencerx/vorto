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
package org.eclipse.vorto.repository.core;

import java.util.List;
import java.util.Optional;

import org.eclipse.vorto.repository.api.ModelId;
import org.eclipse.vorto.repository.api.ModelInfo;
import org.eclipse.vorto.repository.api.attachment.Attachment;
import org.eclipse.vorto.repository.api.attachment.Tag;
import org.eclipse.vorto.repository.api.exception.ModelNotFoundException;

/**
 * 
 * @author Alexander Edelmann
 *
 */
public interface IModelRepository {
		
	/**
	 * Searches model resources for the given expression
	 * @param queryExpression
	 * @return
	 */
	List<ModelInfo> search(String queryExpression);
	
	/**
	 * Gets a model resource for the given model id
	 * @param modelId
	 * @return
	 */
	ModelInfo getById(ModelId modelId);
	
	/**
	 * Returns the actual model content for the given model id
	 * @param modelId
	 * @throws ModelNotFoundException
	 * @return
	 */
	ModelFileContent getModelContent(ModelId modelId);
	
	/**
	 * Creates a new version of an existing model
	 * @param modelId
	 * @param newVersion
	 * @param user
	 * @return the model and content of the newly created model in the repository
	 * @throws ModelNotFoundException if the given modelId cannot be found
	 * @throws ModelAlreadyExistsException if the given model with the given version already exists 
	 */
	ModelResource createVersion(ModelId modelId, String newVersion, IUserContext user);
	
	/**
	 * Saves the model to the repo. If it does not exist, the model is created.
	 * 
	 * @param modelId the id of the model
	 * @param content the content
	 * @param fileName the filename of the model
	 * @param user user who has modified the model
	 * @return model info containing model meta data of the saved model
	 */
	ModelInfo save(ModelId modelId, byte[] content, String fileName, IUserContext user);
				
	/**
	 * Gets the mapping model for the given modelId and the given target platform
	 * @param modelId
	 * @param targetPlatform
	 * @return
	 */
	List<ModelInfo> getMappingModelsForTargetPlatform(ModelId modelId, String targetPlatform);
	
	/**
	 * Removes the model for the given ModelID
	 * @param modelId
	 */
    void removeModel(ModelId modelId);
    
    /**
     * Updates the model meta information 
     * @param model
     * @return
     */
	ModelInfo updateMeta(ModelInfo model);
	
	/**
     * Updates the state of the model
     * 
     * @param modelId the model Id
     * @param state the state of the model
     * @return
     */
    ModelId updateState(ModelId modelId, String state);
    
    /**
     * adds the given file content to the model
     * @param id
     * @param fileContent
     */
	void addFileContent(ModelId id, FileContent fileContent);

	/**
	 * gets file content for the given model id and file name
	 * @param modelId
	 * @param fileName
	 * @return
	 */
	Optional<FileContent> getFileContent(ModelId modelId, Optional<String> fileName);
	
	/**
	 * Attaches the given file to the model
	 * 
	 * @param modelid The modelId where to attach the file
	 * @param fileContent the filename
	 * @param userContext the user context
	 * @param tags attachment tags 
     * @throws Attachment when the attachment could not be attached to the node, e.g. because it is not valid.
	 */
	void attachFile(ModelId modelid, FileContent fileContent, IUserContext userContext, Tag...tags) throws AttachmentException;
	
	/**
	 * Gets a list of attachments for the model (without its content)
	 * 
	 * @param modelId
	 * @return list of attachments of the given model
	 */
	List<Attachment> getAttachments(ModelId modelId);
	
	/**
	 * Gets a list of attachments having the given tag 
	 * @param modelId
	 * @param attachmentTag
	 * @return
	 */
	List<Attachment> getAttachmentsByTag(ModelId modelId, Tag attachmentTag);
	
	/**
	 * Gets the content of the attachment
	 * 
	 * @param modelid The model id where the file was attached
	 * @param fileName the filename of the attachment
	 * @return
	 */
	Optional<FileContent> getAttachmentContent(ModelId modelid, String fileName);
	
	/**
	 * Deletes the attachment
	 * 
	 * @param modelid The model id where the file was attached
	 * @param fileName the filename of the attachment
	 * @return
	 */
	boolean deleteAttachment(ModelId modelId, String fileName);
}