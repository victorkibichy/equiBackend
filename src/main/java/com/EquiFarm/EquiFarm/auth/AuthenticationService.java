package com.EquiFarm.EquiFarm.auth;

import com.EquiFarm.EquiFarm.AccountOpening.CIFService;
import com.EquiFarm.EquiFarm.AccountOpening.DTO.CIFFinResponse;
import com.EquiFarm.EquiFarm.Admin.Admin;
import com.EquiFarm.EquiFarm.Admin.AdminRepository;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusiness;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessRepository;
import com.EquiFarm.EquiFarm.BankAccounts.BankAccount;
import com.EquiFarm.EquiFarm.BankAccounts.BankAccountRepository;
import com.EquiFarm.EquiFarm.Branch.Branch;
import com.EquiFarm.EquiFarm.Branch.BranchRepository;
import com.EquiFarm.EquiFarm.Customer.Customer;
import com.EquiFarm.EquiFarm.Customer.CustomerRepository;
import com.EquiFarm.EquiFarm.Driver.Driver;
import com.EquiFarm.EquiFarm.Driver.DriverRepository;
import com.EquiFarm.EquiFarm.FarmTech.FarmTech;
import com.EquiFarm.EquiFarm.FarmTech.FarmTechRepository;
import com.EquiFarm.EquiFarm.Farmer.Farmer;
import com.EquiFarm.EquiFarm.Farmer.FarmerRepository;
import com.EquiFarm.EquiFarm.Manufacturer.Manufacturer;
import com.EquiFarm.EquiFarm.Manufacturer.ManufacturerRepo;
import com.EquiFarm.EquiFarm.Processor.Processor;
import com.EquiFarm.EquiFarm.Processor.ProcessorRepository;
import com.EquiFarm.EquiFarm.ServiceProvider.ServiceProvider;
import com.EquiFarm.EquiFarm.ServiceProvider.ServiceProviderRepository;
import com.EquiFarm.EquiFarm.Staff.Staff;
import com.EquiFarm.EquiFarm.Staff.StaffRepository;
import com.EquiFarm.EquiFarm.Wallet.WalletService;
import com.EquiFarm.EquiFarm.Warehouse.Warehouse;
import com.EquiFarm.EquiFarm.Warehouse.WarehouseRepo;
import com.EquiFarm.EquiFarm.WishList.WishList;
import com.EquiFarm.EquiFarm.WishList.WishListRepository;
import com.EquiFarm.EquiFarm.auth.DTO.AuthenticationRequest;
import com.EquiFarm.EquiFarm.auth.DTO.AuthenticationResponse;
import com.EquiFarm.EquiFarm.auth.DTO.RegisterRequest;
import com.EquiFarm.EquiFarm.auth.DTO.SignoutRequest;
import com.EquiFarm.EquiFarm.config.JwtService;
import com.EquiFarm.EquiFarm.token.Token;
import com.EquiFarm.EquiFarm.token.TokenRepository;
import com.EquiFarm.EquiFarm.token.TokenType;
import com.EquiFarm.EquiFarm.user.Role;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.user.UserRepository;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.io.IOException;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final FarmerRepository farmerRepository;
    private final AdminRepository adminRepository;
    private final CustomerRepository customerRepository;
    private final WalletService walletService;
    private final WishListRepository wishListRepository;
    private final DriverRepository driverRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final AgriBusinessRepository agriBusinessRepository;
    private final StaffRepository staffRepository;
    private final ManufacturerRepo manufacturerRepo;
    private final WarehouseRepo warehouseRepo;
    private final ProcessorRepository processorRepository;
    private final FarmTechRepository farmTechRepository;
    private final CIFService cifService;
    private final BankAccountRepository bankAccountRepository;
    private final ModelMapper modelMapper;
    private final BranchRepository branchRepository;

