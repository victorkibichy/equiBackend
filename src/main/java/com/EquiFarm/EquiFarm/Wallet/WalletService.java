package com.EquiFarm.EquiFarm.Wallet;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.tomcat.util.bcel.Const;
import org.apache.tomcat.util.codec.binary.Base64;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.EquiFarm.EquiFarm.Wallet.DTO.WalletResponse;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.Role;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;

import jakarta.transaction.Transactional;
import lombok.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {
    private final WalletRepository walletRepository;
    private final ModelMapper modelMapper;

    public Wallet createWallet(User user) {
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setWalletNumber(String.valueOf(generateWalletNumber(user)));
        wallet.setBalance(47000.00);
        wallet.setCurrency(Currency.KES);
       
        walletRepository.save(wallet);
         System.out.println("Wallet Created:::->" + wallet.getWalletNumber());
        // 102|2373700|1

        return wallet;
    }

    private Long generateWalletNumber(User user) {
        String encodedRole = encodeUserRole(user.getRole());
        String encodedUserId = encodeUserId(user.getId());

        String walletNumber = encodedRole + encodedUserId + user.getId();
        return Long.parseLong(walletNumber);
    }

    private String encodeUserRole(Role role) {
        String encodedRole;
        switch (role) {
            case ADMIN:
                encodedRole = "101";
                break;
            case CUSTOMER:
                encodedRole = "102";
                break;
            case FARMER:
                encodedRole = "103";
                break;
            default:
                encodedRole = "111";
            
        }
        return encodedRole;
    }

    private String encodeUserId(Long userId) {
        String encodedString = Base64.encodeBase64String(String.valueOf(userId).getBytes());
        byte[] decodedString = Base64.decodeBase64(encodedString);
        System.out.println(decodedString);
        System.out.println("Encoded String:" + encodedString);
        // Converting the encoded string to a six-digit integer
        int encodedNumber = Math.abs(encodedString.hashCode() % 10000000);

        System.out.println("User Id: " + userId);
        System.out.println("Encoded Number: " + encodedNumber);

        return Integer.toString(encodedNumber);
    }

    @Transactional
    public ApiResponse<?> getUserWallet() {
        try {

            User currentUser = SecurityUtils.getCurrentUser();

            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<Wallet> walletOptional = walletRepository.findByDeletedFlagAndUserId(Constants.NO,
                    currentUser.getId());

            if (walletOptional.isPresent()) {
                Wallet wallet = walletOptional.get();

                WalletResponse walletResponse = modelMapper.map(wallet,
                        WalletResponse.class);

                return new ApiResponse<>("User wallet fetched successfully.", walletResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("User wallet not found", null,
                        HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while fetching user wallet.", e);
            return new ApiResponse<>("An error occurred while fetching user wallet.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getAllWallets() {
        try {
            List<Wallet> walletList = walletRepository.findByDeletedFlag(Constants.NO);

            List<WalletResponse> walletResponseList = walletList.stream()
                    .map(wallet -> modelMapper.map(wallet, WalletResponse.class))
                    .collect(Collectors.toList());

            return new ApiResponse<>("Wallets successfully fetched.", walletResponseList, HttpStatus.OK.value());

        } catch (Exception e) {
            log.error("An error occurred while fetching Wallets.", e);

            return new ApiResponse<>("An error occurred while fetching Walllets.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getWalletById(Long id) {
        try {
            Optional<Wallet> walletOptional = walletRepository.findByDeletedFlagAndId(Constants.NO, id);
            if (walletOptional.isPresent()) {
                Wallet wallet = walletOptional.get();

                WalletResponse walletResponse = modelMapper.map(wallet, WalletResponse.class);

                return new ApiResponse<WalletResponse>("Wallet Fetched Successfully.", walletResponse,
                        HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("Wallet not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while fetching Wallets.", e);

            return new ApiResponse<>("An error occurred while fetching Walllets.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> deleteWallet(Long id) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }
            Optional<Wallet> walletOptional = walletRepository.findByDeletedFlagAndId(Constants.NO, id);

            if (walletOptional.isPresent()) {
                Wallet wallet = walletOptional.get();

                wallet.setDeletedAt(LocalDateTime.now());
                wallet.setDeletedBy(currentUser);
                wallet.setDeletedFlag(Constants.YES);

                walletRepository.save(wallet);

                return new ApiResponse<>("Wallet was successfully deleted.", null, HttpStatus.NO_CONTENT.value());

            } else {
                return new ApiResponse<>("Wallet not found.", null, HttpStatus.NOT_FOUND.value());

            }

        } catch (Exception e) {
            log.error("An error occurred while deleting Wallet.", e);

            return new ApiResponse<>("An error occurred while deleting Walllet.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

}
