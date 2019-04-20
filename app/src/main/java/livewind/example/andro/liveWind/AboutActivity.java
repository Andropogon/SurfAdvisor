package livewind.example.andro.liveWind;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

import livewind.example.andro.liveWind.googlePay.GooglePay;


public class AboutActivity extends AppCompatActivity {

    private PaymentsClient mPaymentsClient;
    private Button mDonateUsButton;
    /**
     * Arbitrarily-picked constant integer you define to track a request for payment data activity.
     *
     * @value #LOAD_PAYMENT_DATA_REQUEST_CODE
     */
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(livewind.example.andro.liveWind.R.layout.activity_about);

        mDonateUsButton = (Button) findViewById(R.id.donate_us_about_activity_button);
        mDonateUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDonateMethodDialog();
               // requestPayment(voiew);
            }
        });

        TextView contactWithDeveloperTextView = findViewById(R.id.user_about_info_for_owners_contact_text_view);

        contactWithDeveloperTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                contactWithDevelopers(AboutActivity.this);
            }
        });
        /**
         * Create an instance of PaymentsClient in the onCreate method in your Activity.
         * The PaymentsClient is used for interaction with the Google Pay API.
         */
        mPaymentsClient =
                Wallet.getPaymentsClient(
                        this,
                        new Wallet.WalletOptions.Builder()
                                .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                                .build());
    }

    /**
     * Dialog where you select with type of donate method you want to use
     */
    public void showDonateMethodDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, livewind.example.andro.liveWind.R.style.DialogeTheme);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_about_dialog_select_donate_type,null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView)
                //builder.setView(gridView)
                .setPositiveButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });

        RelativeLayout payPalDonate = (RelativeLayout) dialogView.findViewById(R.id.about_activity_donate_by_paypal_relativelayout_view);
        payPalDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://www.paypal.me/liveWind");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
