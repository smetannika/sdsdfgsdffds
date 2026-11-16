package dev.celestiallite.module;

import dev.celestiallite.util.Friends;
import net.minecraft.entity.Entity;

/** Cancels attacks on friends. Middle click a player to add/remove a friend. */
public class FriendProtect extends Module {
    public FriendProtect() { super("FriendProtect"); }

    @Override
    public boolean cancelAttack(Entity target) {
        return Friends.isFriend(target);
    }
}
