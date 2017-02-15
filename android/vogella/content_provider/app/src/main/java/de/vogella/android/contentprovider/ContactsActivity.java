package de.vogella.android.contentprovider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ContactsActivity extends AppCompatActivity {

    private static final String TAG = "ContactsActivity";
    private static final int REQUEST_READ_CONTACTS = 1;
    private static boolean mayReadContacts;
    private TextView contactView;

    private void requestReadContactsPermission() {
        if (! mayReadContacts) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS
            );
        }
    }

    private Cursor getContacts() {
        /* FROM clause.
         *
         * URI (content://<authority>/<path>) that points to the contacts database table.
         */
        Uri uri = ContactsContract.Contacts.CONTENT_URI;

        /* SELECT clause.
         *
         * Columns to retrieve.
         */
        String[] projection = new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
        };

        /* WHERE clause.
        *
        * Contains optional ? placeholders.
        */
        String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '" + ("1") + "'";

        /* Values for each of the ? placeholders in the WHERE clause. */
        String[] selectionArgs = null;

        /* ORDER BY clause. */
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

        return getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
    }

    private  void displayContacts() {
        if (mayReadContacts) {
            Cursor cursor = getContacts();

            while (cursor.moveToNext()) {
                String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                contactView.append("Name: ");
                contactView.append(displayName);
                contactView.append("\n");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        contactView = (TextView) findViewById(R.id.contactView);

        mayReadContacts = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED;

        requestReadContactsPermission();
        displayContacts();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_CONTACTS: {
               boolean permissionGranted = grantResults.length > 0
                                           && grantResults[0] == PackageManager.PERMISSION_GRANTED;

                if (permissionGranted) {
                    mayReadContacts = true;
                    displayContacts();
                }
            }
        }
    }
}
