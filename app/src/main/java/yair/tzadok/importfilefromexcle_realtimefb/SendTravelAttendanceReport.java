package yair.tzadok.importfilefromexcle_realtimefb;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SendTravelAttendanceReport {

    private Context context;
    private ArrayList<Traveler> travelerList;
    private String fileName;

    public SendTravelAttendanceReport(Context context, ArrayList<Traveler> travelerList, String fileName) {
        this.context = context;
        this.travelerList = travelerList;
        this.fileName = fileName;
    }

    public void sendTravelAttendanceReportEmail() {
        // Get the current date and time

       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());

        // Create the email body content with HTML formatting
        StringBuilder reportBody = new StringBuilder();
        String[] reportRecipients = {"example@example.com"};
        String reportSubject = "Trip Attendance Report - " + currentDateAndTime;

        reportBody.append("<html><body>");
        reportBody.append("<h2>Trip Attendance Report</h2>");
        reportBody.append("<p>Generated on: ").append(currentDateAndTime).append("</p>");
        reportBody.append("<ul>");


      //  Toast.makeText(context, ""+travelerList.get(0).toString(), Toast.LENGTH_LONG).show();

        for (Traveler traveler : travelerList) {
            String attendanceState = traveler.getTravelerAttendanceState();
            String color = attendanceState.equalsIgnoreCase("absent") ? "red" : "green";
            reportBody.append("<li>")
                    .append(traveler.getFirstName()).append(" ")
                    .append(traveler.getLastName()).append(" - ")
                    .append("<span style='color:").append(color).append(";'>")
                    .append(attendanceState)
                    .append("</span>")
                    .append(" (ID: ").append(traveler.getTravelerId()).append(")</li>");
        }

        reportBody.append("</ul>");
        reportBody.append("</body></html>");

        // Locate the Excel file in the Downloads directory
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File excelFile = new File(downloadsDir, fileName);

        Intent reportEmailIntent = new Intent(Intent.ACTION_SEND);
        reportEmailIntent.setType("text/html");  // Set the type to HTML
        reportEmailIntent.putExtra(Intent.EXTRA_EMAIL, reportRecipients);
        reportEmailIntent.putExtra(Intent.EXTRA_SUBJECT, reportSubject);
        reportEmailIntent.putExtra(Intent.EXTRA_TEXT, android.text.Html.fromHtml(reportBody.toString()));  // Convert the HTML string

        if (excelFile.exists()) {
            Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", excelFile);
            reportEmailIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        } else {
            Toast.makeText(context, "File not found in Downloads directory.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            context.startActivity(Intent.createChooser(reportEmailIntent, "Choose an email client:"));
        } catch (Exception e) {
            Toast.makeText(context, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
