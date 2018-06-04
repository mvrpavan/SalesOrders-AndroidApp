package com.ceren.salesorders.handlers;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ceren.salesorders.models.TransactionDetails;
import com.ceren.salesorders.models.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class FirebaseDBHandler {
    private DatabaseReference UserDataReference;
    private DatabaseReference ProductLineDataReference;
    private FirebaseUser mUser;
    private UserData currentUserData;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private List<TransactionDetails> ListRecentTransactionDetails, ListTransactionDetails;

    private static final String USER_DATABASE = "UserData";
    private static final String PRODUCTLINE_DATABASE = "ProductLine";
    private static FirebaseDBHandler staticInstance = null;
    private static String UID = "";

    public static FirebaseDBHandler getInstance(String UID) {

        if (staticInstance == null || FirebaseDBHandler.UID != UID) {
            FirebaseDBHandler.UID = UID;
            staticInstance = new FirebaseDBHandler();
        }

        return staticInstance;
    }

    private FirebaseDBHandler() {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        UserDataReference = mDatabase.getReference(USER_DATABASE);
        ProductLineDataReference = mDatabase.getReference(PRODUCTLINE_DATABASE);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void LoadCurrentUserData(final Runnable func) {
        currentUserData = null;
        UserDataReference.child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("", "loadUserData:onDataChange:" + dataSnapshot.getChildrenCount());
                // Get userData object and use the values to update the UI
                currentUserData = dataSnapshot.getValue(UserData.class);
                if (func != null) func.run();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting UserData failed, log a message
                Log.w("", "loadUserData:onCancelled", databaseError.toException());
            }
        });
    }

    public UserData getCurrentUserData() {
        return currentUserData;
    }

    private void LoadTransactionDetailsForDateList(List<String> RecentDateStrings, final Runnable callbackFunc) {
        List<Task<List<TransactionDetails>>> ListTasks = new ArrayList<>();
        DatabaseReference TransactionDataReference = ProductLineDataReference.child(currentUserData.getProductLine()).child("TransactionData");
        for (String date : RecentDateStrings) {
            final TaskCompletionSource<List<TransactionDetails>> tcs = new TaskCompletionSource<>();
            Log.d("FirebaseDBHandler","Getting Transactions for User:" + UID + " and Date:" + date);
            TransactionDataReference.child(UID).child(date).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<TransactionDetails> ListTransactionDetails = new ArrayList<>();
                    Log.d("FirebaseDBHandler","OnDataChange() invoked, Children:"+ dataSnapshot.getChildrenCount());
                    // Get currentTransactionDetails object and use the values to update the UI
                    if (dataSnapshot.getChildrenCount() > 0) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Log.d("FirebaseDBHandler","OnDataChange() invoked, Children:"+ ds.getChildrenCount());
                            TransactionDetails currentTransactionDetails = ds.getValue(TransactionDetails.class);
                            if (currentTransactionDetails != null)
                                ListTransactionDetails.add(currentTransactionDetails);
                        }
                    }
                    tcs.setResult(ListTransactionDetails);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    List<TransactionDetails> ListTransactionDetails = new ArrayList<>();
                    // Getting Post failed, log a message
                    Log.w("", "loadTransactionData:onCancelled", databaseError.toException());
                    tcs.setResult(ListTransactionDetails);
                }
            });

            ListTasks.add(tcs.getTask());
        }

        Tasks.whenAllSuccess(ListTasks).addOnCompleteListener(new OnCompleteListener<List<Object>>() {
            @Override
            public void onComplete(@NonNull Task<List<Object>> task) {
                Log.d("FirebaseDBHandler","All tasks Complete:onComplete() invoked");
                ListTransactionDetails = new ArrayList<>();
                List<Object> tmpListTransactionDetails = task.getResult();
                //Log.d("FirebaseDBHandler","tmpListTransactionDetails.size():" + tmpListTransactionDetails.size());
                if (tmpListTransactionDetails.size() > 0) {
                    for (Object obj : tmpListTransactionDetails) {
                        //Log.d("FirebaseDBHandler","tmpListTransactionDetails[i] cast:" + obj.getClass().getName());
                        List<TransactionDetails> tmp = (ArrayList<TransactionDetails>) obj;
                        //Log.d("FirebaseDBHandler","tmp.size():" + tmp.size());
                        ListTransactionDetails.addAll(tmp);
                    }
                }

                Collections.sort(ListTransactionDetails, new Comparator<TransactionDetails>() {
                    @Override
                    public int compare(TransactionDetails t1, TransactionDetails t2) {
                        try {
                            return dateFormat.parse(t1.getCreateTime()).compareTo(dateFormat.parse(t2.getCreateTime()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    }
                });

                if (callbackFunc != null) callbackFunc.run();
                Log.d("FirebaseDBHandler","All tasks Complete:onComplete() completed");
            }
        });
    }

    public void LoadRecentTransactionsData(final Runnable callbackFunc) {
        if (currentUserData == null) return;

        List<String> RecentDateStrings = currentUserData.getRecentTransactionDates();
        if (RecentDateStrings.size() == 0) return;

        Collections.sort(RecentDateStrings, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                try {
                    Log.d("FirebaseDBHandler","Collections.Comparator:" + o1);
                    return dateFormat.parse(o1).compareTo(dateFormat.parse(o2));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });

        LoadTransactionDetailsForDateList(RecentDateStrings, new Runnable() {
            @Override
            public void run() {
                ListRecentTransactionDetails = new ArrayList<>();
                ListRecentTransactionDetails.addAll(ListTransactionDetails);
                //Keep latest 5 transactions only
                if (ListRecentTransactionDetails.size() > 5) {
                    for (int i = 0; i < ListRecentTransactionDetails.size() && ListRecentTransactionDetails.size() > 5; i++) {
                        ListRecentTransactionDetails.remove(i);
                        i--;
                    }
                }
                ListTransactionDetails.clear();
                ListTransactionDetails = null;
                if (callbackFunc != null) callbackFunc.run();
            }
        });
    }

    public void LoadTransactionsDataForDateRange(Date StartDate, Date EndDate, final Runnable callbackFunc) {
        Calendar cal = Calendar.getInstance();
        Date CurrentDate = StartDate;
        cal.setTime(CurrentDate);
        List<String> DateList = new ArrayList<>();
        while (CurrentDate.compareTo(EndDate) <= 0) {
            DateList.add(dateFormat.format(CurrentDate));
            cal.add(Calendar.DATE, 1);
            CurrentDate = cal.getTime();
        }

        LoadTransactionDetailsForDateList(DateList, callbackFunc);
    }

    public List<TransactionDetails> GetListTransactionDetails() {
        return ListTransactionDetails;
    }

    public List<TransactionDetails> GetListRecentTransactionDetails() { return ListRecentTransactionDetails; }

    public void UpdateDisplayName(final String NewDispalyName, final Runnable callbackFunc) {
        UserDataReference.child(UID).child("DisplayName").setValue(NewDispalyName).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("FirebaseDBHandler", "UpdateDisplayName(), Name changed to:" + NewDispalyName);
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(NewDispalyName)
                        //.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                        .build();

                mUser.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("FirebaseDBHandler", "UpdateDisplayName(), User DisplayName updated");
                                }
                                if (callbackFunc != null) callbackFunc.run();
                            }
                        });
            }
        });
    }

    public void updateLastLoginDate(Date loginDate) {
        UserDataReference.child(UID).child("LastLoginDate").setValue(dateTimeFormat.format(loginDate));
    }
}
