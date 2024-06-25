package main.dataModels;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class JsonUtil {

    private static final Gson gson = new Gson();

    /**
     * Serialize an obj list in a JSON file.
     *
     * @param <T>      the type of the object in the list
     * @param list     the object list to serialize
     * @param filePath the path of the JSON file
     * @throws IOException if occour an error during the writing
     */
    public static <T> void serializeListToFile(List<T> list, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(list, writer);
        }
    }

    /**
     * Deserialize a list of object from the JSON file.
     *
     * @param <T>      the type of object in the list
     * @param filePath the path of the source JSON file
     * @param clazz    the class of the object in the list
     * @return a list of object deserialized from the JSON file
     * @throws IOException if occour an error during the reading
     */
    public static <T> List<T> deserializeListFromFile(String filePath, Class<T> clazz) throws IOException {
        try (FileReader reader = new FileReader(filePath)) {
            Type type = TypeToken.getParameterized(List.class, clazz).getType();
            return gson.fromJson(reader, type);
        }
    }

    /**
     * Serialize a single object in a JSON file
     *
     * @param <T>      type of the object file
     * @param object   the object to serialize
     * @param filePath the path to the file to write the JSON to
     * @throws IOException if occour an error during the writing
     */
    public static <T> void serializeObjectToFile(T object, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(object, writer);
        }
    }

    /**
     * Deserialize a single object from a JSON file
     *
     * @param <T>      the type of the obj
     * @param filePath the path to the file from which to read the JSON
     * @param clazz    the class of the object to deserialize
     * @return the deserialized object from the JSON
     * @throws IOException if occour an error during the reading
     */
    public static <T> T deserializeObjectFromFile(String filePath, Class<T> clazz) throws IOException {
        try (FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, clazz);
        }
    }
}
