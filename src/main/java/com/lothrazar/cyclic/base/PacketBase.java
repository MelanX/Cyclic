package com.lothrazar.cyclic.base;

import java.util.function.Supplier;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketBase {

  public void done(Supplier<Context> ctx) {
    ctx.get().setPacketHandled(true);
  }
}
