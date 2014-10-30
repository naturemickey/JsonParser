package net.yeah.zhouyou.mickey.json;

import java.util.HashMap;
import java.util.Map;

public class JSONObject {

	private Map<String, Object> fields = new HashMap<>();

	public JSONObject(CharacterArray ca) {
		ca.moveOneStep();
		ca.moveUntilNotBlank();
		char c = ca.first();
		if (c != '}') {
			JSONUtils.parseMembers(ca, fields);
		}
		ca.moveUntilNotBlank();
		if (ca.first() != '}') {
			throw new RuntimeException();
		}
		ca.moveOneStep();
	}

	@SuppressWarnings("unchecked")
	public <T> T getField(String fn) {
		return (T) fields.get(fn);
	}

	@Override
	public String toString() {
		return fields.toString();
	}
}
