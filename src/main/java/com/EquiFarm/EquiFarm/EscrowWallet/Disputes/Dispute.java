package com.EquiFarm.EquiFarm.EscrowWallet.Disputes;

import java.time.LocalDateTime;

import javax.validation.constraints.Size;

import com.EquiFarm.EquiFarm.DeliveryAddress.DeliveryAddress;
import com.EquiFarm.EquiFarm.EscrowWallet.EscrowWallet;
import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.utils.Gender;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "Disputes")
public class Dispute extends TrackingEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    private User initiator;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disputed_escrow_wallet_id")
    private EscrowWallet escrowWallet;

    @Column(name = "date_initiated", updatable = false)
    private LocalDateTime dateInitiated;

    @Column(name = "complaint", columnDefinition = "TEXT", nullable = true)
    @Size(max = 2500)
    private String complaint;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    @Builder.Default
    private DisputeStatus status=DisputeStatus.OPEN;

    @Column(name = "resolutionDecision", columnDefinition = "TEXT", nullable = true)
    @Size(max = 2500)
    private String resolutionDecision;

    @Column(name = "resolutionDate", updatable = false)
    private LocalDateTime resolutionDate;
    
}
