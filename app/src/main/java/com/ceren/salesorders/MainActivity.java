package com.ceren.salesorders;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseUser mUser;
    private FloatingActionButton fabReload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fabReload = findViewById(R.id.fabReload);
        /*fabReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        fabReload.setVisibility(View.GONE);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navigationHeaderView = navigationView.getHeaderView(0);
        TextView nav_header_Name = navigationHeaderView.findViewById(R.id.nav_header_Name);
        TextView nav_header_Email = navigationHeaderView.findViewById(R.id.nav_header_Email);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            nav_header_Name.setText(mUser.getDisplayName());
            nav_header_Email.setText(mUser.getEmail());

            HomeFragment fragmentHome = HomeFragment.newInstance(mUser);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_content, fragmentHome);
            ft.commit();
            getSupportActionBar().setTitle("Home");
            fabReload.setVisibility(View.VISIBLE);

            navigationView.setCheckedItem(R.id.nav_home);
        }
        else {
            finish();
            Intent LoginIntent = new Intent(this, LoginActivity.class);
            startActivity(LoginIntent);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the HomeFragment/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        fabReload.setVisibility(View.GONE);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser == null) {
            finish();
            Intent LoginIntent = new Intent(this, LoginActivity.class);
            startActivity(LoginIntent);
        }

        if (id == R.id.nav_home) {
            HomeFragment fragmentHome = HomeFragment.newInstance(mUser);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_content, fragmentHome);
            ft.commit();
            getSupportActionBar().setTitle("Home");
            fabReload.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_trans_history) {
            TransactionHistoryFragment fragmentTransHistory = TransactionHistoryFragment.newInstance();
            //TableViewFragment fragmentTransHistory = TableViewFragment.newInstance();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_content, fragmentTransHistory);
            ft.commit();
            getSupportActionBar().setTitle("Transaction History");
            /*finish();
            Intent TVIntent = new Intent(this, TableViewActivity.class);
            startActivity(TVIntent);*/
        } else if (id == R.id.nav_profile) {
            ProfileFragment fragmentProfile = ProfileFragment.newInstance(mUser);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_content, fragmentProfile);
            ft.commit();
            getSupportActionBar().setTitle("Profile");
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();

            Toast.makeText(MainActivity.this, "Logged out successfully",
                    Toast.LENGTH_SHORT).show();

            finish();
            Intent LoginIntent = new Intent(this, LoginActivity.class);
            startActivity(LoginIntent);
        } else if (id == R.id.nav_exit) {
            FirebaseAuth.getInstance().signOut();

            Toast.makeText(MainActivity.this, "Logged out successfully",
                    Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            System.exit(0);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
