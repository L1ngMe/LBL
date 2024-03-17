package org.ling.lbl.bh;

import org.bukkit.Location;
import org.bukkit.Particle;

public interface IBlackHole {

    void setName(String name);

    void setRaduis(double raduis);

    void setParticle(Particle particle);

    void setLocation(Location location);
}
