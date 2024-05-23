package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessChekout;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessChekout.DTO.AgriBusinessCheckoutRequest;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessChekout.DTO.AgriBusinessCheckoutResponse;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessOrder.AgriBusinessOrder;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessOrder.AgriBusinessOrderRepository;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessOrder.AgriBusinessOrderStatus;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessOrderItem.AgriBusinessOrderItem;
import com.EquiFarm.EquiFarm.BankAccounts.BankAccount;
import com.EquiFarm.EquiFarm.BankAccounts.BankAccountRepository;
import com.EquiFarm.EquiFarm.DeliveryAddress.DeliveryAddress;
import com.EquiFarm.EquiFarm.DeliveryAddress.DeliveryAddressRepository;
import com.EquiFarm.EquiFarm.EscrowWallet.EscrowWallet;
import com.EquiFarm.EquiFarm.EscrowWallet.EscrowWalletRepository;
import com.EquiFarm.EquiFarm.EscrowWallet.EscrowWalletService;
import com.EquiFarm.EquiFarm.EscrowWallet.TypeOfTrade;
import com.EquiFarm.EquiFarm.Transactions.DTO.TransactionRequest;
import com.EquiFarm.EquiFarm.Transactions.PartTrans.DTO.PartTransRequest;
import com.EquiFarm.EquiFarm.Transactions.PartTrans.PartTransTypes;
import com.EquiFarm.EquiFarm.Transactions.TransactionService;
import com.EquiFarm.EquiFarm.Transactions.TransactionType;
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
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AgriBusinessCheckoutService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final AgriBusinessCheckoutRepository agriBusinessCheckoutRepository;
    private final WalletRepository walletRepository;
    private final AgriBusinessOrderRepository agriBusinessOrderRepository;
    private final EscrowWalletRepository escrowWalletRepository;
    private final EscrowWalletService escrowWalletService;
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final TransactionService transactionService;
    private final BankAccountRepository bankAccountRepository;


    public ApiResponse<?> getAllAgriBusinessCheckout() {
        try {
            List<AgriBusinessCheckout> agriBusinessCheckoutList = agriBusinessCheckoutRepository.findByDeletedFlag(Constants.NO);
            List<AgriBusinessCheckoutResponse> agriBusinessCheckoutResponses = agriBusinessCheckoutList.stream()
                    .map(agriBusinessCheckout -> modelMapper.map(agriBusinessCheckout, AgriBusinessCheckoutResponse.class)).collect(Collectors.toList());

            return new ApiResponse<>("Agribusiness checkout fetched successfully.", agriBusinessCheckoutResponses, HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("An error occurred while fetching agribusiness checkout.", e);

            return new ApiResponse<>("An error occurred while fetching agribusiness checkout.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    /** Add createAgribusinessCheckout method **/
    public ApiResponse<List<ApiResponse<?>>> createAgriBusinessCheckout(
            AgriBusinessCheckoutRequest agriBusinessCheckoutRequest) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

//            List<ApiResponse<?>> responseList = new ArrayList<>();

            // Check if the order is empty
            if (agriBusinessCheckoutRequest.getAgriBusinessOrderId() == null) {
                new ApiResponse<>("AgriBusiness Order is required.", null, HttpStatus.BAD_REQUEST.value());
            }

            if (agriBusinessCheckoutRequest.getDeliveryAddressId() == null) {
                new ApiResponse<>("Delivery address is required.", null, HttpStatus.BAD_REQUEST.value());
            }

            DeliveryAddress deliveryAddress = getDeliveryAddress(currentUser, agriBusinessCheckoutRequest.getDeliveryAddressId());
            if (deliveryAddress == null) {
                new ApiResponse<>("Delivery address not found.", null, HttpStatus.BAD_REQUEST.value());
            }

            // Get the  all orders in the checkout
            for (Long agriBusinessOrderId: agriBusinessCheckoutRequest.getAgriBusinessOrderId()) {
                AgriBusinessOrder agriBusinessOrder = getAgriBusinessOrder(agriBusinessOrderId);
                if (agriBusinessOrder == null) {
                    new ApiResponse<>("Order not found.", null, HttpStatus.NOT_FOUND.value());
                }

                if (agriBusinessOrder.getAgriBusinessOrderItems().isEmpty()) {
                    new ApiResponse<>("Can't checkout an empty order.", null, HttpStatus.BAD_REQUEST.value());
                }

                Optional<AgriBusinessCheckout> agriBusinessCheckoutOptional = getAgriBusinessCheckout(agriBusinessOrder);

                if (agriBusinessCheckoutOptional.isPresent()) {
                    updateAgriBusinessCheckout(agriBusinessCheckoutOptional.get(), agriBusinessCheckoutRequest, deliveryAddress);
//                    ApiResponse<?> updateResponse = updateCheckout(checkoutOptional.get(), checkoutRequest, deliveryAddress);
//                    return updateResponse);
                } else {
                    createNewAgriBusinessCheckout(agriBusinessOrder, agriBusinessCheckoutRequest, deliveryAddress);
                }
            }
            return new ApiResponse<>("Checkout Processing", null, HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("An error occurred while checking out.", e);
            return new ApiResponse<>("An error occurred while checking out.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private DeliveryAddress getDeliveryAddress(User currentUser, Long deliveryAddressId) {
        return deliveryAddressRepository
                .findByDeletedFlagAndUserIdAndId(Constants.NO, currentUser.getId(), deliveryAddressId)
                .orElse(null);
    }

    private AgriBusinessOrder getAgriBusinessOrder(Long agriBusinessOrderId) {
        return agriBusinessOrderRepository.findByDeletedFlagAndId(Constants.NO, agriBusinessOrderId).orElse(null);
    }

    private Optional<AgriBusinessCheckout> getAgriBusinessCheckout(AgriBusinessOrder agriBusinessOrder) {
        return agriBusinessCheckoutRepository.findByDeletedFlagAndAgriBusinessOrderId(Constants.NO, agriBusinessOrder.getId());
    }

    /** Add createNewAgribusinessCheckout method **/
    private ApiResponse<?> createNewAgriBusinessCheckout(AgriBusinessOrder agriBusinessOrder,
                                                         AgriBusinessCheckoutRequest agriBusinessCheckoutRequest,
                                                         DeliveryAddress deliveryAddress) {
        User buyer = agriBusinessOrder.getUser();
        for (AgriBusinessOrderItem agriBusinessOrderItem: agriBusinessOrder.getAgriBusinessOrderItems()){
            User seller = agriBusinessOrderItem.getAgriBusinessProduct().getAgriBusiness().getUser();
            agriBusinessOrder.setAgriBusinessOrderStatus(AgriBusinessOrderStatus.IN_PROGRESS);
            agriBusinessOrder.setIsOrdered(true);
            agriBusinessOrderRepository.save(agriBusinessOrder);
        }

//        Wallet buyerWallet = getWalletByUser(buyer.getId());
//        Wallet sellerWallet = getWalletByUser(seller.getId());

// TODO: Check buyers balance before proceeding to checkout
//        if (buyer/getBalance < order.getTotalAmount()) {
//            return new ApiResponse<>("Insufficient Funds.", null, HttpStatus.NOT_FOUND.value());
//        }

        AgriBusinessCheckout agriBusinessCheckout = AgriBusinessCheckout.builder()
                .deliveryAddress(deliveryAddress)
                .orderNote(agriBusinessCheckoutRequest.getOrderNote())
                .agriBusinessCheckoutStatus(AgriBusinessCheckoutStatus.PAYMENT_PENDING)
                .agriBusinessOrder(agriBusinessOrder)
                .typeOfTrade(TypeOfTrade.AGRIBUSINESSPRODUCT)
                .build();

        AgriBusinessCheckout addedAgriBusinessCheckout = agriBusinessCheckoutRepository.save(agriBusinessCheckout);

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

        AgriBusinessCheckoutResponse agriBusinessCheckoutResponse = modelMapper.map(addedAgriBusinessCheckout,
                AgriBusinessCheckoutResponse.class);
        return new ApiResponse<>("Order was successfully checkout.", null, HttpStatus.CREATED.value());
    }
//    @Transactional
//    public ApiResponse<?> createCheckout(AgriBusinessCheckoutRequest checkoutRequest) {
//        try {
//            User currentUser = SecurityUtils.getCurrentUser();
//            if (currentUser == null) {
//                return new ApiResponse<>("Access denied. User is not authenticated.", null,
//                        HttpStatus.UNAUTHORIZED.value());
//            }
//
//            // Check if the order is empty
//            if (checkoutRequest.getAgriBusinessOrderIds() == null) {
//                new ApiResponse<>("Order is required.", null, HttpStatus.BAD_REQUEST.value());
//            }
//
//            if (checkoutRequest.getDeliveryAddressId() == null) {
//                new ApiResponse<>("Delivery address is required.", null, HttpStatus.BAD_REQUEST.value());
//            }
//
//            DeliveryAddress deliveryAddress = getDeliveryAddress(currentUser, checkoutRequest.getDeliveryAddressId());
//            if (deliveryAddress == null) {
//                new ApiResponse<>("Delivery address not found.", null, HttpStatus.BAD_REQUEST.value());
//            }
//
//            // Get the  all orders in the checkout
//            for (Long orderId: checkoutRequest.getAgriBusinessOrderIds()) {
//                AgriBusinessOrder order = getOrder(orderId);
//                if (order == null) {
//                    new ApiResponse<>("Order not found.", null, HttpStatus.NOT_FOUND.value());
//                }
//
//                if (order.getOrderItems().isEmpty()) {
//                    new ApiResponse<>("Can't checkout an empty order.", null, HttpStatus.BAD_REQUEST.value());
//                }
//
//                Optional<Checkout> checkoutOptional = getCheckout(order);
//
//                if (checkoutOptional.isPresent()) {
//                    updateCheckout(checkoutOptional.get(), checkoutRequest, deliveryAddress);
////                    ApiResponse<?> updateResponse = updateCheckout(checkoutOptional.get(), checkoutRequest, deliveryAddress);
////                    return updateResponse);
//                } else {
//                    createNewCheckout(order, checkoutRequest, deliveryAddress);
//                }
//            }
//            return new ApiResponse<>("Checkout Processing", null, HttpStatus.OK.value());
//        } catch (Exception e) {
//            log.error("An error occurred while checking out.", e);
//            return new ApiResponse<>("An error occurred while checking out.", null,
//                    HttpStatus.INTERNAL_SERVER_ERROR.value());
//        }
//    }

    /** Add updateAgriBusinessCheckout method **/
    private ApiResponse<?> updateAgriBusinessCheckout(AgriBusinessCheckout agriBusinessCheckout,
                                                      AgriBusinessCheckoutRequest agriBusinessCheckoutRequest,
                                                      DeliveryAddress deliveryAddress) {
        User buyer = agriBusinessCheckout.getAgriBusinessOrder().getUser();
        User seller = agriBusinessCheckout.getAgriBusinessOrder().getAgriBusinessOrderItems().get(0)
                .getAgriBusinessProduct().getAgriBusiness().getUser();


        agriBusinessCheckout.getAgriBusinessOrder().setAgriBusinessOrderStatus(AgriBusinessOrderStatus.IN_PROGRESS);
        agriBusinessCheckout.getAgriBusinessOrder().setIsOrdered(true);
        agriBusinessOrderRepository.save(agriBusinessCheckout.getAgriBusinessOrder());
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

        agriBusinessCheckout.setDeliveryAddress(deliveryAddress);
        agriBusinessCheckout.setOrderNote(agriBusinessCheckoutRequest.getOrderNote());
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

        AgriBusinessCheckoutResponse agriBusinessCheckoutResponse = modelMapper.map(agriBusinessCheckout,
                AgriBusinessCheckoutResponse.class);
        return new ApiResponse<>("Checkout was successfully updated.", null, HttpStatus.OK.value());
    }

    private void handleTransactions(AgriBusinessCheckout agriBusinessCheckout, EscrowWallet escrowWallet, Wallet buyerWallet,
                                    Wallet sellerWallet) throws IOException {
        try {
            List<Long> creditWalletIdsList = new ArrayList<>();
            creditWalletIdsList.add(sellerWallet.getId());

            LocalDateTime transactionDate = LocalDateTime.now();
            Double orderAmount = agriBusinessCheckout.getAgriBusinessOrderAmount();
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
                    .transactionAmount(agriBusinessCheckout.getTotalAmount())
                    .escrowWalletId(escrowWallet.getId())
                    .currency(currency)
                    .creditWalletId(creditWalletIdsList)
                    .debitWalletId(buyerWallet.getId())
                    .currency(currency)
                    .partrans(partTransRequestList)
                    .checkoutId(agriBusinessCheckout.getId())
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

    public ApiResponse<?> getAgriBusinessCheckoutById(Long id) {
        try {
            Optional<AgriBusinessCheckout> agriBusinessCheckoutOptional = agriBusinessCheckoutRepository.findByDeletedFlagAndId(Constants.NO, id);

            if (agriBusinessCheckoutOptional.isPresent()) {
                AgriBusinessCheckout agriBusinessCheckout = agriBusinessCheckoutOptional.get();

                AgriBusinessCheckoutResponse agriBusinessCheckoutResponse = modelMapper.map(agriBusinessCheckout, AgriBusinessCheckoutResponse.class);
                return new ApiResponse<>("Agribusiness checkout fetched successfully.", agriBusinessCheckoutResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Agribusiness checkout not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while fetching agribusiness checkout.", e);

            return new ApiResponse<>("An error occurred while fetching agribusiness checkout.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

    }


    public ApiResponse<?> getAgriBusinessCheckoutByAgriBusinessOrderId(Long id) {
        try {
            Optional<AgriBusinessCheckout> agriBusinessCheckoutOptional = agriBusinessCheckoutRepository.findByDeletedFlagAndAgriBusinessOrderId(Constants.NO, id);
            if (agriBusinessCheckoutOptional.isPresent()) {
                AgriBusinessCheckout agriBusinessCheckout = agriBusinessCheckoutOptional.get();

                AgriBusinessCheckoutResponse agriBusinessCheckoutResponse = modelMapper.map(agriBusinessCheckout, AgriBusinessCheckoutResponse.class);
                return new ApiResponse<>("Agribusiness checkout fetched successfully.", agriBusinessCheckoutResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Agribusiness checkout not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching agribusiness checkout.", e);

            return new ApiResponse<>("An error occurred while fetching agribusiness checkout.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

    }

    public ApiResponse<?> deleteAgriBusinessCheckout(Long id) {
        try {
            Optional<AgriBusinessCheckout> agriBusinessCheckoutOptional = agriBusinessCheckoutRepository.findByDeletedFlagAndAgriBusinessOrderId(Constants.NO, id);

            if (agriBusinessCheckoutOptional.isPresent()) {
                User currentUser = SecurityUtils.getCurrentUser();
                AgriBusinessCheckout agriBusinessCheckout = agriBusinessCheckoutOptional.get();
                agriBusinessCheckout.setDeletedAt(LocalDateTime.now());
                agriBusinessCheckout.setDeletedBy(currentUser);
                agriBusinessCheckout.setDeletedFlag(Constants.YES);

                agriBusinessCheckoutRepository.save(agriBusinessCheckout);

                return new ApiResponse<>("Agribusiness checkout was successfully deleted.", null,
                        HttpStatus.NO_CONTENT.value());
            } else {
                return new ApiResponse<>("Agribusiness checkout not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while deleting agribusiness checkout.", e);

            return new ApiResponse<>("An error occurred while deleting agribusiness checkout.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
