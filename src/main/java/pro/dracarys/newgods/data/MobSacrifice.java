package pro.dracarys.newgods.data;

import org.bukkit.entity.EntityType;

public class MobSacrifice extends Sacrifice {

    EntityType type;

    public MobSacrifice(int number, int reward, EntityType type) {
        super(number, reward);
        this.type = type;
    }

    public EntityType getType() {
        return type;
    }

    public void setType(EntityType type) {
        this.type = type;
    }
}
