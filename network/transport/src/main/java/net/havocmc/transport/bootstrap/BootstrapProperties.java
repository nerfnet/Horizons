package net.havocmc.transport.bootstrap;

import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;
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
 * Created by Giovanni on 25/02/2018.
 */
public class BootstrapProperties implements Serializable {

    @SerializedName("bootImage")
    private HashMap<String, Object> bootMap = Maps.newHashMap();

    public static Optional<BootstrapProperties> load(@Nonnull File file) {
        if (!file.getName().toLowerCase().endsWith(".boot"))
            return Optional.empty();

        BootstrapProperties properties = null;
        try {
            FileInputStream inputStream = new FileInputStream(file);
            properties = GloArgs.gson.fromJson(IOUtils.toString(inputStream, "UTF-8"), BootstrapProperties.class);
            inputStream.close();
        } catch (IOException e) {
            System.err.println("Failed to read bootstrap image, abort.");
            e.printStackTrace();
        }
        return Optional.ofNullable(properties);
    }

    public <T extends Serializable> T read(String in, Class<? extends T> type) {
        return type.cast(bootMap.get(in));
    }

    public void printImage(boolean err) {
        if (bootMap.isEmpty()) {
            System.out.println("Boot image is empty!");
            return;
        }
        if (!err)
            bootMap.keySet().forEach(key -> {
                System.out.println(key + "@" + bootMap.get(key));
            });
        else
            bootMap.keySet().forEach(key -> {
                System.out.println("  " + key + "@" + bootMap.get(key));
            });
    }

    /**
     * Stores the object in memory, the properties do not get saved upon exit.
     */
    public void put_MEMORY(String key, Object value) {
        bootMap.put(key, value);
    }

    public String get() {
        return GloArgs.gson.toJson(this);
    }
}
