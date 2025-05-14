package net.sixk.sdmshop.shop.Tovar.TovarType;

import net.sixk.sdmshop.api.IConstructor;
import net.sixk.sdmshop.shop.Tovar.AbstractTovar;

import java.util.HashMap;

public class TovarTypeRegister {

    public static HashMap<String, IConstructor<AbstractTovar>> TOVAR_MAP = new HashMap<>();

    public static AbstractTovar registerTovar(IConstructor<AbstractTovar> constructor) {
        AbstractTovar tovar = constructor.create();
        if(!TOVAR_MAP.containsKey(tovar.getID())) {
            TOVAR_MAP.put(tovar.getID(), constructor);
        }

        return tovar;
    }


}
