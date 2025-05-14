package net.sixk.sdmshop.shop.modern;

import net.sixk.sdmshop.shop.Tovar.AddTovar.AddTovarPanel;
import net.sixk.sdmshop.shop.Tovar.Tovar;
import org.jetbrains.annotations.Nullable;

public class ModernAddTovarPanel extends AddTovarPanel {



    public ModernAddTovarPanel(@Nullable String tab) {
        super(tab);
        base = new ModernAddProperties(this,tab);
    }
    public ModernAddTovarPanel(Tovar tovar){
        super(tovar);
        base = new ModernAddProperties(this,tovar);
    }

    @Override
    public void addWidgets() {
        this.typePanel = new ModernSelectTypeTovarPanel(this);
        super.addWidgets();
    }
}
