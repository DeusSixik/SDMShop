package net.sdm.sdmshopr.config;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ClientShopData implements INBTSerializable<CompoundTag> {

    public Path path;
    public List<String> favoriteCreator = new ArrayList<>();


    public ClientShopData(Path path){
        this.path = path;
    }


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        ListTag tags = new ListTag();
        for (String s : favoriteCreator) {
            tags.add(StringTag.valueOf(s));
        }
        nbt.put("favoriteCreator", tags);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        favoriteCreator.clear();
        ListTag tags = (ListTag) nbt.get("favoriteCreator");
        for (Tag tag : tags) {
            favoriteCreator.add(((StringTag)tag).getAsString());
        }
    }
}
