package com.example.flutter_braintree;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.Card;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.BraintreeCancelListener;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.CardBuilder;
import com.braintreepayments.api.models.PaymentMethodNonce;

import java.util.HashMap;

public class FlutterBraintreeCustom extends AppCompatActivity implements PaymentMethodNonceCreatedListener, BraintreeCancelListener, BraintreeErrorListener {
    private BraintreeFragment braintreeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flutter_braintree_custom);
        try {
            Intent intent = getIntent();
            braintreeFragment = BraintreeFragment.newInstance(this, intent.getStringExtra("authorization"));
            String type = intent.getStringExtra("type");
            if (type.equals("tokenizeCreditCard")) {
                tokenizeCreditCard();
            } else {
                throw new Exception("Invalid request type: " + type);
            }
        } catch (Exception e) {
            Intent result = new Intent();
            result.putExtra("error", e);
            setResult(2, result);
            finish();
            return;
        }
    }

    protected void tokenizeCreditCard() {
        Intent intent = getIntent();
        CardBuilder builder = new CardBuilder()
                .cardNumber(intent.getStringExtra("cardNumber"))
                .expirationMonth("expirationMonth")
                .expirationYear("expirationYear");
        Card.tokenize(braintreeFragment, builder);
    }

    @Override
    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
        HashMap<String, Object> nonceMap = new HashMap<String, Object>();
        nonceMap.put("nonce", paymentMethodNonce.getNonce());
        nonceMap.put("typeLabel", paymentMethodNonce.getTypeLabel());
        nonceMap.put("description", paymentMethodNonce.getDescription());
        nonceMap.put("isDefault", paymentMethodNonce.isDefault());

        Intent result = new Intent();
        result.putExtra("type", "paymentMethodNonce");
        result.putExtra("paymentMethodNonce", nonceMap);
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    public void onCancel(int requestCode) {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onError(Exception error) {
        Intent result = new Intent();
        result.putExtra("error", error);
        setResult(2, result);
        finish();
    }
}
