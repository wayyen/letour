package com.twt.xtreme;

public class RentalRecord {
	String device_id;
	int pickup_slot_id;
	int dropoff_slot_id;
	
	public String getDeviceId() {
		return device_id;
	}
	public void setDeviceId(String device_id) {
		this.device_id = device_id;
	}

	public int getPickup_slot_id() {
		return pickup_slot_id;
	}
	public void setPickup_slot_id(int pickup_slot_id) {
		this.pickup_slot_id = pickup_slot_id;
	}
	public int getDropoff_slot_id() {
		return dropoff_slot_id;
	}
	public void setDropoff_slot_id(int dropoff_slot_id) {
		this.dropoff_slot_id = dropoff_slot_id;
	}

	
}
