package com.skechers.loyalty.users.logging;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class SemiMaskingPatternLayout extends PatternLayout {

	private Pattern multilinePattern;
	private List<String> maskPatterns = new ArrayList<>();

	public void addMaskPattern(String maskPattern) { // invoked for every single entry in the xml
		maskPatterns.add(maskPattern);
		multilinePattern = Pattern.compile(String.join("|", maskPatterns), Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
	}

	@Override
	public String doLayout(ILoggingEvent event) {
		return maskMessage(super.doLayout(event));
	}

	private String maskMessage(String message) {
		if (multilinePattern == null) {
			return message;
		}
		StringBuilder sb = new StringBuilder(message);
		Matcher matcher = multilinePattern.matcher(sb);
		while (matcher.find()) {
			String targetExpression = matcher.group();
			int eq = targetExpression.indexOf('=');
			int col = targetExpression.indexOf(':');
			String regex = "";
			if (col != -1) {
				regex += ":";
			} else if(eq !=-1){
				regex += "=";
			}
			int start = matcher.start()+1;
			String value = targetExpression;
			if(!"".equals(regex)) {
				String[] split = targetExpression.split(regex);
				value = split[1];
				start = start + split[0].length();
			}
			String maskedValue = getMaskedValue(value);
			
			int end = matcher.end();
			sb.replace(start, end, maskedValue);
		}
		return sb.toString();
	}

	private String getMaskedValue(String value) {
		String result = "";
		if (value.length() < 4) {
			return "**";
		}
		if (value.length() < 8) {
			for (int i = 0; i < value.length() - 2; i++) {
				result += "*";
			}
			char end = value.charAt(value.length() - 2);
			result += String.valueOf(end);
		} else {
			result += value.substring(0, 3);
			for (int i = 0; i < value.length() - 7; i++) {
				result += "*";
			}
			result += value.substring(value.length() - 3, value.length());
		}
		return result;

	}

}
