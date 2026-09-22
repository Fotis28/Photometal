package com.example.photometal1.GlobalMethods;

public final class SessionManager {

    // 1. Static πεδίο για την αποθήκευση του ID. Το -1 δηλώνει μη συνδεδεμένο χρήστη.
    private static int currentUserId = -1;
    private static String currentUserRole = null;

    // 2. Μέθοδος για την αποθήκευση του ID μετά το επιτυχημένο Login
    public static void setUserId(int id, String role) {
        currentUserId = id;
        currentUserRole = role;
    }

    // 3. Μέθοδος για την ανάκτηση του ID από οποιοδήποτε σημείο της εφαρμογής
    public static int getUserId() {
        return currentUserId;
    }

    // 4. Μέθοδος για το Log Out
    public static void clearSession() {
        currentUserId = -1;
        currentUserRole = null;
    }
    public static String getCurrentUserRole() {
        return currentUserRole;
    }

}
