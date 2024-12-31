package yair.tzadok.importfilefromexcle_realtimefb;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class Activity_List_Of_Travelers_checkIn extends AppCompatActivity {

    public static final int PICK_EXCEL_FILE_REQUEST_CODE = 100;
    private static final int SCAN_QR_CODE_REQUEST_CODE = 101;

    private ArrayList<Traveler> excelTravelersAttendanceInfo;
    private Adapter_ImportTravelersFromExcel excelTravelerAttendanceAdapter;

    private File excelFile;

    private AdView adView;
    private AdView adView1;

    // Local map to store attendance data
    private HashMap<String, Traveler> travelerAttendanceMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Set the activity to full-screen mode
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );

        setContentView(R.layout.activity_list_of_travelers_check_in);

        // Initialize AdMob
        MobileAds.initialize(this, initializationStatus -> {});
        adView = findViewById(R.id.adView);
        adView1 = findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView1.loadAd(adRequest);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d("AdMob", "Ad loaded successfully.");
            }
        });

        Button importTravelerTravelerExcelBtn = findViewById(R.id.importTravelerTravelerExcelBtn);
        RecyclerView travelerAttendanceRV = findViewById(R.id.travelerAttendanceRV);
        Button sendTravelerAttendanceReportBtn = findViewById(R.id.sendTravelerAttendanceReportBtn);
        Button btnScan = findViewById(R.id.btnScan);
        Button createExcelFileBtn = findViewById(R.id.createExcelFileBtn);

        travelerAttendanceRV.setLayoutManager(new LinearLayoutManager(this));
        excelTravelersAttendanceInfo = new ArrayList<>();
        excelTravelerAttendanceAdapter = new Adapter_ImportTravelersFromExcel(this, excelTravelersAttendanceInfo);
        excelTravelerAttendanceAdapter.attachSwipeToRecyclerView(travelerAttendanceRV);
        travelerAttendanceRV.setAdapter(excelTravelerAttendanceAdapter);

        // Initialize local map to store attendance
        travelerAttendanceMap = new HashMap<>();

        btnScan.setOnClickListener(view -> {
            Intent intent = new Intent(Activity_List_Of_Travelers_checkIn.this, Activity_QRScanner.class);
            startActivityForResult(intent, SCAN_QR_CODE_REQUEST_CODE);
        });

        importTravelerTravelerExcelBtn.setOnClickListener(view -> openFilePicker());

        sendTravelerAttendanceReportBtn.setOnClickListener(view -> {
            if (excelFile != null) {
                SendTravelAttendanceReport report = new SendTravelAttendanceReport(
                        Activity_List_Of_Travelers_checkIn.this, excelTravelersAttendanceInfo, excelFile.getName());
                report.sendTravelAttendanceReportEmail();
            } else {
                Toast.makeText(Activity_List_Of_Travelers_checkIn.this, "Please create the Excel file first.", Toast.LENGTH_SHORT).show();
            }
        });

        createExcelFileBtn.setOnClickListener(v -> {
            ExcelFileHelper excelFileHelper = new ExcelFileHelper(this);
            excelFileHelper.createExcelFile(excelTravelersAttendanceInfo);
            excelFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "TravelerAttendance.xls");
            Toast.makeText(Activity_List_Of_Travelers_checkIn.this, "Excel file created at " + excelFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.ms-excel");
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        startActivityForResult(Intent.createChooser(intent, "Select an Excel File"), PICK_EXCEL_FILE_REQUEST_CODE);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_EXCEL_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedTravelerExcelFileUri = data.getData();

                if (selectedTravelerExcelFileUri != null) {
                    try (InputStream inputStream = getContentResolver().openInputStream(selectedTravelerExcelFileUri)) {
                        Workbook workbook = WorkbookFactory.create(Objects.requireNonNull(inputStream));
                        Sheet sheet = workbook.getSheetAt(0);

                        ArrayList<Traveler> updatedTravelersList = new ArrayList<>();
                        for (Row row : sheet) {
                            Traveler traveler = new Traveler();
                            Cell firstNameCell = row.getCell(0);
                            Cell lastNameCell = row.getCell(1);
                            Cell travelerIdCell = row.getCell(2);
                            Cell travelerPhoneNumberCell = row.getCell(3);

                            if (firstNameCell != null && lastNameCell != null && travelerIdCell != null && travelerPhoneNumberCell != null) {
                                traveler.setFirstName(firstNameCell.getStringCellValue());
                                traveler.setLastName(lastNameCell.getStringCellValue());
                                traveler.setPhoneNumber(travelerPhoneNumberCell.getStringCellValue());
                                traveler.setTravelerId(String.format(Locale.getDefault(), "%.0f", travelerIdCell.getNumericCellValue()));

                                traveler.setTravelerAttendanceState("false");

                                updatedTravelersList.add(traveler);
                            }
                        }
                        workbook.close();

                        excelTravelersAttendanceInfo.clear();
                        excelTravelersAttendanceInfo.addAll(updatedTravelersList);
                        excelTravelerAttendanceAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error: " + e, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else if (requestCode == SCAN_QR_CODE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                String scannedTravelerId = data.getStringExtra("qr_code_value");
                markTravelerAsPresent(scannedTravelerId);
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private void markTravelerAsPresent(String travelerId) {
        boolean found = false;

        for (Traveler traveler : excelTravelersAttendanceInfo) {
            if (traveler.getTravelerId().equals(travelerId)) {
                traveler.setTravelerAttendanceState("true");
                found = true;
                break;
            }
        }

        if (!found) {
            // Traveler not found, you may want to handle this scenario
            Traveler newTraveler = new Traveler();
            newTraveler.setTravelerId(travelerId);
            newTraveler.setTravelerAttendanceState("true");
            excelTravelersAttendanceInfo.add(newTraveler);
        }

        excelTravelerAttendanceAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        if (adView != null) adView.destroy();
        if (adView1 != null) adView1.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (adView != null) adView.pause();
        if (adView1 != null) adView1.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null) adView.resume();
        if (adView1 != null) adView1.resume();
    }
}