/**
        Button payPalButton = (Button) dialogView.findViewById(R.id.about_activity_donate_by_paypal);
        Button googlePayButton = (Button) dialogView.findViewById(R.id.about_activity_donate_by_google_pay);

        payPalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://www.paypal.me/liveWind");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        googlePayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPayment(view);
            }
        });
 */
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        ((Button)alertDialog.findViewById(android.R.id.button1)).setBackgroundResource(livewind.example.andro.liveWind.R.drawable.custom_button);
    }
    /**
     * Determine the viewer's ability to pay with a payment method supported by your app and display a
     * Google Pay payment button.
     *
     * @see <a href=
     *     "https://developers.google.com/android/reference/com/google/android/gms/wallet/PaymentsClient.html#isReadyToPay(com.google.android.gms.wallet.IsReadyToPayRequest)">PaymentsClient#IsReadyToPay</a>
     */
    private void possiblyShowGooglePayButton() {
        final Optional<JSONObject> isReadyToPayJson = GooglePay.getIsReadyToPayRequest();
        if (!isReadyToPayJson.isPresent()) {
            return;
        }
        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.get().toString());
        if (request == null) {
            return;
        }

        // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
        // OnCompleteListener to be triggered when the result of the call is known.
        Task<Boolean> task = mPaymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(
                new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(Task<Boolean> task) {
                        try {
                            boolean result = task.getResult(ApiException.class);
                            setGooglePayAvailable(result);
                        } catch (ApiException exception) {
                            // Process error
                            Log.w("isReadyToPay failed", exception);
                        }
                    }
                });
    }

    /**
     * If isReadyToPay returned {@code true}, show the button and hide the "checking" text. Otherwise,
     * notify the user that Google Pay is not available. Please adjust to fit in with your current
     * user flow. You are not required to explicitly let the user know if isReadyToPay returns {@code
     * false}.
     *
     * @param available isReadyToPay API response.
     */
    private void setGooglePayAvailable(boolean available) {
        if (available) {
           // mGooglePayStatusText.setVisibility(View.GONE);
           // mGooglePayButton.setVisibility(View.VISIBLE);
        } else {
           // mGooglePayStatusText.setText(R.string.googlepay_status_unavailable);
        }
    }

        // This method is called when the Pay with Google button is clicked.
        public void requestPayment(View view) {
            // Disables the button to prevent multiple clicks.
            mDonateUsButton.setClickable(false);

            // The price provided to the API should include taxes and shipping.
            // This price is not displayed to the user.
            String price = GooglePay.microsToString(100 + 23);

            // TransactionInfo transaction = PaymentsUtil.createTransaction(price);
            Optional<JSONObject> paymentDataRequestJson = GooglePay.getPaymentDataRequest(price);
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
                        mPaymentsClient.loadPaymentData(request), this, LOAD_PAYMENT_DATA_REQUEST_CODE);
            }
        }

    /**
     * Handle a resolved activity from the Google Pay payment sheet.
     *
     * @param requestCode Request code originally supplied to AutoResolveHelper in requestPayment().
     * @param resultCode Result code returned by the Google Pay API.
     * @param data Intent from the Google Pay API containing payment or error data.
     * @see <a href="https://developer.android.com/training/basics/intents/result">Getting a result
     *     from an Activity</a>
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // value passed in AutoResolveHelper
            case LOAD_PAYMENT_DATA_REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        handlePaymentSuccess(paymentData);
                        break;
                    case Activity.RESULT_CANCELED:
                        // Nothing to here normally - the user simply cancelled without selecting a
                        // payment method.
                        break;
                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        handleError(status.getStatusCode());
                        break;
                    default:
                        // Do nothing.
                }

                // Re-enables the Google Pay payment button.
                mDonateUsButton.setClickable(true);
                break;
        }
    }

    /**
     * PaymentData response object contains the payment information, as well as any additional
     * requested information, such as billing and shipping address.
     *
     * @param paymentData A response object returned by Google after a payer approves payment.
     * @see <a
     *     href="https://developers.google.com/pay/api/android/reference/object#PaymentData">Payment
     *     Data</a>
     */
    private void handlePaymentSuccess(PaymentData paymentData) {
        String paymentInformation = paymentData.toJson();

        // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
        if (paymentInformation == null) {
            return;
        }
        JSONObject paymentMethodData;

        try {
            paymentMethodData = new JSONObject(paymentInformation).getJSONObject("paymentMethodData");
            // If the gateway is set to "example", no payment information is returned - instead, the
            // token will only consist of "examplePaymentMethodToken".
            if (paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("type")
                    .equals("PAYMENT_GATEWAY")
                    && paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("token")
                    .equals("examplePaymentMethodToken")) {
                AlertDialog alertDialog =
                        new AlertDialog.Builder(this)
                                .setTitle("Warning")
                                .setMessage(
                                        "Gateway name set to \"example\" - please modify "
                                                + "Constants.java and replace it with your own gateway.")
                                .setPositiveButton("OK", null)
                                .create();
                alertDialog.show();
            }

            String billingName =
                    paymentMethodData.getJSONObject("info").getJSONObject("billingAddress").getString("name");
            Log.d("BillingName", billingName);
            Toast.makeText(this,"Successfully received payment data for ...!", Toast.LENGTH_LONG)
                    .show();

            // Logging token string.
            Log.d("GooglePaymentToken", paymentMethodData.getJSONObject("tokenizationData").getString("token"));
        } catch (JSONException e) {
            Log.e("handlePaymentSuccess", "Error: " + e.toString());
            return;
        }
    }

    /**
     * At this stage, the user has already seen a popup informing them an error occurred. Normally,
     * only logging is required.
     *
     * @param statusCode will hold the value of any constant from CommonStatusCode or one of the
     *     WalletConstants.ERROR_CODE_* constants.
     * @see <a
     *     href="https://developers.google.com/android/reference/com/google/android/gms/wallet/WalletConstants#constant-summary">
     *     Wallet Constants Library</a>
     */
    private void handleError(int statusCode) {
        Log.w("loadPaymentData failed", String.format("Error code: %d", statusCode));
    }

    private static void contactWithDevelopers(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.contact_our_email)});
            intent.putExtra(Intent.EXTRA_SUBJECT, "[SurfAdvisor]");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, context.getString(livewind.example.andro.liveWind.R.string.toast_contact_error), Toast.LENGTH_LONG).show();
        }
    }
}
