package com.EquiFarm.EquiFarm.Transactions;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.EquiFarm.EquiFarm.Transactions.PartTrans.PartTransTypes;
import com.EquiFarm.EquiFarm.Transactions.PartTrans.DTO.PartTransRequest;
import com.EquiFarm.EquiFarm.Wallet.Wallet;
import com.EquiFarm.EquiFarm.Wallet.WalletRepository;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.EquiFarm.EquiFarm.Checkout.Checkout;
import com.EquiFarm.EquiFarm.Checkout.CheckoutRepository;
import com.EquiFarm.EquiFarm.EscrowWallet.EscrowWallet;
import com.EquiFarm.EquiFarm.EscrowWallet.EscrowWalletRepository;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProducts;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProductsRepository;
import com.EquiFarm.EquiFarm.Order.Order;
import com.EquiFarm.EquiFarm.Order.OrderRepository;
import com.EquiFarm.EquiFarm.Order.OrderStatus;
import com.EquiFarm.EquiFarm.OrderItem.OrderItem;
import com.EquiFarm.EquiFarm.OrderItem.OrderItemRepository;
import com.EquiFarm.EquiFarm.Transactions.DTO.TransactionRequest;
import com.EquiFarm.EquiFarm.Transactions.DTO.TransactionResponse;
import com.EquiFarm.EquiFarm.Transactions.PartTrans.PartTrans;
import com.EquiFarm.EquiFarm.Transactions.PartTrans.PartTransRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransactionService {
    private final TransactionsRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final EscrowWalletRepository escrowWalletRepository;
    private final CheckoutRepository checkoutRepository;
    private final PartTransRepository partTransRepository;
    private final ModelMapper modelMapper;
    private final FarmProductsRepository farmProductsRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    public Transactions createTransaction(TransactionRequest transactionRequest) throws IOException {
        try {
            if (transactionRequest.getCheckoutId() == null) {
                throw new IOException("Checkout is required.");
            }

            Optional<Checkout> checkoutOptional = checkoutRepository.findByDeletedFlagAndId(Constants.NO,
                    transactionRequest.getCheckoutId());
            if (!checkoutOptional.isPresent()) {
                throw new IOException("Order checkout not found.");
            }

            Checkout checkout = checkoutOptional.get();

            if (transactionRepository.findByDeletedFlagAndCheckout(Constants.NO, checkout).isPresent()) {
                throw new IOException("Transaction already exists.");
            }

            List<PartTransRequest> partTransRequestList = transactionRequest.getPartrans();
            List<PartTrans> partTransList = new ArrayList<>();

            for (PartTransRequest partTransRequest : partTransRequestList) {
                validatePartTransRequest(partTransRequest);

                partTransList.add(createPartTrans(partTransRequest));
            }

            if (!checkTransactionBalance(partTransList)) {
                throw new IOException("Transaction not balancing.");
            }

            List<PartTrans> savedPartTransList = partTransRepository.saveAll(partTransList);

            if (transactionRequest.getTransactionType().equals(TransactionType.FARM_PRODUCT_TRANSACTION)) {
                return createFarmProductTransaction(transactionRequest, savedPartTransList, checkout);
            } else {
                throw new IOException("Invalid type of transaction.");
            }
        } catch (IOException e) {
            log.error("An error occurred while creating transaction: " + e.getMessage());
            throw new IOException("An error occurred while creating transaction.");
        }
    }

    private void validatePartTransRequest(PartTransRequest partTransRequest) throws IOException {

        if (partTransRequest.getCreditWalletId() == null) {
            log.info("Receiving wallet is required.");
            throw new IOException("Receiving Wallet is required.");
        }
        Optional<Wallet> creditWalletOptional = walletRepository.findByDeletedFlagAndId(Constants.NO,
                partTransRequest.getCreditWalletId());

        if (!creditWalletOptional.isPresent()) {
            log.info("Receiving wallet not found.");
            throw new IOException("Receiving wallet find not found.");
        }

        if (partTransRequest.getDebitWalletId() == null) {
            throw new IOException("Buyer Wallet required.");
        }

        Optional<Wallet> debitWalletOptional = walletRepository.findByDeletedFlagAndId(Constants.NO,
                partTransRequest.getDebitWalletId());

        if (!debitWalletOptional.isPresent()) {
            log.info("Buyer wallet not found.");
            throw new IOException("Buyer Wallet not found.");
        }

    }

    public PartTrans createPartTrans(PartTransRequest partTransRequest) throws IOException {
        try {
            Optional<Wallet> debitWalletOptional = walletRepository.findByDeletedFlagAndId(Constants.NO,
                    partTransRequest.getDebitWalletId());
            if (!debitWalletOptional.isPresent()) {
                throw new IOException("Couldn't find the debit wallet.");
            }
            Optional<Wallet> creditWalletOptional = walletRepository.findByDeletedFlagAndId(Constants.NO,
                    partTransRequest.getCreditWalletId());

            if (!creditWalletOptional.isPresent()) {
                throw new IOException("Couldn't find the debit wallet.");
            }

            Wallet debitWallet = debitWalletOptional.get();
            Wallet creditWallet = creditWalletOptional.get();

            PartTrans partTrans = new PartTrans();
            partTrans.setCreditWallet(creditWallet);
            partTrans.setDebitWallet(debitWallet);
            partTrans.setCurrency(partTransRequest.getCurrency());
            partTrans.setTransactionAmount(partTransRequest.getTransactionAmount());
            partTrans.setPartTranType(partTransRequest.getPartTranType());
            partTrans.setStatus(TransactionStatus.IN_PROGRESS);
            partTrans.setTransactionDate(LocalDateTime.now());

            if (partTransRequest.getEscrowWalletId() != null) {
                Optional<EscrowWallet> escrowWalletOptional = escrowWalletRepository.findByDeletedFlagAndId(
                        Constants.NO,
                        partTransRequest.getEscrowWalletId());
                if (escrowWalletOptional.isPresent()) {
                    partTrans.setEscrowWallet(escrowWalletOptional.get());
                }
            }

            return partTrans;
        } catch (Exception e) {
            log.error("Error creating PartTrans: " + e.getMessage());
            throw new IOException("Error creating PartTrans.");
        }
    }

    private Transactions createFarmProductTransaction(TransactionRequest transactionRequest,
            List<PartTrans> partTransList, Checkout checkout) throws IOException {
        try {
            Optional<EscrowWallet> escrowWalletOptional = escrowWalletRepository
                    .findByDeletedFlagAndCheckout(Constants.NO, checkout);
            if (!escrowWalletOptional.isPresent()) {
                throw new IOException("Checkout Escrow Wallet not found.");
            }

            EscrowWallet escrowWallet = escrowWalletOptional.get();

            Long debitWalletId = transactionRequest.getDebitWalletId();
            List<Long> creditWalletIds = transactionRequest.getCreditWalletId();

            if (debitWalletId == null) {
                throw new IOException("Buyer wallet is required.");
            }
            Optional<Wallet> debitWalletOptional = walletRepository.findByDeletedFlagAndId(Constants.NO, debitWalletId);

            if (!debitWalletOptional.isPresent()) {
                throw new IOException("Buyer wallet not found.");
            }
            Wallet debitWallet = debitWalletOptional.get();

            if (creditWalletIds == null || creditWalletIds.isEmpty()) {
                throw new IOException("Seller wallet is required.");
            }

            List<Wallet> creditWallets = walletRepository.findAllById(creditWalletIds);
            if (creditWallets == null || creditWallets.isEmpty()) {
                throw new IOException("Seller wallet not found.");
            }

            Transactions transactionObj = Transactions.builder()
                    .escrowWallet(escrowWallet)
                    .transactionType(transactionRequest.getTransactionType())
                    .transactionDate(LocalDateTime.now())
                    .transactionAmount(transactionRequest.getTransactionAmount())
                    .senderWallet(debitWallet)
                    .receiverWallet(creditWallets)
                    .checkout(checkout)
                    .currency(transactionRequest.getCurrency())
                    .status(TransactionStatus.IN_PROGRESS)
                    .partTrans(partTransList)
                    .build();

            Transactions createdTransaction = transactionRepository.save(transactionObj);
            updateWallets(createdTransaction);
            updateInventory(createdTransaction);
            return createdTransaction;
        } catch (Exception e) {
            log.error("Error creating farm product transaction: " + e.getMessage());
            throw new IOException("Error creating farm product transaction.");
        }
    }

    public boolean checkTransactionBalance(List<PartTrans> partTrans) throws IOException {

        AtomicReference<BigDecimal> totalCreditAmount = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> totalDebitAmount = new AtomicReference<>(BigDecimal.ZERO);

        partTrans.stream().forEach(partTran -> {
            BigDecimal transactionAmount = BigDecimal.valueOf(partTran.getTransactionAmount());
            if (partTran.getPartTranType().equals(PartTransTypes.CREDIT)) {
                totalCreditAmount.updateAndGet(v -> v.add(transactionAmount));
            } else if (partTran.getPartTranType().equals(PartTransTypes.DEBIT)) {
                totalDebitAmount.updateAndGet(v -> v.add(transactionAmount));
            }
        });

        if (!totalCreditAmount.get().equals(totalDebitAmount.get())) {
            log.error("Transaction not balanced. Total Debit amount: {} Total Credit amount: {}",
                    totalDebitAmount.get(), totalCreditAmount.get());
            return false;
        }

        log.info("Transaction balance.");
        return true;

    }

    // update wallet
    public void updateWallets(Transactions transaction) throws IOException {
        try {
            validateTransaction(transaction);

            Wallet debitWallet = transaction.getSenderWallet();
            Checkout checkout = transaction.getCheckout();

            validateDebitWallet(debitWallet);
            validateCheckout(checkout, transaction.getTransactionAmount());

            double transactionAmount = transaction.getTransactionAmount();
            double remainingBalance = debitWallet.getBalance() - transactionAmount;

            if (remainingBalance >= 0) {
                List<PartTrans> partTransactions = transaction.getPartTrans();
                System.out.println("Transaction PartTrans Wallet Update::::" + transaction.getPartTrans().size());

                if (!checkTransactionBalance(partTransactions)) {
                    throw new IOException("Transaction is not balanced....");
                }

                for (PartTrans partTransaction : partTransactions) {
                    if (PartTransTypes.CREDIT.equals(partTransaction.getPartTranType()) &&
                            debitWallet.equals(partTransaction.getDebitWallet())) {

                        EscrowWallet escrowWallet = partTransaction.getEscrowWallet();
                        Wallet creditWallet = partTransaction.getCreditWallet();

                        if (escrowWallet != null) {
                            updateWalletBalance(debitWallet, -transactionAmount);
                            updateEscrowWalletBalance(escrowWallet, transactionAmount);
                            escrowWalletRepository.save(escrowWallet);
                            partTransaction.setStatus(TransactionStatus.ON_HOLD);
                        } else {
                            updateWalletBalance(debitWallet, -transactionAmount);
                            updateWalletBalance(creditWallet, transactionAmount);
                            walletRepository.save(creditWallet);
                            partTransaction.setStatus(TransactionStatus.COMPLETED);
                        }

                        walletRepository.save(debitWallet);
                    } else if (PartTransTypes.DEBIT.equals(partTransaction.getPartTranType()) &&
                            debitWallet.equals(partTransaction.getDebitWallet())) {

                        EscrowWallet escrowWallet = partTransaction.getEscrowWallet();

                        if (escrowWallet != null) {
                            partTransaction.setStatus(TransactionStatus.ON_HOLD);
                        } else {
                            partTransaction.setStatus(TransactionStatus.COMPLETED);
                        }
                    }

                    partTransRepository.save(partTransaction);
                }

            } else {
                throw new IOException("Insufficient funds in the debit wallet.");
            }
        } catch (IOException e) {
            log.error("Validation error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("An error occurred while updating the wallet: " + e.getMessage());
            throw new IOException("An error occurred while updating the wallet.", e);
        }
    }

    private void validateTransaction(Transactions transaction) throws IOException {
        if (transaction == null) {
            throw new IOException("Transaction cannot be null.");
        }
        if (!TransactionType.FARM_PRODUCT_TRANSACTION.equals(transaction.getTransactionType())) {
            throw new IOException("Invalid transaction type.");
        }
    }

    private void validateDebitWallet(Wallet debitWallet) throws IOException {
        if (debitWallet == null) {
            throw new IOException("Debit wallet not found.");
        }
    }

    private void validateCheckout(Checkout checkout, double transactionAmount) throws IOException {
        if (checkout == null) {
            throw new IOException("Checkout not found.");
        }
        if (checkout.getTotalAmount() != transactionAmount) {
            throw new IOException("Transaction amount does not match the total checkout amount.");
        }
    }

    private void updateWalletBalance(Wallet wallet, double amount) {
        double newBalance = wallet.getBalance() + amount;
        wallet.setBalance(newBalance);
    }

    private void updateEscrowWalletBalance(EscrowWallet wallet, double amount) {
        double newBalance = wallet.getBalance() + amount;
        wallet.setBalance(newBalance);
    }

    // Inventory management(Update the inventory)
    public void updateInventory(Transactions transaction) throws IOException {
        try {
            validateTransaction(transaction);

            Checkout checkout = transaction.getCheckout();
            validateCheckout(checkout, transaction.getTransactionAmount());

            Order order = checkout.getOrder();
            validateOrder(order);

            List<OrderItem> orderItems = order.getOrderItems();

            for (OrderItem orderItem : orderItems) {
                FarmProducts farmProduct = orderItem.getFarmProduct();
                double quantityToSubtract = Double.valueOf(orderItem.getQuantity());
                double newQuantity = farmProduct.getUnitsAvailable() - quantityToSubtract;

                if (farmProduct.getUnitsAvailable() < 1) {
                    farmProduct.setOnStock(farmProduct.getOnStock());
                }

                farmProduct.setUnitsAvailable(newQuantity);

                orderItem.setIsPaid(true);
                orderItem.setIsOrdered(true);
            }

            order.setOrderStatus(OrderStatus.IN_PROGRESS);
            order.setOrderDate(LocalDateTime.now());
            order.setIsOrdered(true);
            order.setIsPaid(true);

            orderItemRepository.saveAll(orderItems);
            farmProductsRepository
                    .saveAll(orderItems.stream().map(OrderItem::getFarmProduct).collect(Collectors.toList()));
            orderRepository.save(order);
        } catch (IOException e) {
            log.error("Error while updating Inventory: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error while updating Inventory: " + e.getMessage());
            throw new IOException("Error while updating Inventory.");
        }
    }

    private void validateOrder(Order order) throws IOException {
        if (order == null) {
            throw new IOException("Order not found.");
        }
    }


    

    // Payment Integration

    // Business Rules and Validation:
    // E-commerce transactions often involve various business rules and validations.
    // For example, checking if the product is in stock, validating the customer's
    // payment details, applying discounts or promotional codes, etc. You can
    // incorporate these rules into the transaction service to ensure that
    // transactions are processed correctly and meet the required criteria.

    public ApiResponse<?> getAllTransactions() {
        try {
            List<Transactions> transactionsList = transactionRepository.findByDeletedFlag(Constants.NO);

            List<TransactionResponse> transactionsResponses = transactionsList.stream()
                    .map(transaction -> modelMapper.map(transaction, TransactionResponse.class))
                    .collect(Collectors.toList());

            return new ApiResponse<>("Transactions fetched successfully.", transactionsResponses,
                    HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("An error occurred while fetching Transactions.", e);

            return new ApiResponse<>("An error occurred while fetching Transactions.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

}
