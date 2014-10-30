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
			if (ca.moveOneStep() == 'r' && ca.moveOneStep() == 'u' && ca.moveOneStep() == 'e') {
				ca.moveOneStep();
				return Boolean.TRUE;
			}
			break;
		case 'f':
			if (ca.moveOneStep() == 'a' && ca.moveOneStep() == 'l' && ca.moveOneStep() == 's'
					&& ca.moveOneStep() == 'e') {
				ca.moveOneStep();
				return Boolean.FALSE;
			}
			break;
		case 'n':
			if (ca.moveOneStep() == 'u' && ca.moveOneStep() == 'l' && ca.moveOneStep() == 'l') {
				ca.moveOneStep();
				return null;
			}
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
			c = ca.moveOneStep();
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
		if (c == '0')
			return "0";
		return parseDigits(ca);
	}

	static String parseDigits(CharacterArray ca) {
		StringBuilder sb = new StringBuilder();
		char c = ca.first();
		while (c >= '0' && c <= '9') {
			sb.append(c);
			c = ca.moveOneStep();
		}
		if (sb.length() == 0)
			throw new RuntimeException();
		return sb.toString();
	}
}
