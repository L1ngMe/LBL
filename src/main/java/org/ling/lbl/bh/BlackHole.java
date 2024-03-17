package org.ling.lbl.bh;

import org.bukkit.Location;
import org.bukkit.Particle;

public class BlackHole implements IBlackHole {

    private String name;
    private double radius;
    private Particle particle;
    private Location location;



    private void build() {
            if (name == null)
    }


    public void setName(String name) {
        this.name = name;
    }


    public void setRaduis(double raduis) {
        this.radius = raduis;
    }


    public void setParticle(Particle particle) {
        this.particle = particle;
    }


    public void setLocation(Location location) {
        this.location = location;
    }


}
