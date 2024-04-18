package net.sdm.sdmshopr.tags;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.api.tags.ITag;
import net.sdm.sdmshopr.tags.types.CustomizationTag;
import org.openjdk.nashorn.internal.parser.JSONParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagFileParser {

    public static void writeNewFile(){
        try {
            Gson gson = new Gson();
            JsonWriter writer = new JsonWriter(new FileWriter(SDMShopR.getTagFile().toFile()));
            writer.setLenient(true);
            gson.toJson(getJsonExample(), writer);
            writer.close();

        } catch (IOException e) {
            SDMShopR.LOGGER.error(e.toString());
        }

    }

    public static JsonElement getJsonExample(){
        return JsonParser.parseString("{" +
                "tags:[{\"id\" : \"SDM\",\"drawable\" : [{\"type\":\"react\",\"width\" : 20,\"height\" : 10,\"posX\" : 0,\"posY\" : 0," +
                "          \"color\" : \"#c5d0e6\"" +
                "        }," +
                "        {" +
                "          \"width\" : 18," +
                "          \"height\" : 8," +
                "          \"posX\" : 1," +
                "          \"posY\" : 1," +
                "          \"color\" : \"#ffffff\"" +
                "        }" +
                "      ]" +
                "    }" +
                "  ]" +
                "}");
    }
    
    public static Map<String, ITag> getTags(){
        Map<String, ITag> tags = new HashMap<>();
        try {
            JsonElement element = JsonParser.parseReader(new FileReader(SDMShopR.getTagFile().toFile()));

            if(element.getAsJsonObject().get("tags").isJsonArray()){
                JsonArray array = element.getAsJsonObject().get("tags").getAsJsonArray();
                for (JsonElement d1 : array) {
                    if(d1.isJsonObject()){
                        JsonObject f1 = d1.getAsJsonObject();

                        if(!f1.has("id")) continue;

                        if(f1.has("drawable")){
                            CustomizationTag tag = new CustomizationTag(f1.get("id").getAsString());
                            for (JsonElement drawable : f1.get("drawable").getAsJsonArray()) {
                                if(drawable.isJsonObject()){
                                    JsonObject h1 = drawable.getAsJsonObject();

                                    if(!h1.has("width")) continue;
                                    if(!h1.has("height")) continue;
                                    if(!h1.has("posX")) continue;
                                    if(!h1.has("posY")) continue;
                                    if(!h1.has("color")) continue;

                                    boolean isReact = false;

                                    if(h1.has("type")) {
                                        String z1 = h1.get("type").getAsString();
                                        if(z1.equals("react")) isReact  = true;
                                    }

                                    tag.add(new CustomizationTag.Drawable(
                                            h1.get("width").getAsInt(),
                                            h1.get("height").getAsInt(),
                                            h1.get("posX").getAsInt(),
                                            h1.get("posY").getAsInt(),
                                            h1.get("color").getAsString(),
                                            isReact
                                        ));
                                }
                            }
                            tags.put(f1.get("id").getAsString(), tag);
                        }
                    }
                }
            }


        } catch (FileNotFoundException e) {
            SDMShopR.LOGGER.error(e.toString());
        }

        return tags;
    }
}
