package com.EquiFarm.EquiFarm.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.EquiFarm.EquiFarm.user.Permission.*;

@RequiredArgsConstructor
public enum Role {
        FARMER(Set.of(
                        FARMER_CREATE,
                        FARMER_DELETE,
                        FARMER_READ,
                        FARMER_UPDATE)),
        SERVICE_PROVIDER(
                Set.of(
                        SERVICE_PROVIDER_CREATE,
                        SERVICE_PROVIDER_DELETE,
                        SERVICE_PROVIDER_READ,
                        SERVICE_PROVIDER_UPDATE)),
        AGRIBUSINESS_OWNER(
                        Set.of(

                                        AGRIBUSINESS_OWNER_CREATE,
                                        AGRIBUSINESS_OWNER_DELETE,
                                        AGRIBUSINESS_OWNER_READ,
                                        AGRIBUSINESS_OWNER_UPDATE)),
        DRIVER(
                        Set.of(
                                        DRIVER_CREATE,
                                        DRIVER_DELETE,
                                        DRIVER_READ,
                                        DRIVER_UPDATE)),

        ADMIN(
                        Set.of(
                                        ADMIN_READ,
                                        ADMIN_UPDATE,
                                        ADMIN_DELETE,
                                        ADMIN_CREATE,
                                        FARMER_CREATE,
                                        FARMER_DELETE,
                                        FARMER_READ,
                                        FARMER_UPDATE)),
        CUSTOMER(
                        Set.of(

                                        CUSTOMER_CREATE,
                                        CUSTOMER_DELETE,
                                        CUSTOMER_READ,
                                        CUSTOMER_UPDATE)),
        STAFF(
                        Set.of(


                                        STAFF_CREATE,
                                        STAFF_DELETE,
                                        STAFF_READ,
                                        STAFF_UPDATE)),
        MANUFACTURER(
                Set.of(
                        MANUFACTURER_CREATE,
                        MANUFACTURER_DELETE,
                        MANUFACTURER_READ,
                        MANUFACTURER_UPDATE)),


        WAREHOUSE(
                Set.of(
                        WAREHOUSE_CREATE,
                        WAREHOUSE_DELETE,
                        WAREHOUSE_READ,
                        WAREHOUSE_UPDATE)),
        PROCESSOR(
                Set.of(
                        PROCESSOR_CREATE,
                        PROCESSOR_DELETE,
                        PROCESSOR_READ,
                        PROCESSOR_UPDATE)),

        FARMTECH_OWNER(
                Set.of(
                        FARMTECH_OWNER_CREATE,
                        FARMTECH_OWNER_DELETE,
                        FARMTECH_OWNER_READ,
                        FARMTECH_OWNER_UPDATE))
        ;

        @Getter
        private final Set<Permission> permissions;

        public List<SimpleGrantedAuthority> getAuthorities() {
                var authorities = getPermissions()
                                .stream()
                                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                                .collect(Collectors.toList());
                authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
                return authorities;
        }

}
