package net.sdm.sdmshopr.api;

import net.sdm.sdmshopr.api.tags.ITag;

import java.util.LinkedHashMap;
import java.util.Map;

public interface TagsRegister {
    Map<String, ITag> TAGS = new LinkedHashMap();


    static ITag register(ITag provider) {
        return (ITag) TAGS.computeIfAbsent(provider.getID(), (id) -> {
            return provider;
        });
    }

}
