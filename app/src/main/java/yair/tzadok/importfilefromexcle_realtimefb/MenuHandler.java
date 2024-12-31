package yair.tzadok.importfilefromexcle_realtimefb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.method.LinkMovementMethod;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MenuHandler {

    private Context context;

    public MenuHandler(Context context) {
        this.context = context;
    }

    @SuppressLint("RestrictedApi")
    public boolean onCreateOptionsMenu(Menu menu, int menuResId) {
        ((AppCompatActivity) context).getMenuInflater().inflate(menuResId, menu);
        if (menu instanceof MenuBuilder) {
            MenuBuilder mb = (MenuBuilder) menu;
            mb.setOptionalIconsVisible(true);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item, int helpResId) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_about) {
            showAboutDialog();
            return true;
        } else if (itemId == R.id.action_help) {
            showHelpDialog();
            return true;
        } else if (itemId == R.id.action_login) {
            Toast.makeText(context, "You clicked Login", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void showAboutDialog() {
        SpannableString spannableText = new SpannableString("About Me\n\nDr. Yair Zadok\nEmail: yair6655@gmail.com\nPhone: +972-52-8876688");

        // Make email clickable
        int emailStart = spannableText.toString().indexOf("yair6655@gmail.com");
        int emailEnd = emailStart + "yair6655@gmail.com".length();
        spannableText.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:yair6655@gmail.com"));
                context.startActivity(Intent.createChooser(emailIntent, "Send Email"));
            }
        }, emailStart, emailEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make phone number clickable
        int phoneStart = spannableText.toString().indexOf("+972-52-8876688");
        int phoneEnd = phoneStart + "+972-52-8876688".length();
        spannableText.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:+972528876688"));
                context.startActivity(callIntent);
            }
        }, phoneStart, phoneEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Create and show the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("About")
                .setMessage(spannableText)
                .setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Enable clickable spans
        ((android.widget.TextView) dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void showHelpDialog() {
        // Create a WebView to display the HTML file
        WebView webView = new WebView(context);
        webView.loadUrl("file:///android_res/raw/help.html");

        new AlertDialog.Builder(context)
                .setTitle("Help")
                .setView(webView)
                .setPositiveButton("OK", null)
                .show();
    }

    private String readRawTextFile(int resId) {
        InputStream inputStream = context.getResources().openRawResource(resId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
