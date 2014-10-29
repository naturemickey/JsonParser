package net.yeah.zhouyou.mickey.json;

import java.util.List;
import java.util.Map;

public class JSONUtils {

	static boolean isBlank(char c) {
		return c == ' ' || c == '\n' || c == '\r' || c == '\t';
	}

	/**
	 * 返回一个JSONObject或JSONArray类型的对象。
	 */
	@SuppressWarnings("unchecked")
	public static <T> T parseJson(String json) {
		CharacterArray ca = new CharacterArray(json);
		if (ca.length() > 1) {
			char c = ca.first();
			Object o = null;
			if (c == '{') {
				o = new JSONObject(ca);
			} else if (c == '[') {
				o = new JSONArray(ca);
			}
			if (ca.isFinish())
				return (T) o;
		}
		throw new RuntimeException();
	}

	static void parseMembers(CharacterArray ca, Map<String, Object> fields) {
		ca.moveUntilNotBlank();
		parsePair(ca, fields);
		ca.moveUntilNotBlank();
		if (ca.first() == ',') {
			ca.moveOneStep();
			parseMembers(ca, fields);
		}
	}

	static void parsePair(CharacterArray ca, Map<String, Object> fields) {
	}

	static void parseElements(CharacterArray ca, List<Object> els) {
		ca.moveUntilNotBlank();
		els.add(parseValue(ca));
		ca.moveUntilNotBlank();
		if (ca.first() == ',') {
			ca.moveOneStep();
			parseElements(ca, els);
		}
	}

	static Object parseValue(CharacterArray ca) {
		return null;
	}

	static String parseString(CharacterArray ca) {
		return null;
	}

	static char parseChar(CharacterArray ca) {
		return ' ';
	}

	static Number parseNumber(CharacterArray ca) {
		return null;
	}
}
