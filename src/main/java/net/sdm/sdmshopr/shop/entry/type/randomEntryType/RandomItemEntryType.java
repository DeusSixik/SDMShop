package net.sdm.sdmshopr.shop.entry.type.randomEntryType;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.sdm.sdmshopr.api.IEntryType;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class RandomItemEntryType implements IEntryType {

    public List<RandomEntry> randomEntryList = new ArrayList<>();


    @Override
    public boolean isSellable() {
        return false;
    }

    @Override
    public boolean isCountable() {
        return false;
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public void getConfig(ConfigGroup group) {

    }

    @Override
    public Icon getCreativeIcon() {
        return null;
    }

    @Override
    public String getID() {
        return "";
    }

    @Override
    public IEntryType copy() {
        return null;
    }

    @Override
    public Component getTranslatableForContextMenu() {
        return null;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

    }
}
