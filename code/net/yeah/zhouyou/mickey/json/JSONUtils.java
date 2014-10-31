package net.yeah.zhouyou.mickey.json;

import java.util.List;
import java.util.Map;

public class JSONUtils {

	@SuppressWarnings("unchecked")
	public static <T> T parseJson(String json) {
		CharacterArray ca = new CharacterArray(json.trim());
		if (ca.length() > 1) {
			char c = ca.first();
			Object o = null;
			if (c == '{') {
				o = new JSONObject(ca);
			} else if (c == '[') {
				o = new JSONArray(ca);
			}
			if (ca.length() == 0)
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

	private static void parsePair(CharacterArray ca, Map<String, Object> fields) {
		String fieldName = parseString(ca);
		ca.moveUntilNotBlank();
		char c = ca.first();
		if (c != ':')
			throw new RuntimeException();
		ca.moveOneStep();
		Object value = parseValue(ca);
		fields.put(fieldName, value);
	}

	static void parseElements(CharacterArray ca, List<Object> els) {
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
			char[] cs = ca.popArray(3);
			if (cs[0] == 'r' && cs[1] == 'u' && cs[2] == 'e') {
				return Boolean.TRUE;
			}
			break;
		}
		case 'f': {
			ca.moveOneStep();
			char[] cs = ca.popArray(4);
			if (cs[0] == 'a' && cs[1] == 'l' && cs[2] == 's' && cs[3] == 'e') {
				return Boolean.FALSE;
			}
			break;
		}
		case 'n': {
			ca.moveOneStep();
			char[] cs = ca.popArray(3);
			if (cs[0] == 'u' && cs[1] == 'l' && cs[2] == 'l') {
				return null;
			}
			break;
		}
		default:
			return parseNumber(ca);
		}
		throw new RuntimeException();
	}

	private static char parseChar(CharacterArray ca) {
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
				return (char) Integer.parseInt(String.valueOf(ca.popArray(4)), 16);
			default:
				throw new RuntimeException();
			}
		}
		ca.moveOneStep();
		return c;
	}

	private static Number parseNumber(CharacterArray ca) {
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

	private static String parseInt(CharacterArray ca) {
		char c = ca.first();
		if (c == '-') {
			ca.moveOneStep();
			return '-' + parseIntInner(ca);
		}
		return parseIntInner(ca);
	}

	private static String parseIntInner(CharacterArray ca) {
		char c = ca.first();
		if (c == '0') { // 按json的规范一个整数1，不能写成01
			ca.moveOneStep();
			return "0";
		}
		return parseDigits(ca);
	}

	private static String parseDigits(CharacterArray ca) {
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
