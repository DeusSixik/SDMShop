package net.sixik.sdmshoprework.common.config;

public class ConfigFile {

    public static ConfigFile SERVER = new ConfigFile();
    public static ConfigFile CLIENT = new ConfigFile();

    //    public static boolean disableButton = false;
    public boolean disableKeyBind = false;

    public String defaultBackground = "#5555FF";
    public String defaultShadow = "#5555FF";
    public String defaultReact = "#5555FF";
    public String defaultStoke = "#5555FF";
    public String defaultTextColor = "#5555FF";
    public String colorSelectTab = "#5555FF";


    public void load() {

    }
}
