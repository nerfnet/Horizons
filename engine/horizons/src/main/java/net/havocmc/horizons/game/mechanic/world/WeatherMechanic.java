package net.havocmc.horizons.game.mechanic.world;

import com.google.gson.internal.LinkedTreeMap;
import net.havocmc.horizons.Horizons;
import net.havocmc.horizons.game.api.Properties;
import net.havocmc.horizons.game.api.mechanic.Mechanic;
import net.havocmc.transport.concurrent.TransportThreadFactory;
import org.bukkit.WeatherType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.io.File;

/**
 * Created by Giovanni on 06/03/2018.
 */
public class WeatherMechanic implements Mechanic {

    private Properties properties;

    private boolean constantType = false;
    private WeatherType weatherType = WeatherType.CLEAR;

    @Override
    public void bootstrap() {
        listen();

        LinkedTreeMap<String, Object> weatherProperties = properties.readMap("WEATHER");
        constantType = properties.helpRead("useConstantType", Boolean.class, weatherProperties);

        String rawWeather = properties.helpRead("constantType", String.class, weatherProperties);

        try {
            weatherType = WeatherType.valueOf(rawWeather);
        } catch (Exception e) {
            Horizons.runtime().logger().warning("Failed to read the properties of a mechanic: " + e.getMessage());
        }
    }

    @Override
    public void exit() {

    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (!constantType) return;

        if (true) { // Temp.
            return;
        }

        switch (weatherType) {
            case CLEAR:
                event.getWorld().setStorm(false);
                break;
            case DOWNFALL:
                event.getWorld().setStorm(true);
                break;
        }
    }


    @Override
    public void sideLoadProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public File getFile() {
        return new File(getName().toLowerCase());
    }

    @Override
    public String getName() {
        return "WEATHERTIME.mech";
    }

    @Override
    public TransportThreadFactory getThreadFactory() {
        return Horizons.runtime().getThreadFactory();
    }
}
