package net.sixik.sdmshop.client.render;

import net.sixik.sdmuilib.client.utils.math.Vector2;

public final class BuyerRenderVariable {
    public Vector2 pos;
    public int iconSize;

    public BuyerRenderVariable(Vector2 pos, int iconSize) {
        this.pos = pos;
        this.iconSize = iconSize;
    }
}
