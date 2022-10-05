package com.marcusslover.plus.lib.file;

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

@Deprecated
public class JsonFile extends AbstractFile {

    protected JsonElement jsonElement;

    @Nullable
    private Gson gson = null;

    public JsonFile(@NotNull File file) {
        super(file);
    }

    @Override
    public boolean isSet(@NotNull String key) {
        if (this.jsonElement == null) {
            return false;
        }
        if (this.jsonElement instanceof JsonObject jsonObject) {
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
            FileReader reader = new FileReader(this.file);
            this.jsonElement = JsonParser.parseReader(reader);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            this.jsonElement = new JsonObject();
        }
    }

    @Override
    public void save() {
        try {
            FileWriter fileWriter = new FileWriter(this.file);
            String json;

            if (this.gson == null) {
                json = this.jsonElement.toString();
            } else {
                json = this.gson.toJson(this.jsonElement);
            }

            fileWriter.write(json);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    public JsonElement getJsonElement() {
        return this.jsonElement;
    }

    public void setJsonElement(@NotNull JsonElement jsonElement) {
        this.jsonElement = jsonElement;
    }
}
