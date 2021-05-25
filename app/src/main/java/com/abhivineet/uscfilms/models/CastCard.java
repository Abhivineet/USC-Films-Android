package com.abhivineet.uscfilms.models;

import org.jetbrains.annotations.NotNull;

public class CastCard {
    private final String name;
    private final String profilePath;

    public String getName() {
        return name;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public CastCard(String name, String profilePath) {
        this.name = name;
        this.profilePath = profilePath;
    }

    @NotNull
    public String toString(){
        return getName() + " " + getProfilePath();
    }
}
