package com.example.sslp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;



public class RegisterPage extends AppCompatActivity {

    private Button buttonRegister;
    private EditText editTextRegisterEmailId,editTextRegisterFullName,editTextRegisterPassword,
            editTextRegisterConfirmPassword,editTextRegisterBioID;
    private TextView editTextRegisterDOB;
    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;
    private Toolbar toolbar;

    private final ActivityResultLauncher<ScanOptions> QrCodeScanner = registerForActivityResult(new ScanContract(), new ActivityResultCallback<ScanIntentResult>() {
        @Override
        public void onActivityResult(ScanIntentResult intentResult) {
            if(intentResult != null){
                String QRCode = intentResult.getContents();
                if(QRCode != null){
                    editTextRegisterBioID.setText(QRCode);
                }
            }
        }
    });



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
        editTextRegisterEmailId = findViewById(R.id.editText_Register_Email_Id);
        editTextRegisterFullName = findViewById(R.id.editText_Register_Full_Name);
        editTextRegisterDOB = findViewById(R.id.editText_Register_DOB);
        editTextRegisterPassword = findViewById(R.id.editTextRegisterPagePassword);
        editTextRegisterConfirmPassword = findViewById(R.id.editTextRegisterPageConfirmPassword);
        editTextRegisterBioID = findViewById(R.id.editText_Register_BioID);
        buttonRegister = findViewById(R.id.buttonRegisterPageRegister);
        firebaseAuth = FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.topAppBarRegisterPage);



        scanQR();
        onClickedRegisterButton();
        dateOfBirthSelection();
        onClickedBackButton();


    }

    private void onClickedBackButton() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Show the back button
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterPage.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void userRegisteredToFirebase(User user) {
        String email = user.getUserEmail();
        String password = user.getUserPassword();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    try {
                        clearAllSections();
                        Toast.makeText(RegisterPage.this,"Registerd Succesfully",Toast.LENGTH_SHORT).show();

                        String userId = firebaseAuth.getCurrentUser().getUid();
                        String securePassword = encryptPassword();
                        user.setUserPassword(securePassword);
                        user.setUserID(userId);
                        addUserDetailsToFirebase(user,userId);

                        Intent intent = new Intent(RegisterPage.this,LoginPage.class);
                        startActivity(intent);
                    }catch (Exception e){
                        Toast.makeText(RegisterPage.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    // Task failed; handle error
                    String errorMessage = "Registration failed.";
                    if (task.getException() != null) {
                        errorMessage = task.getException().getMessage();
                    }
                    Toast.makeText(RegisterPage.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addUserDetailsToFirebase(User user, String userId) {
        if(user != null || userId != null){
            databaseReference.child(userId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(RegisterPage.this,"User Details Added",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(RegisterPage.this,"Unable to add User Details",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void clearAllSections(){
        editTextRegisterEmailId.setText("");
        editTextRegisterFullName.setText("");
        editTextRegisterDOB.setText("");
        editTextRegisterPassword.setText("");
        editTextRegisterConfirmPassword.setText("");
        editTextRegisterBioID.setText("");
    }

    private void scanQR() {
        editTextRegisterBioID.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Check if the click is inside the bounds of the drawableRight
                    Drawable drawableRight = editTextRegisterBioID.getCompoundDrawables()[2];
                    if (drawableRight != null) {
                        // Get the bounds of the drawable
                        int drawableRightWidth = drawableRight.getBounds().width();
                        int drawableRightX = editTextRegisterBioID.getWidth() - editTextRegisterBioID.getPaddingRight() - drawableRightWidth;

                        // Check if the touch event is inside the drawable area
                        if (event.getX() >= drawableRightX && event.getX() <= drawableRightX + drawableRightWidth) {
                            // Perform the function when the drawable is clicked
                            performDrawableClickAction();
                            return true; // Return true to indicate the event was handled
                        }
                    }
                }
                return false; // Return false to allow further processing of the event
            }
        });
    }

    private void performDrawableClickAction() {
        ScanOptions scanOptions = new ScanOptions();
        scanOptions.setBeepEnabled(true);
        scanOptions.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        scanOptions.setOrientationLocked(true);
        scanOptions.setPrompt("Scan a QR code");
        QrCodeScanner.launch(scanOptions);

    }

    private String encryptPassword() {
        String password = editTextRegisterPassword.getText().toString().trim();
        try{
            byte[] bytePassword = password.getBytes();
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] byteSecuredPassword = messageDigest.digest(bytePassword);
            StringBuilder stringBuilder = new StringBuilder();
            for(byte b : byteSecuredPassword){

                stringBuilder.append(String.format("%02X",(b & 0xFF)));
            }
            String securedPassword = stringBuilder.toString().trim();
            return securedPassword;
        }catch (Exception e) {
            String errorMessage = e.getMessage();
            if (errorMessage != null) {
                Toast.makeText(RegisterPage.this, errorMessage, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RegisterPage.this, R.string.errorEncryption, Toast.LENGTH_SHORT).show();
            }
            return "Encryption Error";
        }
    }


    private void dateOfBirthSelection() {
        editTextRegisterDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDate();
            }
        });
    }

    private void setDate() {
        // Used for finding the date
        final Calendar calendar = Calendar.getInstance();

        // This  gives today's date which can be used as setting the maximum age
        Calendar today = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,R.style.CustomDatePickerDialog,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year1, int monthOfYear, int dayOfMonth) {
                        String formattedDay = String.format("%02d",dayOfMonth);
                        String formattedMonth = String.format("%02d",monthOfYear+1);
                        editTextRegisterDOB.setText(formattedDay + "-" + formattedMonth + "-" + year1);
                    }
                },year,month,day);

        DatePicker datePicker = datePickerDialog.getDatePicker();
        datePicker.setMaxDate(today.getTimeInMillis());
        datePickerDialog.show();
    }

    private void onClickedRegisterButton() {

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userBioID = editTextRegisterBioID.getText().toString().trim();
                String userEmail = editTextRegisterEmailId.getText().toString().trim();
                checkBioIDAndEmails(userEmail,userBioID);
            }
        });

    }

    private boolean validInput() {
        String email = editTextRegisterEmailId.getText().toString().trim();
        String fullName = editTextRegisterFullName.getText().toString().trim();
        String password = editTextRegisterPassword.getText().toString().trim();
        String confirmPassword = editTextRegisterConfirmPassword.getText().toString().trim();
        String dateOfBirth = editTextRegisterDOB.getText().toString().trim();
        String bioID = editTextRegisterBioID.getText().toString().trim();
        String yearInDOB = "";
        if(!dateOfBirth.isEmpty()) {
            yearInDOB = dateOfBirth.substring(dateOfBirth.length()-4,dateOfBirth.length());
        }

        // Check if the username is empty or not
        if(TextUtils.isEmpty(fullName)){
            int errorMessage = R.string.errorRegisterPageEmptyFullName;
            showAlertDialog(errorMessage);
        }
        // Check if the FullName starts with an alphabet
        else if(!fullName.matches("^[a-zA-Z][a-zA-Z ]*$")){
            int errorMessage = R.string.errorRegisterPageInvalidFullName;
            showAlertDialog(errorMessage);
            editTextRegisterFullName.setText("");
        }
        // Check if the email is empty or not
        else if(TextUtils.isEmpty(email)){
            int errorMessage = R.string.errorRegisterPageEmptyEmail;
            showAlertDialog(errorMessage);
        }
        // Check if the email is valid or not
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            int errorMessage = R.string.errorRegisterPageInvalidEmail;
            showAlertDialog(errorMessage);
            editTextRegisterEmailId.setText("");
        }
        // Check if the DOB is empty or not
        else if(TextUtils.isEmpty(dateOfBirth)){
            int errorMessage = R.string.errorRegisterPageEmptyDOB;
            showAlertDialog(errorMessage);
        }
        // Check if the person is 18+
        else if(!isAgeValid(yearInDOB)){
            int errorMessage = R.string.errorRegisterPageInvalidDate;
            showAlertDialog(errorMessage);
            editTextRegisterDOB.setText("");
        }
        // Check if the password is empty or not
        else if (TextUtils.isEmpty(password)) {
            int errorMessage = R.string.errorRegisterPageEmptyPassword;
            showAlertDialog(errorMessage);
        }
        // Check if the password is valid or not
        else if (password.length() < 6) {
            int errorMessage = R.string.errorRegisterPageInvalidPassword;
            showAlertDialog(errorMessage);
            editTextRegisterPassword.setText("");
        }
        else if (!password.matches(".*\\d.*")) {
            int errorMessage = R.string.errorRegisterPageInvalidPassword3;
            showAlertDialog(errorMessage);
            editTextRegisterPassword.setText("");
        }
        else if (!password.matches(".*[!@#$%^&*()_+=<>?/{}~`\\[\\]-].*")) {
            int errorMessage = R.string.errorRegisterPageInvalidPassword2;
            showAlertDialog(errorMessage);
            editTextRegisterPassword.setText("");
        }
        // Check if the confirm password is empty or not
        else if (TextUtils.isEmpty(confirmPassword)) {
            int errorMessage = R.string.errorRegisterPageEmptyConfirmPassword;
            showAlertDialog(errorMessage);
            editTextRegisterConfirmPassword.setText("");
        }
        else if(!TextUtils.equals(password,confirmPassword)){
            int errorMessage = R.string.errorRegisterPageInvalidConfirmPassword;
            showAlertDialog(errorMessage);
            editTextRegisterConfirmPassword.setText("");
        }
        else if(TextUtils.isEmpty(bioID)){
            int errorMessage = R.string.errorRegisterPageEmptyBioID;
            showAlertDialog(errorMessage);
        }
        else if(!ifPresent(bioID)){
            int errorMessage = R.string.errorRegisterPageInvalidBioID;
            showAlertDialog(errorMessage);
            editTextRegisterBioID.setText("");
        }
        else{
            return true;
        }
        return false;
    }

    private void checkBioIDAndEmails(String emails,String bioID){
        fetchDetailsFromFirebase("userBioID", new FetchDataCallback() {
            @Override
            public void onSuccess(List<String> bioIDs) {
                if(bioIDs.contains(bioID)){
                    int errorMessage = R.string.errorRegisterPageExistingBioID;
                    showAlertDialog(errorMessage);
                    editTextRegisterBioID.setText("");
                }
                else{
                    fetchDetailsFromFirebase("userEmail", new FetchDataCallback() {
                        @Override
                        public void onSuccess(List<String> bioIDs) {
                            if(bioIDs.contains(emails)){
                                int errorMessage = R.string.errorRegisterPageExistingEmails;
                                showAlertDialog(errorMessage);
                                editTextRegisterEmailId.setText("");
                            }
                            else{
                                if(validInput()){
//                                    String userSecuredPassword = encryptPassword();
                                    String userSecuredPassword = editTextRegisterPassword.getText().toString().trim();
                                    String userDOB = editTextRegisterDOB.getText().toString().trim();
                                    String userName = editTextRegisterFullName.getText().toString().trim();
                                    User user = new User(emails,userName,userDOB,userSecuredPassword,bioID);
                                    user.setUserRole("User");
                                    userRegisteredToFirebase(user);
                                }
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Toast.makeText(RegisterPage.this,errorMessage,Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(RegisterPage.this,errorMessage,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDetailsFromFirebase(String fieldName,FetchDataCallback dataCallback){
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        List<String> dataList = new ArrayList<>();
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    DataSnapshot dataSnapshot = task.getResult();
                    if(dataSnapshot.exists()){
                        for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                            User user = userSnapshot.getValue(User.class);
                            String fieldValue = null;
                            if(fieldName.equals("userEmail")){
                                fieldValue = user.getUserEmail();
                            } else if (fieldName.equals("userBioID")) {
                                fieldValue = user.getUserBioID();
                            }
                            if(fieldValue != null){
                                dataList.add(fieldValue);
                            }
                        }
                        dataCallback.onSuccess(dataList);
                    }
                    else {
                        dataCallback.onError("Failed To Retrieve Data of " + fieldName + " From Database");
                    }
                }
            }
        });
    }

    // Checks the age of user
    private boolean isAgeValid(String yearInDOB) {
        Calendar calendar = Calendar.getInstance();
        int intYearInDOB = Integer.parseInt(yearInDOB);
        int year = calendar.get(Calendar.YEAR);
        int age = year - intYearInDOB;
        return age >= 18;
    }

    private void showAlertDialog(int errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterPage.this);
        builder.setTitle("Error");
        builder.setIcon(R.drawable.toast_error_icon);
        builder.setMessage(errorMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean ifPresent(String bioID) {
        List<String>bioIdList = Arrays.asList("1K3JTWHA05", "1PUQV970LA", "2BIB99Z54V", "2WYIM3QCK9", "30MY51J1CJ",
                "340B1EOCMG", "49YFTUA96K", "4HTOAI9YKO", "6EBQ28A62V", "6X6I6TSUFG",
                "7DMPYAZAP2", "88V3GKIVSF", "8OLYIE2FRC", "9JSXWO4LGH", "ABQYUQCQS2",
                "AT66BX2FXM", "BPX8O0YB5L", "BZW5WWDMUY", "C7IFP4VWIL", "CCU1D7QXDT",
                "CET8NUAE09", "CG1I9SABLL", "D05HPPQNJ4", "DHKFIYHMAZ", "E7D6YUPQ6J",
                "F3ATSRR5DQ", "FH6260T08H", "FINNMWJY0G", "FPALKDEL5T", "GOYWJVDA8A",
                "H5C98XCENC", "JHDCXB62SA", "K1YL8VA2HG", "LZK7P0X0LQ", "O0V55ENOT0",
                "O3WJFGR5WE", "PD6XPNB80J", "PGPVG5RF42", "QJXQOUPTH9", "QTLCWUS8NB",
                "RYU8VSS4N5", "S22A588D75", "SEIQTS1H16", "TLFDFY7RDG", "TTK74SYYAN",
                "V2JX0IC633", "V30EPKZQI2", "VQKBGSE3EA", "X16V7LFHR2", "Y4FC3F9ZGS");

        if(bioIdList.contains(bioID)){
            return true;
        }
        else {
            return false;
        }
    }
}