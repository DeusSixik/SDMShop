package net.sixik.sdmshop.utils;

import com.google.common.hash.Hashing;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.sixik.sdmshop.SDMShop;

import java.io.ByteArrayOutputStream;

public class HashUtils {

    public static String calculateHash(final CompoundTag tags) {
        try {
            final ByteArrayOutputStream btc = new ByteArrayOutputStream();
            NbtIo.writeCompressed(tags, btc);
            final byte[] data = btc.toByteArray();
            return Hashing.sha256().hashBytes(data).toString();
        } catch (Exception e) {
            SDMShop.LOGGER.error("Error calculate hash", e);
            return "error_hash";
        }
    }

}
