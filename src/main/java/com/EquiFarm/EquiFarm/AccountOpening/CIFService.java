package com.EquiFarm.EquiFarm.AccountOpening;

import com.EquiFarm.EquiFarm.AccountOpening.DTO.CIFFinResponse;
import com.EquiFarm.EquiFarm.AccountOpening.DTO.CIFResponse;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CIFService {
    private final CIFRepo cifRepo;

    private final ModelMapper modelMapper;
//    public void performTransaction(CIF cif_creation) {
//        String date = new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss.SSS").format(new Date());
//        String requestId = UUID.randomUUID().toString();
//
//        String responsexml = "";
//        String xml = "<?xml version=\"1.0\" encoding=\"utf-16\"?>\n" +
//                "<FIXML xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns=\"http://www.finacle.com/fixml\">\n" +
//                "  <Header>\n" +
//                "    <RequestHeader>\n" +
//                "      <MessageKey>\n" +
//                "        <RequestUUID>"+requestId+"</RequestUUID>\n" +
//                "        <ServiceRequestId>RetCustAdd</ServiceRequestId>\n" +
//                "        <ServiceRequestVersion>10.2</ServiceRequestVersion>\n" +
//                "        <ChannelId>OMN</ChannelId>\n" +
//                "      </MessageKey>\n" +
//                "      <RequestMessageInfo>\n" +
//                "        <BankId>56</BankId>\n" +
//                "        <TimeZone />\n" +
//                "        <EntityId />\n" +
//                "        <EntityType />\n" +
//                "        <MessageDateTime>"+date+"</MessageDateTime>\n" +
//                "      </RequestMessageInfo>\n" +
//                "      <Security>\n" +
//                "        <Token>\n" +
//                "          <PasswordToken>\n" +
//                "            <UserId />\n" +
//                "            <Password />\n" +
//                "          </PasswordToken>\n" +
//                "        </Token>\n" +
//                "        <FICertToken />\n" +
//                "        <RealUserLoginSessionId />\n" +
//                "        <RealUser />\n" +
//                "        <RealUserPwd />\n" +
//                "        <SSOTransferToken />\n" +
//                "      </Security>\n" +
//                "    </RequestHeader>\n" +
//                "  </Header>\n" +
//                "  <Body>\n" +
//                "    <RetCustAddRequest>\n" +
//                "      <RetCustAddRq>\n" +
//                "        <CustDtls>\n" +
//                "          <CustData>\n" +
//                "            <AcctName>"+cif_creation.getFirstName() + cif_creation.getMiddleName()+ cif_creation.getLastName()+"</AcctName>\n" +
//                "            <AddrDtls>\n" +
//                "              <AddrLine1>NA</AddrLine1>\n" +
//                "              <AddrCategory>"+cif_creation.getAddress().getCategory()+"</AddrCategory>\n" +
//                "              <City>"+cif_creation.getAddress().getCity()+"</City>\n" +
//                "              <Country>"+cif_creation.getAddress().getCountry()+"</Country>\n" +
//                "              <FreeTextLabel>ADDR</FreeTextLabel>\n" +
//                "              <PrefAddr>N</PrefAddr>\n" +
//                "              <PrefFormat>FREE_TEXT_FORMAT</PrefFormat>\n" +
//                "              <StartDt>2023-03-21T13:45:26.784</StartDt>\n" +
//                "              <State>"+cif_creation.getAddress().getState()+"</State>\n" +
//                "              <PostalCode>"+cif_creation.getAddress().getPostalCode()+"</PostalCode>\n" +
//                "              <HoldMailFlag>N</HoldMailFlag>\n" +
//                "            </AddrDtls>\n" +
//                "            <AddrDtls>\n" +
//                "              <AddrLine1>NA</AddrLine1>\n" +
//                "              <AddrCategory>Mailing</AddrCategory>\n" +
//                "              <City>001</City>\n" +
//                "              <Country>KE</Country>\n" +
//                "              <FreeTextLabel>ADDR</FreeTextLabel>\n" +
//                "              <PrefAddr>Y</PrefAddr>\n" +
//                "              <PrefFormat>FREE_TEXT_FORMAT</PrefFormat>\n" +
//                "              <StartDt>2023-03-21T13:45:26.784</StartDt>\n" +
//                "              <State>KLA</State>\n" +
//                "              <PostalCode>80100</PostalCode>\n" +
//                "              <HoldMailFlag>N</HoldMailFlag>\n" +
//                "            </AddrDtls>\n" +
//                "            <BirthDt>"+cif_creation.getDateOfBirth().getDayOfMonth()+"</BirthDt>\n" +
//                "            <BirthMonth>"+cif_creation.getDateOfBirth().getMonth()+"</BirthMonth>\n" +
//                "            <BirthYear>"+cif_creation.getDateOfBirth().getYear()+"</BirthYear>\n" +
//                "            <CustType>Retail</CustType>\n" +
//                "            <CreatedBySystemId>OMN</CreatedBySystemId>\n" +
//                "            <DateOfBirth>"+cif_creation.getDateOfBirth()+"</DateOfBirth>\n" +
//                "            <Language>USA (English)</Language>\n" +
//                "            <LastName>"+cif_creation.getLastName()+"</LastName>\n" +
//                "            <MiddleName />\n" +
//                "            <FirstName>"+cif_creation.getFirstName()+"</FirstName>\n" +
//                "            <IsMinor>"+cif_creation.getMinor()+"</IsMinor>\n" +
//                "            <IsCustNRE>N</IsCustNRE>\n" +
//                "            <DefaultAddrType>Mailing</DefaultAddrType>\n" +
//                "            <Gender>"+cif_creation.getGender()+"</Gender>\n" +
//                "            <Manager>EO36130</Manager>\n" +
//                "            <NativeLanguageCode>INFENG</NativeLanguageCode>\n" +
//                "            <Occupation>"+cif_creation.getOccupation()+"</Occupation>\n" +
//                "            <SwiftCode>A564554664W</SwiftCode>\n" +
//                "            <CustId />\n" +
//                "            <PhoneEmailDtls>\n" +
//                "              <PhoneEmailType>CELLPH</PhoneEmailType>\n" +
//                "              <PhoneNumCityCode />\n" +
//                "              <PhoneNumCountryCode>"+cif_creation.getPhoneNumber().substring(1,4)+"</PhoneNumCountryCode>\n" +
//                "              <PhoneNumLocalCode>"+cif_creation.getPhoneNumber().substring(4)+"</PhoneNumLocalCode>\n" +
//                "              <PhoneOrEmail>PHONE</PhoneOrEmail>\n" +
//                "              <PrefFlag>Y</PrefFlag>\n" +
//                "            </PhoneEmailDtls>\n" +
//                "            <PhoneEmailDtls>\n" +
//                "              <Email>"+cif_creation.getEmail()+"/Email>\n" +
//                "              <PhoneEmailType>COMMEML</PhoneEmailType>\n" +
//                "              <PhoneOrEmail>EMAIL</PhoneOrEmail>\n" +
//                "              <PrefFlag>Y</PrefFlag>\n" +
//                "            </PhoneEmailDtls>\n" +
//                "            <PrefName>"+cif_creation.getPrefName()+"</PrefName>\n" +
//                "            <PrimarySolId>1000</PrimarySolId>\n" +
//                "            <Region>001</Region>\n" +
//                "            <RelationshipOpeningDt>"+date+"</RelationshipOpeningDt>\n" +
//                "            <Salutation>"+cif_creation.getSalutation()+"</Salutation>\n" +
//                "            <SegmentationClass>AGRIC</SegmentationClass>\n" +
//                "            <ShortName>"+cif_creation.getFirstName()+"</ShortName>\n" +
//                "            <StaffFlag>N</StaffFlag>\n" +
//                "            <SubSegment>AGRCB</SubSegment>\n" +
//                "            <TaxDeductionTable>Zero</TaxDeductionTable>\n" +
//                "            <SSN>0001112223344</SSN>\n" +
//                "            <TradeFinFlag>N</TradeFinFlag>\n" +
//                "            <IsEbankingEnabled>N</IsEbankingEnabled>\n" +
//                "            <Sector>F01S</Sector>\n" +
//                "            <SubSector>F42</SubSector>\n" +
//                "          </CustData>\n" +
//                "        </CustDtls>\n" +
//                "        <RelatedDtls>\n" +
//                "          <DemographicData>\n" +
//                "\t\t<DemographicMiscData>\n" +
//                "\t\t<Type>CURRENT_EMPLOYMENT</Type>  \n" +
//                "\t\t\t<StrText2>OTH</StrText2> \n" +
//                "\t\t\t<EmployerID>OTH</EmployerID>  \n" +
//                "\t\t</DemographicMiscData>\n" +
//                "            <NatureOfIncome>"+cif_creation.getNatureOfIncome()+"</NatureOfIncome>  \n" +
//                "            <NameOfEmployer>"+cif_creation.getNameOfEmployer()+"</NameOfEmployer>\n" +
//                "            <MaritalStatus>"+cif_creation.getMaritalStatus()+"</MaritalStatus>\n" +
//                "            <Nationality>"+cif_creation.getNationality()+"</Nationality>\n" +
//                "            <EmploymentStatus>"+cif_creation.getEmploymentStatus()+"</EmploymentStatus>\n" +
//                "          </DemographicData>\n" +
//                "          <EntityDoctData>\n" +
//                "            <CountryOfIssue>KE</CountryOfIssue>\n" +
//                "            <DocCode>DOC03</DocCode>\n" +
//                "            <Desc>"+cif_creation.getNationalId()+"</Desc>\n" +
//                "            <IssueDt>2018-03-29T21:00:00.000</IssueDt>\n" +
//                "            <TypeCode>INDIV</TypeCode>\n" +
//                "            <TypeDesc>IDENTIFICATION PROOF FOR INDIVIDUALS</TypeDesc>\n" +
//                "            <PlaceOfIssue>003</PlaceOfIssue>\n" +
//                "            <ReferenceNum>971889719822</ReferenceNum>\n" +
//                "            <PrefUniqueId>Y</PrefUniqueId>\n" +
//                "            <preferredUniqueId>Y</preferredUniqueId>\n" +
//                "            <IDIssuedOrganisation>URA</IDIssuedOrganisation>\n" +
//                "          </EntityDoctData>\n" +
//                "          <PsychographicData>\n" +
//                "            <PsychographMiscData>\n" +
//                "              <StrText10>KES</StrText10>\n" +
//                "              <StrText2>ARTIS</StrText2>\n" +
//                "              <Type>CURRENCY</Type>\n" +
//                "            </PsychographMiscData>\n" +
//                "            <preferred_Locale>ENGLISH</preferred_Locale>\n" +
//                "          </PsychographicData>\n" +
//                "          <TradeFinData>\n" +
//                "            <CustNative />\n" +
//                "            <InlandTradeAllowed />\n" +
//                "            <Rmks />\n" +
//                "            <TradeAuthorityCode />\n" +
//                "          </TradeFinData>\n" +
//                "        </RelatedDtls>\n" +
//                "      </RetCustAddRq>\n" +
//                "    </RetCustAddRequest>\n" +
//                "  </Body>\n" +
//                "</FIXML>";
//        try {
//            URL obj = new URL(FIN_URL);
//            disableSSlVerification();
//            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//
//            con.setRequestMethod("POST");
//            con.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
//            con.setDoOutput(true);
//
//            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
//                wr.writeBytes(xml);
//                wr.flush();
//            }
//            String responseStatus = con.getResponseMessage();
//            System.out.println(responseStatus);
//            StringBuffer response;
//            try (BufferedReader in = new BufferedReader(new InputStreamReader(
//                    con.getInputStream()))) {
//                String inputLine;
//                response = new StringBuffer();
//                while ((inputLine = in.readLine()) != null) {
//                    response.append(inputLine);
//                }
//            }
//            responsexml = response.toString();
//            String prettyJson = convertXML(responsexml);
//            System.out.println("XML Response:" + responsexml);
//            JSONObject joBody = new JSONObject(prettyJson);
//
//        } catch (Exception e) {
//            log.info("Error Creating CIF: " + e.getLocalizedMessage());
//        }
//    }

    public void disableSSlVerification() {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = (String hostname, SSLSession session) -> true;

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
        }
    }

