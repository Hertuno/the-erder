package ru.alastar.main.net.responses;

import com.alastar.game.enums.EntityType;
import com.alastar.game.enums.ModeType;


public class AddEntityResponse
{
    public int        id;
    public int x,y,z;
    public String     caption;
    public EntityType type;
    public ModeType mode;

}
