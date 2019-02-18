package cn.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegTest {
	public static boolean test(String value, String patternString) {
		Pattern pattern = Pattern.compile(patternString);
		Matcher result = pattern.matcher(value);
		if (!result.matches()) {
			return false;
		}
		return true;
	}
}
