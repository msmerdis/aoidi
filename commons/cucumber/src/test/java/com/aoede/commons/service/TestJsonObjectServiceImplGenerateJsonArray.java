package com.aoede.commons.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;

import com.aoede.JsonObjectServiceImplTestCaseSetup;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import io.cucumber.datatable.DataTable;

public class TestJsonObjectServiceImplGenerateJsonArray extends JsonObjectServiceImplTestCaseSetup {

	@Test
	public void verifyGenerateEmptyArray () throws Exception {
		DataTable template = buildDataTable (List.of(
			List.of( "id",  "name" ),
			List.of("int", "string")
		));

		DataTable table = buildDataTable (List.of(
			List.of("id", "name")
		));

		JsonArray array = uut ().generateJsonArray(template, table);

		assertEquals ("json is not generated correctly", "[]", array.toString());
	}

	@Test
	public void verifyGenerateSingleItem () throws Exception {
		DataTable template = buildDataTable (List.of(
			List.of( "id",  "name" ),
			List.of("int", "string")
		));

		DataTable table = buildDataTable (List.of(
			List.of("id", "name"),
			List.of("12", "abcd")
		));

		JsonArray array = uut ().generateJsonArray(template, table);

		assertEquals ("json is not generated correctly", "[{\"id\":12,\"name\":\"abcd\"}]", array.toString());
	}

	@Test
	public void verifyGenerateMultipleItems () throws Exception {
		DataTable template = buildDataTable (List.of(
			List.of(  "id",    "name"),
			List.of("integer", "long")
		));

		DataTable table = buildDataTable (List.of(
			List.of("id", "name"),
			List.of("12", "1234"),
			List.of("21", "2341")
		));

		JsonArray array = uut ().generateJsonArray(template, table);

		assertEquals ("json is not generated correctly", "[{\"id\":12,\"name\":1234},{\"id\":21,\"name\":2341}]", array.toString());
	}

	@Test
	public void verifyGenerateBooleans () throws Exception {
		DataTable template = buildDataTable (List.of(
			List.of("true", "false"),
			List.of("bool", "boolean")
		));

		DataTable table = buildDataTable (List.of(
			List.of("true", "false"),
			List.of("true", "false"),
			List.of("false", "true")
		));

		JsonArray array = uut ().generateJsonArray(template, table);

		assertEquals ("json is not generated correctly", "[{\"true\":true,\"false\":false},{\"true\":false,\"false\":true}]", array.toString());
	}

	@Test
	public void verifyGenerateFraction () throws Exception {
		DataTable template = buildDataTable (List.of(
			List.of("key"),
			List.of("fraction")
		));

		DataTable table = buildDataTable (List.of(
			List.of("key"),
			List.of("2/4")
		));

		JsonArray array = uut ().generateJsonArray(template, table);

		assertEquals ("json is not generated correctly", "[{\"key\":{\"numerator\":2,\"denominator\":4}}]", array.toString());
	}

	@Test
	public void verifyGeneratePreparedData () throws Exception {
		DataTable template = buildDataTable (List.of(
			List.of("id", "data"),
			List.of("compositeId", "json")
		));

		DataTable table = buildDataTable (List.of(
			List.of("id", "data"),
			List.of("id name", "data name")
		));

		var uut = uut ();

		// mock internal calls
		when (compositeIdService.containsKey(eq("id name"))).thenReturn(true);
		when (compositeIdService.get(eq("id name"))).thenReturn("id value");
		JsonObject theJson = new JsonObject();

		theJson.add("nada", JsonNull.INSTANCE);

		uut.put("data name", theJson);

		JsonArray array = uut.generateJsonArray(template, table);

		assertEquals ("json is not generated correctly", "[{\"id\":\"id value\",\"data\":{\"nada\":null}}]", array.toString());
	}

	@Test
	public void verifyGenerateNoTypes () throws Exception {
		DataTable template = buildDataTable (List.of(
			List.of("key")
		));

		DataTable table = buildDataTable (List.of(
			List.of("key"),
			List.of("2/4")
		));

		assertThrows ("json is not generated correctly", AssertionError.class, () -> {
			uut().generateJsonArray(template, table);
		});
	}

	@Test
	public void verifyGenerateMultipleTypes () throws Exception {
		DataTable template = buildDataTable (List.of(
			List.of("key"),
			List.of("int"),
			List.of("long")
		));

		DataTable table = buildDataTable (List.of(
			List.of("key"),
			List.of("2/4")
		));

		assertThrows ("json is not generated correctly", AssertionError.class, () -> {
			uut().generateJsonArray(template, table);
		});
	}

	@Test
	public void verifyGenerateWidthMismatch () throws Exception {
		DataTable template = buildDataTable (List.of(
			List.of("key", "value"),
			List.of("int", "string")
		));

		DataTable table = buildDataTable (List.of(
			List.of("key"),
			List.of("1")
		));

		assertThrows ("json is not generated correctly", AssertionError.class, () -> {
			uut().generateJsonArray(template, table);
		});
	}

}



