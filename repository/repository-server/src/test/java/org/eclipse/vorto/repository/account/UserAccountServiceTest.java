package org.eclipse.vorto.repository.account;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.eclipse.vorto.repository.AbstractIntegrationTest;
import org.eclipse.vorto.repository.account.impl.DefaultUserAccountService;
import org.eclipse.vorto.repository.account.impl.User;
import org.eclipse.vorto.repository.core.IUserContext;
import org.eclipse.vorto.repository.core.impl.UserContext;
import org.eclipse.vorto.repository.workflow.IWorkflowService;
import org.eclipse.vorto.repository.workflow.impl.DefaultWorkflowService;
import org.junit.Before;
import org.junit.Test;

public class UserAccountServiceTest extends AbstractIntegrationTest  {

	private DefaultUserAccountService accountService;
	
	private IWorkflowService workflow = null;
	
	@Before
	public void setUp() {
		accountService = new DefaultUserAccountService();
		accountService.setModelRepository(modelRepository);
		accountService.setUserRepository(userRepository);
		workflow = new DefaultWorkflowService(this.modelRepository,userRepository);

	}
	
	@Test
	public void testRemoveAccountWithModelsByUser()  throws Exception {
		IUserContext alex = UserContext.user("alex");
		IUserContext admin = UserContext.user("admin");
		
		
		this.workflow.start(importModel("Color.type", alex).getId());
		this.workflow.start(importModel("Colorlight.fbmodel", alex).getId());
		importModel("Switcher.fbmodel", admin);
		importModel("ColorLightIM.infomodel", admin);
		importModel("HueLightStrips.infomodel", admin);
		
		when(userRepository.findByUsername("alex")).thenReturn(User.create("alex"));

		assertEquals(2, this.modelRepository.search("author:" + alex.getUsername()).size());
		accountService.delete("alex");		
		assertEquals(0, this.modelRepository.search("author:" + alex.getUsername()).size());
		assertEquals(2, this.modelRepository.search("author:anonymous").size());
	}

	@Test
	public void testGetUserRoles() throws Exception {
		User user = setupUserWithRoles();
		assertEquals(2, user.getRoles().size());
	}

	@Test (expected = IllegalArgumentException.class)
	public void testCreateUserAlreadyExists() throws Exception {
		User user = setupUserWithRoles();
		when(userRepository.findByUsername("S-1-5-21")).thenReturn(user);
		accountService.create(user.getUsername());
	}

	@Test
	public void testUpdateRole() {
		User user = setupUserWithRoles();
		when(userRepository.findByUsername("S-1-5-21")).thenReturn(user);
		accountService.create(user.getUsername(), Role.ADMIN, Role.MODEL_CREATOR, Role.MODEL_INTEGRATOR);
	}

	private User setupUserWithRoles() {
		User user = new User();
		user.setUsername("S-1-5-21");
		user.addRoles(Role.ADMIN,Role.MODEL_VALIDATOR);
		return user;
	}
}
