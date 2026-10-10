package com.example.expensetracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
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

    private final List<Expense> expenses = new ArrayList<>();
    private SharedPreferences preferences;
    private LinearLayout content;
    private int selectedTab = 0;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        preferences = getSharedPreferences("expense_tracker", MODE_PRIVATE);
        loadExpenses();
        if (preferences.getBoolean("logged_in", false)) showShell(); else showLogin();
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
        LinearLayout root = column(Color.WHITE, 24);
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
        LinearLayout root = column(Color.WHITE, 0);
        LinearLayout top = row();
        top.setGravity(Gravity.CENTER_VERTICAL);
        top.setPadding(16, 12, 8, 8);
        TextView title = label("Dashboard", 22, INK, true);
        top.addView(title, new LinearLayout.LayoutParams(0, 56, 1));
        TextView profile = label("●", 22, INDIGO, true);
        profile.setGravity(Gravity.CENTER);
        profile.setContentDescription("Profile and logout");
        profile.setOnClickListener(v -> {
            preferences.edit().putBoolean("logged_in", false).apply();
            showLogin();
        });
        top.addView(profile, new LinearLayout.LayoutParams(56, 56));
        root.addView(top);
        if (!hasNetwork()) {
            TextView offline = label("You're offline — changes are saved on this device.", 13, RED, true);
            offline.setPadding(16, 10, 16, 10);
            offline.setBackgroundColor(Color.rgb(255, 238, 240));
            root.addView(offline);
        }

        content = column(Color.WHITE, 0);
        root.addView(content, new LinearLayout.LayoutParams(-1, 0, 1));

        LinearLayout nav = row();
        nav.setPadding(4, 4, 4, 4);
        String[] tabs = {"▦\nDashboard", "▤\nExpenses", "▣\nBudget", "▥\nHistory"};
        for (int i = 0; i < tabs.length; i++) {
            final int index = i;
            TextView item = label(tabs[i], 12, i == 0 ? INDIGO : BODY, false);
            item.setGravity(Gravity.CENTER);
            item.setOnClickListener(v -> { selectedTab = index; renderTab(); });
            nav.addView(item, new LinearLayout.LayoutParams(0, 64, 1));
        }
        root.addView(nav);

        Button fab = button("+", INDIGO, Color.WHITE);
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
        LinearLayout body = column(Color.WHITE, 16);
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
    private LinearLayout card() { LinearLayout l = column(Color.WHITE, 12); l.setPadding(16, 14, 16, 14); l.setBackgroundColor(Color.WHITE); return l; }
    private TextView label(String text, int size, int color, boolean bold) { TextView v = new TextView(this); v.setText(text); v.setTextSize(size); v.setTextColor(color); v.setTypeface(Typeface.DEFAULT, bold ? Typeface.BOLD : Typeface.NORMAL); v.setPadding(0, 4, 0, 4); return v; }
    private EditText input(String hint, boolean password) { EditText e = new EditText(this); e.setHint(hint); e.setTextSize(16); e.setSingleLine(true); e.setPadding(14, 0, 14, 0); if (password) e.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD); e.setLayoutParams(new LinearLayout.LayoutParams(-1, 52)); return e; }
    private Button button(String text, int background, int foreground) { Button b = new Button(this); b.setText(text); b.setTextColor(foreground); b.setTextSize(14); b.setAllCaps(false); b.setBackgroundColor(background); return b; }
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
