package net.havocmc.horizons.network;

import io.netty.channel.Channel;
import net.havocmc.horizons.Horizons;
import net.havocmc.horizons.HorizonsRuntime;
import net.havocmc.transport.RuntimeWrapper;
import net.havocmc.transport.pool.Transporter;
import net.havocmc.transport.proto.BufferedObject;
import net.havocmc.transport.proto.signal.connection.Exit01;

/**
 * Created by Giovanni on 01/04/2018.
 */
public class Proto01Transporter implements Transporter<BufferedObject, RuntimeWrapper> {

    private final HorizonsRuntime runtime;

    public Proto01Transporter(HorizonsRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void inWrite(BufferedObject object, Channel channel) {
        if (object instanceof Exit01) {
            Exit01 exit01 = (Exit01) object;
            Horizons.getInstance().close();
        }
    }

    @Override
    public String id() {
        return "0x01";
    }

    @Override
    public RuntimeWrapper getRuntime() {
        return runtime;
    }
}
