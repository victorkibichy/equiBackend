package com.EquiFarm.EquiFarm.TempTransactions;

import com.EquiFarm.EquiFarm.AccountOpening.DTO.CIFFinResponse;
import com.EquiFarm.EquiFarm.TempTransactions.DTO.FinRequest;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class FinService {
    private final ModelMapper modelMapper;
    public CustomResponse fundTransfer(FinRequest finRequest) throws Exception {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .build();
        okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json");
        // Prepare the request payload
//        Headers headers = new Headers.Builder()
//                .add("Api_Key", API_KEY)
//                .build();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("drAcr", finRequest.getDrAcc());
        requestBody.put("crAcr", finRequest.getCrAcc());
        requestBody.put("amount", finRequest.getAmount());

        String jsonBody = new Gson().toJson(requestBody);

        RequestBody body = RequestBody.create(mediaType, jsonBody);

        Request request = new Request.Builder()
                .url("http://3.140.250.115:8085/payments/convert/json")
//                .headers(headers)
//                .post(body)
                .get()
                .build();
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string(); // Get the response body as a string
        JSONObject jsonObject = new JSONObject(responseBody); // Parse the string as JSON

// Now you can access the JSON data
//                JSONObject joBody = jsonObject.getJSONObject("body");
        System.out.println("recieved Body: " + jsonObject);
        CustomResponse customResponse = new CustomResponse();
        customResponse.setStatus(jsonObject.optString("status"));
        customResponse.setTransDate(jsonObject.optString("transDate"));
        customResponse.setTransId(jsonObject.optString("transId"));
        customResponse.setMessage(jsonObject.optString("message"));
//                customResponse.setTransDate(jsonObject.getString("transDate"));

        return customResponse;
    }
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
                .url("http://3.140.250.115:8085/CIF/get")
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
        cifFinResponse.setAcid(jsonObject.optString("acid"));
        cifFinResponse.setLienAmt(jsonObject.optString("lienAmt"));
        cifFinResponse.setCifld(jsonObject.optString("Cifld"));
        cifFinResponse.setRefCode(jsonObject.optString("refCode"));

        return cifFinResponse;
    }
}
