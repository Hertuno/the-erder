package ru.alastar.net;

import java.util.ArrayList;
import java.util.Hashtable;

import ru.alastar.main.net.requests.*;
import ru.alastar.main.net.responses.AddEntityResponse;
import ru.alastar.main.net.responses.AddFlagResponse;
import ru.alastar.main.net.responses.AddNearLocationResponse;
import ru.alastar.main.net.responses.AddSkillResponse;
import ru.alastar.main.net.responses.AddStatResponse;
import ru.alastar.main.net.responses.ChatSendResponse;
import ru.alastar.main.net.responses.InventoryResponse;
import ru.alastar.main.net.responses.LocationInfoResponse;
import ru.alastar.main.net.responses.LoginResponse;
import ru.alastar.main.net.responses.MessageResponse;
import ru.alastar.main.net.responses.RegisterResponse;
import ru.alastar.main.net.responses.RemoveEntityResponse;
import ru.alastar.main.net.responses.RemoveFlagResponse;
import ru.alastar.main.net.responses.RemoveFromInventoryResponse;
import ru.alastar.main.net.responses.SetData;

import com.alastar.game.Entity;
import com.alastar.game.enums.EntityType;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Listener;

public class ClientListener extends Listener {
    public Kryo kryo;
	public ClientListener(EndPoint e) {
        kryo = new Kryo();

        kryo.setRegistrationRequired(true);
        kryo.setAsmEnabled(true);
        
        kryo.register(EntityType.class);
        kryo.register(Hashtable.class);
        kryo.register(ArrayList.class);
        kryo.register(Entity.class);
        kryo.register(String.class);
        kryo.register(Integer.class);
        kryo.register(String[].class);
        
        kryo.register(RemoveFlagResponse.class);
        kryo.register(LoginResponse.class);
        kryo.register(AddEntityResponse.class);
        kryo.register(LocationInfoResponse.class);
        kryo.register(AddNearLocationResponse.class);
        kryo.register(SetData.class);
        kryo.register(ChatSendResponse.class);
        kryo.register(RemoveEntityResponse.class);
        kryo.register(RegisterResponse.class);
        kryo.register(AddStatResponse.class);
        kryo.register(AddSkillResponse.class);
        kryo.register(InventoryResponse.class);
        kryo.register(MessageResponse.class);
        kryo.register(RemoveFromInventoryResponse.class);
        kryo.register(AddFlagResponse.class);
        kryo.register(CommandRequest.class);
        kryo.register(RemoveFlagResponse.class);
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
