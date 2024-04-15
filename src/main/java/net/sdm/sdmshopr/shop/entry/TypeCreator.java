package net.sdm.sdmshopr.shop.entry;

import dev.ftb.mods.ftblibrary.ui.ContextMenuItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.sdm.sdmshopr.SDMShopRIntegration;
import net.sdm.sdmshopr.client.MainShopScreen;
import net.sdm.sdmshopr.network.CreateShopEntry;
import net.sdm.sdmshopr.shop.entry.type.CommandEntryType;
import net.sdm.sdmshopr.shop.entry.type.ItemEntryType;
import net.sdm.sdmshopr.shop.entry.type.integration.GameStagesEntryType;
import net.sdm.sdmshopr.shop.entry.type.integration.QuestEntryType;
import net.sdm.sdmshopr.shop.entry.type.integration.SkillTreeEntryType;

import java.util.ArrayList;
import java.util.List;

import static net.sdm.sdmshopr.SDMShopRIntegration.GameStagesLoaded;

public class TypeCreator {

    public static List<ContextMenuItem> createContext(MainShopScreen screen){
        List<ContextMenuItem> contextMenu = new ArrayList<>();

        contextMenu.add(new ContextMenuItem(Component.translatable("sdm.shop.entry.add.context.item"), ItemEntryType.of(ItemStack.EMPTY).getCreativeIcon(), () -> {
            ShopEntry<ItemEntryType> create = new ShopEntry<>(screen.selectedTab, ItemEntryType.of(ItemStack.EMPTY), 1,1,false);
            screen.selectedTab.shopEntryList.add(create);
            screen.refreshWidgets();
            new CreateShopEntry(create).sendToServer();
        }));
        contextMenu.add(new ContextMenuItem(Component.translatable("sdm.shop.entry.add.context.command"), CommandEntryType.of("","").getCreativeIcon(), () -> {
            ShopEntry<CommandEntryType> create = new ShopEntry<>(screen.selectedTab, CommandEntryType.of("", ""), 1,1,false);
            screen.selectedTab.shopEntryList.add(create);
            screen.refreshWidgets();
            new CreateShopEntry(create).sendToServer();
        }));

        createContextIntegration(screen, contextMenu);

        return contextMenu;
    }

    public static void createContextIntegration(MainShopScreen screen, List<ContextMenuItem> contextMenu){
        if(SDMShopRIntegration.FTBQuestLoaded){
            contextMenu.add(new ContextMenuItem(Component.translatable("sdm.shop.entry.add.context.integration.quest"), QuestEntryType.of("").getCreativeIcon(), () -> {
                ShopEntry<QuestEntryType> create = new ShopEntry<>(screen.selectedTab, QuestEntryType.of(""), 1,1,false);
                screen.selectedTab.shopEntryList.add(create);
                screen.refreshWidgets();
                new CreateShopEntry(create).sendToServer();
            }));
        }
        if(SDMShopRIntegration.GameStagesLoaded){
            contextMenu.add(new ContextMenuItem(Component.translatable("sdm.shop.entry.add.context.integration.gamestage"), new GameStagesEntryType("").getCreativeIcon(), () -> {
                ShopEntry<GameStagesEntryType> create = new ShopEntry<>(screen.selectedTab, new GameStagesEntryType(""), 1,1,false);
                screen.selectedTab.shopEntryList.add(create);
                screen.refreshWidgets();
                new CreateShopEntry(create).sendToServer();
            }));
        }
        if(SDMShopRIntegration.PSTLoaded){
            contextMenu.add(new ContextMenuItem(Component.translatable("sdm.shop.entry.add.context.integration.passiveskilltree"), new SkillTreeEntryType().getCreativeIcon(), () -> {
                ShopEntry<SkillTreeEntryType> create = new ShopEntry<>(screen.selectedTab, new SkillTreeEntryType(), 1,1,false);
                screen.selectedTab.shopEntryList.add(create);
                screen.refreshWidgets();
                new CreateShopEntry(create).sendToServer();
            }));
        }
    }
}
