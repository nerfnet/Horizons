package net.havocmc.transport;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Created by Giovanni on 25/02/2018.
 */
public final class GloArgs {

    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * A Gson instance which overrides the default Double serialization.
     * Use the older {@link GloArgs#gson} instance for default behaviour.
     */
    public static final Gson NO_DOUBLE_GSON = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Double.class, new JsonSerializer<Double>() {
        @Override public JsonElement serialize(Double aDouble, Type type, JsonSerializationContext jsonSerializationContext) {
            Integer value = (int) Math.round(aDouble);
            return new JsonPrimitive(value);
        }
    }).create();

    public static final int FRAME_LENGTH_MAX = 512;
    public static final int FRAME_WIDTH_MAX = FRAME_LENGTH_MAX / 2;

    public final static int MAX_OBJECT_QUEUE_SIZE = 150;
    public final static int MAX_TRANSPORTERS = 15;
}
