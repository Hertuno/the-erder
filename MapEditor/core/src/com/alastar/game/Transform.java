package com.alastar.game;

import java.io.Serializable;

import com.badlogic.gdx.math.Vector2;

public class Transform implements Serializable {

	private static final long serialVersionUID = -862176953291711450L;
	public Vector2 position = Vector2.Zero;

	public Transform(Vector2 pos) {
		this.position = pos;
	}
}
