package net.sixik.sdmshoprework.common.theme;

import dev.ftb.mods.ftblibrary.icon.Color4I;

import java.util.HashMap;
import java.util.List;

public class ShopThemeConstructor {

    public List<String> IDs = List.of(
            "background",
            "shadow",
            "react",
            "stoke",
            "button_tab_background",
            "button_tab_shadow",
            "button_tab_react",
            "button_tab_stoke",
            "button_tab_selected_color",
            "button_entry_background",
            "button_entry_shadow",
            "button_entry_react",
            "button_entry_stoke",
            "money_text_color",
            "money_entry_text_color",
            "text_color",
            "text_entry_color",
            "text_tab_color"
    );

    public static void load() {
//        HashMap<String, Color4I> map = new HashMap<String, Color4I>();
//
//        File file = SDMShopPaths.getFileClient().toFile();
//        if (file.exists()) {
//            try (InputStream fis = new FileInputStream(file);
//                 JsonReader reader = Json.createReader(fis)) {
//
//                JsonObject jsonObject = reader.readObject();
//
//                // Проходим по элементам и записываем их в массив
//                for (Map.Entry<String, JsonValue> key : jsonObject.entrySet()) {
//                    map.put(key.getKey(), Color4I.fromString(jsonObject.getString(key.getKey())));
//                }
//
//            } catch (IOException | JsonParsingException e) {
//                e.printStackTrace();
//            }
//        }


    }


    public static class Theme {
        public HashMap<String, Color4I> colors = new HashMap<String, Color4I>();

        public Theme(HashMap<String, Color4I> colors){
            this.colors = colors;
        }
    }
}
