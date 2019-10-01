package com.peter1303.phonograph.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.webkit.MimeTypeMap;

import com.peter1303.phonograph.loader.SongLoader;
import com.peter1303.phonograph.loader.SortedCursor;
import com.peter1303.phonograph.model.Song;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public final class FileUtil {
    private FileUtil() {
    }

    @NonNull
    public static List<Song> matchFilesWithMediaStore(@NonNull Context context, @Nullable List<File> files) {
        return SongLoader.getSongs(makeSongCursor(context, files));
    }

    @Nullable
    public static SortedCursor makeSongCursor(@NonNull final Context context, @Nullable final List<File> files) {
        String selection = null;
        String[] paths = null;

        if (files != null) {
            paths = toPathArray(files);

            if (files.size() > 0 && files.size() < 999) { // 999 is the max amount Androids SQL implementation can handle.
                selection = MediaStore.Audio.AudioColumns.DATA + " IN (" + makePlaceholders(files.size()) + ")";
            }
        }

        Cursor songCursor = SongLoader.makeSongCursor(context, selection, selection == null ? null : paths);

        return songCursor == null ? null : new SortedCursor(songCursor, paths, MediaStore.Audio.AudioColumns.DATA);
    }

    private static String makePlaceholders(int len) {
        StringBuilder sb = new StringBuilder(len * 2 - 1);
        sb.append("?");
        for (int i = 1; i < len; i++) {
            sb.append(",?");
        }
        return sb.toString();
    }

    @Nullable
    private static String[] toPathArray(@Nullable List<File> files) {
        if (files != null) {
            String[] paths = new String[files.size()];
            for (int i = 0; i < files.size(); i++) {
                paths[i] = safeGetCanonicalPath(files.get(i));
            }
            return paths;
        }
        return null;
    }

    @NonNull
    public static List<File> listFiles(@NonNull File directory, @Nullable FileFilter fileFilter) {
        List<File> fileList = new LinkedList<>();
        File[] found = directory.listFiles(fileFilter);
        if (found != null) {
            Collections.addAll(fileList, found);
        }
        return fileList;
    }

    @NonNull
    public static List<File> listFilesDeep(@NonNull File directory, @Nullable FileFilter fileFilter) {
        List<File> files = new LinkedList<>();
        internalListFilesDeep(files, directory, fileFilter);
        return files;
    }

    @NonNull
    public static List<File> listFilesDeep(@NonNull Collection<File> files, @Nullable FileFilter fileFilter) {
        List<File> resFiles = new LinkedList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                internalListFilesDeep(resFiles, file, fileFilter);
            } else if (fileFilter == null || fileFilter.accept(file)) {
                resFiles.add(file);
            }
        }
        return resFiles;
    }

    private static void internalListFilesDeep(@NonNull Collection<File> files, @NonNull File directory, @Nullable FileFilter fileFilter) {
        File[] found = directory.listFiles(fileFilter);

        if (found != null) {
            for (File file : found) {
                if (file.isDirectory()) {
                    internalListFilesDeep(files, file, fileFilter);
                } else {
                    files.add(file);
                }
            }
        }
    }

    public static boolean fileIsMimeType(File file, String mimeType, MimeTypeMap mimeTypeMap) {
        if (mimeType == null || mimeType.equals("*/*")) {
            return true;
        } else {
            // get the file mime type
            String filename = file.toURI().toString();
            int dotPos = filename.lastIndexOf('.');
            if (dotPos == -1) {
                return false;
            }
            String fileExtension = filename.substring(dotPos + 1).toLowerCase();
            String fileType = mimeTypeMap.getMimeTypeFromExtension(fileExtension);
            if (fileType == null) {
                return false;
            }
            // check the 'type/subtype' pattern
            if (fileType.equals(mimeType)) {
                return true;
            }
            // check the 'type/*' pattern
            int mimeTypeDelimiter = mimeType.lastIndexOf('/');
            if (mimeTypeDelimiter == -1) {
                return false;
            }
            String mimeTypeMainType = mimeType.substring(0, mimeTypeDelimiter);
            String mimeTypeSubtype = mimeType.substring(mimeTypeDelimiter + 1);
            if (!mimeTypeSubtype.equals("*")) {
                return false;
            }
            int fileTypeDelimiter = fileType.lastIndexOf('/');
            if (fileTypeDelimiter == -1) {
                return false;
            }
            String fileTypeMainType = fileType.substring(0, fileTypeDelimiter);
            return fileTypeMainType.equals(mimeTypeMainType);
        }
    }

    public static String stripExtension(String str) {
        if (str == null) return null;
        int pos = str.lastIndexOf('.');
        if (pos == -1) return str;
        return str.substring(0, pos);
    }

    public static String readFromStream(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            if (sb.length() > 0) sb.append("\n");
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }

    public static String read(File file) throws Exception {
        FileInputStream fin = new FileInputStream(file);
        String ret = readFromStream(fin);
        fin.close();
        return ret;
    }

    public static String safeGetCanonicalPath(File file) {
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
            return file.getAbsolutePath();
        }
    }

    public static File safeGetCanonicalFile(File file) {
        try {
            return file.getCanonicalFile();
        } catch (IOException e) {
            e.printStackTrace();
            return file.getAbsoluteFile();
        }
    }

    public static boolean string2File(String content, String name, String suffix, String path) {
        String new_path = path + name + (suffix.isEmpty() ? "" : "." + suffix);
        try {
            Log.i("Phonograph", "write File:" + new_path);
            FileWriter fw = new FileWriter(new_path);
            fw.flush();
            fw.write(content);
            fw.close();
            return true;
        } catch (Exception e) {
            Log.e("Phonograph", "Error on write File:" + e);
            return false;
        }
    }

    /**
     * 得到 App 的 files 目录
     * @param context
     * @return
     */
    public static File getAppStorageDir(Context context) {
        File file = new File(context.getExternalFilesDir(null) + File.separator);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    /**
     * 获取封面目录
     * @param context
     * @return
     */
    public static String getAlbumCoverDir(Context context) {
        File file = new File(getAppStorageDir(context).toString() + "/album/");
        if (!file.exists()) {
            file.mkdirs();
        }
        boolean b = new File(file.toString() + "/.nomeadia").exists();
        if (!b) {
            string2File("", ".nomeadia", "", file.toString() + "/");
        }
        Log.i("Phonograph", "getAlbumCoverDir -> nomeadia: " + b);
        return file.toString();
    }

    /**
     * 检测封面图片是否存在
     * @param context
     * @param name
     * @return
     */
    public static boolean albumExists(Context context, String name) {
        return new File(getAlbumCoverDir(context) + "/" + name + ".png").exists();
    }

    /**
     * 获取本地的封面图片
     * @param context
     * @param name
     * @return
     */
    public static File getAlbumCover(Context context, String name) {
        return new File(getAlbumCoverDir(context) + "/" + name + ".png");
    }

    /*
    public static boolean bitmap2File(Context context, Bitmap bitmap, String name) {
        try {
            File file = new File(getAlbumCoverDir(context) + name + ".png");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            return new File(file.toString()).exists();
        } catch (Exception e) {
            Log.e("Phonograph", "bitmap2File: error -> " + e.toString());
            return false;
        }
    }
     */

    /**
     * 将缓冲的图片进行保存
     * @param context
     * @param o
     * @param name
     * @return
     */
    public static boolean album2File(Context context, File o, String name) {
        try {
            File file = new File(getAlbumCoverDir(context) + "/" + name + ".png");
            copy(o, file);
            return new File(file.toString()).exists();
        } catch (Exception e) {
            Log.e("Phonograph", "bitmap2File: error -> " + e.toString());
            return false;
        }
    }

    /**
     * 复制文件
     *
     * @param source 输入文件
     * @param target 输出文件
     */
    public static void copy(File source, File target) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(source);
            fileOutputStream = new FileOutputStream(target);
            byte[] buffer = new byte[1024];
            while (fileInputStream.read(buffer) > 0) {
                fileOutputStream.write(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
