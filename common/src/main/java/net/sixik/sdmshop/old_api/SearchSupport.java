package net.sixik.sdmshop.old_api;

import net.minecraft.tags.TagKey;

public interface SearchSupport {

    boolean isSearch(String search);


    interface ByTag {
        boolean search(TagKey<?> tagKey);
    }

    interface ByTooltip {
        boolean search(String text);
    }
}
