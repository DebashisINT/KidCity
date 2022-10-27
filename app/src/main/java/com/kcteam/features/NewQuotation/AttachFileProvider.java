package com.kcteam.features.NewQuotation;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;

public class AttachFileProvider extends ContentProvider {

    // The authority is the symbolic name for the provider class
    public static final String AUTHORITY = "com.example.pDoc.pdocsigner.provider";

    // UriMatcher used to match against incoming requests<br />
    private UriMatcher uriMatcher;
    @Override
    public boolean onCreate() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // Add a URI to the matcher which will match against the form
        uriMatcher.addURI(AUTHORITY, "*", 1);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }


    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        // Check incoming Uri against the matcher
        switch (uriMatcher.match(uri)) {
            // If it returns 1 - then it matches the Uri defined in onCreate
            case 1:
                // The desired file name is specified by the last segment of the path
                // Take this and build the path to the file
                String fileLocation = getContext().getCacheDir() + File.separator + uri.getLastPathSegment();

                // Create & return a ParcelFileDescriptor pointing to the file
                ParcelFileDescriptor pfd = ParcelFileDescriptor.open(new File(fileLocation),  ParcelFileDescriptor.MODE_READ_ONLY);
                return pfd;

            // Otherwise unrecognised Uri
            default:
                //Log.v(LOG_TAG, "Unsupported uri: '" + uri + "'.");
                throw new FileNotFoundException("Unsupported uri: " + uri.toString());
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
