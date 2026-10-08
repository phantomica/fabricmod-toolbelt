package net.phantomica.toolbelt.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.phantomica.toolbelt.Toolbelt;

public record ToolbeltSelectionPayload(int mode, int toolSlot) implements CustomPacketPayload {
	public static final Type<ToolbeltSelectionPayload> TYPE = new Type<>(Toolbelt.id("toolbelt_selection"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ToolbeltSelectionPayload> CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT,
			ToolbeltSelectionPayload::mode,
			ByteBufCodecs.VAR_INT,
			ToolbeltSelectionPayload::toolSlot,
			ToolbeltSelectionPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
