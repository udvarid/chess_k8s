package com.donat.donchess.model.objects;

import com.donat.donchess.domain.enums.SpecialMoveType;

public class ValidMove {

    private Coordinate coordinate;

    private SpecialMoveType specialMoveType;

    public ValidMove(Coordinate coordinate, SpecialMoveType specialMoveType) {
        this.coordinate = coordinate;
        this.specialMoveType = specialMoveType;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public SpecialMoveType getSpecialMoveType() {
        return specialMoveType;
    }

    public void setSpecialMoveType(SpecialMoveType specialMoveType) {
        this.specialMoveType = specialMoveType;
    }
}
