package com.example.app.utils;

import android.media.Image;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ImageListDeserializer implements JsonDeserializer<List<Image>> {

    @Override
    public List<Image> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        List<Image> images = new ArrayList<>();

        if (json.isJsonArray()) {
            for (JsonElement element : json.getAsJsonArray()) {
                images.add(context.deserialize(element, Image.class));
            }
        } else if (json.isJsonObject()) {
            images.add(context.deserialize(json, Image.class));
        }

        return images;
    }
}
