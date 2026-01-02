package net.sixik.sdmshop.shop.entry_types.configs;

import dev.ftb.mods.ftblibrary.config.ItemStackConfig;
import dev.ftb.mods.ftblibrary.config.ui.SelectItemStackScreen;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.sixik.sdmshop.client.screen_new.components.creator.ShopCreatorComponentModalPanel;
import net.sixik.sdmshop.client.screen_new.components.creator.custom.CustomEntryConfig;
import net.sixik.sdmshop.client.screen_new.components.creator.data.ShopCreatorComponentData;
import net.sixik.sdmshop.client.screen_new.components.creator.entry.ShopCreatorEntryPanel;
import net.sixik.sdmshop.client.screen_new.components.creator.entry.ShopCreatorEntryTypesPanel;
import net.sixik.sdmshop.shop.ShopEntry;

import java.util.List;
import java.util.function.BooleanSupplier;

public class ItemEntryTypeConfig extends CustomEntryConfig {

    private static final List<Component> tooltips = new ObjectArrayList<>();

    protected SimpleTextButton itemButton;

    protected SelectItemStackScreen selectItemStackScreen;

    @Override
    @Environment(EnvType.CLIENT)
    public void addWidgets(ShopCreatorEntryPanel panel, BooleanSupplier musBeRender, ShopCreatorComponentData data) {
        final ItemStack lastItem = data.Entry.lastSelectedItemStack;

        Icon itemIcon;

        if(lastItem.isEmpty()) {
            itemIcon = Icons.ADD;
        } else itemIcon = ItemIcon.getItemIcon(lastItem);

        panel.add(itemButton = new SimpleTextButton(panel, Component.empty(), itemIcon) {
            @Override
            public void onClicked(MouseButton button) {
                final ItemStackConfig config = new ItemStackConfig(false, false);
                config.setValue(data.Entry.lastSelectedItemStack);

                selectItemStackScreen = new SelectItemStackScreen(config, (callback) -> {
                    if(callback) {
                        final ItemStack item = config.getValue();
                        data.Entry.lastSelectedItemStack = item;
                        itemButton.setIcon(ItemIcon.getItemIcon(item));
                        tooltips.clear();
                        GuiHelper.addStackTooltip(item, tooltips);
                    }

                    selectItemStackScreen.closeGui();
                });
                selectItemStackScreen.openGui();
            }

            @Override
            public void addMouseOverText(TooltipList list) {
                if(data.Entry.lastSelectedItemStack.isEmpty()) {
                    list.add(Component.literal("Select Item"));
                } else {
                    tooltips.forEach(list::add);
                }
            }
        });
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void alignWidgets(ShopCreatorEntryPanel panel,
                             ShopCreatorEntryTypesPanel entryTypesPanel,
                             ShopCreatorComponentData data
    ) {
        itemButton.posX = 4;
        itemButton.posY = entryTypesPanel.height + 4;
        itemButton.setSize(32, 32);
    }



    @Override
    public void applyCreate(ShopCreatorComponentData data, ShopEntry shopEntry) {

    }
}
