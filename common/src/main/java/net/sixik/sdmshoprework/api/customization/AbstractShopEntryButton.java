package net.sixik.sdmshoprework.api.customization;


import com.mojang.blaze3d.vertex.PoseStack;
import net.sixik.sdmshoprework.api.IIdentifier;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntry;

@Deprecated
public abstract class AbstractShopEntryButton implements IIdentifier {

    public AbstractShopEntry entry;

    public AbstractShopEntryButton(AbstractShopEntry entry){
        this.entry = entry;
    }

    public abstract String getID();

    public abstract void draw(PoseStack graphics, int x, int y, int width, int height);
}
