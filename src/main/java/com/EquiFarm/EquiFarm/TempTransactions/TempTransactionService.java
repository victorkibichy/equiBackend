package com.EquiFarm.EquiFarm.TempTransactions;

import com.EquiFarm.EquiFarm.BankAccounts.BankAccount;
import com.EquiFarm.EquiFarm.BankAccounts.BankAccountRepository;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Inventory.InventoryService;
import com.EquiFarm.EquiFarm.Order.Order;
import com.EquiFarm.EquiFarm.Order.OrderRepository;
import com.EquiFarm.EquiFarm.Order.OrderStatus;
import com.EquiFarm.EquiFarm.OrderItem.OrderItem;
import com.EquiFarm.EquiFarm.OrderItem.OrderItemRepository;
import com.EquiFarm.EquiFarm.TempTransactions.DTO.FinRequest;
import com.EquiFarm.EquiFarm.TempTransactions.DTO.TransResponse;
import com.EquiFarm.EquiFarm.TempTransactions.TempPatrans.TempPartTrans;
import com.EquiFarm.EquiFarm.TempTransactions.TempPatrans.TempPatransRepo;
import com.EquiFarm.EquiFarm.Transactions.PartTrans.PartTransTypes;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.user.UserRepository;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class TempTransactionService {
    private final OrderItemRepository orderItemRepository;
    private final BankAccountRepository bankAccountRepository;
    private final TempPatransRepo tempPatransRepo;
    private final TempTransactionRepo tempTransactionRepo;
    private final OrderRepository orderRepository;
    private final FinService finService;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final InventoryService inventoryService;

    @Value("${fin_url}")
    private String FIN_URL;
@Transactional
    public ApiResponse<?> enterTransaction(Long orderId) {
        try {
//            User currentUser = SecurityUtils.getCurrentUser();
//            if (currentUser == null){
//                return new ApiResponse<>("User Unauthorized", null, HttpStatus.UNAUTHORIZED.value());
//            }
//            Optional<User> userOptional = userRepository.findByDeletedFlagAndId(Constants.NO, currentUser.getId());
//            if(userOptional.isEmpty()){
//                return new ApiResponse<>("User not found", null, HttpStatus.NOT_FOUND.value());
//            }
//            User user = userOptional.get();
            Optional<Order> orderOptional = orderRepository.findByDeletedFlagAndId(Constants.NO, orderId);
            if (orderOptional.isEmpty()) {
                return new ApiResponse<>("Order Item Not Found", null, HttpStatus.NOT_FOUND.value());
            }
            Order order = orderOptional.get();
            if(order.getOrderStatus().equals(OrderStatus.COMPLETED)){
                return new ApiResponse<>("Order is already completed", null, HttpStatus.BAD_REQUEST.value());
            }
//            if(!Objects.equals(user.getId(), order.getUser().getId())){
//                return new ApiResponse<>("Order does not belong to the user", null, HttpStatus.BAD_REQUEST.value());
//            }
            Long buyerUserId = order.getUser().getId();
            System.out.println(buyerUserId);
            Optional<BankAccount> bankAccountOptional = bankAccountRepository.findByDeletedFlagAndUserId(Constants.NO, buyerUserId);
            if (bankAccountOptional.isEmpty()) {
                return new ApiResponse<>("Buyer bank account not found", null, HttpStatus.NOT_FOUND.value());
            }
            BankAccount buyerBankAccount = bankAccountOptional.get();
            Optional<BankAccount> accountOptional = bankAccountRepository.findByDeletedFlagAndUserId(Constants.NO, order.getOrderItems().get(0).getFarmProduct().getFarmer().getUser().getId());
            if (accountOptional.isEmpty()) {
                return new ApiResponse<>("Seller Bank Account not found", null, HttpStatus.NOT_FOUND.value());
            }
            BankAccount sellerBankAccount = accountOptional.get();

            if(Double.parseDouble(sellerBankAccount.getAccBal()) < order.getTotalAmount()){
                return new ApiResponse<>("Insufficient Funds", null, HttpStatus.BAD_REQUEST.value());
            }

            TempPartTrans tempCreditPartTrans = TempPartTrans
                    .builder()
                    .amount(order.getTotalAmount())
                    .crAcc(sellerBankAccount.getAccountNumber())
                    .drAcc(buyerBankAccount.getAccountNumber())
                    .partTransTypes(PartTransTypes.CREDIT)
                    .build();
            tempPatransRepo.save(tempCreditPartTrans);

            TempPartTrans tempDebitPartTrans = TempPartTrans
                    .builder()
                    .amount(order.getTotalAmount())
                    .crAcc(buyerBankAccount.getAccountNumber())
                    .drAcc(sellerBankAccount.getAccountNumber())
                    .partTransTypes(PartTransTypes.DEBIT)
                    .build();
            tempPatransRepo.save(tempDebitPartTrans);
            List<TempPartTrans> tempPartTransList = new ArrayList<>();
            tempPartTransList.add(tempDebitPartTrans);
            tempPartTransList.add(tempCreditPartTrans);
            String transUUid = UUID.randomUUID().toString();

            User buyer = order.getUser();
            User seller = order.getOrderItems().get(0).getFarmProduct().getFarmer().getUser();


            TempTransaction tempTransaction = TempTransaction
                    .builder()
                    .orderId(orderId)
                    .buyerUserId(buyer.getId())
                    .transactionId(transUUid)
                    .sellerUserId(seller.getId())

//                    Add buyer name, role
                    .buyerName(buyer.getFirstName())
                    .buyerRole(buyer.getRole().name())

//                    Add buyer name, role
                    .sellerName(seller.getFirstName())
                    .sellerRole(seller.getRole().name())

                    .orderItemName(order.getOrderItems().get(0).getFarmProduct().getTypeOfProduct().getTypeOfProduct())
                    .totalAmount(order.getTotalAmount())
                    .tempPartTrans(tempPartTransList)
                    .transStatus(TransStatus.PROCESSING)
                    .productId(order.getOrderItems().get(0).getFarmProduct().getId())
                    .timestamp(LocalDateTime.now())
                    .build();
            TempTransaction transaction = tempTransactionRepo.save(tempTransaction);
            FinRequest finRequest = new FinRequest();
            finRequest.setAmount(transaction.getTotalAmount().toString());
            finRequest.setDrAcc(transaction.getTempPartTrans().get(0).getDrAcc());
            finRequest.setCrAcc(transaction.getTempPartTrans().get(0).getCrAcc());
            CustomResponse response = finService.fundTransfer(finRequest);
            System.out.println("whole response " + response);

            TempTransaction updatedTransaction = new TempTransaction();
            if(response.getStatus().equals("SUCCESS")){
                    transaction.setTransactionId(generateRandomDigits());
                    transaction.setTransStatus(TransStatus.COMPLETE);
                    order.setOrderStatus(OrderStatus.COMPLETED);
                    order.setIsPaid(true);
                    orderRepository.save(order);
                    updateInventory(order.getOrderItems());
                System.out.println(order.getTotalAmount());
                    updateBal(sellerBankAccount, buyerBankAccount, order.getTotalAmount());
                     updatedTransaction = tempTransactionRepo.save(transaction);
                    System.out.println("If success: " + updatedTransaction);
                } else if(response.getStatus().equals("FAILURE")){
                    transaction.setTransStatus(TransStatus.FAILED);
                    updatedTransaction = tempTransactionRepo.save(transaction);
                    System.out.println("If Failure: " + updatedTransaction);
                } else{
                    System.out.println("If some kind of error " + updatedTransaction);
                    return new ApiResponse<>("Some kind of error occurred", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
                }
            TransResponse transResponse = modelMapper.map(updatedTransaction, TransResponse.class);
            return new ApiResponse<>("Transaction Entered Successfully", transResponse, HttpStatus.OK.value());
        } catch (Exception e){
            log.info("Error occurred entering transaction", e);
            return new ApiResponse<>("Error occurred entering transaction", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private void updateBal(BankAccount sellerBankAccount, BankAccount buyerBankAccount, Double totalAmount) {
        System.out.println("seller bal before" + sellerBankAccount.getAccBal());
        System.out.println("Buyer bal before" + buyerBankAccount.getAccBal());

        sellerBankAccount.setAccBal(String.valueOf(Double.parseDouble(sellerBankAccount.getAccBal()) + totalAmount));
        System.out.println("seller bal after" + sellerBankAccount.getAccBal());
    buyerBankAccount.setAccBal(String.valueOf(Double.parseDouble(buyerBankAccount.getAccBal()) - totalAmount));
        System.out.println("Buyer bal after" + buyerBankAccount.getAccBal());

        bankAccountRepository.save(sellerBankAccount);
    bankAccountRepository.save(buyerBankAccount);
    }
    private void updateInventory(List<OrderItem> orderItems) {
    for(OrderItem orderItem : orderItems){
        inventoryService.soldUnits(orderItem.getFarmProduct(), orderItem.getQuantity());
    }
    }
    public ApiResponse<?> fetchBuyerUserIdOrSellerUserIdAllTransactions(Long userId){
        try {
//            User currentUser = SecurityUtils.getCurrentUser();
//            if(currentUser == null){
//                return new ApiResponse<>("User Unauthenticated", null, HttpStatus.UNAUTHORIZED.value());
//            }
            Optional<User> userOptional = userRepository.findByDeletedFlagAndId(Constants.NO, userId);
            if(userOptional.isEmpty()){
                return new ApiResponse<>("User not found", null, HttpStatus.NOT_FOUND.value());
            }

            User user = userOptional.get();
            List<TempTransaction> tempTransactionList = tempTransactionRepo.findByBuyerUserIdOrSellerUserId(user.getId());
            if(tempTransactionList.size() == 0){
                return new ApiResponse<>("User has no transactions yet", null, HttpStatus.FOUND.value());
            }
            List<TransResponse> transResponses = tempTransactionList
                    .stream()
//                    .filter(tempTransaction -> tempTransaction
//                            .getTempPartTrans()
//                            .stream()
//                            .anyMatch(tempPartTrans -> tempPartTrans.getPartTransTypes() == PartTransTypes.CREDIT))
                    .map(tempTransaction -> modelMapper.map(tempTransaction, TransResponse.class))

                    .toList();
            return new ApiResponse<>("Transactions fetched successfully", transResponses, HttpStatus.OK.value());
        } catch (Exception e){
            log.info("An error occurred while fetching transactions", e);
            return new ApiResponse<>("An error occurred while fetching transactions", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
//    public ApiResponse<?> fetchSellerAllTransactions(Long userId){
//        try {
////            User currentUser = SecurityUtils.getCurrentUser();
////            if(currentUser == null){
////                return new ApiResponse<>("User Unauthenticated", null, HttpStatus.UNAUTHORIZED.value());
////            }
//            Optional<User> userOptional = userRepository.findByDeletedFlagAndId(Constants.NO, userId);
//            if(userOptional.isEmpty()){
//                return new ApiResponse<>("User not found", null, HttpStatus.NOT_FOUND.value());
//            }
//            User user = userOptional.get();
//            List<TempTransaction> tempTransactionList = tempTransactionRepo.findByDeletedFlagAndBuyerUserId(Constants.NO, user.getId());
//            if(tempTransactionList.size() == 0){
//                return new ApiResponse<>("User has no transactions yet", null, HttpStatus.FOUND.value());
//            }
//            List<TransResponse> transResponses = tempTransactionList
//                    .stream()
////                    .filter(tempTransaction -> tempTransaction
////                            .getTempPartTrans()
////                            .stream()
////                            .anyMatch(tempPartTrans -> tempPartTrans.getPartTransTypes() == PartTransTypes.DEBIT))
//                    .map(tempTransaction -> modelMapper.map(tempTransaction, TransResponse.class))
//                    .toList();
//            return new ApiResponse<>("Success", transResponses, HttpStatus.OK.value());
//        } catch (Exception e){
//            log.info("Error occured", e);
//            return new ApiResponse<>("Error occurred", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
//        }
//    }
    public ApiResponse<?> fetchAllTransactions(){
        try {
            List<TempTransaction> tempTransactionList = tempTransactionRepo.findByDeletedFlag(Constants.NO);
            List<TransResponse> transResponses = tempTransactionList
                    .stream()
                    .map(trans -> modelMapper.map(trans, TransResponse.class))
                    .toList();
            return new ApiResponse<>("Success", transResponses, HttpStatus.OK.value());
        } catch (Exception e){
            log.info("Error", e);
            return new ApiResponse<>("Error", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public static String generateRandomDigits() {
        Random random = new Random();
        int length = 12;
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10); // Generate a random digit between 0 and 9
            sb.append(digit);
        }

        return sb.toString();
    }
}
