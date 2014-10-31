package net.yeah.zhouyou.mickey.json;

import java.util.List;
import java.util.Map;

public class JSONUtils {

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
		case 't': {
			ca.moveOneStep();
			char c0 = ca.first();
			ca.moveOneStep();
			char c1 = ca.first();
			ca.moveOneStep();
			char c2 = ca.first();
			if (c0 == 'r' && c1 == 'u' && c2 == 'e') {
				ca.moveOneStep();
				return Boolean.TRUE;
			}
		}
			break;
		case 'f': {
			ca.moveOneStep();
			char c0 = ca.first();
			ca.moveOneStep();
			char c1 = ca.first();
			ca.moveOneStep();
			char c2 = ca.first();
			ca.moveOneStep();
			char c3 = ca.first();
			if (c0 == 'a' && c1 == 'l' && c2 == 's' && c3 == 'e') {
				ca.moveOneStep();
				return Boolean.FALSE;
			}
		}
			break;
		case 'n': {
			ca.moveOneStep();
			char c0 = ca.first();
			ca.moveOneStep();
			char c1 = ca.first();
			ca.moveOneStep();
			char c2 = ca.first();
			if (c0 == 'u' && c1 == 'l' && c2 == 'l') {
				ca.moveOneStep();
				return null;
			}
		}
			break;
		default:
			return parseNumber(ca);
		}
		throw new RuntimeException();
	}

	static char parseChar(CharacterArray ca) {
		char c = ca.first();
		if (c == '\\') {
			ca.moveOneStep();
			c = ca.first();
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
				ca.moveOneStep();
				char c0 = ca.first();
				ca.moveOneStep();
				char c1 = ca.first();
				ca.moveOneStep();
				char c2 = ca.first();
				ca.moveOneStep();
				char c3 = ca.first();
				c = (char) Integer.parseInt(String.valueOf(new char[] { c0, c1, c2, c3 }), 16);
				break;
			default:
				throw new RuntimeException();
			}
		}
		ca.moveOneStep();
		return c;
	}

	static Number parseNumber(CharacterArray ca) {
		ca.moveUntilNotBlank();
		StringBuilder sb = new StringBuilder();
		sb.append(parseInt(ca));
		if (ca.first() == '.') {
			ca.moveOneStep();
			sb.append('.').append(parseDigits(ca));
		}
		char c = ca.first();
		if (c == 'e' || c == 'E') {
			sb.append('e');
			ca.moveOneStep();
			c = ca.first();
			if (c == '+' || c == '-') {
				sb.append(c);
				ca.moveOneStep();
			}
			sb.append(parseDigits(ca));
		}
		return Double.parseDouble(sb.toString());
	}

	static String parseInt(CharacterArray ca) {
		char c = ca.first();
		if (c == '-') {
			ca.moveOneStep();
			return '-' + parseIntInner(ca);
		}
		return parseIntInner(ca);
	}

	private static String parseIntInner(CharacterArray ca) {
		char c = ca.first();
		if (c == '0') {
			ca.moveOneStep();
			return "0";
		}
		return parseDigits(ca);
	}

	static String parseDigits(CharacterArray ca) {
		StringBuilder sb = new StringBuilder();
		char c = ca.first();
		while (c >= '0' && c <= '9') {
			sb.append(c);
			ca.moveOneStep();
			c = ca.first();
		}
		if (sb.length() == 0)
			throw new RuntimeException();
		return sb.toString();
	}
}
