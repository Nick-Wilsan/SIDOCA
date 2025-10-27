package com.sidoca.services;

import com.midtrans.Config;
import com.midtrans.service.MidtransSnapApi;
import com.midtrans.service.impl.MidtransSnapApiImpl; // Tambahkan import ini
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.midtrans.httpclient.error.MidtransError;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

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
        // Perbaikan: Gunakan MidtransSnapApiImpl
        this.snapApi = new MidtransSnapApiImpl(config);
    }

    public String createSnapToken(String orderId, double grossAmount, String itemName, String firstName, String email) throws MidtransError {
        Map<String, Object> transactionDetails = new HashMap<>();
        transactionDetails.put("order_id", orderId);
        transactionDetails.put("gross_amount", grossAmount);

        Map<String, String> itemDetails = new HashMap<>();
        itemDetails.put("id", "DONASI-" + orderId);
        itemDetails.put("price", String.valueOf(grossAmount));
        itemDetails.put("quantity", "1");
        itemDetails.put("name", itemName);
        
        Map<String, String> customerDetails = new HashMap<>();
        customerDetails.put("first_name", firstName);
        customerDetails.put("email", email);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("transaction_details", transactionDetails);
        requestBody.put("item_details", Collections.singletonList(itemDetails));
        requestBody.put("customer_details", customerDetails);

        return snapApi.createTransactionToken(requestBody);
    }
}