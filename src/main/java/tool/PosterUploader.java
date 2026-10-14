package tool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Pattern;

import jakarta.servlet.http.Part;

/**
 * 映画のポスター画像を、実行中のアプリ(Tomcat)の images/movies フォルダに保存する共通クラス。
 *
 * ・ファイル名は、アップロードされたときの名前のまま使う(DBの POSTER_URL にもその名前を入れる)。
 * ・同じ名前の画像がすでにあるときは、上書きせずに連番をつける(octopus.png → octopus_1.png → octopus_2.png ...)。
 * ・種類はファイルの先頭のバイト列で判定し、拡張子と食い違うものは受け付けない。
 */
public class PosterUploader {

    /** 1枚あたりの上限(5MB) */
    public static final long MAX_SIZE = 5L * 1024 * 1024;

    /** ファイル名に使ってよい文字(文字・数字・_ - . ( ) 空白)。URLを壊す # ? % & + などは使えない */
    private static final Pattern SAFE_NAME = Pattern.compile("[\\p{L}\\p{N}\\p{M}_\\-.() ]+");

    /** 画像として受け付けられなかったときの例外。getMessage() をそのまま画面に出せる */
    public static class UploadException extends Exception {
        public UploadException(String message) {
            super(message);
        }
    }

    /**
     * 画像を dir に保存して、保存したファイル名を返す。ファイルが選ばれていない場合は null を返す。
     *
     * @param part  アップロードされたファイル
     * @param dir   保存先(実行中のアプリの images/movies)
     */
    public static String save(Part part, File dir) throws UploadException, IOException {

        if (part == null || part.getSize() == 0) {
            return null;
        }
        if (part.getSize() > MAX_SIZE) {
            throw new UploadException("画像は5MB以下にしてください。");
        }

        // ファイル名(ブラウザによってはフォルダ名が付くので、最後の部分だけ使う)
        String name = part.getSubmittedFileName();
        if (name == null) {
            throw new UploadException("ファイル名を取得できませんでした。");
        }
        name = name.substring(Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\')) + 1).trim();
        if (name.isEmpty() || name.length() > 100 || name.startsWith(".") || name.contains("..")
                || !SAFE_NAME.matcher(name).matches()) {
            throw new UploadException("ファイル名に使えない文字が含まれています。"
                    + "文字・数字・「_ - . ( )」だけの名前に変えてください。");
        }

        // 先頭12バイトで画像の種類を判定し、拡張子と合っているか確かめる
        byte[] head = new byte[12];
        int len;
        try (InputStream in = part.getInputStream()) {
            len = in.readNBytes(head, 0, head.length);
        }
        String type = detectType(head, len);
        if (type == null) {
            throw new UploadException("画像は JPEG / PNG / GIF / WebP のいずれかを選んでください。");
        }
        if (!extensionMatches(name, type)) {
            throw new UploadException("ファイルの拡張子と画像の種類が合っていません。");
        }

        if (!dir.isDirectory() && !dir.mkdirs()) {
            throw new IOException("保存先フォルダを作成できません: " + dir);
        }

        // 同じ名前の画像がすでにあるときは、上書きせずに連番をつけた名前で保存する。
        // REPLACE_EXISTING を付けないので、同時に同じ名前が来ても上書きされず、次の番号を試す。
        int dot = name.lastIndexOf('.');
        String base = name.substring(0, dot);
        String ext = name.substring(dot);
        String saveName = name;
        for (int n = 1; ; n++) {
            try (InputStream in = part.getInputStream()) {
                Files.copy(in, new File(dir, saveName).toPath());
                break;
            } catch (FileAlreadyExistsException e) {
                if (n > 9999) {
                    throw new UploadException("同じ名前の画像が多すぎます。ファイル名を変えてください。");
                }
                saveName = base + "_" + n + ext;
            }
        }
        name = saveName;
        return name;
    }

    /** 保存した画像を削除する(登録や更新に失敗したときに使う) */
    public static void delete(String fileName, File dir) {
        if (fileName == null || fileName.contains("/") || fileName.contains("\\") || fileName.contains("..")) {
            return;
        }
        new File(dir, fileName).delete();
    }

    /**
     * 映画の削除や画像の差し替えのあとに、使われなくなった画像を削除する。
     * まだほかの映画(movies)がその画像を使っているときは、削除しない。
     *
     * @param fileName  削除したい画像のファイル名(DBの POSTER_URL の値)
     * @param dir       画像のフォルダ(実行中のアプリの images/movies)
     * @param movies    いまDBにある全映画(変更・削除が終わったあとの状態)
     */
    public static void deleteIfUnused(String fileName, File dir, List<bean.Movie> movies) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }
        for (bean.Movie m : movies) {
            if (fileName.equals(m.getPosterUrl())) {
                return;
            }
        }
        delete(fileName, dir);
    }

    static boolean extensionMatches(String name, String type) {
        int dot = name.lastIndexOf('.');
        if (dot < 0) {
            return false;
        }
        String ext = name.substring(dot + 1).toLowerCase();
        switch (type) {
        case "jpeg": return ext.equals("jpg") || ext.equals("jpeg");
        case "png":  return ext.equals("png");
        case "gif":  return ext.equals("gif");
        case "webp": return ext.equals("webp");
        default:     return false;
        }
    }

    static String detectType(byte[] b, int len) {
        if (len >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) {
            return "jpeg";
        }
        if (len >= 8 && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G'
                && b[4] == 0x0D && b[5] == 0x0A && b[6] == 0x1A && b[7] == 0x0A) {
            return "png";
        }
        if (len >= 6 && b[0] == 'G' && b[1] == 'I' && b[2] == 'F' && b[3] == '8'
                && (b[4] == '7' || b[4] == '9') && b[5] == 'a') {
            return "gif";
        }
        if (len >= 12 && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P') {
            return "webp";
        }
        return null;
    }
}