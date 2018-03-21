package io.javaweb.community.web.converter;

import io.javaweb.community.exception.ServiceExceptions;
import io.javaweb.community.utils.DateUtils;

import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by KevinBlandy on 2018/1/19 15:53
 */
public class DateConverter implements Converter<String,Date>{

    public static final SimpleDateFormat[] DATE_FORMATTS = new SimpleDateFormat[] {
            DateUtils.SIMPLE_DATE_FORMAT,
            new SimpleDateFormat("yyyy-MM-dd"),
    };

    @Override
    public Date convert(String source) {
        for(SimpleDateFormat simpleDateFormat : DATE_FORMATTS) {
            try {
                return simpleDateFormat.parse(source);
            } catch (ParseException e) {
                //skip
            }
        }
        throw new RuntimeException(ServiceExceptions.BAD_PARAM);
    }
}
