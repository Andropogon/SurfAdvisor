package livewind.example.andro.liveWind.Notifications;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import livewind.example.andro.liveWind.R;

public class NewContentNotificationDialog {

    public static void showNewContentNotificationDialog(final Activity context, final NewContentNotification newContentNotification) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.NewContentDialogTheme);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_new_content_notification, null);
        //Init views
        TextView titleTextView = dialogView.findViewById(R.id.new_content_notification_title);
        TextView descriptionTextView = dialogView.findViewById(R.id.new_content_notification_description);
        titleTextView.setText(newContentNotification.getTitle().toString().trim());
        descriptionTextView.setText(newContentNotification.getDescription().toString().trim());

        //Set ok button
        builder.setView(dialogView)
                .setNeutralButton(R.string.dialog_dissmis, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        //Set action button
        builder.setView(dialogView)
                .setPositiveButton(newContentNotification.getActionTitle(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO check that it is correct
                        Uri uri = Uri.parse(newContentNotification.getActionLink());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        ((Button)alertDialog.findViewById(android.R.id.button1)).setBackgroundResource(R.drawable.custom_aprove_button);
        ((Button)alertDialog.findViewById(android.R.id.button3)).setBackgroundResource(R.drawable.custom_button);
    }
}
