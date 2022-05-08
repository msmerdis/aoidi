package com.aoede;

import java.lang.reflect.Field;

import org.mockito.Mockito;

import com.aoede.commons.cucumber.service.AbstractTestService;
import com.aoede.commons.cucumber.stepdefs.GenericControllerStepDefs;

public class GenericControllerStepDefsTestCaseSetup extends BaseStepDefinitionTestCaseSetup {

	protected AbstractTestService latestService;

	// unit under test
	@Override
	protected GenericControllerStepDefs uut () throws Exception {
		GenericControllerStepDefs uut = new GenericControllerStepDefs (
			this.serverProperties,
			this.services,
			this.testCaseIdTrackerService,
			this.jsonService,
			this.dataTableService
		);

		super.setField (uut, "restTemplate", this.restTemplate);

		latestService = createLatestServiceMock ();

		return uut;
	}

	protected void setField (GenericControllerStepDefs uut, String fieldName, Object value) throws Exception {
		Field field = GenericControllerStepDefs.class.getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(uut, value);
	}

	protected Object getField (GenericControllerStepDefs uut, String fieldName) throws Exception {
		Field field = GenericControllerStepDefs.class.getDeclaredField(fieldName);

		field.setAccessible(true);

		return field.get(uut);
	}

	protected AbstractTestService createLatestServiceMock() {
		return Mockito.mock(AbstractTestService.class);
	}

}



