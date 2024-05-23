package com.EquiFarm.EquiFarm.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {
    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),
    FARMER_READ("farmer:read"),
    FARMER_UPDATE("farmer:update"),
    FARMER_CREATE("farmer:create"),
    FARMER_DELETE("farmer:delete"),
    CUSTOMER_READ("customer:read"),
    CUSTOMER_UPDATE("customer:update"),
    CUSTOMER_CREATE("customer:create"),
    CUSTOMER_DELETE("customer:delete"),
    DRIVER_READ("driver:read"),
    DRIVER_UPDATE("driver:update"),
    DRIVER_CREATE("driver:create"),
    DRIVER_DELETE("driver:delete"),
    SERVICE_PROVIDER_READ("serviceProvider:read"),
    SERVICE_PROVIDER_UPDATE("serviceProvider:update"),
    SERVICE_PROVIDER_CREATE("serviceProvider:create"),
    SERVICE_PROVIDER_DELETE("serviceProvider:delete"),

    MANUFACTURER_READ("manufacturer:read"),
    MANUFACTURER_UPDATE("manufacturer:update"),
    MANUFACTURER_CREATE("manufacturer:create"),
    MANUFACTURER_DELETE("manufacturer:delete"),
    AGRIBUSINESS_OWNER_READ("agriBusinessOwner:read"),
    AGRIBUSINESS_OWNER_UPDATE("agriBusinessOwner:update"),
    AGRIBUSINESS_OWNER_CREATE("agriBusinessOwner:create"),
    AGRIBUSINESS_OWNER_DELETE("agriBusinessOwner:delete"),

    STAFF_READ("staff:read"),
    STAFF_UPDATE("staff:update"),
    STAFF_CREATE("staff:create"),
    STAFF_DELETE("staff:delete"),

    WAREHOUSE_READ("warehouse:read"),
    WAREHOUSE_UPDATE("warehouse:update"),
    WAREHOUSE_CREATE("warehouse:create"),
    WAREHOUSE_DELETE("warehouse:delete"),

    PROCESSOR_READ("processor:read"),
    PROCESSOR_UPDATE("processor:update"),
    PROCESSOR_CREATE("processor:create"),
    PROCESSOR_DELETE("processor:delete"),

    FARMTECH_OWNER_READ("farmTechOwner:read"),
    FARMTECH_OWNER_UPDATE("farmTechOwner:update"),
    FARMTECH_OWNER_CREATE("farmTechOwner:create"),
    FARMTECH_OWNER_DELETE("farmTechOwner:delete"),

    ;

    @Getter
    private final String permission;
}
