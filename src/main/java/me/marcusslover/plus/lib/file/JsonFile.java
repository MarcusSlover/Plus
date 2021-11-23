package me.marcusslover.plus.lib.file;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JsonFile extends AbstractFile {

    protected JsonElement jsonElement;
    @Nullable
    private Gson gson = null;

    public JsonFile(@NotNull File file) {
        super(file);
    }

    @Override
    public boolean isSet(String key) {
        if (jsonElement == null) return false;
        if (jsonElement instanceof JsonObject jsonObject) {
            return jsonObject.has(key);
        }
        return false;
    }

    public void setGson(@Nullable Gson gson) {
        this.gson = gson;
    }

    @Override
    public void load() {
        try {
            FileReader reader = new FileReader(file);
            jsonElement = JsonParser.parseReader(reader);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            jsonElement = new JsonObject();
        }
    }

    @Override
    public void save() {
        try {
            FileWriter fileWriter = new FileWriter(file);
            String json;

            if (gson == null) json = jsonElement.toString();
            else json = gson.toJson(jsonElement);

            fileWriter.write(json);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JsonElement getJsonElement() {
        return jsonElement;
    }

    public void setJsonElement(JsonElement jsonElement) {
        this.jsonElement = jsonElement;
    }
}
