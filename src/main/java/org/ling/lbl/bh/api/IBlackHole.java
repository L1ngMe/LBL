package org.ling.lbl.bh.api;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import java.sql.SQLException;

public interface IBlackHole {

    void setName(String name) throws SQLException;

    void setRadius(double radius);

    void setParticle(Particle particle);
    void setQuality(int quality);

    void setX(double x);
    void setY(double y);
    void setZ(double z);
    void setWorld(World world);
}