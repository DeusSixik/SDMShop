package net.sixik.sdmshop;

import dev.architectury.platform.Platform;
import net.sixik.sdmeconomy.SDMEconomy;

import java.io.IOException;
import java.nio.file.Path;

public class SDMShopPaths {

    public static void initFilesAndFolders() {
        if(!getModFolder().toFile().exists()){
            getModFolder().toFile().mkdir();
        }

        if(!getFile().toFile().exists()) {
            try {
                getFile().toFile().createNewFile();
            } catch (IOException e) {
                SDMEconomy.printStackTrace("", e);
            }
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
        return Platform.getConfigFolder().resolve("SDMShop");
    }

    public static Path getModConfig(){
        return getModFolder().resolve("sdmshop-common.snbt");
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
