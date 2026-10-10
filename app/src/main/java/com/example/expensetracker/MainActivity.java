package com.example.expensetracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.Typeface;
import android.os.Bundle;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final int INDIGO = Color.rgb(59, 47, 209);
    private static final int INDIGO_TINT = Color.rgb(239, 239, 251);
    private static final int INK = Color.rgb(11, 12, 15);
    private static final int BODY = Color.rgb(92, 97, 107);
    private static final int BORDER = Color.rgb(223, 226, 230);
    private static final int GREEN = Color.rgb(10, 155, 97);
    private static final int RED = Color.rgb(201, 42, 56);
    private static final int NAVY = Color.rgb(0, 35, 47);
    private static final int NEON_GREEN = Color.rgb(0, 232, 104);
    private static final int PLANT_TEAL = Color.rgb(12, 137, 140);
    private static final int PLANT_MINT = Color.rgb(157, 222, 211);
    private static final int PLANT_SHEET = Color.rgb(244, 249, 248);

    private final List<Expense> expenses = new ArrayList<>();
    private SharedPreferences preferences;
    private LinearLayout content;
    private int selectedTab = 0;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        preferences = getSharedPreferences("expense_tracker", MODE_PRIVATE);
        loadExpenses();
        if (preferences.getBoolean("logged_in", false)) showShell(); else showLoginReference();
    }

    private void seedExpenses() {
        expenses.add(new Expense("Apartment rent", "Housing", "₱18,000.00", "Today · Due Oct 15", "Due"));
        expenses.add(new Expense("Electricity", "Utilities", "₱2,450.00", "Today · Due Oct 10", "Due"));
        expenses.add(new Expense("Credit card", "Subscriptions", "₱3,280.00", "Yesterday · Due Oct 8", "Overdue"));
        expenses.add(new Expense("Internet", "Utilities", "₱1,899.00", "Oct 5 · Due Oct 5", "Paid"));
    }

    private void loadExpenses() {
        int count = preferences.getInt("expense_count", 0);
        if (count == 0) { seedExpenses(); saveExpenses(); return; }
        for (int i = 0; i < count; i++) {
            expenses.add(new Expense(
                    preferences.getString("expense_" + i + "_name", "Expense"),
                    preferences.getString("expense_" + i + "_category", "Other"),
                    preferences.getString("expense_" + i + "_amount", "₱0.00"),
                    preferences.getString("expense_" + i + "_date", "Today"),
                    preferences.getString("expense_" + i + "_status", "Due")));
        }
    }

    private void saveExpenses() {
        SharedPreferences.Editor editor = preferences.edit().putInt("expense_count", expenses.size());
        for (int i = 0; i < expenses.size(); i++) {
            Expense e = expenses.get(i);
            editor.putString("expense_" + i + "_name", e.name);
            editor.putString("expense_" + i + "_category", e.category);
            editor.putString("expense_" + i + "_amount", e.amount);
            editor.putString("expense_" + i + "_date", e.date);
            editor.putString("expense_" + i + "_status", e.status);
        }
        editor.apply();
    }

    private void showLogin() {
        getWindow().setStatusBarColor(PLANT_TEAL);
        getWindow().setNavigationBarColor(PLANT_SHEET);
        getWindow().getDecorView().setSystemUiVisibility(0);

        LinearLayout root = column(PLANT_MINT, 0);
        root.setPadding(0, 12, 0, 0);

        LinearLayout hero = column(PLANT_TEAL, 8);
        hero.setPadding(20, 18, 20, 12);
        TextView leafTop = label("◜  ◝", 42, Color.rgb(104, 202, 174), false);
        leafTop.setGravity(Gravity.RIGHT);
        hero.addView(leafTop, new LinearLayout.LayoutParams(-1, 42));
        hero.addView(label("Hello!", 77, Color.WHITE, true));
        hero.addView(label("Welcome to Expense Tracker", 24, Color.WHITE, false));
        TextView plant = label("❯\n❮", 92, Color.rgb(105, 205, 167), false);
        plant.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        hero.addView(plant, new LinearLayout.LayoutParams(-1, 0, 1));
        root.addView(hero, new LinearLayout.LayoutParams(-1, 0, 0.40f));

        LinearLayout sheet = column(PLANT_SHEET, 10);
        sheet.setPadding(26, 24, 26, 16);
        GradientDrawable sheetBackground = new GradientDrawable();
        sheetBackground.setColor(PLANT_SHEET);
        sheetBackground.setCornerRadii(new float[]{24, 24, 24, 24, 0, 0, 0, 0});
        sheet.setBackground(sheetBackground);
        sheet.addView(label("Login", 35, PLANT_TEAL, true));
        sheet.addView(space(22));

        EditText email = pillInput("✉  Email", false);
        email.setContentDescription("Email address");
        sheet.addView(email, new LinearLayout.LayoutParams(-1, 50));
        sheet.addView(space(18));
        EditText password = pillInput("♙  Password", true);
        password.setContentDescription("Password");
        sheet.addView(password, new LinearLayout.LayoutParams(-1, 50));
        TextView forgot = label("Forgot Password", 12, PLANT_TEAL, false);
        forgot.setGravity(Gravity.RIGHT);
        sheet.addView(forgot, new LinearLayout.LayoutParams(-1, 34));
        sheet.addView(space(12));

        Button login = button("Login", PLANT_TEAL, Color.WHITE);
        login.setTextSize(30);
        sheet.addView(login, new LinearLayout.LayoutParams(-1, 56));
        login.setOnClickListener(v -> {
            if (email.getText().toString().trim().isEmpty()) email.setError("Enter your email");
            else if (password.getText().toString().trim().isEmpty()) password.setError("Enter your password");
            else { preferences.edit().putBoolean("logged_in", true).apply(); showShell(); }
        });
        sheet.addView(space(18));
        LinearLayout divider = row();
        divider.setGravity(Gravity.CENTER);
        divider.addView(label("────────  or sign with  ────────", 15, Color.LTGRAY, false));
        sheet.addView(divider, new LinearLayout.LayoutParams(-1, 30));
        sheet.addView(space(10));
        LinearLayout social = row();
        social.setGravity(Gravity.CENTER);
        for (String provider : new String[]{"●", "G", "●"}) {
            Button socialButton = button(provider, Color.WHITE, provider.equals("G") ? Color.rgb(219, 68, 55) : PLANT_TEAL);
            socialButton.setTextSize(20);
            social.addView(socialButton, new LinearLayout.LayoutParams(52, 48));
            social.addView(spaceHorizontal(8));
        }
        sheet.addView(social);
        sheet.addView(space(18));
        LinearLayout signup = row();
        signup.setGravity(Gravity.CENTER);
        signup.addView(label("Don't have an account? ", 13, BODY, false));
        signup.addView(label("Sign Up", 13, PLANT_TEAL, true));
        sheet.addView(signup);
        root.addView(sheet, new LinearLayout.LayoutParams(-1, 0, 0.60f));
        setContentView(root);
    }

    private void showLoginReferenceOld() {
        final int teal = Color.rgb(8, 122, 128);
        final int darkTeal = Color.rgb(0, 55, 59);
        final int card = Color.rgb(255, 251, 252);
        final int field = Color.rgb(242, 239, 239);

        getWindow().setStatusBarColor(teal);
        getWindow().setNavigationBarColor(teal);
        getWindow().getDecorView().setSystemUiVisibility(0);

        LinearLayout root = column(teal, 0);
        root.setPadding(18, 2, 18, 0);
        root.setBackground(gradientBackground(Color.rgb(10, 137, 143), Color.rgb(0, 74, 80), 0));

        LinearLayout sheet = column(card, 0);
        sheet.setPadding(28, 42, 28, 24);
        sheet.setBackground(roundedBackground(card, 34));
        sheet.setElevation(2);

        LinearLayout logo = column(darkTeal, 0);
        logo.setGravity(Gravity.CENTER);
        logo.setBackground(roundedBackground(darkTeal, 10));
        TextView logoMark = label("*", 38, Color.WHITE, true);
        logoMark.setGravity(Gravity.CENTER);
        logo.addView(logoMark, new LinearLayout.LayoutParams(92, 92));
        LinearLayout logoWrap = row();
        logoWrap.setGravity(Gravity.CENTER);
        logoWrap.addView(logo, new LinearLayout.LayoutParams(92, 92));
        sheet.addView(logoWrap, new LinearLayout.LayoutParams(-1, 92));
        sheet.addView(space(22));

        TextView title = label("Expense Tracker", 32, Color.rgb(18, 16, 17), true);
        title.setGravity(Gravity.CENTER);
        sheet.addView(title, new LinearLayout.LayoutParams(-1, 54));
        TextView subtitle = label("To sign in to an account in the application,\nenter your email and password", 14, BODY, false);
        subtitle.setGravity(Gravity.CENTER);
        sheet.addView(subtitle, new LinearLayout.LayoutParams(-1, 52));
        sheet.addView(space(24));

        TextView emailLabel = label("Email", 16, Color.rgb(34, 30, 31), true);
        emailLabel.setGravity(Gravity.CENTER_VERTICAL);
        sheet.addView(emailLabel, new LinearLayout.LayoutParams(-1, 44));
        EditText email = loginField("", false, field);
        email.setContentDescription("Email address");
        sheet.addView(email, new LinearLayout.LayoutParams(-1, 82));
        sheet.addView(space(24));
        TextView passwordLabel = label("Password", 16, Color.rgb(34, 30, 31), true);
        passwordLabel.setGravity(Gravity.CENTER_VERTICAL);
        sheet.addView(passwordLabel, new LinearLayout.LayoutParams(-1, 44));
        EditText password = loginField("", true, field);
        password.setContentDescription("Password");
        sheet.addView(password, new LinearLayout.LayoutParams(-1, 82));
        sheet.addView(space(24));

        TextView forgot = label("Forgot password?", 18, Color.rgb(56, 90, 190), false);
        forgot.setGravity(Gravity.RIGHT);
        forgot.setPaintFlags(forgot.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
        forgot.setOnClickListener(v -> Toast.makeText(this, "Password recovery is not configured yet", Toast.LENGTH_SHORT).show());
        sheet.addView(forgot, new LinearLayout.LayoutParams(-1, 78));

        Button login = filledButton("Sign In  " + "\u2192", teal, Color.WHITE, 30, 22);
        sheet.addView(login, new LinearLayout.LayoutParams(-1, 86));
        login.setOnClickListener(v -> {
            if (email.getText().toString().trim().isEmpty()) email.setError("Enter your email");
            else if (password.getText().toString().trim().isEmpty()) password.setError("Enter your password");
            else { preferences.edit().putBoolean("logged_in", true).apply(); showShell(); }
        });
        sheet.addView(space(34));

        LinearLayout divider = row();
        divider.setGravity(Gravity.CENTER);
        divider.addView(dividerLine(), new LinearLayout.LayoutParams(0, 1, 1));
        TextView or = label("or", 16, Color.GRAY, false);
        or.setGravity(Gravity.CENTER);
        divider.addView(or, new LinearLayout.LayoutParams(52, 38));
        divider.addView(dividerLine(), new LinearLayout.LayoutParams(0, 1, 1));
        sheet.addView(divider, new LinearLayout.LayoutParams(-1, 38));
        sheet.addView(space(24));

        LinearLayout social = row();
        social.setGravity(Gravity.CENTER);
        social.addView(outlinedButton("G  Sign in with Google", Color.rgb(72, 72, 72), 15), new LinearLayout.LayoutParams(190, 70));
        social.addView(spaceHorizontal(14));
        social.addView(outlinedButton("●  Sign in with Apple", Color.rgb(25, 25, 25), 15), new LinearLayout.LayoutParams(190, 70));
        sheet.addView(social, new LinearLayout.LayoutParams(-1, 70));
        sheet.addView(space(28));

        LinearLayout signup = row();
        signup.setGravity(Gravity.CENTER);
        signup.addView(label("Don't have an account yet? ", 17, BODY, false));
        TextView create = label("Create Account", 17, Color.rgb(56, 90, 190), true);
        create.setPaintFlags(create.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
        create.setOnClickListener(v -> Toast.makeText(this, "Account creation is not configured yet", Toast.LENGTH_SHORT).show());
        signup.addView(create);
        sheet.addView(signup, new LinearLayout.LayoutParams(-1, 42));
        root.addView(sheet, new LinearLayout.LayoutParams(-1, 1200));

        TextView legal = label("By clicking \"Sign In\", I have read and agree\nwith the Term Sheet, Privacy Policy", 17, Color.WHITE, false);
        legal.setGravity(Gravity.CENTER);
        root.addView(legal, new LinearLayout.LayoutParams(-1, 110));
        setContentView(root);
    }

    private void showLoginReference() {
        final int teal = Color.rgb(8, 126, 132);
        final int deepTeal = Color.rgb(0, 57, 61);
        final int offWhite = Color.rgb(255, 251, 252);
        final int ink = Color.rgb(20, 18, 19);
        final int link = Color.rgb(57, 89, 184);

        getWindow().setStatusBarColor(teal);
        getWindow().setNavigationBarColor(teal);
        getWindow().getDecorView().setSystemUiVisibility(0);

        LinearLayout root = column(teal, 0);
        root.setGravity(Gravity.CENTER);
        root.setPadding(dp(9), 0, dp(9), 0);
        root.setBackground(gradientBackground(Color.rgb(12, 143, 149), Color.rgb(0, 72, 78), 0));

        LinearLayout card = column(offWhite, 0);
        card.setPadding(dp(22), dp(20), dp(22), dp(18));
        card.setBackground(roundedBackground(offWhite, dp(36)));
        card.setElevation(dp(2));

        ImageView logoImage = new ImageView(this);
        logoImage.setImageResource(R.drawable.expenses_logo);
        logoImage.setContentDescription("Expenses Tracker logo");
        logoImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        card.addView(logoImage, new LinearLayout.LayoutParams(-1, dp(72)));
        card.addView(space(dp(12)));

        TextView title = label("Expenses Tracker System", 18, ink, true);
        title.setGravity(Gravity.CENTER);
        card.addView(title, new LinearLayout.LayoutParams(-1, dp(32)));
        TextView subtitle = label("To sign in to an account in the application,\nenter your email and password", 12, BODY, false);
        subtitle.setGravity(Gravity.CENTER);
        card.addView(subtitle, new LinearLayout.LayoutParams(-1, dp(32)));
        card.addView(space(dp(12)));

        TextView emailLabel = label("Email", 14, ink, true);
        emailLabel.setGravity(Gravity.CENTER_VERTICAL);
        card.addView(emailLabel, new LinearLayout.LayoutParams(-1, dp(22)));
        EditText email = loginField("", false, Color.rgb(242, 240, 240));
        email.setContentDescription("Email address");
        card.addView(email, new LinearLayout.LayoutParams(-1, dp(48)));
        card.addView(space(dp(8)));

        TextView passwordLabel = label("Password", 14, ink, true);
        passwordLabel.setGravity(Gravity.CENTER_VERTICAL);
        card.addView(passwordLabel, new LinearLayout.LayoutParams(-1, dp(22)));
        EditText password = loginField("", true, Color.rgb(242, 240, 240));
        password.setContentDescription("Password");
        card.addView(password, new LinearLayout.LayoutParams(-1, dp(48)));
        card.addView(space(dp(7)));

        TextView forgot = label("Forgot password?", 14, link, false);
        forgot.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        forgot.setPaintFlags(forgot.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
        forgot.setOnClickListener(v -> Toast.makeText(this, "Password recovery is not configured yet", Toast.LENGTH_SHORT).show());
        card.addView(forgot, new LinearLayout.LayoutParams(-1, dp(31)));

        Button login = filledButton("Sign In  " + "\u2192", teal, Color.WHITE, 22, dp(16));
        card.addView(login, new LinearLayout.LayoutParams(-1, dp(48)));
        login.setOnClickListener(v -> {
            if (email.getText().toString().trim().isEmpty()) email.setError("Enter your email");
            else if (password.getText().toString().trim().isEmpty()) password.setError("Enter your password");
            else { preferences.edit().putBoolean("logged_in", true).apply(); showShell(); }
        });
        card.addView(space(dp(12)));

        LinearLayout divider = row();
        divider.setGravity(Gravity.CENTER_VERTICAL);
        divider.addView(dividerLine(), new LinearLayout.LayoutParams(0, dp(1), 1));
        TextView or = label("or", 10, Color.GRAY, false);
        or.setGravity(Gravity.CENTER);
        divider.addView(or, new LinearLayout.LayoutParams(dp(28), dp(20)));
        divider.addView(dividerLine(), new LinearLayout.LayoutParams(0, dp(1), 1));
        card.addView(divider, new LinearLayout.LayoutParams(-1, dp(20)));
        card.addView(space(dp(8)));

        LinearLayout social = row();
        social.setGravity(Gravity.CENTER);
        Button google = outlinedButton("G  Sign in with Google", Color.rgb(70, 70, 70), 10);
        Button apple = outlinedButton("●  Sign in with Apple", Color.rgb(25, 25, 25), 10);
        social.addView(google, new LinearLayout.LayoutParams(0, dp(36), 1));
        social.addView(spaceHorizontal(dp(8)));
        social.addView(apple, new LinearLayout.LayoutParams(0, dp(36), 1));
        card.addView(social, new LinearLayout.LayoutParams(-1, dp(36)));
        card.addView(space(dp(10)));

        LinearLayout signup = row();
        signup.setGravity(Gravity.CENTER);
        signup.addView(label("Don't have an account yet? ", 12, BODY, false));
        TextView create = label("Create Account", 12, link, true);
        create.setPaintFlags(create.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
        create.setOnClickListener(v -> Toast.makeText(this, "Account creation is not configured yet", Toast.LENGTH_SHORT).show());
        signup.addView(create);
        card.addView(signup, new LinearLayout.LayoutParams(-1, dp(24)));

        root.addView(card, new LinearLayout.LayoutParams(-1, -2));
        setContentView(root);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private GradientDrawable roundedBackground(int color, float radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        return drawable;
    }

    private GradientDrawable gradientBackground(int startColor, int endColor, float radius) {
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{startColor, endColor});
        drawable.setCornerRadius(radius);
        return drawable;
    }

    private EditText loginField(String hint, boolean password, int backgroundColor) {
        EditText e = input(hint, password);
        e.setBackground(gradientBackground(Color.rgb(247, 245, 245), backgroundColor, 24));
        e.setPadding(14, 0, password ? 42 : 14, 0);
        e.setElevation(2);
        if (password) {
            e.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_menu_view, 0);
            e.setOnTouchListener((view, event) -> {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP && event.getX() > e.getWidth() - 72) {
                    int cursor = e.getSelectionStart();
                    boolean visible = (e.getInputType() & InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) != 0;
                    e.setInputType(InputType.TYPE_CLASS_TEXT | (visible ? InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD));
                    e.setSelection(Math.max(0, cursor));
                    return true;
                }
                return false;
            });
        }
        return e;
    }

    private Button filledButton(String text, int backgroundColor, int textColor, int textSize, int radius) {
        Button b = button(text, backgroundColor, textColor);
        b.setTextSize(textSize);
        b.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        b.setBackground(gradientBackground(backgroundColor, Color.rgb(0, 57, 61), radius));
        return b;
    }

    private Button outlinedButton(String text, int textColor, int textSize) {
        Button b = button(text, Color.WHITE, textColor);
        b.setTextSize(textSize);
        b.setBackground(roundedBackground(Color.WHITE, 18));
        b.getBackground().setAlpha(255);
        b.setElevation(2);
        return b;
    }

    private View dividerLine() {
        View line = new View(this);
        line.setBackgroundColor(Color.rgb(120, 116, 117));
        return line;
    }

    private void showLoginGastos() {
        getWindow().setStatusBarColor(NAVY);
        getWindow().setNavigationBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(0);

        LinearLayout root = column(NAVY, 0);
        root.setPadding(20, 20, 20, 0);
        LinearLayout hero = column(NAVY, 12);
        hero.setPadding(20, 0, 20, 20);
        hero.addView(label("Gastos", 17, Color.WHITE, true));
        hero.addView(space(28));
        hero.addView(label("Every peso,\naccounted for.", 30, Color.WHITE, false));
        root.addView(hero, new LinearLayout.LayoutParams(-1, 0, 0.36f));

        LinearLayout sheet = column(Color.WHITE, 12);
        sheet.setPadding(20, 20, 20, 12);
        GradientDrawable sheetBackground = new GradientDrawable();
        sheetBackground.setColor(Color.WHITE);
        sheetBackground.setCornerRadii(new float[]{22, 22, 22, 22, 0, 0, 0, 0});
        sheet.setBackground(sheetBackground);
        sheet.setGravity(Gravity.TOP);
        sheet.addView(label("Log in", 20, INK, true));
        sheet.addView(space(14));
        sheet.addView(label("Email", 12, NAVY, true));
        EditText email = outlinedInput("ana.reyes@gmail.com", false);
        email.setContentDescription("Email address");
        sheet.addView(email);
        sheet.addView(space(10));
        sheet.addView(label("Password", 12, NAVY, true));
        EditText password = outlinedInput("At least 6 characters", true);
        password.setContentDescription("Password");
        sheet.addView(password);
        sheet.addView(space(64));

        Button login = button("Log in", NEON_GREEN, Color.BLACK);
        login.setTextSize(14);
        sheet.addView(login, new LinearLayout.LayoutParams(-1, 50));
        login.setOnClickListener(v -> {
            if (email.getText().toString().trim().isEmpty()) email.setError("Enter your email");
            else if (password.getText().toString().trim().isEmpty()) password.setError("Enter your password");
            else { preferences.edit().putBoolean("logged_in", true).apply(); showShell(); }
        });
        sheet.addView(space(10));
        Button google = button("Continue with Google", Color.WHITE, Color.BLACK);
        google.setTextSize(14);
        sheet.addView(google, new LinearLayout.LayoutParams(-1, 46));
        sheet.addView(space(10));
        sheet.addView(space(0), new LinearLayout.LayoutParams(1, 0, 1));
        LinearLayout secure = row();
        secure.setGravity(Gravity.CENTER);
        sheet.addView(secure);
        root.addView(sheet, new LinearLayout.LayoutParams(-1, 0, 0.64f));
        setContentView(root);
    }

    private void showLoginLegacy() {
        LinearLayout root = column(Color.WHITE, 30);
        root.setGravity(Gravity.CENTER_VERTICAL);
        root.setPadding(24, 24, 24, 24);

        root.addView(label("Expense Tracker", 18, INDIGO, true));
        root.addView(space(20));
        root.addView(label("Welcome back", 32, INK, true));
        root.addView(label("See what is due, where it goes, and what is left.", 16, BODY, false));
        root.addView(space(28));

        root.addView(label("EMAIL", 12, BODY, true));
        EditText email = input("you@example.com", false);
        email.setContentDescription("Email address");
        root.addView(email);
        root.addView(space(16));
        root.addView(label("PASSWORD", 12, BODY, true));
        EditText password = input("Enter your password", true);
        password.setContentDescription("Password");
        root.addView(password);
        root.addView(space(12));
        TextView forgot = label("Forgot password?", 14, INDIGO, false);
        root.addView(forgot);
        root.addView(space(12));

        Button login = button("Log in", INDIGO, Color.WHITE);
        root.addView(login, new LinearLayout.LayoutParams(-1, 56));
        login.setOnClickListener(v -> {
            if (email.getText().toString().trim().isEmpty()) {
                email.setError("Enter your email");
            } else if (password.getText().toString().trim().isEmpty()) {
                password.setError("Enter your password");
            } else {
                preferences.edit().putBoolean("logged_in", true).apply();
                showShell();
            }
        });
        root.addView(space(24));
        LinearLayout secure = row();
        secure.addView(label("🔒", 16, BODY, false));
        secure.addView(label("  Secure session", 14, BODY, false));
        root.addView(secure);
        setContentView(root);
    }

    private void showShell() {
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        LinearLayout root = column(PLANT_TEAL, 0);
        root.setPadding(12, 16, 12, 0);
        root.setBackground(gradientBackground(Color.rgb(12, 143, 149), Color.rgb(0, 72, 78), 0));

        LinearLayout surface = column(PLANT_SHEET, 0);
        surface.setBackground(roundedBackground(PLANT_SHEET, 26));
        surface.setElevation(3);
        LinearLayout top = row();
        top.setGravity(Gravity.CENTER_VERTICAL);
        top.setPadding(16, 12, 8, 8);
        TextView title = label("Dashboard", 22, Color.rgb(0, 55, 59), true);
        top.addView(title, new LinearLayout.LayoutParams(0, 56, 1));
        TextView profile = label("●", 22, INDIGO, true);
        profile.setGravity(Gravity.CENTER);
        profile.setContentDescription("Profile and logout");
        profile.setOnClickListener(v -> {
            preferences.edit().putBoolean("logged_in", false).apply();
            showLoginReference();
        });
        top.addView(profile, new LinearLayout.LayoutParams(56, 56));
        surface.addView(top);
        if (!hasNetwork()) {
            TextView offline = label("You're offline — changes are saved on this device.", 13, RED, true);
            offline.setPadding(16, 10, 16, 10);
            offline.setBackgroundColor(Color.rgb(255, 238, 240));
            surface.addView(offline);
        }

        content = column(PLANT_SHEET, 0);
        surface.addView(content, new LinearLayout.LayoutParams(-1, 0, 1));

        LinearLayout nav = row();
        nav.setPadding(8, 4, 8, 4);
        nav.setBackgroundColor(PLANT_SHEET);
        String[] tabs = {"▦\nDashboard", "▤\nExpenses", "▣\nBudget", "▥\nHistory"};
        for (int i = 0; i < tabs.length; i++) {
            final int index = i;
            TextView item = label(tabs[i], 12, i == 0 ? PLANT_TEAL : BODY, i == 0);
            item.setGravity(Gravity.CENTER);
            item.setOnClickListener(v -> { selectedTab = index; renderTab(); });
            nav.addView(item, new LinearLayout.LayoutParams(0, 64, 1));
        }
        surface.addView(nav);
        root.addView(surface, new LinearLayout.LayoutParams(-1, 0, 1));

        Button fab = filledButton("+", PLANT_TEAL, Color.WHITE, 26, 32);
        fab.setTextSize(26);
        fab.setContentDescription("Add expense");
        fab.setOnClickListener(v -> showNewExpenseDialog());
        addContentView(fab, new ViewGroup.LayoutParams(64, 64));
        fab.setX(getResources().getDisplayMetrics().widthPixels - 88);
        fab.setY(getResources().getDisplayMetrics().heightPixels - 150);
        setContentView(root);
        renderTab();
    }

    private void renderTab() {
        content.removeAllViews();
        if (selectedTab == 0) renderDashboard();
        else if (selectedTab == 1) renderExpenses();
        else if (selectedTab == 2) renderBudget();
        else if (selectedTab == 3) renderHistory();
        else renderPlaceholder(new String[]{"Dashboard", "Expenses", "Budget", "History"}[selectedTab]);
    }

    private void renderDashboard() {
        ScrollView scroll = new ScrollView(this);
        LinearLayout body = column(PLANT_SHEET, 16);
        body.setPadding(16, 8, 16, 80);
        body.addView(label("₱23,730.00 due today", 30, INK, false));
        body.addView(label("Next cutoff is in 4 days · October 15", 14, BODY, false));
        body.addView(space(24));
        body.addView(section("Due today", "3 payments"));
        LinearLayout dueCard = card();
        addDueRow(dueCard, "Electricity", "Utilities", "₱2,450.00", false);
        addDueRow(dueCard, "Apartment rent", "Housing", "₱18,000.00", false);
        addDueRow(dueCard, "Credit card", "Subscriptions · Overdue", "₱3,280.00", true);
        body.addView(dueCard);
        body.addView(space(20));
        body.addView(section("Where it goes", "This month"));
        LinearLayout breakdown = card();
        breakdown.addView(label("Housing                         ₱18,000", 14, BODY, false));
        breakdown.addView(label("Utilities                       ₱12,450", 14, BODY, false));
        breakdown.addView(label("Lifestyle                      ₱19,280", 14, BODY, false));
        breakdown.addView(space(10));
        breakdown.addView(label("36% Housing   25% Utilities   39% Lifestyle", 13, INDIGO, true));
        body.addView(breakdown);
        body.addView(space(20));
        body.addView(section("Groups & cutoffs", ""));
        LinearLayout cutoffs = row();
        cutoffs.addView(cutoff("1st cutoff", "₱31,450", "Paid"), new LinearLayout.LayoutParams(0, -2, 1));
        cutoffs.addView(spaceHorizontal(12));
        cutoffs.addView(cutoff("2nd cutoff", "₱18,280", "Upcoming"), new LinearLayout.LayoutParams(0, -2, 1));
        body.addView(cutoffs);
        body.addView(space(20));
        LinearLayout insight = card();
        insight.setBackgroundColor(Color.rgb(247, 247, 248));
        insight.addView(label("💡  A small win", 16, INDIGO, true));
        insight.addView(label("You are spending 12% less on subscriptions this month.", 14, BODY, false));
        body.addView(insight);
        scroll.addView(body);
        content.addView(scroll);
    }

    private void renderBudget() {
        ScrollView scroll = new ScrollView(this);
        LinearLayout body = column(Color.WHITE, 16);
        body.setPadding(16, 8, 16, 80);
        body.addView(label("You have ₱28,270 left after bills", 28, GREEN, true));
        body.addView(label("Your planned income covers all fixed expenses this month.", 14, BODY, false));
        body.addView(space(20));

        LinearLayout selector = row();
        for (String cutoff : new String[]{"1st cutoff", "2nd cutoff", "Month"}) {
            Button chip = button(cutoff, cutoff.equals("Month") ? INDIGO : Color.WHITE, cutoff.equals("Month") ? Color.WHITE : INK);
            selector.addView(chip, new LinearLayout.LayoutParams(0, 48, 1));
            chip.setOnClickListener(v -> Toast.makeText(this, cutoff + " selected", Toast.LENGTH_SHORT).show());
        }
        body.addView(selector);
        body.addView(space(20));
        body.addView(section("Money flow", "October 2026"));
        LinearLayout flow = card();
        addFlowRow(flow, "Income", "₱80,000", 100, INDIGO);
        addFlowRow(flow, "Fixed bills", "₱31,450", 39, RED);
        addFlowRow(flow, "Savings", "₱20,280", 25, GREEN);
        addFlowRow(flow, "Remaining", "₱28,270", 35, INDIGO);
        body.addView(flow);
        body.addView(space(20));
        body.addView(section("Budget by category", "Edit"));
        body.addView(budgetCategory("Housing", "₱24,000", "₱18,000 spent", INDIGO));
        body.addView(budgetCategory("Utilities", "₱15,000", "₱12,450 spent", GREEN));
        body.addView(budgetCategory("Food & lifestyle", "₱20,000", "₱19,280 spent", Color.rgb(232, 163, 23)));
        body.addView(space(16));
        TextView note = label("Tip: set aside savings before flexible spending to keep your plan on track.", 14, BODY, false);
        note.setPadding(16, 16, 16, 16);
        note.setBackgroundColor(Color.rgb(247, 247, 248));
        body.addView(note);
        scroll.addView(body);
        content.addView(scroll);
    }

    private void addFlowRow(LinearLayout parent, String name, String amount, int progress, int color) {
        LinearLayout row = column(Color.WHITE, 4);
        LinearLayout heading = row();
        heading.addView(label(name, 15, INK, true), new LinearLayout.LayoutParams(0, -2, 1));
        heading.addView(label(amount, 15, color, true));
        row.addView(heading);
        ProgressBar bar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        bar.setMax(100); bar.setProgress(progress); bar.getProgressDrawable().setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);
        row.addView(bar, new LinearLayout.LayoutParams(-1, 12));
        parent.addView(row);
        parent.addView(space(12));
    }

    private View budgetCategory(String name, String budget, String spent, int color) {
        LinearLayout item = card();
        LinearLayout heading = row();
        heading.addView(label(name, 16, INK, true), new LinearLayout.LayoutParams(0, -2, 1));
        Button edit = button("Edit", Color.WHITE, INDIGO);
        heading.addView(edit, new LinearLayout.LayoutParams(72, 44));
        edit.setOnClickListener(v -> showBudgetEditDialog(name, budget));
        item.addView(heading);
        item.addView(label(budget + " budget  ·  " + spent, 14, BODY, false));
        return item;
    }

    private void showBudgetEditDialog(String category, String current) {
        EditText value = input(current, false);
        new AlertDialog.Builder(this).setTitle("Edit " + category + " budget").setView(value).setNegativeButton("Cancel", null).setPositiveButton("Save", (dialog, which) -> {
            preferences.edit().putString("budget_" + category, value.getText().toString()).apply();
            Toast.makeText(this, category + " budget updated", Toast.LENGTH_SHORT).show();
        }).show();
    }

    private void renderHistory() {
        ScrollView scroll = new ScrollView(this);
        LinearLayout body = column(Color.WHITE, 16);
        body.setPadding(16, 8, 16, 80);
        LinearLayout cycle = row();
        Button previous = button("‹", Color.WHITE, INK);
        Button next = button("›", Color.WHITE, INK);
        cycle.addView(previous, new LinearLayout.LayoutParams(52, 48));
        cycle.addView(label("October 2026", 22, INK, true), new LinearLayout.LayoutParams(0, 48, 1));
        cycle.addView(next, new LinearLayout.LayoutParams(52, 48));
        previous.setOnClickListener(v -> Toast.makeText(this, "September 2026", Toast.LENGTH_SHORT).show());
        next.setOnClickListener(v -> Toast.makeText(this, "November 2026", Toast.LENGTH_SHORT).show());
        body.addView(cycle);
        body.addView(label("Monthly movement", 22, INK, true));
        body.addView(label("Income versus expenses by month", 14, BODY, false));
        body.addView(monthlyChart());
        body.addView(space(20));
        body.addView(section("Per cutoff", "October 2026"));
        LinearLayout cutoff = card();
        cutoff.addView(label("1st cutoff                 2nd cutoff", 13, BODY, true));
        cutoff.addView(label("₱31,450                         ₱18,280", 18, INK, true));
        cutoff.addView(label("██████████████      ████████", 16, INDIGO, true));
        cutoff.addView(label("↓ 18% lower spending on the second cutoff", 13, GREEN, true));
        body.addView(cutoff);
        body.addView(space(20));
        LinearLayout monthHeading = row();
        monthHeading.addView(label("Month by month", 22, INK, true), new LinearLayout.LayoutParams(0, -2, 1));
        Button export = button("Export CSV", Color.WHITE, INDIGO);
        monthHeading.addView(export, new LinearLayout.LayoutParams(110, 44));
        export.setOnClickListener(v -> Toast.makeText(this, "CSV export prepared", Toast.LENGTH_SHORT).show());
        body.addView(monthHeading);
        body.addView(historyMonth("October 2026", "₱49,730", "Housing · ₱18,000\nUtilities · ₱12,450\nLifestyle · ₱19,280"));
        body.addView(historyMonth("September 2026", "₱45,280", "Housing · ₱18,000\nUtilities · ₱10,980\nLifestyle · ₱16,300"));
        body.addView(historyMonth("August 2026", "₱51,240", "Housing · ₱18,000\nUtilities · ₱13,240\nLifestyle · ₱20,000"));
        scroll.addView(body);
        content.addView(scroll);
    }

    private View monthlyChart() {
        LinearLayout chart = card();
        chart.setPadding(16, 20, 16, 16);
        LinearLayout bars = row();
        String[] months = {"May", "Jun", "Jul", "Aug", "Sep", "Oct"};
        int[] income = {72, 84, 78, 92, 88, 100};
        int[] spending = {48, 58, 62, 54, 45, 63};
        for (int i = 0; i < months.length; i++) {
            LinearLayout group = column(Color.WHITE, 2);
            group.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            LinearLayout columns = row();
            View incomeBar = new View(this);
            incomeBar.setBackgroundColor(INDIGO);
            View spendingBar = new View(this);
            spendingBar.setBackgroundColor(GREEN);
            columns.addView(incomeBar, new LinearLayout.LayoutParams(16, Math.max(16, income[i])));
            columns.addView(spendingBar, new LinearLayout.LayoutParams(16, Math.max(16, spending[i])));
            group.addView(columns);
            group.addView(label(months[i], 12, BODY, false));
            bars.addView(group, new LinearLayout.LayoutParams(0, 150, 1));
        }
        chart.addView(bars);
        chart.addView(label("■ Income     ■ Expenses", 12, BODY, false));
        return chart;
    }

    private View historyMonth(String month, String total, String detail) {
        LinearLayout item = card();
        item.setOnClickListener(v -> {
            TextView details = label(detail, 14, BODY, false);
            item.addView(details);
            item.setOnClickListener(null);
        });
        LinearLayout heading = row();
        heading.addView(label("⌄  " + month, 16, INK, true), new LinearLayout.LayoutParams(0, -2, 1));
        heading.addView(label(total, 15, INK, true));
        item.addView(heading);
        return item;
    }

    private void renderExpenses() {
        LinearLayout wrapper = column(Color.WHITE, 16);
        wrapper.setPadding(16, 8, 16, 80);
        EditText search = input("Search expenses", false);
        wrapper.addView(search);
        LinearLayout filters = row();
        for (String filter : new String[]{"All", "Due", "Paid", "Overdue"}) {
            Button chip = button(filter, filter.equals("All") ? INDIGO : Color.WHITE, filter.equals("All") ? Color.WHITE : INK);
            filters.addView(chip, new LinearLayout.LayoutParams(0, 44, 1));
            chip.setOnClickListener(v -> filterExpenses(search, filter));
        }
        wrapper.addView(filters);
        wrapper.addView(space(12));
        wrapper.addView(label("October 2026", 13, BODY, true));
        LinearLayout list = column(Color.WHITE, 12);
        wrapper.addView(list);
        Runnable refresh = () -> populateExpenseList(list, search.getText().toString(), "All");
        search.setOnEditorActionListener((v, actionId, event) -> { refresh.run(); return false; });
        populateExpenseList(list, "", "All");
        ScrollView scroll = new ScrollView(this);
        scroll.addView(wrapper);
        content.addView(scroll);
    }

    private void filterExpenses(EditText search, String filter) {
        View parent = (View) search.getParent();
        while (parent != null && !(parent instanceof ScrollView)) parent = (View) parent.getParent();
        renderExpenses();
        Toast.makeText(this, filter + " filter selected", Toast.LENGTH_SHORT).show();
    }

    private void populateExpenseList(LinearLayout list, String query, String filter) {
        list.removeAllViews();
        for (Expense expense : expenses) {
            if (!query.isEmpty() && !expense.name.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))) continue;
            LinearLayout row = card();
            row.addView(label(expense.status.equals("Paid") ? "✓" : "₱", 22, expense.status.equals("Overdue") ? RED : INDIGO, true));
            row.addView(label(expense.name + "\n" + expense.category + " · " + expense.date, 14, INK, false), new LinearLayout.LayoutParams(0, -2, 1));
            row.addView(label(expense.amount + "\n" + expense.status, 13, expense.status.equals("Overdue") ? RED : (expense.status.equals("Paid") ? GREEN : INDIGO), true));
            list.addView(row);
        }
    }

    private void showNewExpenseDialog() {
        LinearLayout form = column(Color.WHITE, 12);
        form.setPadding(24, 8, 24, 8);
        EditText amount = input("₱0.00", false);
        EditText name = input("e.g. Groceries", false);
        EditText notes = input("Notes (optional)", false);
        form.addView(label("New expense", 24, INK, true));
        form.addView(label("Add a bill or payment to your tracker.", 14, BODY, false));
        form.addView(label("Amount", 12, BODY, true)); form.addView(amount);
        form.addView(label("Expense name", 12, BODY, true)); form.addView(name);
        form.addView(label("Category: Utilities · Housing · Food", 14, BODY, false));
        form.addView(label("Due date: October 15, 2026 · Repeat: Once", 14, BODY, false));
        form.addView(notes);
        AlertDialog alert = new AlertDialog.Builder(this).setView(form).setNegativeButton("Cancel", null).setPositiveButton("Save expense", null).create();
        alert.setOnShowListener(dialog -> {
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(INDIGO);
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                if (amount.getText().toString().trim().isEmpty() || name.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, "Amount and name are required", Toast.LENGTH_SHORT).show(); return;
                }
                expenses.add(0, new Expense(name.getText().toString(), "Utilities", "₱" + amount.getText(), "Today · Due Oct 15", "Due"));
                saveExpenses();
                alert.dismiss(); selectedTab = 1; renderTab();
            });
        });
        alert.show();
    }

    private void addDueRow(LinearLayout parent, String name, String category, String amount, boolean overdue) { parent.addView(label((overdue ? "⚠  " : "✓  ") + name + "\n      " + category + "                         " + amount, 14, overdue ? RED : INK, false)); }
    private LinearLayout cutoff(String title, String amount, String status) { LinearLayout c = card(); c.setBackgroundColor(INDIGO_TINT); c.addView(label(title + "\n" + amount + "\n" + status, 14, status.equals("Paid") ? GREEN : INDIGO, true)); return c; }
    private LinearLayout section(String title, String action) { LinearLayout s = row(); s.addView(label(title, 22, INK, true), new LinearLayout.LayoutParams(0, -2, 1)); s.addView(label(action, 13, BODY, false)); return s; }
    private void renderPlaceholder(String title) { content.setGravity(Gravity.CENTER); content.addView(label(title + "\n\nThis section is planned for the next phase.", 22, INK, true)); }
    private LinearLayout column(int color, int gap) { LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); l.setBackgroundColor(color); return l; }
    private LinearLayout row() { LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.HORIZONTAL); return l; }
    private LinearLayout card() {
        LinearLayout l = column(Color.WHITE, 12);
        l.setPadding(16, 14, 16, 14);
        l.setBackground(roundedBackground(Color.WHITE, 18));
        l.setElevation(2);
        return l;
    }
    private TextView label(String text, int size, int color, boolean bold) { TextView v = new TextView(this); v.setText(text); v.setTextSize(size); v.setTextColor(color); v.setTypeface(Typeface.SANS_SERIF, bold ? Typeface.BOLD : Typeface.NORMAL); v.setPadding(0, 4, 0, 4); return v; }
    private EditText input(String hint, boolean password) { EditText e = new EditText(this); e.setHint(hint); e.setTextSize(16); e.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL); e.setSingleLine(true); e.setPadding(14, 0, 14, 0); if (password) e.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD); e.setLayoutParams(new LinearLayout.LayoutParams(-1, 52)); return e; }
    private EditText outlinedInput(String hint, boolean password) {
        EditText e = input(hint, password);
        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.WHITE);
        background.setStroke(1, Color.rgb(180, 195, 205));
        background.setCornerRadius(10);
        e.setBackground(background);
        if (password) e.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_menu_view, 0);
        return e;
    }
    private EditText pillInput(String hint, boolean password) {
        EditText e = input(hint, password);
        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.WHITE);
        background.setCornerRadius(100);
        e.setBackground(background);
        e.setPadding(14, 0, 14, 0);
        return e;
    }
    private Button button(String text, int background, int foreground) { Button b = new Button(this); b.setText(text); b.setTextColor(foreground); b.setTextSize(14); b.setAllCaps(false); b.setMinHeight(0); b.setMinimumHeight(0); b.setPadding(0, 0, 0, 0); b.setGravity(Gravity.CENTER); b.setBackgroundColor(background); return b; }
    private View space(int height) { Space s = new Space(this); s.setLayoutParams(new LinearLayout.LayoutParams(1, height)); return s; }
    private View spaceHorizontal(int width) { Space s = new Space(this); s.setLayoutParams(new LinearLayout.LayoutParams(width, 1)); return s; }
    private boolean hasNetwork() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        android.net.Network network = manager == null ? null : manager.getActiveNetwork();
        NetworkCapabilities capabilities = network == null ? null : manager.getNetworkCapabilities(network);
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    private static class Expense { String name, category, amount, date, status; Expense(String n, String c, String a, String d, String s) { name=n; category=c; amount=a; date=d; status=s; } }
}
