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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileNotFoundException;
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
    private DatabaseReference mDatabaseReference;
    private ExcelFileHelper excelFileHelper;
    private File excelFile;

    private AdView adView;
    private AdView adView1;

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

        excelFileHelper = new ExcelFileHelper(this);

        travelerAttendanceRV.setLayoutManager(new LinearLayoutManager(this));
        excelTravelersAttendanceInfo = new ArrayList<>();
        excelTravelerAttendanceAdapter = new Adapter_ImportTravelersFromExcel(this, excelTravelersAttendanceInfo);
        excelTravelerAttendanceAdapter.attachSwipeToRecyclerView(travelerAttendanceRV);
        travelerAttendanceRV.setAdapter(excelTravelerAttendanceAdapter);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

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

                try (InputStream inputStream = getContentResolver().openInputStream(selectedTravelerExcelFileUri)) {
                    Workbook workbook = WorkbookFactory.create(inputStream);
                    Sheet sheet = workbook.getSheetAt(0);

                    HashMap<String, String> existingAttendanceStates = new HashMap<>();
                    for (Traveler traveler : excelTravelersAttendanceInfo) {
                        existingAttendanceStates.put(traveler.getTravelerId(), traveler.getTravelerAttendanceState());
                    }

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

                            // Preserve existing attendance state
                            String attendanceState = existingAttendanceStates.getOrDefault(traveler.getTravelerId(), "false");
                            traveler.setTravelerAttendanceState(attendanceState);

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
            // Fetch traveler from Firebase if not found locally
            mDatabaseReference.child("Travelers").child(travelerId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Traveler traveler = snapshot.getValue(Traveler.class);
                    if (traveler != null) {
                        traveler.setTravelerAttendanceState("true");
                        excelTravelersAttendanceInfo.add(traveler);
                    }
                    excelTravelerAttendanceAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Activity_List_Of_Travelers_checkIn.this,
                            "Failed to fetch traveler: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            excelTravelerAttendanceAdapter.notifyDataSetChanged();
        }

        // Update Firebase
        mDatabaseReference.child("Travelers").child(travelerId).child("travelerAttendanceState").setValue("true");
    }


    private void synchronizeDataFromFirebase() {
        mDatabaseReference.child("Travelers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                excelTravelersAttendanceInfo.clear(); // Clear existing data
                for (DataSnapshot travelerSnapshot : snapshot.getChildren()) {
                    Traveler traveler = travelerSnapshot.getValue(Traveler.class);
                    if (traveler != null) {
                        excelTravelersAttendanceInfo.add(traveler);
                    }
                }
                excelTravelerAttendanceAdapter.notifyDataSetChanged(); // Notify adapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Activity_List_Of_Travelers_checkIn.this,
                        "Failed to fetch data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean containsTraveler(String travelerId) {
        for (Traveler traveler : excelTravelersAttendanceInfo) {
            if (traveler.getTravelerId().equals(travelerId)) {
                return true;
            }
        }
        return false;
    }


    /* Firebase Methods */
    public void addTravelerToFirebase() {
        mDatabaseReference.child("Travelers").removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (Traveler traveler : excelTravelersAttendanceInfo) {
                    mDatabaseReference.child("Travelers").child(traveler.getTravelerId())
                            .setValue(traveler);
                }
            }
        });
    }

    /* Ad Methods */
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

    // Call synchronizeDataFromFirebase() in onResume
    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null) adView.resume();
        if (adView1 != null) adView1.resume();

        // Synchronize with Firebase to refresh attendance states
        synchronizeDataFromFirebase();
    }
}

