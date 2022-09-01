package com.khauminhduy.productapifunctional.model;

import java.util.Objects;

public class ProductEvent {

	private Long eventId;
	private String eventType;

	public ProductEvent() {
	}

	public ProductEvent(Long eventId, String eventType) {
		this.eventId = eventId;
		this.eventType = eventType;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(eventId, eventType);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductEvent other = (ProductEvent) obj;
		return Objects.equals(eventId, other.eventId) && Objects.equals(eventType, other.eventType);
	}

	@Override
	public String toString() {
		return "ProductEvent [eventId=" + eventId + ", eventType=" + eventType + "]";
	}

}
