package net.sixk.sdmshop.shop.Tovar.TovarType;

import com.mojang.datafixers.util.Function6;
import net.sixk.sdmshop.SDMShop;
import net.sixk.sdmshop.shop.Tovar.AbstractTovar;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TovarTypeRegister {

    protected static final Map<String, Function6<UUID, String, String, Integer, Long, Boolean,AbstractTovar>> TYPES = new HashMap<>();

    public static void register(String id, Function6<UUID, String, String, Integer, Long, Boolean,AbstractTovar> func){
        if(TYPES.containsKey(id)) {
            SDMShop.LOGGER.error("SellerType with {} id already registered!", id);
            return;
        }
        TYPES.put(id, func);
    };

    public static Map<String, Function6<UUID, String, String, Integer, Long, Boolean,AbstractTovar>> getTypes(){
        return new HashMap<>(TYPES);
    }

    public static Optional<Function6<UUID, String, String, Integer, Long, Boolean,AbstractTovar>> getType(String id) {
       return Optional.ofNullable(TYPES.get(id));
    }

}
