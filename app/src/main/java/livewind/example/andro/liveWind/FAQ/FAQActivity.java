package livewind.example.andro.liveWind.FAQ;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import livewind.example.andro.liveWind.R;

import static livewind.example.andro.liveWind.Helpers.SocialHelper.getFacebookPageURL;

public class FAQActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(livewind.example.andro.liveWind.R.layout.activity_faq);

        TextView contactEmailTextView = findViewById(R.id.faq_contact);
        TextView contactFacebookTextView = findViewById(R.id.faq_facebook);
        contactEmailTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                contactWithDevelopers(getApplicationContext());
            }
        });
        contactFacebookTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                String facebookUrl = getFacebookPageURL(getApplicationContext());
                facebookIntent.setData(Uri.parse(facebookUrl));
                facebookIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(facebookIntent);
            }
        });

    }

    private static void contactWithDevelopers(Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"jgjozkowy@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "[SurfAdvisor FAQ] ");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
    }
}
