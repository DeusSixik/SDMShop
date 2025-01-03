package net.sixik.sdmshoprework.common.shop;

import dev.ftb.mods.ftblibrary.snbt.SNBT;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmshoprework.SDMShopPaths;
import net.sixik.sdmshoprework.api.INBTSerializable;
import net.sixik.sdmshoprework.common.serializer.SerializerControl;
import net.sixik.sdmshoprework.network.client.SyncShopS2C;
import net.sixik.sdmshoprework.network.sync.SendClearTabsS2C;
import net.sixik.sdmshoprework.network.sync.SendShopTabS2C;

import java.util.*;
import java.util.concurrent.Future;

public class ShopBase implements INBTSerializable<CompoundTag> {

    public static ShopBase SERVER;
    public static ShopBase CLIENT = new ShopBase();

    private final LinkedList<Runnable> saveTasks = new LinkedList<Runnable>();
    private final LinkedList<Runnable> deserializeTask = new LinkedList<Runnable>();

    private final List<ShopTab> shopTabs = new ArrayList<>();


    public List<ShopTab> getShopTabs() {
        return shopTabs;
    }

    public ShopTab createShopTab(CompoundTag nbt) {
        ShopTab tab = new ShopTab(this);
        tab.deserializeNBT(nbt);
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
            ListTag tagShopTabs = nbt.getList("shopTabs", 10);
            for (int i = 0; i < tagShopTabs.size(); i++) {
                ShopTab tab = new ShopTab(this);
                tab.deserializeNBT(tagShopTabs.getCompound(i));
                shopTabs.add(tab);
            }
//            if (SerializerControl.isOldVersion(nbt)) {
//                SerializerControl.deserializeVersion(nbt, this);
//            } else {
//                shopTabs.clear();
//                ListTag tagShopTabs = nbt.getList("shopTabs", 10);
//                for (int i = 0; i < tagShopTabs.size(); i++) {
//                    ShopTab tab = new ShopTab(this);
//                    tab.deserializeNBT(tagShopTabs.getCompound(i));
//                    shopTabs.add(tab);
//                }
//            }
        };

        deserializeTask.add(runnable);

        Iterator<Runnable> runnableIterator = deserializeTask.iterator();
        if(runnableIterator.hasNext()) {
            runnableIterator.next().run();
            runnableIterator.remove();
        }
    }


    public List<CompoundTag> serializeTabs() {
        List<CompoundTag> compoundTags = new ArrayList<>();
        for (ShopTab shopTab : shopTabs) {
            compoundTags.add(shopTab.serializeNBT());
        }
        return compoundTags;
    }

    public List<ShopTab> deserializeTabs(List<CompoundTag> compoundTags) {
        List<ShopTab> shopTabs = new ArrayList<>();
        for (CompoundTag compoundTag : compoundTags) {
            ShopTab t = new ShopTab(this);
            t.deserializeNBT(compoundTag);
            shopTabs.add(t);
        }
        return shopTabs;
    }

    public CompoundTag serializeTab(UUID id) {
        ShopTab shopTab = getShopTab(id);
        if(shopTab != null)
            return shopTab.serializeNBT();
        return new CompoundTag();
    }

    public ShopTab deserializeTab(CompoundTag nbt) {
        if(nbt.isEmpty()) return new ShopTab(this);

        ShopTab shopTab = new ShopTab(this);
        shopTab.deserializeNBT(nbt);
        return shopTab;
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
}
