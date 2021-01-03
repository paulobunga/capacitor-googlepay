package com.caratafrica.googlepay;

import android.app.Activity;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.WalletConstants;

import java.util.Optional;

import org.json.JSONException;
import org.json.JSONObject;

@NativePlugin
public class IonicGooglePay extends Plugin {

    private PaymentsClient paymentsClient;
    private static final String IS_READY_TO_PAY = "is_ready_to_pay";
    private static final String REQUEST_PAYMENT = "request_payment";
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 42;

    private static final String ENVIRONMENT_PRODUCTION_KEY = "ENVIRONMENT_PRODUCTION";

    private static final String ENVIRONMENT_TEST_KEY = "ENVIRONMENT_TEST";

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", value);
        call.success(ret);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @PluginMethod
    public void isReadyToPay(final PluginCall call) {

        final boolean[] result = {false};
        final Optional<JSONObject> isReadyToPayJson = PaymentsUtil.getIsReadyToPayRequest();
        if (!isReadyToPayJson.isPresent()) {
//            return false;
            call.reject("Google play not available");
        }

        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.get().toString());
        Task<Boolean> task = paymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(getActivity(), new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if(task.isSuccessful()) {
                    //SetGooglePayAvailable
                    call.resolve();
                } else {
                    Log.w("isReadyToPay failed", task.getException());
                    call.reject(task.getException().getLocalizedMessage());
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @PluginMethod
    public void requestPayment(final PluginCall call) {
        double itemPrice = call.getDouble("price");
        long itemPriceCents = Math.round(itemPrice * PaymentsUtil.CENTS_IN_A_UNIT.longValue());
        long priceCents = itemPriceCents;

        Optional<JSONObject> paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(priceCents);
        if (!paymentDataRequestJson.isPresent()) {
            return;
        }

        PaymentDataRequest request =
                PaymentDataRequest.fromJson(paymentDataRequestJson.get().toString());

        // Since loadPaymentData may show the UI asking the user to select a payment method, we use
        // AutoResolveHelper to wait for the user interacting with it. Once completed,
        // onActivityResult will be called with the result.
        if (request != null) {
            AutoResolveHelper.resolveTask(
                    paymentsClient.loadPaymentData(request),
                    getActivity(), LOAD_PAYMENT_DATA_REQUEST_CODE);
        }

    }


    @Override
    public void load() {
        super.load();
        paymentsClient = PaymentsUtil.createPaymentsClient(getActivity());
    }
}
