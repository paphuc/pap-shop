package com.pap_shop.enums;

/**
 * Enum representing the possible statuses of an order.
 * Used to track the lifecycle of an order from creation to delivery.
 */
public enum OrderStatus {
    /**
     * Order has been created but not yet processed.
     */
    PENDING,

    /**
     * Order is currently being processed.
     */
    PROCESSING,

    /**
     * Order has been shipped and is on its way to the customer.
     */
    SHIPPED,

    /**
     * Order has been delivered to the customer.
     */
    DELIVERED,

    /**
     * Order has been canceled and will not be processed or shipped.
     */
    CANCELED
}
