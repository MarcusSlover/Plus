package me.marcusslover.plus.lib.file;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.marcusslover.plus.lib.util.JsonUtil;
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

    public void setGson(@Nullable Gson gson) {
        this.gson = gson;
    }

    @Override
    public void load() {
        try {
            FileReader reader = new FileReader(file);
            jsonElement = JsonUtil.PARSER.parse(reader);
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

    public void setJsonElement(JsonElement jsonElement) {
        this.jsonElement = jsonElement;
    }

    public JsonElement getJsonElement() {
        return jsonElement;
    }
}
