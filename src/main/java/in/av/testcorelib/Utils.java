package in.av.testcorelib;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import wiremock.com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import wiremock.com.flipkart.zjsonpatch.JsonDiff;
import wiremock.com.google.common.base.Charsets;
import wiremock.com.google.common.io.Resources;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.databind.MapperFeature.DEFAULT_VIEW_INCLUSION;

public class Utils {
    private Utils() {
    }

    private static final ObjectReader objectReader;
    private static final ObjectWriter objectWriter;

    static {
        ObjectMapper objectMapper = JsonMapper.builder().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DEFAULT_VIEW_INCLUSION, false)
                .configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .build();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectReader = objectMapper.reader();
        objectWriter = objectMapper.writer();
    }

    public static String toJson(Object src) {
        String result = "";
        try {
            result = objectWriter.writeValueAsString(src);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        T result;
        try {
            result = objectReader.forType(classOfT).readValue(json);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    public static <T> T fromJson(String json, TypeReference<T> classOfT) {
        T result;
        try {
            result = objectReader.forType(classOfT).readValue(json);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    @Nonnull
    public static String getResourceAsString(String fileName) {
        try {
            return Resources.toString(Resources.getResource(fileName), Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("[RESOURCE LOADER]: Can not find file!");
        }
    }

    @Nonnull
    public static List<String> getResourcesFrom(String dirName) {
        dirName.replace("\\/", File.separator);
        try {
            return Resources.readLines(Resources.getResource(dirName), Charsets.UTF_8).stream().map(
                    file -> getResourceAsString(dirName + file)).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("[RESOURCE LOADER]: Can not find file!");
        }
    }
    
    public static JsonNode compareJsons(String source, String target) {
        return JsonDiff.asJson(Utils.fromJson(source, JsonNode.class), Utils.fromJson(target, JsonNode.class));
    }
}
