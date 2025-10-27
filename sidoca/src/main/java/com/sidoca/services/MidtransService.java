package com.sidoca.services;

import com.midtrans.Config;
import com.midtrans.service.MidtransSnapApi;
import com.midtrans.service.impl.MidtransSnapApiImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.midtrans.httpclient.error.MidtransError;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

@Service
public class MidtransService {

    @Value("${midtrans.server.key}")
    private String serverKey;

    @Value("${midtrans.client.key}")
    private String clientKey;

    @Value("${midtrans.is.production}")
    private boolean isProduction;

    private MidtransSnapApi snapApi;

    @PostConstruct
    public void init() {
        Config config = Config.builder()
                .setServerKey(serverKey)
                .setClientKey(clientKey)
                .setIsProduction(isProduction)
                .build();
        this.snapApi = new MidtransSnapApiImpl(config);
    }

    public String createSnapToken(String orderId, double nominalDonasi, String itemName, String firstName, String email) throws MidtransError {
        
        BigDecimal donasi = new BigDecimal(nominalDonasi);
        BigDecimal adminFee = donasi.multiply(new BigDecimal("0.1")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal grossAmount = donasi.add(adminFee);

        Map<String, Object> transactionDetails = new HashMap<>();
        transactionDetails.put("order_id", orderId);
        transactionDetails.put("gross_amount", grossAmount.doubleValue());

        List<Map<String, String>> itemDetailsList = new ArrayList<>();

        Map<String, String> itemDonasi = new HashMap<>();
        itemDonasi.put("id", "DONASI-" + orderId);
        itemDonasi.put("price", String.valueOf(donasi.doubleValue()));
        itemDonasi.put("quantity", "1");
        itemDonasi.put("name", itemName);
        itemDetailsList.add(itemDonasi);

        Map<String, String> itemBiayaAdmin = new HashMap<>();
        itemBiayaAdmin.put("id", "ADMIN-" + orderId);
        itemBiayaAdmin.put("price", String.valueOf(adminFee.doubleValue()));
        itemBiayaAdmin.put("quantity", "1");
        itemBiayaAdmin.put("name", "Biaya Admin (10%)");
        itemDetailsList.add(itemBiayaAdmin);
        
        Map<String, String> customerDetails = new HashMap<>();
        customerDetails.put("first_name", firstName);
        customerDetails.put("email", email);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("transaction_details", transactionDetails);
        requestBody.put("item_details", itemDetailsList);
        requestBody.put("customer_details", customerDetails);

        return snapApi.createTransactionToken(requestBody);
    }
}