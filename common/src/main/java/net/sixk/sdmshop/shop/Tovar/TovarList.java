package net.sixk.sdmshop.shop.Tovar;

import com.mojang.datafixers.util.Function7;
import dev.ftb.mods.ftblibrary.icon.Icon;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.sixk.sdmshop.api.DataSerializerCompound;
import net.sixk.sdmshop.shop.Tovar.TovarType.TovarTypeRegister;
import net.sixk.sdmshop.utils.ShopNBTUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TovarList implements DataSerializerCompound {

    public List<AbstractTovar> tovarList = new ArrayList<>();
    public static TovarList SERVER;
    public static TovarList CLIENT = new TovarList() ;

    public TovarList(){}

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();

        ShopNBTUtils.putList(nbt, "tovarList", tovarList, s -> s.serializeNBT(provider));

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, HolderLookup.Provider provider) {
        tovarList = ShopNBTUtils.getList(nbt, "tovarList", tag -> {
            if(tag instanceof CompoundTag nbt1) {
                if(!nbt1.contains("tovarType")) return null;

                Optional<Function7<UUID, Icon, String, String, Integer, Long, Boolean, AbstractTovar>> opt = TovarTypeRegister.getType(nbt1.getString("tovarType"));
                if(opt.isEmpty()) return null;

                AbstractTovar w2 = opt.get().apply(UUID.randomUUID(), Icon.empty(), "", "", 0, 0L, false);
                w2.deserializeNBT(nbt1, provider);
                return w2;
            }

            return null;
        });
    }
}
