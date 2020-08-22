package net.malfact.bgmanager.api;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;

public interface NBTContainer {

    public CompoundTag writeNBT(CompoundTag tag);

    public CompoundTag readNBT(CompoundTag tag);
}
