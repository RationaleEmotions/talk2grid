package com.rationaleemotions;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.rationaleemotions.pojos.IndividualCapability;

import java.lang.reflect.Type;
import java.util.List;

/**
 * An internal {@link JsonDeserializer} that is wired into {@link GsonBuilder#registerTypeAdapter(Type, Object)}
 */
class IndividualCapabilityDeserializer implements JsonDeserializer<List<IndividualCapability>> {
    private Gson gson = new Gson();

    @Override
    public List<IndividualCapability> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        return gson.fromJson(json.getAsJsonArray(), getAdaptor());
    }

    static Type getAdaptor() {
        return new TypeToken<List<IndividualCapability>>() {}.getType();
    }
}