//    public String convertXML(String xmlResponse) {
//        XmlMapper xmlMapper = new XmlMapper();
//
//        try {
//            JsonNode jsonNode = xmlMapper.readTree(xmlResponse.getBytes());
//            ObjectMapper objectMapper = new ObjectMapper();
//            String jsonResponse = objectMapper.writeValueAsString(jsonNode);
//
//            String unescapedJsonString = StringEscapeUtils.unescapeJson(jsonResponse);
//            System.out.println(unescapedJsonString);
//
////            System.out.println("Json Response: " + unescapedJsonString);
//            return unescapedJsonString;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    public CIFFinResponse fetchCifByIdNo(String nationalId) throws  Exception{
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .build();
        okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json");
        // Prepare the request payload
//        Headers headers = new Headers.Builder()
//                .add("Api_Key", API_KEY)
//                .build();

        Request request = new Request.Builder()
//                .url("http://3.140.250.115:8085/CIF/get")
                .url("http://localhost:8085/CIF/get")
//                .headers(headers)

                .get()
                .build();
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        JSONObject jsonObject = new JSONObject(responseBody);

        System.out.println("received Body: " + jsonObject);
        CIFFinResponse cifFinResponse = new CIFFinResponse();
        cifFinResponse.setCrncyCode(jsonObject.optString("crncyCode"));
        cifFinResponse.setCntryCode(jsonObject.optString("cntryCode"));
        cifFinResponse.setClrBalamt(jsonObject.optString("clrBalamt"));
        cifFinResponse.setCode(jsonObject.optString("code"));
        cifFinResponse.setSchemeType(jsonObject.optString("schemeType"));
        cifFinResponse.setAddress2(jsonObject.optString("address2"));
        cifFinResponse.setAddress1(jsonObject.optString("address1"));
        cifFinResponse.setAcctNum(jsonObject.optString("acctNum"));
        cifFinResponse.setPhoneNumber2(jsonObject.optString("phoneNumber2"));
        cifFinResponse.setAcctName(jsonObject.optString("acctName"));
        cifFinResponse.setSolld(jsonObject.optString("solld"));
        cifFinResponse.setAcid(jsonObject.optString("acid"));
        cifFinResponse.setLienAmt(jsonObject.optString("lienAmt"));
        cifFinResponse.setCifld(jsonObject.optString("Cifld"));
        cifFinResponse.setRefCode(jsonObject.optString("refCode"));
        cifFinResponse.setNationalId(nationalId);
        addCIFData(cifFinResponse);
        return cifFinResponse;
    }

    public CIF addCIFData(CIFFinResponse cifFinResponse) {
        try {
            CIF newCif = CIF.builder()
                    .Cifld(cifFinResponse.getCifld())
                    .AcctNum(cifFinResponse.getAcctNum())
                    .acid(cifFinResponse.getAcid())
                    .address1(cifFinResponse.getAddress1())
                    .address2(cifFinResponse.getAddress2())
                    .code(cifFinResponse.getCode())
                    .ClrBalamt(cifFinResponse.getClrBalamt())
                    .crncyCode(cifFinResponse.getCrncyCode())
                    .LienAmt(cifFinResponse.getLienAmt())
                    .schemeType(cifFinResponse.getSchemeType())
                    .phoneNumber2(cifFinResponse.getPhoneNumber2())
                    .refCode(cifFinResponse.getRefCode())
                    .nationalId(cifFinResponse.getNationalId())
                    .solld(cifFinResponse.getSolld())
                    .build();
            return cifRepo.save(newCif);
        } catch (Exception e) {
            log.info("Error adding data", e);
            return null;
        }
    }
    public Boolean checkCIFExists(String nationalId){
        try {
            return cifRepo.existsByNationalId(nationalId);
        } catch (Exception e){
            log.info("Error checking CIF Existence: ",e);
            return false;
        }
    }
    public ApiResponse<CIFResponse> fetchCIF(String nationalId, String firstName) {
            try {
                if (checkCIFExists(nationalId)) {
                    Optional<CIF> cifOptional = cifRepo.findByNationalIdAndFirstName(nationalId, firstName);
                if (cifOptional.isEmpty()) {
                    return new ApiResponse<>("Data not found", null, HttpStatus.NOT_FOUND.value());
                }
                CIF cif = cifOptional.get();
                CIFResponse cifResponse = modelMapper.map(cif, CIFResponse.class);
                return new ApiResponse<>("Success fetching CIF", cifResponse, HttpStatus.OK.value());
            }
            return new ApiResponse<>("CIF does not Exist", null, HttpStatus.NOT_FOUND.value());

            } catch (Exception e) {
                return new ApiResponse<>("Error fetching CIF", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
    }
    public ApiResponse<?> fetchAll(){
        try {
            List<CIF> cifList = cifRepo.findAll();
            List<CIFResponse> cifResponseList = cifList.stream()
                    .map(cif-> modelMapper.map(cif, CIFResponse.class))
                    .toList();
            return new ApiResponse<>("Success", cifResponseList, HttpStatus.OK.value());

        } catch (Exception e){
            log.info("Error occurred: ", e);
            return new ApiResponse<>("Error Occurred", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}

