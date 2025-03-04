package net.sixik.sdmshoprework.common.shop;

import dev.ftb.mods.ftblibrary.snbt.SNBT;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmshoprework.SDMShopPaths;
import net.sixik.sdmshoprework.api.INBTSerializable;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntry;
import net.sixik.sdmshoprework.common.serializer.SerializerControl;

import java.util.*;

public class ShopBase implements INBTSerializable<CompoundTag> {

    public static ShopBase SERVER;
    public static ShopBase CLIENT = new ShopBase();

    public Component shopName = Component.empty();

    private final LinkedList<Runnable> saveTasks = new LinkedList<Runnable>();
    private final LinkedList<Runnable> deserializeTask = new LinkedList<Runnable>();

    private final List<ShopTab> shopTabs = new ArrayList<>();


    public List<ShopTab> getShopTabs() {
        return shopTabs;
    }

    public ShopTab createShopTab(CompoundTag nbt, int bits) {
        ShopTab tab = new ShopTab(this);
        tab.deserializeNBT(nbt, bits);
        shopTabs.add(tab);
        return tab;
    }

    public ShopTab getShopTab(UUID uuid){
        for (ShopTab shopTab : shopTabs) {
            if(Objects.equals(shopTab.shopTabUUID, uuid))
                return shopTab;
        }
        return null;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        SerializerControl.serializeVersion(nbt);

        nbt.putString("shopName", shopName.getString());

        ListTag tagShopTabs = new ListTag();
        for (ShopTab shopTab : shopTabs) {
            tagShopTabs.add(shopTab.serializeNBT());
        }
        nbt.put("shopTabs", tagShopTabs);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        Runnable runnable = () -> {
            shopTabs.clear();
            if(nbt.contains("shopName")) shopName = Component.literal(nbt.getString("shopName"));
            ListTag tagShopTabs = nbt.getList("shopTabs", 10);
            for (int i = 0; i < tagShopTabs.size(); i++) {
                ShopTab tab = new ShopTab(this);
                tab.deserializeNBT(tagShopTabs.getCompound(i));
                shopTabs.add(tab);
            }
        };

        deserializeTask.add(runnable);

        Iterator<Runnable> runnableIterator = deserializeTask.iterator();
        if(runnableIterator.hasNext()) {
            runnableIterator.next().run();
            runnableIterator.remove();
        }
    }

    public void saveShopToFile() {
        Runnable runnable = () -> {
            try {
                SNBT.write(SDMShopPaths.getFile(), serializeNBT());
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        saveTasks.add(runnable);

        Iterator<Runnable> runnableIterator = saveTasks.iterator();

        while (runnableIterator.hasNext()) {
            runnableIterator.next().run();
            runnableIterator.remove();
        }
    }

    public void syncShop(MinecraftServer server) {
        ShopDataHelper.syncShopData(server);
    }

    public void syncShop(ServerPlayer player) {
        ShopDataHelper.syncShopData(player);
    }

    public boolean changeTab(UUID uuid, CompoundTag nbt) {
        ShopTab tab = getShopTab(uuid);
        if(tab!= null) {
            tab.deserializeNBT(nbt);
            return true;
        }
        return false;
    }

    public boolean deleteTab(UUID uuid) {
        ShopTab tab = getShopTab(uuid);
        if(tab!= null) {
            return shopTabs.remove(tab);
        }
        return false;
    }

    public boolean changeEntry(UUID uuid, CompoundTag nbt) {
        for (ShopTab shopTab : shopTabs) {
            Optional<AbstractShopEntry> obj = shopTab.getTabEntry().stream().filter(s -> s.entryUUID.equals(uuid)).findFirst();
            if(obj.isPresent()) {
                obj.get().deserializeNBT(nbt);
                return true;
            }
        }
        return false;
    }

    public boolean deleteEntry(UUID uuid) {
        for (ShopTab shopTab : shopTabs) {
            if(shopTab.getTabEntry().removeIf(s -> s.entryUUID.equals(uuid)))
                return true;
        }
        return false;
    }
}
