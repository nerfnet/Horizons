package net.havocmc.horizons.game.world;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import org.bukkit.Location;
import org.bukkit.World;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * Created by Giovanni on 13/03/2018.
 */
public class SchematicLoader {

    private final String schematicFile;
    private final World world;

    public SchematicLoader(@Nonnull String file, @Nonnull World world) {
        this.schematicFile = file;
        this.world = world;
    }

    public void loadSchematicAt(@Nonnull Vector vector, @Nonnull Consumer<Location> completionFuture) throws DataException, IOException, MaxChangedBlocksException {
        // This is from FAWEHandler#50, however it has a few changes.
        EditSession editSession = new EditSessionBuilder(FaweAPI.getWorld("islands")).fastmode(true).autoQueue(true).checkMemory(false).build();
        editSession.enableQueue();

        CuboidClipboard clipboard = SchematicFormat.MCEDIT.load(new File(schematicFile));
        clipboard.paste(editSession, vector, true);

        editSession.flushQueue();
        completionFuture.accept(new Location(world, vector.getX(), vector.getY(), vector.getZ()));
    }
}
