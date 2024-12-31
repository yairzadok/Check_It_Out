package yair.tzadok.importfilefromexcle_realtimefb;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.OutputStream;
import java.util.ArrayList;

public class ExcelFileHelper {

    private Context context;

    public ExcelFileHelper(Context context) {
        this.context = context;
    }

    public void createExcelFile(ArrayList<Traveler> travelerList) {
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet hssfSheet = hssfWorkbook.createSheet("TravelerAttendance");

        // Create the header row
        HSSFRow headerRow = hssfSheet.createRow(0);
        headerRow.createCell(0).setCellValue("First Name");
        headerRow.createCell(1).setCellValue("Last Name");
        headerRow.createCell(2).setCellValue("Attendance State");

        // Populate the sheet with data from the traveler list
        for (int i = 0; i < travelerList.size(); i++) {
            HSSFRow hssfRow = hssfSheet.createRow(i + 1); // Start from row 1, because row 0 is the header

            Traveler traveler = travelerList.get(i);
            hssfRow.createCell(0).setCellValue(traveler.getFirstName());
            hssfRow.createCell(1).setCellValue(traveler.getLastName());
            hssfRow.createCell(2).setCellValue(traveler.getTravelerAttendanceState());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveWorkBook(hssfWorkbook);
        } else {
            saveWorkBookLegacy(hssfWorkbook);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void saveWorkBook(HSSFWorkbook hssfWorkbook) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "TravelerAttendance.xls");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.ms-excel");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            Uri uri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);

            if (uri != null) {
                OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                hssfWorkbook.write(outputStream);
                outputStream.close();
                hssfWorkbook.close();
                Toast.makeText(context, "File Created Successfully in Downloads", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Failed to create file", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(context, "File Creation Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void saveWorkBookLegacy(HSSFWorkbook hssfWorkbook) {
        // Check permission for WRITE_EXTERNAL_STORAGE for Android < 10
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return;
        }

        try {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            String fileName = "TravelerAttendance.xls";
            java.io.File file = new java.io.File(path, fileName);
            OutputStream outputStream = new java.io.FileOutputStream(file);
            hssfWorkbook.write(outputStream);
            outputStream.close();
            hssfWorkbook.close();
            Toast.makeText(context, "File Created Successfully in Downloads", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, "File Creation Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
