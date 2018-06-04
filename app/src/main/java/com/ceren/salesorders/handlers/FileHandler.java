package com.ceren.salesorders.handlers;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileHandler {
    private Context mContext;
    private String FILENAME = "ceren_sales_orders_data";

    public FileHandler(Context context) {
        mContext = context;
    }

    public String getUserName() {
        if (isFilePresent(FILENAME)) {
            return read(FILENAME);
        }

        return null;
    }

    public boolean setUserName(String userName) {
        return create(FILENAME, userName);
    }

    private String read(String fileName) {
        try {
            FileInputStream fis = mContext.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (FileNotFoundException fileNotFound) {
            return null;
        } catch (IOException ioException) {
            return null;
        }
    }

    private boolean create(String fileName, String jsonString){
        try {
            FileOutputStream fos = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            if (jsonString != null) {
                fos.write(jsonString.getBytes());
            }
            fos.close();
            return true;
        } catch (FileNotFoundException fileNotFound) {
            return false;
        } catch (IOException ioException) {
            return false;
        }
    }

    private boolean isFilePresent(String fileName) {
        String path = mContext.getFilesDir().getAbsolutePath() + "/" + fileName;
        File file = new File(path);
        return file.exists();
    }
}
