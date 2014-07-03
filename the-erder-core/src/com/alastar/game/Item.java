package com.alastar.game;

import java.util.Hashtable;

import com.alastar.game.enums.EntityType;
import com.badlogic.gdx.math.Vector3;

@SuppressWarnings("serial")
public class Item extends Entity {

    public int amount;
    public Hashtable<String, Integer> attributes;
    
	public Item(int id, Vector3 pos, String caption, EntityType type, int amt, Hashtable<String, Integer> attr) {
		super(id, pos, caption, type);
		this.amount = amt;
		this.attributes = attr;
	}

}
