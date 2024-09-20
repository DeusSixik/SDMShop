package net.sixik.sdmshoprework.api.customization;


import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshoprework.api.IIdentifier;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntry;

@Deprecated
public abstract class AbstractShopEntryButton implements IIdentifier {

    public AbstractShopEntry entry;

    public AbstractShopEntryButton(AbstractShopEntry entry){
        this.entry = entry;
    }

    public abstract String getID();

    public abstract void draw(GuiGraphics graphics, int x, int y, int width, int height);
}
