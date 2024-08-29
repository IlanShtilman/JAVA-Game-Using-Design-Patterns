package game.entities;

import utilities.Point;

//Our interface we will use to move the players and get their location
public interface IMobileEntity {
    void move(double friction);
    Point getLocation();
}