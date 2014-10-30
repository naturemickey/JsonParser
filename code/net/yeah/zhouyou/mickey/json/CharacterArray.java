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

	public char moveOneStep() {
		++idx;
		if (idx < charArray.length)
			return charArray[idx];
		return (char)-1;
	}

	public void moveUntilNotBlank() {
		do {
			if (!JSONUtils.isBlank(charArray[idx]))
				return;
		} while (++idx < charArray.length);
	}
}