//    public ApiResponse<?> register(RegisterRequest request) {
//        try {
//            // Check if either email or phone number is provided
//            if (StringUtils.isBlank(request.getEmail()) && StringUtils.isBlank(request.getPhoneNo())) {
//                return new ApiResponse<>("Either email or phone number must be provided.", null, HttpStatus.BAD_REQUEST.value());
//            }
//
//            // Check if a user with the provided email already exists
//            if (StringUtils.isNotBlank(request.getEmail())) {
//                Optional<User> checkIfUserEmailExists = repository.findByEmail(request.getEmail());
//                if (checkIfUserEmailExists.isPresent()) {
//                    return new ApiResponse<>("A user with this email already exists.", null, HttpStatus.BAD_REQUEST.value());
//                }
//            }
//
//            // Check if other necessary fields are filled and proceed with registration
//            if (StringUtils.isNotBlank(request.getFirstName()) && StringUtils.isNotBlank(request.getLastName()) &&
//                    StringUtils.isNotBlank(request.getPassword()) && request.getRole() != null) {
//
//                var userBuilder = User.builder()
//                        .firstName(request.getFirstName())
//                        .lastName(request.getLastName())
//                        .nationalId(request.getNationalId())
//                        .latitude(request.getLatitude() != null ? request.getLatitude() : -0.0236)
//                        .longitude(request.getLongitude() != null ? request.getLongitude() : 37.9062)
//                        .password(passwordEncoder.encode(request.getPassword()))
//                        .role(request.getRole());
//
//                // Set either email or phone number, but not both
//                if (StringUtils.isNotBlank(request.getEmail())) {
//                    userBuilder.email(request.getEmail());
//                } else {
//                    userBuilder.phoneNo(request.getPhoneNo());
//                }
//
//                var user = userBuilder.build();
//
//                var savedUser = repository.save(user);
//                var jwtToken = jwtService.generateToken(user);
//                var refreshToken = jwtService.generateRefreshToken(user);
//                saveUserToken(savedUser, jwtToken);
//
//                createUserProfile(savedUser);
//                walletService.createWallet(savedUser);
//
//                AuthenticationResponse authResponse = modelMapper.map(savedUser, AuthenticationResponse.class);
//                authResponse.setAccessToken(jwtToken);
//                authResponse.setRefreshToken(refreshToken);
//
//                return new ApiResponse<>("Sign up successful", authResponse, HttpStatus.CREATED.value());
//            } else {
//                return new ApiResponse<>("Invalid registration data.", null, HttpStatus.BAD_REQUEST.value());
//            }
//        } catch (Exception e) {
//            log.info("An error occurred", e);
//            return new ApiResponse<>("An error occurred", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
//        }
//    }


    private  final UserDetailsService userDetailsService;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String PHONE_NUMBER_REGEX = "^[0-9]{12}$";


    @Transactional
    public ApiResponse<?> register(RegisterRequest request) {
        try {
            if (request.getEmail() != null && request.getEmail().length() > 0) {
                Optional<User> checkIfUserEmailExists = repository.findByEmail(request.getEmail());
                if (checkIfUserEmailExists.isPresent()) {
                    return new ApiResponse<>("A user with this email already exists.", null,
                            HttpStatus.BAD_REQUEST.value());
                }
            }
            Optional<User> checkIfNationalIdExists = repository.findByNationalId(request.getNationalId());
            if (checkIfNationalIdExists.isPresent()) {
                return new ApiResponse<>("A user with this National ID already exists.", null,
                        HttpStatus.BAD_REQUEST.value());
            }
            if(!isValidEmail(request.getEmail())){
              return new ApiResponse<>("Invalid email format", null, HttpStatus.BAD_REQUEST.value());
            }
            if(!isValidPhoneNumber(request.getPhoneNo())){
                return new ApiResponse<>("Invalid phone number format", null, HttpStatus.BAD_REQUEST.value());
            }
            var user = User.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .phoneNo(request.getPhoneNo())
                    .nationalId(request.getNationalId())
                    .latitude(request.getLatitude())
                    .longitude(request.getLongitude())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(request.getRole())
                    .build();

            var savedUser = repository.save(user);
            var jwtToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);
            saveUserToken(savedUser, jwtToken);

            createUserProfile(savedUser);
            walletService.createWallet(savedUser);

            AuthenticationResponse authResponse = modelMapper.map(savedUser, AuthenticationResponse.class);
            authResponse.setAccessToken(jwtToken);
            authResponse.setRefreshToken(refreshToken);

            return new ApiResponse<AuthenticationResponse>("Sign up successful", authResponse,
                    HttpStatus.CREATED.value());
        } catch (Exception e) {
            log.info("An error occurred", e);
            return new ApiResponse<>("An error occurred", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> authenticate(AuthenticationRequest request) {
        try {
            try {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
//                                request.getEmail(),
                                request.getEmailOrNationalId(),
                                request.getPassword()));

                /** Check if user has a profile, if not exist call createuserprofile method else continue*/


            } catch (Exception e) {
                log.error(e.getLocalizedMessage());
                return new ApiResponse<>("Invalid Email or Password", null, HttpStatus.BAD_REQUEST.value());
            }

//            User loggedUser = repository.findByEmail(request.getEmail())
//                    .orElseThrow();
            User loggedUser = repository.findByEmailOrNationalId(request.getEmailOrNationalId(), request.getEmailOrNationalId())
                    .orElseThrow();
//            System.out.println("User...." + loggedUser.getEmail());
            System.out.println("User...." + loggedUser.getEmail() + " " + loggedUser.getNationalId());
            var jwtToken = jwtService.generateToken(loggedUser);
            var refreshToken = jwtService.generateRefreshToken(loggedUser);
            revokeAllUserTokens(loggedUser);
            saveUserToken(loggedUser, jwtToken);

            AuthenticationResponse authResponse = modelMapper.map(loggedUser, AuthenticationResponse.class);
            authResponse.setAccessToken(jwtToken);
            authResponse.setRefreshToken(refreshToken);

            return new ApiResponse<AuthenticationResponse>("Authentication successful", authResponse,
                    HttpStatus.OK.value());
        } catch (Exception e) {
            return new ApiResponse<>("An error occurred", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    public ApiResponse<?> signOut(SignoutRequest signoutRequest) {
        try {
            String accessToken = signoutRequest.getAccessToken();
            String userEmail = jwtService.extractUsername(accessToken);

            if (userEmail != null) {
                var user = this.repository.findByEmail(userEmail).orElseThrow();

                // Revoke all tokens for the user
                revokeAllUserTokens(user);
                return new ApiResponse<>("Sign out was successful.", null, HttpStatus.OK.value());
            }

            return new ApiResponse<>("Invalid access token.", null, HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            log.error("An error occurred while signing out.", e);
            return new ApiResponse<>("An error occurred while signing out.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
    public void createUserProfile(User user) throws Exception {
        CIFFinResponse cif = cifService.fetchCifByIdNo(String.valueOf(user.getNationalId()));
        BankAccount bankAccount = new BankAccount();

        WishList userWishList = new WishList();
        userWishList.setUser(user);
        wishListRepository.save(userWishList);

        if (user.getRole() == Role.ADMIN || user.getIsAdmin() == true || user.getIsStaff()) {
            Admin admin = new Admin();
            admin.setUser(user);
            User addedUser = admin.getUser();
            user.setIsAdmin(true);

            repository.save(addedUser);
            adminRepository.save(admin);
        } else if (user.getRole() == Role.FARMER) {
            Farmer farmer = new Farmer();
            farmer.setUser(user);
            if (cif != null) {
                Optional<Branch> branchOptional = branchRepository
                        .findByDeletedFlagAndSolId(Constants.NO, cif.getSolld());
                if (branchOptional.isPresent()) {
                    Branch branch = branchOptional.get();
                    farmer.setBranch(branch);
                }
                farmer.setIdNumber(cif.getNationalId());
                farmer.getUser().setPhoneNo(user.getPhoneNo());
//                farmer.setGender(cif.getGender());
                bankAccount.setUser(user);
                bankAccount.setAccountNumber(generateRandomAccounts());
                bankAccount.setAccountName(user.getFirstName() + user.getLastName());
                bankAccount.setUser(user);
                bankAccount.setAccBal(generateRandomBals());
                bankAccountRepository.save(bankAccount);
            }
            System.out.println(generateFarmerCode());
            farmer.setFarmerCode(generateFarmerCode());

            farmerRepository.save(farmer);
        } else if (user.getRole() == Role.CUSTOMER) {
            Customer customer = new Customer();
            customer.setUser(user);
            if (cif != null) {
                Optional<Branch> branchOptional = branchRepository
                        .findByDeletedFlagAndSolId(Constants.NO, cif.getSolld());
                if (branchOptional.isPresent()) {
                    Branch branch = branchOptional.get();
                    customer.setBranch(branch);
                }
                customer.setIdNumber(cif.getNationalId());
                customer.getUser().setPhoneNo(user.getPhoneNo());
//                customer.setGender(cif.getGender());
                bankAccount.setUser(user);
                bankAccount.setAccountNumber(generateRandomAccounts());
                bankAccount.setAccountName(user.getFirstName() + user.getLastName());
                bankAccount.setUser(user);
                bankAccount.setAccBal(generateRandomBals());
                bankAccountRepository.save(bankAccount);
            }

            customerRepository.save(customer);
        } else if (user.getRole() == Role.DRIVER) {
            Driver driver = new Driver();
            driver.setUser(user);
            if (cif != null) {
                Optional<Branch> branchOptional = branchRepository
                        .findByDeletedFlagAndSolId(Constants.NO, cif.getSolld());
                if (branchOptional.isPresent()) {
                    Branch branch = branchOptional.get();
                    driver.setBranch(branch);
                }
                driver.setIdNumber(cif.getNationalId());
                driver.getUser().setPhoneNo(user.getPhoneNo());
//                driver.setGender(cif.getGender());
                bankAccount.setUser(user);
                bankAccount.setAccountNumber(generateRandomAccounts());
                bankAccount.setAccountName(user.getFirstName() + user.getLastName());
                bankAccount.setUser(user);
                bankAccount.setAccBal(generateRandomBals());
                bankAccountRepository.save(bankAccount);
            }
            System.out.println(generateDriverCode());
            driver.setDriverCode(generateDriverCode());
            driverRepository.save(driver);
        } else if (user.getRole() == Role.SERVICE_PROVIDER) {
            ServiceProvider serviceProvider = new ServiceProvider();
            serviceProvider.setUser(user);
            if (cif != null) {
                Optional<Branch> branchOptional = branchRepository
                        .findByDeletedFlagAndSolId(Constants.NO, cif.getSolld());
                if (branchOptional.isPresent()) {
                    Branch branch = branchOptional.get();
                    serviceProvider.setBranch(branch);
                }
                serviceProvider.setIdNumber(cif.getNationalId());
                serviceProvider.getUser().setPhoneNo(user.getPhoneNo());
//                serviceProvider.setGender(cif.getGender());
            }
            serviceProvider.setSpCode(generateServiceProviderCode());
            serviceProviderRepository.save(serviceProvider);

        } else if (user.getRole() == Role.MANUFACTURER) {
            Manufacturer manufacturer = new Manufacturer();
            manufacturer.setUser(user);
            if (cif != null) {
                manufacturer.setIdNumber(cif.getNationalId());
                manufacturer.getUser().setPhoneNo(user.getPhoneNo());
//                manufacturer.setGender(cif.getGender());
            }
            System.out.println(generateManufacturerCode());
            manufacturer.setManufacturerCode(generateManufacturerCode());

            manufacturerRepo.save(manufacturer);
        } else if (user.getRole() == Role.PROCESSOR) {
            Processor processor = new Processor();
            processor.setUser(user);
            if (cif != null) {
                processor.setIdNumber(cif.getNationalId());
                processor.getUser().setPhoneNo(user.getPhoneNo());
            }
            System.out.println(generateProcessorCode());
            processor.setProcessorCode(generateProcessorCode());

            processorRepository.save(processor);

        } else if (user.getRole() == Role.FARMTECH_OWNER) {
            FarmTech farmTech = new FarmTech();
            farmTech.setUser(user);
            if (cif != null) {
                farmTech.setIdNumber(cif.getNationalId());
                farmTech.getUser().setPhoneNo(user.getPhoneNo());
            }
            System.out.println(generateFarmTechCode());
            farmTech.setFarmTechCode(generateFarmTechCode());

            farmTechRepository.save(farmTech);



        }else if (user.getRole() == Role.AGRIBUSINESS_OWNER) {
            AgriBusiness agriBusiness = new AgriBusiness();
            agriBusiness.setUser(user);
            if (cif != null) {
                Optional<Branch> branchOptional = branchRepository
                        .findByDeletedFlagAndSolId(Constants.NO, cif.getSolld());
                if (branchOptional.isPresent()) {
                    Branch branch = branchOptional.get();
                    agriBusiness.setBranch(branch);
                }
                agriBusiness.setIdNumber(cif.getNationalId());
                agriBusiness.getUser().setPhoneNo(user.getPhoneNo());
//                agriBusiness.setGender(cif.getGender());
                bankAccount.setUser(user);
                bankAccount.setAccountNumber(generateRandomAccounts());
                bankAccount.setAccountName(user.getFirstName() + user.getLastName());
                bankAccount.setUser(user);
                bankAccount.setAccBal(generateRandomBals());
                bankAccountRepository.save(bankAccount);
            }
            System.out.println(generateAgribusinessCode());
            agriBusiness.setAgribusinessCode(generateAgribusinessCode());
            agriBusinessRepository.save(agriBusiness);

        } else if (user.getRole() == Role.STAFF) {
            Staff staff = new Staff();
            staff.setUser(user);
            staffRepository.save(staff);

        } else if (user.getRole() == Role.WAREHOUSE) {
            Warehouse warehouse = new Warehouse();
            warehouse.setUser(user);
            if (cif != null) {
                warehouse.setIdNumber(cif.getNationalId());
                warehouse.getUser().setPhoneNo(user.getPhoneNo());
//                warehouse.setGender(cif.getGender());
                bankAccount.setUser(user);
                bankAccount.setAccountNumber(generateRandomAccounts());
                bankAccount.setAccountName(user.getFirstName() + user.getLastName());
                bankAccount.setUser(user);
                bankAccount.setAccBal(generateRandomBals());
                bankAccountRepository.save(bankAccount);
            }
            System.out.println(generateWarehouseCode());
            warehouse.setWarehouseCode(generateWarehouseCode());

            warehouseRepo.save(warehouse);
        }
    }
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .tokenType(String.valueOf(TokenType.BEARER))
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                try {
                    new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
                } catch (java.io.IOException e) {

                    e.printStackTrace();
                }
            }
        }
    }

    public String generateFarmerCode() {
        String newFarmerNo = "";
        Optional<Farmer> farmerOptional = farmerRepository.findFirstByOrderByFarmerCodeDesc();
        if (farmerOptional.isPresent()) {
            String memberNo = farmerOptional.get().getFarmerCode();
            int suffixNo = Integer.parseInt(memberNo.substring(2));
            String formattedCode = String.format("%05d", suffixNo + 1);
            newFarmerNo = "FA" + formattedCode;
        } else {
            newFarmerNo = "FA00001";
        }

        return newFarmerNo;
    }
    public String generateServiceProviderCode() {
        String newSpCode = "";
        Optional<ServiceProvider> spOptional = serviceProviderRepository.findFirstByOrderBySpCodeDesc();
        if (spOptional.isPresent()) {
            String spNo = spOptional.get().getSpCode();
            int suffixNo = Integer.parseInt(spNo.substring(2));
            String formattedCode = String.format("%05d", suffixNo + 1);
            newSpCode = "SP" + formattedCode;
        } else {
            newSpCode = "SP00001";
        }

        return newSpCode;
    }
    public String generateManufacturerCode() {
        String newManufacturerNo = "";
        Optional<Manufacturer> manufacturerOptional = manufacturerRepo.findFirstByOrderByManufacturerCodeDesc();
        if (manufacturerOptional.isPresent()) {
            String manNo = manufacturerOptional.get().getManufacturerCode();
            int suffixNo = Integer.parseInt(manNo.substring(2));
            String formattedCode = String.format("%05d", suffixNo + 1);
            newManufacturerNo = "MA" + formattedCode;
        } else {
            newManufacturerNo = "MA00001";
        }

        return newManufacturerNo;
    }
    public String generateProcessorCode() {
        String newProcessorNo = "";
        Optional<Processor> processorOptional = processorRepository.findFirstByOrderByProcessorCodeDesc();
        if (processorOptional.isPresent()) {
            String ProcNo = processorOptional.get().getProcessorCode();
            int suffixNo = Integer.parseInt(ProcNo.substring(2));
            String formattedCode = String.format("%05d", suffixNo + 1);
            newProcessorNo = "PA" + formattedCode;
        } else {
            newProcessorNo = "PA00001";
        }

        return newProcessorNo;
    }

    public String generateFarmTechCode() {
        String newFarmTechNo = "";
        Optional<FarmTech> farmTechOptional = farmTechRepository.findFirstByOrderByFarmTechCodeDesc();
        if (farmTechOptional.isPresent()) {
            String memberNo = farmTechOptional.get().getFarmTechCode();
            int suffixNo = Integer.parseInt(memberNo.substring(2));
            String formattedCode = String.format("%05d", suffixNo + 1);
            newFarmTechNo = "FT" + formattedCode;
        } else {
            newFarmTechNo = "FT00001";
        }

        return newFarmTechNo;
    }

    public String generateWarehouseCode() {
        String newWarehouseNo = "";
        Optional<Warehouse> warehouseOptional = warehouseRepo.findFirstByOrderByWarehouseCodeDesc();
        if (warehouseOptional.isPresent()) {
            String memberNo = warehouseOptional.get().getWarehouseCode();
            int suffixNo = Integer.parseInt(memberNo.substring(2));
            String formattedCode = String.format("%05d", suffixNo + 1);
            newWarehouseNo = "WA" + formattedCode;
        } else {
            newWarehouseNo = "WA00001";
        }

        return newWarehouseNo;
    }
    public String generateDriverCode() {
        String newDriverNo = "";
        Optional<Driver> driverOptional = driverRepository.findFirstByOrderByDriverCodeDesc();
        if (driverOptional.isPresent()) {
            log.info("Increment by 1");
            String memberNo = driverOptional.get().getDriverCode();
            int suffixNo = Integer.parseInt(memberNo.substring(2));
            String formattedCode = String.format("%05d", suffixNo + 1);
            newDriverNo = "DR" + formattedCode;
        } else {
            newDriverNo = "DR00001";
        }

        return newDriverNo;
    }
    public String generateAgribusinessCode() {
        String newAgribusinessNo = "";
        Optional<AgriBusiness> agribusinessOptional = agriBusinessRepository.findFirstByOrderByAgribusinessCodeDesc();
        if (agribusinessOptional.isPresent()) {
            log.info("Increment by 1");
            String memberNo = agribusinessOptional.get().getAgribusinessCode();
            int suffixNo = Integer.parseInt(memberNo.substring(2));
            String formattedCode = String.format("%05d", suffixNo + 1);
            newAgribusinessNo = "AB" + formattedCode;
        } else {
            newAgribusinessNo = "AB00001";
        }

        return newAgribusinessNo;
    }
    public static String generateRandomAccounts() {
        Random random = new Random();
        int length = 13;
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10); // Generate a random digit between 0 and 9
            sb.append(digit);
        }

        return sb.toString();
    }
    public static String generateRandomBals() {
        Random random = new Random();
        int length = 6;
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10); // Generate a random digit between 0 and 9
            sb.append(digit);
        }

        return sb.toString();
    }
//    public static boolean isValidEmail(String email) {
//        Pattern pattern = Pattern.compile(EMAIL_REGEX);
//        Matcher matcher = pattern.matcher(email);
//        return matcher.matches();
//    }

    public static boolean isValidEmail(@Nullable String email) {
        if (email == null) {
            return true;
        }
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        Pattern pattern = Pattern.compile(PHONE_NUMBER_REGEX);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
}
