package ru.alastar.main.net;

import com.alastar.game.enums.PacketID;
import com.esotericsoftware.kryonet.Connection;

import ru.alastar.main.Server;

public class PacketGenerator {

	public static void generatePacketAll(PacketID id, Object object) {
		Server.server.sendToAllTCP(object);
	}

	public static void generatePacketTo(PacketID reg, Connection c,
			Object object) {
		c.sendUDP(object);
	}
	/*
	 * public static ByteBuf packIt(PacketID i, Object... v) { ByteBuf m =
	 * Unpooled.buffer(); m.writeInt(i.ordinal()); for(Object o: v) { if(o
	 * instanceof Integer) { m.writeInt((Integer) o); } else if(o instanceof
	 * String) { m.writeBytes(((String) o).getBytes()); } else if(o instanceof
	 * Float) { m.writeFloat((Float) o); } else if(o instanceof Boolean) {
	 * m.writeBoolean((Boolean) o); } } return m; }
	 */
}
