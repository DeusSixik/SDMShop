package net.sixk.sdmshop.shop.widgets;

import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleButton;
import net.minecraft.network.chat.Component;
import net.sixk.sdmshop.SDMShop;
import net.sixk.sdmshop.data.config.ConfigFile;
import net.sixk.sdmshop.shop.Tovar.AddTovar.AddTovarPanel;
import net.sixk.sdmshop.shop.Tovar.TovarList;
import net.sixk.sdmshop.shop.Tovar.TovarPanel;
import net.sixk.sdmshop.shop.modern.ModernAddTovarPanel;
import net.sixk.sdmshop.shop.modern.ModernTovarPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EntryPanel extends Panel {

    public List<TovarPanel> tovarRenderList = new ArrayList<>();
    public Panel panel;
    public String tab;
    public TovarPanel tovarRender = null;
    public SimpleButton addTovar;
    int w1 = 0;
    int w2 = 0;

    public EntryPanel(Panel panel, String tab) {
        super(panel);
        this.panel = panel;
        this.tab = tab;
    }

    @Override
    public void addWidgets() {

        w1 = 0;
        w2 = 0;

        for (int w = 0; w< TovarList.CLIENT.tovarList.size(); w++) {

            if(ConfigFile.CLIENT.style){
                tovarRender = new ModernTovarPanel(this, TovarList.CLIENT.tovarList.get(w));
            }else{
                tovarRender = new TovarPanel(this, TovarList.CLIENT.tovarList.get(w));
            }
            if( w1 == (panel.width) / 30) {

                w1 = 0;
                w2++;

            }

            if((Objects.equals(tab, TovarList.CLIENT.tovarList.get(w).tab) || Objects.equals(tab, "All")) && (TovarList.CLIENT.tovarList.get(w).limit != 0 || SDMShop.isEditMode())) {
                String i = TovarList.CLIENT.tovarList.get(w).abstractTovar.getTitel().toLowerCase();
                if(Search.searchContent.isEmpty() || i.contains(Search.searchContent.toLowerCase())){
                    add(tovarRender);
                    tovarRenderList.add(tovarRender);
                    tovarRender.setPos(2 + 30 * w1, 2 + 45 * w2);
                    w1++;
                }
            }
        }

        if( w1 == (panel.width) / 30 ) {

            w1 = 0;
            w2++;

        }

        if(SDMShop.isEditMode())
            add(addTovar = new SimpleButton(this, Component.translatable("sdm_shop.shop_page.add_tovar"), Icons.ADD,((simpleButton, mouseButton) -> {
                if(ConfigFile.CLIENT.style) new ModernAddTovarPanel(tab.equals("All") ? null : tab).openGui();
                    else new AddTovarPanel(tab.equals("All") ? null : tab).openGui();
            })));
    }

    @Override
    public void alignWidgets() {
        for (int n = 0; n < tovarRenderList.size(); n++) {
            tovarRenderList.get(n).setSize(30, 44);
        }

        if(SDMShop.isEditMode()) {
            addTovar.setPos(2 + 30 * w1, 7 + 45 * w2);
            addTovar.setSize(20, 20);
        }
    }

}
