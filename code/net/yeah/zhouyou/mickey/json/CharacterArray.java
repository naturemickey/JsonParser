package net.yeah.zhouyou.mickey.json;

import static java.lang.Character.isWhitespace;

public class CharacterArray {
	private char[] charArray;
	private int idx = 0;

	public CharacterArray(String json) {
		charArray = json.toCharArray();
	}

	public char first() {
		return this.charArray[idx];
	}

	public int length() {
		return charArray.length - idx;
	}

	public void moveOneStep() {
		++idx;
	}

	public void moveUntilNotBlank() {
		if (!isWhitespace(charArray[idx]))
			return;
		while (++idx < charArray.length && isWhitespace(charArray[idx]))
			;
	}

	public char[] popArray(int len) {
		char[] res = new char[len];
		for (int i = 0; i < len; ++i, ++idx) {
			res[i] = charArray[idx];
		}
		return res;
	}
}
