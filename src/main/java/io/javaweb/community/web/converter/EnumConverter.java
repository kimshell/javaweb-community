package io.javaweb.community.web.converter;


import io.javaweb.community.exception.ServiceExceptions;
import org.springframework.core.convert.converter.Converter;

/**
 * Created by KevinBlandy on 2018/1/19 15:50
 */
public class EnumConverter implements Converter<String,Enum<?>> {

    @SuppressWarnings("unchecked")
	@Override
    public Enum<?> convert(String source) {
        try {
            return Enum.valueOf(Enum.class, source.toUpperCase());
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(ServiceExceptions.BAD_PARAM);
        }
    }
}
