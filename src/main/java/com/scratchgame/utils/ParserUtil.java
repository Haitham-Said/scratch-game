package com.scratchgame.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scratchgame.model.Configuration;

import java.io.File;

public class ParserUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T parse(String configFilePath,Class<T> clazz) throws Exception {
        return objectMapper.readValue(new File(configFilePath), clazz);
    }
}
