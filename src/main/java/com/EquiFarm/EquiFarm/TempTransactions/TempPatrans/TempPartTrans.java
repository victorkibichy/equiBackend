package com.EquiFarm.EquiFarm.TempTransactions.TempPatrans;

import com.EquiFarm.EquiFarm.TempTransactions.TransStatus;
import com.EquiFarm.EquiFarm.Transactions.PartTrans.PartTransTypes;
import com.EquiFarm.EquiFarm.Transactions.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TempPartTrans {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;
    private String drAcc;
    private String crAcc;
    private Double amount;
//    private TransStatus status;
    private PartTransTypes partTransTypes;
}
