package ru.alastar.main.net.requests;

import com.alastar.game.enums.Race;

public class CreateCharacterRequest {

	public String nick = "";
	public Race r;
	/*
	 * int nickL = m.readInt(); byte[] nickB = new byte[nickL]; for(int i = 0; i
	 * < nickL; ++i) { nickB[i] = m.readByte(); }
	 * 
	 * nick = new String(nickB, "UTF-8");
	 * 
	 * 
	 * 
	 * int raceL = m.readInt(); byte[] raceB = new byte[raceL]; for(int i = 0; i
	 * < raceL; ++i) { raceB[i] = m.readByte(); }
	 * 
	 * r = Race.valueOf(new String(raceB, "UTF-8"));
	 * 
	 * Server.TryCreate(ctx, nick, r); } catch (UnsupportedEncodingException e)
	 * { e.printStackTrace(); } }
	 */
}
