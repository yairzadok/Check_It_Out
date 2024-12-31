package yair.tzadok.importfilefromexcle_realtimefb;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Activity_Main extends AppCompatActivity {

    private MenuHandler menuHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        menuHandler = new MenuHandler(this);

        findViewById(R.id.startBtb).setOnClickListener(v -> {
            Intent intent = new Intent(Activity_Main.this, Activity_List_Of_Travelers_checkIn.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return menuHandler.onCreateOptionsMenu(menu, R.menu.menu_travelers);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return menuHandler.onOptionsItemSelected(item, R.raw.help); // Pass raw resource ID for help text
    }
}
