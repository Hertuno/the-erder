package ru.alastar.net;

import java.util.ArrayList;
import java.util.Hashtable;

import ru.alastar.main.net.requests.*;
import ru.alastar.main.net.responses.AddCharacterResponse;
import ru.alastar.main.net.responses.AddEntityResponse;
import ru.alastar.main.net.responses.AddFlagResponse;
import ru.alastar.main.net.responses.AddNearLocationResponse;
import ru.alastar.main.net.responses.AddSkillResponse;
import ru.alastar.main.net.responses.AddStatResponse;
import ru.alastar.main.net.responses.ChatSendResponse;
import ru.alastar.main.net.responses.InventoryResponse;
import ru.alastar.main.net.responses.LoadWorldResponse;
import ru.alastar.main.net.responses.LocationInfoResponse;
import ru.alastar.main.net.responses.LoginResponse;
import ru.alastar.main.net.responses.MessageResponse;
import ru.alastar.main.net.responses.RegisterResponse;
import ru.alastar.main.net.responses.RemoveEntityResponse;
import ru.alastar.main.net.responses.RemoveFromInventoryResponse;
import ru.alastar.main.net.responses.SetData;
import ru.alastar.main.net.responses.UpdatePlayerResponse;

import com.alastar.game.Entity;
import com.alastar.game.Item;
import com.alastar.game.ModeManager;
import com.alastar.game.enums.EntityType;
import com.alastar.game.enums.MenuState;
import com.alastar.game.enums.ModeType;
import com.alastar.game.enums.UpdateType;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Listener;

public class ClientListener extends Listener {
    public Kryo kryo;
	public ClientListener(EndPoint e) {
        kryo = e.getKryo();

        kryo.setRegistrationRequired(true);
        kryo.setAsmEnabled(true);
        
        kryo.register(EntityType.class);
        kryo.register(Hashtable.class);
        kryo.register(ArrayList.class);
        kryo.register(Entity.class);
        kryo.register(String.class);
        kryo.register(Integer.class);
        kryo.register(String[].class);
        kryo.register(ModeType.class);
        kryo.register(UpdateType.class);

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
        kryo.register(AuthPacketRequest.class);
        kryo.register(CharacterChooseRequest.class);
        kryo.register(CreateCharacterRequest.class);
        kryo.register(InputRequest.class);
        kryo.register(MessagePacketRequest.class);
        kryo.register(RegistrationPacketRequest.class);
        kryo.register(AddCharacterResponse.class);
        kryo.register(LoadWorldResponse.class);
        kryo.register(CharacterRemove.class);
        kryo.register(UpdatePlayerResponse.class);

		//System.out.println("Client Handler have been started!");
	}

	public void received(Connection connection, Object object) {
		 if (object instanceof LoginResponse) {
		 LoginResponse r = (LoginResponse)object;
		 if(r.succesful){
		     Client.game.SwitchScreen(MenuState.CharacterChoose);
		 }
		 else{
	         Client.game.SwitchScreen(MenuState.Login); 
		 }
		 }
		 else if (object instanceof SetData) {
		     SetData r = (SetData)object;
		     Client.id = r.id;
		 }
	     else if (object instanceof AddEntityResponse) {
	          AddEntityResponse r = (AddEntityResponse)object;
	          Entity e = new Entity(r.id, new Vector3(r.x, r.y, r.z), r.caption, r.type);
	          
	          if(e.id == Client.id)
	              Client.controlledEntity = e;
	          
	          ModeManager.handleEntity(e);
	          
	     }
	     else if (object instanceof AddStatResponse) {
	          AddStatResponse r = (AddStatResponse)object;
              Client.handleStat(r.name, r.sValue, r.mValue);
         }
	     else if (object instanceof AddSkillResponse) {
	          AddSkillResponse r = (AddSkillResponse)object;
              Client.handleSkill(r.name, r.sValue, r.mValue);
         }
	     else if (object instanceof InventoryResponse) {
	         InventoryResponse r = (InventoryResponse)object;
	         Client.handleInv(new Item(r.id, new Vector3(0,0,0), r.captiion, EntityType.Human, r.amount, r.attrs));
	     }
	     else if (object instanceof AddCharacterResponse) {
	         AddCharacterResponse r = (AddCharacterResponse)object;
	         Client.handleChar(r.name, r.type);
	     }
	     else if (object instanceof RemoveEntityResponse) {
	         RemoveEntityResponse r = (RemoveEntityResponse)object;
	         ModeManager.removeEntity(r.id); 
	     }
	     else if (object instanceof LoadWorldResponse) {
	         LoadWorldResponse r = (LoadWorldResponse)object;
	         Client.LoadWorld(r.name);
	     }
	     else if (object instanceof UpdatePlayerResponse) {
	          UpdatePlayerResponse r = (UpdatePlayerResponse)object;
	          switch(r.updType){
	              case Position:
	              ModeManager.currentMode.handleUpdate(r.id, new Vector3(r.x, r.y, r.z));
	              break;
                case All:
                    break;
                case Health:
                    break;
                case Name:
                    break;
                default:
                    break;
	          }
	     }
	}

	@Override
	public void connected(Connection connection) {

	}

	@Override
	public void disconnected(Connection connection) {
		connection.close();
	}

}
