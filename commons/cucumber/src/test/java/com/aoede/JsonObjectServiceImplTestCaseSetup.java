package com.aoede;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.aoede.commons.base.BaseTestComponent;
import com.aoede.commons.service.AbstractTestServiceDiscoveryService;
import com.aoede.commons.service.CompositeIdService;
import com.aoede.commons.service.JsonObjectServiceImpl;

import io.cucumber.datatable.DataTable;
import io.cucumber.datatable.DataTableTypeRegistry;
import io.cucumber.datatable.DataTableTypeRegistryTableConverter;

@RunWith(SpringRunner.class)
public class JsonObjectServiceImplTestCaseSetup extends BaseTestComponent {

	protected Random random = new Random(0);

	@MockBean
	protected AbstractTestServiceDiscoveryService abstractTestServiceDiscoveryService;

	@MockBean
	protected CompositeIdService compositeIdService;

	// unit under test
	protected JsonObjectServiceImpl uut () throws Exception {
		JsonObjectServiceImpl uut = new JsonObjectServiceImpl ();

		setField (uut, "random", this.random);
		setField (uut, "compositeIdService", this.compositeIdService);
		setField (uut, "abstractTestServiceDiscoveryService", this.abstractTestServiceDiscoveryService);

		return uut;
	}

	protected void setField (JsonObjectServiceImpl uut, String fieldName, Object value) throws Exception {
		Field field = JsonObjectServiceImpl.class.getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(uut, value);
	}

	protected Object getField (JsonObjectServiceImpl uut, String fieldName) throws Exception {
		Field field = JsonObjectServiceImpl.class.getDeclaredField(fieldName);

		field.setAccessible(true);

		return field.get(uut);
	}

	protected DataTable buildDataTable (List<List<String>> data) {
		return DataTable.create(data,
			new DataTableTypeRegistryTableConverter (
				new DataTableTypeRegistry (Locale.ENGLISH)
			)
		);
	}

}



