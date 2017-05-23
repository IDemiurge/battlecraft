package main.game.battlecraft.logic.meta.scenario.hq;

import java.util.List;

/**
 * Created by JustMe on 5/22/2017.
 */
public interface ShopInterface {
    public List<String> getTabs();
    public List<String> getGroupLists(String tabName);
    public List<String> getTextures(String groupList);
    public String getName();
    public String getGold();
    public String getIcon();
}