package com.EquiFarm.EquiFarm.EscrowWallet;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.tomcat.util.codec.binary.Base64;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.EquiFarm.EquiFarm.Checkout.Checkout;
import com.EquiFarm.EquiFarm.EscrowWallet.DTO.EscrowWalletResponse;
import com.EquiFarm.EquiFarm.Wallet.Wallet;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;

import jakarta.transaction.Transactional;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EscrowWalletService {
    private final EscrowWalletRepository escrowWalletRepository;
    private final ModelMapper modelMapper;

    public EscrowWallet createEscrowWallet(Checkout checkout, TypeOfTrade typeOfTrade, Wallet receiverWallet,
            Wallet senderWallet) {
        EscrowWallet escrowWallet = new EscrowWallet();
        escrowWallet.setCheckout(checkout);
        escrowWallet.setWalletNumber(String.valueOf(generateEscrowWalletNumber(checkout, typeOfTrade)));
        escrowWallet.setReceiverWallet(receiverWallet);
        escrowWallet.setSenderWallet(senderWallet);
        escrowWallet.setCurrency(receiverWallet.getCurrency());
        escrowWallet.setTypeOfTrade(typeOfTrade);

        escrowWalletRepository.save(escrowWallet);

        System.out.println("Escrow Wallet Created:::->" + escrowWallet.getWalletNumber());
        // 202|2373700|1
        return escrowWallet;
    }

    private Long generateEscrowWalletNumber(Checkout checkout, TypeOfTrade typeOfTrade) {
        String encodedTypeOfTrade = encodeTypeOfTrade(typeOfTrade);
        String encodedCheckoutId = encodeCheckoutId(checkout.getId());
        String escrowWalletNumber = encodedTypeOfTrade + encodedCheckoutId + checkout.getId();

        return Long.parseLong(escrowWalletNumber);
    }

    private String encodeTypeOfTrade(TypeOfTrade typeOfTrade) {
        String encodedTypeOfTrade;
        switch (typeOfTrade) {
            case FARMPRODUCT:
                encodedTypeOfTrade = "201";
                break;
            case AGRIBUSINESSPRODUCT:
                encodedTypeOfTrade = "202";
                break;
            case SERVICE:
                encodedTypeOfTrade = "203";
                break;
            default:
                encodedTypeOfTrade = "222";
        }
        return encodedTypeOfTrade;
    }

    private String encodeCheckoutId(Long checkoutId) {
        String encodedString = Base64.encodeBase64String(String.valueOf(checkoutId).getBytes());
        byte[] decodedString = Base64.decodeBase64(encodedString);
        System.out.println(decodedString);
        System.out.println("Encoded String:" + encodedString);
        // Converting the encoded string to a six-digit integer
        int encodedNumber = Math.abs(encodedString.hashCode() % 10000000);

        System.out.println("Checkout Id: " + checkoutId);
        System.out.println("Encoded Number: " + encodedNumber);

        return Integer.toString(encodedNumber);
    }

    public ApiResponse<?> getAllEscrowWallets() {
        try {
            List<EscrowWallet> escrowWalletList = escrowWalletRepository.findByDeletedFlag(Constants.NO);
            List<EscrowWalletResponse> escrowWalletResponses = escrowWalletList.stream()
                    .map(wallet -> modelMapper.map(wallet, EscrowWalletResponse.class)).collect(Collectors.toList());

            return new ApiResponse<>("Escrow wallets fetched successfully", escrowWalletResponses,
                    HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("An error occurred while fetching escrow wallets.", e);

            return new ApiResponse<>("An error occurred while escrow wallets.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getEscrowWalletById(Long id) {
        try {
            Optional<EscrowWallet> escrowWalletOptional = escrowWalletRepository.findByDeletedFlagAndId(Constants.NO,
                    id);

            if (escrowWalletOptional.isPresent()) {
                EscrowWallet escrowWallet = escrowWalletOptional.get();

                EscrowWalletResponse escrowWalletResponse = modelMapper.map(escrowWallet, EscrowWalletResponse.class);

                return new ApiResponse<>("Escrow wallet fetched successfully.", escrowWalletResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Escrow wallet not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching escrow wallet.", e);

            return new ApiResponse<>("An error occurred while escrow wallet.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

}
