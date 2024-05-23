package com.EquiFarm.EquiFarm.Checkout;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.EquiFarm.EquiFarm.BankAccounts.BankAccount;
import com.EquiFarm.EquiFarm.BankAccounts.BankAccountRepository;
import com.EquiFarm.EquiFarm.EscrowWallet.EscrowWallet;
import com.EquiFarm.EquiFarm.EscrowWallet.EscrowWalletRepository;
import com.EquiFarm.EquiFarm.EscrowWallet.EscrowWalletService;
import com.EquiFarm.EquiFarm.EscrowWallet.TypeOfTrade;

import com.EquiFarm.EquiFarm.Order.OrderStatus;
import com.EquiFarm.EquiFarm.OrderItem.OrderItem;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.EquiFarm.EquiFarm.Checkout.DTO.CheckoutRequest;
import com.EquiFarm.EquiFarm.Checkout.DTO.CheckoutResponse;
import com.EquiFarm.EquiFarm.DeliveryAddress.DeliveryAddress;
import com.EquiFarm.EquiFarm.DeliveryAddress.DeliveryAddressRepository;
import com.EquiFarm.EquiFarm.Order.Order;
import com.EquiFarm.EquiFarm.Order.OrderRepository;
import com.EquiFarm.EquiFarm.Transactions.TransactionService;
import com.EquiFarm.EquiFarm.Transactions.TransactionType;
import com.EquiFarm.EquiFarm.Transactions.DTO.TransactionRequest;
import com.EquiFarm.EquiFarm.Transactions.PartTrans.PartTransTypes;
import com.EquiFarm.EquiFarm.Transactions.PartTrans.DTO.PartTransRequest;
import com.EquiFarm.EquiFarm.Wallet.Currency;
import com.EquiFarm.EquiFarm.Wallet.Wallet;
import com.EquiFarm.EquiFarm.Wallet.WalletRepository;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.user.UserRepository;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CheckoutService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final CheckoutRepository checkoutRepository;
    private final WalletRepository walletRepository;
    private final OrderRepository orderRepository;
    private final EscrowWalletRepository escrowWalletRepository;
    private final EscrowWalletService escrowWalletService;
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final TransactionService transactionService;
    private final BankAccountRepository bankAccountRepository;

    public ApiResponse<?> getAllCheckout() {
        try {
            List<Checkout> checkOutList = checkoutRepository.findByDeletedFlag(Constants.NO);
            List<CheckoutResponse> checkoutResponses = checkOutList.stream()
                    .map(checkout -> modelMapper.map(checkout, CheckoutResponse.class)).collect(Collectors.toList());

            return new ApiResponse<>("Checkout fetched successfully.", checkoutResponses, HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("An error occurred while fetching checkout.", e);

            return new ApiResponse<>("An error occurred while fetching checkout.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<List<ApiResponse<?>>> createCheckout(CheckoutRequest checkoutRequest) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

//            List<ApiResponse<?>> responseList = new ArrayList<>();

                // Check if the order is empty
                if (checkoutRequest.getOrderId() == null) {
                    new ApiResponse<>("Order is required.", null, HttpStatus.BAD_REQUEST.value());
                }

                if (checkoutRequest.getDeliveryAddressId() == null) {
                    new ApiResponse<>("Delivery address is required.", null, HttpStatus.BAD_REQUEST.value());
                }

                DeliveryAddress deliveryAddress = getDeliveryAddress(currentUser, checkoutRequest.getDeliveryAddressId());
                if (deliveryAddress == null) {
                    new ApiResponse<>("Delivery address not found.", null, HttpStatus.BAD_REQUEST.value());
                }

                // Get the  all orders in the checkout
            for (Long orderId: checkoutRequest.getOrderId()) {
                Order order = getOrder(orderId);
                if (order == null) {
                    new ApiResponse<>("Order not found.", null, HttpStatus.NOT_FOUND.value());
                }

                if (order.getOrderItems().isEmpty()) {
                    new ApiResponse<>("Can't checkout an empty order.", null, HttpStatus.BAD_REQUEST.value());
                }

                Optional<Checkout> checkoutOptional = getCheckout(order);

                if (checkoutOptional.isPresent()) {
                    updateCheckout(checkoutOptional.get(), checkoutRequest, deliveryAddress);
//                    ApiResponse<?> updateResponse = updateCheckout(checkoutOptional.get(), checkoutRequest, deliveryAddress);
//                    return updateResponse);
                } else {
                    createNewCheckout(order, checkoutRequest, deliveryAddress);
                }
            }
            return new ApiResponse<>("Checkout Processing", null, HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("An error occurred while checking out.", e);
            return new ApiResponse<>("An error occurred while checking out.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

//    public ApiResponse<?> createCheckout(List<CheckoutRequest> checkoutRequestList) {
//        try {
//            User currentUser = SecurityUtils.getCurrentUser();
//            if (currentUser == null) {
//                return new ApiResponse<>("Access denied. User is not authenticated.", null, HttpStatus.UNAUTHORIZED.value());
//            }
//
//            for (CheckoutRequest checkoutRequest : checkoutRequestList) {
//                // Check if the order is empty
//                if (checkoutRequest.getOrderId() == null) {
//                    return new ApiResponse<>("Order is required.", null, HttpStatus.BAD_REQUEST.value());
//                }
//
//                // Get the order in the checkout
//                Order order = getOrder(checkoutRequest.getOrderId());
//
//                if (order == null) {
//                    return new ApiResponse<>("Order not found.", null, HttpStatus.NOT_FOUND.value());
//                }
//
//                if (order.getOrderItems().isEmpty()) {
//                    return new ApiResponse<>("Can't checkout an empty order.", null, HttpStatus.BAD_REQUEST.value());
//                }
//
//                Optional<Checkout> checkoutOptional = getCheckout(order);
//                if (checkoutOptional.isPresent()) {
//                    return new ApiResponse<>("Order Already exists", null, HttpStatus.BAD_REQUEST.value());
//                } else {
//                    // Create a new checkout for the order
//                    ApiResponse<?> createCheckoutResponse = createNewCheckout(order, checkoutRequest);
//                    if (createCheckoutResponse.getStatusCode() != HttpStatus.CREATED.value()) {
//                        // Handle any errors during checkout creation, if needed
//                        return createCheckoutResponse;
//                    }
//                }
//            }
//
//            // If all checkouts were successfully created, you can return a success response here.
//            return new ApiResponse<>("Checkouts created successfully", null, HttpStatus.CREATED.value());
//        } catch (Exception e) {
//            log.error("An error occurred while checking out.", e);
//            return new ApiResponse<>("An error occurred while checking out.", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
//        }
//    }

    private DeliveryAddress getDeliveryAddress(User currentUser, Long deliveryAddressId) {
        return deliveryAddressRepository
                .findByDeletedFlagAndUserIdAndId(Constants.NO, currentUser.getId(), deliveryAddressId)
                .orElse(null);
    }

    private Order getOrder(Long orderId) {
        return orderRepository.findByDeletedFlagAndId(Constants.NO, orderId).orElse(null);
    }

    private Optional<Checkout> getCheckout(Order order) {
        return checkoutRepository.findByDeletedFlagAndOrderId(Constants.NO, order.getId());
    }
//    private ApiResponse<?> updateCheckout(Checkout checkout, CheckoutRequest checkoutRequest) {
//        User buyer = checkout.getOrder().getUser();
//        User seller = checkout.getOrder().getOrderItems().get(0).getFarmProduct().getFarmer().getUser();
////        Wallet buyerWallet = getWalletByUser(buyer.getId());
////        Wallet sellerWallet = getWalletByUser(seller.getId());
//
////        if (buyerWallet == null) {
////            return new ApiResponse<>("Buyer wallet not found.", null, HttpStatus.NOT_FOUND.value());
////        }
////
////        if (buyerWallet.getBalance() < checkout.getOrder().getTotalAmount()) {
////            return new ApiResponse<>("Insufficient funds in the wallet.", null, HttpStatus.NOT_FOUND.value());
////        }
////
////        if (sellerWallet == null) {
////            return new ApiResponse<>("Receiving wallet not found.", null, HttpStatus.NOT_FOUND.value());
////        }
////
////        if (sellerWallet.getCurrency() != buyerWallet.getCurrency()) {
////            return new ApiResponse<>("Cannot transact due to different currencies between buyer and seller.", null,
////                    HttpStatus.NOT_FOUND.value());
////        }
//
////        Optional<EscrowWallet> escrowWalletOptional = escrowWalletRepository
////                .findByDeletedFlagAndCheckoutId(Constants.NO, checkout.getId());
//
////        if (escrowWalletOptional.isPresent()) {
////            // Update the existing checkout
////            checkout.setOrderNote(checkoutRequest.getOrderNote());
//////            checkout.setDeliveryAddress(deliveryAddress);
////            checkoutRepository.save(checkout);
////        } else {
//            // Create a new escrow wallet
//            checkout.setOrderNote(checkoutRequest.getOrderNote());
////            checkout.setDeliveryAddress(deliveryAddress);
//            checkoutRepository.save(checkout);
//            escrowWalletService.createEscrowWallet(checkout, TypeOfTrade.FARMPRODUCT, sellerWallet, buyerWallet);
////        }
//
//        CheckoutResponse checkoutResponse = modelMapper.map(checkout, CheckoutResponse.class);
//        return new ApiResponse<>("Checkout was successfully saved.", checkoutResponse, HttpStatus.OK.value());
//    }
//    @Transactional
//    private ApiResponse<?> createNewCheckout(Order order, CheckoutRequest checkoutRequest) {
//        User buyer = order.getUser();
//        User seller = order.getOrderItems().get(0).getFarmProduct().getFarmer().getUser();
//
//        Wallet buyerWallet = getWalletByUser(buyer.getId());
//        Wallet sellerWallet = getWalletByUser(seller.getId());
//
//
//        if (buyerWallet == null) {
//            return new ApiResponse<>("Buyer wallet not found.", null, HttpStatus.NOT_FOUND.value());
//        }
//
//        if (buyerWallet.getBalance() < order.getTotalAmount()) {
//            return new ApiResponse<>("Insufficient funds in the wallet, please top up your wallet to trade.", null,
//                    HttpStatus.NOT_FOUND.value());
//        }
//
//        if (sellerWallet == null) {
//            return new ApiResponse<>("Receiving wallet not found.", null, HttpStatus.NOT_FOUND.value());
//        }
//
//        if (sellerWallet.getCurrency() != buyerWallet.getCurrency()) {
//            return new ApiResponse<>("Cannot transact due to different currencies between buyer and seller.", null,
//                    HttpStatus.NOT_FOUND.value());
//        }
//
//        Checkout checkout = Checkout.builder()
////                .deliveryAddress(deliveryAddress)
//                .orderNote(checkoutRequest.getOrderNote())
//                .status(CheckoutStatus.PAYMENT_PENDING)
//                .order(order)
//                .build();
//        Checkout addedCheckout;
//
//        try {
//            addedCheckout = checkoutRepository.save(checkout);
//            EscrowWallet createdEscrowWallet = escrowWalletService.createEscrowWallet(addedCheckout,
//                    TypeOfTrade.FARMPRODUCT,
//                    sellerWallet, buyerWallet);
//            // Perform transaction handling
//            handleTransactions(addedCheckout, createdEscrowWallet, buyerWallet, sellerWallet);
//
//        } catch (Exception e) {
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            return new ApiResponse<>("Failed to handle transactions.", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
//        }
//
//        CheckoutResponse checkoutResponse = modelMapper.map(addedCheckout, CheckoutResponse.class);
//        return new ApiResponse<>("Order was successfully checkout.", checkoutResponse, HttpStatus.CREATED.value());
//    }
    private ApiResponse<?> createNewCheckout(Order order, CheckoutRequest checkoutRequest, DeliveryAddress deliveryAddress) {
        User buyer = order.getUser();
        for (OrderItem orderItem: order.getOrderItems()){
            User seller = orderItem.getFarmProduct().getFarmer().getUser();
            order.setOrderStatus(OrderStatus.IN_PROGRESS);
            order.setIsOrdered(true);
            orderRepository.save(order);
        }

//        Wallet buyerWallet = getWalletByUser(buyer.getId());
//        Wallet sellerWallet = getWalletByUser(seller.getId());

// TODO: Check buyers balance before proceeding to checkout
//        if (buyer/getBalance < order.getTotalAmount()) {
//            return new ApiResponse<>("Insufficient Funds.", null, HttpStatus.NOT_FOUND.value());
//        }

        Checkout checkout = Checkout.builder()
                .deliveryAddress(deliveryAddress)
                .orderNote(checkoutRequest.getOrderNote())
                .status(CheckoutStatus.PAYMENT_PENDING)
                .order(order)
                .typeOfTrade(TypeOfTrade.FARMPRODUCT)
                .build();

        Checkout addedCheckout = checkoutRepository.save(checkout);

//        Checkout addedCheckout;
//        try {
//            addedCheckout = checkoutRepository.save(checkout);
//            EscrowWallet createdEscrowWallet = escrowWalletService.createEscrowWallet(addedCheckout,
//                    TypeOfTrade.FARMPRODUCT,
//                    sellerWallet, buyerWallet);
//            // Perform transaction handling
//            handleTransactions(addedCheckout, createdEscrowWallet, buyerWallet, sellerWallet);
//
//        } catch (Exception e) {
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            return new ApiResponse<>("Failed to handle transactions.", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
//        }

        CheckoutResponse checkoutResponse = modelMapper.map(addedCheckout, CheckoutResponse.class);
        return new ApiResponse<>("Order was successfully checkout.", null, HttpStatus.CREATED.value());
    }

    private ApiResponse<?> updateCheckout(Checkout checkout, CheckoutRequest checkoutRequest,
                                          DeliveryAddress deliveryAddress) {
        User buyer = checkout.getOrder().getUser();
        User seller = checkout.getOrder().getOrderItems().get(0).getFarmProduct().getFarmer().getUser();


        checkout.getOrder().setOrderStatus(OrderStatus.IN_PROGRESS);
        checkout.getOrder().setIsOrdered(true);
        orderRepository.save(checkout.getOrder());
//        Wallet buyerWallet = getWalletByUser(buyer.getId());
//        Wallet sellerWallet = getWalletByUser(seller.getId());
        Optional<BankAccount> buyerBankAccountOptional = bankAccountRepository.findByDeletedFlagAndUserId(Constants.NO, buyer.getId());
        if (buyerBankAccountOptional.isEmpty()) {
            return new ApiResponse<>("Buyer Account not found.", null, HttpStatus.NOT_FOUND.value());
        }

//        if (buyerWallet.getBalance() < checkout.getOrder().getTotalAmount()) {
//            return new ApiResponse<>("Insufficient funds in the wallet.", null, HttpStatus.NOT_FOUND.value());
//        }
        Optional<BankAccount> accountOptional = bankAccountRepository.findByDeletedFlagAndUserId(Constants.NO, seller.getId());
        if (accountOptional.isEmpty()) {
            return new ApiResponse<>("Seller Bank Account not found", null, HttpStatus.NOT_FOUND.value());
        }
//
//        if (sellerWallet.getCurrency() != buyerWallet.getCurrency()) {
//            return new ApiResponse<>("Cannot transact due to different currencies between buyer and seller.", null,
//                    HttpStatus.NOT_FOUND.value());
//        }

        checkout.setDeliveryAddress(deliveryAddress);
        checkout.setOrderNote(checkoutRequest.getOrderNote());
//        Optional<EscrowWallet> escrowWalletOptional = escrowWalletRepository
//                .findByDeletedFlagAndCheckoutId(Constants.NO, checkout.getId());

//        if (escrowWalletOptional.isPresent()) {
//            // Update the existing checkout
//            checkout.setOrderNote(checkoutRequest.getOrderNote());
//            checkout.setDeliveryAddress(deliveryAddress);
//            checkoutRepository.save(checkout);
//        } else {
//            // Create a new escrow wallet
//            checkout.setOrderNote(checkoutRequest.getOrderNote());
//            checkout.setDeliveryAddress(deliveryAddress);
//            checkoutRepository.save(checkout);
//            escrowWalletService.createEscrowWallet(checkout, TypeOfTrade.FARMPRODUCT, sellerWallet, buyerWallet);
//        }

        CheckoutResponse checkoutResponse = modelMapper.map(checkout, CheckoutResponse.class);
        return new ApiResponse<>("Checkout was successfully updated.", null, HttpStatus.OK.value());
    }
    private void handleTransactions(Checkout checkout, EscrowWallet escrowWallet, Wallet buyerWallet,
            Wallet sellerWallet) throws IOException {
        try {
            List<Long> creditWalletIdsList = new ArrayList<>();
            creditWalletIdsList.add(sellerWallet.getId());

            LocalDateTime transactionDate = LocalDateTime.now();
            Double orderAmount = checkout.getOrderAmount();
            Currency currency = sellerWallet.getCurrency();

            // Create debit and credit buyer
            PartTransRequest orderDebitpartTransRequest = createPartTransRequest(buyerWallet.getId(),
                    sellerWallet.getId(),
                    escrowWallet.getId(), currency, transactionDate, orderAmount, PartTransTypes.DEBIT);
            PartTransRequest orderCreditpartTransRequest = createPartTransRequest(buyerWallet.getId(),
                    sellerWallet.getId(),
                    escrowWallet.getId(), currency, transactionDate, orderAmount, PartTransTypes.CREDIT);

             // TODO: Logistic credit and debit
       

            List<PartTransRequest> partTransRequestList = new ArrayList<>();
            partTransRequestList.add(orderCreditpartTransRequest);
            partTransRequestList.add(orderDebitpartTransRequest);

           

            TransactionRequest transactionRequest = TransactionRequest.builder()
                    .transactionAmount(checkout.getTotalAmount())
                    .escrowWalletId(escrowWallet.getId())
                    .currency(currency)
                    .creditWalletId(creditWalletIdsList)
                    .debitWalletId(buyerWallet.getId())
                    .currency(currency)
                    .partrans(partTransRequestList)
                    .checkoutId(checkout.getId())
                    .transactionType(TransactionType.FARM_PRODUCT_TRANSACTION)
                    .build();

            transactionService.createTransaction(transactionRequest);
        } catch (IOException e) {
            log.error("An error occurred while handling transactions: " + e.getMessage());
            throw new IOException("An error occurred while handling transactions.");
        }
    }
    private PartTransRequest createPartTransRequest(Long debitWalletId, Long creditWalletIds,
            Long escrowWalletId, Currency currency, LocalDateTime transactionDate, Double transactionAmount,
            PartTransTypes partTranType) {
        return PartTransRequest.builder()
                .debitWalletId(debitWalletId)
                .creditWalletId(creditWalletIds)
                .escrowWalletId(escrowWalletId)
                .currency(currency)
                .transactionDate(transactionDate)
                .transactionAmount(transactionAmount)
                .partTranType(partTranType)
                .build();
    }

    private Wallet getWalletByUser(Long userId) {
        return walletRepository.findByDeletedFlagAndUserId(Constants.NO, userId).orElse(null);
    }

    public ApiResponse<?> getCheckoutById(Long id) {
        try {
            Optional<Checkout> checkoutOptional = checkoutRepository.findByDeletedFlagAndId(Constants.NO, id);

            if (checkoutOptional.isPresent()) {
                Checkout checkout = checkoutOptional.get();

                CheckoutResponse checkoutResponse = modelMapper.map(checkout, CheckoutResponse.class);
                return new ApiResponse<>("Checkout fetched successfully.", checkoutResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Checkout not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while fetching checkout.", e);

            return new ApiResponse<>("An error occurred while fetching checkout.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

    }

    public ApiResponse<?> getCheckoutByOrderId(Long id) {
        try {
            Optional<Checkout> checkoutOptional = checkoutRepository.findByDeletedFlagAndOrderId(Constants.NO, id);
            if (checkoutOptional.isPresent()) {
                Checkout checkout = checkoutOptional.get();

                CheckoutResponse checkoutResponse = modelMapper.map(checkout, CheckoutResponse.class);
                return new ApiResponse<>("Checkout fetched successfully.", checkoutResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Checkout not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching checkout.", e);

            return new ApiResponse<>("An error occurred while fetching checkout.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

    }

    // public ApiResponse<?> checkoutOrderUpdate(CheckoutRequest checkoutRequest,
    // Long id) {
    // try {
    // Optional<Checkout> checkoutOptional =
    // checkoutRepository.findByDeletedFlagAndId(Constants.NO, id);
    // if (checkoutOptional.isPresent()) {
    // Checkout checkout = checkoutOptional.get();
    // FieldUpdateUtil.updateFieldIfNotNullAndChanged(checkoutRequest.,
    // user::setFirstName,
    // user::getFirstName);

    // } else {
    // return new ApiResponse<>("Checkout not found.", null,
    // HttpStatus.NOT_FOUND.value());
    // }
    // } catch (Exception e) {
    // log.error("An error occurred while updating checkout.", e);

    // return new ApiResponse<>("An error occurred while updating checkout.", null,
    // HttpStatus.INTERNAL_SERVER_ERROR.value());
    // }
    // }

    public ApiResponse<?> deleteCheckout(Long id) {
        try {
            Optional<Checkout> checkoutOptional = checkoutRepository.findByDeletedFlagAndOrderId(Constants.NO, id);

            if (checkoutOptional.isPresent()) {
                User currentUser = SecurityUtils.getCurrentUser();
                Checkout checkout = checkoutOptional.get();
                checkout.setDeletedAt(LocalDateTime.now());
                checkout.setDeletedBy(currentUser);
                checkout.setDeletedFlag(Constants.YES);

                checkoutRepository.save(checkout);

                return new ApiResponse<>("Checkout was successfully deleted.", null,
                        HttpStatus.NO_CONTENT.value());
            } else {
                return new ApiResponse<>("Checkout not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while deleting checkout.", e);

            return new ApiResponse<>("An error occurred while deleting checkout.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

}
