package net.havocmc.horizons.game.api;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.UUID;

/**
 * Created by Giovanni on 07/04/2018.
 */
public enum SkullPrefab {

    CHERRY("d525707696bcd15a173056fa39296e80ff41168bb0add552f4523e2558a3119"),
    APPLE("cbb311f3ba1c07c3d1147cd210d81fe11fd8ae9e3db212a0fa748946c3633"),
    GLOBE("9dfc8932865fd57d9d2365f1ae2d475135d746b2af15abd33ffc2a6abd3628"),
    BREAD("f3487d457f9062d787a3e6ce1c4664bf7402ec67dd111256f19b38ce4f670");

    public static final String MOJANG_TEXTURES = "http://textures.minecraft.net/texture/";
    private String b64String;

    SkullPrefab(String base64String) {
        this.b64String = base64String;
    }

    public static ItemStack createSkullFromURL(String par1) {
        String url = MOJANG_TEXTURES + par1;
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        if (url.isEmpty())
            return skull;

        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        assert profileField != null;
        profileField.setAccessible(true);
        try {
            profileField.set(skullMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        skull.setItemMeta(skullMeta);
        return skull;
    }

    public ItemStack getSkull() {
        return createSkullFromURL(getTexturesURL());
    }

    public String getTexturesURL() {
        return MOJANG_TEXTURES + b64String;
    }
}
