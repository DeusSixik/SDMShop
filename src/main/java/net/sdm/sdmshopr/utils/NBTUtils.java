package net.sdm.sdmshopr.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import net.sdm.sdmshopr.api.EntryTypeRegister;
import net.sdm.sdmshopr.api.IEntryType;
import org.jetbrains.annotations.Nullable;
import org.openjdk.nashorn.internal.ir.BreakableNode;

public class NBTUtils {

    @Nullable
    public static <T extends IEntryType> T getEntryType(CompoundTag nbt){
        if(nbt.contains("type")) {
            String type = nbt.getString("type");

            if(EntryTypeRegister.TYPES.containsKey(type)){
                IEntryType entryType = EntryTypeRegister.TYPES.get(type).copy();
                if(ModList.get().isLoaded(entryType.getModID())) {
                    entryType.deserializeNBT(nbt);
                    return (T) entryType;
                } else return null;
            }
        }

        return null;
    }

    public static void putItemStack(CompoundTag nbt, String key, ItemStack stack){
        CompoundTag tagItem = new CompoundTag();
        if(stack.hasTag() && stack.getTag().contains("Item")){
            tagItem = stack.getTag().getCompound("Item");
        } else{
            stack.save(tagItem);
        }

        if(tagItem.size() == 2 && tagItem.getInt("Count") == 1){
            nbt.putString(key,tagItem.getString("id"));
        } else {
            nbt.put(key,tagItem);
        }
    }

    public static ItemStack getItemStack(CompoundTag nbt, String key){
        Tag nbt1 = nbt.get(key);
        if(nbt1 instanceof CompoundTag){
            return readItem((CompoundTag) nbt1);
        } else if (nbt1 instanceof StringTag tag) {
            CompoundTag tag1 = new CompoundTag();
            tag1.putString("id", nbt1.getAsString());
            tag1.putByte("Count", (byte)1);
            return readItem(tag1);
        } else{
            return ItemStack.EMPTY;
        }
    }


    protected static ItemStack readItem(CompoundTag tag){
        if(tag.isEmpty()) return ItemStack.EMPTY;
        else {
            ResourceLocation id = new ResourceLocation(tag.getString("id"));
            if (id.equals(new ResourceLocation("minecraft:air"))) {
                return ItemStack.EMPTY;
            } else {
                Item item = (Item) ForgeRegistries.ITEMS.getValue(id);
                if (item == Items.AIR) {
                    ItemStack stack = new ItemStack(ItemStack.EMPTY.getItem());
                    stack.addTagElement("Item", tag);
                    return stack;
                } else {
                    return ItemStack.of(tag);
                }
            }
        }
    }
}
