package net.yeah.zhouyou.mickey.json;

import java.util.ArrayList;
import java.util.List;

public class JSONArray {

	private List<Object> values = new ArrayList<Object>();

	public JSONArray(CharacterArray ca) {
		ca.moveOneStep();
		ca.moveUntilNotBlank();
		char c = ca.first();
		if (c != ']') {
			JSONUtils.parseElements(ca, values);
		}
		ca.moveUntilNotBlank();
		if (ca.first() != ']') {
			throw new RuntimeException();
		}
		ca.moveOneStep();
	}

	@Override
	public String toString() {
		return this.values.toString();
	}
}
