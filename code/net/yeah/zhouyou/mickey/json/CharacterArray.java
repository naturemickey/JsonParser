package net.yeah.zhouyou.mickey.json;

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

	public boolean isFinish() {
		return idx == charArray.length;
	}

	public void moveOneStep() {
		++idx;
	}

	public void moveUntilNotBlank() {
		do {
			if (!isBlank(charArray[idx]))
				return;
		} while (++idx < charArray.length);
	}

	private static boolean isBlank(char c) {
		return c == ' ' || c == '\n' || c == '\r' || c == '\t';
	}
}
