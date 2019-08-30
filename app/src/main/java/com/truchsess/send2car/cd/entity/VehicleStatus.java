package com.truchsess.send2car.cd.entity;

/**********************************************************************************************
 Copyright (C) 2018 Norbert Truchsess norbert.truchsess@t-online.de

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **********************************************************************************************/
public class VehicleStatus {

    private String vin;

    private int mileage;

    private String updateReason;

    private String updateTime;

    private String doorDriverFront;

    private String doorDriverRear;

    private String doorPassengerFront;

    private String doorPassengerRear;

    private String windowDriverFront;

    private String windowDriverRear;

    private String windowPassengerFront;

    private String windowPassengerRear;

    private String trunk;

    private String rearWindow;

    private String convertibleRoofState;

    private String hood;

    private String doorLockState;

    private String parkingLight;

    private String positionLight;

    private float remainingFuel;

    private int remainingRangeElectric;

    private int remainingRangeElectricMls;

    private int remainingRangeFuel;

    private int remainingRangeFuelMls;

    private int maxRangeElectric;

    private int maxRangeElectricMls;

    private int fuelPercent;

    private float maxFuel;

    private String connectionStatus;

    private String chargingStatus;

    private int chargingLevelHv;

    private String lastChargingEndReason;

    private String lastChargingEndResult;

    private Position position;

    private int chargingTimeRemaining;

    private String internalDataTimeUTC;

    public String getVin() {
        return vin;
    }

    public int getMileage() {
        return mileage;
    }

    public String getUpdateReason() {
        return updateReason;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public String getDoorDriverFront() {
        return doorDriverFront;
    }

    public String getDoorDriverRear() {
        return doorDriverRear;
    }

    public String getDoorPassengerFront() {
        return doorPassengerFront;
    }

    public String getDoorPassengerRear() {
        return doorPassengerRear;
    }

    public String getWindowDriverFront() {
        return windowDriverFront;
    }

    public String getWindowDriverRear() {
        return windowDriverRear;
    }

    public String getWindowPassengerFront() {
        return windowPassengerFront;
    }

    public String getWindowPassengerRear() {
        return windowPassengerRear;
    }

    public String getTrunk() {
        return trunk;
    }

    public String getRearWindow() {
        return rearWindow;
    }

    public String getConvertibleRoofState() {
        return convertibleRoofState;
    }

    public String getHood() {
        return hood;
    }

    public String getDoorLockState() {
        return doorLockState;
    }

    public String getParkingLight() {
        return parkingLight;
    }

    public String getPositionLight() {
        return positionLight;
    }

    public float getRemainingFuel() {
        return remainingFuel;
    }

    public int getRemainingRangeElectric() {
        return remainingRangeElectric;
    }

    public int getRemainingRangeElectricMls() {
        return remainingRangeElectricMls;
    }

    public int getRemainingRangeFuel() {
        return remainingRangeFuel;
    }

    public int getRemainingRangeFuelMls() {
        return remainingRangeFuelMls;
    }

    public int getMaxRangeElectric() {
        return maxRangeElectric;
    }

    public int getMaxRangeElectricMls() {
        return maxRangeElectricMls;
    }

    public int getFuelPercent() {
        return fuelPercent;
    }

    public float getMaxFuel() {
        return maxFuel;
    }

    public String getConnectionStatus() {
        return connectionStatus;
    }

    public String getChargingStatus() {
        return chargingStatus;
    }

    public int getChargingLevelHv() {
        return chargingLevelHv;
    }

    public String getLastChargingEndReason() {
        return lastChargingEndReason;
    }

    public String getLastChargingEndResult() {
        return lastChargingEndResult;
    }

    public Position getPosition() {
        return position;
    }

    public int getChargingTimeRemaining() {
        return chargingTimeRemaining;
    }

    public String getInternalDataTimeUTC() {
        return internalDataTimeUTC;
    }
}
