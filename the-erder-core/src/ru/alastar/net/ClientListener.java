package ru.alastar.net;

import ru.alastar.main.net.requests.*;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Listener;

public class ClientListener extends Listener {
    public Kryo kryo;
	public ClientListener(EndPoint e) {
        kryo = new Kryo();

		// Subscribe(PacketID.Msg, new MessagePacketHandler());
		// Subscribe(PacketID.Auth, new AuthPacketHandler());
		// Subscribe(PacketID.Reg, new RegistrationPacketHandler());
		// Subscribe(PacketID.AddCharacter, new AddCharacterPacketHandler());
		// Subscribe(PacketID.CreatePlayer, new CreateEntityPacketHandler());
		// Subscribe(PacketID.LoadWorld, new LoadWorldPacketHandler());
		// Subscribe(PacketID.SetData, new SetDataPacketHandler());
		// Subscribe(PacketID.UpdatePlayer, new UpdatePlayerPacketHandler());
		// Subscribe(PacketID.RemoveEntity, new RemoveEntityPacketHandler());
        kryo.setRegistrationRequired(true);
        kryo.setAsmEnabled(true);
        kryo.register(AuthPacketRequest.class);
        kryo.register(CharacterChooseRequest.class);
        kryo.register(CreateCharacterRequest.class);
        kryo.register(InputRequest.class);
        kryo.register(MessagePacketRequest.class);
        kryo.register(RegistrationPacketRequest.class);

		System.out.println("Client Handler have been started!");
	}

	public void received(Connection connection, Object object) {
		// if (object instanceof SomeRequest) {
		// SomeRequest request = (SomeRequest)object;
		// System.out.println(request.text);
		// SomeResponse response = new SomeResponse();
		// response.text = "Thanks";
		// connection.sendTCP(response);
		// }
	}

	@Override
	public void connected(Connection connection) {

	}

	@Override
	public void disconnected(Connection connection) {
		connection.close();
	}

}
