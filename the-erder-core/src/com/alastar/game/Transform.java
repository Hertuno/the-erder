package com.alastar.game;

import java.io.Serializable;

import com.badlogic.gdx.math.Vector3;

public class Transform implements Serializable {

	private static final long serialVersionUID = -862176953291711450L;
	public Vector3 position = Vector3.Zero;
	public Vector3 o_position = Vector3.Zero;

	public Transform(Vector3 pos) {
		this.o_position = pos;
		this.position = pos;
	}

	public void setPosition(Vector3 pos) {
		this.o_position = position;
		this.position = pos;
	}
}
