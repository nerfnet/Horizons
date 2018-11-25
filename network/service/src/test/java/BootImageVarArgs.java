import com.google.common.collect.Maps;
import net.havocmc.transport.GloArgs;
import net.havocmc.transport.bootstrap.BootstrapProperties;

import java.util.HashMap;

/**
 * Created by Giovanni on 25/02/2018.
 */
public class BootImageVarArgs {

    public static void main(String[] args) {
        BootstrapProperties properties = new BootstrapProperties();

        properties.put_MEMORY("AF_INET_ADDRESS", "127.0.0.1");
        properties.put_MEMORY("AF_INET_PORT", 3310);
        properties.put_MEMORY("MAX_OBJECT_QUEUE", 150);
        properties.put_MEMORY("MAX_TRANSPORTERS", 15);

        HashMap<String, Object> dbData = Maps.newHashMap();
        dbData.put("ADDRESS", "127.0.0.1");
        dbData.put("PORT", 1337);
        dbData.put("TYPE", "MONGO");

        HashMap<String, Object> mcData = Maps.newHashMap();
        mcData.put("MAX_PLAYERS", 250);
        mcData.put("CONNECTIONS_PER_IP", 1);

        properties.put_MEMORY("MINECRAFT", mcData);
        properties.put_MEMORY("DATABASE", dbData);

        System.out.println(GloArgs.gson.toJson(properties));
    }
}
