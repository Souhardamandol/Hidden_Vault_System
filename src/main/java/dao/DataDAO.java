package dao;

import model.Data;
import db.MyConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;


import java.io.*;
import java.security.Key;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DataDAO {
    private static final String AES_KEY = "Tv5hfG6u9aBg2etTrReIc3iWLfsW25S39w3rBP74yn0=";

    public static List<Data> getAllFiles(String email) throws SQLException {
        Connection connection = MyConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement("select * from data where email = ?");
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        List<Data> files = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt(1);
            String name = rs.getString(2);
            String path = rs.getString(3);
            files.add(new Data(id, name, path));
        }
        return files;
    }

    public static int hideFile(Data file) throws SQLException, IOException {
        Connection connection = MyConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement(
                "insert into data(name, path, email, bin_data) values(?,?,?,?)");
        ps.setString(1, file.getFileName());
        ps.setString(2, file.getPath());
        ps.setString(3, file.getEmail());
        File f = new File(file.getPath());
        byte[] fileData = Files.readAllBytes(f.toPath());
        byte[] encryptedData = encryptFile(fileData);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(encryptedData);
        ps.setBinaryStream(4, inputStream, encryptedData.length);
        int ans = ps.executeUpdate();
        inputStream.close();
        f.delete();
        return ans;
    }

    public static void unhide(int id) throws SQLException, IOException {
        Connection connection = MyConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement("select path, bin_data from data where id = ?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        rs.next();
        String path = rs.getString("path");
        InputStream binaryStream = rs.getBinaryStream("bin_data");
        byte[] encryptedData = binaryStream.readAllBytes();
        byte[] decryptedData = decryptFile(encryptedData);
        Files.write(Paths.get(path), decryptedData);
        ps = connection.prepareStatement("delete from data where id = ?");
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("Successfully Unhidden");
    }

    public static byte[] encryptFile(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec secretKey = new SecretKeySpec(Base64.getDecoder().decode(AES_KEY), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] decryptFile(byte[] encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec secretKey = new SecretKeySpec(Base64.getDecoder().decode(AES_KEY), "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
