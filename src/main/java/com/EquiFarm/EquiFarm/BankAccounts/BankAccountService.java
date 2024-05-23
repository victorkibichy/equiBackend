package com.EquiFarm.EquiFarm.BankAccounts;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.EquiFarm.EquiFarm.BankAccounts.DTO.BankAccountRequest;
import com.EquiFarm.EquiFarm.BankAccounts.DTO.BankAccountResponse;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.user.UserRepository;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;
import com.EquiFarm.EquiFarm.utils.FieldUpdateUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ApiResponse<?> addBankAccount(BankAccountRequest bankAccountRequest) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();

            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            ApiResponse<?> validationResponse = validateBankAccountRequest(bankAccountRequest);
            if (validationResponse != null) {
                return validationResponse;
            }

            Optional<BankAccount> bankAccountOptional = bankAccountRepository.findByDeletedFlagAndUserId(Constants.NO,
                    currentUser.getId());

            if (bankAccountOptional.isPresent()) {
                BankAccount updatedBankAccount = bankAccountOptional.get();
                updateBankAccount(bankAccountRequest, updatedBankAccount.getId());

                BankAccountResponse bankAccountResponse = modelMapper.map(updatedBankAccount,
                        BankAccountResponse.class);

                return new ApiResponse<>("Bank account was successfully Updated.", bankAccountResponse,
                        HttpStatus.CREATED.value());
            } else {
                BankAccount bankAccount = createBankAccount(currentUser, bankAccountRequest);
                BankAccount addedAccount = bankAccountRepository.save(bankAccount);
                BankAccountResponse bankAccountResponse = modelMapper.map(addedAccount, BankAccountResponse.class);

                return new ApiResponse<>("Bank account was successfully added.", bankAccountResponse,
                        HttpStatus.CREATED.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while adding a bank account.", e);
            return new ApiResponse<>("An error occurred while adding a bank account.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private ApiResponse<?> validateBankAccountRequest(BankAccountRequest bankAccountRequest) {
        if (bankAccountRequest.getAccountName() == null) {
            return new ApiResponse<>("Account name is required.", null, HttpStatus.BAD_REQUEST.value());
        }

        if (bankAccountRequest.getAccountNumber() == null) {
            return new ApiResponse<>("Account number is required.", null, HttpStatus.BAD_REQUEST.value());
        }

        if (bankAccountRequest.getBank() == null) {
            return new ApiResponse<>("Bank is required.", null, HttpStatus.BAD_REQUEST.value());
        }

        return null;
    }

    private BankAccount createBankAccount(User currentUser, BankAccountRequest bankAccountRequest) {
        return BankAccount.builder()
                .accountName(bankAccountRequest.getAccountName())
                .accountNumber(bankAccountRequest.getAccountNumber())
                .bank(bankAccountRequest.getBank())
                .user(currentUser)
                .build();
    }

    @Transactional
    public ApiResponse<?> staffBankAccountCreation(BankAccountRequest bankAccountRequest) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();

            System.out.println("Account creation:::" + bankAccountRequest.getUserId());
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            ApiResponse<?> validationResponse = validateBankAccountRequest(bankAccountRequest);
            if (validationResponse != null) {
                return validationResponse;
            }

            if (bankAccountRequest.getUserId() == null) {
                return new ApiResponse<>("User id is required.", null, HttpStatus.BAD_REQUEST.value());
            }

            Optional<User> userOptional = userRepository.findByDeletedFlagAndId(Constants.NO,
                    bankAccountRequest.getUserId());

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                Optional<BankAccount> bankAccountOptional = bankAccountRepository.findByDeletedFlagAndUserId(
                        Constants.NO,
                        user.getId());

                if (bankAccountOptional.isPresent()) {
                    BankAccount updatedBankAccount = bankAccountOptional.get();
                    updateBankAccount(bankAccountRequest, updatedBankAccount.getId());

                    BankAccountResponse bankAccountResponse = modelMapper.map(updatedBankAccount,
                            BankAccountResponse.class);

                    return new ApiResponse<>("Bank account was successfully Updated.", bankAccountResponse,
                            HttpStatus.CREATED.value());
                } else {
                    BankAccount bankAccount = createBankAccount(currentUser, bankAccountRequest);
                    BankAccount addedAccount = bankAccountRepository.save(bankAccount);
                    BankAccountResponse bankAccountResponse = modelMapper.map(addedAccount, BankAccountResponse.class);

                    return new ApiResponse<>("Bank account was successfully added.", bankAccountResponse,
                            HttpStatus.CREATED.value());
                }

            } else {
                return new ApiResponse<>("User not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while adding a bank account", e);
            return new ApiResponse<>("An error occurred while adding a bank account", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getBankAccountByUserId(Long id) {
        try {
            Optional<User> userOptional = userRepository.findByDeletedFlagAndId(Constants.NO, id);

            if (userOptional.isPresent()) {
                return fetchUserBankAccount(userOptional.get());
            } else {
                return new ApiResponse<>("User not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching the user's bank accounts.", e);
            return new ApiResponse<>("An error occurred while fetching the user's bank accounts.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getBankAccountByUser() {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<User> userOptional = userRepository.findByDeletedFlagAndId(Constants.NO, currentUser.getId());

            if (userOptional.isPresent()) {
                return fetchUserBankAccount(userOptional.get());
            } else {
                return new ApiResponse<>("User not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching the user's bank account.", e);
            return new ApiResponse<>("An error occurred while fetching the user's bank account.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    private ApiResponse<?> fetchUserBankAccount(User user) {
        Optional<BankAccount> bankAccountOptional = bankAccountRepository.findByDeletedFlagAndUserId(Constants.NO,
                user.getId());

        if (bankAccountOptional.isPresent()) {
            BankAccount bankAccount = bankAccountOptional.get();
            BankAccountResponse bankAccountResponseList = modelMapper.map(bankAccount, BankAccountResponse.class);
            return new ApiResponse<>("User's bank account fetched successfully.", bankAccountResponseList,
                    HttpStatus.OK.value());
        } else {
            return new ApiResponse<>("Bank account not found.", null, HttpStatus.NOT_FOUND.value());
        }

    }

    @Transactional
    public ApiResponse<?> updateBankAccount(BankAccountRequest bankAccountRequest, Long id) {
        try {
            if (isBankAccountExists(id)) {
                Optional<BankAccount> bankAccountOptional = bankAccountRepository.findByDeletedFlagAndId(Constants.NO,
                        id);
                BankAccount bankAccount = bankAccountOptional.get();

                if (bankAccountRequest.getAccountNumber() != null
                        && bankAccountRequest.getAccountNumber().length() > 0
                        && !Objects.equals(bankAccount.getAccountNumber(),
                                bankAccountRequest.getAccountNumber())) {
                    Optional<BankAccount> bankAccountOptionalAccountNumber = bankAccountRepository
                            .findByDeletedFlagAndUserIdAndAccountNumber(Constants.NO, bankAccount.getUser().getId(),
                                    bankAccountRequest.getAccountNumber());

                    if (bankAccountOptionalAccountNumber.isPresent()) {
                        return new ApiResponse<>("User already have this bank account.", null,
                                HttpStatus.BAD_REQUEST.value());
                    }
                    bankAccount.setAccountNumber(bankAccountRequest.getAccountNumber());
                }

                BankAccount updatedBankAccount = updateBankAccountFields(bankAccount, bankAccountRequest);
                BankAccountResponse bankAccountResponse = modelMapper.map(updatedBankAccount,
                        BankAccountResponse.class);

                return new ApiResponse<>("Bank Account was successfully updated.", bankAccountResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Bank Account not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while updating the bank account.", e);
            return new ApiResponse<>("An error occurred while updating the bank account.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private BankAccount updateBankAccountFields(BankAccount bankAccount, BankAccountRequest bankAccountRequest) {
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(
                bankAccountRequest.getAccountName(),
                bankAccount::setAccountName,
                bankAccount::getAccountName);

        FieldUpdateUtil.updateFieldIfNotNullAndChanged(
                bankAccountRequest.getBank(),
                bankAccount::setBank,
                bankAccount::getBank);

        return bankAccountRepository.save(bankAccount);
    }

    private boolean isBankAccountExists(Long id) {
        Optional<BankAccount> bankAccountOptional = bankAccountRepository.findByDeletedFlagAndId(Constants.NO, id);
        return bankAccountOptional.isPresent();
    }

    @Transactional
    public ApiResponse<?> getBankAccountById(Long id) {
        try {
            Optional<BankAccount> bankAccountOptional = bankAccountRepository.findByDeletedFlagAndId(Constants.NO, id);

            if (bankAccountOptional.isPresent()) {
                BankAccount bankAccount = bankAccountOptional.get();

                BankAccountResponse bankAccountResponse = modelMapper.map(bankAccount, BankAccountResponse.class);

                return new ApiResponse<>("Bank account fetched successfully.", bankAccountResponse,
                        HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("Bank account not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while fetching bank account.", e);
            return new ApiResponse<>("An error occurred while fetching bank account.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> bankAccountDelete(Long id) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<BankAccount> bankAccountOptional = bankAccountRepository.findByDeletedFlagAndId(Constants.NO, id);

            if (bankAccountOptional.isPresent()) {
                BankAccount bankAccount = bankAccountOptional.get();

                bankAccount.setDeletedAt(LocalDateTime.now());
                bankAccount.setDeletedBy(currentUser);
                bankAccount.setDeletedFlag(Constants.YES);

                bankAccountRepository.save(bankAccount);

                return new ApiResponse<>("Bank account was successfully deleted.", null, HttpStatus.NO_CONTENT.value());
            } else {
                return new ApiResponse<>("Bank Account not found.", null, HttpStatus.NOT_FOUND.value());

            }
        } catch (Exception e) {
            log.error("An error occurred while deleting bank account.", e);
            return new ApiResponse<>("An error occurred while deleting bank account.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

}
