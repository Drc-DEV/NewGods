package pro.dracarys.newgods.data;

import org.bukkit.Material;

public class ItemSacrifice extends Sacrifice {

    Material type;

    public ItemSacrifice(int number, int reward, Material type) {
        super(number, reward);
        this.type = type;
    }

    public Material getType() {
        return type;
    }

    public void setType(Material type) {
        this.type = type;
    }
}
