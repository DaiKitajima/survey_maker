package jp.co.SurveyMaker.Util;

import java.util.UUID;
 
public class UUIDUtil {
    /**
     * URL Base64を使用，“+/”を“-_”へ変換
     */
    static private char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_=".toCharArray();
 
    public static void main(String[] angs){
        System.out.println(get4UUID());
        System.out.println(get8UUID());
        System.out.println(get12UUID());
        System.out.println(get16UUID());
        System.out.println(get20UUID());
        System.out.println(get24UUID());
        System.out.println(get32UUID());
        String nn = "PDDJK"+UUIDUtil.get32UUID().substring(4, 31);
        System.out.println(nn.toUpperCase()+";"+nn.length());
    }
 
    /**
     * 4バイトのUUIDを取得
     * @return UUID
     */
    public static String get4UUID(){
        UUID id=UUID.randomUUID();
        String[] idd=id.toString().split("-");
        return idd[1];
    }
    /**
     * 8バイトのUUIDを取得
     * @return UUID
     */
    public static String get8UUID(){
        UUID id=UUID.randomUUID();
        String[] idd=id.toString().split("-");
        return idd[0];
    }
    /**
     * 12バイトのUUIDを取得
     * @return UUID
     */
    public static String get12UUID(){
        UUID id=UUID.randomUUID();
        String[] idd=id.toString().split("-");
        return idd[0]+idd[1];
    }
    /**
     * 16バイトのUUIDを取得
     * @return UUID
     */
    public static String get16UUID(){
 
        UUID id=UUID.randomUUID();
        String[] idd=id.toString().split("-");
        return idd[0]+idd[1]+idd[2];
    }
    /**
     * 20バイトのUUIDを取得
     * @return UUID
     */
    public static String get20UUID(){
 
        UUID id=UUID.randomUUID();
        String[] idd=id.toString().split("-");
        return idd[0]+idd[1]+idd[2]+idd[3];
    }
    /**
     * 24バイトのUUIDを取得
     * @return UUID
     */
    public static String get24UUID(){
        UUID id=UUID.randomUUID();
        String[] idd=id.toString().split("-");
        return idd[0]+idd[1]+idd[4];
    }
    /**
     * 32バイトのUUIDを取得
     * @return UUID
     */
    public static String get32UUID(){
        UUID id=UUID.randomUUID();
        String[] idd=id.toString().split("-");
        return idd[0]+idd[1]+idd[2]+idd[3]+idd[4];
    }
 
    /**
     * バイトへ
     * @return
     */
    private static byte[] toBytes() {
        UUID uuid = UUID.randomUUID();
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        byte[] buffer = new byte[16];
        for (int i = 0; i < 8; i++) {
            buffer[i] = (byte) ((msb >>> 8 * (7 - i)) & 0xFF);
            buffer[i + 8] = (byte) ((lsb >>> 8 * (7 - i)) & 0xFF);
        }
        return buffer;
    }
 
//    /**
//     * 24バイトUUID
//     * @return UUID
//     */
//    public static String getUUID() {
//        return new BASE64Encoder().encode(toBytes());
//    }
 
    /**
     * 指定バイトのUUIDを取得
     * @param bits 指定
     * @return
     */
 
    public static String getUUIDBits(int bits) {
        UUID uuid = UUID.randomUUID();
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        char[] out = new char[24];
        int tmp = 0, idx = 0;
 
        // 循环写法
        int bit = 0, bt1 = 8, bt2 = 8;
        int mask = 0x00, offsetm = 0, offsetl = 0;
 
        for(; bit < 16; bit += 3, idx += 4) {
            offsetm = 64 - (bit + 3) * 8;
            offsetl = 0;
            tmp = 0;
 
            if(bt1 > 3) {
                mask = (1 << 8 * 3) - 1;
            } else if(bt1 >= 0) {
                mask = (1 << 8 * bt1) - 1;
                bt2 -= 3 - bt1;
            } else {
                mask = (1 << 8 * ((bt2 > 3) ? 3 : bt2)) - 1;
                bt2 -= 3;
            }
            if(bt1 > 0) {
                bt1 -= 3;
                tmp = (int) ((offsetm < 0) ? msb : (msb >>> offsetm) & mask);
                if(bt1 < 0) {
                    tmp <<= Math.abs(offsetm);
                    mask = (1 << 8 * Math.abs(bt1)) - 1;
                }
            }
            if(offsetm < 0) {
                offsetl = 64 + offsetm;
                tmp |= ((offsetl < 0) ? lsb : (lsb >>> offsetl)) & mask;
            }
 
            if(bit == 15) {
                out[idx + 3] = alphabet[64];
                out[idx + 2] = alphabet[64];
                tmp <<= 4;
            } else {
                out[idx + 3] = alphabet[tmp & 0x3f];
                tmp >>= 6;
                out[idx + 2] = alphabet[tmp & 0x3f];
                tmp >>= 6;
            }
            out[idx + 1] = alphabet[tmp & 0x3f];
            tmp >>= 6;
            out[idx] = alphabet[tmp & 0x3f];
        }
 
        return new String(out, 0, bits);
    }
}