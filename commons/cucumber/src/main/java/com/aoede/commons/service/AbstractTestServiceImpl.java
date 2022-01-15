package com.aoede.commons.service;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;

import com.aoede.commons.base.BaseTestComponent;
import com.aoede.commons.base.ResponseResults;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.cucumber.datatable.DataTable;
import lombok.Getter;

@Getter
public abstract class AbstractTestServiceImpl extends BaseTestComponent implements AbstractTestService {
	private boolean success;
	private JsonElement latestKey;
	private JsonObject latestObj;
	private JsonArray latestArr;
	private ResponseResults latestResults;

	private void results (ResponseResults results, int expectedStatus, boolean expectingBody, boolean multipleResults) {
		success =
			(results.status.value() == expectedStatus) &&
			((results.body.isEmpty()) != expectingBody);

		latestObj = null;
		latestArr = null;
		latestResults = results;

		JsonElement element = JsonParser.parseString(results.body);

		if (success && expectingBody && multipleResults) {
			assertTrue ("expecting json array as result", element.isJsonArray());
			latestArr = element.getAsJsonArray();
		}

		if (!success || (expectingBody && !multipleResults)) {
			assertTrue ("expecting json object as result", element.isJsonObject());
			latestObj = element.getAsJsonObject();
		}
	}

	@Override
	public void searchResults (ResponseResults results) {
		results (results, HttpStatus.OK.value(), true, true);
	}

	@Override
	public void accessResults(ResponseResults results) {
		results (results, HttpStatus.OK.value(), true, false);
	}

	@Override
	public void findAllResults (ResponseResults results) {
		results (results, HttpStatus.OK.value(), true, true);
	}

	@Override
	public void createResults (ResponseResults results) {
		results (results, HttpStatus.CREATED.value(), true, false);

		if (success && latestObj.has(getKeyName())) {
			latestKey = latestObj.get(getKeyName());
		}
	}

	@Override
	public void updateResults (ResponseResults results) {
		results (results, HttpStatus.NO_CONTENT.value(), false, false);
	}

	@Override
	public void deleteResults (ResponseResults results) {
		results (results, HttpStatus.NO_CONTENT.value(), false, false);
	}

	@Override
	public void setup () {
		success = false;
	}

	@Override
	public void clear () {
		latestKey = null;
		latestObj = null;
		latestArr = null;
		latestResults = null;
	}

	// TODO: move to json object service
	final public boolean lastKeyMatches (String key) {
		return latestKey.equals(key);
	}

	// TODO: move to json array service
	final public boolean containsKeyInElement (String element, String key, String value) {
		JsonElement keyElement = latestObj.get(element);

		if (!keyElement.isJsonArray()) {
			logger.error("element " + element + " is not an array");
			return false;
		}

		for (var item : keyElement.getAsJsonArray()) {
			if (!item.isJsonObject()) {
				logger.error("element " + element + " array does not contain objects");
				return false;
			}
			JsonObject obj = item.getAsJsonObject();

			if (!obj.has(key)) {
				logger.error("element " + element + " objects do not contain key " + key);
				return false;
			}

			if (obj.get(key).getAsString().equals(value))
				return true;
		}
		return false;
	}

	// TODO: move to json object service
	public boolean lastObjectMatches (DataTable data) {
		Map<String, String> element = new HashMap<String, String> ();

		for (var row : data.asLists()) {
			element.put(row.get(0), row.get(1));
		}

		return objectMatches (latestObj, element.entrySet());
	}

	public boolean lastArrayMatches(DataTable data) {
		for (var tableRow : data.asMaps()) {
			if (!arrayContains (latestArr, tableRow)) {
				return false;
			}
		}
		return true;
	}

	// TODO: move to json array service
	public boolean lastArrayContainsObjectWith(String id, String value) {
		for (var element : latestArr) {
			if (objectMatches(element.getAsJsonObject(), id, value))
				return true;
		}
		return false;
	}

	// TODO: move to json object service
	private boolean arrayContains (JsonArray arr, Map<String, String> result) {
		for (var element : arr) {
			JsonObject obj = element.getAsJsonObject();

			if (objectMatches (obj, result.entrySet())) {
				return true;
			}
		}

		return false;
	}

	// TODO: move to json object service
	protected boolean objectMatches (JsonObject obj, Set<Map.Entry<String, String>> values) {
		for (var value : values) {
			if (!objectMatches (obj, value.getKey(), value.getValue())) {
				return false;
			}
		}

		return true;
	}

	// TODO: move to json object service
	protected boolean objectMatches (JsonObject obj, String key, String value) {
		if (!obj.has(key)) {
			return false;
		}

		if (!obj.get(key).getAsString().equals(value)) {
			return false;
		}

		return true;
	}

}



