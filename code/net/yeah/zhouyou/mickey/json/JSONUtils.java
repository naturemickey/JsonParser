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
		ca.moveUntilNotBlank();
		String fieldName = parseString(ca);
		ca.moveUntilNotBlank();
		char c = ca.first();
		if (c != ':')
			throw new RuntimeException();
		ca.moveOneStep();
		ca.moveUntilNotBlank();
		Object value = parseValue(ca);
		fields.put(fieldName, value);
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

	static String parseString(CharacterArray ca) {
		if (ca.first() != '"')
			throw new RuntimeException();
		ca.moveOneStep();
		if (ca.first() == '"')
			return "";
		String chars = parseChars(ca);
		if (ca.first() != '"')
			throw new RuntimeException();
		ca.moveOneStep();
		return chars;
	}

	static String parseChars(CharacterArray ca) {
		StringBuilder sb = new StringBuilder();
		while (ca.first() != '"') {
			sb.append(parseChar(ca));
		}
		if (ca.first() != '"')
			throw new RuntimeException();
		ca.moveOneStep();
		return sb.toString();
	}

	static Object parseValue(CharacterArray ca) {
		ca.moveUntilNotBlank();
		switch (ca.first()) {
		case '{':
			return new JSONObject(ca);
		case '"':
			return parseString(ca);
		case '[':
			return new JSONArray(ca);
		case 't':
			if (ca.charAt(1) == 'u' && ca.charAt(2) == 'r' && ca.charAt(3) == 'e')
				return Boolean.TRUE;
			break;
		case 'f':
			if (ca.charAt(1) == 'a' && ca.charAt(2) == 'l' && ca.charAt(3) == 's' && ca.charAt(4) == 'e')
				return Boolean.FALSE;
			break;
		case 'n':
			if (ca.charAt(1) == 'u' && ca.charAt(2) == 'l' && ca.charAt(3) == 'l')
				return null;
			break;
		default:
			return parseNumber(ca);
		}
		throw new RuntimeException();
	}

	static boolean isControlCharacter(char c) {
		// TODO 不知道什么是 control character
		return false;
	}

	static char parseChar(CharacterArray ca) {
		char c = ca.first();
		if (c == '\\') {
			c = ca.moveOneStep();
			switch (c) {
			case '"':
				c = '"';
				break;
			case '\\':
				c = '\\';
				break;
			case '/':
				c = '/';
				break;
			case 'b':
				c = '\b';
				break;
			case 'f':
				c = '\f';
				break;
			case 'n':
				c = '\n';
				break;
			case 'r':
				c = '\r';
				break;
			case 't':
				c = '\t';
				break;
			case 'u':
				c = (char) Integer.parseInt(
						String.valueOf(new char[] { ca.moveOneStep(), ca.moveOneStep(), ca.moveOneStep(),
								ca.moveOneStep() }), 16);
				break;
			default:
				throw new RuntimeException();
			}
		} else if (isControlCharacter(c)) {
			throw new RuntimeException();
		}
		ca.moveOneStep();
		return c;
	}

	static Number parseNumber(CharacterArray ca) {
		return null;
	}
}
