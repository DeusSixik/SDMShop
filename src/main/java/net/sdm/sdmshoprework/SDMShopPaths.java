package net.sdm.sdmshoprework;

import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Path;

public class SDMShopPaths {

    public static void initFilesAndFolders() {
        if(!getModFolder().toFile().exists()){
            getModFolder().toFile().mkdir();
        }

        if(!getTagFile().toFile().exists()) {
            try {
                getTagFile().toFile().createNewFile();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static Path getModFolder(){
        return FMLPaths.CONFIGDIR.get().resolve("SDMShop");
    }

    public static Path getOldFile(){
        return FMLPaths.CONFIGDIR.get().resolve("sdmshop.snbt");
    }

    public static Path getTagFile(){
        return getModFolder().resolve("customization.json");
    }
    public static Path getFile() {
        return getModFolder().resolve("sdmshop.snbt");
    }


    public static Path getFileClient() {
        return getModFolder().resolve("sdmshop-data-client.snbt");
    }
}
