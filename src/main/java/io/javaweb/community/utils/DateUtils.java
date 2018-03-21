package io.javaweb.community.utils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 * 
 * @author KevinBlandy
 *
 */
public class DateUtils {
	
	public static final String DEFAULF_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULF_DATETIME_PATTERN);
	
	public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DEFAULF_DATETIME_PATTERN);
	
	public static String now() {
		return LocalDateTime.now().format(DATE_TIME_FORMATTER);
	}

	public static Long timestamp() {
		return Instant.now().toEpochMilli();
	}
}
