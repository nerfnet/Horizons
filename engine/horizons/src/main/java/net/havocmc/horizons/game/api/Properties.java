package net.havocmc.horizons.game.api;

import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;
import net.havocmc.horizons.Horizons;
import net.havocmc.transport.GloArgs;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Optional;

/**
 * Created by Giovanni on 06/03/2018.
 */
public class Properties {

    @SerializedName("bootImage")
    private LinkedTreeMap<String, Object> properties = new LinkedTreeMap<>();

    private transient HashMap<String, LinkedTreeMap<String, Object>> localMaps = Maps.newHashMap();

    public static Optional<Properties> load(@Nonnull File file) {
        Properties properties = null;
        try {
            FileInputStream inputStream = new FileInputStream(file);
            properties = GloArgs.gson.fromJson(IOUtils.toString(inputStream, "UTF-8"), Properties.class);
            inputStream.close();
        } catch (IOException e) {
            Horizons.runtime().logger().warning("Failed to read properties from " + file.getName());
        }
        return Optional.ofNullable(properties);
    }

    public <T extends Serializable> T read(String in, Class<T> out) {
        return helpRead(in, out, properties);
    }

    public Properties set(@Nonnull String key, @Nonnull Object object) {
        properties.put(key, object);
        return this;
    }

    @SuppressWarnings("unchecked")
    public LinkedTreeMap<String, Object> readMap(String in) {
        if (localMaps.containsKey(in))
            return localMaps.get(in);

        localMaps.put(in, read(in, LinkedTreeMap.class));
        return localMaps.get(in);
    }

    @SuppressWarnings("unchecked")
    public <K, V> LinkedTreeMap<K, V> readGenericMap(String in) {
        return read(in, LinkedTreeMap.class);
    }

    public <T extends Serializable> T helpRead(String in, Class<T> out, LinkedTreeMap<String, Object> map) {
        return out.cast(map.get(in));
    }

    public void flush() {
        properties.clear();
        localMaps.clear();
    }
}
