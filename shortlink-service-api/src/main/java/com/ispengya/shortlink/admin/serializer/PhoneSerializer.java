package com.ispengya.shortlink.admin.serializer;

import cn.hutool.core.util.DesensitizedUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author ispengya
 * @date 2023/11/16 17:51
 */
public class PhoneSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String phone, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String mobilePhone = DesensitizedUtil.mobilePhone(phone);
        jsonGenerator.writeString(mobilePhone);
    }
}
